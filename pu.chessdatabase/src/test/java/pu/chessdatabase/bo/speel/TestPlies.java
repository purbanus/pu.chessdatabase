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
import pu.chessdatabase.dbs.TestHelper;

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
	assertThat( plies.getCurrentPlyNummer(), is( 1 ) );
	assertThat( plies.getLastPlyNummer(), is( 1 ) );
	plies.clear();
	assertThat( plies.getSize(), is( 0 ) );
	assertThat( plies.getCurrentPlyNummer(), is( -1 ) );
	assertThat( plies.getLastPlyNummer(), is( -1 ) );
}
@Test
public void testAddPly()
{
	Ply ply = new Ply();
	plies.addPly( ply );
	assertThat( plies.getSize(), is( 1 ) );
	assertThat( plies.getCurrentPlyNummer(), is( 0 ) );
	assertThat( plies.getLastPlyNummer(), is( 0 ) );
	assertThat( plies.isBegonnen(), is( true ) );
	
	ply.setPlyNummer( 1 );
	plies.addPly( ply );
	assertThat( plies.getSize(), is( 2 ) );
	assertThat( plies.getCurrentPlyNummer(), is( 1 ) );
	assertThat( plies.getLastPlyNummer(), is( 1 ) );
	assertThat( plies.isBegonnen(), is( true ) );
}
@Test
public void testAddPlyWithBoStellingVanNaarAndEinde()
{
	VanNaar vanNaar = VanNaar.alfaBuilder()
		.van( "b2" )
		.naar( "g7" )
		.build();
	BoStelling boStellingMetWitAanZet = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.schaak(  true )
		.build();
	plies.addPly( boStellingMetWitAanZet, vanNaar, Nog_niet );
	assertThat( plies.getSize(), is( 1 ) );
	assertThat( plies.getCurrentPlyNummer(), is( 0 ) );
	assertThat( plies.getLastPlyNummer(), is( 0 ) );
	Ply firstPly = plies.getFirstPly();
	assertThat( firstPly.getBoStelling(), is( boStellingMetWitAanZet ) );
	assertThat( firstPly.getEinde(), is( Nog_niet ) );
	assertThat( firstPly.getZetNummer(), is( 1 ) );

	BoStelling boStellingMetZwartAanZet = boStellingMetWitAanZet.clone();
	boStellingMetZwartAanZet.setAanZet( Zwart );
	plies.addPly( boStellingMetZwartAanZet, vanNaar, Mat );
	Ply secondPly = plies.getSecondPly();
	assertThat( secondPly.getBoStelling(), is( boStellingMetZwartAanZet ) );
	assertThat( secondPly.getEinde(), is( Mat ) );
	assertThat( secondPly.getZetNummer(), is( 2 ) );

	plies.addPly( boStellingMetWitAanZet, vanNaar, Nog_niet );
	Ply thirdPly = plies.getLastPly();
	assertThat( thirdPly.getBoStelling(), is( boStellingMetWitAanZet ) );
	assertThat( thirdPly.getEinde(), is( Nog_niet ) );
	assertThat( thirdPly.getZetNummer(), is( 2 ) );
}
@Test
public void testHasPly()
{
	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
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
	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies(), boStelling );
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
	assertThat( newFirstPly.getPlyNummer(), is( 0 ) );
	
	Ply newSecondPly = plies.getPly( 1 );
	assertThat( newSecondPly, is( secondPly ) );
	assertThat( newSecondPly.getBoStelling(), is( boStelling ) );
	assertThat( newSecondPly.getEinde(), is( Mat ) );
	assertThat( newSecondPly.getPlyNummer(), is( 1 ) );
}
@Test
public void testGetCurrentPreviousAndLastPly()
{
	plies.setCurrentPlyNummerForTestingOnlhy( -5 );
	assertThrows( RuntimeException.class, () -> plies.getCurrentPly() );
	assertThrows( RuntimeException.class, () -> plies.getPreviousPly() );
	assertThrows( RuntimeException.class, () -> plies.getLastPly() );

	plies.setCurrentPlyNummerForTestingOnlhy( 0 );
	assertThrows( RuntimeException.class, () -> plies.getPreviousPly() );
	
	plies = new Plies( getConfig().getConfig() );
	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
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
	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
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
public void testIsAtLastPlyNummer()
{
	assertThat( plies.hasPlies(), is( false ) );
	plies.addPly( new Ply() );
	assertThat( plies.hasPlies(), is( true ) );
}
@Test
public void testSetToBeginAndEnd()
{
	assertThrows( RuntimeException.class, () -> plies.setNaarBegin() );
	assertFalse( plies.isNaarBeginMag() );
	assertThrows( RuntimeException.class, () -> plies.setNaarEinde() );
	assertFalse( plies.isNaarEindeMag() );

	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	assertTrue( plies.isNaarBeginMag() );
	plies.setNaarBegin();
	plies.setVooruit();
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );

	assertTrue( plies.isNaarBeginMag() );
	assertFalse( plies.isNaarEindeMag() );
	plies.setNaarBegin();
	assertTrue( plies.isNaarEindeMag() );
	assertThat( plies.getCurrentPlyNummer(), is( -1 ) );
	assertThrows( RuntimeException.class, () -> plies.getCurrentPly() );
	
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

	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	assertTrue( plies.isTerugMag() );
	plies.setTerug();
	plies.setVooruit();
	plies.setCurrentPlyNummerForTestingOnlhy( 0 );
	assertTrue( plies.isTerugMag() );
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

	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	assertTrue( plies.isVooruitMag() );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertFalse( plies.isVooruitMag() ); // Want die ply is Mat
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
	plies.setTerug();
	assertFalse( plies.isVooruitMag() ); // Want die ply is ook Mat
	assertThat( plies.getCurrentPly(), is( secondPly ) );
	plies.setTerug();
	assertTrue( plies.isVooruitMag() );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	plies.setVooruit();
	plies.setVooruit();
	assertFalse( plies.isVooruitMag() );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
}
@Test
public void testClearPliesFromNextPly()
{
	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();
	
	plies.addPly( firstPly );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNummer(), is( 2 ) );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
	assertThat( plies.getLastPlyNummer(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );

	plies.setTerug();
	plies.setTerug();
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNummer(), is( 0 ) );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	assertThat( plies.getLastPlyNummer(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );
	
	plies.clearPliesFromNextPly();
	assertThat( plies.getSize(), is( 1 ) );
	assertThat( plies.getCurrentPlyNummer(), is( 0 ) );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	assertThat( plies.getLastPlyNummer(), is( 0 ) );
	assertThat( plies.getLastPly(), is( firstPly ) );
}
@Test
public void testgetEindePartij()
{
	Triple<Ply, Ply, Ply> threePlies = TestHelper.createThreeDifferentPlies( getPlies() );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	//Ply thirdPly = threePlies.getRight();
	
	plies.addPly( firstPly );
	assertThat( plies.getCurrentEinde(), is( Nog_niet ) );
	plies.addPly( secondPly );
	assertThat( plies.getCurrentEinde(), is( Mat ) );
}

}