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

import java.util.List;

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
		assertThat( plyRecord, is( PlyRecord.getNullPlyRecord() ) );
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
	assertThat( partij.isEindStelling( boStelling ), is( EindeType.ILLEGAAL ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( NOG_NIET ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x20 )
		.aanZet( WIT )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( MAT ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x00 )
		.s4( 0x12 )
		.aanZet( WIT )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( PAT ) );
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
		.einde( NOG_NIET )
		.zetNr( 1 )
		.vanNaar( VanNaar.ILLEGAL_VAN_NAAR )
		.build();
	assertThat( partij.plies[0], is( plyRecord ) );
	
	final BoStelling illegaleStartStelling = BoStelling.builder()
		.wk( 0x35 )
		.zk( 0x37 )
		.s3( 0x36 )
		.s4( 0x00 )
		.aanZet( WIT )
		.build();
	assertThrows( RuntimeException.class, () -> partij.newGame( illegaleStartStelling ) );
	;
//@@HIERO
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
	assertThat( partij.isEindePartij(), is( NOG_NIET ) );

	startStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x20 )
		.aanZet( WIT )
		.build();
	partij.newGame( startStelling );
	assertThat( partij.isEindePartij(), is( MAT ) );
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
	assertThrows( RuntimeException.class, () -> partij.isLegaal( new VanNaar( 0x21, 0x20 ) ) );

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
/** @@NOG Deze tests ook inbouwen
 * De belangrijkste struktuur is Plies. Een aantal voorbeelden:

Voorbeeld a):  Wit begint

1. Ke2-e3 Ta1-a8
2. Ke3-e4 Kf6-g6

Plies ziet er als volgt uit:

                 Stelling       ZetNr       Van/naar
Plies[0]     Ke2Dh1Kf6Ta1 waz     1          e2 e3
Plies[1]     Ke3Dh1Kf6Ta1 zaz     1          a1 a8
Plies[2]     Ke3Dh1Kf6Ta8 waz     2          e3 e4
Plies[3]     Ke4Dh1Kf6Ta8 zaz     2          f6 g6
Plies[4]     Ke3Dh1Kg6Ta8 waz     3           ...

Voorbeeld b):  Zwart begint

1.   ...  Ta1-a8
2. Ke3-e4 Kf6-g6

Het plyarray ziet er als volgt uit:

                 Stelling       ZetNr       Van/naar
Plies[0]     Ke3Dh1Kf6Ta1 zaz     1          a1 a8
Plies[1]     Ke3Dh1Kf6Ta8 waz     2          e3 e4
Plies[2]     Ke4Dh1Kf6Ta8 zaz     2          f6 g6
Plies[3]     Ke3Dh1Kg6Ta8 waz     3           ...

Met andere woorden, in een plyrecord zit de stelling waaruit zetten
gegenereerd worden, plus de zet die uiteindelijk gedaan is. Het zetnummer
is gewoon het nummer dat afgedrukt moet worden.


 */
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
	
	assertThrows( RuntimeException.class, () -> partij.zet( vanNaar ) );

	partij.curPartij.setBegonnen( true );
	partij.plies[0].setEinde( MAT );
	assertThrows( RuntimeException.class, () -> partij.zet( vanNaar ) );
	partij.plies[0].setEinde( NOG_NIET );
	
	partij.newGame( boStellingVan );
	assertThat( partij.plies[0].getBoStelling(), is( boStellingVan ) );
	assertThat( partij.vanNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zet( vanNaar ), is( true ) );
	assertThat( partij.plies[0].getVanNaar(), is( vanNaar ) );
	assertThat( partij.curPartij.getCurPly(), is( 1 ) );
	assertThat( partij.curPartij.getLastPly(), is( 1 ) );
	assertThat( partij.plies[1].getBoStelling(), is( boStellingNaar ) );
	assertThat( partij.plies[1].getEinde(), is( NOG_NIET ) );
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
	assertThat( partij.plies[1].getEinde(), is( NOG_NIET ) );
	assertThat( partij.plies[1].getZetNr(), is( 2 ) );
}
@Test
public void testIsSlagZet()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x66 );
	assertThat( partij.isSlagZet( boStelling, vanNaar ), is( true ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	vanNaar = new VanNaar( 0x11, 0x17 );
	assertThat( partij.isSlagZet( boStelling, vanNaar ), is( false ) );
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
	assertThat( partij.plies[1].getEinde(), is( NOG_NIET ) );
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
	assertThat( partij.plies[1].getEinde(), is( NOG_NIET ) );
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
		.einde( NOG_NIET )
		.vanNaar( vanNaar )
		.zetNr( 1 )
		.schaak( false )
		.build();
	assertThat( partij.plyToString( plyRecord ), is( "Db2-c3 " ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	vanNaar = new VanNaar( 0x11, 0x66 );
	plyRecord = PlyRecord.builder()
		.boStelling( boStelling )
		.einde( NOG_NIET )
		.vanNaar( vanNaar )
		.zetNr( 1 )
		.schaak( true )
		.build();
	assertThat( partij.plyToString( plyRecord ), is( "Db2xg7+" ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.schaak(  true )
		.aanZet( WIT )
		.build();
	vanNaar = new VanNaar( 0x11, 0x17 );
	plyRecord = PlyRecord.builder()
		.boStelling( boStelling )
		.einde( NOG_NIET )
		.vanNaar( vanNaar )
		.zetNr( 1 )
		.schaak( true )
		.build();
	assertThat( partij.plyToString( plyRecord ), is( "Db2-h2+" ) );
}
@Test
public void testCurPlyToString()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x22 );
	partij.newGame( boStelling );
	partij.zet( vanNaar );
	assertThat( partij.curPlyToString(), is( "Db2-c3 " ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.schaak( false )
		.aanZet( WIT )
		.build();
	vanNaar = new VanNaar( 0x11, 0x17 );
	partij.newGame( boStelling );
	partij.zet( vanNaar );
	assertThat( partij.curPlyToString(), is( "Db2-h2+" ) );
}
@Test
public void testResultaatToString()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x35 )
		.zk( 0x37 )
		.s3( 0x36 )
		.s4( 0x00 )
		.aanZet( ZWART )
		.build();
	partij.newGame( boStelling );
	ResultaatRecord resultaatRecord = new ResultaatRecord( "Mat", "" );
	assertThat( partij.getResultaatRecord(), is( resultaatRecord ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	partij.newGame( boStelling );
	resultaatRecord = new ResultaatRecord( "Gewonnen", "Mat in 29" );
	assertThat( partij.getResultaatRecord(), is( resultaatRecord ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.newGame( boStelling );
	resultaatRecord = new ResultaatRecord( "Verloren", "Mat in 30" );
	assertThat( partij.getResultaatRecord(), is( resultaatRecord ) );
}
@Test
public void testZetNummerToString()
{
	assertThat( partij.zetNummerToString(   3 ), is ( "  3" ) );
	assertThat( partij.zetNummerToString(  13 ), is ( " 13" ) );
	assertThat( partij.zetNummerToString( 313 ), is ( "313" ) );
}
@Test
public void testHeleZetToString()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( 0x11, 0x66 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x77, 0x66 );
	partij.zet( vanNaar );
	assertThat( partij.heleZetToString( 0 ), is( "  1. Db2xg7+ Kh8xg7 " ) );
}
@Test
public void testCreateZetten()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( 0x11, 0x66 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x77, 0x66 );
	partij.zet( vanNaar );
	
	List<String> zetten = partij.createZetten();
	assertThat( zetten.size(), is( 1 ) );
	assertThat( zetten.get( 0 ), is( "  1. Db2xg7+ Kh8xg7 " ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.newGame( boStelling );
	vanNaar = new VanNaar( 0x77, 0x76 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x11, 0x71 );
	partij.zet( vanNaar );
	
	zetten = partij.createZetten();
	assertThat( zetten.size(), is( 2 ) );
	assertThat( zetten.get( 0 ), is( "  1.   ...   Kh8-g8 " ) );
	assertThat( zetten.get( 1 ), is( "  2. Db2-b8+" ) );
}
@Test
public void testCreateVooruit()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( WIT )
		.build();
	partij.newGame( boStelling );
	VooruitRecord vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 1 )
		.halverwege( false ) // ??
		.build();
	assertThat( partij.createVooruit(), is( vooruitRecord ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( 0x77, 0x76 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x11, 0x71 );
	partij.zet( vanNaar );
	vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 2 )
		.halverwege( true )
		.build();
	assertThat( partij.createVooruit(), is( vooruitRecord ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.newGame( boStelling );
	vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 1 )
		.halverwege( true ) // ZAZ
		.build();
	assertThat( partij.createVooruit(), is( vooruitRecord ) );

	vanNaar = new VanNaar( 0x77, 0x76 );
	partij.zet( vanNaar );
	vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 1 )
		.halverwege( false ) // WAZ
		.build();
	assertThat( partij.createVooruit(), is( vooruitRecord ) );

	vanNaar = new VanNaar( 0x11, 0x71 );
	partij.zet( vanNaar );
	vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 2 )
		.halverwege( true )
		.build();
	assertThat( partij.createVooruit(), is( vooruitRecord ) );

	vanNaar = new VanNaar( 0x76, 0x67 );
	partij.zet( vanNaar );
	vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 2 )
		.halverwege( false )
		.build();
	assertThat( partij.createVooruit(), is( vooruitRecord ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.newGame( boStelling );
	partij.curPartij.setLastPly( 15 ); // Dit heeft dus geen effect
	vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 1 )
		.halverwege( true ) // ZAZ
		.build();
	assertThat( partij.createVooruit(), is( vooruitRecord ) );
}
@Test
public void testPartijReport()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( ZWART )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( 0x77, 0x76 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x11, 0x71 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x76, 0x67 );
	partij.zet( vanNaar );
	
	PartijReport partijReport = partij.getPartijReport();
	assertThat( partijReport.isErZijnZetten(), is( true ) );
	
	VooruitRecord vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 2 )
		.halverwege( false )
		.build();
	assertThat( partijReport.getVooruit(), is( vooruitRecord ) );
	
	List<String> zetten = partijReport.getZetten();
	assertThat( zetten.size(), is( 2 ) );
	assertThat( zetten.get( 0 ), is( "  1.   ...   Kh8-g8 " ) );
	assertThat( zetten.get( 1 ), is( "  2. Db2-b8+ Kg8-h7 " ) );
}


}