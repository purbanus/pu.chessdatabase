package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pu.chessdatabase.util.MatrixFormatter;

public class TestVM
{
public static final String DATABASE_NAME = "Pipo";
public final VM vm = new VM();

@BeforeEach
public void setup()
{
	vm.create( DATABASE_NAME );
	vm.initializePageDescriptorTabel();
	vm.initializeCache();
}
@AfterEach
public void destroy()
{
	vm.delete();
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
@SuppressWarnings( "unused" )
private void writePage0WithAllOnes()
{
	writePageWithAll(0L, 1, (byte)1 );
}
private void writePageWithAll( long aPageNumber, int aCacheNumber, byte aValue )
{
	Page page = TestHelper.createPageWithAll( aValue );
	long schijfAdres = aPageNumber * vm.PAGE_SIZE;
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( aCacheNumber )
		.schijfAdres( schijfAdres )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.generatie( 15 )
		.page( page )
		.pageDescriptor( pageDescriptor )
		.vuil( true )
		.build();
	vm.Cache[aCacheNumber] = cacheEntry;
	vm.pageOut( pageDescriptor );
}
//@Test
public void testShowCache()
{
	vm.initializePageDescriptorTabel();
	vm.initializeCache();
	//showCache( vm );
}
public static void showCache( VM vm )
{
	MatrixFormatter matrixFormatter = new MatrixFormatter();
	matrixFormatter.setDefaultAlignment( MatrixFormatter.ALIGN_RIGHT );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 50 ) );
	matrixFormatter.addDetail( new String [] { "Number", "PD.Lokatie", "PD.cachenummer", "PD.Schijfadres", "Page, eerste 10", "Vuil", "Generatie" } );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 50 ) );
	for ( int x = 0; x < vm.CACHE_SIZE; x++ )
	{
		CacheEntry cacheEntry = vm.Cache[x];
		if ( cacheEntry == null )
		{
			matrixFormatter.addDetail( new String []{ 
				String.valueOf( x  ), 
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
					String.valueOf( x  ), 
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
				byte [] page = cacheEntry.getPage().getPage();
				StringBuilder sb = new StringBuilder();
				for ( int y = 0; y < 10; y++ )
				{
					sb.append( page[y] ).append(  " " );
				}
					
				matrixFormatter.addDetail( new String []{ 
					String.valueOf( x  ), 
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
	// @@NOG Maak hier een functie van in VM met een lambda als parm
    for ( int WK = vm.wkveldRange.getMinimum(); WK < vm.wkveldRange.getMaximum() + 1; WK++ )
    {
        for ( int ZK = vm.veldRange.getMinimum(); ZK < vm.veldRange.getMaximum() + 1; ZK++ )
        {
            for ( int aanZet = 0; aanZet < 2; aanZet++ )
            {
                PageDescriptor pageDescriptor = vm.pageDescriptorTabel[WK][ZK][aanZet];
//                pageDescriptor.setCacheNummer( 1 );
//                vm.Cache[1].setVuil( true );
        		vm.getDatabase().seek( pageDescriptor.getSchijfAdres() );
        		Page page = new Page();
        	    int aantal = vm.getDatabase().read( page.getPage(), 0, vm.PAGE_SIZE );
        	    assertThat( aantal, is( vm.PAGE_SIZE) );
        	    assertThat( TestHelper.isAllZero( page.getPage() ), is( true ) );
            }
        }
    }
}

//=================================================================================================
//Hier komen de tests
//=================================================================================================

@Test
public void testInzPDT()
{
	vm.initializePageDescriptorTabel();
	
	long Adres = 0;
	for ( int WK = 0; WK < 10; WK++ )
	{
		for ( int ZK = 0; ZK < 64; ZK++ )
		{
			for ( int aanZet = 0; aanZet < 2; aanZet++ )
			{
				PageDescriptor pageDescriptor = vm.pageDescriptorTabel[WK][ZK][aanZet];
				assertThat( pageDescriptor.getWaar(), is( Lokatie.OP_SCHIJF ) );
				assertThat( pageDescriptor.getSchijfAdres(), is( Adres ) );
				assertThat( pageDescriptor.getCacheNummer(), is( Integer.MAX_VALUE ) );
				Adres += vm.PAGE_SIZE;
			}
		}
	}
}
@Test
public void testInzCache()
{
	vm.initializeCache();
	
	for ( int x = 1; x < vm.CACHE_SIZE; x++ )
	{
		CacheEntry cacheEntry = vm.Cache[x];
		assertThat( cacheEntry.getPageDescriptor(), is( nullValue() ) );
		assertThat( cacheEntry.getPage().page[0], is( (byte)0 ) );
		assertThat( TestHelper.isAllZero( cacheEntry.getPage().page ), is( true ) );
		assertThat( cacheEntry.getGeneratie(), is( 0L ) );
		assertThat( cacheEntry.isVuil(), is( false ) );
	}
}
@Test
public void testReport()
{
	// Hier gebeurt nog niks in VM
}
@Test
public void testGetFreeCacheEntry()
{
	for ( int x = 1; x < vm.CACHE_SIZE; x++ )
	{
		CacheEntry cacheEntry = vm.Cache[x];
		cacheEntry.setVuil( true );
		cacheEntry.setGeneratie( x + 100 );
	}
	CacheEntry cacheEntry = vm.Cache[1];
	cacheEntry.setVuil( false );
	cacheEntry.setGeneratie( 10 );
	assertThat( vm.getFreeCacheEntry(), is( 1 ) );

	cacheEntry = vm.Cache[10];
	cacheEntry.setVuil( false );
	cacheEntry.setGeneratie( 5 );
	assertThat( vm.getFreeCacheEntry(), is( 10 ) );

	cacheEntry = vm.Cache[1];
	cacheEntry.setVuil( true );
	cacheEntry = vm.Cache[10];
	cacheEntry.setVuil( true );
	assertThat( vm.getFreeCacheEntry(), is( 10 ) );

	cacheEntry = vm.Cache[20];
	cacheEntry.setVuil( true );
	cacheEntry.setGeneratie( 3 );
	cacheEntry = vm.Cache[25];
	cacheEntry.setVuil( true );
	cacheEntry.setGeneratie( 15 );
	
	assertThat( vm.getFreeCacheEntry(), is( 20 ) );
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
		.schijfAdres( 5L * vm.PAGE_SIZE )
		.build();
	vm.getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( vm.Cache[cacheNumber].getPage().getPage(), value ), is( true ) );
}
@Test
public void testPutRawPageData()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x70;

	Page page = TestHelper.createPageWithAll( value );
	vm.Cache[cacheNumber].setPage( page );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
		.build();
	vm.putRawPageData( pageDescriptor );
	
	vm.getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( vm.Cache[cacheNumber].getPage().getPage(), value ), is( true ) );

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
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
		.build();
	Page page = TestHelper.createPageWithAll( value );
	vm.Cache[cacheNumber].setPage( page );
	vm.Cache[cacheNumber].setPageDescriptor( pageDescriptor );
	vm.Cache[cacheNumber].setVuil( true );
	
	//showCache( vm );
	vm.pageOut( pageDescriptor );
	vm.Cache[cacheNumber].setPage( new Page() );
	vm.getRawPageData( pageDescriptor );
	assertThat( TestHelper.isAll( vm.Cache[cacheNumber].getPage().getPage(), value ), is( true ) );
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
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
		.build();
	//showCache( vm );
	vm.pageIn( pageDescriptor );
	assertThat( TestHelper.isAll( vm.Cache[cacheNumber].getPage().getPage(), value ), is( true ) );
}
@Test
public void testGetPage()
{
	long pageNumber = 12L;
	int cacheNumber = 29;
	byte value = (byte)0x30;

	writePageWithAll( pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.pageDescriptorTabel[vmStelling.getWk()][vmStelling.getZk()][vmStelling.getAanZet().ordinal()] = pageDescriptor;

	Page page = vm.getPage( vmStelling, true );
	assertThat( TestHelper.isAll( page.getPage(), value ), is( true ) );
	// De pageDescriptor is veranderd, hij wijst nu naar cachenummer 2
	assertThat( pageDescriptor.getCacheNummer(), is( 1 ) );
	assertThat( vm.Cache[pageDescriptor.getCacheNummer()].isVuil(), is( true ) );
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
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.pageDescriptorTabel[vmStelling.getWk()][vmStelling.getZk()][vmStelling.getAanZet().ordinal()] = pageDescriptor;

	Page page = vm.getPage( vmStelling, false );
	assertThat( TestHelper.isAll( page.getPage(), value ), is( true ) );
	// De pageDescriptor is NIET veranderd
	assertThat( pageDescriptor.getCacheNummer(), is( cacheNumber ) );
	assertThat( vm.Cache[pageDescriptor.getCacheNummer()].isVuil(), is( false ) );
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
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.pageDescriptorTabel[vmStelling.getWk()][vmStelling.getZk()][vmStelling.getAanZet().ordinal()] = pageDescriptor;

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
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( WIT )
		.build();
	vm.pageDescriptorTabel[vmStelling.getWk()][vmStelling.getZk()][vmStelling.getAanZet().ordinal()] = pageDescriptor;

	int dbsRec = 0x55;
	vm.put( vmStelling, dbsRec );
	Page page = vm.Cache[pageDescriptor.getCacheNummer()].getPage();
	assertThat( page.getPage()[vmStelling.getPositionWithinPage()], is( (byte)( dbsRec & 0xff ) ) );
}
@Test
public void testFreeRecord()
{
	long pageNumber = 9L;
	int cacheNumber = 21;
	byte value = (byte)0xe0;

	//writePageWithAll( pageNumber, cacheNumber, value );
	Page page = TestHelper.createPageWithAll( value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PAGE_SIZE )
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
	vm.Cache[pageDescriptor.getCacheNummer()] = cacheEntry;
	vm.pageDescriptorTabel[vmStelling.getWk()][vmStelling.getZk()][vmStelling.getAanZet().ordinal()] = pageDescriptor;

	vm.freeRecord( vmStelling );
	assertThat( cacheEntry.getGeneratie(), is( 0L ) );
	assertThat( cacheEntry.isVuil(), is( false ) );
}
@Test
public void testFlushWithNothingChanged()
{
	vm.flush();
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
	vm.Cache[1] = cacheEntry;
	
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
	vm.Cache[15] = cacheEntry;
	
	vm.flush();
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
	vm.Cache[1] = cacheEntry;
	
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
	vm.Cache[15] = cacheEntry;
	
	vm.flush();

	// @@NOG Lees de eerste twee paginas en check of die allemaal 1 zijn
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( WIT )
		.build();
	Page newPage = vm.getPage( vmStelling, false );
	assertThat( TestHelper.isAllOne( newPage.getPage() ), is( true ) );
	
	vmStelling.setAanZet( WIT );
	newPage = vm.getPage( vmStelling, false );
	assertThat( TestHelper.isAllOne( newPage.getPage() ), is( true ) );
}
@Test
public void testCloseWithNoDatabesePresent()
{
	vm.delete();
	vm.close();
}
@Test
public void testCloseWithDatabaseOpen()
{
	vm.close();
}
@Test
public void testOpen()
{
	vm.open( DATABASE_NAME );
	assertThat( vm.getDatabase(), is( notNullValue() ) );
}
@Test
public void testCreateFile()
{
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
	vm.delete();

	file = new File( DATABASE_NAME );
	assertThat( file.exists(), is( false ) );
}

}
