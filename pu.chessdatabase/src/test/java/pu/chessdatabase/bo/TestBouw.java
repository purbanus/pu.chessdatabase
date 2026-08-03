package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.PassType.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.configuraties.ConfigImpl;
import pu.chessdatabase.dbs.Dbs;
import pu.chessdatabase.dbs.PassType;
import pu.chessdatabase.dbs.Transformator;
import pu.chessdatabase.dbs.VM;
import pu.chessdatabase.dbs.VMStellingIterator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Data
@Slf4j
public class TestBouw
{
private static final boolean DO_PRINT = false;
@Autowired private Bouw bouw;
@Autowired private Dbs dbs;
@Autowired private VM vm;
@Autowired private Gen gen;
@Autowired private VMStellingIterator vmStellingIterator;
@Autowired private Config config;
String savedConfigString;

@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( Config.PIPOKDKT, false ); // false want de database bestaat nog niet dus VM kan m niet openen
	dbs.create();
}
@AfterEach
public void destroy()
{
	assertThat( dbs.getDatabaseName(), startsWith( PREFIX_TEST_DATABASE ) );
	//assertThat( config.getAantalStukken(), is( lessThanOrEqualTo( 4 ) ) );
	dbs.delete();
	config.switchConfig( savedConfigString );
}
@Test
public void testConstructor()
{
	assertThat( bouw.passNchanges, is( true ) );
	assertThat( bouw.passNumber, is( 0 ) );
}
@Test
public void testReportNewPass()
{
	int [][] totals = new int [][] { { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };
	vmStellingIterator.setTellingen( totals );
	vmStellingIterator.setStellingTeller( 1500 );
	vmStellingIterator.setReportFunction( null );
	vmStellingIterator.setReportFrequency( 0 );
	bouw.reportNewPass( "", DO_PRINT );
	
	totals = new int [][] { { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
	assertThat( vmStellingIterator.getTellingen(), is( totals ) ); 
	assertThat( vmStellingIterator.getStellingTeller(), is( 0 ) ); 
	// Niet mogelijk assertThat( vmStellingIterator.getReportFunction(), is( bouw::showTellers ) ); 
	assertThat( vmStellingIterator.getReportFunction(), is( notNullValue() ) );
	// Dit is de databaseSize / 4 (64 * 64 * 64 * 10 * 2 / 4), zie Bouw,getReportFrequency
	assertThat( vmStellingIterator.getReportFrequency(), is( 64 * 64 * 64 * 5 ) ); 
}
//@Test
public void testTelAlles()
{
//	if ( DO_PRINT )
	{
		LOG.info( "methode testTelAlles" );
	}
	doTestTelAlles( Config.KDKTT );
	doTestTelAlles( Config.TESTKDKTT );
	// doTestTelAlles( Config.PIPOKDKTT ); // Duurt een beetje lang, zo'n 3 minuten
	
	config.switchConfig( Config.PIPOKDKT );
}
void doTestTelAlles( ConfigImpl aConfigImpl )
{
	LOG.info( "\nTel alles in {}", aConfigImpl );
	getConfig().switchConfig( aConfigImpl );
	bouw.telAndPrintAlles( true );
}
//@Test
public void testTelAllesInBeginstelling()
{
//	if ( DO_PRINT )
	{
		LOG.info( "methode testTelAllesInBeginStelling" );
	}
	doTestTelAllesInBeginStelling( Config.PIPOKDK );
	doTestTelAllesInBeginStelling( Config.PIPOKDKT );
	// doTestTelAlles( Config.PIPOKDKTT ); // Duurt een beetje lang, zo'n 3 minuten
	
	config.switchConfig( Config.PIPOKDKT );
}
void doTestTelAllesInBeginStelling( ConfigImpl aConfigImpl )
{
	LOG.info( "\nTel alles in {}", aConfigImpl );
	getConfig().switchConfig( aConfigImpl );
	
	bouw.pass_0( false );
	dbs.open();
	bouw.telAndPrintAlles( true );
}
//@Test
public void testMatStellingen()
{
	if ( DO_PRINT )
	{
		LOG.info( "methode testMatStellingen" );
	}
	getConfig().switchConfig( Config.PIPOKDK );

	bouw.pass_0( false );
	dbs.open();
	bouw.telAndPrintAlles( true );
	vmStellingIterator.clearTellingen();
	vmStellingIterator.setDoAllPositions( true );
	dbs.pass( MarkeerWitEnZwart, this::printMatStelling, "rw" );
}
private void printMatStelling( BoStelling aBoStelling )
{
	if ( aBoStelling.getResultaat() == Verloren && aBoStelling.getAanZet() == Zwart )
	{
		LOG.info( "{}", aBoStelling );
	}
}
@Test
public void testIsIllegaal()
{
	//IsGeomIllegaal wordt al getest in TestGen. We nemen nu een willekeurige illegale stelling
	BoStelling boStelling;
	BoStelling gotBoStelling;
	boStelling = BoStelling.alfaBuilder()
		.wk( "f1" )
		.zk( "f1" )
		.s3( "f2" )
		.s4( "g2" )
		.aanZet( Wit )
		.build();
	bouw.isIllegaal( boStelling );
	
	// Dit is VMStelling(WK=2, ZK=2, s3=10, s4=9, AanZet=false)
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( Illegaal ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "g7" )
		.s4( "h8" )
		.aanZet( Zwart )
		.build();
	bouw.isIllegaal( boStelling );
	
	// Dit is VMStelling(WK=2, ZK=2, s3=10, s4=9, AanZet=false)
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
}
//@Test // Deze test duurt ruim 3 minuten
public void testIsIllegaal5Stukken()
{
	config.switchConfig( Config.PIPOKDKTT );
	dbs.create();
	bouw.pass_0();
	
	// Bug van 18-06-2026
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "a8" )
		.s4( "h8" )
		.s5( "h8" )
		.aanZet( Zwart )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( false ) );
	BoStelling gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
}
//@Test // Deze test duurt zo'n 10 seconden
public void testIsIllegaal5Stukken_2()
{
	config.switchConfig( Config.PIPOKDKTT );
	bouw.reportNewPass( "Markeren illegale stellingen", false );
	dbs.pass( PassType.MarkeerWit, bouw::isIllegaal, "rw" );
	
	// Bug van 18-06-2026
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "a8" )
		.s4( "h8" )
		.s5( "h8" )
		.aanZet( Zwart )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( false ) );
	BoStelling gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
}

//@Test
public void telIllegaleStellingen()
{
	config.switchConfig( Config.PIPOKDKTT );
	bouw.illegaleStellingen.clear();
	bouw.passNumber = 0;

	bouw.reportNewPass( "Markeren illegale stellingen", true );
	dbs.pass( PassType.MarkeerWit, bouw::isIllegaal, "rw" );
	bouw.telAndPrintAlles( true );
	
//	LOG.info( "Aantal Illegale stellingen: " + bouw.illegaleStellingen.size() );
//	LOG.info( "Illegale stellingen" );
//	LOG.info(   bouw.illegaleStellingen );
//
//	LOG.info( "Aantal niet-Illegale stellingen: " + bouw.changes.size() );
//	LOG.info( "Niet-Illegale stellingen" );
//	LOG.info(   bouw.changes );
}
@Test
public void testSchaakjes()
{
	BoStelling boStelling;

	// T links geeft schaak
	boStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "g3" )
		.s3( "g8" )
		.s4( "d4" )
		.aanZet( Wit )
		.resultaat( Remise )
		.build();
	bouw.schaakjes( boStelling );
	BoStelling gotBoStelling = dbs.get( boStelling );
	gotBoStelling.setSchaak( gen.isSchaak( boStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Illegaal ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "b2" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "a2" )
		.aanZet( Wit )
		.resultaat( Remise )
		.build();
	bouw.schaakjes( boStelling );
	gotBoStelling = dbs.get( boStelling );
	gotBoStelling.setSchaak( gen.isSchaak( boStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
	
	gotBoStelling.setAanZet( Zwart );
	gotBoStelling = dbs.get( gotBoStelling );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Illegaal ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "f3" )
		.zk( "h4" )
		.s3( "g3" )
		.s4( "a2" )
		.aanZet( Wit )
		.schaak( false )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.schaakjes( boStelling );
	gotBoStelling = dbs.get( boStelling );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Illegaal ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 0 ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "g7" )
		.s4( "h8" )
		.aanZet( Wit )
		.schaak( false )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.schaakjes( boStelling );
	
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( Illegaal ) );
}
// @@NOG Uitzoeken waarom ditt niet werkt
//1@Test
public void testPassSchaakjes()
{
	// Voor deze test moet je HOU_STELLINGEN_BIJ in VMStellingIterator op true zetten
	getConfig().switchConfig( Config.PIPOKDK, false );
	bouw.reportNewPass( "Reserveren schijfruimte", DO_PRINT );

	bouw.reportNewPass( "Markeren illegale stellingen", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::isIllegaal, "rw" );
	assertThat(vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 2 ) );
	
	bouw.reportNewPass( "Markeren illegale stellingen", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::schaakjes, "rw" );
	assertThat(vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 2 ) );

	Map<String, BoStelling> stellingLookup = new HashMap<>();
	for ( BoStelling boStelling : vmStellingIterator.getStellingen() )
	{
		StringBuilder sb = new StringBuilder()
			.append( Gen.veldToAlfa( boStelling.getWk() ) )
			.append( Gen.veldToAlfa( boStelling.getZk() ) )
			.append( Gen.veldToAlfa( boStelling.getS3() ) )
			.append( boStelling.getAanZet().getAfko() );
		stellingLookup.put( sb.toString(), boStelling );
	}
	Transformator transformator = getDbs().getTransformator();
	for ( int wk : WK_VELD_RANGE )
	{
		for ( int zk : STUK_VELD_RANGE )
		{
			for ( int s3 : STUK_VELD_RANGE )
			{
				StringBuilder sb = new StringBuilder()
					.append( Gen.veldToAlfa( transformator.vmStellingWkToBoStellingWk( wk ) ) )
					.append( Gen.veldToAlfa( transformator.vmStellingWkToBoStellingWk( zk ) ) )
					.append( Gen.veldToAlfa( transformator.vmStellingWkToBoStellingWk( s3 ) ) )
					.append( Wit.getAfko() );
				BoStelling boStellingLookup = stellingLookup.get( sb.toString() );
				assertThat("For key: " + sb.toString(), boStellingLookup, is( notNullValue() ) );
			}
		}
	}
}
void checkTellingen()
{
	int [][] tellingen = vmStellingIterator.getTellingen();
	//getBouw().printAlles( tellingen);
	bouw.telAlles( DO_PRINT );
	
	// @@NOG Als je alle tests runt, gaat deze fout, maar als je alleen TestBouw runt gaattie goed !!??
	// Expected: is [[<1337361>, <0>, <1284079>, <0>], [<1048551>, <0>, <1572889>, <0>]]
    // but:     was [[<1163970>, <0>, <1457470>, <0>], [<875160>, <0>, <1746280>, <0>]]

	//getBouw().printAlles( getVmStellingIterator().getTellingen() );
	//assertThat( vmStellingIterator.getTellingen(), is( tellingen ) );
	
}
@Test
public void testIsMat()
{
	if ( DO_PRINT )
	{
		LOG.info( "methode testIsMat" );
	}

	// Je moet nu eerst de illegale stellingen markeren anders denkt genZPerStuk
	// dat in het schaak gaan staan een legale zet is
	markeerIllegaal();
	dbs.open( "rw" ); // Want hij wordt gesloten in dbs.Pass
	BoStelling boStelling;
	
	BoStelling matStelling = BoStelling.alfaBuilder()
		.wk( "f3" )
		.zk( "h3" )
		.s3( "g3" )
		.s4( "a2" )
		.aanZet( Zwart )
		.schaak( true )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( matStelling );
	BoStelling gotBoStelling = dbs.get( matStelling );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( Verloren ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 1 ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "c1" )
		.zk( "a1" )
		.s3( "g1" )
		.s4( "e1" )
		.aanZet( Wit)
		.schaak( true )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 0 ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "c1" )
		.zk( "a1" )
		.s3( "c6" )
		.s4( "b1" )
		.aanZet( Wit)
		.schaak( true )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 0 ) );

	matStelling = BoStelling.alfaBuilder()
		.wk( "c1" )
		.zk( "a1" )
		.s3( "a5" )
		.s4( "b1" )
		.aanZet( Zwart )
		.schaak( true )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( matStelling );
	gotBoStelling = dbs.get( matStelling );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Verloren ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 1 ) );
}
private void markeerIllegaal()
{
	bouw.illegaleStellingen = new ArrayList<>();
	bouw.stellingenMetSchaak = new ArrayList<>();
	bouw.matStellingen  = new ArrayList<>();
	bouw.passNumber = 0;
	
	bouw.reportNewPass( "Reserveren schijfruimte", DO_PRINT );
//	dbs.create();

	bouw.reportNewPass( "Markeren illegale stellingen", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::isIllegaal, "rw" );
	checkTellingen();
	
	bouw.reportNewPass( "Markeren schaakjes", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::schaakjes, "rw" );
	checkTellingen();
}

@Test
public void testMarkeer()
{
	config.switchConfig( Config.PIPOKDKT );
	if ( DO_PRINT )
	{
		LOG.info( "methode testMarkeer" );
	}
	bouw.pass_0( DO_PRINT );
	dbs.open( "rw" );
	BoStelling boStellingVan;
	BoStelling gotBoStelling;
	
	// Matstelling: WK=0 ZK=2 S3=0 S4=20 AanZet=W, de ZT geeft schaak & mat
	boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c2" )
		.s3( "a1" )
		.s4( "b3" )
		.aanZet( Zwart )
		.schaak( false )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Gewonnen ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 2 ) );
	
	boStellingVan = BoStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "c2" )
		.s3( "a2" )
		.s4( "b3" )
		.aanZet( Wit )
		.schaak( false )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Verloren ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 2 ) );

	boStellingVan = BoStelling.alfaBuilder()
		.wk( "c3" )
		.zk( "c1" )
		.s3( "c4" )
		.s4( "c1" )
		.aanZet( Wit )
		.schaak( false )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	// Ze zijn allemaal remise, behalve de laatste, die is Verloren
	List<BoStelling> gegenereerdeZetten = gen.genereerZetten( boStellingVan );
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		boStellingNaar.setResultaat( Remise );
		boStellingNaar.setAantalZetten( 0 );
		dbs.put( boStellingNaar );
	}
	gegenereerdeZetten.get(  3 ).setResultaat( Gewonnen );
	gegenereerdeZetten.get(  3 ).setAantalZetten( 2 );
	dbs.put( gegenereerdeZetten.get( 3 ) );
	
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 0 ) );

	boStellingVan = BoStelling.alfaBuilder()
	.wk( "c3" )
	.zk( "c1" )
	.s3( "c4" )
	.s4( "c1" )
	.aanZet( Wit )
	.schaak( false )
	.resultaat( Remise )
	.aantalZetten( 0 )
	.build();
	// Ze zijn allemaal remise, behalve de laatste, die is Verloren
	gegenereerdeZetten = gen.genereerZetten( boStellingVan );
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		boStellingNaar.setResultaat( Remise );
		boStellingNaar.setAantalZetten( 0 );
		dbs.put( boStellingNaar );
	}
	gegenereerdeZetten.get(  3 ).setResultaat( Gewonnen );
	gegenereerdeZetten.get(  3 ).setAantalZetten( 2 );
	dbs.put( gegenereerdeZetten.get( 3 ) );
	
	gegenereerdeZetten.get(  5 ).setResultaat( Verloren );
	gegenereerdeZetten.get(  5 ).setAantalZetten( 5 );
	dbs.put( gegenereerdeZetten.get( 5 ) );
	
	gegenereerdeZetten.get(  7 ).setResultaat( Gewonnen );
	gegenereerdeZetten.get(  7 ).setAantalZetten( 3 );
	dbs.put( gegenereerdeZetten.get( 3 ) );
	
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Gewonnen ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 6 ) );
}
//@Test
public void buildDeDatabase()
{
	bouw.bouwDatabase();
}


}
