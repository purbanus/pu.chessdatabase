package pu.chessdatabase.dal;

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
public static boolean isAll( Page aPage, byte aValue )
{
	return isAll( aPage.getData(), aValue );
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
public static Page createPageWithAllOnes()
{
	return createPageWithAll( (byte)1 );
}
public static Page createPageWithAll( byte aValue )
{
	byte [] entries = new byte [Cache.PAGE_SIZE];
	for ( int x = 0; x < entries.length; x++ )
	{
		entries[x] = aValue;
	}
	return new Page( entries );
}

}
