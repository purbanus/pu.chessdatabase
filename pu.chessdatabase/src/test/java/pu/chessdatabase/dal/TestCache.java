package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;
import pu.services.MatrixFormatter;

import lombok.Data;

@SpringBootTest
@Data
public class TestCache
{
@Autowired private VM vm;
@Autowired private Config config;

private static final String DATABASE_NAME = "dbs/Pipo";

private MockCache cache;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	vm.create();
	cache = new MockCache( vm.getCache() );
}
@AfterEach
public void destroy()
{
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	assertThat( vm.getDatabaseName(), startsWith( DATABASE_NAME ) );
	vm.delete();
	config.switchConfig( savedConfigString );
}

private void writePageWithAll( long aPageNumber, int aCacheNumber, byte aValue )
{
	byte [] page = TestHelper.createPageWithAll( aValue );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
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
public void testShowCache()
{
	MatrixFormatter matrixFormatter = new MatrixFormatter();
	matrixFormatter.setDefaultAlignment( MatrixFormatter.ALIGN_RIGHT );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 90 ) );
	matrixFormatter.addDetail( new String [] { "Number", "PD.Lokatie", "PD.cachenummer", "PD.Schijfadres", "Page, eerste 10", "Vuil", "Generatie" } );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 90 ) );
	int index = -1;
	for ( CacheEntry cacheEntry : getCache().getCache() )
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
public void testGetPageSize()
{
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwithed
	getConfig().switchConfig( "KDK" );
	assertThat( vm.getCache().getPageSize(), is( 64 ) );
	getConfig().switchConfig( "KDKT" );
	assertThat( vm.getCache().getPageSize(), is( 4096 ) );
	getConfig().switchConfig( "KDKTT" );
	assertThat( vm.getCache().getPageSize(), is( 262144 ) );
}
@Test
public void testInitializeCache()
{
	getCache().initializeCache();
	for ( CacheEntry cacheEntry : getCache().getCache() )
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
	for ( CacheEntry cacheEntry : getCache().getCache() )
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
public void testGetSetPage()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x70;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = TestHelper.createPageWithAll( value );
	getCache().setPage( pageDescriptor, page );
	
	byte [] gotPage = getCache().getPage( pageDescriptor );
	assertThat( TestHelper.isAll( gotPage, value ), is( true ) );
	assertThat( gotPage, is( page ) );
}
//@Test
//public void testGetPageData()
//{
//	long pageNumber = 3L;
//	int cacheNumber = 15;
//	byte value = (byte)0x60;
//
//	PageDescriptor pageDescriptor = PageDescriptor.builder()
//		.waar( Lokatie.OP_SCHIJF )
//		.cacheNummer( cacheNumber )
//		.schijfAdres( pageNumber * getCache().getPageSize() )
//		.build();
//	byte [] page = TestHelper.createPageWithAll( value );
//	getCache().setPage( pageDescriptor, page );
//	
//	byte [] pageData = getCache().getPageData( pageDescriptor );
//	assertThat( TestHelper.isAll( pageData, value ), is( true ) );
//}
@Test
public void testIsSetVuil()
{
	long pageNumber = 3L;
	int cacheNumber = 15;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
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
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = TestHelper.createPageWithAll( value );
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
public void testGetRawPageData()
{
	long pageNumber = 5L;
	int cacheNumber = 15;
	byte value = (byte)0x80;

	writePageWithAll( pageNumber, cacheNumber, value );

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	getCache().getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
}
@Test
public void testPutRawPageData()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x70;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = TestHelper.createPageWithAll( value );
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
		.waar( Lokatie.IN_RAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = TestHelper.createPageWithAll( value );
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
public void testPageIn() throws IOException
{
	long pageNumber = 10L;
	int cacheNumber = 17;
	byte value = (byte)0x40;

	writePageWithAll( pageNumber, cacheNumber, value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	//showCache( vm );
	getCache().pageIn( pageDescriptor );
	assertThat( TestHelper.isAll( getCache().getPage( pageDescriptor ), value ), is( true ) );
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
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	byte [] page = getCache().getPageFromDatabase( pageDescriptor );
	assertThat( TestHelper.isAll( page, value ), is( true ) );
	// De pageDescriptor is veranderd, hij wijst nu naar cachenummer 0
	assertThat( pageDescriptor.getCacheNummer(), is( 0 ) );
	assertThat( getCache().isVuil( pageDescriptor ), is( false ) );
}
@Test
public void testGetPageNotDirtyAndInRam()
{
	long pageNumber = 7L;
	int cacheNumber = 20;
	byte value = (byte)0x2f;

	writePageWithAll( pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	byte [] page = getCache().getPageFromDatabase( pageDescriptor );
	assertThat( TestHelper.isAll( page, value ), is( true ) );
	// De pageDescriptor is NIET veranderd
	assertThat( pageDescriptor.getCacheNummer(), is( cacheNumber ) );
	assertThat( getCache().isVuil( pageDescriptor ), is( false ) );
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
		.aanZet( WIT )
		.build();
	// Hier niet de lokale cache gebruiken maar die uit VM, want die is geconfigSwithed
	getConfig().switchConfig( "KDK" );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 0 ) );
	getConfig().switchConfig( "KDKT" );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling ), is( 7 ) );
	getConfig().switchConfig( "KDKTT" );
	assertThat( vm.getCache().getPositionWithinPage( vmStelling), is( 7 * 64 + 9 ) );
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
		.waar( Lokatie.IN_RAM )
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
	
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x02 )
		.s4( 0x66 )
		.s5( 0x71 )
		.aanZet( WIT )
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
		.waar( Lokatie.IN_RAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * getCache().getPageSize() )
		.build();
	byte [] page = getCache().getCache().get( 20 ).getPage();
	CacheEntry cacheEntry = CacheEntry.builder()
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( false )
		.generatie( 1 )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	//getCache().setData( pageDescriptor, positionWithinPage, newValue );
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
	byte [] page = TestHelper.createPageWithAllOnes();
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
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
		.waar( Lokatie.IN_RAM )
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
	byte [] page = TestHelper.createPageWithAllOnes();
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( 1 )
		.schijfAdres( 0L )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 2156 )
		.pageDescriptor( pageDescriptor )
		.page( page )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( 15 )
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
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( WIT )
		.build();
	PageDescriptor newPageDescriptor = vm.getPageDescriptor( vmStelling );
	byte [] newPage = getCache().getPage( newPageDescriptor );
	assertThat( TestHelper.isAllOne( newPage ), is( true ) );
	
	vmStelling.setAanZet( ZWART );
	newPage = getCache().getPage( newPageDescriptor );
	assertThat( TestHelper.isAllOne( newPage ), is( true ) );
}

}
