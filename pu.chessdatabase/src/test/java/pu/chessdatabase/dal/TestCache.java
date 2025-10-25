package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;

import lombok.Data;

@SpringBootTest
@Data
public class TestCache
{
@Autowired private VM vm;
@Autowired private Config config;

private static final String DATABASE_NAME = "dbs/Pipo";

private Cache cache;
String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getName();
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	vm.create();
	cache = vm.getCache();
}
@AfterEach
public void destroy()
{
	assertThat( vm.getDatabaseName(), is( DATABASE_NAME ) );
	vm.delete();
	config.switchConfig( savedConfigString );
}

private void writePageWithAll( long aPageNumber, int aCacheNumber, byte aValue )
{
	Page page = TestHelper.createPageWithAll( aValue );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( aCacheNumber )
		.schijfAdres( aPageNumber * Cache.PAGE_SIZE )
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
@Test
public void testInitializeCache()
{
	for ( CacheEntry cacheEntry : getCache().getCache() )
	{
		assertThat( cacheEntry.getPageDescriptor(), is( nullValue() ) );
		assertThat( TestHelper.isAllZero( cacheEntry.getPage().data ), is( true ) );
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
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	Page page = TestHelper.createPageWithAll( value );
	getCache().setPage( pageDescriptor, page );
	
	Page gotPage = getCache().getPage( pageDescriptor );
	assertThat( TestHelper.isAll( gotPage, value ), is( true ) );
	assertThat( gotPage, is( page ) );
}
@Test
public void testGetPageData()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x60;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	Page page = TestHelper.createPageWithAll( value );
	getCache().setPage( pageDescriptor, page );
	
	byte [] pageData = getCache().getPageData( pageDescriptor );
	assertThat( TestHelper.isAll( pageData, value ), is( true ) );
}
@Test
public void testIsSetVuil()
{
	long pageNumber = 3L;
	int cacheNumber = 15;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
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
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	Page page = TestHelper.createPageWithAll( value );
	CacheEntry cacheEntry = CacheEntry.builder()
		.pageDescriptor( pageDescriptor )
		.page( page )
		.generatie( generatie )
		.vuil( true )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	CacheEntry gotCacheEntry = getCache().getCacheEntry( pageDescriptor );
	assertThat( cacheEntry.isVuil(), is( true ) );
	assertThat( TestHelper.isAll( cacheEntry.getPage(), value ), is( true ) );
	assertThat( cacheEntry.getGeneratie(), is( generatie ) );
	assertThat( cacheEntry.isVuil(), is( true ) );
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
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	getCache().getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( vm.getCache().getPageData( pageDescriptor ), value ), is( true ) );
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
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	Page page = TestHelper.createPageWithAll( value );
	getCache().setPage( pageDescriptor, page );
	getCache().putRawPageData( pageDescriptor );
	
	getCache().getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( vm.getCache().getPageData( pageDescriptor ), value ), is( true ) );
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
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	Page page = TestHelper.createPageWithAll( value );
	CacheEntry cacheEntry = CacheEntry.builder()
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( true )
		.generatie( 1 )
		.build();
	vm.getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	//showCache( vm );
	getCache().pageOut( pageDescriptor );
	getCache().getCacheEntry( pageDescriptor ).setPage( new Page() );
	getCache().getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( vm.getCache().getPageData( pageDescriptor ), value ), is( true ) );
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
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	//showCache( vm );
	getCache().pageIn( pageDescriptor );
	assertThat( TestHelper.isAll( vm.getCache().getPageData( pageDescriptor ), value ), is( true ) );
}
@Test
public void testGetPageFromDatabase()
{
	long pageNumber = 12L;
	int cacheNumber = 29;
	byte value = (byte)0x30;
	vm.setCache( getCache() );
	
	writePageWithAll( pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	Page page = getCache().getPageFromDatabase( pageDescriptor );
	assertThat( TestHelper.isAll( page.getData(), value ), is( true ) );
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
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	Page page = getCache().getPageFromDatabase( pageDescriptor );
	assertThat( TestHelper.isAll( page.getData(), value ), is( true ) );
	// De pageDescriptor is NIET veranderd
	assertThat( pageDescriptor.getCacheNummer(), is( cacheNumber ) );
	assertThat( vm.getCache().isVuil( pageDescriptor ), is( false ) );
}@
Test
public void testSetData()
{
	long pageNumber = 9L;
	int cacheNumber = 20;
	byte value = (byte)0x25;
	byte newValue = (byte)0x77;
	int positionWithinPage = 1000;

	writePageWithAll( pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	Page page = getCache().getPage( pageDescriptor ); 
	CacheEntry cacheEntry = CacheEntry.builder()
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( false )
		.generatie( 1 )
		.build();
	getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	getCache().setData( pageDescriptor, positionWithinPage, newValue );
	assertThat( getCache().getPageData( pageDescriptor )[positionWithinPage], is( newValue ) );
}
@Test
public void testFlushWithNothingChanged()
{
	vm.flush();
	// @@>NOG
}
@Test
public void testFlushWithSomePagesPresentButNoneVuil()
{
	Page page = TestHelper.createPageWithAllOnes();
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
	vm.getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
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
	vm.getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	vm.flush();
	// @@NOG testjes??
}
@Test
public void testFlushWithSomePagesPresentAndVuil()
{
	Page page = TestHelper.createPageWithAllOnes();
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
	vm.getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
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
	vm.getCache().setCacheEntry( pageDescriptor, cacheEntry );
	
	vm.flush();

	// @@NOG Lees de eerste twee paginas en check of die allemaal 1 zijn
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( WIT )
		.build();
	PageDescriptor newPageDescriptor = vm.getPageDescriptor( vmStelling );
	Page newPage = getCache().getPage( pageDescriptor );
	assertThat( TestHelper.isAllOne( newPage.getData() ), is( true ) );
	
	vmStelling.setAanZet( WIT );
	newPage = getCache().getPage( pageDescriptor );
	assertThat( TestHelper.isAllOne( newPage.getData() ), is( true ) );
}

}
