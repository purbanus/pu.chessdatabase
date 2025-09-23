package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Bouw;
import pu.chessdatabase.util.Vector;

@SpringBootTest
public class TestDbs
{
@Autowired private Dbs dbs;
@Autowired private Bouw bouw;
@Autowired private VM vm;

@BeforeEach
public void setup()
{
	dbs.Name( "Pipo" );
	dbs.Create(); // Doet ook Open, dus initialiseert de tabellen
}
@AfterEach
public void destroy()
{
	dbs.delete();
}
@Test
public void testResultaatRange()
{
	assertThat( dbs.ResultaatRange.getMinimum(), is( 0 ) );
	assertThat( dbs.ResultaatRange.getMaximum(), is( 3 ) );
}

@Test
public void testClearTellers()
{
	dbs.ClearTellers();
	for ( int x = dbs.ResultaatRange.getMinimum(); x < dbs.ResultaatRange.getMaximum() + 1; x++ )
	{
		assertThat( dbs.Rpt[x], is( 0L ) );
	}
	assertThat( dbs.RptTeller, is( 0 ) );
}
private void doReport( long [] aReportArray )
{
	System.out.println( "Dit is een ReportProc" );
}
public void doReport2( long [] aReportArray )
{
	System.out.println( aReportArray );
}
@Test
public void testSetReport()
{
	dbs.SetReport( 5000, this::doReport );
	assertThat( dbs.RptFreq, is( 5000 ) );
	// Je kunt lambdas niet vergelijken, beha	lve misschien met Serializable; viond ik overdone
	//assertThat( dbs.RptProc, is( this::doReport ) );
}

@Test
public void testUpdateTellers()
{
	dbs.SetReport( 5000, this::doReport );
	dbs.Rpt = new long [] { 1L, 2L, 3L, 4L };
	dbs.UpdateTellers( ResultaatType.Gewonnen );
	assertThat( dbs.Rpt[ResultaatType.Gewonnen.ordinal()], is( 3L ) );
	assertThat( dbs.RptTeller, is( 1 ) );
}
/**
 * public static final int [] OktTabel = {
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   6,6,6,6,5,5,5,5
};
 */
@Test
public void testCreateTrfTabel()
{
	// Laten we beginnen in oktant 1. Alles is identiek behalve dat VMStelling maar 8 kolommen per rij heeft. 
	int oktant = 1;
	for ( int rij = 0; rij < 8; rij++ )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			assertThat( dbs.TrfTabel[oktant][kol + 16 * rij], is( kol + 8 * rij ) );
		}
	}
	// Oktant 2 is een spiegeling over de y-as
	oktant = 2;
	for ( int rij = 0; rij < 8; rij++ )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			Vector vector = new Vector( kol, rij );
			Vector resVector = dbs.MatrixTabel[oktant].multiply( vector );
			resVector = resVector.add( dbs.TranslatieTabel[oktant] );
			int oudVeld = kol + 16 * rij;
			int newVeld = resVector.get( 0 ) + 8 * resVector.get( 1 );
			//System.out.print( Integer.toHexString( oudVeld ) + "->" + Integer.toHexString( newVeld ) + " " );
			assertThat( dbs.TrfTabel[oktant][oudVeld], is( newVeld ) );
		}
		System.out.println();
	}
}
/*
0 -1 -2 -3 -4 -5 -6 -7 
8 7 6 5 4 3 2 1 
16 15 14 13 12 11 10 9 
24 23 22 21 20 19 18 17 
32 31 30 29 28 27 26 25 
40 39 38 37 36 35 34 33 
48 47 46 45 44 43 42 41 
56 55 54 53 52 51 50 49 

0->0 1->ffffffff 2->fffffffe 3->fffffffd 4->fffffffc 5->fffffffb 6->fffffffa 7->fffffff9 
10->8 11->7 12->6 13->5 14->4 15->3 16->2 17->1 
20->10 21->f 22->e 23->d 24->c 25->b 26->a 27->9 
30->18 31->17 32->16 33->15 34->14 35->13 36->12 37->11 
40->20 41->1f 42->1e 43->1d 44->1c 45->1b 46->1a 47->19 
50->28 51->27 52->26 53->25 54->24 55->23 56->22 57->21 
60->30 61->2f 62->2e 63->2d 64->2c 65->2b 66->2a 67->29 
70->38 71->37 72->36 73->35 74->34 75->33 76->32 77->31 

0->7 1->6 2->5 3->4 4->3 5->2 6->1 7->0 
10->f 11->e 12->d 13->c 14->b 15->a 16->9 17->8 
20->17 21->16 22->15 23->14 24->13 25->12 26->11 27->10 
30->1f 31->1e 32->1d 33->1c 34->1b 35->1a 36->19 37->18 
40->27 41->26 42->25 43->24 44->23 45->22 46->21 47->20 
50->2f 51->2e 52->2d 53->2c 54->2b 55->2a 56->29 57->28 
60->37 61->36 62->35 63->34 64->33 65->32 66->31 67->30 
70->3f 71->3e 72->3d 73->3c 74->3b 75->3a 76->39 77->38 

 */
//@Test
public void printTrfTabel()
{
	for ( int oktant = 1; oktant < dbs.OKTANTEN; oktant++ ) // @@NOG CHECK is die grens goed of moet het <= zijn
	{
		for ( int x = dbs.Veld.getMinimum(); x < dbs.Veld.getMaximum(); x++ )
		{
			System.out.print( Integer.toHexString( dbs.TrfTabel[oktant][x] ) + " " );
		}
		System.out.println();
	}
}
@Test
public void testCardinaliseer()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( false )
		.build();
	VMStelling vmStelling = dbs.Cardinaliseer( boStelling );
	//System.out.println( boStelling );
	//System.out.println( vmStelling );
	VMStelling newVmStelling = VMStelling.builder()
		.wk( 0x01 )
		.zk( 0x11 )
		.s3( 0x00 )
		.s4( 0x19 )
		.aanZet( false )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x06 )
		.zk( 0x26 )
		.s3( 0x07 )
		.s4( 0x27 )
		.aanZet( false )
		.build();
	vmStelling = dbs.Cardinaliseer( boStelling );
	//System.out.println( boStelling );
	//System.out.println( vmStelling );
	newVmStelling = VMStelling.builder()
		.wk( 0x01 )
		.zk( 0x11 )
		.s3( 0x00 )
		.s4( 0x10 )
		.aanZet( false )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
}
@Test
public void testPut()
{
//	VMillegaal      = 0x0FF;
//	VMremise        = 0x000;
//	VMschaak        = 0x080;
//	VerliesOffset   = 0x080;
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( false )
		.resultaat( ResultaatType.Illegaal )
		.aantalZetten( 0 )
		.schaak( false )
		.build();
	dbs.Put( boStelling );
	
	BoStelling newBoStelling = dbs.Get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( ResultaatType.Illegaal ) );
	assertThat( newBoStelling.getAantalZetten(), is( 0 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	assertThat( newBoStelling, is( boStelling ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( false )
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 0 )
		.schaak( false )
		.build();
	dbs.Put( boStelling );
	
	newBoStelling = dbs.Get( boStelling );
	assertThat( newBoStelling, is( boStelling ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( false )
		.resultaat( ResultaatType.Gewonnen )
		.aantalZetten( 13 )
		.schaak( false )
		.build();
	dbs.Put( boStelling );
	
	newBoStelling = dbs.Get( boStelling );
	assertThat( newBoStelling, is( boStelling ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( false )
		.resultaat( ResultaatType.Verloren )
		.aantalZetten( 27 )
		.schaak( false )
		.build();
	dbs.Put( boStelling );
	
	newBoStelling = dbs.Get( boStelling );
	assertThat( newBoStelling, is( boStelling ) );
	newBoStelling = dbs.Get( boStelling );
	assertThat( newBoStelling, is( boStelling ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( false )
		.resultaat( ResultaatType.Remise )
		.aantalZetten( 27 )
		.schaak( true )
		.build();
	dbs.Put( boStelling );
	
	newBoStelling = dbs.Get( boStelling );
	//assertThat( newBoStelling, is( boStelling ) );
	assertThat( newBoStelling.getResultaat(), is( ResultaatType.Remise ) );
	assertThat( newBoStelling.getAantalZetten(), is( 0 ) );
	assertThat( newBoStelling.isSchaak(), is( true ) );

}
@Test
public void testGet()
{
	// Is hierboven al flink getest
}
@Test
public void testGetDirect()
{
	// Is hierboven al flink getest
}
@Test
public void testFreeRecord()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( false )
		.resultaat( ResultaatType.Verloren )
		.aantalZetten( 27 )
		.schaak( false )
		.build();
	dbs.Put( boStelling );
	dbs.FreeRecord( boStelling );
	// Dit is verder in TestVM al getest
}
@Test
public void testName()
{
	dbs.Name( "Mamaloe" );
	assertThat( dbs.DbsNaam, is( "Mamaloe" ) );
}
@Test
public void testCreate()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testOpen()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testClose()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testDelete()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testPass34()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x11 )
		.s4( 0x13 )
		.aanZet( false )
		.resultaat( ResultaatType.Gewonnen )
		.aantalZetten( 13 )
		.schaak( false )
		.build();
	VMStelling vmStelling = dbs.Cardinaliseer( boStelling );
	dbs.Pass34( boStelling, vmStelling, bouw::isIllegaal );
	dbs.flush();
	vmStelling.setAanZet( false );
	Page page = vm.GetPage( vmStelling, false );
	assertThat( TestHelper.isAll( page.getPage(), (byte)0xff ), is( true ) );
	
	vmStelling.setAanZet( true );
	page = vm.GetPage( vmStelling, false );
	assertThat( TestHelper.isAll( page.getPage(), (byte)0xff ), is( true ) );
}
void set0x0b( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( ResultaatType.Gewonnen );
	aBoStelling.setAantalZetten( 0x0b );
	dbs.Put( aBoStelling );
}
@Test
public void testMarkeerWitPass()
{
	dbs.markeerWitPass( this::set0x0b );
	dbs.flush();
	
	// De even pagina's moeten nu allmaal 0x0b zijn, oftewel alle pagina's met wit aan zet
	VMStelling vmStelling = new VMStelling();
	Page page;
	for ( int wk = 0; wk < 10; wk++)
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			vmStelling.setAanZet( false );
			page = vm.GetPage( vmStelling, false );
			assertThat( TestHelper.isAll( page.getPage(), (byte)0x0b ), is( true ) );
			vmStelling.setAanZet( true );
			page = vm.GetPage( vmStelling, false );
			assertThat( TestHelper.isAll( page.getPage(), (byte)0x00 ), is( true ) );
		}
	}
}

void set0x11( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( ResultaatType.Gewonnen );
	aBoStelling.setAantalZetten( 0x11 );
	dbs.Put( aBoStelling );
}
@Test
public void testMarkeerZwartPass()
{
	dbs.markeerZwartPass( this::set0x11 );
	dbs.flush();
	
	// De even pagina's moeten nu allmaal 0x11 zijn, oftewel alle pagina's met zwart aan zet
	VMStelling vmStelling = new VMStelling();
	Page page;
	for ( int wk = 0; wk < 10; wk++ )
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			vmStelling.setAanZet( false );
			page = vm.GetPage( vmStelling, false );
			assertThat( TestHelper.isAll( page.getPage(), (byte)0x00 ), is( true ) );
			vmStelling.setAanZet( true );
			page = vm.GetPage( vmStelling, false );
			assertThat( TestHelper.isAll( page.getPage(), (byte)0x11 ), is( true ) );
		}
	}
}
void set0x34( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( ResultaatType.Gewonnen );
	aBoStelling.setAantalZetten( 0x34 );
	dbs.Put( aBoStelling );
}
@Test
public void testMarkeerWitEnZwartPass()
{
	dbs.markeerWitEnZwartPass( this::set0x34 );
	dbs.flush();
	
	// Alle pagina's moeten nu 0x34 zijn
	VMStelling vmStelling = new VMStelling();
	Page page;
	vmStelling.setAanZet( true );
	for ( int wk = 0; wk < 10; wk++ )
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			vmStelling.setAanZet( false );
			page = vm.GetPage( vmStelling, false );
			assertThat( TestHelper.isAll( page.getPage(), (byte)0x34 ), is( true ) );
			vmStelling.setAanZet( true );
			page = vm.GetPage( vmStelling, false );
			assertThat( TestHelper.isAll( page.getPage(), (byte)0x34 ), is( true ) );
		}
	}
}
@Test
public void testPass()
{
	dbs.Pass( PassType.MarkeerWit, this::set0x0b );
	dbs.Pass( PassType.MarkeerZwart, this::set0x0b );
	dbs.Pass( PassType.WitEnZwart, this::set0x0b );
	// Die markeer-methodes zijn hierboven al getest, we checken hier alleen of de aanroepjes goed gaan
}






/*
public static final int [] OktTabel = {
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   6,6,6,6,5,5,5,5
};
*/
/*
 7  8  9  a  b  c  d  e 0 0 0 0 0 0 0 0 
 f 10 11 12 13 14 15 16 0 0 0 0 0 0 0 0 
17 18 19 1a 1b 1c 1d 1e 0 0 0 0 0 0 0 0 
1f 20 21 22 23 24 25 26 0 0 0 0 0 0 0 0 
27 28 29 2a 2b 2c 2d 2e 0 0 0 0 0 0 0 0 
2f 30 31 32 33 34 35 36 0 0 0 0 0 0 0 0 
37 38 39 3a 3b 3c 3d 3e 0 0 0 0 0 0 0 0 
3f 40 41 42 43 44 45 

38 37 36 35 34 33 32 31 0 0 0 0 0 0 0 0 40 3f 3e 3d 3c 3b 3a 39 0 0 0 0 0 0 0 0 48 47 46 45 44 43 42 41 0 0 0 0 0 0 0 0 50 4f 4e 4d 4c 4b 4a 49 0 0 0 0 0 0 0 0 58 57 56 55 54 53 52 51 0 0 0 0 0 0 0 0 60 5f 5e 5d 5c 5b 5a 59 0 0 0 0 0 0 0 0 68 67 66 65 64 63 62 61 0 0 0 0 0 0 0 0 70 6f 6e 6d 6c 6b 6a 
3f 37 2f 27 1f 17 f 7 0 0 0 0 0 0 0 0 40 38 30 28 20 18 10 8 0 0 0 0 0 0 0 0 41 39 31 29 21 19 11 9 0 0 0 0 0 0 0 0 42 3a 32 2a 22 1a 12 a 0 0 0 0 0 0 0 0 43 3b 33 2b 23 1b 13 b 0 0 0 0 0 0 0 0 44 3c 34 2c 24 1c 14 c 0 0 0 0 0 0 0 0 45 3d 35 2d 25 1d 15 d 0 0 0 0 0 0 0 0 46 3e 36 2e 26 1e 16 
3f 37 2f 27 1f 17 f 7 0 0 0 0 0 0 0 0 3e 36 2e 26 1e 16 e 6 0 0 0 0 0 0 0 0 3d 35 2d 25 1d 15 d 5 0 0 0 0 0 0 0 0 3c 34 2c 24 1c 14 c 4 0 0 0 0 0 0 0 0 3b 33 2b 23 1b 13 b 3 0 0 0 0 0 0 0 0 3a 32 2a 22 1a 12 a 2 0 0 0 0 0 0 0 0 39 31 29 21 19 11 9 1 0 0 0 0 0 0 0 0 38 30 28 20 18 10 8 
38 37 36 35 34 33 32 31 0 0 0 0 0 0 0 0 30 2f 2e 2d 2c 2b 2a 29 0 0 0 0 0 0 0 0 28 27 26 25 24 23 22 21 0 0 0 0 0 0 0 0 20 1f 1e 1d 1c 1b 1a 19 0 0 0 0 0 0 0 0 18 17 16 15 14 13 12 11 0 0 0 0 0 0 0 0 10 f e d c b a 9 0 0 0 0 0 0 0 0 8 7 6 5 4 3 2 1 0 0 0 0 0 0 0 0 0 ffffffff fffffffe fffffffd fffffffc fffffffb fffffffa 
7 8 9 a b c d e 0 0 0 0 0 0 0 0 ffffffff 0 1 2 3 4 5 6 0 0 0 0 0 0 0 0 fffffff7 fffffff8 fffffff9 fffffffa fffffffb fffffffc fffffffd fffffffe 0 0 0 0 0 0 0 0 ffffffef fffffff0 fffffff1 fffffff2 fffffff3 fffffff4 fffffff5 fffffff6 0 0 0 0 0 0 0 0 ffffffe7 ffffffe8 ffffffe9 ffffffea ffffffeb ffffffec ffffffed ffffffee 0 0 0 0 0 0 0 0 ffffffdf ffffffe0 ffffffe1 ffffffe2 ffffffe3 ffffffe4 ffffffe5 ffffffe6 0 0 0 0 0 0 0 0 ffffffd7 ffffffd8 ffffffd9 ffffffda ffffffdb ffffffdc ffffffdd ffffffde 0 0 0 0 0 0 0 0 ffffffcf ffffffd0 ffffffd1 ffffffd2 ffffffd3 ffffffd4 ffffffd5 
0 8 10 18 20 28 30 38 0 0 0 0 0 0 0 0 ffffffff 7 f 17 1f 27 2f 37 0 0 0 0 0 0 0 0 fffffffe 6 e 16 1e 26 2e 36 0 0 0 0 0 0 0 0 fffffffd 5 d 15 1d 25 2d 35 0 0 0 0 0 0 0 0 fffffffc 4 c 14 1c 24 2c 34 0 0 0 0 0 0 0 0 fffffffb 3 b 13 1b 23 2b 33 0 0 0 0 0 0 0 0 fffffffa 2 a 12 1a 22 2a 32 0 0 0 0 0 0 0 0 fffffff9 1 9 11 19 21 29 

 */
}
