package pu.chessdatabase.bo.speel;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.Einde.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import org.apache.commons.lang3.tuple.Triple;

import pu.chessdatabase.bo.BoStelling;

public class TestPlyHelper
{
public Ply createOnePly( Plies aPlies)
{
	return createOnePly( aPlies, new VanNaar( "a1", "a2" ) );
}
public Ply createOnePly( Plies aPlies, VanNaar aVanNaar )
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.build();
	return createOnePly( aPlies, boStelling, aVanNaar );
}
public Ply createOnePly( Plies aPlies, BoStelling aBoStelling )
{
	return createOnePly( aPlies, aBoStelling, new VanNaar( "a1", "a2" ) );
}
public Ply createOnePly( Plies aPlies, BoStelling aBoStelling, VanNaar aVanNaar )
{
	return Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Nog_niet )
		.plyNummer( 0 )
		.vanNaar( aVanNaar )
		.boStelling( aBoStelling )
		.build();
}
public Triple<Ply, Ply, Ply> createThreeDifferentPlies( Plies aPlies )
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.schaak( false )
		.build();
	return createThreeDifferentPlies( aPlies, boStelling );
}
public Triple<Ply, Ply, Ply> createThreeDifferentPlies( Plies aPlies, BoStelling aBoStelling )
{
	Ply firstPly = Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Nog_niet )
		.plyNummer( 0 )
		.vanNaar( new VanNaar( "a1", "a2" ) )
		.boStelling( aBoStelling )
		.build();
	Ply secondPly = Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Mat )
		.plyNummer( 1 )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.boStelling( aBoStelling )
		.build();
	Ply thirdPly = Ply.builder()
		//.id is voor JPA
		.plies( aPlies )
		.einde( Mat )
		.plyNummer( 2 )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.boStelling( aBoStelling )
		.build();
	return Triple.of( firstPly, secondPly, thirdPly );
}

}
