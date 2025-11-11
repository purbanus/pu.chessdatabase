package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.ResultaatType.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dbs.Dbs;
import pu.chessdatabase.dbs.ResultaatType;

import lombok.Data;

@SpringBootTest
@Data
public class TestGen
{
private static final String DATABASE_NAME = "dbs/Pipo";
@Autowired private Gen gen;
@Autowired private Dbs dbs;
@Autowired private Config config;
String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	dbs.create();
}
@AfterEach
public void destroy()
{
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	assertThat( dbs.getDatabaseName(), startsWith( DATABASE_NAME ) );
	dbs.delete();
	config.switchConfig( savedConfigString );
}

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
public void testVeldToAlfa()
{
	assertThat( gen.veldToAlfa( 0x00 ), is( "a1" ) );
	assertThat( gen.veldToAlfa( 0x07 ), is( "h1" ) );
	assertThat( gen.veldToAlfa( 0x70 ), is( "a8" ) );
	assertThat( gen.veldToAlfa( 0x77 ), is( "h8" ) );
	assertThrows( RuntimeException.class, () -> { Gen.veldToAlfa( -31415 ); } );
	assertThrows( RuntimeException.class, () -> { Gen.veldToAlfa(   0x08 ); } );
	assertThrows( RuntimeException.class, () -> { Gen.veldToAlfa(     63 ); } );
	assertThrows( RuntimeException.class, () -> { Gen.veldToAlfa(   1000 ); } );
}
@Test
public void testAlfaToVeld()
{
	assertThat( gen.alfaToVeld( "a1" ), is( 0x00 ) );
	assertThat( gen.alfaToVeld( "h1" ), is( 0x07 ) );
	assertThat( gen.alfaToVeld( "a8" ), is( 0x70 ) );
	assertThat( gen.alfaToVeld( "h8" ), is( 0x77 ) );
	assertThat( gen.alfaToVeld( "A8" ), is( 0x70 ) );
	assertThat( gen.alfaToVeld( "H8" ), is( 0x77 ) );
	assertThrows( RuntimeException.class, () -> { Gen.alfaToVeld( "a9" ); } );
	assertThrows( RuntimeException.class, () -> { Gen.alfaToVeld( "i2" ); } );
	assertThrows( RuntimeException.class, () -> { Gen.alfaToVeld( "a" ); } );
	assertThrows( RuntimeException.class, () -> { Gen.alfaToVeld( "abc" ); } );
}

@Test
public void testIsGeomIllegaal3Stukken()
{
	getConfig().switchConfig( "TESTKDK", false );

	// Gewoon goed
	BoStelling boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
//		.s4( 8 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( false ) );

	// De koningen op hetzelfde veld
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 5 )
		.s3( 7 )
//		.s4( 8 )
		.build();
	
	// Gewoon goed
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 5 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( false ) );

	// Stuk onder koning van verkeerde kleur
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 6 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( true ) );
	
}
@Test
public void testIsGeomIllegaal4Stukken()
{
	// Twee stukken op hetzelfde veld
	BoStelling boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 7 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( true ) );

	// Stuk onder koning van verkeerde kleur
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 5 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( true ) );
	
	// Stuk onder koning van verkeerde kleur
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 6 )
		.s4( 8 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( true ) );
	
	config.switchConfig( "KLLK" );
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 0x10 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( true ) );
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 0x11 )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( false ) );
}
@Test
public void testIsGeomIllegaal5Stukken()
{
	// @@NOG Bij 5 stukken
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
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	Bord bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.isSchaakDoorStuk( gen.getStukken().getS4(), 0x11, 0x33, bord ), is( false ) );

	// T links
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.isSchaakDoorStuk( gen.getStukken().getS4(), 0x11, 0x10, bord ), is( true ) );

	// T uiterst rechts
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x17 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.isSchaakDoorStuk( gen.getStukken().getS4(), 0x11, 0x17, bord ), is( true ) );

	// T nog steeds uiterst rechts, maar D ertussen
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x15 )
		.s4( 0x17 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.isSchaakDoorStuk( gen.getStukken().getS4(), 0x11, 0x17, bord ), is( false ) );

	// Check of Z schaak staat
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x20 )
		.s4( 0x77 )
		.aanZet( WIT )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.isSchaakDoorStuk( gen.getStukken().getS3(), 0x27, 0x20, bord ), is( true ) );
}
@Test
public void testCheckSchaakDoorStuk()
{
	// Check aStukVeld == aStelling.getWK(), d.w.z. het witte stuk is geslagen
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x11 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	Bord bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.checkSchaakDoorStuk( boStelling, gen.getStukken().getS3(), 0x11, 0x11, bord ), is( false ) );

	// Check aStukVeld == aStelling.getZK(), d.w.z. het zwarte stuk is geslagen
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x27 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.checkSchaakDoorStuk( boStelling, gen.getStukken().getS4(), 0x27, 0x27, bord ), is( false ) );

	// Check dat het stuk aan zet is
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.checkSchaakDoorStuk( boStelling, gen.getStukken().getS4(), 0x11, 0x33, bord ), is( false ) );

	// T links
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( WIT )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	assertThat( gen.checkSchaakDoorStuk( boStelling, gen.getStukken().getS4(), 0x11, 0x10, bord ), is( true ) );
}
@Test
public void testIsSchaak3Stukken()
{
	getConfig().switchConfig( "TESTKDK", false );
	
	// Check aStukVeld == aStelling.getWk(), d.w.z. s3 is geslagen door zwart
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "b2" )
		.aanZet( WIT )
		.build();
	assertThat( gen.isSchaak( boStelling ), is( false ) );

	// Check normale stelling
	boStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "b2" )
		.aanZet( WIT )
		.build();
	assertThat( gen.isSchaak( boStelling ), is( false ) );

	// Schaakje
	boStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "h8" )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( boStelling ), is( true ) );

}
@Test
public void testIsSchaak4Stukken()
{
	getConfig().switchConfig( "TESTKDKT", false );
	
	// Check aStukVeld == aStelling.getZK(), d.w.z. s4 is geslagen door wit
	BoStelling stelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "h3" )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );

	// Check dat het stuk aan zet is
	stelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "d4" )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( false ) );

	// T links geeft schaak
	stelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "a2" )
		.aanZet( WIT )
		.build();
	assertThat( gen.isSchaak( stelling ), is( true ) );

	// D geeft schaak
	stelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "h8" )
		.s4( "a2" )
		.aanZet( ZWART )
		.build();
	assertThat( gen.isSchaak( stelling ), is( true ) );
}
@Test
public void testIsSchaak5Stukken()
{
	// @@NOG NOG
}
@Test
public void testAddZet()
{
	dbs.setDatabaseName( DATABASE_NAME );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	List<BoStelling> gegenereerdeZetten;
	BoStelling resultaatStelling;
	
	// Gewone zet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	gegenereerdeZetten = new ArrayList<>();
	gen.addZet( stelling, gen.getStukken().getS3(), 0x77, ZetSoort.GEWOON, 0x11, 0x76, gegenereerdeZetten );
	assertThat( gegenereerdeZetten.size(), is( 1 ) );
	resultaatStelling = gegenereerdeZetten.get( 0 );
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
	gegenereerdeZetten = new ArrayList<>();
	gen.addZet( stelling, gen.getStukken().getS4(), 0x76, ZetSoort.SLAGZET, 0x11, 0x76, gegenereerdeZetten );
	assertThat( gegenereerdeZetten.size(), is( 1 ) );
	resultaatStelling = gegenereerdeZetten.get( 0 );
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
	gegenereerdeZetten = new ArrayList<>();
	gen.addZet( stelling, gen.getStukken().getWk(), 0x12, ZetSoort.GEWOON, 0x11, 0x11, gegenereerdeZetten );
	assertThat( gegenereerdeZetten.size(), is( 1 ) );
	resultaatStelling = gegenereerdeZetten.get( 0 );
	assertThat( resultaatStelling.getWk(), is( 0x12 ) );
	assertThat( resultaatStelling.getZk(), is( 0x27 ) );
	assertThat( resultaatStelling.getS3(), is( 0x12 ) );
	assertThat( resultaatStelling.getS4(), is( 0x33 ) );
	assertThat( resultaatStelling.getAanZet(), is( ZWART ) );
}

@Test
public void testGenZetPerStuk()
{
	// @@NOG NOG varianten met 3 en 5 stukken
	dbs.setDatabaseName( DATABASE_NAME );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling boStelling;
	
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	Bord bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );

	List<BoStelling> gegenereerdeZetten = gen.genereerZettenPerStuk( boStelling, gen.getStukken().getS4(), boStelling.getZk(), boStelling.getS4(), bord );
	assertThat( gegenereerdeZetten.size(), is( 14 ) );
	assertThat( gegenereerdeZetten.get(  0 ).getS4(), is( 0x34 ) );
	assertThat( gegenereerdeZetten.get(  0 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  1 ).getS4(), is( 0x35 ) );
	assertThat( gegenereerdeZetten.get(  1 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  2 ).getS4(), is( 0x36 ) );
	assertThat( gegenereerdeZetten.get(  2 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  3 ).getS4(), is( 0x37 ) );
	assertThat( gegenereerdeZetten.get(  3 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  4 ).getS4(), is( 0x43 ) );
	assertThat( gegenereerdeZetten.get(  4 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  5 ).getS4(), is( 0x53 ) );
	assertThat( gegenereerdeZetten.get(  5 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  6 ).getS4(), is( 0x63 ) );
	assertThat( gegenereerdeZetten.get(  6 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  7 ).getS4(), is( 0x73 ) );
	assertThat( gegenereerdeZetten.get(  7 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  8 ).getS4(), is( 0x32 ) );
	assertThat( gegenereerdeZetten.get(  8 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get(  9 ).getS4(), is( 0x31 ) );
	assertThat( gegenereerdeZetten.get(  9 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get( 10 ).getS4(), is( 0x30 ) );
	assertThat( gegenereerdeZetten.get( 10 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get( 11 ).getS4(), is( 0x23 ) );
	assertThat( gegenereerdeZetten.get( 11 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get( 12 ).getS4(), is( 0x13 ) );
	assertThat( gegenereerdeZetten.get( 12 ).getAanZet(), is( WIT ) );
	assertThat( gegenereerdeZetten.get( 13 ).getS4(), is( 0x03 ) );
	assertThat( gegenereerdeZetten.get( 13 ).getAanZet(), is( WIT ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x77 )
		.aanZet( ZWART )
		.build();
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );
	gegenereerdeZetten = gen.genereerZettenPerStuk( boStelling, gen.getStukken().getS4(), boStelling.getZk(), boStelling.getS4(), bord );
	assertThat( gegenereerdeZetten.size(), is( 5 ) );
	assertThat( gegenereerdeZetten.get(  0 ).getS4(), is( 0x76 ) );
	assertThat( gegenereerdeZetten.get(  0 ).getS3(), is( 0x11 ) );
	assertThat( gegenereerdeZetten.get(  1 ).getS4(), is( 0x67 ) );
	assertThat( gegenereerdeZetten.get(  2 ).getS4(), is( 0x57 ) );
	assertThat( gegenereerdeZetten.get(  3 ).getS4(), is( 0x47 ) );
	assertThat( gegenereerdeZetten.get(  4 ).getS4(), is( 0x37 ) );
	
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
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken(), boStelling );	//gen.printBord();
	gegenereerdeZetten = gen.genereerZettenPerStuk( boStelling, gen.getStukken().getWk(), boStelling.getWk(), boStelling.getWk(), bord );
	assertThat( gegenereerdeZetten.size(), is( 5 ) );

}
@Test
public void testGenereerZetten()
{
	// @@NOG NOG varianten met 3 en 5 stukken
	dbs.setDatabaseName( DATABASE_NAME );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling boStelling;
	List<BoStelling> gegenereerdeZetten;
	
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	gegenereerdeZetten = gen.genereerZetten( boStelling );
	assertThat( gegenereerdeZetten.size(), is( 19 ) );

	assertThat( gegenereerdeZetten.get(  0 ).getZk(), is( 0x37 ) );
	assertThat( gegenereerdeZetten.get(  1 ).getZk(), is( 0x36 ) );
	// Dit is een illegale stelling! Klopt, maar je hebt ook nog geen bouw.schaakjes gedaan. Die zou hem markeren
	// als ILLEGAAL en dan zou deze stelling nit gegenereerd worden.
	assertThat( gegenereerdeZetten.get(  2 ).getZk(), is( 0x26 ) );
	assertThat( gegenereerdeZetten.get(  3 ).getZk(), is( 0x16 ) );
	assertThat( gegenereerdeZetten.get(  4 ).getZk(), is( 0x17 ) );

	assertThat( gegenereerdeZetten.get(  5 ).getS4(), is( 0x34 ) );
	assertThat( gegenereerdeZetten.get(  6 ).getS4(), is( 0x35 ) );
	assertThat( gegenereerdeZetten.get(  7 ).getS4(), is( 0x36 ) );
	assertThat( gegenereerdeZetten.get(  8 ).getS4(), is( 0x37 ) );
	assertThat( gegenereerdeZetten.get(  9 ).getS4(), is( 0x43 ) );
	assertThat( gegenereerdeZetten.get( 10 ).getS4(), is( 0x53 ) );
	assertThat( gegenereerdeZetten.get( 11 ).getS4(), is( 0x63 ) );
	assertThat( gegenereerdeZetten.get( 12 ).getS4(), is( 0x73 ) );
	assertThat( gegenereerdeZetten.get( 13 ).getS4(), is( 0x32 ) );
	assertThat( gegenereerdeZetten.get( 14 ).getS4(), is( 0x31 ) );
	assertThat( gegenereerdeZetten.get( 15 ).getS4(), is( 0x30 ) );
	assertThat( gegenereerdeZetten.get( 16 ).getS4(), is( 0x23 ) );
	assertThat( gegenereerdeZetten.get( 17 ).getS4(), is( 0x13 ) );
	assertThat( gegenereerdeZetten.get( 18 ).getS4(), is( 0x03 ) );
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
	dbs.setDatabaseName( DATABASE_NAME );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen

	BoStelling stelling;
	List<BoStelling> gegenereerdeZetten;
	
	// Zwart aan zet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( ZWART )
		.build();
	gegenereerdeZetten = gen.genereerZetten( stelling );
	assertThat( gegenereerdeZetten.size(), is( 19 ) );

	// Om een beetje verschil te krijgen
	gegenereerdeZetten.get( 5 ).setResultaat( ResultaatType.GEWONNEN );
	gegenereerdeZetten.get( 5 ).setAantalZetten( 11 );
	gegenereerdeZetten.get( 6 ).setResultaat( ResultaatType.VERLOREN );
	gegenereerdeZetten.get( 6 ).setAantalZetten( 11 );
	gegenereerdeZetten.get( 7 ).setResultaat( ResultaatType.GEWONNEN );
	gegenereerdeZetten.get( 7 ).setAantalZetten( 9 );
	gegenereerdeZetten.get( 8 ).setResultaat( ResultaatType.VERLOREN );
	gegenereerdeZetten.get( 8 ).setAantalZetten( 9 );
	
	gegenereerdeZetten.sort( gen.stellingComparator );
	
	assertThat( gegenereerdeZetten.get(  0 ).getS4(), is( 0x37 ) );
	assertThat( gegenereerdeZetten.get(  1 ).getS4(), is( 0x35 ) );

	assertThat( gegenereerdeZetten.get(  2 ).getZk(), is( 0x37 ) );
	assertThat( gegenereerdeZetten.get(  3 ).getZk(), is( 0x36 ) );
	assertThat( gegenereerdeZetten.get(  4 ).getZk(), is( 0x26 ) );
	assertThat( gegenereerdeZetten.get(  5 ).getZk(), is( 0x16 ) );
	assertThat( gegenereerdeZetten.get(  6 ).getZk(), is( 0x17 ) );

	assertThat( gegenereerdeZetten.get(  7 ).getS4(), is( 0x43 ) );
	assertThat( gegenereerdeZetten.get(  8 ).getS4(), is( 0x53 ) );
	assertThat( gegenereerdeZetten.get(  9 ).getS4(), is( 0x63 ) );
	assertThat( gegenereerdeZetten.get( 10 ).getS4(), is( 0x73 ) );
	assertThat( gegenereerdeZetten.get( 11 ).getS4(), is( 0x32 ) );
	assertThat( gegenereerdeZetten.get( 12 ).getS4(), is( 0x31 ) );
	assertThat( gegenereerdeZetten.get( 13 ).getS4(), is( 0x30 ) );
	assertThat( gegenereerdeZetten.get( 14 ).getS4(), is( 0x23 ) );
	assertThat( gegenereerdeZetten.get( 15 ).getS4(), is( 0x13 ) );
	assertThat( gegenereerdeZetten.get( 16 ).getS4(), is( 0x03 ) );

	assertThat( gegenereerdeZetten.get( 17 ).getS4(), is( 0x34 ) );
	assertThat( gegenereerdeZetten.get( 18 ).getS4(), is( 0x36 ) );

	// Wit aan zet
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( WIT )
		.build();
	gegenereerdeZetten = gen.genereerZetten( stelling );
	assertThat( gegenereerdeZetten.size(), is( 29 ) );

	// Om een beetje verschil te krijgen
	gegenereerdeZetten.get( 5 ).setResultaat( ResultaatType.GEWONNEN );
	gegenereerdeZetten.get( 5 ).setAantalZetten( 11 );
	gegenereerdeZetten.get( 6 ).setResultaat( ResultaatType.VERLOREN );
	gegenereerdeZetten.get( 6 ).setAantalZetten( 11 );
	gegenereerdeZetten.get( 7 ).setResultaat( ResultaatType.GEWONNEN );
	gegenereerdeZetten.get( 7 ).setAantalZetten( 9 );
	gegenereerdeZetten.get( 8 ).setResultaat( ResultaatType.VERLOREN );
	gegenereerdeZetten.get( 8 ).setAantalZetten( 9 );

	// Verwijder een groot aantal remisestellingen
	int size = gegenereerdeZetten.size();
	for ( int x = 11; x < size; x++ )
	{
		gegenereerdeZetten.remove( 11 );
	}
	gegenereerdeZetten.sort( gen.stellingComparator );
	
	assertThat( gegenereerdeZetten.get(  0 ).getWk(), is( 0x02 ) );
	assertThat( gegenereerdeZetten.get(  1 ).getWk(), is( 0x00 ) );

	assertThat( gegenereerdeZetten.get(  2 ).getWk(), is( 0x12 ) );
	assertThat( gegenereerdeZetten.get(  3 ).getWk(), is( 0x22 ) );
	assertThat( gegenereerdeZetten.get(  4 ).getWk(), is( 0x21 ) );
	assertThat( gegenereerdeZetten.get(  5 ).getWk(), is( 0x20 ) );
	assertThat( gegenereerdeZetten.get(  6 ).getWk(), is( 0x10 ) );

	assertThat( gegenereerdeZetten.get(  7 ).getS3(), is( 0x75 ) );
	assertThat( gegenereerdeZetten.get(  8 ).getS3(), is( 0x74 ) );
	assertThat( gegenereerdeZetten.get(  9 ).getWk(), is( 0x01 ) );
	assertThat( gegenereerdeZetten.get( 10 ).getS3(), is( 0x77 ) );
}

@Test
public void testIsPat()
{
	// @@@NOG
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
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getWk() ).getVeld(), is( 0x11 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getWk() ).getX(), is( 2 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getWk() ).getY(), is( 2 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getWk() ).getKleur(), is( WIT ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getWk() ).getAfko(), is( "K" ) );

	assertThat( gen.getStukInfo( stelling, gen.getStukken().getZk() ).getVeld(), is( 0x27 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getZk() ).getX(), is( 8 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getZk() ).getY(), is( 3 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getZk() ).getKleur(), is( ZWART ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getZk() ).getAfko(), is( "K" ) );

	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS3() ).getVeld(), is( 0x76 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS3() ).getX(), is( 7 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS3() ).getY(), is( 8 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS3() ).getKleur(), is( WIT ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS3() ).getAfko(), is( "D" ) );

	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS4() ).getVeld(), is( 0x33 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS4() ).getX(), is( 4 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS4() ).getY(), is( 4 ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS4() ).getKleur(), is( ZWART ) );
	assertThat( gen.getStukInfo( stelling, gen.getStukken().getS4() ).getAfko(), is( "T" ) );
}

}
