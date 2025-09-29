package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

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
public void testMaakBordLeeg()
{
	gen.maakBordLeeg();
	for ( int x = 0; x < 0x77; x++ )
	{
		assertThat( gen.bord[x], is( gen.LEEG ) );
	}
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
public void testZetBordOp()
{
	gen.maakBordLeeg();
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.bord[5], is( 1 ) );
	assertThat( gen.bord[6], is( 2 ) );
	assertThat( gen.bord[7], is( 3 ) );
	assertThat( gen.bord[8], is( 4 ) );
	for ( int x = 0; x < 5; x++ )
	{
		assertThat( gen.bord[x], is( gen.LEEG ) );
	}
	for ( int x = 9; x < 0x77; x++ )
	{
		assertThat( gen.bord[x], is( gen.LEEG ) );
	}
	gen.clearBord( stelling );
}
@Test
public void testClrBord()
{
	gen.maakBordLeeg();
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	gen.zetBordOp( stelling );
	System.out.println( "In testClrBord" );
	gen.printBord();
	gen.clearBord( stelling );
	System.out.println();
	gen.printBord();
	for ( int x = 0; x < 0x78; x++ )
	{
		if ( ! ( gen.bord[x] == gen.LEEG ) )
		{
			System.out.println( "x=" + x + " Bord[x]=" + gen.bord[x] );
		}
		assertThat( gen.bord[x], is( gen.LEEG ) );
	}
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
	gen.zetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x33 ), is( false ) );
	gen.clearBord( stelling );

	// T links
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( ZWART )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x10 ), is( true ) );
	gen.clearBord( stelling );

	// T uiterst rechts
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x17 )
		.aanZet( ZWART )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x17 ), is( true ) );
	gen.clearBord( stelling );

	// T nog steeds uiterst rechts, maar D ertussen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x15 )
		.s4( 0x17 )
		.aanZet( ZWART )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[4], 0x11, 0x17 ), is( false ) );
	gen.clearBord( stelling );

	// Check of Z schaak staat
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x20 )
		.s4( 0x77 )
		.aanZet( WIT )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.stukTabel[3], 0x27, 0x20 ), is( true ) );
	gen.clearBord( stelling );
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
	gen.zetBordOp( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[3], 0x11, 0x11 ), is( false ) );
	gen.clearBord( stelling );

	// Check aStukVeld == aStelling.getZK(), d.w.z. het zwarte stuk is geslagen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x27 )
		.aanZet( ZWART )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[4], 0x27, 0x27 ), is( false ) );
	gen.clearBord( stelling );

	// Check dat het stuk aan zet is
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[4], 0x11, 0x33 ), is( false ) );
	gen.clearBord( stelling );

	// T links
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( WIT )
		.build();
	gen.zetBordOp( stelling );
	assertThat( gen.checkSchaakDoorStuk( stelling, gen.stukTabel[4], 0x11, 0x10 ), is( true ) );
	gen.clearBord( stelling );
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
	gen.clearBord( stelling );

	// Check aStukVeld == aStelling.getZK(), d.w.z. het stuk is geslagen door zwart
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x27 )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );
	gen.clearBord( stelling );

	// Check dat het stuk aan zet is
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );
	gen.clearBord( stelling );

	// T links geeft schaak
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( WIT )
		.build();
	assertThat( gen.isSchaak( stelling ), is( true ) );
	gen.clearBord( stelling );
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
	gen.zetBordOp( boStelling );
	GegenereerdeZetten genZRec = gen.genereerZettenPerStuk( boStelling, 4, 0x27, 0x33 );
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
	gen.clearBord( boStelling );
	
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x77 )
		.aanZet( ZWART )
		.build();
	//System.out.println( "In testGenZetPerStuk" );
	//gen.printBord();
	gen.zetBordOp( boStelling );
	//gen.printBord();
	genZRec = gen.genereerZettenPerStuk( boStelling, 4, 0x27, 0x77 );
	assertThat( genZRec.getAantal(), is( 5 ) );
	assertThat( genZRec.getStellingen().get(  0 ).getS4(), is( 0x76 ) );
	assertThat( genZRec.getStellingen().get(  0 ).getS3(), is( 0x11 ) );
	assertThat( genZRec.getStellingen().get(  1 ).getS4(), is( 0x67 ) );
	assertThat( genZRec.getStellingen().get(  2 ).getS4(), is( 0x57 ) );
	assertThat( genZRec.getStellingen().get(  3 ).getS4(), is( 0x47 ) );
	assertThat( genZRec.getStellingen().get(  4 ).getS4(), is( 0x37 ) );
	gen.clearBord( boStelling );
	
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
	gen.zetBordOp( boStelling );
	//gen.printBord();
	genZRec = gen.genereerZettenPerStuk( boStelling, 1, 0x02, 0x02 );
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
	assertThat( genZRec.getStellingen().get(  2 ).getZk(), is( 0x26 ) );
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
public void testGenZetSort()
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
	
	// @@NOG Dit klopt nog niet. Hij sorteert Gewonnen in 11 zetten vòòr Gewonnen in 9 setten
	// en of we nu 1 of -1 retourneren in de Gewonnen tak, maakt niets uit
	assertThat( genZRec.getStellingen().get(  0 ).getS4(), is( 0x34 ) );
	assertThat( genZRec.getStellingen().get(  1 ).getS4(), is( 0x36 ) );

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

	assertThat( genZRec.getStellingen().get( 17 ).getS4(), is( 0x37 ) );
	assertThat( genZRec.getStellingen().get( 18 ).getS4(), is( 0x35 ) );
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
