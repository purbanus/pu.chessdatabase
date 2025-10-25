package pu.chessdatabase.dal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

class Cache
{
static final int PAGE_SIZE = 4096;               // Bytes per page
static final int CACHE_SIZE     = 30;                 // Aantal pagina"s

private RandomAccessFile database = null;
private List<CacheEntry> cache = new ArrayList<>();
@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PRIVATE ) 
private long generatieTeller;

Cache()
{
	super();
	initializeCache();
	setGeneratieTeller( 1L );
}
Cache( RandomAccessFile aDatabase )
{
	this();
	database = aDatabase;
}
private void initializeCache()
{
	for ( int x = 0; x < CACHE_SIZE; x++ )
	{
		CacheEntry cacheEntry = CacheEntry.builder()
			.pageDescriptor( null )
			.page( new Page() )
			.vuil( false )
			.generatie( 0 )
			.build();
		getCache().add( cacheEntry );
		cacheEntry.getPage().clearPage();
	}
}
private int getFreeCacheEntry()
{
    //---- laagste generatienummers -------
    long LaagsteGeneratie        = Long.MAX_VALUE;
    long LaagsteSchoneGeneratie  = Long.MAX_VALUE;
    int LaagsteGeneratieNr      = Integer.MAX_VALUE;
    int LaagsteSchoneGeneratieNr= Integer.MAX_VALUE;
    int index = -1;
    for ( CacheEntry cacheEntry : cache ) 
    {
    	index++;
        if ( cacheEntry.getGeneratie() < LaagsteGeneratie )
        {
            LaagsteGeneratie  = cacheEntry.getGeneratie();
            LaagsteGeneratieNr = index;
        }
        if ( ! cacheEntry.isVuil() && ( cacheEntry.getGeneratie() < LaagsteSchoneGeneratie ) )
        {
            LaagsteSchoneGeneratie  = cacheEntry.getGeneratie();
            LaagsteSchoneGeneratieNr = index;
        }
    }
    //----- bij voorkeur schone cache entry nemen ------
    if ( LaagsteSchoneGeneratieNr != Integer.MAX_VALUE )
    {
        return LaagsteSchoneGeneratieNr;
    }
    else
    {
    	return LaagsteGeneratieNr;
    }
}
// @@NOG private maken want wordt alleen in tests gebruikt. Helaas ook in TestVN, dus nog ff niet
Page getPage( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getCacheNummer() > 30 )
	{
		System.out.println( "Got him! Hij is " + aPageDescriptor.getCacheNummer() );
	}
	return getCache().get( aPageDescriptor.getCacheNummer() ).getPage();
}
private byte [] getPageData( PageDescriptor aPageDescriptor )
{
	return getPage( aPageDescriptor ).getData();
}
@SuppressWarnings( "unused" )
private void setPage( PageDescriptor aPageDescriptor, Page aPage )
{
	getCache().get( aPageDescriptor.getCacheNummer() ).setPage( aPage );
}
private boolean isVuil( PageDescriptor aPageDescriptor )
{
	return getCache().get( aPageDescriptor.getCacheNummer() ).isVuil();
}
void setVuil( PageDescriptor aPageDescriptor, boolean aVuil )
{
	getCache().get( aPageDescriptor.getCacheNummer() ).setVuil( aVuil );
}
CacheEntry getCacheEntry( PageDescriptor aPageDescriptor )
{
	return getCache().get( aPageDescriptor.getCacheNummer() );
}
void setCacheEntry( PageDescriptor aPageDescriptor, CacheEntry aCacheEntry )
{
	getCache().set( aPageDescriptor.getCacheNummer(), aCacheEntry );
}

private void getRawPageData( PageDescriptor aPageDescriptor )
{
    try
	{
		getDatabase().seek( aPageDescriptor.getSchijfAdres() );
		int Aantal = getDatabase().read( getPageData( aPageDescriptor ), 0, Cache.PAGE_SIZE );
		if ( Aantal != Cache.PAGE_SIZE )
		{
			throw new RuntimeException( "Ernstig: VM.GetPage heeft " + Aantal + " records gelezen. Dat zouden er " + Cache.PAGE_SIZE + " moeten zijn" );
		}
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
}
private void putRawPageData( PageDescriptor aPageDescriptor )
{
	try
	{
		getDatabase().seek( aPageDescriptor.getSchijfAdres() );
	    //getDatabase().write( Cache[aPageDescriptor.getCacheNummer()].getPage().getPage(), 0, PAGE_SIZE );
	    getDatabase().write( getPageData( aPageDescriptor ), 0, Cache.PAGE_SIZE );
	    // @@NOG moet hier niet vuil=false gedaan worden?
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
}
/**
 *------------ Pagina schrijven naar de schijf ------
 */
void pageOut( PageDescriptor aPageDescriptor )
{
    if ( aPageDescriptor != null && isVuil( aPageDescriptor ) )
    {
        putRawPageData( aPageDescriptor );
        setVuil( aPageDescriptor, false );
    }
}
/**
 * ----------- Pagina ophalen van de schijf ---------
 */
private void pageIn( PageDescriptor aPageDescriptor )
{
    if ( aPageDescriptor.getWaar() == Lokatie.OP_SCHIJF )
    {
    	aPageDescriptor.setCacheNummer( getFreeCacheEntry() );
    }
    CacheEntry cacheEntry = getCacheEntry( aPageDescriptor );
    
    //-------- Update oude page descriptor -------
    PageDescriptor oudePageDescriptor = cacheEntry.getPageDescriptor();
    if ( oudePageDescriptor != null )
    {
        pageOut( oudePageDescriptor );
        oudePageDescriptor.setWaar( Lokatie.OP_SCHIJF );
        oudePageDescriptor.setCacheNummer( Integer.MAX_VALUE );
    }

    //-------- Ophalen nieuwe pagina -------------
 	getRawPageData( aPageDescriptor );

    //-------- Update cache ----------------------
    cacheEntry.setPageDescriptor( aPageDescriptor );
    cacheEntry.setVuil( false );
    cacheEntry.setGeneratie( generatieTeller++ );

    //-------- Update Page descriptor ------------
    aPageDescriptor.setWaar( Lokatie.IN_RAM );
}
/**
 *  ------- Haal pagina op uit de cache ---------
 */
Page getPageFromDatabase( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getWaar() == Lokatie.OP_SCHIJF )
	{
		pageIn( aPageDescriptor );
	}
	Page page = getPage( aPageDescriptor );
	return page;
}
void setData( PageDescriptor aPageDescriptor, int aPositionWithinPage, byte aData )
{
    getPageData( aPageDescriptor )[aPositionWithinPage] = aData;
	setVuil( aPageDescriptor, true );
}
void flush()
{
	for ( CacheEntry cacheEntry : getCache() )
	{
		if ( cacheEntry.getPageDescriptor() != null && cacheEntry.getPageDescriptor().getCacheNummer() != Integer.MAX_VALUE )
		{
			pageOut( cacheEntry.getPageDescriptor() );
			cacheEntry.setGeneratie( 0 );
		}
	}
	setGeneratieTeller( 1 );
}

}