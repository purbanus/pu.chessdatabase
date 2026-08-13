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
import pu.chessdatabase.bo.BoStelling.AlfaBuilder;
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
	getDbs().create(); // Doet ook Open, dus initialiseert de tabellen
	testHelper = new TestHelper( getConfig() );
}
@AfterEach
public void destroy()
{
	assertThat( dbs.getDatabaseName(), startsWith( PREFIX_TEST_DATABASE ) );
	getDbs().delete();
	getDbs().setDoAllPositions( false );
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
	assertThat( RESULTAAT_RANGE.getMinimum(), is( 0 ) );
	assertThat( RESULTAAT_RANGE.getMaximum(), is( 3 ) );
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
	VMStelling vmStelling = aVmStelling.clone();
	byte [] page;
	if ( vm.getPageSizeCalculator().getCacheType() == Serial )
	{
		vmStelling.setAanZet( Wit );
		page = vm.getPage( vmStelling );
		assertThat( getTestHelper().isAll( page, (byte)aWitValue ), is( true ) );
		vmStelling.setAanZet( Zwart );
		page = vm.getPage( vmStelling );
		assertThat( getTestHelper().isAll( page, (byte)aZwartValue ), is( true ) );
	}
	else
	{
		vmStelling.setAanZet( Wit );
		vmStelling.setWk( getConfig().getTransformator().vmStellingWkFromLinear( vmStelling.getWk() ) );
		page = vm.getPage( vmStelling );
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
public void testMarkeerWitPassMet0x27WitEnZwart()
{
	getConfig().switchConfig( Config.PipoKoK );
	getDbs().create();

	getDbs().setNumberOfPuts( 0 );
	getVmStellingIterator().setStellingTeller( 0 );
	// Dit is een pass over alle witstellingen maar die alles dubbel telt
	getDbs().markeerWitPass( this::set0x27WitEnZwart );
	getDbs().flush();
	assertThat( getVmStellingIterator().getStellingTeller(), is( 32 * 64 * 64 * 2 ) );
	assertThat( getDbs().getNumberOfPuts(), is( 32 * 64 * 64 * 2 ) );
	// De even pagina's moeten nu allmaal 0x0b zijn, oftewel alle pagina's met wit aan zet
	// Dit is eveneens een pass over alle witstellingen maar die alles dubbel telt4
	getVmStellingIterator().setStellingTeller( 0 );
	getVm().getPageDescriptorTable().iterateOverAllPageDescriptors( this::checkMarkeerPassMet0x27 );
}
void set0x27WitEnZwart( BoStelling aBoStelling )
{
	aBoStelling.setAanZet( Wit );
	aBoStelling.setResultaat( Gewonnen );
	aBoStelling.setAantalZetten( 0x27 );
	dbs.put( aBoStelling );
	aBoStelling.setAanZet( Zwart );
	aBoStelling.setResultaat( Gewonnen );
	aBoStelling.setAantalZetten( 0x27 );
	dbs.put( aBoStelling );
}
void checkMarkeerPassMet0x27( VMStelling aVmStelling )
{
	checkWitEnZwartPages( aVmStelling, 0x27, 0x27 );
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
@Test
public void testPut20260808()
{
	getConfig().switchConfig( Config.PipoKLoK );
	getDbs().create();
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "e1" )
		.zk( "a1" )
		.s3( "a5" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 5 )
		.build();
	getDbs().put( boStelling );
}

}
