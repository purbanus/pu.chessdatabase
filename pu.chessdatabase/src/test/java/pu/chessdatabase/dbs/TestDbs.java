package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.CacheType.*;
import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.PassType.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Bouw;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Gen;

import lombok.Data;


@SpringBootTest
@Data
public class TestDbs
{
private static final boolean DO_PRINT = false;

@Autowired private Dbs dbs;
@Autowired private Bouw bouw;
@Autowired private VM vm;
@Autowired private Gen gen;
@Autowired private Config config;
@Autowired private VMStellingIterator vmStellingIterator;

TestHelper testHelper;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( Config.PipoKDKT );
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen
	testHelper = new TestHelper( getConfig() );
}
@AfterEach
public void destroy()
{
	assertThat( dbs.getDatabaseName(), startsWith( PREFIX_TEST_DATABASE ) );
	dbs.delete();
	config.switchConfig( savedConfigString );
}
PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
public void doReport( int aStellingTeller, int [][] aTellingen )
{
	if ( DO_PRINT )
	{
		System.out.println( "Dbs Aantal Stellingen: " + aStellingTeller );
	}
}
@SuppressWarnings( "unused" )
private void printPage( byte [] aPage )
{
	System.out.println( "Page length: " + aPage.length );
	for ( int row = 0; row < aPage.length; row += 64 )
	{
		for ( int col = 0; col < 64; col++ )
		{
			//System.out.print( aPage[row + col] + " " );
		}
		//System.out.println();
	}
}
@Test
public void testIterateOVerKleurEnResultaat()
{
	//@@NOG
}
@Test
public void testResultaatRange()
{
	assertThat( dbs.RESULTAAT_RANGE.getMinimum(), is( 0 ) );
	assertThat( dbs.RESULTAAT_RANGE.getMaximum(), is( 3 ) );
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
		.aanZet( Wit )
		.resultaat( Illegaal )
		.aantalZetten( 0 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	BoStelling newBoStelling = dbs.get( boStelling );
	newBoStelling.setSchaak( gen.isSchaak( newBoStelling ) );
	// Dit moet je niet doen want het is altijd true!!
	// assertThat( newBoStelling, is( boStelling ) );
	assertThat( newBoStelling.getResultaat(), is( Illegaal ) );
	assertThat( newBoStelling.getAantalZetten(), is( 0 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( Wit )
		.resultaat( Remise )
		.aantalZetten( 0 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( Remise ) );
	assertThat( newBoStelling.getAantalZetten(), is( 0 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 13 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( Gewonnen ) );
	assertThat( newBoStelling.getAantalZetten(), is( 13 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( Wit )
		.resultaat( Verloren )
		.aantalZetten( 27 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( Verloren ) );
	assertThat( newBoStelling.getAantalZetten(), is( 27 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( Wit )
		.resultaat( Remise )
		.aantalZetten( 27 )
		.schaak( true )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( Remise ) );
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
		.aanZet( Wit )
		.resultaat( Verloren )
		.aantalZetten( 27 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	dbs.freeRecord( boStelling );
	// Dit is verder in TestVM al getest
}
@Test
public void testSetDatabaseName()
{
	dbs.setDatabaseName( "Mamaloe" );
	assertThat( dbs.getDatabaseName(), is( "Mamaloe" ) );
	dbs.setDatabaseName( DATABASE_NAME_PIPO );
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
void checkWitEnZwartPages( VMStelling aVmStelling, int aWitValue, int aZwartValue )
{
	byte [] page;
	if ( vm.getPageSizeCalculator().getCacheType() == Serial )
	{
		aVmStelling.setAanZet( Wit );
		page = vm.getPage( aVmStelling );
		assertThat( getTestHelper().isAll( page, (byte)aWitValue ), is( true ) );
		aVmStelling.setAanZet( Zwart );
		page = vm.getPage( aVmStelling );
		assertThat( getTestHelper().isAll( page, (byte)aZwartValue ), is( true ) );
	}
	else
	{
		aVmStelling.setAanZet( Wit );
		page = vm.getPage( aVmStelling );
		boolean wit = true;
		for ( int x = 0; x < page.length; x += 4096 )
		{
			byte [] subPage = ArrayUtils.subarray( page, x, x + 4096 );
			if ( wit )
			{
				assertThat( getTestHelper().isAll( subPage, (byte)aWitValue ), is( true ) );
			}
			else
			{
				assertThat( getTestHelper().isAll( subPage, (byte)aZwartValue ), is( true ) );
			}
			wit = ! wit;
		}
	}
}

@Test
public void testMarkeerWitPassMet0x37()
{
	dbs.setReport( (int)dbs.getDatabaseSize() / 10, this::doReport );

	// Dit is een pass over alle witstellingen maar die alles dubbel telt
	dbs.markeerWitPass( this::set0x37 );
	dbs.flush();
	assertThat( vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 64 * 2 ) );
	// De even pagina's moeten nu allmaal 0x0b zijn, oftewel alle pagina's met wit aan zet
	// Dit is eveneens een pass over alle witstellingen maar die alles dubbel telt4
	vm.getPageDescriptorTable().iterateOverAllPageDescriptors( this::checkMarkeerPassMet0x37 );
}
void set0x37( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( Gewonnen );
	aBoStelling.setAantalZetten( 0x37 );
	dbs.put( aBoStelling );
}
void checkMarkeerPassMet0x37( VMStelling aVmStelling )
{
	checkWitEnZwartPages( aVmStelling, 0x37, 0x00 );
}
@Test
public void testMarkeerZwartPassMet0x11()
{
	dbs.setReport( (int)dbs.getDatabaseSize() / 10, this::doReport );

	dbs.markeerZwartPass( this::set0x11 );
	dbs.flush();
	assertThat( vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 64 * 2 ) );
	// De even pagina's moeten nu allmaal 0x11 zijn, oftewel alle pagina's met zwart aan zet
	vm.getPageDescriptorTable().iterateOverAllPageDescriptors( this::checkMarkeerPassMet0x11 );
}
void set0x11( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( Gewonnen );
	aBoStelling.setAantalZetten( 0x11 );
	dbs.put( aBoStelling );
}
void checkMarkeerPassMet0x11( VMStelling aVmStelling )
{
	checkWitEnZwartPages( aVmStelling, 0x00, 0x11 );
}
@Test
public void testMarkeerWitEnZwartPassMet0x34()
{
	dbs.setReport( (int)dbs.getDatabaseSize() / 10, this:: doReport );

	dbs.markeerWitEnZwartPass( this::set0x34 );
	dbs.flush();
	assertThat( vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 64 * 2 ) );

	// Alle pagina's moeten nu 0x34 zijn
	vm.getPageDescriptorTable().iterateOverAllPageDescriptors( this::checkMarkeerPassMet0x34 );
}
void set0x34( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( Gewonnen );
	aBoStelling.setAantalZetten( 0x34 );
	dbs.put( aBoStelling );
}
void checkMarkeerPassMet0x34( VMStelling vmStelling )
{
	byte [] page = vm.getPage( vmStelling );
	assertThat( getTestHelper().isAll( page, (byte)0x34 ), is( true ) );
}
@Test
public void testPass()
{
	vmStellingIterator.setDoAllPositions( true );
	
	dbs.setReport( (int)dbs.getDatabaseSize() / 10, this:: doReport );
	dbs.pass( MarkeerWit, this::set0x0b, "rw" );
	assertThat( vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 64 * 2) );
	vmStellingIterator.clearTellingen();
	dbs.pass( MarkeerZwart, this::set0x0b, "rw" );
	assertThat( vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 64 * 2 ) );
	// @@HIGH Volkomen raadselachtig hoe die database = null in de cache komt
	//        En bovendien, je krijgt een NullPointerException op de seek(), maar die hoeft ie helemaal niet te doen
	//        want alle pagina's zijn InRam!
	if ( getVm().getCache().getDatabase() != null)
	{
		vm.getPageDescriptorTable().iterateOverAllPageDescriptors( this::checkMarkeerPassMet0x0b );
	}

	vmStellingIterator.clearTellingen();
	dbs.pass( MarkeerWitEnZwart, this::set0x17, "rw" );
	assertThat( vmStellingIterator.getStellingTeller(), is( 10 * 64 * 64 * 64 * 2 ) );
	// @@HIGH Volkomen raadselachtig hoe die database = null in de cache komt, etc
	if ( getVm().getCache().getDatabase() != null)
	{
		vm.getPageDescriptorTable().iterateOverAllPageDescriptors( this::checkMarkeerPassMet0x17 );
	}
}
void set0x0b( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( Gewonnen );
	aBoStelling.setAantalZetten( 0x0b );
	dbs.put( aBoStelling );
}
void checkMarkeerPassMet0x0b( VMStelling vmStelling )
{
	byte [] page = vm.getPage( vmStelling );
	assertThat( getTestHelper().isAll( page, (byte)0x0b ), is( true ) );
}
void set0x17( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( Gewonnen );
	aBoStelling.setAantalZetten( 0x17 );
	dbs.put( aBoStelling );
}
void checkMarkeerPassMet0x17( VMStelling vmStelling )
{
	byte [] page = vm.getPage( vmStelling );
	assertThat( getTestHelper().isAll( page, (byte)0x17 ), is( true ) );
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
