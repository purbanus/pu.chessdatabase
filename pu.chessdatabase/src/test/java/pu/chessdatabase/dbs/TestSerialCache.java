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
import static pu.chessdatabase.dbs.Lokatie.*;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;
import pu.services.MatrixFormatter;
import pu.services.StopWatch;

import lombok.Data;

@SpringBootTest
@Data
public class TestSerialCache
{
@Autowired private VM vm;
@Autowired private Config config;
private MockCache cache;
private TestHelper testHelper;

private String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	getConfig().setCacheType( Serial );
	getConfig().switchConfig( Config.PipoKDKT );
	getVm().open( "rw" );
	cache = new MockCache( vm.getCache() );
	testHelper = new TestHelper( getConfig() );
}
@AfterEach
public void destroy()
{
	assertThat( vm.getDatabaseName(), startsWith( PREFIX_TEST_DATABASE ) );
	vm.delete();
	config.switchConfig( savedConfigString );
}
PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
//@Test
public void showCache()
{
	MatrixFormatter matrixFormatter = new MatrixFormatter();
	matrixFormatter.setDefaultAlignment( MatrixFormatter.ALIGN_RIGHT );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 90 ) );
	matrixFormatter.addDetail( new String [] { "Number", "PD.Lokatie", "PD.cachenummer", "PD.Schijfadres", "Page, eerste 10", "Vuil", "Generatie" } );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 90 ) );
	int index = -1;
	for ( CacheEntry cacheEntry : getCache().getCacheEntries() )
	{
		index++;
		if ( cacheEntry == null )
		{
			matrixFormatter.addDetail( new String []{ 
				String.valueOf( index ), 
				"null", 
				"null", 
				"null", 
				"null", 
				"null", 
				"null", 
			} );
		}
		else
		{
			PageDescriptor pageDescriptor = cacheEntry.getPageDescriptor();
			byte [] page = cacheEntry.getPage();
			StringBuilder sb = new StringBuilder();
			for ( int y = 0; y < 10; y++ )
			{
				sb.append( page[y] ).append( " " );
			}
			if ( pageDescriptor == null )
			{
				matrixFormatter.addDetail( new String []{
					String.valueOf( index ), 
					"null", 
					"null", 
					"null", 
					sb.toString(), 
					String.valueOf( cacheEntry.isVuil() ), 
					String.valueOf( cacheEntry.getGeneratie() ) 
				} );
			}
			else
			{
				matrixFormatter.addDetail( new String []{ 
					String.valueOf( index ), 
					String.valueOf( pageDescriptor.getWaar() ), 
					String.valueOf( pageDescriptor.getCacheNummer() ), 
					String.valueOf( pageDescriptor.getSchijfAdres() ), 
					sb.toString(),
					String.valueOf( cacheEntry.isVuil() ), 
					String.valueOf( cacheEntry.getGeneratie() ) 
				} );
			}
		}
	}
	matrixFormatter.addHeader( StringUtils.repeat( '-', 90 ) );
	System.out.println( matrixFormatter.getOutput() );
}
@Test
public void testGetCacheSize()
{
	assertThat( getCache().getCacheSize(), is( SerialCache.CACHE_SIZE ) );
}
@Test
public void testInitializeCache()
{
	getConfig().switchConfig( Config.PipoKDKT );
	doTestInitalizeCache();
	getConfig().switchConfig( Config.PipoKLoK );
	doTestInitalizeCache();
}
void doTestInitalizeCache()
{
	assertThat( getCache().getCacheEntries().size(), is( SerialCache.CACHE_SIZE ) );
//	long address = 0L;
//	int index = 0;
	for ( CacheEntry cacheEntry : getCache().getCacheEntries() )
	{
		assertThat( cacheEntry.getPageDescriptor(), is( nullValue() ) );
		assertThat( getTestHelper().isAllZero( cacheEntry.getPage() ), is( true ) );
		assertThat( cacheEntry.getGeneratie(), is( 0L ) );
		assertThat( cacheEntry.isVuil(), is( false ) );

//		address += getPageSizeCalculator().getPageSize( getConfig().getAantalStukken() );
//		index++;
	}
}
@Test
public void testGetFreeCacheEntry()
{
	int index = 0;
	for ( CacheEntry cacheEntry : getCache().getCacheEntries() )
	{
		cacheEntry.setVuil( true );
		cacheEntry.setGeneratie( index + 100 );
		index++;
	}
	PageDescriptor pageDescriptor01 = PageDescriptor.builder()
		.cacheNummer( 1 )
		.build();
	PageDescriptor pageDescriptor10 = PageDescriptor.builder()
		.cacheNummer( 10 )
		.build();
	PageDescriptor pageDescriptor20 = PageDescriptor.builder()
		.cacheNummer( 20 )
		.build();
	PageDescriptor pageDescriptor25 = PageDescriptor.builder()
		.cacheNummer( 25 )
		.build();
	CacheEntry cacheEntry = getCache().getCacheEntry( pageDescriptor01 );
	cacheEntry.setVuil( false );
	cacheEntry.setGeneratie( 10 );
	assertThat( getCache().getFreeCacheEntry(), is( 1 ) );

	cacheEntry = getCache().getCacheEntry( pageDescriptor10 );
	cacheEntry.setVuil( false );
	cacheEntry.setGeneratie( 5 );
	assertThat( getCache().getFreeCacheEntry(), is( 10 ) );

	cacheEntry = getCache().getCacheEntry( pageDescriptor01 );
	cacheEntry.setVuil( true );
	cacheEntry = getCache().getCacheEntry( pageDescriptor10 );
	cacheEntry.setVuil( true );
	assertThat( getCache().getFreeCacheEntry(), is( 10 ) );

	cacheEntry = getCache().getCacheEntry( pageDescriptor20 );
	cacheEntry.setVuil( true );
	cacheEntry.setGeneratie( 3 );
	cacheEntry = getCache().getCacheEntry( pageDescriptor25 );
	cacheEntry.setVuil( true );
	cacheEntry.setGeneratie( 15 );
	assertThat( getCache().getFreeCacheEntry(), is( 20 ) );
}
@Test
public void testGetRawPageData()
{
	long pageNumber = 5L;
	int cacheNumber = 15;
	byte value = (byte)0x80;

	getTestHelper().writePageWithAll( getCache(), pageNumber, cacheNumber, value );

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	getCache().getRawPageData( pageDescriptor );
	assertThat( getTestHelper().isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}
@Test
public void testPageIn() throws IOException
{
	long pageNumber = 10L;
	int cacheNumber = 17;
	byte value = (byte)0x40;

	getTestHelper().writePageWithAll( getCache(), pageNumber, cacheNumber, value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	//showCache( vm );
	getCache().pageIn( pageDescriptor );
	assertThat( getTestHelper().isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}

//===================================================================================================
// Metodes uit AbstractCache
//===================================================================================================

@Test
public void testGetPageSize()
{
	// @@HIGH Dit nog geschikt maken voor pionnen
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwitched
	getConfig().switchConfig( Config.PipoKDK );
	assertThat( vm.getCache().getPageSize(), is( 64 ) );
	getConfig().switchConfig( Config.PipoKDKT );
	assertThat( vm.getCache().getPageSize(), is( 4096 ) );
	getConfig().switchConfig( Config.PipoKDKTT );
	assertThat( vm.getCache().getPageSize(), is( 262144 ) );
}
@Test
public void testGetDatabaseSize()
{
	// @@HIGH Dit nog geschikt maken voor pionnen
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwitched
	getConfig().switchConfig( Config.PipoKDK );
	assertThat( vm.getCache().getDatabaseSize(), is( 10 * 64 * 2 * 64L ) );
	getConfig().switchConfig( Config.PipoKDKT );
	assertThat( vm.getCache().getDatabaseSize(), is(10 * 64 * 2 * 64 * 64L ) );
	getConfig().switchConfig( Config.PipoKDKTT );
	assertThat( vm.getCache().getDatabaseSize(), is(10 * 64 * 2 * 64 * 64 * 64L ) );
}
@Test
public void testGetPage()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x60;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = getTestHelper().createPageWithAll( value );
	getCache().setPage( pageDescriptor, page );
	
	byte [] pageData = getCache().getPage( pageDescriptor );
	assertThat( getTestHelper().isAll( pageData, value ), is( true ) );
}
@Test
public void testGetPageFromDatabase()
{
	long pageNumber = 12L;
	int cacheNumber = 29;
	byte value = (byte)0x30;
	setCache( getCache() );
	
	getTestHelper().writePageWithAll( getCache(), pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( Wit )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	byte [] page = getCache().getPageFromDatabase( pageDescriptor );
	assertThat( getTestHelper().isAll( page, value ), is( true ) );
	// De pageDescriptor is veranderd, hij wijst nu naar cachenummer 0
	assertThat( pageDescriptor.getCacheNummer(), is( 0 ) );
	assertThat( getCache().isVuil( pageDescriptor ), is( false ) );
}
@Test
public void testGetSetPage()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x70;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = getTestHelper().createPageWithAll( value );
	getCache().setPage( pageDescriptor, page );
	
	byte [] gotPage = getCache().getPage( pageDescriptor );
	assertThat( getTestHelper().isAll( gotPage, value ), is( true ) );
	assertThat( gotPage, is( page ) );
}
@Test
public void testGetPageNotDirtyAndInRam()
{
	long pageNumber = 7L;
	int cacheNumber = 20;
	byte value = (byte)0x2f;

	getTestHelper().writePageWithAll( getCache(), pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( Wit )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	byte [] page = getCache().getPageFromDatabase( pageDescriptor );
	assertThat( getTestHelper().isAll( page, value ), is( true ) );
	// De pageDescriptor is NIET veranderd
	assertThat( pageDescriptor.getCacheNummer(), is( cacheNumber ) );
	assertThat( getCache().isVuil( pageDescriptor ), is( false ) );
}
@Test
public void testIsSetVuil()
{
	long pageNumber = 3L;
	int cacheNumber = 15;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	getCache().setVuil( pageDescriptor, false );
	assertThat( getCache().isVuil( pageDescriptor ), is( false ) );
	getCache().setVuil( pageDescriptor, true );
	assertThat( getCache().isVuil( pageDescriptor ), is( true ) );
}
@Test
public void testGetSetCacheEntry()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x40;
	long generatie = 215L;
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = getTestHelper().createPageWithAll(value );
	CacheEntry cacheEntry = CacheEntry.builder()
		.pageDescriptor( pageDescriptor )
		.page( page )
		.generatie( generatie )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	CacheEntry gotCacheEntry = getCache().getCacheEntry( pageDescriptor );
	assertThat( gotCacheEntry.isVuil(), is( true ) );
	assertThat( getTestHelper().isAll( gotCacheEntry.getPage(), value ), is( true ) );
	assertThat( gotCacheEntry.getGeneratie(), is( generatie ) );
	assertThat( gotCacheEntry.isVuil(), is( true ) );
}
@Test
public void testPutRawPageData()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x70;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = getTestHelper().createPageWithAll( value );
	getCache().setPage( pageDescriptor, page );
	getCache().putRawPageData( pageDescriptor );
	
	getCache().getRawPageData( pageDescriptor );
	assertThat( getTestHelper().isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}
@Test
public void testPageOut()
{
	long pageNumber = 15L;
	int cacheNumber = 15;
	byte value = (byte)0x60;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = getTestHelper().createPageWithAll( value );
	CacheEntry cacheEntry = CacheEntry.builder()
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( true )
		.generatie( 1 )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	//showCache( vm );
	getCache().pageOut( pageDescriptor );
	page = new byte [getCache().getPageSize()];
	getCache().getCacheEntry( pageDescriptor ).setPage( page );
	getCache().getRawPageData( pageDescriptor );
	assertThat( getTestHelper().isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}
@Test
public void testGetPositionWithinPage()
{
	// @@HIGH Dit nog geschikt maken voor pionnen
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.s5( 0x09 )
		.aanZet( Wit )
		.build();
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwithed
	getConfig().switchConfig( Config.PipoKDK );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 0 ) );
	getConfig().switchConfig( Config.PipoKDKT );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling ), is( 7 ) );
	getConfig().switchConfig( Config.PipoKDKTT );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 7 * 64 + 9 ) );
}
@Test
public void testGetAllPositionsWithinPage3Stukken()
{
	// @@HIGH Dit nog geschikt maken voor pionnen
	getConfig().switchConfig( Config.PipoKDK );
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x00 )
		.s4( 0x00 )
		.s5( 0x00 )
		.aanZet( Wit )
		.build();
	int pos = 0;
	for ( int s3 : STUK_VELD_RANGE )
	{
		vmStelling.setS3( s3 );
		assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( pos ) );
		pos++;
	}
}
@Test
public void testGetAllPositionsWithinPage4Stukken()
{
	// @@HIGH Dit nog geschikt maken voor pionnen
	getConfig().switchConfig( Config.PipoKDKT );
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x00 )
		.s4( 0x00 )
		.s5( 0x00 )
		.aanZet( Wit )
		.build();
	int pos = 0;
	for ( int s3 : STUK_VELD_RANGE )
	{
		vmStelling.setS3( s3 );
		for ( int s4 : STUK_VELD_RANGE )
		{
			vmStelling.setS4( s4 );
			assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( pos ) );
			pos++;
		}
	}
}
@Test
public void testGetAllPositionsWithinPage5Stukken()
{
	// @@HIGH Dit nog geschikt maken voor pionnen
	StopWatch timer = new StopWatch();
	getConfig().switchConfig( Config.PipoKDKTT );
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x00 )
		.s4( 0x00 )
		.s5( 0x00 )
		.aanZet( Wit )
		.build();
	int pos = 0;
	for ( int s3 : STUK_VELD_RANGE )
	{
		vmStelling.setS3( s3 );
		for ( int s4 : STUK_VELD_RANGE )
		{
			vmStelling.setS4( s4 );
			for ( int s5 : STUK_VELD_RANGE )
			{
				vmStelling.setS5( s5 );
//				if ( pos == 1 && vm.getCache().getPositionWithinPage( vmStelling ) == 64 )
//				{
//					System.out.println( "gottit" );
//				}
				assertThat( vm.getCache().getPositionWithinPage( vmStelling ), is( pos ) );
				pos++;
			}
		}
	}
	System.out.println( "testGetAllPositionsWithinPage5Stukken duurde " + timer.getElapsedMs() );
}

@Test
public void testGetSetData()
{
	long pageNumber = 9L;
	int cacheNumber = 20;
	byte value = (byte)0x25;
	byte newValue = (byte)0x77;
	int positionWithinPage = 10;

	getTestHelper().writePageWithAll( getCache(), pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = getCache().getPage( pageDescriptor ); 
	CacheEntry cacheEntry = CacheEntry.builder()
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( false )
		.generatie( 1 )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	getCache().setData( pageDescriptor, positionWithinPage, newValue );
	assertThat( getCache().getData( pageDescriptor, positionWithinPage ), is( newValue ) );
	
	VMStelling vmStelling = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "c1" )
		.s4( "g7" )
		.s5( "h8" )
		.aanZet( Wit )
		.build();
	getCache().setData( pageDescriptor, vmStelling, newValue );
	assertThat( getCache().getData( pageDescriptor, vmStelling ), is( newValue ) );
}
@Test

public void testGetDataWithNoGetPage()
{
	long pageNumber = 9L;
	int cacheNumber = 21;
	byte value = (byte)0x25;
	byte newValue = (byte)0x00;
	int positionWithinPage = 10;

	getTestHelper().writePageWithAll( getCache(), pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	// @@NOG Maar hier gebeurt toch wel een getPage????
	byte [] page = getCache().getCacheEntries().get( cacheNumber ).getPage();
	CacheEntry cacheEntry = CacheEntry.builder()
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( false )
		.generatie( 1 )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	getCache().setData( pageDescriptor, positionWithinPage, newValue );
	assertThat( getCache().getData( pageDescriptor, positionWithinPage ), is( newValue ) );
}
@Test
public void testFlushWithNothingChanged()
{
	getCache().flush();
	// @@>NOG
}
@Test
public void testFlushWithSomePagesPresentButNoneVuil()
{
	byte [] page = getTestHelper().createPageWithAllOnes();
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( 1 )
		.schijfAdres( 0L )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 2156 )
		.pageDescriptor( pageDescriptor )
		.page( page )
		.vuil( false )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( 15 )
		.schijfAdres( 4096L )
		.build();
	cacheEntry = CacheEntry.builder()
		.generatie( 9500 )
		.pageDescriptor( pageDescriptor )
		.page( page )
		.vuil( false )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	getCache().flush();
	// @@NOG testjes??
}
@Test
public void testFlushWithSomePagesPresentAndVuil()
{
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( Wit )
		.build();

	byte [] page = getTestHelper().createPageWithAllOnes();
	PageDescriptor pageDescriptor = vm.getPageDescriptor( vmStelling );
	pageDescriptor.setCacheNummer( 0 );
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 2156 )
		.pageDescriptor( pageDescriptor )
		.page( page )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );

	vmStelling.setAanZet( Zwart );
	pageDescriptor = vm.getPageDescriptor( vmStelling );
	pageDescriptor.setCacheNummer( 1 );
	cacheEntry = CacheEntry.builder()
		.generatie( 9500 )
		.pageDescriptor( pageDescriptor )
		.page( page )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	getCache().flush();

	// Lees de eerste twee paginas en check of die allemaal 1 zijn
	vmStelling.setAanZet( Wit );
	PageDescriptor newPageDescriptor = vm.getPageDescriptor( vmStelling );
	byte [] newPage = getCache().getPage( newPageDescriptor );
	assertThat( getTestHelper().isAllOne( newPage ), is( true ) );
	
	vmStelling.setAanZet( Zwart );
	newPage = getCache().getPage( newPageDescriptor );
	assertThat( getTestHelper().isAllOne( newPage ), is( true ) );
}

}
