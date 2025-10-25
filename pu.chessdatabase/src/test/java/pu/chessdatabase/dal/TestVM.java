package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;
import pu.services.MatrixFormatter;

@SpringBootTest
public class TestVM
{
public static final String DATABASE_NAME = "dbs/Pipo";
@Autowired private VM vm;
@Autowired private Config config;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getName();
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	vm.create(); // Doet ook Open, dus initialiseert de tabellen
}
@AfterEach
public void destroy()
{
	assertThat( vm.getDatabaseName(), is( DATABASE_NAME ) );
	vm.delete();
	config.switchConfig( savedConfigString );

}

@Test
public void testIntToByte()
{
	int x = 0x80;
//	System.out.println( x );
//	System.out.println( (byte)x );
	assertThat( (byte)x, is( (byte)-128 ) ); // Dat is dus niet wat we willen
	assertThat( (byte)( x & 0xff ), is( (byte)128 ) ); // Dit is beter!
	x = 250;
	assertThat( (byte)( x & 0xff ), is( (byte)250 ) ); // Doet het ook
	assertThat( Integer.valueOf( 250 ).byteValue(), is( (byte)250 ) ); // Is zeer breedsprakig
}
@Test
public void testByteToInt()
{
	byte x = (byte) 0x80;
//	System.out.println( x );
//	System.out.println( (int)x );
	assertThat( (int)x, is( -128 ) ); // Dat is dus niet wat we willen
	assertThat( Byte.toUnsignedInt( x ), is( 128 ) );
}
@Test
public void testVeldToAlfa()
{
	assertThat( VM.veldToAlfa( 0x00 ), is( "a1" ) );
	assertThat( VM.veldToAlfa( 0x07 ), is( "h1" ) );
	assertThat( VM.veldToAlfa( 0x08 ), is( "a2" ) );
	assertThat( VM.veldToAlfa( 0x38 ), is( "a8" ) );
	assertThat( VM.veldToAlfa( 0x3f ), is( "h8" ) );
	assertThrows( RuntimeException.class, () -> { VM.veldToAlfa( -31415 ); } );
	assertThrows( RuntimeException.class, () -> { VM.veldToAlfa( 64 ); } );
	assertThrows( RuntimeException.class, () -> { VM.veldToAlfa( 100 ); } );
}
@Test
public void testAlfaToVeld()
{
	assertThat( VM.alfaToVeld( "a1" ), is( 0x00 ) );
	assertThat( VM.alfaToVeld( "h1" ), is( 0x07 ) );
	assertThat( VM.alfaToVeld( "a8" ), is( 0x38 ) );
	assertThat( VM.alfaToVeld( "h8" ), is( 0x3f ) );
	assertThat( VM.alfaToVeld( "A8" ), is( 0x38 ) );
	assertThat( VM.alfaToVeld( "H8" ), is( 0x3f ) );
	assertThrows( RuntimeException.class, () -> { VM.alfaToVeld( "a9" ); } );
	assertThrows( RuntimeException.class, () -> { VM.alfaToVeld( "i2" ); } );
	assertThrows( RuntimeException.class, () -> { VM.alfaToVeld( "a" ); } );
	assertThrows( RuntimeException.class, () -> { VM.alfaToVeld( "abc" ); } );
}

@SuppressWarnings( "unused" )
private void writePage0WithAllOnes()
{
	writePageWithAll(0L, 1, (byte)1 );
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
	vm.getCache().setCacheEntry( pageDescriptor, cacheEntry );
	vm.getCache().pageOut( pageDescriptor );
}
//@Test
public void testShowCache()
{
	vm.getCache().initializeCache();
	showCache();
}
public void showCache()
{
	MatrixFormatter matrixFormatter = new MatrixFormatter();
	matrixFormatter.setDefaultAlignment( MatrixFormatter.ALIGN_RIGHT );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 50 ) );
	matrixFormatter.addDetail( new String [] { "Number", "PD.Lokatie", "PD.cachenummer", "PD.Schijfadres", "Page, eerste 10", "Vuil", "Generatie" } );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 50 ) );
	int index = -1;
	for ( CacheEntry cacheEntry : vm.getCache().getCache() )
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
			if ( pageDescriptor == null )
			{
				matrixFormatter.addDetail( new String []{
					String.valueOf( index ), 
					"null", 
					"null", 
					"null", 
					"null", 
					String.valueOf( cacheEntry.isVuil() ), 
					String.valueOf( cacheEntry.getGeneratie() ) 
				} );
			}
			else
			{
				byte [] page = cacheEntry.getPage().getData();
				StringBuilder sb = new StringBuilder();
				for ( int y = 0; y < 10; y++ )
				{
					sb.append( page[y] ).append( " " );
				}
					
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
	matrixFormatter.addHeader( StringUtils.repeat( '-', 50 ) );
	System.out.println( matrixFormatter.getOutput() );
}

private void checkIfAllDatabaseEntriesAreZero() throws IOException
{
	PageDescriptorTable pageDescriptorTable = new PageDescriptorTable();
	pageDescriptorTable.iterateOverAllPageDescriptors( this::checkIfDatabaseEntryIsZero );
}
void checkIfDatabaseEntryIsZero( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = vm.getPageDescriptor( aVmStelling );
//  pageDescriptor.setCacheNummer( 1 );
//  vm.Cache[1].setVuil( true );
	Page page = new Page();
	try
	{
		vm.getDatabase().seek( pageDescriptor.getSchijfAdres() );
		int aantal = vm.getDatabase().read( page.getData(), 0, Cache.PAGE_SIZE );
		assertThat( aantal, is( Cache.PAGE_SIZE) );
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
	assertThat( TestHelper.isAllZero( page.getData() ), is( true ) );
}

//=================================================================================================
//Hier komen de tests
//=================================================================================================

@Test
public void testGetPage()
{
	long pageNumber = 27L;
	int cacheNumber = 27;
	byte value = (byte)0xf0;

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

	Page page = vm.getPage( vmStelling );
	assertThat( TestHelper.isAll( page.getData(), value ), is( true ) );

	// Test met Integer.MAX_VALUE
	pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( Integer.MAX_VALUE )
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	page = vm.getPage( vmStelling );
	assertThat( TestHelper.isAll( page.getData(), value ), is( true ) );

}
@Test
public void testGet()
{
	long pageNumber = 27L;
	int cacheNumber = 25;
	byte value = (byte)0xff;

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

	int dbsRec = vm.get( vmStelling );
	assertThat( value, is( (byte)( dbsRec & 0xff ) ) );
}
@Test
public void testPut()
{
	long pageNumber = 11L;
	int cacheNumber = 23;
	byte value = (byte)0xf0;

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

	int dbsRec = 0x55;
	vm.put( vmStelling, dbsRec );
	Page page = vm.getCache().getPage( pageDescriptor );
	assertThat( page.getData()[vmStelling.getPositionWithinPage()], is( (byte)( dbsRec & 0xff ) ) );
	
	// Test met Integer.MAX_VALUE
	pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( Integer.MAX_VALUE )
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	vm.put( vmStelling, dbsRec );
	page = vm.getCache().getPage( pageDescriptor );
	assertThat( page.getData()[vmStelling.getPositionWithinPage()], is( (byte)( dbsRec & 0xff ) ) );
}
@Test
public void testFreeRecord()
{
	long pageNumber = 9L;
	int cacheNumber = 21;
	byte value = (byte)0xe0;

	Page page = TestHelper.createPageWithAll( value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * Cache.PAGE_SIZE )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x03 )
		.zk( 0x29 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( WIT )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 15554 )
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( true )
		.build();
	vm.getCache().setCacheEntry( pageDescriptor,  cacheEntry );
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	vm.freeRecord( vmStelling );
	assertThat( cacheEntry.getGeneratie(), is( 0L ) );
	assertThat( cacheEntry.isVuil(), is( false ) );
}
@Test
public void testCloseWithNoDatabesePresent()
{
	assertThat( vm.getDatabaseName(), is( DATABASE_NAME ) );
	vm.delete();
	vm.close();
	// @@NOG
}
@Test
public void testCloseWithDatabaseOpen()
{
	vm.close();
	// @@NOG
}
@Test
public void testOpen()
{
	vm.open();
	assertThat( vm.getDatabase(), is( notNullValue() ) );
	// @@NOG
}
@Test
public void testCreateFile()
{
	assertThat( vm.getDatabaseName(), is( DATABASE_NAME ) );
	vm.delete();
	vm.createFile( DATABASE_NAME );
	File file = new File( DATABASE_NAME );
	assertThat( file.exists(), is( true ) );
}
@Test
public void testCreate() throws IOException
{
	File file = new File( DATABASE_NAME );
	assertThat( file.exists(), is( true ) );
	assertThat( file.length(), is( 5242880L  ) );
	checkIfAllDatabaseEntriesAreZero();
}
@Test
public void testDelete()
{
	File file = new File( DATABASE_NAME );
	assertThat( file.exists(), is( true ) );
	assertThat( vm.getDatabaseName(), is( DATABASE_NAME ) );
	vm.delete();

	file = new File( DATABASE_NAME );
	assertThat( file.exists(), is( false ) );
}

}
