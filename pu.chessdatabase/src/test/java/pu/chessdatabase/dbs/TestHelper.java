package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.Einde.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import org.apache.commons.lang3.tuple.Triple;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.speel.Ply;
import pu.chessdatabase.bo.speel.VanNaar;

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
public static Ply createOnePly()
{
	return createOnePly( new VanNaar( "a1", "a2" ) );
}
public static Ply createOnePly( VanNaar aVanNaar )
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.build();
	return createOnePly( boStelling, aVanNaar );
}
public static Ply createOnePly( BoStelling aBoStelling )
{
	return createOnePly( aBoStelling, new VanNaar( "a1", "a2" ) );
}
public static Ply createOnePly( BoStelling aBoStelling, VanNaar aVanNaar )
{
	return Ply.builder()
		.boStelling( aBoStelling )
		.einde( Nog_niet )
		.schaak( false )
		.vanNaar( aVanNaar )
		.zetNummer( 1 )
		.build();
}
public static Triple<Ply, Ply, Ply> createThreeDifferentPlies()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.build();
	return createThreeDifferentPlies( boStelling );
}
public static Triple<Ply, Ply, Ply> createThreeDifferentPlies( BoStelling aBoStelling )
{
	Ply firstPly = Ply.builder()
		.boStelling( aBoStelling )
		.einde( Nog_niet )
		.schaak( false )
		.vanNaar( new VanNaar( "a1", "a2" ) )
		.zetNummer( 17 )
		.build();
	Ply secondPly = Ply.builder()
		.boStelling( aBoStelling )
		.einde( Mat )
		.schaak( false )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.zetNummer( 27 )
		.build();
	Ply thirdPly = Ply.builder()
		.boStelling( aBoStelling )
		.einde( Mat )
		.schaak( false )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.zetNummer( 39 )
		.build();
	return Triple.of( firstPly, secondPly, thirdPly );
}

}
