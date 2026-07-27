package pu.chessdatabase.dbs;

import lombok.Data;

//=================================================================================================
// Hulpmethodes bij testen
//=================================================================================================

@Data
public class TestHelper
{
private final PageSizeCalculator pageSizeCalculator;
private final int aantalStukken;

public TestHelper( PageSizeCalculator aPageSizeCalculator, int aAantalStukken )
{
	super();
	pageSizeCalculator = aPageSizeCalculator;
	aantalStukken = aAantalStukken;
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

}
