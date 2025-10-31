package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.PassType;

import lombok.Data;

@SpringBootTest
@Data
public class TestBouw
{
private static final String DATABASE_NAME = "dbs/Pipo";
@Autowired private Bouw bouw;
@Autowired private Dbs dbs;
@Autowired private Gen gen;
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
	assertThat( bouw.passNr, is( 0 ) );
}
//@Test
//public void testInzReport()
//{
//	bouw.inzReport();
//
//	assertThat( bouw.rptPrev[0], is( 0L ) );
//	assertThat( bouw.rptPrev[1], is( 0L ) );
//	assertThat( bouw.rptPrev[2], is( 5 * bouw.MEG ) );
//	assertThat( bouw.rptPrev[3], is( 0L ) );
//	
//	assertThat( bouw.rptTot[0], is( 0L ) );
//	assertThat( bouw.rptTot[1], is( 0L ) );
//	assertThat( bouw.rptTot[2], is( 5 * bouw.MEG ) );
//	assertThat( bouw.rptTot[3], is( 0L ) );
//}
//@Test
//public void testSetTotals()
//{
//	long [] totalsArray = new long [] { 1L, 2L, 3L, 4L };
//	bouw.setTotals( totalsArray );
//	for ( int x = 0; x < 4; x++ )
//	{
//		assertThat( bouw.rptTot[x], is( totalsArray[x] ) );
//	}
//}
//@Test
//public void testShowThisPass()
//{
//	long [] totalsArray = new long [] { 1L, 2L, 3L, 4L };
//	bouw.showThisPass( totalsArray );
//	assertThat( totalsArray[REMISE.ordinal()], is( -7L ) );
//}
//@Test
//public void testReportNewPass()
//{
//	dbs.report = new long[] { 10L, 11L, 12L, 13L };
//	bouw.rptTot  = new long[] { 5L, 6L, 7L, 9L };
//	bouw.rptPrev = new long[] { 1L, 2L, 3L, 4L };
//	long [] totals = new long[] { 6L, 8L, 10L, 13L };
//	bouw.reportNewPass( "Pipo Koeie" );
//	for ( int x = 0; x < 4; x++ )
//	{
//		if ( x == REMISE.ordinal() ) //Remise = 2
//		{
//			assertThat( bouw.rptPrev[x], is( -34L ) ); 
//			assertThat( bouw.rptTot [x], is( -27L ) ); 
//		}
//		else
//		{
//			assertThat( bouw.rptTot[x], is( totals[x] ) );
//			assertThat( bouw.rptTot[x], is( totals[x] ) );
//		}
//	}
//}
@Test
public void testIsIllegaal()
{
	//IsGeomIllegaal wordt al getest in TestGen. We nemen nu een willekeurige illegale stelling
	BoStelling boStelling;
	boStelling = BoStelling.builder()
		.wk( 0x05 )
		.zk( 0x05 )
		.s3( 0x15 )
		.s4( 0x16 )
		.aanZet( WIT )
		.build();
	bouw.isIllegaal( boStelling );
	
	// Dit is VMStelling(WK=2, ZK=2, s3=10, s4=9, AanZet=false)
	BoStelling gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( ILLEGAAL ) );
}
@Test
public void testSchaakjes()
{
	BoStelling boStelling;

	// T links geeft schaak
	boStelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( WIT )
		.resultaat( REMISE )
		.build();
	bouw.schaakjes( boStelling );
	BoStelling gotBoStelling = dbs.get( boStelling );
	gotBoStelling.setSchaak( gen.isSchaak( boStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( REMISE ) );
	
	gotBoStelling.setAanZet( ZWART );
	gotBoStelling = dbs.get( gotBoStelling );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( ILLEGAAL ) );
}
private void markeerIllegaal()
{
	// @@HIGH Check dat alle illegale stellingen zowel met wit als met zwart illegaal zijn
	bouw.illegaalStellingen = new ArrayList<>();
	bouw.stellingenMetSchaak = new ArrayList<>();
	bouw.matStellingen  = new ArrayList<>();
	
	bouw.passNr = 0;
//	dbs.setReport( dbs.DFT_RPT_FREQ, bouw::showThisPass );
	dbs.clearTellers();
//	bouw.inzReport();
//	bouw.reportNewPass( "Reserveren schijfruimte" );

//	bouw.reportNewPass( "Illegaal" );
	dbs.pass( PassType.MARKEER_WIT, bouw::isIllegaal );

//	bouw.reportNewPass( "Schaakjes" );
	dbs.pass( PassType.MARKEER_WIT, bouw::schaakjes );

//	dbs.SetReport( 100, bouw::showThisPass );
//	bouw.reportNewPass( "Matstellingen" );
//	dbs.Pass( PassType.MarkeerWit  , bouw::isMat );
//	dbs.Pass( PassType.MarkeerZwart, bouw::isMat );
}
@Test
public void testIsMat()
{
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
		.aanZet( ZWART )
		.schaak( true )
		.resultaat( REMISE )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	BoStelling gotBoStelling = dbs.get( boStelling );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( VERLOREN ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 1 ) );

	//WK=2 ZK=0 S3=6 S4=4 AanZet=W
	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x06 )
		.s4( 0x04 )
		.aanZet( WIT)
		.schaak( true )
		.resultaat( REMISE )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( REMISE ) );
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
		.aanZet( WIT)
		.schaak( true )
		.resultaat( REMISE )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.isSchaak(), is( true ) );
	assertThat( gotBoStelling.getResultaat(), is( REMISE ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 0 ) );

	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x50 )
		.s4( 0x01 )
		.aanZet( ZWART )
		.schaak( true )
		.resultaat( REMISE )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( VERLOREN ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 1 ) );
}
//@Test
public void testShowMatStellingen()
{
	bouw.pass_0();
	dbs.open();
	System.out.println( "MatMetWitAanZet" );
	System.out.println( bouw.matStellingenMetWit );
	System.out.println( "\n\n\nMatMetZwartAanZet" );
	System.out.println( bouw.matStellingenMetZwart );
}
//@Test
public void testTelAlles()
{
	getConfig().switchConfig( "KDK", false );

	bouw.pass_0();
	dbs.open();
	bouw.telAlles();
//	System.out.println( bouw.rptTot );
	for ( int x = 0; x < 4; x++ )
	{
//		System.out.println( bouw.rptTot[x] );
	}
	/* Illegaal: 2039130
       Gewonnen:       0
       Remise    3201556
       Verloren     2194
	 */
	bouw.printAllesMetKleur();
	System.out.println( "Illegale stellingen: " + bouw.illegaalStellingen );
}
@Test
public void testMarkeer()
{
	bouw.pass_0();
	dbs.open();
	BoStelling boStellingVan;
	BoStelling gotBoStelling;
	
	// Matstelling: WK=0 ZK=2 S3=0 S4=20 AanZet=W, de ZT geeft schaak & mat
	boStellingVan = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c2" )
		.s3( "a1" )
		.s4( "b3" )
		.aanZet( ZWART )
		.schaak( false )
		.resultaat( REMISE )
		.aantalZetten( 0 )
		.build();
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	gotBoStelling.setSchaak( gen.isSchaak( gotBoStelling ) );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( GEWONNEN ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 2 ) );
	
	boStellingVan = BoStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "c2" )
		.s3( "a2" )
		.s4( "b3" )
		.aanZet( WIT )
		.schaak( false )
		.resultaat( REMISE )
		.aantalZetten( 0 )
		.build();
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( VERLOREN ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 2 ) );

	boStellingVan = BoStelling.alfaBuilder()
		.wk( "c3" )
		.zk( "c1" )
		.s3( "c4" )
		.s4( "c1" )
		.aanZet( WIT )
		.schaak( false )
		.resultaat( REMISE )
		.aantalZetten( 0 )
		.build();
	// Ze zijn allemaal remise, behalve de laatste, die is Verloren
	List<BoStelling> gegenereerdeZetten = gen.genereerZetten( boStellingVan );
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		boStellingNaar.setResultaat( REMISE );
		boStellingNaar.setAantalZetten( 0 );
		dbs.put( boStellingNaar );
	}
	gegenereerdeZetten.get(  3 ).setResultaat( GEWONNEN );
	gegenereerdeZetten.get(  3 ).setAantalZetten( 2 );
	dbs.put( gegenereerdeZetten.get( 3 ) );
	
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( REMISE ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 0 ) );

	boStellingVan = BoStelling.alfaBuilder()
	.wk( "c3" )
	.zk( "c1" )
	.s3( "c4" )
	.s4( "c1" )
	.aanZet( WIT )
	.schaak( false )
	.resultaat( REMISE )
	.aantalZetten( 0 )
	.build();
	// Ze zijn allemaal remise, behalve de laatste, die is Verloren
	gegenereerdeZetten = gen.genereerZetten( boStellingVan );
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		boStellingNaar.setResultaat( REMISE );
		boStellingNaar.setAantalZetten( 0 );
		dbs.put( boStellingNaar );
	}
	gegenereerdeZetten.get(  3 ).setResultaat( GEWONNEN );
	gegenereerdeZetten.get(  3 ).setAantalZetten( 2 );
	dbs.put( gegenereerdeZetten.get( 3 ) );
	
	gegenereerdeZetten.get(  5 ).setResultaat( VERLOREN );
	gegenereerdeZetten.get(  5 ).setAantalZetten( 5 );
	dbs.put( gegenereerdeZetten.get( 5 ) );
	
	gegenereerdeZetten.get(  7 ).setResultaat( GEWONNEN );
	gegenereerdeZetten.get(  7 ).setAantalZetten( 3 );
	dbs.put( gegenereerdeZetten.get( 3 ) );
	
	bouw.markeer( boStellingVan );
	gotBoStelling = dbs.get( boStellingVan );
	assertThat( gotBoStelling.isSchaak(), is( false ) );
	assertThat( gotBoStelling.getResultaat(), is( GEWONNEN ) );
	assertThat( gotBoStelling.getAantalZetten(), is( 6 ) );
}
//@Test
public void buildDeDatabase()
{
	bouw.bouwDatabase();
}


}
