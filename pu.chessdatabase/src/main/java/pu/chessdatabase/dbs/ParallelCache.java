package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Lokatie.*;
import static pu.chessdatabase.dbs.Constants.*;

import java.io.RandomAccessFile;
import java.util.ArrayList;

import pu.chessdatabase.bo.Config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=true )
class ParallelCache  extends AbstractCache
{
//static final int CACHE_SIZE = Constants.MAX_WK; // Aantal stellingen van de WK 
//private static int staticAantalStukken;
//public static int getStaticAantalStukken()
//{
//	return staticAantalStukken;
//}
ParallelCache( Config aConfig, RandomAccessFile aDatabase )
{
	super( aConfig, aDatabase );
}
@Override
int getCacheSize()
{
	return getConfig().heeftPionnen() ? MAX_STUK : MAX_WK;
}
@Override
void initializeCache()
{
	setCacheEntries( new ArrayList<>() );
	long address = 0L;
	for ( int wk : getConfig().heeftPionnen() ? STUK_VELD_RANGE : WK_VELD_RANGE )
	{
		// @@NOG Kun je deie PD niet uit de PDTable halen?
		PageDescriptor pageDescriptor = PageDescriptor.builder()
			.cacheNummer( wk )
			.schijfAdres( address )
			.waar( OpSchijf )
			.build();
		CacheEntry cacheEntry = CacheEntry.builder()
			.pageDescriptor( pageDescriptor )
			// Niet nodig, bouwt zich vanzelf op
			//.page( getRawPageData( pageDescriptor ) )
			.page( new byte[getPageSize()] )
			.vuil( false )
			.generatie( 0 )
			.build();
		getCacheEntries().add( cacheEntry );
		address += getPageSizeCalculator().getPageSize( getAantalStukken() );
	}
}
@Override
public int getPositionWithinPage( VMStelling aVmStelling )
{
	switch ( getAantalStukken() )
	{

//		case 3: return ( aVmStelling.getZk() << 7 ) + ( aVmStelling.getAanZet().ordinal() << 6 ) + aVmStelling.getS3();
//		case 4: return ( aVmStelling.getZk() << 13 ) + ( aVmStelling.getAanZet().ordinal() << 12 ) + ( aVmStelling.getS3() << 6 ) + aVmStelling.getS4();
//		case 5: return ( aVmStelling.getZk() << 19 ) + ( aVmStelling.getAanZet().ordinal() << 18 ) + ( aVmStelling.getS3() << 12 ) + ( aVmStelling.getS4() << 6 ) + aVmStelling.getS5();
		case 3: return ( aVmStelling.getZk() * 64 * 2 ) + ( aVmStelling.getAanZet().ordinal() * 64 ) + aVmStelling.getS3();
		case 4: return ( aVmStelling.getZk() * 64 * 64 * 2 ) + ( aVmStelling.getAanZet().ordinal() * 64 * 64 ) + ( aVmStelling.getS3() * 64 ) + aVmStelling.getS4();
		case 5: return ( aVmStelling.getZk() * 64 * 64 * 64 * 2 ) + ( aVmStelling.getAanZet().ordinal() * 64 * 64 * 64 ) + ( aVmStelling.getS3() * 64 * 64 ) + ( aVmStelling.getS4() * 64 ) + aVmStelling.getS5() ;
//		{
//			int pos =  ( aVmStelling.getZk() * 64 * 64 * 64 * 2 );
//			    pos += ( aVmStelling.getAanZet().ordinal() * 64 * 64 * 64 );
//			    pos	+= ( aVmStelling.getS3() * 64 * 64 );
//			    pos += ( aVmStelling.getS4() * 64 );
//			    pos += aVmStelling.getS5();
//			return pos;
//		}
		default: throw new RuntimeException( "Ongeldig aantal stukken in Cache: " + getAantalStukken() );
	}
}
@Override
void pageIn( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getWaar() == OpSchijf )
	{
	    //-------- Ophalen nieuwe pagina -------------
		getRawPageData( aPageDescriptor );

		//-------- Update cache ----------------------
		CacheEntry cacheEntry = getCacheEntry( aPageDescriptor );
	    cacheEntry.setPageDescriptor( aPageDescriptor );
	    cacheEntry.setVuil( false );
	    cacheEntry.setGeneratie( incrementGeneratieTeller() );

	    //-------- Update Page descriptor ------------
	    aPageDescriptor.setWaar( InRam );
	}
}
}