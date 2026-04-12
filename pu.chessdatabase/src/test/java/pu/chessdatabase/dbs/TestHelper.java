package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.Einde.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import org.apache.commons.lang3.tuple.Triple;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.speel.Plies;
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
public static Ply createOnePly( Plies aPlies)
{
	return createOnePly( aPlies, new VanNaar( "a1", "a2" ) );
}
public static Ply createOnePly( Plies aPlies, VanNaar aVanNaar )
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
	return createOnePly( aPlies, boStelling, aVanNaar );
}
public static Ply createOnePly( Plies aPlies, BoStelling aBoStelling )
{
	return createOnePly( aPlies, aBoStelling, new VanNaar( "a1", "a2" ) );
}
public static Ply createOnePly( Plies aPlies, BoStelling aBoStelling, VanNaar aVanNaar )
{
	return Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Nog_niet )
		.zetNummer( 1 )
		.plySchaak( false )
		.vanNaar( aVanNaar )
		.boStelling( aBoStelling )
		.build();
}
public static Triple<Ply, Ply, Ply> createThreeDifferentPlies( Plies aPlies )
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
	return createThreeDifferentPlies( aPlies, boStelling );
}
public static Triple<Ply, Ply, Ply> createThreeDifferentPlies( Plies aPlies, BoStelling aBoStelling )
{
	Ply firstPly = Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Nog_niet )
		.zetNummer( 17 )
		.plySchaak( false )
		.vanNaar( new VanNaar( "a1", "a2" ) )
		.boStelling( aBoStelling )
		.build();
	Ply secondPly = Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Mat )
		.zetNummer( 27 )
		.plySchaak( false )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.boStelling( aBoStelling )
		.build();
	Ply thirdPly = Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Mat )
		.zetNummer( 39 )
		.plySchaak( false )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.boStelling( aBoStelling )
		.build();
	return Triple.of( firstPly, secondPly, thirdPly );
}

}
