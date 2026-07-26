package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Lokatie.*;

import java.io.RandomAccessFile;
import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=true )
class SerialCache extends AbstractCache
{
static final int CACHE_SIZE = 30; // Aantal pagina"s
private static int staticAantalStukken;
public static int getStaticAantalStukken()
{
	return staticAantalStukken;
}
SerialCache( PageSizeCalculator aPageSizeCalculator, int aAantalStukken, RandomAccessFile aDatabase )
{
	super( aPageSizeCalculator, aAantalStukken, aDatabase );
}
@Override
int getCacheSize()
{
	return CACHE_SIZE;
}
@Override
void initializeCache()
{
	setCacheEntries( new ArrayList<>() );
	for ( int x = 0; x < CACHE_SIZE; x++ )
	{
		CacheEntry cacheEntry = CacheEntry.builder()
			.pageDescriptor( null )
			.page( new byte[getPageSize()] )
			.vuil( false )
			.generatie( 0 )
			.build();
		getCacheEntries().add( cacheEntry );
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
    for ( CacheEntry cacheEntry : getCacheEntries() ) 
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

@Override
public int getPositionWithinPage( VMStelling aVmStelling )
{
	switch ( getAantalStukken() )
	{
		case 3: return aVmStelling.getS3();
		case 4: return ( aVmStelling.getS3() << 6 ) + aVmStelling.getS4();
		case 5: return ( aVmStelling.getS3() << 12 ) + ( aVmStelling.getS4() << 6 ) + aVmStelling.getS5();
		default: throw new RuntimeException( "Ongeldig aantal stukken in Cache: " + getAantalStukken() );
 	}
}
 /**
 * ----------- Pagina ophalen van de schijf ---------
 */
@Override
void pageIn( PageDescriptor aPageDescriptor )
{
    if ( aPageDescriptor.getWaar() == OpSchijf )
    {
    	aPageDescriptor.setCacheNummer( getFreeCacheEntry() );
    }
    CacheEntry cacheEntry = getCacheEntry( aPageDescriptor );
    
    //-------- Update oude page descriptor -------
    PageDescriptor oudePageDescriptor = cacheEntry.getPageDescriptor();
    if ( oudePageDescriptor != null )
    {
        pageOut( oudePageDescriptor );
        oudePageDescriptor.setWaar( OpSchijf );
        oudePageDescriptor.setCacheNummer( Integer.MAX_VALUE );
    }

    //-------- Ophalen nieuwe pagina -------------
 	getRawPageData( aPageDescriptor );

    //-------- Update cache ----------------------
    cacheEntry.setPageDescriptor( aPageDescriptor );
    cacheEntry.setVuil( false );
    cacheEntry.setGeneratie( incrementGeneratieTeller() );

    //-------- Update Page descriptor ------------
    aPageDescriptor.setWaar( InRam );
}
}