package pu.chessdatabase.bo.speel;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.Einde.*;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;

import lombok.Data;

@Data
@SpringBootTest
public class TestPlies
{
@Autowired private Config config;
Plies plies;

@BeforeEach
public void setup()
{
	plies = new Plies( getConfig().getConfig() );
}
private Triple<Ply, Ply, Ply> createThreeDifferentPlies( BoStelling aBoStelling )
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
private Triple<Ply, Ply, Ply> createThreeDifferentPlies()
{
	return createThreeDifferentPlies( null );
}
@Test
public void testSize()
{
	Ply ply = new Ply();
	plies.addPly( ply );
	plies.addPly( ply );
	assertThat( plies.getSize(), is( 2 ) );
}
@Test
public void testClear()
{
	Ply ply = new Ply();
	plies.addPly( ply );
	plies.addPly( ply );
	assertThat( plies.getSize(), is( 2 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 1 ) );
	assertThat( plies.getLastPlyNumber(), is( 1 ) );
	plies.clear();
	assertThat( plies.getSize(), is( 0 ) );
	assertThat( plies.getCurrentPlyNumber(), is( -1 ) );
	assertThat( plies.getLastPlyNumber(), is( -1 ) );
}
@Test
public void testAddPly()
{
	Ply ply = new Ply();
	plies.addPly( ply );
	assertThat( plies.getSize(), is( 1 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 0 ) );
	assertThat( plies.getLastPlyNumber(), is( 0 ) );
	assertThat( plies.isBegonnen(), is( true ) );
	
	plies.addPly( ply );
	assertThat( plies.getSize(), is( 2 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 1 ) );
	assertThat( plies.getLastPlyNumber(), is( 1 ) );
	assertThat( plies.isBegonnen(), is( true ) );
}
@Test
public void testAddPlyWithBoStellingAndEinde()
{
	BoStelling boStellingMetWitAanZet = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	plies.addPly( boStellingMetWitAanZet, Nog_niet );
	assertThat( plies.getSize(), is( 1 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 0 ) );
	assertThat( plies.getLastPlyNumber(), is( 0 ) );
	Ply firstPly = plies.getFirstPly();
	assertThat( firstPly.getBoStelling(), is( boStellingMetWitAanZet ) );
	assertThat( firstPly.getEinde(), is( Nog_niet ) );
	assertThat( firstPly.getZetNummer(), is( 1 ) );

	BoStelling boStellingMetZwartAanZet = boStellingMetWitAanZet.clone();
	boStellingMetZwartAanZet.setAanZet( Zwart );
	plies.addPly( boStellingMetZwartAanZet, Mat );
	Ply secondPly = plies.getSecondPly();
	assertThat( secondPly.getBoStelling(), is( boStellingMetZwartAanZet ) );
	assertThat( secondPly.getEinde(), is( Mat ) );
	assertThat( secondPly.getZetNummer(), is( 1 ) );

	plies.addPly( boStellingMetWitAanZet, Nog_niet );
	Ply thirdPly = plies.getLastPly();
	assertThat( thirdPly.getBoStelling(), is( boStellingMetWitAanZet ) );
	assertThat( thirdPly.getEinde(), is( Nog_niet ) );
	assertThat( thirdPly.getZetNummer(), is( 2 ) );
}
@Test
public void testHasPly()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies( boStelling );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.hasPly( -1 ), is( false ) );
	assertThat( plies.hasPly( 0 ), is( true ) );
	assertThat( plies.hasPly( 1 ), is( true ) );
	assertThat( plies.hasPly( 2 ), is( true ) );
	assertThat( plies.hasPly( 3 ), is( false ) );
	assertThat( plies.hasPly( 1729 ), is( false ) );
}
@Test
public void testGetPly()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies( boStelling );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();

	assertThrows( RuntimeException.class, () -> plies.getPly( 17 ) );
	assertThrows( RuntimeException.class, () -> plies.getPly( -1 ) );
	plies.addPly( firstPly );
	plies.addPly( secondPly );

	Ply newFirstPly = plies.getPly( 0 );
	assertThat( newFirstPly, is( firstPly ) );
	assertThat( newFirstPly.getBoStelling(), is( boStelling ) );
	assertThat( newFirstPly.getEinde(), is( Nog_niet ) );
	assertThat( newFirstPly.getZetNummer(), is( 17 ) );
	
	Ply newSecondPly = plies.getPly( 1 );
	assertThat( newSecondPly, is( secondPly ) );
	assertThat( newSecondPly.getBoStelling(), is( boStelling ) );
	assertThat( newSecondPly.getEinde(), is( Mat ) );
	assertThat( newSecondPly.getZetNummer(), is( 27 ) );
}
@Test
public void testGetCurrentPreviousAndLastPly()
{
	plies.setCurrentPlyNumberForTestingOnlhy( -5 );
	assertThrows( RuntimeException.class, () -> plies.getCurrentPly() );
	assertThrows( RuntimeException.class, () -> plies.getPreviousPly() );
	assertThrows( RuntimeException.class, () -> plies.getLastPly() );

	plies.setCurrentPlyNumberForTestingOnlhy( 0 );
	assertThrows( RuntimeException.class, () -> plies.getPreviousPly() );
	
	plies = new Plies( getConfig().getConfig() );
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();
	
	plies.addPly( firstPly );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getFirstPly(), is( firstPly ) );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
	assertThat( plies.getPreviousPly(), is( secondPly ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );
}
@Test
public void testGetFirstAndSecondPly()
{
	assertThrows( RuntimeException.class, () -> plies.getFirstPly() );
	assertThrows( RuntimeException.class, () -> plies.getSecondPly() );

	plies = new Plies( getConfig().getConfig() );
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();
	
	plies.addPly( firstPly );
	assertThrows( RuntimeException.class, () -> plies.getSecondPly() );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getFirstPly(), is( firstPly ) );
	assertThat( plies.getSecondPly(), is( secondPly ) );

}
@Test
public void testHasPlies()
{
	assertThat( plies.hasPlies(), is( false ) );
	plies.addPly( new Ply() );
	assertThat( plies.hasPlies(), is( true ) );
}
@Test
public void testIsAtLastPlyNumber()
{
	assertThat( plies.hasPlies(), is( false ) );
	plies.addPly( new Ply() );
	assertThat( plies.hasPlies(), is( true ) );
}
@Test
public void testSetToBeginAndEnd()
{
	assertThrows( RuntimeException.class, () -> plies.setToBegin() );
	assertFalse( plies.isNaarBeginMag() );
	assertThrows( RuntimeException.class, () -> plies.setNaarEinde() );
	assertFalse( plies.isNaarEindeMag() );

	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );

	assertTrue( plies.isNaarBeginMag() );
	assertFalse( plies.isNaarEindeMag() );
	plies.setToBegin();
	assertTrue( plies.isNaarEindeMag() );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	
	assertTrue( plies.isNaarEindeMag() );
	plies.setNaarEinde();
	assertFalse( plies.isNaarEindeMag() );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
}
@Test
public void testSetTerug()
{
	assertThrows( RuntimeException.class, () -> plies.setTerug() );
	assertFalse( plies.isTerugMag() );

	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	plies.setCurrentPlyNumberForTestingOnlhy( -1 );
	assertFalse( plies.isTerugMag() );
	plies.setCurrentPlyNumberForTestingOnlhy( 0 );
	assertFalse( plies.isTerugMag() );
	plies.addPly( secondPly );
	assertTrue( plies.isTerugMag() );
	plies.addPly( thirdPly );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
	assertTrue( plies.isTerugMag() );
	plies.setTerug();
	assertThat( plies.getCurrentPly(), is( secondPly ) );
}
@Test
public void testSetVooruit()
{
	assertThrows( RuntimeException.class, () -> plies.setVooruit() );
	assertFalse( plies.isVooruitMag() );

	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	assertTrue( plies.isVooruitMag() );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertTrue( plies.isVooruitMag() );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
	plies.setTerug();
	assertTrue( plies.isVooruitMag() );
	assertThat( plies.getCurrentPly(), is( secondPly ) );
	plies.setVooruit();
	assertTrue( plies.isVooruitMag() );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
}
@Test
public void testClearPliesFromNextPly()
{
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();
	
	plies.addPly( firstPly );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 2 ) );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
	assertThat( plies.getLastPlyNumber(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );

	plies.setTerug();
	plies.setTerug();
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 0 ) );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	assertThat( plies.getLastPlyNumber(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );
	
	plies.clearPliesFromNextPly();
	assertThat( plies.getSize(), is( 1 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 0 ) );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	assertThat( plies.getLastPlyNumber(), is( 0 ) );
	assertThat( plies.getLastPly(), is( firstPly ) );
}
@Test
public void testgetEindePartij()
{
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	//Ply thirdPly = threePlies.getRight();
	
	plies.addPly( firstPly );
	assertThat( plies.getCurrentEinde(), is( Nog_niet ) );
	plies.addPly( secondPly );
	assertThat( plies.getCurrentEinde(), is( Mat ) );
}

}