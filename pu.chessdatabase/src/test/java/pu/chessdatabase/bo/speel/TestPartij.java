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
	dbs.name( dbs.DFT_DBS_NAAM );
	dbs.open();
	partij.inzPartij();
}
@AfterEach
public void destroy()
{
	dbs.close();
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
		.aanZet( WIT )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.aanZet( WIT )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.aanZet( WIT )
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
		.aanZet( WIT )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( EindeType.Illegaal ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( NogNiet ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x20 )
		.aanZet( WIT )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( Mat ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x00 )
		.s4( 0x12 )
		.aanZet( WIT )
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
		.aanZet( WIT )
		.build();
	partij.newGame( startStelling );
	assertThat( startStelling.isSchaak(), is( false ) );
	PlyRecord plyRecord = PlyRecord.builder()
		.boStelling( startStelling )
		.einde( NogNiet )
		.zetNr( 1 )
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
		.aanZet( WIT )
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
		.aanZet( WIT )
		.build();
	partij.newGame( startStelling );
	assertThat( partij.isEindePartij(), is( NogNiet ) );

	startStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x20 )
		.aanZet( WIT )
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
		.aanZet( WIT )
		.build();
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x66 )
		.s4( 0x77 )
		.aanZet( ZWART )
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
		.aanZet( WIT )
		.build();
	partij.plies[partij.curPartij.getCurPly()].setBoStelling( boStellingVan );
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x66 )
		.s4( 0x77 )
		.aanZet( ZWART )
		.resultaat( REMISE )
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
		.aanZet( ZWART )
		.build();
	partij.plies[partij.curPartij.getCurPly()].setBoStelling( boStelling );
	assertThat( partij.isLegaal( new VanNaar( 0x21, 0x20 ) ), is( true ) );
	
	boStelling.setAanZet( WIT );
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
		.aanZet( WIT )
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
		.aanZet( WIT )
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
		.aanZet( WIT )
		.build();
	partij.curPartij.setBegonnen( false );
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x22 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.resultaat( VERLOREN )
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
	
	assertThat( partij.zet( vanNaar ), is( true ) );
	assertThat( partij.plies[0].getVanNaar(), is( vanNaar ) );
	assertThat( partij.curPartij.getCurPly(), is( 1 ) );
	assertThat( partij.curPartij.getLastPly(), is( 1 ) );
	assertThat( partij.plies[1].getBoStelling(), is( boStellingNaar ) );
	assertThat( partij.plies[1].getEinde(), is( NogNiet ) );
	assertThat( partij.plies[1].getZetNr(), is( 1 ) );
}
@Test
public void testZetMetZwart()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.curPartij.setBegonnen( false );
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x67 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.resultaat( GEWONNEN )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( 0x77, 0x67 );
	partij.newGame( boStellingVan );
	assertThat( partij.plies[0].getBoStelling(), is( boStellingVan ) );
	assertThat( partij.vanNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zet( vanNaar ), is( true ) );
	assertThat( partij.plies[0].getVanNaar(), is( vanNaar ) );
	assertThat( partij.curPartij.getCurPly(), is( 1 ) );
	assertThat( partij.curPartij.getLastPly(), is( 1 ) );
	assertThat( partij.plies[1].getBoStelling(), is( boStellingNaar ) );
	assertThat( partij.plies[1].getEinde(), is( NogNiet ) );
	assertThat( partij.plies[1].getZetNr(), is( 2 ) );
}
@Test
public void testZetStelling()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.curPartij.setBegonnen( false );
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x76 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.resultaat( GEWONNEN )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( 0x77, 0x76 );
	partij.newGame( boStellingVan );
	assertThat( partij.plies[0].getBoStelling(), is( boStellingVan ) );
	assertThat( partij.vanNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zetStelling( boStellingNaar ), is( true ) );
	assertThat( partij.plies[0].getVanNaar(), is( vanNaar ) );
	assertThat( partij.curPartij.getCurPly(), is( 1 ) );
	assertThat( partij.curPartij.getLastPly(), is( 1 ) );
	assertThat( partij.plies[1].getBoStelling(), is( boStellingNaar ) );
	assertThat( partij.plies[1].getEinde(), is( NogNiet ) );
	assertThat( partij.plies[1].getZetNr(), is( 2 ) );
}
@Test
public void testBedenk()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	partij.curPartij.setBegonnen( false );
	// @@NOG Dit klopt niet. 
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x16 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.resultaat( GEWONNEN )
		.aantalZetten( 12 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x16 );
	partij.newGame( boStellingVan );
	assertThat( partij.plies[0].getBoStelling(), is( boStellingVan ) );
	assertThat( partij.vanNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.bedenk(), is( true ) );
	assertThat( partij.plies[0].getVanNaar(), is( vanNaar ) );
	assertThat( partij.curPartij.getCurPly(), is( 1 ) );
	assertThat( partij.curPartij.getLastPly(), is( 1 ) );
	assertThat( partij.plies[1].getBoStelling(), is( boStellingNaar ) );
	assertThat( partij.plies[1].getEinde(), is( NogNiet ) );
	assertThat( partij.plies[1].getZetNr(), is( 1 ) );
}
@Test
public void testWatStaatErOp()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	assertThat( partij.watStaatErOp( boStelling, 0x00 ), is( "K" ) );
	assertThat( partij.watStaatErOp( boStelling, 0x77 ), is( "K" ) );
	assertThat( partij.watStaatErOp( boStelling, 0x11 ), is( "D" ) );
	assertThat( partij.watStaatErOp( boStelling, 0x66 ), is( "T" ) );
	assertThat( partij.watStaatErOp( boStelling, 0x15 ), is( "?" ) );
}
@Test
public void testPlyToString()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x22 );
	PlyRecord plyRecord = PlyRecord.builder()
		.boStelling( boStelling )
		.einde( NogNiet )
		.vanNaar( vanNaar )
		.zetNr( 1 )
		.build();
	assertThat( partij.plyToString( plyRecord ), is( "Db2-c3 " ) );
}


}