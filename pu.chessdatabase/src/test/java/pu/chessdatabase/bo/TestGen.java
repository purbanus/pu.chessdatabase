package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;

@SpringBootTest
public class TestGen
{
@Autowired private Gen gen;
@Autowired private Dbs dbs;

@Test
public void testVeldToBitSetAndBuitenBord()
{
	assertThat( gen.veldToBitSetAndBuitenBord( 0 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 7 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 8 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x10 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x17 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x18 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x20 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x27 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x28 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x70 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x77 ), is( gen.NUL ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x78 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( -1 ), is( gen.bitSetOfInt( 0x88 ) ) ); // 136
}
@Test
public void testVulStukTabel()
{
	gen.vulStukTabel();
	Stuk stuk = gen.stukTabel[1];
	assertThat( stuk.getSoort(), is( StukType.KONING ) );
	assertThat( stuk.getKleur(), is( WIT ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( gen.KRICHTING ) );
	assertThat( stuk.getAantalRichtingen(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	
	stuk = gen.stukTabel[2];
	assertThat( stuk.getSoort(), is( StukType.KONING ) );
	assertThat( stuk.getKleur(), is( ZWART ) );
	assertThat( stuk.getKoningsNummer(), is( 2 ) );
	assertThat( stuk.getRichtingen(), is( gen.KRICHTING ) );
	assertThat( stuk.getAantalRichtingen(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );

	stuk = gen.stukTabel[3];
	assertThat( stuk.getSoort(), is( StukType.DAME ) );
	assertThat( stuk.getKleur(), is( WIT ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( gen.KRICHTING ) );
	assertThat( stuk.getAantalRichtingen(), is( 8 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "D" ) );
	
	stuk = gen.stukTabel[4];
	assertThat( stuk.getSoort(), is( StukType.TOREN ) );
	assertThat( stuk.getKleur(), is( ZWART ) );
	assertThat( stuk.getKoningsNummer(), is( 2 ) );
	assertThat( stuk.getRichtingen(), is( gen.TRICHTING ) );
	assertThat( stuk.getAantalRichtingen(), is( 4 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "T" ) );

}
@Test
public void testIsGeomIllegaal()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	assertThat( gen.isGeomIllegaal( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 5 )
		.s3( 7 )
		.s4( 8 )
		.build();
	assertThat( gen.isGeomIllegaal( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 7 )
		.build();
	assertThat( gen.isGeomIllegaal( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 5 )
		.s4( 7 )
		.build();
	assertThat( gen.isGeomIllegaal( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 5 )
		.build();
	assertThat( gen.isGeomIllegaal( stelling ), is( true ) );
}
@Test
public void testIsKKSchaak()
{
	//Zie Gen.java voor een (paar) coordinaten, na Notatie
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x00 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x10 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x21 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( true ) );
}

@Test
public void testIsSchaakDoorStuk()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	Bord bord = new Bord( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x33, bord ), is( false ) );

	// T links
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x10, bord ), is( true ) );

	// T uiterst rechts
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x17 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x17, bord ), is( true ) );

	// T nog steeds uiterst rechts, maar D ertussen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x15 )
		.s4( 0x17 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x17, bord ), is( false ) );

	// Check of Z schaak staat
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x20 )
		.s4( 0x77 )
		.aanZet( WIT )
		.build();
	bord = new Bord( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[3], 0x27, 0x20, bord ), is( true ) );
}
@Test
public void testCheckSchaakDoorStuk()
{
	// Check aStukVeld == aStelling.getWK(), d.w.z. het witte stuk is geslagen
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x11 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	Bord bord = new Bord( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[3], 0x11, 0x11, bord ), is( false ) );

	// Check aStukVeld == aStelling.getZK(), d.w.z. het zwarte stuk is geslagen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x27 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[4], 0x27, 0x27, bord ), is( false ) );

	// Check dat het stuk aan zet is
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[4], 0x11, 0x33, bord ), is( false ) );

	// T links
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( WIT )
		.build();
	bord = new Bord( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[4], 0x11, 0x10, bord ), is( true ) );
}
@Test
public void testIsSchaak()
{
	// Check aStukVeld == aStelling.getWK(), d.w.z. het stuk is geslagen door wit
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x11 )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );

	// Check aStukVeld == aStelling.getZK(), d.w.z. het stuk is geslagen door zwart
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x27 )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );

	// Check dat het stuk aan zet is
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );

	// T links geeft schaak
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( WIT )
		.build();
	assertThat( gen.isSchaak( stelling ), is( true ) );
}
@Test
public void testAddZet()
{
	dbs.name( "Pipo" );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	GegenereerdeZetten genZRec;
	BoStelling resultaatStelling;
	
	// Gewone zet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	genZRec = new GegenereerdeZetten();
	gen.addZet( stelling, 3, 0x77, ZetSoort.GEWOON, 0x11, 0x76, genZRec );
	assertThat( genZRec.getAantal(), is( 1 ) );
	resultaatStelling = genZRec.getStellingen().get( 0 );
	assertThat( resultaatStelling.getWk(), is( 0x11 ) );
	assertThat( resultaatStelling.getZk(), is( 0x27 ) );
	assertThat( resultaatStelling.getS3(), is( 0x77 ) );
	assertThat( resultaatStelling.getS4(), is( 0x33 ) );
	assertThat( resultaatStelling.getAanZet(), is( WIT ) );

	// Slagzet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( WIT )
		.build();
	genZRec = new GegenereerdeZetten();
	gen.addZet( stelling, 4, 0x76, ZetSoort.SLAGZET, 0x11, 0x76, genZRec );
	assertThat( genZRec.getAantal(), is( 1 ) );
	resultaatStelling = genZRec.getStellingen().get( 0 );
	assertThat( resultaatStelling.getWk(), is( 0x11 ) );
	assertThat( resultaatStelling.getZk(), is( 0x27 ) );
	assertThat( resultaatStelling.getS3(), is( 0x11 ) );
	assertThat( resultaatStelling.getS4(), is( 0x76 ) );
	assertThat( resultaatStelling.getAanZet(), is( ZWART ) );

	// Geslagen stukken meeverplaatsen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x11 )
		.s4( 0x33 )
		.aanZet( WIT )
		.build();
	genZRec = new GegenereerdeZetten();
	gen.addZet( stelling, 1, 0x12, ZetSoort.GEWOON, 0x11, 0x11, genZRec );
	assertThat( genZRec.getAantal(), is( 1 ) );
	resultaatStelling = genZRec.getStellingen().get( 0 );
	assertThat( resultaatStelling.getWk(), is( 0x12 ) );
	assertThat( resultaatStelling.getZk(), is( 0x27 ) );
	assertThat( resultaatStelling.getS3(), is( 0x12 ) );
	assertThat( resultaatStelling.getS4(), is( 0x33 ) );
	assertThat( resultaatStelling.getAanZet(), is( ZWART ) );

	dbs.delete();
}

@Test
public void testGenZetPerStuk()
{
	dbs.name( "Pipo" );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling boStelling;
	
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	Bord bord = new Bord( boStelling );
	GegenereerdeZetten genZRec = gen.genereerZettenPerStuk( boStelling, 4, 0x27, 0x33, bord );
	assertThat( genZRec.getAantal(), is( 14 ) );
	assertThat( genZRec.getStellingen().get(  0 ).getS4(), is( 0x34 ) );
	assertThat( genZRec.getStellingen().get(  0 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  1 ).getS4(), is( 0x35 ) );
	assertThat( genZRec.getStellingen().get(  1 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  2 ).getS4(), is( 0x36 ) );
	assertThat( genZRec.getStellingen().get(  2 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  3 ).getS4(), is( 0x37 ) );
	assertThat( genZRec.getStellingen().get(  3 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  4 ).getS4(), is( 0x43 ) );
	assertThat( genZRec.getStellingen().get(  4 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  5 ).getS4(), is( 0x53 ) );
	assertThat( genZRec.getStellingen().get(  5 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  6 ).getS4(), is( 0x63 ) );
	assertThat( genZRec.getStellingen().get(  6 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  7 ).getS4(), is( 0x73 ) );
	assertThat( genZRec.getStellingen().get(  7 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  8 ).getS4(), is( 0x32 ) );
	assertThat( genZRec.getStellingen().get(  8 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get(  9 ).getS4(), is( 0x31 ) );
	assertThat( genZRec.getStellingen().get(  9 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get( 10 ).getS4(), is( 0x30 ) );
	assertThat( genZRec.getStellingen().get( 10 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get( 11 ).getS4(), is( 0x23 ) );
	assertThat( genZRec.getStellingen().get( 11 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get( 12 ).getS4(), is( 0x13 ) );
	assertThat( genZRec.getStellingen().get( 12 ).getAanZet(), is( WIT ) );
	assertThat( genZRec.getStellingen().get( 13 ).getS4(), is( 0x03 ) );
	assertThat( genZRec.getStellingen().get( 13 ).getAanZet(), is( WIT ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x77 )
		.aanZet( ZWART )
		.build();
	//System.out.println( "In testGenZetPerStuk" );
	//gen.printBord();
	bord = new Bord( boStelling );
	//gen.printBord();
	genZRec = gen.genereerZettenPerStuk( boStelling, 4, 0x27, 0x77, bord );
	assertThat( genZRec.getAantal(), is( 5 ) );
	assertThat( genZRec.getStellingen().get(  0 ).getS4(), is( 0x76 ) );
	assertThat( genZRec.getStellingen().get(  0 ).getS3(), is( 0x11 ) );
	assertThat( genZRec.getStellingen().get(  1 ).getS4(), is( 0x67 ) );
	assertThat( genZRec.getStellingen().get(  2 ).getS4(), is( 0x57 ) );
	assertThat( genZRec.getStellingen().get(  3 ).getS4(), is( 0x47 ) );
	assertThat( genZRec.getStellingen().get(  4 ).getS4(), is( 0x37 ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x06 )
		.s4( 0x04 )
		.aanZet( WIT)
		.schaak( true )
		.resultaat( ResultaatType.REMISE )
		.aantalZetten( 0 )
		.build();
	bord = new Bord( boStelling );
	//gen.printBord();
	genZRec = gen.genereerZettenPerStuk( boStelling, 1, 0x02, 0x02, bord );
	assertThat( genZRec.getAantal(), is( 5 ) );

}
@Test
public void testGenZet()
{
	dbs.name( "Pipo" );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	GegenereerdeZetten genZRec;
	
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	genZRec = gen.genereerZetten( stelling );
	assertThat( genZRec.getAantal(), is( 19 ) );

	assertThat( genZRec.getStellingen().get(  0 ).getZk(), is( 0x37 ) );
	assertThat( genZRec.getStellingen().get(  1 ).getZk(), is( 0x36 ) );
	assertThat( genZRec.getStellingen().get(  2 ).getZk(), is( 0x26 ) ); //@@NOG Dit is een illegale stelling
	assertThat( genZRec.getStellingen().get(  3 ).getZk(), is( 0x16 ) );
	assertThat( genZRec.getStellingen().get(  4 ).getZk(), is( 0x17 ) );

	assertThat( genZRec.getStellingen().get(  5 ).getS4(), is( 0x34 ) );
	assertThat( genZRec.getStellingen().get(  6 ).getS4(), is( 0x35 ) );
	assertThat( genZRec.getStellingen().get(  7 ).getS4(), is( 0x36 ) );
	assertThat( genZRec.getStellingen().get(  8 ).getS4(), is( 0x37 ) );
	assertThat( genZRec.getStellingen().get(  9 ).getS4(), is( 0x43 ) );
	assertThat( genZRec.getStellingen().get( 10 ).getS4(), is( 0x53 ) );
	assertThat( genZRec.getStellingen().get( 11 ).getS4(), is( 0x63 ) );
	assertThat( genZRec.getStellingen().get( 12 ).getS4(), is( 0x73 ) );
	assertThat( genZRec.getStellingen().get( 13 ).getS4(), is( 0x32 ) );
	assertThat( genZRec.getStellingen().get( 14 ).getS4(), is( 0x31 ) );
	assertThat( genZRec.getStellingen().get( 15 ).getS4(), is( 0x30 ) );
	assertThat( genZRec.getStellingen().get( 16 ).getS4(), is( 0x23 ) );
	assertThat( genZRec.getStellingen().get( 17 ).getS4(), is( 0x13 ) );
	assertThat( genZRec.getStellingen().get( 18 ).getS4(), is( 0x03 ) );
}
@Test
public void testCompareResultaten()
{
	assertThat( ILLEGAAL.compareTo( GEWONNEN ), is( lessThan( 0 ) ) );
	assertThat( GEWONNEN.compareTo( REMISE ), is( lessThan( 0 ) ) );
	assertThat( GEWONNEN.compareTo( VERLOREN ), is( lessThan( 0 ) ) );
	// Ze comparen dus gewoon op ordinal
}
/* 
 * Je moet het zo bekijken: als hier zwart aan zet is, dan bekijk je de zetten vanuit het oogpunt van wit
 * - Bij Zwart aan zet is de volgorde Gewonnen, kleinste aantal zetten, Remise, Verloren met grootste aantal zetten
 * - Bij Wit   aan zet is de volgorde Verloren, kleinste aantal zetten, Remise, Gewonnen met grootste aantal zetten 
 * 
 * ResultaatType = ILLEGAAL( "Illegaal" ), GEWONNEN( "Gewonnen" ), REMISE( "Remise"), VERLOREN( "Verloren" );
 */

@Test
public void testStellingComparator()
{
	// Resultaten ongelijk
	BoStelling links = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( GEWONNEN )
		.aantalZetten( 11 )
		.build();
	BoStelling rechts = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( VERLOREN )
		.aantalZetten( 9 )
		.build();
	assertThat( gen.stellingComparator.compare( links, rechts ), is( greaterThan( 0 ) ) );

	links.setAanZet( ZWART );
	rechts.setAanZet( ZWART );
	assertThat( gen.stellingComparator.compare( links, rechts ), is( lessThan( 0 ) ) );

	// Aantal zetten ongelijk, links groter dan rechts
	links = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( GEWONNEN )
		.aantalZetten( 11 )
		.build();
	rechts = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( GEWONNEN )
		.aantalZetten( 9 )
		.build();
	assertThat( gen.stellingComparator.compare( links, rechts ), is( lessThan( 0 ) ) );

	links.setAanZet( ZWART );
	rechts.setAanZet( ZWART );
	assertThat( gen.stellingComparator.compare( links, rechts ), is( greaterThan( 0 ) ) );
	
	// Aantal zetten ongelijk, links < rechts
	links = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( GEWONNEN )
		.aantalZetten( 9 )
		.build();
	rechts = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( GEWONNEN )
		.aantalZetten( 11 )
		.build();
	assertThat( gen.stellingComparator.compare( links, rechts ), is( greaterThan( 0 ) ) );

	links.setAanZet( ZWART );
	rechts.setAanZet( ZWART );
	assertThat( gen.stellingComparator.compare( links, rechts ), is( lessThan( 0 ) ) );

	// Aantal zetten ongelijk, links groter dan rechts
	links = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( VERLOREN )
		.aantalZetten( 11 )
		.build();
	rechts = BoStelling.builder()
		.aanZet( WIT )
		.resultaat( VERLOREN )
		.aantalZetten( 9 )
		.build();
	assertThat( gen.stellingComparator.compare( links, rechts ), is( greaterThan( 0 ) ) );

	links.setAanZet( ZWART );
	rechts.setAanZet( ZWART );
	assertThat( gen.stellingComparator.compare( links, rechts ), is( lessThan( 0 ) ) );
}
@Test
public void testGenZetSort()
{
	dbs.name( "Pipo" );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	GegenereerdeZetten genZRec;
	
	// Zwart aan zet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	genZRec = gen.genereerZetten( stelling );
	assertThat( genZRec.getAantal(), is( 19 ) );

	// Om een beetje verschil te krijgen
	genZRec.getStellingen().get( 5 ).setResultaat( ResultaatType.GEWONNEN );
	genZRec.getStellingen().get( 5 ).setAantalZetten( 11 );
	genZRec.getStellingen().get( 6 ).setResultaat( ResultaatType.VERLOREN );
	genZRec.getStellingen().get( 6 ).setAantalZetten( 11 );
	genZRec.getStellingen().get( 7 ).setResultaat( ResultaatType.GEWONNEN );
	genZRec.getStellingen().get( 7 ).setAantalZetten( 9 );
	genZRec.getStellingen().get( 8 ).setResultaat( ResultaatType.VERLOREN );
	genZRec.getStellingen().get( 8 ).setAantalZetten( 9 );
	
	genZRec.getStellingen().sort( gen.stellingComparator );
	
	assertThat( genZRec.getStellingen().get(  0 ).getS4(), is( 0x37 ) );
	assertThat( genZRec.getStellingen().get(  1 ).getS4(), is( 0x35 ) );

	assertThat( genZRec.getStellingen().get(  2 ).getZk(), is( 0x37 ) );
	assertThat( genZRec.getStellingen().get(  3 ).getZk(), is( 0x36 ) );
	assertThat( genZRec.getStellingen().get(  4 ).getZk(), is( 0x26 ) );
	assertThat( genZRec.getStellingen().get(  5 ).getZk(), is( 0x16 ) );
	assertThat( genZRec.getStellingen().get(  6 ).getZk(), is( 0x17 ) );

	assertThat( genZRec.getStellingen().get(  7 ).getS4(), is( 0x43 ) );
	assertThat( genZRec.getStellingen().get(  8 ).getS4(), is( 0x53 ) );
	assertThat( genZRec.getStellingen().get(  9 ).getS4(), is( 0x63 ) );
	assertThat( genZRec.getStellingen().get( 10 ).getS4(), is( 0x73 ) );
	assertThat( genZRec.getStellingen().get( 11 ).getS4(), is( 0x32 ) );
	assertThat( genZRec.getStellingen().get( 12 ).getS4(), is( 0x31 ) );
	assertThat( genZRec.getStellingen().get( 13 ).getS4(), is( 0x30 ) );
	assertThat( genZRec.getStellingen().get( 14 ).getS4(), is( 0x23 ) );
	assertThat( genZRec.getStellingen().get( 15 ).getS4(), is( 0x13 ) );
	assertThat( genZRec.getStellingen().get( 16 ).getS4(), is( 0x03 ) );

	// @@NOG Dit zou moeten
	assertThat( genZRec.getStellingen().get( 17 ).getS4(), is( 0x34 ) );
	assertThat( genZRec.getStellingen().get( 18 ).getS4(), is( 0x36 ) );

	// Wit aan zet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( WIT )
		.build();
	genZRec = gen.genereerZetten( stelling );
	assertThat( genZRec.getAantal(), is( 29 ) );

	// Om een beetje verschil te krijgen
	genZRec.getStellingen().get( 5 ).setResultaat( ResultaatType.GEWONNEN );
	genZRec.getStellingen().get( 5 ).setAantalZetten( 11 );
	genZRec.getStellingen().get( 6 ).setResultaat( ResultaatType.VERLOREN );
	genZRec.getStellingen().get( 6 ).setAantalZetten( 11 );
	genZRec.getStellingen().get( 7 ).setResultaat( ResultaatType.GEWONNEN );
	genZRec.getStellingen().get( 7 ).setAantalZetten( 9 );
	genZRec.getStellingen().get( 8 ).setResultaat( ResultaatType.VERLOREN );
	genZRec.getStellingen().get( 8 ).setAantalZetten( 9 );

	// Verwijder een groot aantal remisestellingen
	int size = genZRec.getStellingen().size();
	for ( int x = 11; x < size; x++ )
	{
		genZRec.getStellingen().remove( 11 );
	}
	genZRec.getStellingen().sort( gen.stellingComparator );
	
	assertThat( genZRec.getStellingen().get(  0 ).getWk(), is( 0x02 ) );
	assertThat( genZRec.getStellingen().get(  1 ).getWk(), is( 0x00 ) );

	assertThat( genZRec.getStellingen().get(  2 ).getWk(), is( 0x12 ) );
	assertThat( genZRec.getStellingen().get(  3 ).getWk(), is( 0x22 ) );
	assertThat( genZRec.getStellingen().get(  4 ).getWk(), is( 0x21 ) );
	assertThat( genZRec.getStellingen().get(  5 ).getWk(), is( 0x20 ) );
	assertThat( genZRec.getStellingen().get(  6 ).getWk(), is( 0x10 ) );

	assertThat( genZRec.getStellingen().get(  7 ).getS3(), is( 0x75 ) );
	assertThat( genZRec.getStellingen().get(  8 ).getS3(), is( 0x74 ) );
	assertThat( genZRec.getStellingen().get(  9 ).getWk(), is( 0x01 ) );
	assertThat( genZRec.getStellingen().get( 10 ).getS3(), is( 0x77 ) );
}

@Test
public void testIsPat()
{
	// @@@NOG
}
@Test
public void testVeldToAscii()
{
	assertThat( gen.veldToAscii( 0x00 ), is( "a1" ) );
	assertThat( gen.veldToAscii( 0x07 ), is( "h1" ) );
	assertThat( gen.veldToAscii( 0x08 ), is( "??" ) );
	assertThat( gen.veldToAscii( 0x70 ), is( "a8" ) );
	assertThat( gen.veldToAscii( 0x77 ), is( "h8" ) );
}
@Test
public void testAsciiToVeld()
{
	assertThat( gen.asciiToVeld( "a1" ), is( 0x00 ) );
	assertThat( gen.asciiToVeld( "h1" ), is( 0x07 ) );
	assertThat( gen.asciiToVeld( "a8" ), is( 0x70 ) );
	assertThat( gen.asciiToVeld( "h8" ), is( 0x77 ) );
	// @@NOG hoe werken die exceptions nou?
	//assertThrows( RuntimeException.class, () -> { gen.AsciiToVeld( "a9" ); } );
}
@Test
public void testGetStukInfo()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	assertThat( gen.getStukInfo( stelling, 1 ).getVeld(), is( 0x11 ) );
	assertThat( gen.getStukInfo( stelling, 1 ).getX(), is( 2 ) );
	assertThat( gen.getStukInfo( stelling, 1 ).getY(), is( 2 ) );
	assertThat( gen.getStukInfo( stelling, 1 ).getKleur(), is( WIT ) );
	assertThat( gen.getStukInfo( stelling, 1 ).getAfko(), is( "K" ) );

	assertThat( gen.getStukInfo( stelling, 2 ).getVeld(), is( 0x27 ) );
	assertThat( gen.getStukInfo( stelling, 2 ).getX(), is( 8 ) );
	assertThat( gen.getStukInfo( stelling, 2 ).getY(), is( 3 ) );
	assertThat( gen.getStukInfo( stelling, 2 ).getKleur(), is( ZWART ) );
	assertThat( gen.getStukInfo( stelling, 2 ).getAfko(), is( "K" ) );

	assertThat( gen.getStukInfo( stelling, 3 ).getVeld(), is( 0x76 ) );
	assertThat( gen.getStukInfo( stelling, 3 ).getX(), is( 7 ) );
	assertThat( gen.getStukInfo( stelling, 3 ).getY(), is( 8 ) );
	assertThat( gen.getStukInfo( stelling, 3 ).getKleur(), is( WIT ) );
	assertThat( gen.getStukInfo( stelling, 3 ).getAfko(), is( "D" ) );

	assertThat( gen.getStukInfo( stelling, 4 ).getVeld(), is( 0x33 ) );
	assertThat( gen.getStukInfo( stelling, 4 ).getX(), is( 4 ) );
	assertThat( gen.getStukInfo( stelling, 4 ).getY(), is( 4 ) );
	assertThat( gen.getStukInfo( stelling, 4 ).getKleur(), is( ZWART ) );
	assertThat( gen.getStukInfo( stelling, 4 ).getAfko(), is( "T" ) );
}

}
