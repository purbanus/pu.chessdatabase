package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static pu.chessdatabase.bo.Kleur.*;
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

import pu.chessdatabase.dbs.Dbs;
import pu.chessdatabase.dbs.VM;
import pu.chessdatabase.dbs.VMStellingIterator;

import lombok.Data;

@SpringBootTest
@Data
public class TestBouw
{
private static final String DATABASE_NAME = "dbs/Pipo";
private static final boolean DO_PRINT = false;
@Autowired private Bouw bouw;
@Autowired private Dbs dbs;
@Autowired private VM vm;
@Autowired private Gen gen;
@Autowired private VMStellingIterator vmStellingIterator
;
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
	assertThat( vmStellingIterator.getReportFrequency(), is( 64 * 64 * 64 * 2 ) ); 
}
//@Test
public void testTelAlles()
{
	if ( DO_PRINT )
	{
		System.out.println( "methode testTelAlles\n" );
	}
	getConfig().switchConfig( "KDK" );

	bouw.pass_0( DO_PRINT );
	dbs.open();
	bouw.telAndPrintAlles( DO_PRINT );
}

@Test
public void testIsIllegaal()
{
	//IsGeomIllegaal wordt al getest in TestGen. We nemen nu een willekeurige illegale stelling
	BoStelling boStelling;
	BoStelling gotBoStelling;
	boStelling = BoStelling.builder()
		.wk( 0x05 )
		.zk( 0x05 )
		.s3( 0x15 )
		.s4( 0x16 )
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
//@Test
public void testPassSchaakjes()
{
	// BELANGRIJK!! Als je deze runt moet je in VMStellingIterator HOU_STELLINGEN_BIJ = true doen
	getConfig().switchConfig( "TESTKDK", false );
	bouw.reportNewPass( "Reserveren schijfruimte\n", DO_PRINT );
	dbs.create();

	bouw.reportNewPass( "Markeren illegale stellingen", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::isIllegaal );
	assertThat(vmStellingIterator.getStellingen().size(), is( 10 * 64 * 64 ) ); // NIet * 2 want we hebben alleen witstellingen
	
	bouw.reportNewPass( "Markeren illegale stellingen", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::schaakjes );
	assertThat(vmStellingIterator.getStellingen().size(), is( 10 * 64 * 64 ) ); // NIet * 2 want we hebben alleen witstellingen

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
	for ( int wk : vm.wkVeldRange )
	{
		for ( int zk : vm.stukVeldRange )
		{
			for ( int s3 : vm.stukVeldRange )
			{
				StringBuilder sb = new StringBuilder()
					.append( Gen.veldToAlfa( Dbs.CVT_WK  [wk] ) )
					.append( Gen.veldToAlfa( Dbs.CVT_STUK[zk] ) )
					.append( Gen.veldToAlfa( Dbs.CVT_STUK[s3] ) )
					.append( Wit.getAfko() );
				BoStelling boStellingLookup = stellingLookup.get( sb.toString() );
				assertThat("For key: " + sb.toString(), boStellingLookup, is( notNullValue() ) );
			}
		}
	}
}
private void markeerIllegaal()
{
	bouw.illegaleStellingen = new ArrayList<>();
	bouw.stellingenMetSchaak = new ArrayList<>();
	bouw.matStellingen  = new ArrayList<>();
	bouw.passNumber = 0;
	
	bouw.reportNewPass( "Reserveren schijfruimte\n", DO_PRINT );
	dbs.create();

	bouw.reportNewPass( "Markeren illegale stellingen", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::isIllegaal );
	checkTellingen();
	
	bouw.reportNewPass( "Markeren schaakjes", DO_PRINT );
	dbs.pass( MarkeerWit, bouw::schaakjes );
	checkTellingen();
}
void checkTellingen()
{
	int [][] tellingen = vmStellingIterator.getTellingen();
	bouw.telAlles( DO_PRINT );
	assertThat( vmStellingIterator.getTellingen(), is( tellingen ) );
}
@Test
public void testIsMat()
{
	if ( DO_PRINT )
	{
		System.out.println( "methode testIsMat\n" );
	}

	// Je moet nu eerst de illegale stellingen markeren anders denkt genZPerStuk
	// dat in het schaak gaan staan een legale zet is
	markeerIllegaal();
	dbs.open(); // Want hij wordt gesloten in dbs.Pass
	BoStelling boStelling;
	
	boStelling = BoStelling.builder()
		.wk( 0x25 )
		.zk( 0x27 )
		.s3( 0x26 )
		.s4( 0x10 )
		.aanZet( Zwart )
		.schaak( true )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	BoStelling gotBoStelling = dbs.get( boStelling );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( Verloren ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 1 ) );

	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x06 )
		.s4( 0x04 )
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
	
	/*WK=2 ZK=0 S3=52 S4=1 AanZet=W
.. .. .. .. .. .. .. .. 
.. .. .. .. .. .. .. .. 
.. .. WD .. .. .. .. .. 
.. .. .. .. .. .. .. .. 
.. .. .. .. .. .. .. .. 
.. .. .. .. .. .. .. .. 
.. .. .. .. .. .. .. .. 
ZK ZT WK .. .. .. .. .. 
	 */
	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x52 )
		.s4( 0x01 )
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

	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x50 )
		.s4( 0x01 )
		.aanZet( Zwart )
		.schaak( true )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( Verloren ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 1 ) );
}
@Test
public void testMarkeer()
{
	if ( DO_PRINT )
	{
		System.out.println( "methode testMarkeer\n" );
	}
	bouw.pass_0( DO_PRINT );
	dbs.open();
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
