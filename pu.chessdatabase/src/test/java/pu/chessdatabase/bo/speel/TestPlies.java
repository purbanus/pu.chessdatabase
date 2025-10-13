package pu.chessdatabase.bo.speel;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.EindeType.*;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import pu.chessdatabase.bo.BoStelling;

public class TestPlies
{
Plies plies = new Plies();

private Triple<Ply, Ply, Ply> createThreeDifferentPlies( BoStelling aBoStelling )
{
	Ply firstPly = Ply.builder()
		.boStelling( aBoStelling )
		.einde( NOG_NIET )
		.schaak( false )
		.vanNaar( new VanNaar( "a1", "a2" ) )
		.zetNr( 17 )
		.build();
	Ply secondPly = Ply.builder()
		.boStelling( aBoStelling )
		.einde( MAT )
		.schaak( false )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.zetNr( 27 )
		.build();
	Ply thirdPly = Ply.builder()
		.boStelling( aBoStelling )
		.einde( MAT )
		.schaak( false )
		.vanNaar( new VanNaar( "b2", "c3" ) )
		.zetNr( 39 )
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
		.aanZet( WIT )
		.build();
	plies.addPly( boStellingMetWitAanZet, NOG_NIET );
	assertThat( plies.getSize(), is( 1 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 0 ) );
	assertThat( plies.getLastPlyNumber(), is( 0 ) );
	Ply firstPly = plies.getFirstPly();
	assertThat( firstPly.getBoStelling(), is( boStellingMetWitAanZet ) );
	assertThat( firstPly.getEinde(), is( NOG_NIET ) );
	assertThat( firstPly.getZetNr(), is( 1 ) );

	BoStelling boStellingMetZwartAanZet = boStellingMetWitAanZet.clone();
	boStellingMetZwartAanZet.setAanZet( ZWART );
	plies.addPly( boStellingMetZwartAanZet, MAT );
	Ply secondPly = plies.getSecondPly();
	assertThat( secondPly.getBoStelling(), is( boStellingMetZwartAanZet ) );
	assertThat( secondPly.getEinde(), is( MAT ) );
	assertThat( secondPly.getZetNr(), is( 1 ) );

	plies.addPly( boStellingMetWitAanZet, NOG_NIET );
	Ply thirdPly = plies.getLastPly();
	assertThat( thirdPly.getBoStelling(), is( boStellingMetWitAanZet ) );
	assertThat( thirdPly.getEinde(), is( NOG_NIET ) );
	assertThat( thirdPly.getZetNr(), is( 2 ) );
}
@Test
public void testHasPly()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( WIT )
		.build();
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies( boStelling );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();
	Ply thirdPly = threePlies.getRight();

	plies.addPly( firstPly );
	plies.addPly( secondPly );
	plies.addPly( thirdPly );
	assertThat( plies.hasPly( 0 ), is( true ) );
	assertThat( plies.hasPly( 1 ), is( true ) );
	assertThat( plies.hasPly( 2 ), is( true ) );
	assertThat( plies.hasPly( 3 ), is( false ) );
	assertThat( plies.hasPly( 1729 ), is( false ) );
}
@Test
public void testGetPly()
{
	//@@NOG Test met plynummer > lastPlyNummer
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( WIT )
		.build();
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies( boStelling );
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();

	plies.addPly( firstPly );
	plies.addPly( secondPly );
	assertThat( plies.getFirstPly(), is( firstPly ) );
	assertThat( plies.getFirstPly().getBoStelling(), is( boStelling ) );
	assertThat( plies.getFirstPly().getEinde(), is( NOG_NIET ) );
	assertThat( plies.getFirstPly().getZetNr(), is( 17 ) );
	assertThat( plies.getSecondPly(), is( secondPly ) );
	assertThat( plies.getSecondPly().getBoStelling(), is( boStelling ) );
	assertThat( plies.getSecondPly().getEinde(), is( MAT ) );
	assertThat( plies.getSecondPly().getZetNr(), is( 27 ) );
}
@Test
public void testGetCurrentPreviousAndLastPly()
{
	//@@NOG Test met currentplynummer <= 0
	Triple<Ply, Ply, Ply> threePlies = createThreeDifferentPlies();
	Ply firstPly = threePlies.getLeft();
	Ply secondPly = threePlies.getMiddle();

	assertThat( plies.getCurrentPly(), is( nullValue() ) );
	assertThat( plies.getPreviousPly(), is( nullValue() ) );
	
	plies.addPly( firstPly );
	plies.addPly( secondPly );
	assertThat( plies.getSize(), is( 2 ) );
	assertThat( plies.getCurrentPly(), is( secondPly ) );
	assertThat( plies.getPreviousPly(), is( firstPly ) );
	assertThat( plies.getLastPly(), is( secondPly ) );
}
@Test
public void testGetFirstPly()
{
	// @@NOG
}
@Test
public void testGetSecondPly()
{
	// @@NOG
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
public void testSetToBegin()
{
	// @@NOG test met isBegonnen = false
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

	plies.setToBegin();
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 0 ) );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	assertThat( plies.getLastPlyNumber(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );
}
@Test
public void testSetTerug()
{
	// @@NOG test met isBegonnen = false

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
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 1 ) );
	assertThat( plies.getCurrentPly(), is( secondPly ) );
	assertThat( plies.getLastPlyNumber(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );
}
@Test
public void testSetVooruit()
{
	// @@NOG
}
@Test
public void testSetToEnd()
{
	// @@NOG test met isBegonnen = false

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

	plies.setToBegin();
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 0 ) );
	assertThat( plies.getCurrentPly(), is( firstPly ) );
	assertThat( plies.getLastPlyNumber(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );
	
	plies.setToEnd();
	assertThat( plies.getSize(), is( 3 ) );
	assertThat( plies.getCurrentPlyNumber(), is( 2 ) );
	assertThat( plies.getCurrentPly(), is( thirdPly ) );
	assertThat( plies.getLastPlyNumber(), is( 2 ) );
	assertThat( plies.getLastPly(), is( thirdPly ) );
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
	assertThat( plies.getCurrentEinde(), is( NOG_NIET ) );
	plies.addPly( secondPly );
	assertThat( plies.getCurrentEinde(), is( MAT ) );
}

}
