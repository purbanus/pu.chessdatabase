package pu.chessdatabase.dbs;

//=================================================================================================
// Hulpmethodes bij testen
//=================================================================================================

public class TestHelper
{
public static boolean isAllZero( byte [] aPage )
{
	return isAll( aPage, (byte)0 );
}
public static boolean isAllOne( byte [] aPage )
{
	return isAll( aPage, (byte)1 );
}
public static boolean isAll( byte [] aPage, byte aValue )
//public static Boolean isAll( byte [] aPage, byte aValue )
{
	for ( byte b : aPage )
	{
		if ( b != aValue )
		{
			return false;
		}
	}
	return true;
}
public static byte [] createPageWithAllOnes()
{
	return createPageWithAll( (byte)1 );
}
public static byte [] createPageWithAll( byte aValue )
{
	byte [] entries = new byte [Cache.getStaticPageSize()];
	for ( int x = 0; x < entries.length; x++ )
	{
		entries[x] = aValue;
	}
	return entries;
}

}
