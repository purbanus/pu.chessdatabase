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

import lombok.Data;

@Data
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
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( true ) );
}
@Test
public void testGetEinde()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.aanZet( Wit )
		.resultaat( Resultaat.Illegaal )
		.build();
	assertThat( partij.getEinde( boStelling ), is( Einde.Illegaal ) );
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.resultaat( Remise )
		.aanZet( Wit )
		.build();
	assertThat( partij.getEinde( boStelling ), is( Nog_niet ) );
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "c1" )
		.aanZet( Wit )
		.schaak(  true )
		.build();
	assertThat( partij.getEinde( boStelling ), is( Mat ) );
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "a3" )
		.s3( "a1" )
		.s4( "b3" )
		.aanZet( Wit )
		.schaak(  false )
		.build();
	assertThat( partij.getEinde( boStelling ), is( Pat ) );
}
@Test
public void testNewGame()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.schaak( false )
		.build();
	BoStelling newBoStelling = partij.newGame( startStelling );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	assertThat( partij.getPlies().getStartStelling(), is( newBoStelling ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( -1 ) );
	assertThat( partij.getEinde( newBoStelling ), is( Nog_niet ) );

	final BoStelling illegaleStartStelling = BoStelling.alfaBuilder()
		.wk( "f4" )
		.zk( "h4" )
		.s3( "g4" )
		.s4( "a1" )
		.aanZet( Wit )
		.build();
	assertThrows( RuntimeException.class, () -> partij.newGame( illegaleStartStelling ) );
}
@Test
public void testIsBegonnen()
{
	assertThat( partij.isBegonnen(), is( false ) );
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	partij.newGame( startStelling );
	assertThat( partij.isBegonnen(), is( true ) );
}
@Test
public void testStellingToVanNaar()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	BoStelling boStellingNaar = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "g7" )
		.s4( "h8" )
		.s5( "a1" )
		.aanZet( Zwart )
		.build();
assertThat( partij.stellingToVanNaar( boStellingVan, boStellingNaar ), is( new VanNaar( "Db2xg7+" ) ) );
}
@Test
public void testVanCurrentStandNaarToStelling()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	boStellingVan = partij.newGame( boStellingVan );
	BoStelling boStellingNaar = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "g7" )
		.s4( "h8" )
		.s5( "a1" )
		.aanZet( Zwart )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.schaak( true )
		.build();
	assertThat( partij.vanCurrentStandNaarToStelling( new VanNaar( "Db2xg7" ) ), is( boStellingNaar ) );
}
@Test
public void testIsLegalMove()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "b3" ) //0x12
		.s3( "a1" )
		.s4( "c2" ) //0x21
		.s5( "a1" )
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
@Test
public void testZetNaarBegin()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
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
		.s5( "b2" )
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
		.s5( "a1" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.schaak(  false )
		.build();
	assertThat( actualNaarBeginStelling, is( expectedNaarBeginStelling ) );
	
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( -1 ) );
}
@Test
public void testZetTerug()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
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
		.s5( "b2" )
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
		.s5( "a1" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 28 )
		.schaak(  false )
		.build();
	assertThat( actualTerugStelling, is( expectedTerugStelling ) );
	
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 1 ) );
}
@Test
public void testZetVooruit()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();

	// Het is niet zo dat we op de laatste ply zitten
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
		.s5( "a1" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 28 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( actualVooruitStelling ) );
	assertThat( actualVooruitStelling, is( expectedVooruitStelling ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 0 ) );

	// We zitten op de laatste ply. Als het geen einde is bedenken we een nieuwe zet
	partij.newGame( startStelling );
	partij.zet( "Db2-e5" );
	partij.zet( "Kh8-h7" );
	partij.zet( "Ka1-b2" );
	actualVooruitStelling = partij.zetVooruit();
	expectedVooruitStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "g6" )
		.s3( "e5" )
		.s4( "g7" )
		.s5( "b2" )
		.aanZet( Wit )
		.resultaat( Gewonnen)
		.aantalZetten( 27 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( actualVooruitStelling ) );
	assertThat( actualVooruitStelling, is( expectedVooruitStelling ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 3 ) );
	
	// Er zijn nog geen zetten gedaan, dus bedenken we er een
	partij.newGame( startStelling );
	actualVooruitStelling = partij.zetVooruit();
	expectedVooruitStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "e5" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.resultaat( Verloren)
		.aantalZetten( 28 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( actualVooruitStelling ) );
	assertThat( actualVooruitStelling, is( expectedVooruitStelling ) );
	
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 0 ) );
}
@Test
public void testZetNaarEinde()
{
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
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
		.s5( "b2" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 27 )
		.schaak(  false )
		.build();
	assertThat( partij.getStand(), is( actualVooruitStelling ) );
	assertThat( actualVooruitStelling, is( expectedVooruitStelling ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 2 ) );
}
@Test
public void testBedenk()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	BoStelling boStellingNaar = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "e5" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 28 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x44 );
	BoStelling newBoStelling = partij.newGame( boStellingVan );
	assertThat( partij.vanCurrentStandNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.bedenk(), is( boStellingNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 0 ) );
	assertThat( partij.getPlies().getLastPlyNummer(), is( 0 ) );
	Ply firstPly = partij.getPlies().getFirstPly();
	assertThat( firstPly.getVanNaar(), is( vanNaar ) );
	assertThat( firstPly.getBoStelling(), is( boStellingNaar ) );
	assertThat( firstPly.getEinde(), is( Nog_niet ) );
	assertThat( firstPly.getPlyNummer(), is( 0 ) );
}
@Test
public void testCheckPartijVoorZet()
{
	// De pratij moet begonnen zijn
	BoStelling startStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	assertThrows( RuntimeException.class, () -> partij.checkPartijVoorZet( startStelling ) );
	
	// Het mag niet mat zijn
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "f7" )
		.zk( "h8" )
		.s3( "h2" )
		.s4( "h8" )
		.s5( "f7" )
		.aanZet( Zwart )
		.build();
	assertThrows( RuntimeException.class, () -> partij.checkPartijVoorZet( startStelling ) );

	// Het mag niet pat zijn
	boStelling = BoStelling.alfaBuilder()
		.wk( "f7" )
		.zk( "h8" )
		.s3( "h7" )
		.s4( "h8" )
		.s5( "f7" )
		.aanZet( Zwart )
		.build();
	assertThrows( RuntimeException.class, () -> partij.checkPartijVoorZet( startStelling ) );

	// De stelling mag niet null zijn
	boStelling = null;
	assertThrows( RuntimeException.class, () -> partij.checkPartijVoorZet( startStelling ) );
}
@Test
public void testZetStelling()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.build();
	BoStelling boStellingNaar = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "g8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( "h8-g8" );
	BoStelling newBoStelling = partij.newGame( boStellingVan );
	assertThat( partij.vanCurrentStandNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zetStelling( boStellingNaar ), is( boStellingNaar ) );
	Ply firstPly = partij.getPlies().getFirstPly();
	assertThat( firstPly.getVanNaar(), is( vanNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 0 ) );
	assertThat( partij.getPlies().getLastPlyNummer(), is( 0 ) );
}
@Test
public void testZet()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
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
		.s5( "a1" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	assertThat( partij.isBegonnen(), is ( false ) );
	VanNaar vanNaar = new VanNaar( "b2", "c3" );

	// De partij moet begonnen zijn
	assertThrows( RuntimeException.class, () -> partij.zet( vanNaar ) );

	BoStelling newBoStelling = partij.newGame( boStellingVan );
	assertThat( partij.getPlies().isBegonnen(), is ( true ) );
	assertThat( partij.getPlies().getStartStelling(), is( newBoStelling ) );
	assertThat( partij.vanCurrentStandNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zet( vanNaar ), is( boStellingNaar ) );
	Ply firstPly = partij.getPlies().getPly( 0 );
	assertThat( firstPly.getVanNaar(), is(vanNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 0 ) );
	assertThat( partij.getPlies().getLastPlyNummer(), is( 0 ) );
	assertThat( firstPly.getBoStelling(), is( boStellingNaar ) );
	assertThat( firstPly.getEinde(), is( Nog_niet ) );
	assertThat( firstPly.getPlyNummer(), is( 0 ) );
}
@Test
public void testZetMetClearPliesVoorZet()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
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
		.s5( "b2" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 26 )
		.schaak( true )
		.build();
	assertThat( actualEindeStelling, is( expectedEindeStelling ) );
	
	partij.zetNaarBegin();
	for ( int x = 0; x <= 4; x++ )
	{
		assertThat( partij.getPlies().getPly( x ), is( notNullValue() ) );
	}
	assertThat( partij.getPlies().getSize(), is( 5 ) );
	partij.zet( "Db2-c3" );
	assertThat( partij.getPlies().getPly( 0 ), is( notNullValue() ) );
	assertThat( partij.getPlies().getSize(), is( 1 ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 0 ) );
	assertThat( partij.getPlies().getLastPlyNummer(), is( 0 ) );
}
@Test
public void testZetMetZwart()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.build();
	BoStelling boStellingNaar = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "g8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	assertThat( partij.getPlies().isBegonnen(), is ( false ) );

	VanNaar vanNaar = new VanNaar( "h8-g8" );
	BoStelling newBoStelling = partij.newGame( boStellingVan );
	assertThat( partij.getPlies().isBegonnen(), is ( true ) );
	assertThat( partij.vanCurrentStandNaarToStelling( vanNaar ), is( boStellingNaar ) );
	
	assertThat( partij.zet( vanNaar ), is( boStellingNaar ) );
	Ply firstPly = partij.getPlies().getFirstPly();
	assertThat( firstPly.getVanNaar(), is( vanNaar ) );
	assertThat( partij.getPlies().getCurrentPlyNummer(), is( 0 ) );
	assertThat( partij.getPlies().getLastPlyNummer(), is( 0 ) );
	assertThat( firstPly.getBoStelling(), is( boStellingNaar ) );
	assertThat( firstPly.getEinde(), is( Nog_niet ) );
	assertThat( firstPly.getPlyNummer(), is( 0 ) );
}
@Test
public void testIsSlagZet()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "g8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	VanNaar vanNaar = new VanNaar( "b2-g7" );
	assertThat( partij.isSlagZet( boStelling, vanNaar.getNaar() ), is( true ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "g8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	vanNaar = new VanNaar( "b2-b8" );
	assertThat( partij.isSlagZet( boStelling, vanNaar.getNaar() ), is( false ) );
}
@Test
public void testWatStaatErOp()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
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
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.schaak( false )
		.build();
	partij.newGame( boStelling );
	partij.zet( "b2-c3" );
	Ply ply = partij.getPlies().getPly(  0 );
	assertThat( partij.plyToString( ply ), is( "Db2-c3 " ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.schaak( false )
		.build();
	partij.newGame( boStelling );
	partij.zet( "b2-g7" );
	ply = partij.getPlies().getPly(  0 );
	assertThat( partij.plyToString( ply ), is( "Db2xg7+" ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.schaak( false )
		.build();
	partij.newGame( boStelling );
	partij.zet( "b2-h2" );
	ply = partij.getPlies().getPly(  0 );
	assertThat( partij.plyToString( ply ), is( "Db2-h2+" ) );
}
@Test
public void testCurrentPlyToString()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	VanNaar vanNaar = new VanNaar( 0x11, 0x22 );
	partij.newGame( boStelling );
	partij.zet( vanNaar );
	assertThat( partij.currentPlyToString(), is( "Db2-c3 " ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.schaak( false )
		.aanZet( Wit )
		.build();
	vanNaar = new VanNaar( 0x11, 0x17 );
	partij.newGame( boStelling );
	partij.zet( vanNaar );
	assertThat( partij.currentPlyToString(), is( "Db2-h2+" ) );
}
@Test
public void testGetResultaatRecord()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "f4" )
		.zk( "h4" )
		.s3( "g4" )
		.s4( "a1" )
		.s5( "f4" )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
	ResultaatRecord resultaatRecord = new ResultaatRecord( "Mat", "" );
	assertThat( partij.getResultaatRecord(), is( resultaatRecord ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	resultaatRecord = new ResultaatRecord( "Gewonnen", "Mat in 28" );
	assertThat( partij.getResultaatRecord(), is( resultaatRecord ) );
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
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
public void testCreateZetDocument()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( "b2-g7" );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( "h8-g7" );
	partij.zet( vanNaar );
	ZetDocument zetDocument = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "Db2xg7+" )
		.zwartZet( "Kh8xg7 " )
		.build();
	assertThat( partij.createZetDocument( 0 ), is( zetDocument ) );

	// Begint met een zwarte zet
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
	vanNaar = new VanNaar( "h8-g8" );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( "b2-b8" );
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
	// Je mag createZetDocument niet meer aanroepen met een ply waarin Zwart aan zet is
	/* Exception exception = */ assertThrows( RuntimeException.class, () -> partij.createZetDocument( 0 ) );
	assertThat( partij.createZetDocument( 1 ), is( zetDocument2 ) );

}
@Test
public void testCreateZetten()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	partij.newGame( boStelling );
	VanNaar vanNaar = new VanNaar( "b2-g7" );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( "h8-g7" );
	partij.zet( vanNaar );
	
	List<ZetDocument> zetten = partij.createZetten();
	ZetDocument zetDocument = ZetDocument.builder()
		.zetNummer( 1 )
		.witZet( "Db2xg7+" )
		.zwartZet( "Kh8xg7 " )
		.build();
	assertThat( zetten.size(), is( 1 ) );
	assertThat( zetten.get( 0 ), is( zetDocument ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.build();
	partij.newGame( boStelling );
	vanNaar = new VanNaar( "h8-g8" );
	partij.zet( vanNaar );
	vanNaar = new VanNaar( "b2-b8" );
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
public void testGegenereerdeZetDocument()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.schaak( false )
		.build();
	VanNaar vanNaar = new VanNaar( "b2-c3" );
	Stuk stukDatZet = config.getStukken().getS3();
	GegenereerdeZetDocument gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 16 )
		.zet( stukDatZet.getAfko() + "b2-c3 " )
		.resultaat( "Gewonnen" )
		.matInHoeveel( "Mat in 29" )
		.build();
	partij.newGame( boStellingVan );
	partij.zet( vanNaar );

	BoStelling boStellingNaar = partij.vanNaarToStelling( boStellingVan, vanNaar );
	assertThat( partij.getStand(), is( boStellingNaar ) );
	
	Ply ply = partij.getCurrentPly();
	assertThat( partij.getGegenereerdeZetDocument( ply, boStellingNaar, 16 ), is( gegenereerdeZetDocument ) );

	boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.schaak( true )
		.build();
	vanNaar = new VanNaar( 0x11, 0x66 );
	stukDatZet = config.getStukken().getS3();
	gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 18 )
		.zet( stukDatZet.getAfko() + "b2xg7+" )
		.resultaat( "Remise" )
		.matInHoeveel( "..." )
		.build();
	partij.newGame( boStellingVan );
	partij.zet( vanNaar );

	boStellingNaar = partij.vanNaarToStelling( boStellingVan, vanNaar );
	assertThat( partij.getStand(), is( boStellingNaar ) );
	
	ply = partij.getCurrentPly();
	assertThat( partij.getGegenereerdeZetDocument( ply, boStellingNaar, 18 ), is( gegenereerdeZetDocument ) );
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
public void testGetGegegenereerdeZetten()
{
	BoStelling boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	partij.newGame( boStellingVan );
	partij.zet( "Db2-e5" );
	
	List<GegenereerdeZetDocument> zetten = partij.getGegenereerdeZetten();
	assertThat( zetten.size(), is( 2 ) );
	GegenereerdeZetDocument gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 1 )
		.zet( "Kh8-g8 " )
		.resultaat( "Verloren" )
		.matInHoeveel( "Mat in 27" )
		.build();
	assertThat( zetten.get( 0 ), is( gegenereerdeZetDocument ) );
	gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 2 )
		.zet( "Kh8-h7 " )
		.resultaat( "Verloren" )
		.matInHoeveel( "Mat in 27" )
		.build();
	assertThat( zetten.get( 1 ), is( gegenereerdeZetDocument ) );
	
	boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	partij.newGame( boStellingVan );
	partij.zet( "Db2-B8+" );
	partij.zet( "Kh8-H7" );
	partij.zet( "Ka1-b2" );
	partij.zetTerug();
	
	zetten = partij.getGegenereerdeZetten();
	assertThat( zetten.size(), is( 24 ) );
	gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 1 )
		.zet( "Ka1-b2 " )
		.resultaat( "Gewonnen" )
		.matInHoeveel( "Mat in 27" )
		.build();
	assertThat( zetten.get( 0 ), is( gegenereerdeZetDocument ) );
	gegenereerdeZetDocument = GegenereerdeZetDocument.builder()
		.zetNummer( 2 )
		.zet( "Db8-e5 " )
		.resultaat( "Gewonnen" )
		.matInHoeveel( "Mat in 28" )
		.build();
	assertThat( zetten.get( 1 ), is( gegenereerdeZetDocument ) );
}
@Test
public void testGetStand()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.build();
	BoStelling newBoStelling = partij.newGame( boStelling );
	assertThat( partij.getStand(), is( newBoStelling ) );
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
		.s5( "e2" )
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
		.s5( "e3" )
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
		.s5( "e3" )
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
		.s5( "e4" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 30 )
		.schaak( false )
		.build();
	BoStelling boStelling5 = BoStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "g6" )
		.s3( "h1" )
		.s4( "a8" )
		.s5( "e4" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 18 )
		.schaak( false )
		.build();
	partij.newGame( boStelling1 );
	VanNaar vanNaar1 = new VanNaar( "e2-e3" );
	partij.zet( vanNaar1 );
	VanNaar vanNaar2 = new VanNaar( "a1-a8" );
	partij.zet( vanNaar2 );
	VanNaar vanNaar3 = new VanNaar( "e3-e4" );
	partij.zet( vanNaar3 );
	VanNaar vanNaar4 = new VanNaar( "f6-g6" );
	partij.zet( vanNaar4 );

	Ply ply1 = Ply.builder()
		//.id is voor JPA
		.plies( getPartij().getPlies() )
		.einde( Einde.Nog_niet )
		.plyNummer( 0 )
		.vanNaar( vanNaar1 )
		.boStelling( boStelling2 )
		.build();
	Ply ply2 = Ply.builder()
		//.id is voor JPA
		.plies( getPartij().getPlies() )
		.einde( Einde.Nog_niet )
		.plyNummer( 1 )
		.vanNaar( vanNaar2 )		
		.boStelling( boStelling3 )
		.build();
	Ply ply3 = Ply.builder()
		//.id is voor JPA
		.plies( getPartij().getPlies() )
		.einde( Einde.Nog_niet )
		.plyNummer( 2 )
		.vanNaar( vanNaar3 )
		.boStelling( boStelling4 )
		.build();
	Ply ply4 = Ply.builder()
		//.id is voor JPA
		.plies( getPartij().getPlies() )
		.einde( Einde.Nog_niet )
		.plyNummer( 3 )
		.vanNaar( vanNaar4 )
		.boStelling( boStelling5 )
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
		.s5( "e3" )
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
		.s5( "e3" )
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
		.s5( "e4" )
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
		.s5( "e4" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 18 )
		.schaak( false )
		.build();
	partij.newGame( boStelling1 );
	VanNaar vanNaar1 = new VanNaar( "a1-a8" );
	partij.zet( vanNaar1 );
	VanNaar vanNaar2 = new VanNaar( "e3-e4" );
	partij.zet( vanNaar2 );
	VanNaar vanNaar3 = new VanNaar( "f6-g6");
	partij.zet( vanNaar3 );

	Ply ply1 = Ply.builder()
		//.id is voor JPA
		.plies( getPartij().getPlies() )
		.einde( Einde.Nog_niet )
		.plyNummer( 0 )
		.vanNaar( vanNaar1 )
		.boStelling( boStelling2 )
		.build();
	Ply ply2 = Ply.builder()
		//.id is voor JPA
		.plies( getPartij().getPlies() )
		.einde( Einde.Nog_niet )
		.plyNummer( 1 )
		.vanNaar( vanNaar2 )
		.boStelling( boStelling3 )
		.build();
	Ply ply3 = Ply.builder()
		//.id is voor JPA
		.plies( getPartij().getPlies() )
		.einde( Einde.Nog_niet )
		.plyNummer( 2 )
		.vanNaar( vanNaar3 )
		.boStelling( boStelling4)
		.build();
	assertThat( partij.getPlies().getPly( 0 ), is( ply1 ) );
	assertThat( partij.getPlies().getPly( 1 ), is( ply2 ) );
	assertThat( partij.getPlies().getPly( 2 ), is( ply3 ) );

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
	// Je mag 
	assertThat( zetten.get( 0 ), is( zetDocument1 ) );
	assertThat( zetten.get( 1 ), is( zetDocument2 ) );
}

}