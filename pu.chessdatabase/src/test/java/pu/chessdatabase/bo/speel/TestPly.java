package pu.chessdatabase.bo.speel;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.configuraties.KLPK;

import lombok.Data;

@Data
@SpringBootTest
public class TestPly
{
@Autowired private Config config;
@Autowired private Partij partij;
Plies plies;

@BeforeEach
public void setup()
{
//	getConfig().switchConfig( Config.KLPK );
	plies = getPartij().getPlies();
}
@Test
public void testGetZetNummer()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();

	Ply ply = Ply.builder()
		.plyNummer( 0 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 1 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 2 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 3 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 4 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	assertThat( plies.getPlies().get( 0 ).getZetNummer(), is( 1 ) );
	assertThat( plies.getPlies().get( 1 ).getZetNummer(), is( 2 ) );
	assertThat( plies.getPlies().get( 2 ).getZetNummer(), is( 2 ) );
	assertThat( plies.getPlies().get( 3 ).getZetNummer(), is( 3 ) );
	assertThat( plies.getPlies().get( 4 ).getZetNummer(), is( 3 ) );

	plies.clear();
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Zwart )
		.build();

	ply = Ply.builder()
		.plyNummer( 0 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 1 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 2 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 3 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	ply = Ply.builder()
		.plyNummer( 4 )
		.boStelling( boStelling )
		.build();
	plies.addPly( ply );
	assertThat( plies.getPlies().get( 0 ).getZetNummer(), is( 1 ) );
	assertThat( plies.getPlies().get( 1 ).getZetNummer(), is( 1 ) );
	assertThat( plies.getPlies().get( 2 ).getZetNummer(), is( 2 ) );
	assertThat( plies.getPlies().get( 3 ).getZetNummer(), is( 2 ) );
	assertThat( plies.getPlies().get( 4 ).getZetNummer(), is( 3 ) );
}
@Test
public void testGetPreviousStelling()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 24 )
		.schaak( false )
		.build();
	getPartij().newGame( startStelling );
	getPartij().zet( new VanNaar(  "Db2-f6" ) );
	plies = getPartij().getPlies();
	BoStelling previousStelling = plies.getPly( 0 ).getPreviousStelling();
	assertThat( previousStelling, is( startStelling ) );
	
	getPartij().zet( new VanNaar( "Kh8-g8"  ) );
	getPartij().zet( new VanNaar( "Df6-d8+" ) );
	BoStelling boStelling2 = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "g8" )
		.s3( "f6" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.schaak( false )
		.build();

	previousStelling = plies.getCurrentPly().getPreviousStelling();
	assertThat( previousStelling, is( boStelling2 ) );
}
}