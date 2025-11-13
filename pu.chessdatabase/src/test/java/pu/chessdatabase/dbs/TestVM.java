package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.Lokatie.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;
import pu.chessdatabase.dbs.CacheEntry;
import pu.chessdatabase.dbs.Lokatie;
import pu.chessdatabase.dbs.PageDescriptor;
import pu.chessdatabase.dbs.PageDescriptorTable;
import pu.chessdatabase.dbs.VM;
import pu.chessdatabase.dbs.VMStelling;

import lombok.Data;

@SpringBootTest
@Data
public class TestVM
{
public static final String DATABASE_NAME = "dbs/Pipo";
private static final String DATABASE_NAME_4 = DATABASE_NAME + "4";
@Autowired private VM vm;
@Autowired private Config config;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	vm.create(); // Doet ook Open, dus initialiseert de tabellen
}
@AfterEach
public void destroy()
{
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	assertThat( vm.getDatabaseName(), startsWith( DATABASE_NAME ) );
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
	byte [] page = TestHelper.createPageWithAll( aValue );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( aCacheNumber )
		.schijfAdres( aPageNumber * vm.getCache().getPageSize() )
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
private void checkIfAllDatabaseEntriesAreZero() throws IOException
{
	PageDescriptorTable pageDescriptorTable = new PageDescriptorTable( getConfig().getAantalStukken() );
	pageDescriptorTable.iterateOverAllPageDescriptors( this::checkIfDatabaseEntryIsZero );
}
void checkIfDatabaseEntryIsZero( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = vm.getPageDescriptor( aVmStelling );
//  pageDescriptor.setCacheNummer( 1 );
//  vm.Cache[1].setVuil( true );
	byte [] page = new byte[vm.getCache().getPageSize()];
	try
	{
		vm.getDatabase().seek( pageDescriptor.getSchijfAdres() );
		int aantal = vm.getDatabase().read( page, 0, vm.getCache().getPageSize() );
		assertThat( aantal, is( vm.getCache().getPageSize() ) );
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
	assertThat( TestHelper.isAllZero( page ), is( true ) );
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
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( Wit )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	byte [] page = vm.getPage( vmStelling );
	assertThat( TestHelper.isAll( page, value ), is( true ) );

	// Test met Integer.MAX_VALUE
	pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( Integer.MAX_VALUE )
		.schijfAdres( pageNumber * vm.getCache().getPageSize() )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	page = vm.getPage( vmStelling );
	assertThat( TestHelper.isAll( page, value ), is( true ) );

}
@Test
public void testGet()
{
	long pageNumber = 27L;
	int cacheNumber = 25;
	byte value = (byte)0xff;

	writePageWithAll( pageNumber, cacheNumber, value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( Wit )
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
		.waar( OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( Wit )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	int dbsRec = 0x55;
	vm.put( vmStelling, dbsRec );
	byte [] page = vm.getCache().getPage( pageDescriptor );
	assertThat( page[vm.getCache().getPositionWithinPage( vmStelling )], is( (byte)( dbsRec & 0xff ) ) );
	
	// Test met Integer.MAX_VALUE
	pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.cacheNummer( Integer.MAX_VALUE )
		.schijfAdres( pageNumber * vm.getCache().getPageSize() )
		.build();
	vm.getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );

	vm.put( vmStelling, dbsRec );
	page = vm.getCache().getPage( pageDescriptor );
	assertThat( page[vm.getCache().getPositionWithinPage( vmStelling )], is( (byte)( dbsRec & 0xff ) ) );
}
@Test
public void testFreeRecord()
{
	long pageNumber = 9L;
	int cacheNumber = 21;
	byte value = (byte)0xe0;

	byte [] page = TestHelper.createPageWithAll( value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.getCache().getPageSize() )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x03 )
		.zk( 0x29 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( Wit )
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
	assertThat( vm.getDatabaseName(), startsWith( DATABASE_NAME ) );
	vm.delete();
	vm.close();
	// @@NOG Tests maken
}
@Test
public void testCloseWithDatabaseOpen()
{
	vm.close();
	// @@NOG Tests maken
}
@Test
public void testOpen()
{
	vm.open();
	assertThat( vm.getDatabase(), is( notNullValue() ) );
	// @@NOG Tests maken
}
@Test
public void testCreateFile()
{
	// @@HIGH config-afhankelijke tests
	assertThat( vm.getDatabaseName(), startsWith( DATABASE_NAME ) );
	vm.delete();
	vm.createFile( DATABASE_NAME_4 );
	File file = new File( DATABASE_NAME_4 );
	assertThat( file.exists(), is( true ) );
}
@Test
public void testCreate() throws IOException
{
	// @@HIGH config-afhankelijke tests
	File file = new File( DATABASE_NAME_4 );
	assertThat( file.exists(), is( true ) );
	assertThat( file.length(), is( 5242880L  ) );
	checkIfAllDatabaseEntriesAreZero();
}
@Test
public void testDelete()
{
	// @@HIGH config-afhankelijke tests
	File file = new File( DATABASE_NAME_4 );
	assertThat( file.exists(), is( true ) );
	assertThat( vm.getDatabaseName(), is( DATABASE_NAME_4 ) );
	vm.delete();

	file = new File( DATABASE_NAME_4 );
	assertThat( file.exists(), is( false ) );
}

}
