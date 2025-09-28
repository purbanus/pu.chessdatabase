package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.PassType;
import pu.chessdatabase.dal.ResultaatType;

@SpringBootTest
public class TestBouw
{
@Autowired private Bouw bouw;
@Autowired private Dbs dbs;

@BeforeEach
public void setup()
{
	dbs.Name( "Pipo" );
	dbs.Create();
}
@AfterEach
public void destroy()
{
	dbs.delete();
}
@Test
public void testConstructor()
{
	assertThat( bouw.passNchanges, is( true ) );
	assertThat( bouw.passNr, is( 0 ) );
}
@Test
public void testInzReport()
{
	bouw.inzReport();

	assertThat( bouw.rptPrev[0], is( 0L ) );
	assertThat( bouw.rptPrev[1], is( 0L ) );
	assertThat( bouw.rptPrev[2], is( 5 * bouw.MEG ) );
	assertThat( bouw.rptPrev[3], is( 0L ) );
	
	assertThat( bouw.rptTot[0], is( 0L ) );
	assertThat( bouw.rptTot[1], is( 0L ) );
	assertThat( bouw.rptTot[2], is( 5 * bouw.MEG ) );
	assertThat( bouw.rptTot[3], is( 0L ) );
}
@Test
public void testSetTotals()
{
	long [] totalsArray = new long [] { 1L, 2L, 3L, 4L };
	bouw.setTotals( totalsArray );
	for ( int x = 0; x < 4; x++ )
	{
		assertThat( bouw.rptTot[x], is( totalsArray[x] ) );
	}
}
@Test
public void testShowThisPass()
{
	long [] totalsArray = new long [] { 1L, 2L, 3L, 4L };
	bouw.showThisPass( totalsArray );
	assertThat( totalsArray[ResultaatType.Remise.ordinal()], is( -7L ) );
}
@Test
public void testReportNewPass()
{
	dbs.Rpt = new long[] { 10L, 11L, 12L, 13L };
	bouw.rptTot  = new long[] { 5L, 6L, 7L, 9L };
	bouw.rptPrev = new long[] { 1L, 2L, 3L, 4L };
	long [] totals = new long[] { 6L, 8L, 10L, 13L };
	bouw.reportNewPass( "Pipo Koeie" );
	for ( int x = 0; x < 4; x++ )
	{
		if ( x == ResultaatType.Remise.ordinal() ) //Remise = 2
		{
			assertThat( bouw.rptPrev[x], is( -34L ) ); 
			assertThat( bouw.rptTot [x], is( -27L ) ); 
		}
		else
		{
			assertThat( bouw.rptTot[x], is( totals[x] ) );
			assertThat( bouw.rptTot[x], is( totals[x] ) );
		}
	}
}
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
		.aanZet( Wit )
		.build();
	bouw.isIllegaal( boStelling );
	
	// Dit is VMStelling(WK=2, ZK=2, s3=10, s4=9, AanZet=false)
	dbs.Get( boStelling );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Illegaal ) );
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
		.aanZet( Wit )
		.resultaat( ResultaatType.Remise )
		.build();
	bouw.schaakjes( boStelling );
	dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( true ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Remise ) );
	
	boStelling.setAanZet( Zwart );
	dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( false ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Illegaal ) );
}
private void markeerIllegaal()
{
	bouw.illegaalStellingen = new ArrayList<>();
	bouw.stellingenMetSchaak = new ArrayList<>();
	bouw.illegaalStellingen  = new ArrayList<>();
	
	bouw.passNr = 0;
	dbs.SetReport( dbs.DFT_RPT_FREQ, bouw::showThisPass );
	dbs.ClearTellers();
	bouw.inzReport();
	bouw.reportNewPass( "Reserveren schijfruimte" );

	bouw.reportNewPass( "Illegaal" );
	dbs.Pass( PassType.MarkeerWit, bouw::isIllegaal );

	bouw.reportNewPass( "Schaakjes" );
	dbs.Pass( PassType.MarkeerWit, bouw::schaakjes );

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
	dbs.Open(); // Want hij wordt gesloten in dbs.Pass
	BoStelling boStelling;
	
	boStelling = BoStelling.builder()
		.wk( 0x25 )
		.zk( 0x27 )
		.s3( 0x26 )
		.s4( 0x10 )
		.aanZet( Zwart )
		.schaak( true )
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( false ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Verloren ) );
	assertThat( boStelling.getAantalZetten(), is( 1 ) );

	//WK=2 ZK=0 S3=6 S4=4 AanZet=W
	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x06 )
		.s4( 0x04 )
		.aanZet( Wit)
		.schaak( true )
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( true ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Remise ) );
	assertThat( boStelling.getAantalZetten(), is( 0 ) );
	
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
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( true ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Remise ) );
	assertThat( boStelling.getAantalZetten(), is( 0 ) );

	boStelling = BoStelling.builder()
		.wk( 0x02 )
		.zk( 0x00 )
		.s3( 0x50 )
		.s4( 0x01 )
		.aanZet( Zwart )
		.schaak( true )
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 0 )
		.build();
	bouw.isMat( boStelling );
	dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( false ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Verloren ) );
	assertThat( boStelling.getAantalZetten(), is( 1 ) );
}
@Test
public void testTelAlles()
{
	bouw.pass_0();
	dbs.Open();
	bouw.telAlles();
	System.out.println( bouw.rptTot );
	for ( int x = 0; x < 4; x++ )
	{
		System.out.println( bouw.rptTot[x] );
	}
	/* Illegaal: 2039130
       Gewonnen:       0
       Remise    3201556
       Verloren     2194
	 */
	bouw.printAllesMetKleur();
}
@Test
public void testMarkeer()
{
	bouw.pass_0();
	dbs.Open();
	BoStelling boStelling;
	
	// Matstelling: WK=0 ZK=2 S3=0 S4=20 AanZet=W, de ZT geeft schaak & mat
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x21 )
		.aanZet( Zwart )
		.schaak( false )
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 0 )
		.build();
	bouw.markeer( boStelling );
	boStelling = dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( false ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Gewonnen ) );
	assertThat( boStelling.getAantalZetten(), is( 2 ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x10 )
		.s4( 0x21 )
		.aanZet( Wit )
		.schaak( false )
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 0 )
		.build();
	bouw.markeer( boStelling );
	boStelling = dbs.Get( boStelling );
	assertThat( boStelling.isSchaak(), is( false ) );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Verloren ) );
	assertThat( boStelling.getAantalZetten(), is( 2 ) );
}
//@Test
public void buildDeDatabase()
{
	bouw.bouwDatabase();
}


}
