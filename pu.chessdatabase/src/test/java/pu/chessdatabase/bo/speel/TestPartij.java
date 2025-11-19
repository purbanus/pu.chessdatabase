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
import static pu.chessdatabase.bo.speel.Einde.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Stuk;
import pu.chessdatabase.dbs.Dbs;
import pu.chessdatabase.dbs.Resultaat;

@SpringBootTest
public class TestPartij
{
@Autowired private Partij partij;
@Autowired private Dbs dbs;
@Autowired private Config config;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( "KDKT" ); // Dit opent de database
}
@AfterEach
public void destroy()
{
	dbs.close();
	config.switchConfig( savedConfigString );
}
@Test
public void testHexGetalToVeld()
{
	assertThat( Partij.hexGetalToVeld(  0 ), is( 0x00 ) );
	assertThat( Partij.hexGetalToVeld(  7 ), is( 0x07 ) );
	assertThat( Partij.hexGetalToVeld( 10 ), is( 0x10 ) );
	assertThat( Partij.hexGetalToVeld( 17 ), is( 0x17 ) );
	assertThat( Partij.hexGetalToVeld( 70 ), is( 0x70 ) );
	assertThat( Partij.hexGetalToVeld( 77 ), is( 0x77 ) );
	assertThat( Partij.hexGetalToVeld( 78 ), is( 0x78 ) ); // Maar dit is eigenlijk buiten het bord
}
@Test
public void testVeldToHexGetal()
{
	assertThat( Partij.veldToHexGetal( 0x00 ), is(  0 ) );
	assertThat( Partij.veldToHexGetal( 0x07 ), is(  7 ) );
	assertThrows( RuntimeException.class, () -> Partij.veldToHexGetal( 0x0a ) );
	assertThat( Partij.veldToHexGetal( 0x10 ), is( 10 ) );
	assertThat( Partij.veldToHexGetal( 0x17 ), is( 17 ) );
	assertThat( Partij.veldToHexGetal( 0x70 ), is( 70 ) );
	assertThat( Partij.veldToHexGetal( 0x77 ), is( 77 ) );
	assertThrows( RuntimeException.class, () -> Partij.veldToHexGetal( 0x78 ) );
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
	assertThat( partij.isEindStelling( boStelling ), is( Einde.Illegaal ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	assertThat( partij.isEindStelling( boStelling ), is( Nog_niet ) );
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
	BoStelling newBoStelling = partij.newGame( startStelling );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	Ply ply = Ply.builder()
		.boStelling( newBoStelling )
		.einde( Nog_niet )
		.zetNummer( 1 )
		.vanNaar( null )
		.build();
	assertThat( partij.getPlies().getFirstPly(), is( ply ) );
	
	final BoStelling illegaleStartStelling = BoStelling.builder()
		.wk( 0x35 )
		.zk( 0x37 )
		.s3( 0x36 )
		.s4( 0x00 )
		.aanZet( Wit )
		.build();
	assertThrows( RuntimeException.class, () -> partij.newGame( illegaleStartStelling ) );
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
public void testVanCurrentPlyNaarToStelling()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	boStellingVan = partij.newGame( boStellingVan );
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
	assertThat( partij.vanCurrentPlyNaarToStelling( new VanNaar( 0x11, 0x66 ) ), is( boStellingNaar ) );
}
@Test
public void testIsLegalMove()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "b3" ) //0x12
		.s3( "a1" )
		.s4( "c2" ) //0x21
		.aanZet( Zwart )
		.build();
	// @@NOG klopt niet, bij het paard komt hier 0x3f uit!!!
	//int naar = 0x21 + config.getStukken().getS4().getRichtingen().get( 3 );
	int naar = 0x20;
	boStelling = partij.newGame( boStelling );
	assertThat( partij.isLegalMove( boStelling, new VanNaar( 0x21, naar ) ), is( true ) );
	
	final BoStelling boStelling2 = boStelling.clone();
	boStelling2.setAanZet( Wit );
	assertThrows( RuntimeException.class, () -> partij.isLegalMove( boStelling2, new VanNaar( 0x21, 0x20 ) ) );

}
/**
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

Met andere woorden, in een Ply zit de stelling waaruit zetten
gegenereerd worden, plus de zet die uiteindelijk gedaan is. Het zetnummer
is gewoon het nummer dat afgedrukt moet worden.


 */

@Test
public void testZetNaarBegin()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	partij.zet( "Db2-e5" );
	partij.zet( "Kh8-h7" );
	partij.zet( "Ka1-b2" );
	BoStelling curBoStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h7" )
		.s3( "e5" )
		.s4( "g7" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 27 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( curBoStelling ) );
	BoStelling actualNaarBeginStelling = partij.zetNaarBegin();
	BoStelling expectedNaarBeginStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.schaak(  false )
		.build();
	assertThat( actualNaarBeginStelling, is( expectedNaarBeginStelling ) );
	
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 0 ) );
}
@Test
public void testZetTerug()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	partij.zet( "Db2-e5" );
	partij.zet( "Kh8-h7" );
	partij.zet( "Ka1-b2" );
	BoStelling curBoStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h7" )
		.s3( "e5" )
		.s4( "g7" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 27 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( curBoStelling ) );
	BoStelling actualTerugStelling = partij.zetTerug();
	BoStelling expectedTerugStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h7" )
		.s3( "e5" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 28 )
		.schaak(  false )
		.build();
	assertThat( actualTerugStelling, is( expectedTerugStelling ) );
	
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 2 ) );
}
@Test
public void testZetVooruit()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	partij.zet( "Db2-e5" );
	partij.zet( "Kh8-h7" );
	partij.zet( "Ka1-b2" );
	partij.zetNaarBegin();
	BoStelling actualVooruitStelling = partij.zetVooruit();
	BoStelling expectedVooruitStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "e5" )
		.s4( "g7" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 28 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( actualVooruitStelling ) );
	assertThat( actualVooruitStelling, is( expectedVooruitStelling ) );
	
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 1 ) );
}
@Test
public void testZetNaarEinde()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.schaak( false )
		.build();
	partij.newGame( startStelling );
	partij.zet( "Db2-e5" );
	partij.zet( "Kh8-h7" );
	partij.zet( "Ka1-b2" );
	BoStelling actualStartStelling = partij.zetNaarBegin();
	assertThat( actualStartStelling, is( startStelling ) );
	BoStelling actualVooruitStelling = partij.zetNaarEinde();
	BoStelling expectedVooruitStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h7" )
		.s3( "e5" )
		.s4( "g7" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 27 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( actualVooruitStelling ) );
	assertThat( actualVooruitStelling, is( expectedVooruitStelling ) );
	
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 3 ) );
}
@Test
public void testZet()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	BoStelling boStellingNaar = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "c3" )
		.s4( "g7" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	assertThat( partij.getPlies().isBegonnen(), is ( false ) );
	VanNaar vanNaar = new VanNaar( "b2", "c3" );
	
	assertThrows( RuntimeException.class, () -> partij.zet( vanNaar ) );

	partij.newGame( boStellingVan );
	partij.getPlies().getFirstPly().setEinde( Mat );
	assertThrows( RuntimeException.class, () -> partij.zet( vanNaar ) );
	partij.getPlies().getFirstPly().setEinde( Nog_niet );
	
	BoStelling newBoStelling = partij.newGame( boStellingVan );
	assertThat( partij.getPlies().isBegonnen(), is ( true ) );
	assertThat( partij.getPlies().getFirstPly().getBoStelling(), is( newBoStelling ) );
	assertThat( partij.vanCurrentPlyNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zet( vanNaar ), is( boStellingNaar ) );
	assertThat( partij.getPlies().getFirstPly().getVanNaar(), is( vanNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 1 ) );
	assertThat( partij.getPlies().getLastPlyNumber(), is( 1 ) );
	Ply secondPly = partij.getPlies().getPly( 1 );
	assertThat( secondPly.getBoStelling(), is( boStellingNaar ) );
	assertThat( secondPly.getEinde(), is( Nog_niet ) );
	assertThat( secondPly.getZetNummer(), is( 1 ) );
}
@Test
public void testZetMetClearPliesVoorZet()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	partij.newGame( boStellingVan );
	partij.bedenk();
	partij.bedenk();
	partij.bedenk();
	partij.bedenk();
	BoStelling actualEindeStelling = partij.bedenk();
	BoStelling expectedEindeStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "f7" )
		.s3( "f5" )
		.s4( "g7" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 26 )
		.schaak( true )
		.build();
	assertThat( actualEindeStelling, is( expectedEindeStelling ) );
	
	partij.zetNaarBegin();
	for ( int x = 0; x <= 5; x++ )
	{
		assertThat( partij.getPlies().getPly( x ), is( notNullValue() ) );
	}
	assertThat( partij.getPlies().getSize(), is( 6 ) );
	partij.zet( "Db2-c3" );
	for ( int x = 0; x <= 1; x++ )
	{
		assertThat( partij.getPlies().getPly( x ), is( notNullValue() ) );
	}
	assertThat( partij.getPlies().getSize(), is( 2 ) );
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 1 ) );
	assertThat( partij.getPlies().getLastPlyNumber(), is( 1 ) );
}
@Test
public void testZetMetZwart()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.build();
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x67 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	assertThat( partij.getPlies().isBegonnen(), is ( false ) );

	VanNaar vanNaar = new VanNaar( 0x77, 0x67 );
	BoStelling newBoStelling = partij.newGame( boStellingVan );
	assertThat( partij.getPlies().isBegonnen(), is ( true ) );
	Ply firstPly = partij.getPlies().getFirstPly();
	assertThat( firstPly.getBoStelling(), is( newBoStelling ) );
	assertThat( partij.vanCurrentPlyNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zet( vanNaar ), is( boStellingNaar ) );
	assertThat( firstPly.getVanNaar(), is( vanNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 1 ) );
	assertThat( partij.getPlies().getLastPlyNumber(), is( 1 ) );
	Ply secondPly = partij.getPlies().getPly( 1 );
	assertThat( secondPly.getBoStelling(), is( boStellingNaar ) );
	assertThat( secondPly.getEinde(), is( Nog_niet ) );
	assertThat( secondPly.getZetNummer(), is( 2 ) );
}
@Test
public void testIsSlagZet()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x66 );
	assertThat( partij.isSlagZet( boStelling, vanNaar ), is( true ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
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
		.aanZet( Zwart )
		.build();
	BoStelling boStellingNaar = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x76 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( 0x77, 0x76 );
	BoStelling newBoStelling = partij.newGame( boStellingVan );
	Ply firstPly = partij.getPlies().getFirstPly();
	assertThat( firstPly.getBoStelling(), is( newBoStelling ) );
	assertThat( partij.vanCurrentPlyNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zetStelling( boStellingNaar ), is( boStellingNaar ) );
	assertThat( firstPly.getVanNaar(), is( vanNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 1 ) );
	assertThat( partij.getPlies().getLastPlyNumber(), is( 1 ) );
	Ply secondPly = partij.getPlies().getPly( 1 );
	assertThat( secondPly.getBoStelling(), is( boStellingNaar ) );
	assertThat( secondPly.getEinde(), is( Nog_niet ) );
	assertThat( secondPly.getZetNummer(), is( 2 ) );
}
@Test
public void testBedenk()
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
		.s3( 0x44 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 28 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x44 );
	BoStelling newBoStelling = partij.newGame( boStellingVan );
	Ply firstPly = partij.getPlies().getFirstPly();
	assertThat( firstPly.getBoStelling(), is( newBoStelling ) );
	assertThat( partij.vanCurrentPlyNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.bedenk(), is( boStellingNaar ) );
	assertThat( firstPly.getVanNaar(), is( vanNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNumber(), is( 1 ) );
	assertThat( partij.getPlies().getLastPlyNumber(), is( 1 ) );
	Ply secondPly = partij.getPlies().getPly( 1 );
	assertThat( secondPly.getBoStelling(), is( boStellingNaar ) );
	assertThat( secondPly.getEinde(), is( Nog_niet ) );
	assertThat( secondPly.getZetNummer(), is( 1 ) );
}
@Test
public void testWatStaatErOp()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
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
		.aanZet( Wit )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x22 );
	Ply ply = Ply.builder()
		.boStelling( boStelling )
		.einde( Nog_niet )
		.vanNaar( vanNaar )
		.zetNummer( 1 )
		.schaak( false )
		.build();
	assertThat( partij.plyToString( ply ), is( "Db2-c3 " ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	vanNaar = new VanNaar( 0x11, 0x66 );
	ply = Ply.builder()
		.boStelling( boStelling )
		.einde( Nog_niet )
		.vanNaar( vanNaar )
		.zetNummer( 1 )
		.schaak( true )
		.build();
	assertThat( partij.plyToString( ply ), is( "Db2xg7+" ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.schaak(  true )
		.aanZet( Wit )
		.build();
	vanNaar = new VanNaar( 0x11, 0x17 );
	ply = Ply.builder()
		.boStelling( boStelling )
		.einde( Nog_niet )
		.vanNaar( vanNaar )
		.zetNummer( 1 )
		.schaak( true )
		.build();
	assertThat( partij.plyToString( ply ), is( "Db2-h2+" ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.schaak(  true )
		.aanZet( Wit )
		.build();
	vanNaar = null;
	ply = Ply.builder()
		.boStelling( boStelling )
		.einde( Nog_niet )
		.vanNaar( vanNaar )
		.zetNummer( 1 )
		.schaak( true )
		.build();
	assertThat( partij.plyToString( ply ), is( "..." ) );
}
@Test
public void testCurPlyToString()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x22 );
	partij.newGame( boStelling );
	partij.zet( vanNaar );
	assertThat( partij.currentPlyToString(), is( "Db2-c3 " ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.schaak( false )
		.aanZet( Wit )
		.build();
	vanNaar = new VanNaar( 0x11, 0x17 );
	partij.newGame( boStelling );
	partij.zet( vanNaar );
	assertThat( partij.currentPlyToString(), is( "Db2-h2+" ) );
}
@Test
public void testResultaatRecord()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x35 )
		.zk( 0x37 )
		.s3( 0x36 )
		.s4( 0x00 )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
	ResultaatRecord resultaatRecord = new ResultaatRecord( "Mat", "" );
	assertThat( partij.getResultaatRecord(), is( resultaatRecord ) );

	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	resultaatRecord = new ResultaatRecord( "Gewonnen", "Mat in 28" );
	assertThat( partij.getResultaatRecord(), is( resultaatRecord ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
	resultaatRecord = new ResultaatRecord( "Verloren", "Mat in 29" );
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
public void testGetPartijReport()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	VooruitRecord vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( true )
		.start( 1 )
		.halverwege( false ) // ??
		.build();
	PartijReport partijReport = PartijReport.builder()
		.erZijnZetten( true )
		.vooruit( vooruitRecord )
		.zetten( new ArrayList<>() )
		.build();
	partij.newGame( boStelling );
	assertThat( partij.getPartijReport(), is( partijReport ) );
	
	VanNaar vanNaar = new VanNaar( 0x11, 0x66 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x77, 0x66 );
	partij.zet( vanNaar );
	ZetDocument zetDocument1 = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "Db2xg7+" )
		.zwartZet( "Kh8xg7 " )
		.build();
	List<ZetDocument> zetten = List.of( zetDocument1 );
	partijReport = PartijReport.builder()
		.erZijnZetten( true )
		.vooruit( vooruitRecord )
		.zetten( zetten )
		.build();
	assertThat( partij.getPartijReport(), is( partijReport ) );

}
@Test
public void testCreateZetDocument()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( 0x11, 0x66 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x77, 0x66 );
	partij.zet( vanNaar );
	ZetDocument zetDocument = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "Db2xg7+" )
		.zwartZet( "Kh8xg7 " )
		.build();
	assertThat( partij.createZetDocument( 0 ), is( zetDocument ) );

	// Begint met een zwarte zet
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
	vanNaar = new VanNaar( 0x77, 0x76 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x11, 0x71 );
	partij.zet( vanNaar );
	ZetDocument zetDocument1 = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "..." )
		.zwartZet( "Kh8-g8 " )
		.build();
	ZetDocument zetDocument2 = ZetDocument.builder()
		.zetNummer( 2 )
		.witZet( "Db2-b8+" )
		.zwartZet( "..." )
		.build();
	assertThat( partij.createZetDocument( 0 ), is( zetDocument1 ) );
	assertThat( partij.createZetDocument( 1 ), is( zetDocument2 ) );

}
@Test
public void testCreateZetten()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( 0x11, 0x66 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x77, 0x66 );
	partij.zet( vanNaar );
	
	List<ZetDocument> zetten = partij.createZetten();
	ZetDocument zetDocument = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "Db2xg7+" )
		.zwartZet( "Kh8xg7 " )
		.build();
	assertThat( zetten.size(), is( 1 ) );
	assertThat( zetten.get( 0 ), is( zetDocument ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
	vanNaar = new VanNaar( 0x77, 0x76 );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( 0x11, 0x71 );
	partij.zet( vanNaar );
	
	zetten = partij.createZetten();
	ZetDocument zetDocument1 = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "..." )
		.zwartZet( "Kh8-g8 " )
		.build();
	ZetDocument zetDocument2 = ZetDocument.builder()
		.zetNummer( 2 )
		.witZet( "Db2-b8+" )
		.zwartZet( "..." )
		.build();

	assertThat( zetten.size(), is( 2 ) );
	assertThat( zetten.get( 0 ), is( zetDocument1 ) );
	assertThat( zetten.get( 1 ), is( zetDocument2 ) );
}
@Test
public void testCreateVooruit()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
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
		.aanZet( Zwart )
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
		.aanZet( Zwart )
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
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
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
		.aanZet( Zwart )
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
	
	List<ZetDocument> zetten = partijReport.getZetten();
	ZetDocument zetDocument1 = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "..." )
		.zwartZet( "Kh8-g8 " )
		.build();
	ZetDocument zetDocument2 = ZetDocument.builder()
		.zetNummer( 2 )
		.witZet( "Db2-b8+" )
		.zwartZet( "Kg8-h7 " )
		.build();

	assertThat( zetten.size(), is( 2 ) );
	assertThat( zetten.get( 0 ), is( zetDocument1 ) );
	assertThat( zetten.get( 1 ), is( zetDocument2 ) );
}
@Test
public void testGetGegenereerdeZetResultaat()
{
	assertThat( partij.getGegenereerdeZetResultaat( Gewonnen ), is ( Verloren ) );
	assertThat( partij.getGegenereerdeZetResultaat( Verloren ), is ( Gewonnen ) );
	assertThat( partij.getGegenereerdeZetResultaat( Remise   ), is ( Remise   ) );
	assertThat( partij.getGegenereerdeZetResultaat( Resultaat.Illegaal ), is ( Resultaat.Illegaal ) );
}
@Test
public void testGegenereerdeZetDocument()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	VanNaar vanNaar = new VanNaar( "b2", "c3" );
	Stuk stukDatZet = config.getStukken().getS3();
	Ply ply = Ply.builder()
		.boStelling( boStellingVan )
		.einde( Nog_niet )
		.vanNaar( vanNaar )
		.zetNummer( 15 )
		.schaak( false )
		.build();
	GegenereerdeZetDocument gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 16 )
		.zet( stukDatZet.getAfko() + "b2-c3 " )
		.resultaat( "Gewonnen" )
		.matInHoeveel( "Mat in 29" )
		.build();
	BoStelling boStellingNaar= partij.vanNaarToStelling( ply, vanNaar );
	assertThat( partij.getGegenereerdeZetDocument( ply, boStellingNaar ), is( gegenereerdeZetDocument ) );

	boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Wit )
		.build();
	vanNaar = new VanNaar( 0x11, 0x66 );
	stukDatZet = config.getStukken().getS3();
	ply = Ply.builder()
		.boStelling( boStellingVan )
		.einde( Nog_niet )
		.vanNaar( vanNaar )
		.zetNummer( 17 )
		.schaak( true )
		.build();
	gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 18 )
		.zet( stukDatZet.getAfko() + "b2xg7+" )
		.resultaat( "Remise" )
		.matInHoeveel( "..." )
		.build();
	boStellingNaar = partij.vanNaarToStelling( ply, vanNaar );
	assertThat( partij.getGegenereerdeZetDocument( ply, boStellingNaar ), is( gegenereerdeZetDocument ) );
}
@Test
public void testGeGegenereerdeZetten()
{
	BoStelling boStellingVan = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStellingVan );
	List<GegenereerdeZetDocument> zetten = partij.getGegenereerdeZetten();
	assertThat( zetten.size(), is( 2 ) );
	GegenereerdeZetDocument gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 1 )
		.zet( "Kh8-g8 " )
		.resultaat( "Verloren" )
		.matInHoeveel( "Mat in 29" )
		.build();
	assertThat( zetten.get( 0 ), is( gegenereerdeZetDocument ) );
	gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 2 )
		.zet( "Kh8-h7 " )
		.resultaat( "Verloren" )
		.matInHoeveel( "Mat in 29" )
		.build();
	assertThat( zetten.get( 1 ), is( gegenereerdeZetDocument ) );
}
@Test
public void testGetStand()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( Zwart )
		.build();
	BoStelling newBoStelling = partij.newGame( boStelling );
	assertThat( partij.getStand(), is( newBoStelling ) );
}
@Test
public void testGetStukInfo()
{
	// Lamaar
}
/**
 * Dit is voorbeeld a) uit modula-2. Zie het commentaar in Partij
 */
@Test
public void testModula2Partij_1()
{
	BoStelling boStelling1 = BoStelling.alfaBuilder()
		.wk( "e2" )
		.zk( "f6" )
		.s3( "h1" )
		.s4( "a1" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 10 )
		.schaak( false )
		.build();
	BoStelling boStelling2 = BoStelling.alfaBuilder()
		.wk( "e3" )
		.zk( "f6" )
		.s3( "h1" )
		.s4( "a1" )
		.aanZet( Zwart )
		.resultaat( Gewonnen )
		.aantalZetten( 15 )
		.schaak( false )
		.build();
	BoStelling boStelling3 = BoStelling.alfaBuilder()
		.wk( "e3" )
		.zk( "f6" )
		.s3( "h1" )
		.s4( "a8" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 8 )
		.schaak( false )
		.build();
	BoStelling boStelling4 = BoStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "f6" )
		.s3( "h1" )
		.s4( "a8" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	partij.newGame( boStelling1 );
	VanNaar vanNaar1 = VanNaar.alfaBuilder()
		.van( "e2" )
		.naar( "e3" )
		.build();
	partij.zet( vanNaar1 );
	VanNaar vanNaar2 = VanNaar.alfaBuilder()
		.van( "a1" )
		.naar( "a8" )
		.build();
	partij.zet( vanNaar2 );
	VanNaar vanNaar3 = VanNaar.alfaBuilder()
		.van( "e3" )
		.naar( "e4" )
		.build();
	partij.zet( vanNaar3 );
	VanNaar vanNaar4 = VanNaar.alfaBuilder()
		.van( "f6" )
		.naar( "g6" )
		.build();
	partij.zet( vanNaar4 );

	Ply ply1 = Ply.builder()
		.boStelling( boStelling1 )
		.vanNaar( vanNaar1 )
		.zetNummer( 1 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	Ply ply2 = Ply.builder()
		.boStelling( boStelling2 )
		.vanNaar( vanNaar2 )
		.zetNummer( 1 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	Ply ply3 = Ply.builder()
		.boStelling( boStelling3 )
		.vanNaar( vanNaar3 )
		.zetNummer( 2 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	Ply ply4 = Ply.builder()
		.boStelling( boStelling4 )
		.vanNaar( vanNaar4 )
		.zetNummer( 2 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	assertThat( partij.getPlies().getPly( 0 ), is( ply1 ) );
	assertThat( partij.getPlies().getPly( 1 ), is( ply2 ) );
	assertThat( partij.getPlies().getPly( 2 ), is( ply3 ) );
	assertThat( partij.getPlies().getPly( 3 ), is( ply4 ) );

	List<ZetDocument> zetten = partij.createZetten();
	ZetDocument zetDocument1 = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "Ke2-e3 " )
		.zwartZet( "Ta1-a8 " )
		.build();
	ZetDocument zetDocument2 = ZetDocument.builder()
		.zetNummer( 2 )
		.witZet( "Ke3-e4 " )
		.zwartZet( "Kf6-g6 " )
		.build();

	assertThat( zetten.size(), is( 2 ) );
	assertThat( zetten.get( 0 ), is( zetDocument1 ) );
	assertThat( zetten.get( 1 ), is( zetDocument2 ) );
}
/**
 * Dit is voorbeeld b) uit modula-2. Zie het commentaar in Partij
 */
@Test
public void testModula2Partij_b()
{
	BoStelling boStelling1 = BoStelling.alfaBuilder()
		.wk( "e3" )
		.zk( "f6" )
		.s3( "h1" )
		.s4( "a1" )
		.aanZet( Zwart )
		.resultaat( Gewonnen )
		.aantalZetten( 15 )
		.schaak( false )
		.build();
	BoStelling boStelling2 = BoStelling.alfaBuilder()
		.wk( "e3" )
		.zk( "f6" )
		.s3( "h1" )
		.s4( "a8" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 8 )
		.schaak( false )
		.build();
	BoStelling boStelling3 = BoStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "f6" )
		.s3( "h1" )
		.s4( "a8" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	BoStelling boStelling4 = BoStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "g6" )
		.s3( "h1" )
		.s4( "a8" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 18 )
		.schaak( false )
		.build();
	partij.newGame( boStelling1 );
	VanNaar vanNaar1 = VanNaar.alfaBuilder()
		.van( "a1" )
		.naar( "a8" )
		.build();
	partij.zet( vanNaar1 );
	VanNaar vanNaar2 = VanNaar.alfaBuilder()
		.van( "e3" )
		.naar( "e4" )
		.build();
	partij.zet( vanNaar2 );
	VanNaar vanNaar3 = VanNaar.alfaBuilder()
		.van( "f6" )
		.naar( "g6" )
		.build();
	partij.zet( vanNaar3 );
	VanNaar vanNaar4 = null;

	Ply ply1 = Ply.builder()
		.boStelling( boStelling1 )
		.vanNaar( vanNaar1 )
		.zetNummer( 1 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	Ply ply2 = Ply.builder()
		.boStelling( boStelling2 )
		.vanNaar( vanNaar2 )
		.zetNummer( 2 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	Ply ply3 = Ply.builder()
		.boStelling( boStelling3 )
		.vanNaar( vanNaar3 )
		.zetNummer( 2 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	Ply ply4 = Ply.builder()
		.boStelling( boStelling4 )
		.vanNaar( vanNaar4 )
		.zetNummer( 3 )
		.einde( Einde.Nog_niet )
		.schaak( false )
		.build();
	assertThat( partij.getPlies().getPly( 0 ), is( ply1 ) );
	assertThat( partij.getPlies().getPly( 1 ), is( ply2 ) );
	assertThat( partij.getPlies().getPly( 2 ), is( ply3 ) );
	assertThat( partij.getPlies().getPly( 3 ), is( ply4 ) );

	List<ZetDocument> zetten = partij.createZetten();
	ZetDocument zetDocument1 = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "..." )
		.zwartZet( "Ta1-a8 " )
		.build();
	ZetDocument zetDocument2 = ZetDocument.builder()
		.zetNummer( 2 )
		.witZet( "Ke3-e4 " )
		.zwartZet( "Kf6-g6 " )
		.build();

	assertThat( zetten.size(), is( 2 ) );
	assertThat( zetten.get( 0 ), is( zetDocument1 ) );
	assertThat( zetten.get( 1 ), is( zetDocument2 ) );
}

}