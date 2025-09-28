package pu.chessdatabase.bo.speel;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.EindeType.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.dal.Dbs;

@SpringBootTest
public class TestPartij
{
@Autowired private Partij partij;
@Autowired private Dbs dbs;

@BeforeEach
public void setup()
{
	dbs.Name( dbs.DFT_DBS_NAAM );
	dbs.Open();
	partij.inzPartij();
}
@AfterEach
public void destroy()
{
	dbs.Close();
}

@Test
public void testInzPartij()
{
	partij.inzPartij();
	// Dit werkt niet: om een of and're reden vindt-ie dat isNotNull null is
	//assertThat( partij.curPartij, isNotNull() );
	assertThat( partij.curPartij, is( notNullValue() ) );
	assertNotNull( partij.curPartij );
	for ( PlyRecord plyRecord : partij.plies )
	{
		assertThat( plyRecord, is( PlyRecord.NULL_PLY_RECORD ) );
	}
}
@Test
public void testIsLegaleStelling()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0 )
		.zk( 0 )
		.s3( 0 )
		.s4( 0 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
}
@Test
public void testIsEindStelling()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0 )
		.zk( 0 )
		.s3( 0 )
		.s4( 0 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( EindeType.Illegaal ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( NogNiet ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x20 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( Mat ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x00 )
		.s4( 0x12 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( Pat ) );
}
@Test
public void testNewGame()
{
	BoStelling startStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	assertThat( startStelling.isSchaak(), is( false ) );
	PlyRecord plyRecord = PlyRecord.builder()
		.boStelling( startStelling )
		.Einde( NogNiet )
		.ZetNr( 1 )
		.vanNaar( VanNaar.ILLEGAL_VAN_NAAR )
		.build();
	assertThat( partij.plies[0], is( plyRecord ) );
}
@Test
public void testIsBegonnen()
{
	assertThat( partij.isBegonnen(), is( false ) );
	BoStelling startStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	assertThat( partij.isBegonnen(), is( true ) );
}
@Test
public void testIsEindePartij()
{
	BoStelling startStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	assertThat( partij.isEindePartij(), is( NogNiet ) );

	startStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x20 )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	assertThat( partij.isEindePartij(), is( Mat ) );
}
@SuppressWarnings( "null" )
@Test
public void testStellingToVanNaar()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x66 )
		.s4( 0x77 )
		.aanZet( Zwart )
		.build();
	assertThat( partij.stellingToVanNaar( boStellingVan, boStellingNaar ), is( new VanNaar( 0x11, 0x66 ) ) );
}
@Test
public void testVanNaarToStelling()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.plies[partij.curPartij.getCurPly()].setBoStelling( boStellingVan );
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x66 )
		.s4( 0x77 )
		.aanZet( Zwart )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.schaak( true )
		.build();
	assertThat( partij.vanNaarToStelling( new VanNaar( 0x11, 0x66 ) ), is( boStellingNaar ) );
}
@Test
public void testIsLegaal()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x21 )
		.aanZet( Zwart )
		.build();
	partij.plies[partij.curPartij.getCurPly()].setBoStelling( boStelling );
	assertThat( partij.isLegaal( new VanNaar( 0x21, 0x20 ) ), is( true ) );
	
	boStelling.setAanZet( Wit );
	assertThat( partij.isLegaal( new VanNaar( 0x21, 0x20 ) ), is( false ) );
}
@Test
public void testZetVooruit()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	partij.curPartij.setCurPly( 2 );
	partij.curPartij.setLastPly( 15 );
	partij.zetVooruit();
	assertThat( partij.curPartij.getCurPly(), is( 3 ) );
}
@Test
public void testZetTerug()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	partij.curPartij.setCurPly( 2 );
	partij.zetTerug();
	assertThat( partij.curPartij.getCurPly(), is( 1 ) );
}
@Test
public void testZet()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.curPartij.setBegonnen( false );
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x22 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x22 );
	partij.curPartij.setBegonnen( false );
	
	assertThat( partij.zet( vanNaar ), is( false ) );

	partij.curPartij.setBegonnen( true );
	partij.plies[0].setEinde( Mat );
	assertThat( partij.zet( vanNaar ), is( false ) );
	partij.plies[0].setEinde( NogNiet );
	
	partij.newGame( boStellingVan );
	assertThat( partij.plies[0].getBoStelling(), is( boStellingVan ) );
	assertThat( partij.vanNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	partij.zet( vanNaar );
	assertThat( partij.plies[0].getVanNaar(), is( vanNaar ) );
	assertThat( partij.curPartij.getCurPly(), is( 1 ) );
	assertThat( partij.curPartij.getLastPly(), is( 1 ) );
	assertThat( partij.plies[1].getBoStelling(), is( boStellingNaar ) );
	assertThat( partij.plies[1].getEinde(), is( NogNiet ) );
	assertThat( partij.plies[1].getZetNr(), is( 1 ) );
	

	
	
	
	
}
}
