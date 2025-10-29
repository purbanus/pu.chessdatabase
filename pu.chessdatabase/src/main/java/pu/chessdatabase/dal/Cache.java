package pu.chessdatabase.dal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

class Cache
{
static final int PAGE_SIZE_3_STUKKEN = 64;               // Bytes per page
static final int PAGE_SIZE_4_STUKKEN = 64*64;            // Bytes per page
static final int PAGE_SIZE_5_STUKKEN = 64*64*64;         // Bytes per page
static final int CACHE_SIZE     = 30;                 // Aantal pagina"s
static final Map<Integer, Integer> PAGE_SIZE_LOOKUP = new HashMap<>();
static
{
	PAGE_SIZE_LOOKUP.put( 3, PAGE_SIZE_3_STUKKEN );
	PAGE_SIZE_LOOKUP.put( 4, PAGE_SIZE_4_STUKKEN );
	PAGE_SIZE_LOOKUP.put( 5, PAGE_SIZE_5_STUKKEN );
}
private final int aantalStukken;
private static int staticAantalStukken;
static int getStaticAantalStukken()
{
	return staticAantalStukken;
}
static int getStaticPageSize()
{
	return PAGE_SIZE_LOOKUP.get( getStaticAantalStukken() );
}

private RandomAccessFile database = null;
private List<CacheEntry> cache = new ArrayList<>();
@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PRIVATE ) 
private long generatieTeller;
Cache( int aAantalStukken )
{
	super();
	aantalStukken = aAantalStukken;
	staticAantalStukken = aAantalStukken;
	initializeCache();
	setGeneratieTeller( 1L );
}
Cache( int aAantalStukken, RandomAccessFile aDatabase )
{
	this( aAantalStukken );
	database = aDatabase;
}
int getPageSize()
{
	return PAGE_SIZE_LOOKUP.get( getAantalStukken() );
}

private void initializeCache()
{
	for ( int x = 0; x < CACHE_SIZE; x++ )
	{
		CacheEntry cacheEntry = CacheEntry.builder()
			.pageDescriptor( null )
			.page( new byte[getPageSize()] )
			.vuil( false )
			.generatie( 0 )
			.build();
		getCache().add( cacheEntry );
		cacheEntry.clearPage();
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
byte [] getPage( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getCacheNummer() >= CACHE_SIZE )
	{
		System.out.println( "Got him! Hij is " + aPageDescriptor.getCacheNummer() );
	}
	return getCache().get( aPageDescriptor.getCacheNummer() ).getPage();
}
@SuppressWarnings( "unused" )
private void setPage( PageDescriptor aPageDescriptor, byte [] aPage )
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
		int pageSize = getPageSize();
		int aantal = getDatabase().read( getPage( aPageDescriptor ), 0, pageSize );
		if ( aantal != pageSize )
		{
			throw new RuntimeException( "Ernstig: VM.GetPage heeft " + aantal + " records gelezen. Dat zouden er " + pageSize + " moeten zijn" );
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
	    getDatabase().write( getPage( aPageDescriptor ), 0, getPageSize() );
	    // @@HIGH moet hier niet vuil=false gedaan worden?
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
byte [ ] getPageFromDatabase( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getWaar() == Lokatie.OP_SCHIJF )
	{
		pageIn( aPageDescriptor );
	}
	return getPage( aPageDescriptor );
}
private int getPositionWithinPage( VMStelling aVmStelling )
{
	if ( getAantalStukken() == 3 )
	{
		return aVmStelling.getS3();
	}
	else if ( getAantalStukken() == 4 )
	{
		return ( aVmStelling.getS3() << 6 ) + aVmStelling.getS4();
	}
	else if ( getAantalStukken() == 5 )
	{
		return ( aVmStelling.getS3() << 12 ) + ( aVmStelling.getS4() << 6 ) + aVmStelling.getS5();
	}
	throw new RuntimeException( "Ongeldig aantal stukken in Cache: " + getAantalStukken() );
}
byte getData( PageDescriptor aPageDescriptor, VMStelling aVmStelling )
{
    return getData( aPageDescriptor, getPositionWithinPage( aVmStelling ) );
}
byte getData( PageDescriptor aPageDescriptor, int aPositionWithinPage )
{
    return getPage( aPageDescriptor )[aPositionWithinPage];
}
void setData( PageDescriptor aPageDescriptor, VMStelling aVmStelling, byte aData )
{
    setData( aPageDescriptor, getPositionWithinPage( aVmStelling ), aData );
}
void setData( PageDescriptor aPageDescriptor, int aPositionWithinPage, byte aData )
{
    getPage( aPageDescriptor )[aPositionWithinPage] = aData;
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