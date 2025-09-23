package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
	assertThat( gen.veldToBitSetAndBuitenBord( 0 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 7 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 8 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x10 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x17 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x18 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x20 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x27 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x28 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x70 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x77 ), is( gen.Nul ) );
	assertThat( gen.veldToBitSetAndBuitenBord( 0x78 ), is( gen.bitSetOfInt( 8 ) ) );
	assertThat( gen.veldToBitSetAndBuitenBord( -1 ), is( gen.bitSetOfInt( 0x88 ) ) ); // 136
}
@Test
public void testMaakBordLeeg()
{
	gen.MaakBordLeeg();
	for ( int x = 0; x < 0x77; x++ )
	{
		assertThat( gen.Bord[x], is( gen.Leeg ) );
	}
}
@Test
public void testVulStukTabel()
{
	gen.VulStukTabel();
	Stuk stuk = gen.StukTabel[1];
	assertThat( stuk.getSoort(), is( StukType.Koning ) );
	assertThat( stuk.isKleur(), is( AlgDef.Wit ) );
	assertThat( stuk.getKnummer(), is( 1 ) );
	assertThat( stuk.getRichting(), is( gen.Krichting ) );
	assertThat( stuk.getAtlRicht(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getStukAfk(), is( 'K' ) );
	
	stuk = gen.StukTabel[2];
	assertThat( stuk.getSoort(), is( StukType.Koning ) );
	assertThat( stuk.isKleur(), is( AlgDef.Zwart ) );
	assertThat( stuk.getKnummer(), is( 2 ) );
	assertThat( stuk.getRichting(), is( gen.Krichting ) );
	assertThat( stuk.getAtlRicht(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getStukAfk(), is( 'K' ) );

	stuk = gen.StukTabel[3];
	assertThat( stuk.getSoort(), is( StukType.Dame ) );
	assertThat( stuk.isKleur(), is( AlgDef.Wit ) );
	assertThat( stuk.getKnummer(), is( 1 ) );
	assertThat( stuk.getRichting(), is( gen.Krichting ) );
	assertThat( stuk.getAtlRicht(), is( 8 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getStukAfk(), is( 'D' ) );
	
	stuk = gen.StukTabel[4];
	assertThat( stuk.getSoort(), is( StukType.Toren ) );
	assertThat( stuk.isKleur(), is( AlgDef.Zwart ) );
	assertThat( stuk.getKnummer(), is( 2 ) );
	assertThat( stuk.getRichting(), is( gen.Trichting ) );
	assertThat( stuk.getAtlRicht(), is( 4 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getStukAfk(), is( 'T' ) );

}
@Test
public void testZetBordOp()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.Bord[5], is( 1 ) );
	assertThat( gen.Bord[6], is( 2 ) );
	assertThat( gen.Bord[7], is( 3 ) );
	assertThat( gen.Bord[8], is( 4 ) );
	for ( int x = 0; x < 4; x++ )
	{
		assertThat( gen.Bord[x], is( gen.Leeg ) );
	}
	for ( int x = 9; x < 0x77; x++ )
	{
		assertThat( gen.Bord[x], is( gen.Leeg ) );
	}
}
@Test
public void testClrBord()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	gen.ZetBordOp( stelling );
	System.out.println( "In testClrBord" );
	gen.printBord();
	gen.ClrBord( stelling );
	System.out.println();
	gen.printBord();
	for ( int x = 0; x < 0x78; x++ )
	{
		if ( ! ( gen.Bord[x] == gen.Leeg ) )
		{
			System.out.println( "x=" + x + " Bord[x]=" + gen.Bord[x] );
		}
		assertThat( gen.Bord[x], is( gen.Leeg ) );
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
	assertThat( gen.IsGeomIllegaal( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 5 )
		.s3( 7 )
		.s4( 8 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 7 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 5 )
		.s4( 7 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 5 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( true ) );
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
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x33 ), is( false ) );
	gen.ClrBord( stelling );

	// T links
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x10 ), is( true ) );
	gen.ClrBord( stelling );

	// T uiterst rechts
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x17 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x17 ), is( true ) );
	gen.ClrBord( stelling );

	// T nog steeds uiterst rechts, maar D ertussen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x15 )
		.s4( 0x17 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x17 ), is( false ) );
	gen.ClrBord( stelling );

	// Check of Z schaak staat
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x20 )
		.s4( 0x77 )
		.aanZet( AlgDef.Wit )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[3], 0x27, 0x20 ), is( true ) );
	gen.ClrBord( stelling );
}
@Test
public void testCheckSchaakDoorStuk()
{
	// Check aStukVeld == aStelling.getWK(), d.w.z. het stuk is geslagen door wit
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x11 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.CheckSchaakDoorStuk( stelling, gen.StukTabel[4], 0x11, 0x11 ), is( false ) );
	gen.ClrBord( stelling );

	// Check aStukVeld == aStelling.getZK(), d.w.z. het stuk is geslagen door zwart
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x27 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.CheckSchaakDoorStuk( stelling, gen.StukTabel[4], 0x27, 0x27 ), is( false ) );
	gen.ClrBord( stelling );

	// Check dat het stuk aan zet is
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.CheckSchaakDoorStuk( stelling, gen.StukTabel[4], 0x11, 0x33 ), is( false ) );
	gen.ClrBord( stelling );

	// T links
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( AlgDef.Wit )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.CheckSchaakDoorStuk( stelling, gen.StukTabel[4], 0x11, 0x10 ), is( true ) );
	gen.ClrBord( stelling );
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
		.aanZet( AlgDef.Zwart )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );
	gen.ClrBord( stelling );

	// Check aStukVeld == aStelling.getZK(), d.w.z. het stuk is geslagen door zwart
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x27 )
		.aanZet( AlgDef.Zwart )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );
	gen.ClrBord( stelling );

	// Check dat het stuk aan zet is
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );
	gen.ClrBord( stelling );

	// T links geeft schaak
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( AlgDef.Wit )
		.build();
	assertThat( gen.isSchaak( stelling ), is( true ) );
	gen.ClrBord( stelling );
}
@Test
public void testAddZet()
{
	dbs.Name( "Pipo" );
	dbs.Create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	GenZRec genZRec;
	BoStelling resultaatStelling;
	
	// Gewone zet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	genZRec = new GenZRec();
	gen.AddZet( stelling, 3, 0x77, ZetSoort.Gewoon, 0x11, 0x76, genZRec );
	assertThat( genZRec.getAantal(), is( 1 ) );
	resultaatStelling = genZRec.getSptr().get( 0 );
	assertThat( resultaatStelling.getWk(), is( 0x11 ) );
	assertThat( resultaatStelling.getZk(), is( 0x27 ) );
	assertThat( resultaatStelling.getS3(), is( 0x77 ) );
	assertThat( resultaatStelling.getS4(), is( 0x33 ) );
	assertThat( resultaatStelling.isAanZet(), is( false ) );

	// Slagzet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Wit )
		.build();
	genZRec = new GenZRec();
	gen.AddZet( stelling, 4, 0x76, ZetSoort.SlagZet, 0x11, 0x76, genZRec );
	assertThat( genZRec.getAantal(), is( 1 ) );
	resultaatStelling = genZRec.getSptr().get( 0 );
	assertThat( resultaatStelling.getWk(), is( 0x11 ) );
	assertThat( resultaatStelling.getZk(), is( 0x27 ) );
	assertThat( resultaatStelling.getS3(), is( 0x11 ) );
	assertThat( resultaatStelling.getS4(), is( 0x76 ) );
	assertThat( resultaatStelling.isAanZet(), is( true ) );

	// Geslagen stukken meeverplaatsen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x11 )
		.s4( 0x33 )
		.aanZet( AlgDef.Wit )
		.build();
	genZRec = new GenZRec();
	gen.AddZet( stelling, 1, 0x12, ZetSoort.Gewoon, 0x11, 0x11, genZRec );
	assertThat( genZRec.getAantal(), is( 1 ) );
	resultaatStelling = genZRec.getSptr().get( 0 );
	assertThat( resultaatStelling.getWk(), is( 0x12 ) );
	assertThat( resultaatStelling.getZk(), is( 0x27 ) );
	assertThat( resultaatStelling.getS3(), is( 0x12 ) );
	assertThat( resultaatStelling.getS4(), is( 0x33 ) );
	assertThat( resultaatStelling.isAanZet(), is( true ) );

	dbs.delete();
}

@Test
public void testGenZetPerStuk()
{
	dbs.Name( "Pipo" );
	dbs.Create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	GenZRec genZRec;
	
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	genZRec = new GenZRec();
	gen.ZetBordOp( stelling );
	gen.GenZPerStuk( stelling, 4, 0x27, 0x33, genZRec );
	assertThat( genZRec.getAantal(), is( 14 ) );
	assertThat( genZRec.getSptr().get(  0 ).getS4(), is( 0x34 ) );
	assertThat( genZRec.getSptr().get(  0 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  1 ).getS4(), is( 0x35 ) );
	assertThat( genZRec.getSptr().get(  1 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  2 ).getS4(), is( 0x36 ) );
	assertThat( genZRec.getSptr().get(  2 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  3 ).getS4(), is( 0x37 ) );
	assertThat( genZRec.getSptr().get(  3 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  4 ).getS4(), is( 0x43 ) );
	assertThat( genZRec.getSptr().get(  4 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  5 ).getS4(), is( 0x53 ) );
	assertThat( genZRec.getSptr().get(  5 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  6 ).getS4(), is( 0x63 ) );
	assertThat( genZRec.getSptr().get(  6 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  7 ).getS4(), is( 0x73 ) );
	assertThat( genZRec.getSptr().get(  7 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  8 ).getS4(), is( 0x32 ) );
	assertThat( genZRec.getSptr().get(  8 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get(  9 ).getS4(), is( 0x31 ) );
	assertThat( genZRec.getSptr().get(  9 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get( 10 ).getS4(), is( 0x30 ) );
	assertThat( genZRec.getSptr().get( 10 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get( 11 ).getS4(), is( 0x23 ) );
	assertThat( genZRec.getSptr().get( 11 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get( 12 ).getS4(), is( 0x13 ) );
	assertThat( genZRec.getSptr().get( 12 ).isAanZet(), is( false ) );
	assertThat( genZRec.getSptr().get( 13 ).getS4(), is( 0x03 ) );
	assertThat( genZRec.getSptr().get( 13 ).isAanZet(), is( false ) );
	gen.ClrBord( stelling );
	
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x77 )
		.aanZet( AlgDef.Zwart )
		.build();
	genZRec = new GenZRec();
	System.out.println( "In testGenZetPerStuk" );
	gen.printBord();
	gen.ZetBordOp( stelling );
	gen.printBord();
	gen.GenZPerStuk( stelling, 4, 0x27, 0x77, genZRec );
	assertThat( genZRec.getAantal(), is( 5 ) );
	assertThat( genZRec.getSptr().get(  0 ).getS4(), is( 0x76 ) );
	assertThat( genZRec.getSptr().get(  0 ).getS3(), is( 0x11 ) );
	assertThat( genZRec.getSptr().get(  1 ).getS4(), is( 0x67 ) );
	assertThat( genZRec.getSptr().get(  2 ).getS4(), is( 0x57 ) );
	assertThat( genZRec.getSptr().get(  3 ).getS4(), is( 0x47 ) );
	assertThat( genZRec.getSptr().get(  4 ).getS4(), is( 0x37 ) );
	gen.ClrBord( stelling );
}
@Test
public void testGenZet()
{
	dbs.Name( "Pipo" );
	dbs.Create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	GenZRec genZRec;
	
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	genZRec = gen.GenZ( stelling );
	assertThat( genZRec.getAantal(), is( 19 ) );

	assertThat( genZRec.getSptr().get(  0 ).getZk(), is( 0x37 ) );
	assertThat( genZRec.getSptr().get(  1 ).getZk(), is( 0x36 ) );
	assertThat( genZRec.getSptr().get(  2 ).getZk(), is( 0x26 ) );
	assertThat( genZRec.getSptr().get(  3 ).getZk(), is( 0x16 ) );
	assertThat( genZRec.getSptr().get(  4 ).getZk(), is( 0x17 ) );

	assertThat( genZRec.getSptr().get(  5 ).getS4(), is( 0x34 ) );
	assertThat( genZRec.getSptr().get(  6 ).getS4(), is( 0x35 ) );
	assertThat( genZRec.getSptr().get(  7 ).getS4(), is( 0x36 ) );
	assertThat( genZRec.getSptr().get(  8 ).getS4(), is( 0x37 ) );
	assertThat( genZRec.getSptr().get(  9 ).getS4(), is( 0x43 ) );
	assertThat( genZRec.getSptr().get( 10 ).getS4(), is( 0x53 ) );
	assertThat( genZRec.getSptr().get( 11 ).getS4(), is( 0x63 ) );
	assertThat( genZRec.getSptr().get( 12 ).getS4(), is( 0x73 ) );
	assertThat( genZRec.getSptr().get( 13 ).getS4(), is( 0x32 ) );
	assertThat( genZRec.getSptr().get( 14 ).getS4(), is( 0x31 ) );
	assertThat( genZRec.getSptr().get( 15 ).getS4(), is( 0x30 ) );
	assertThat( genZRec.getSptr().get( 16 ).getS4(), is( 0x23 ) );
	assertThat( genZRec.getSptr().get( 17 ).getS4(), is( 0x13 ) );
	assertThat( genZRec.getSptr().get( 18 ).getS4(), is( 0x03 ) );
}
@Test
public void testGenZetSort()
{
	dbs.Name( "Pipo" );
	dbs.Create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	GenZRec genZRec;
	
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	genZRec = gen.GenZ( stelling );
	assertThat( genZRec.getAantal(), is( 19 ) );

	// Om een beetje verschil te krijgen
	genZRec.getSptr().get( 5 ).setResultaat( ResultaatType.Gewonnen );
	genZRec.getSptr().get( 5 ).setAantalZetten( 11 );
	genZRec.getSptr().get( 6 ).setResultaat( ResultaatType.Verloren );
	genZRec.getSptr().get( 6 ).setAantalZetten( 11 );
	genZRec.getSptr().get( 7 ).setResultaat( ResultaatType.Gewonnen );
	genZRec.getSptr().get( 7 ).setAantalZetten( 9 );
	genZRec.getSptr().get( 8 ).setResultaat( ResultaatType.Verloren );
	genZRec.getSptr().get( 8 ).setAantalZetten( 9 );
	
	genZRec.getSptr().sort( gen.stellingComparator );
	
	assertThat( genZRec.getSptr().get(  0 ).getS4(), is( 0x34 ) );
	assertThat( genZRec.getSptr().get(  1 ).getS4(), is( 0x36 ) );

	assertThat( genZRec.getSptr().get(  2 ).getZk(), is( 0x37 ) );
	assertThat( genZRec.getSptr().get(  3 ).getZk(), is( 0x36 ) );
	assertThat( genZRec.getSptr().get(  4 ).getZk(), is( 0x26 ) );
	assertThat( genZRec.getSptr().get(  5 ).getZk(), is( 0x16 ) );
	assertThat( genZRec.getSptr().get(  6 ).getZk(), is( 0x17 ) );

	assertThat( genZRec.getSptr().get(  7 ).getS4(), is( 0x43 ) );
	assertThat( genZRec.getSptr().get(  8 ).getS4(), is( 0x53 ) );
	assertThat( genZRec.getSptr().get(  9 ).getS4(), is( 0x63 ) );
	assertThat( genZRec.getSptr().get( 10 ).getS4(), is( 0x73 ) );
	assertThat( genZRec.getSptr().get( 11 ).getS4(), is( 0x32 ) );
	assertThat( genZRec.getSptr().get( 12 ).getS4(), is( 0x31 ) );
	assertThat( genZRec.getSptr().get( 13 ).getS4(), is( 0x30 ) );
	assertThat( genZRec.getSptr().get( 14 ).getS4(), is( 0x23 ) );
	assertThat( genZRec.getSptr().get( 15 ).getS4(), is( 0x13 ) );
	assertThat( genZRec.getSptr().get( 16 ).getS4(), is( 0x03 ) );

	assertThat( genZRec.getSptr().get( 17 ).getS4(), is( 0x35 ) );
	assertThat( genZRec.getSptr().get( 18 ).getS4(), is( 0x37 ) );
}

@Test
public void testIsPat()
{
	// @@@NOG
}
@Test
public void testVeldToAscii()
{
	assertThat( gen.VeldToAscii( 0x00 ), is( "a1" ) );
	assertThat( gen.VeldToAscii( 0x07 ), is( "h1" ) );
	assertThat( gen.VeldToAscii( 0x08 ), is( "??" ) );
	assertThat( gen.VeldToAscii( 0x70 ), is( "a8" ) );
	assertThat( gen.VeldToAscii( 0x77 ), is( "h8" ) );
}
@Test
public void testAsciiToVeld()
{
	assertThat( gen.AsciiToVeld( "a1" ), is( 0x00 ) );
	assertThat( gen.AsciiToVeld( "h1" ), is( 0x07 ) );
	assertThat( gen.AsciiToVeld( "a8" ), is( 0x70 ) );
	assertThat( gen.AsciiToVeld( "h8" ), is( 0x77 ) );
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
		.aanZet( AlgDef.Zwart )
		.build();
	assertThat( gen.GetStukInfo( stelling, 1 ).getVeld(), is( 0x11 ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).getX(), is( 2 ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).getY(), is( 2 ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).isKleur(), is( AlgDef.Wit ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).getStukAfk(), is( 'K' ) );

	assertThat( gen.GetStukInfo( stelling, 2 ).getVeld(), is( 0x27 ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).getX(), is( 8 ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).getY(), is( 3 ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).isKleur(), is( AlgDef.Zwart ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).getStukAfk(), is( 'K' ) );

	assertThat( gen.GetStukInfo( stelling, 3 ).getVeld(), is( 0x76 ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).getX(), is( 7 ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).getY(), is( 8 ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).isKleur(), is( AlgDef.Wit ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).getStukAfk(), is( 'D' ) );

	assertThat( gen.GetStukInfo( stelling, 4 ).getVeld(), is( 0x33 ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).getX(), is( 4 ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).getY(), is( 4 ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).isKleur(), is( AlgDef.Zwart ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).getStukAfk(), is( 'T' ) );
}

}
