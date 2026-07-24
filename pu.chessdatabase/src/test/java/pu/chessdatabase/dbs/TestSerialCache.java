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
import static pu.chessdatabase.dbs.VM.*;

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
private PageSizeCalculator pageSizeCalculator = new PageSizeCalculator( Serial );
String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( Config.PIPOKDKT );
	vm.setPageSizeCalculator( pageSizeCalculator );
	vm.open( "rw" );
	cache = new MockCache( vm.getCache() );
}
@AfterEach
public void destroy()
{
	assertThat( vm.getDatabaseName(), startsWith( PREFIX_TEST_DATABASE ) );
	vm.delete();
	config.switchConfig( savedConfigString );
}

private void writePageWithAll( long aPageNumber, int aCacheNumber, byte aValue )
{
	byte [] page = TestHelper.createPageWithAll( getPageSizeCalculator(), config.getAantalStukken(), aValue );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( aCacheNumber )
		.schijfAdres( aPageNumber * getCache().getPageSize() )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 15 )
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	getCache().pageOut( pageDescriptor );
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
	//getCache().initializeCache(); // gebeurt al in de setup, via vm.open)_
	assertThat( getCache().getCacheEntries().size(), is( SerialCache.CACHE_SIZE ) );
	for ( CacheEntry cacheEntry : getCache().getCacheEntries() )
	{
		assertThat( cacheEntry.getPageDescriptor(), is( nullValue() ) );
		assertThat( TestHelper.isAllZero( cacheEntry.getPage() ), is( true ) );
		assertThat( cacheEntry.getGeneratie(), is( 0L ) );
		assertThat( cacheEntry.isVuil(), is( false ) );
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

	writePageWithAll( pageNumber, cacheNumber, value );

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	getCache().getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}
@Test
public void testPageIn() throws IOException
{
	long pageNumber = 10L;
	int cacheNumber = 17;
	byte value = (byte)0x40;

	writePageWithAll( pageNumber, cacheNumber, value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	//showCache( vm );
	getCache().pageIn( pageDescriptor );
	assertThat( TestHelper.isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}

//===================================================================================================
// Metodes uit AbstractCache
//===================================================================================================

@Test
public void testGetPageSize()
{
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwitched
	getConfig().switchConfig( Config.PIPOKDK );
	assertThat( vm.getCache().getPageSize(), is( 64 ) );
	getConfig().switchConfig( Config.PIPOKDKT );
	assertThat( vm.getCache().getPageSize(), is( 4096 ) );
	getConfig().switchConfig( Config.PIPOKDKTT );
	assertThat( vm.getCache().getPageSize(), is( 262144 ) );
}
@Test
public void testGetDatabaseSize()
{
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwitched
	getConfig().switchConfig( Config.PIPOKDK );
	assertThat( vm.getCache().getDatabaseSize(), is( 10 * 64 * 2 * 64L ) );
	getConfig().switchConfig( Config.PIPOKDKT );
	assertThat( vm.getCache().getDatabaseSize(), is(10 * 64 * 2 * 64 * 64L ) );
	getConfig().switchConfig( Config.PIPOKDKTT );
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
	byte [] page = TestHelper.createPageWithAll( getPageSizeCalculator(), config.getAantalStukken(), value );
	getCache().setPage( pageDescriptor, page );
	
	byte [] pageData = getCache().getPage( pageDescriptor );
	assertThat( TestHelper.isAll( pageData, value ), is( true ) );
}
@Test
public void testGetPageFromDatabase()
{
	long pageNumber = 12L;
	int cacheNumber = 29;
	byte value = (byte)0x30;
	setCache( getCache() );
	
	writePageWithAll( pageNumber, cacheNumber, value );
	
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
	assertThat( TestHelper.isAll( page, value ), is( true ) );
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
	byte [] page = TestHelper.createPageWithAll( getPageSizeCalculator(), config.getAantalStukken(), value );
	getCache().setPage( pageDescriptor, page );
	
	byte [] gotPage = getCache().getPage( pageDescriptor );
	assertThat( TestHelper.isAll( gotPage, value ), is( true ) );
	assertThat( gotPage, is( page ) );
}
@Test
public void testGetPageNotDirtyAndInRam()
{
	long pageNumber = 7L;
	int cacheNumber = 20;
	byte value = (byte)0x2f;

	writePageWithAll( pageNumber, cacheNumber, value );
	
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
	assertThat( TestHelper.isAll( page, value ), is( true ) );
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
	byte [] page = TestHelper.createPageWithAll(getPageSizeCalculator(), config.getAantalStukken(),  value );
	CacheEntry cacheEntry = CacheEntry.builder()
		.pageDescriptor( pageDescriptor )
		.page( page )
		.generatie( generatie )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	CacheEntry gotCacheEntry = getCache().getCacheEntry( pageDescriptor );
	assertThat( gotCacheEntry.isVuil(), is( true ) );
	assertThat( TestHelper.isAll( gotCacheEntry.getPage(), value ), is( true ) );
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
	byte [] page = TestHelper.createPageWithAll( getPageSizeCalculator(), config.getAantalStukken(), value );
	getCache().setPage( pageDescriptor, page );
	getCache().putRawPageData( pageDescriptor );
	
	getCache().getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
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
	byte [] page = TestHelper.createPageWithAll( getPageSizeCalculator(), config.getAantalStukken(), value );
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
	assertThat( TestHelper.isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}
@Test
public void testGetPositionWithinPage()
{
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.s5( 0x09 )
		.aanZet( Wit )
		.build();
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwithed
	getConfig().switchConfig( Config.PIPOKDK );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 0 ) );
	getConfig().switchConfig( Config.PIPOKDKT );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling ), is( 7 ) );
	getConfig().switchConfig( Config.PIPOKDKTT );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 7 * 64 + 9 ) );
}
@Test
public void testGetAllPositionsWithinPage3Stukken()
{
	getConfig().switchConfig( Config.PIPOKDK );
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x00 )
		.s4( 0x00 )
		.s5( 0x00 )
		.aanZet( Wit )
		.build();
	int pos = 0;
	for ( int s3 = 0; s3 < MAX_STUK; s3++ )
	{
		vmStelling.setS3( s3 );
		assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( pos ) );
		pos++;
	}
}
@Test
public void testGetAllPositionsWithinPage4Stukken()
{
	getConfig().switchConfig( Config.PIPOKDKT );
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x00 )
		.s4( 0x00 )
		.s5( 0x00 )
		.aanZet( Wit )
		.build();
	int pos = 0;
	for ( int s3 = 0; s3 < MAX_STUK; s3++ )
	{
		vmStelling.setS3( s3 );
		for ( int s4 = 0; s4 < MAX_STUK; s4++ )
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
	StopWatch timer = new StopWatch();
	getConfig().switchConfig( Config.PIPOKDKTT );
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x00 )
		.s4( 0x00 )
		.s5( 0x00 )
		.aanZet( Wit )
		.build();
	int pos = 0;
	for ( int s3 = 0; s3 < MAX_STUK; s3++ )
	{
		vmStelling.setS3( s3 );
		for ( int s4 = 0; s4 < MAX_STUK; s4++ )
		{
			vmStelling.setS4( s4 );
			for ( int s5 = 0; s5 < MAX_STUK; s5++ )
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

	writePageWithAll( pageNumber, cacheNumber, value );
	
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

	writePageWithAll( pageNumber, cacheNumber, value );
	
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
	byte [] page = TestHelper.createPageWithAllOnes( getPageSizeCalculator(), config.getAantalStukken() );
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

	byte [] page = TestHelper.createPageWithAllOnes(getPageSizeCalculator(), config.getAantalStukken() );
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
	assertThat( TestHelper.isAllOne( newPage ), is( true ) );
	
	vmStelling.setAanZet( Zwart );
	newPage = getCache().getPage( newPageDescriptor );
	assertThat( TestHelper.isAllOne( newPage ), is( true ) );
}

}
