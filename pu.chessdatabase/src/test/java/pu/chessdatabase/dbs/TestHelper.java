package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Lokatie.*;

import pu.chessdatabase.bo.Config;

import lombok.Data;

//=================================================================================================
// Hulpmethodes bij testen
//=================================================================================================

@Data
public class TestHelper
{
private final Config config;

public TestHelper( Config aConfig )
{
	super();
	config = aConfig;
}
PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
int getAantalStukken()
{
	return getConfig().getAantalStukken();
}
public boolean isAllZero( byte [] aPage )
{
	return isAll( aPage, (byte)0 );
}
public boolean isAllOne( byte [] aPage )
{
	return isAll( aPage, (byte)1 );
}
public boolean isAll( byte [] aPage, byte aValue )
{
	for ( int x = 0; x < aPage.length; x++)
	{
		if ( aPage[x] != aValue )
		{
			return false;
		}
	}
	return true;
}
public byte [] createPageWithAllOnes()
{
	return createPageWithAll( (byte)1 );
}
public byte [] createPageWithAll( byte aValue )
{
	byte [] entries = new byte [ getPageSizeCalculator().getPageSize( getAantalStukken() )];
	for ( int x = 0; x < entries.length; x++ )
	{
		entries[x] = aValue;
	}
	return entries;
}
public byte [] createBlockOfBytes( int aNumberOfBytes, byte aValue )
{
	byte [] bytes = new byte [aNumberOfBytes];
	for ( int x = 0; x < bytes.length; x++ )
	{
		bytes[x] = aValue;
	}
	return bytes;
}
public void writePageWithAll( Cache aCache, long aPageNumber, int aCacheNumber, byte aValue )
{
	byte [] page = createPageWithAll( aValue );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( aCacheNumber )
		.schijfAdres( aPageNumber * aCache.getPageSize() )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 15 )
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( true )
		.build();
	aCache.setCacheEntry( pageDescriptor, cacheEntry );
	aCache.pageOut( pageDescriptor );
}

}
