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
public class TestBouw
{
@Autowired private Bouw bouw;
@Autowired private Dbs dbs;

@Test
public void testConstructor()
{
	assertThat( bouw.PassNchanges, is( true ) );
	assertThat( bouw.PassNr, is( 0 ) );
}
@Test
public void testInzReport()
{
	bouw.inzReport();

	assertThat( bouw.RptPrev[0], is( 0L ) );
	assertThat( bouw.RptPrev[1], is( 0L ) );
	assertThat( bouw.RptPrev[2], is( 5 * bouw.MEG ) );
	assertThat( bouw.RptPrev[3], is( 0L ) );
	
	assertThat( bouw.RptTot[0], is( 0L ) );
	assertThat( bouw.RptTot[1], is( 0L ) );
	assertThat( bouw.RptTot[2], is( 5 * bouw.MEG ) );
	assertThat( bouw.RptTot[3], is( 0L ) );
}
@Test
public void testSetTotals()
{
	long [] totalsArray = new long [] { 1L, 2L, 3L, 4L };
	bouw.setTotals( totalsArray );
	for ( int x = 0; x < 4; x++ )
	{
		assertThat( bouw.RptTot[x], is( totalsArray[x] ) );
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
	bouw.RptTot  = new long[] { 5L, 6L, 7L, 9L };
	bouw.RptPrev = new long[] { 1L, 2L, 3L, 4L };
	long [] totals = new long[] { 6L, 8L, 10L, 13L };
	bouw.reportNewPass( "Pipo Koeie" );
	for ( int x = 0; x < 4; x++ )
	{
		if ( x == ResultaatType.Remise.ordinal() ) //Remise = 2
		{
			assertThat( bouw.RptPrev[x], is( -34L ) ); 
			assertThat( bouw.RptTot [x], is( -27L ) ); 
		}
		else
		{
			assertThat( bouw.RptTot[x], is( totals[x] ) );
			assertThat( bouw.RptTot[x], is( totals[x] ) );
		}
	}
}
@Test
public void testIsIllegaal()
{
	dbs.Name( "Pipo" );
	dbs.Create();
	dbs.Open();
	
	//IsGeomIllegaal wordt al getest in TestGen. Wenemen nu een willekeurige illegale stelling
	BoStelling boStelling;
	boStelling = BoStelling.builder()
		.WK( 0x05 )
		.ZK( 0x05 )
		.S3( 0x15 )
		.S4( 0x16 )
		.build();
	bouw.isIllegaal( boStelling );
	
	// Dit is VMStelling(WK=2, ZK=2, s3=10, s4=9, AanZet=false)
	dbs.Get( boStelling );
	assertThat( boStelling.getResultaat(), is( ResultaatType.Illegaal ) );
	
	bouw.delete();

}

}

