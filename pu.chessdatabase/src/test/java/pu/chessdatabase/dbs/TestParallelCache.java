package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static pu.chessdatabase.bo.Kleur.Wit;
import static pu.chessdatabase.bo.Kleur.Zwart;
import static pu.chessdatabase.dbs.CacheType.Parallel;
import static pu.chessdatabase.dbs.Constants.PREFIX_TEST_DATABASE;
import static pu.chessdatabase.dbs.Lokatie.InRam;
import static pu.chessdatabase.dbs.Lokatie.OpSchijf;
import static pu.chessdatabase.dbs.VM.MAX_STUK;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Kleur;
import pu.services.MatrixFormatter;
import pu.services.StopWatch;

import lombok.Data;

@SpringBootTest
@Data
public class TestParallelCache
{
@Autowired private VM vm;
@Autowired private Config config;
private MockCache cache;
private PageSizeCalculator pageSizeCalculator = new PageSizeCalculator( Parallel );
private TestHelper testHelper;
String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( Config.PIPOKDKT );
	vm.setPageSizeCalculator( getPageSizeCalculator() );
	vm.open( "rw" );
	cache = new MockCache( vm.getCache() );
	testHelper = new TestHelper( pageSizeCalculator, getConfig().getAantalStukken() );
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
	byte [] page = getTestHelper().createPageWithAll( aValue );
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
//private void writeDatabaseWithAll( byte aValue )
//{
//	int index = 0;
//	for ( CacheEntry cacheEntry : getCache().getCacheEntries() )
//	{
//		writePageWithAll( index, aValue, aValue );
//		index++;
//	}
//}
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
	assertThat( getCache().getCacheSize(), is( ParallelCache.CACHE_SIZE ) );
}
@Test
public void testInitializeCache()
{
	//getCache().initializeCache(); // gebeurt al in de setup, via vm.open)_
	assertThat( getCache().getCacheEntries().size(), is( ParallelCache.CACHE_SIZE ) );
	long address = 0L;
	int index = 0;
	for ( CacheEntry cacheEntry : getCache().getCacheEntries() )
	{
		PageDescriptor pageDescriptor = PageDescriptor.builder()
			.cacheNummer( index )
			.schijfAdres( address )
			.waar( OpSchijf )
			.build();
		assertThat( cacheEntry.getPageDescriptor(), is( pageDescriptor ) );
		assertThat( getTestHelper().isAllZero( cacheEntry.getPage() ), is( true ) );
		assertThat( cacheEntry.getGeneratie(), is( 0L ) );
		assertThat( cacheEntry.isVuil(), is( false ) );
		
		address += getPageSizeCalculator().getPageSize( getConfig().getAantalStukken() );
		index++;
	}
}
// @Test
// public void testGetFreeCacheEntry()
// Bestaat niet in ParallelCache
@Test
public void testGetRawPageData()
{
	long pageNumber = 5L;
	int cacheNumber = 5;
	byte value = (byte)0x80;

	writePageWithAll( pageNumber, cacheNumber, value );

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	getCache().getRawPageData( pageDescriptor );
	assertThat( getTestHelper().isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}
// @Test
// public void testPageIn() throws IOException
// Bestaat niet in ParallelCache

//===================================================================================================
// Metodes uit AbstractCache
//===================================================================================================

@Test
public void testGetPageSize()
{
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwitched
	getConfig().switchConfig( Config.PIPOKDK );
	assertThat( vm.getCache().getPageSize(), is( 64 * 64 * 2 ) );
	getConfig().switchConfig( Config.PIPOKDKT );
	assertThat( vm.getCache().getPageSize(), is( 64 * 64 * 64 * 2 ) );
	getConfig().switchConfig( Config.PIPOKDKTT );
	assertThat( vm.getCache().getPageSize(), is( 64 * 64 * 64 * 64 * 2  ) );
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
	int cacheNumber = 3;
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
	long pageNumber = 9L;
	int cacheNumber = 9;
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
	assertThat( getTestHelper().isAll( page, value ), is( true ) );
	assertThat( pageDescriptor.getCacheNummer(), is( 9 ) );
	assertThat( getCache().isVuil( pageDescriptor ), is( false ) );
}
@Test
public void testGetSetPage()
{
	long pageNumber = 3L;
	int cacheNumber = 3;
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
	int cacheNumber = 7;
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
	assertThat( getTestHelper().isAll( page, value ), is( true ) );
	// De pageDescriptor is NIET veranderd
	assertThat( pageDescriptor.getCacheNummer(), is( cacheNumber ) );
	assertThat( getCache().isVuil( pageDescriptor ), is( false ) );
}
@Test
public void testIsSetVuil()
{
	long pageNumber = 3L;
	int cacheNumber = 3;

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
	int cacheNumber = 3;
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
	int cacheNumber = 3;
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
	long pageNumber = 5L;
	int cacheNumber = 5;
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
	VMStelling vmStelling = VMStelling.alfaBuilder()
		.wk( "e1" )
		.zk( "b7" )
		.s3( "a1" )
		.s4( "h1" )
		.s5( "b2" )
		.aanZet( Wit )
		.build();
	// Hier niet de lokale cache gebruiken maaT die uit VM, want die is geconfigSwithed
	getConfig().switchConfig( Config.PIPOKDK );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 6272 ) );
	getConfig().switchConfig( Config.PIPOKDKT );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling ), is( 401415 ) );
	getConfig().switchConfig( Config.PIPOKDKTT );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 25690569 ) );
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
	for ( int zk = 0; zk < MAX_STUK; zk++ )
	{
		vmStelling.setZk( zk );
		for ( Kleur aanZet : Kleur.values() )
		{
			vmStelling.setAanZet( aanZet );
			for ( int s3 = 0; s3 < MAX_STUK; s3++ )
			{
				vmStelling.setS3( s3 );
				assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( pos ) );
				pos++;
			}
		}
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
	for ( int zk = 0; zk < MAX_STUK; zk++ )
	{
		vmStelling.setZk( zk );
		for ( Kleur aanZet : Kleur.values() )
		{
			vmStelling.setAanZet( aanZet );
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
	for ( int zk = 0; zk < MAX_STUK; zk++ )
	{
		vmStelling.setZk( zk );
		for ( Kleur aanZet : Kleur.values() )
		{
			vmStelling.setAanZet( aanZet );
			for ( int s3 = 0; s3 < MAX_STUK; s3++ )
			{
				vmStelling.setS3( s3 );
				for ( int s4 = 0; s4 < MAX_STUK; s4++ )
				{
					vmStelling.setS4( s4 );
					for ( int s5 = 0; s5 < MAX_STUK; s5++ )
					{
						vmStelling.setS5( s5 );
//						if ( pos == 1 && vm.getCache().getPositionWithinPage( vmStelling ) == 64 )
//						{
//							System.out.println( "gottit" );
//						}
						assertThat( vm.getCache().getPositionWithinPage( vmStelling ), is( pos ) );
						pos++;
					}
				}
			}
		}
	}
	System.out.println( "testGetAllPositionsWithinPage5Stukken duurde " + timer.getElapsedMs() );
}
@Test
public void testGetSetData()
{
	long pageNumber = 9L;
	int cacheNumber = 9;
	byte value = (byte)0x25;    //  37 dec
	byte newValue = (byte)0x77; // 119 dec
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
public void testBug20260708()
{
	VMStelling vmStelling = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "c1" )
		.s4( "g7" )
		.s5( "h8" )
		.aanZet( Wit )
		.build();
	assertThat( getCache().getPositionWithinPage( vmStelling ), is( 516278 ) );
}
@Test
public void testGetDataWithNoGetPage()
{
	long pageNumber = 7L;
	int cacheNumber = 7;
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
		.cacheNummer( 5 )
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
	byte [] page = getTestHelper().createPageWithAllOnes();
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		// Je moet hier cachenummber=0 gebruiken want verderop staat de WK op a1 en dat is cachenummer 0
		.cacheNummer( 0 )
		.schijfAdres( 0L )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 2156 )
		.pageDescriptor( pageDescriptor )
		.page( page )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	// Deze doet er eigenlijk niet zoveel toe. Er worden bij de flush nog wat extra enen 
	// naar de database g7eschreven, dat is alles
	pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( 5 )
		.schijfAdres( 4096L )
		.build();
	cacheEntry = CacheEntry.builder()
		.generatie( 9500 )
		.pageDescriptor( pageDescriptor )
		.page( page )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	getCache().flush();

	// Lees de eerste twee paginas en check of die allemaal 1 zijn
	// N.B. Bij de parallelCache wordt steeds de cacheEntry 0 uitgelezen, zowel voor Wit als voor Zwart.
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( Wit )
		.build();
	PageDescriptor newPageDescriptor = vm.getPageDescriptor( vmStelling );
	byte [] newPage = getCache().getPage( newPageDescriptor );
	assertThat( getTestHelper().isAllOne( newPage ), is( true ) );
	
	vmStelling.setAanZet( Zwart );
	newPage = getCache().getPage( newPageDescriptor );
	assertThat( getTestHelper().isAllOne( newPage ), is( true ) );
}

}
