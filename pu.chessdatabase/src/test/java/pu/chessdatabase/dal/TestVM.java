package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
	vm.Create( DATABASE_NAME );
	vm.InzPDT();
	vm.InzCache();
}
@AfterEach
public void destroy()
{
	vm.delete();
}

//=================================================================================================
// Hulpmethodes
//=================================================================================================
private boolean isAllZero( byte [] aPage )
{
	return isAll( aPage, (byte)0 );
}
private boolean isAllOne( byte [] aPage )
{
	return isAll( aPage, (byte)1 );
}
private boolean isAll( byte [] aPage, byte aValue )
{
	for ( byte b : aPage )
	{
		if ( b != aValue )
		{
			return false;
		}
	}
	return true;
}
private Page createPageWithAllOnes()
{
	return createPageWithAll( (byte)1 );
}
private Page createPageWithAll( byte aValue )
{
	byte [] entries = new byte [VM.PageSize];
	for ( int x = 0; x < VM.PageSize; x++ )
	{
		
		entries[x] = aValue;
	}
	return new Page( entries);
}
@SuppressWarnings( "unused" )
private void writePage0WithAllOnes()
{
	writePageWithAll(0L, 1, (byte)1 );
}
private void writePageWithAll( long aPageNumber, int aCacheNumber, byte aValue )
{
	Page page = createPageWithAll( aValue );
	long schijfAdres = aPageNumber * vm.PageSize;
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( aCacheNumber )
		.schijfAdres( schijfAdres )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.Generatie( 15 )
		.PagePointer( page )
		.PDPointer( pageDescriptor )
		.Vuil( true )
		.build();
	vm.Cache[aCacheNumber] = cacheEntry;
	vm.PageOut( pageDescriptor );
}
//@Test
public void testShowCache()
{
	vm.InzPDT();
	vm.InzCache();
	//showCache( vm );
}
public static void showCache( VM vm )
{
	MatrixFormatter matrixFormatter = new MatrixFormatter();
	matrixFormatter.setDefaultAlignment( MatrixFormatter.ALIGN_RIGHT );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 50 ) );
	matrixFormatter.addDetail( new String [] { "Number", "PD.Lokatie", "PD.cachenummer", "PD.Schijfadres", "Page, eerste 10", "Vuil", "Generatie" } );
	matrixFormatter.addHeader( StringUtils.repeat( '-', 50 ) );
	for ( int x = 0; x < vm.CacheSize; x++ )
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
			PageDescriptor pageDescriptor = cacheEntry.getPDPointer();
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
				byte [] page = cacheEntry.getPagePointer().getPage();
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
    for ( int WK = vm.WKveld.getMinimum(); WK < vm.WKveld.getMaximum() + 1; WK++ )
    {
        for ( int ZK = vm.Veld.getMinimum(); ZK < vm.Veld.getMaximum() + 1; ZK++ )
        {
            for ( int aanZet = 0; aanZet < 2; aanZet++ )
            {
                PageDescriptor pageDescriptor = vm.PDT[WK][ZK][aanZet];
//                pageDescriptor.setCacheNummer( 1 );
//                vm.Cache[1].setVuil( true );
        		vm.Database.seek( pageDescriptor.getSchijfAdres() );
        		Page page = new Page();
        	    int aantal = vm.Database.read( page.getPage(), 0, vm.PageSize );
        	    assertThat( aantal, is( vm.PageSize) );
        	    assertThat( isAllZero( page.getPage() ), is( true ) );
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
	vm.InzPDT();
	
	long Adres = 0;
	for ( int WK = 0; WK < 10; WK++ )
	{
		for ( int ZK = 0; ZK < 64; ZK++ )
		{
			for ( int aanZet = 0; aanZet < 2; aanZet++ )
			{
				PageDescriptor pageDescriptor = vm.PDT[WK][ZK][aanZet];
				assertThat( pageDescriptor.getWaar(), is( Lokatie.OpSchijf ) );
				assertThat( pageDescriptor.getSchijfAdres(), is( Adres ) );
				assertThat( pageDescriptor.getCacheNummer(), is( Integer.MAX_VALUE ) );
				Adres += vm.PageSize;
			}
		}
	}
}
@Test
public void testInzCache()
{
	vm.InzCache();
	
	for ( int x = 1; x < vm.CacheSize; x++ )
	{
		CacheEntry cacheEntry = vm.Cache[x];
		assertThat( cacheEntry.getPDPointer(), is( nullValue() ) );
		assertThat( cacheEntry.getPagePointer().page[0], is( (byte)0 ) );
		assertThat( isAllZero( cacheEntry.getPagePointer().page ), is( true ) );
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
	for ( int x = 1; x < vm.CacheSize; x++ )
	{
		CacheEntry cacheEntry = vm.Cache[x];
		cacheEntry.setVuil( true );
		cacheEntry.setGeneratie( x + 100 );
	}
	CacheEntry cacheEntry = vm.Cache[1];
	cacheEntry.setVuil( false );
	cacheEntry.setGeneratie( 10 );
	assertThat( vm.GetFreeCacheEntry(), is( 1 ) );

	cacheEntry = vm.Cache[10];
	cacheEntry.setVuil( false );
	cacheEntry.setGeneratie( 5 );
	assertThat( vm.GetFreeCacheEntry(), is( 10 ) );

	cacheEntry = vm.Cache[1];
	cacheEntry.setVuil( true );
	cacheEntry = vm.Cache[10];
	cacheEntry.setVuil( true );
	assertThat( vm.GetFreeCacheEntry(), is( 10 ) );

	cacheEntry = vm.Cache[20];
	cacheEntry.setVuil( true );
	cacheEntry.setGeneratie( 3 );
	cacheEntry = vm.Cache[25];
	cacheEntry.setVuil( true );
	cacheEntry.setGeneratie( 15 );
	
	assertThat( vm.GetFreeCacheEntry(), is( 20 ) );
}
@Test
public void testGetRawPageData()
{
	long pageNumber = 5L;
	int cacheNumber = 15;
	byte value = (byte)0x80;

	writePageWithAll( pageNumber, cacheNumber, value );

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( 5L * vm.PageSize )
		.build();
	vm.getRawPageData( pageDescriptor );
	assertThat( isAll( vm.Cache[cacheNumber].getPagePointer().getPage(), value ), is( true ) );
}
@Test
public void testPutRawPageData()
{
	long pageNumber = 3L;
	int cacheNumber = 15;
	byte value = (byte)0x70;

	Page page = createPageWithAll( value );
	vm.Cache[cacheNumber].setPagePointer( page );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	vm.putRawPageData( pageDescriptor );
	
	vm.getRawPageData( pageDescriptor );
	assertThat( isAll( vm.Cache[cacheNumber].getPagePointer().getPage(), value ), is( true ) );

}
@Test
public void testPageOut()
{
	long pageNumber = 15L;
	int cacheNumber = 15;
	byte value = (byte)0x60;

	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	Page page = createPageWithAll( value );
	vm.Cache[cacheNumber].setPagePointer( page );
	vm.Cache[cacheNumber].setPDPointer( pageDescriptor );
	vm.Cache[cacheNumber].setVuil( true );
	
	//showCache( vm );
	vm.PageOut( pageDescriptor );
	vm.Cache[cacheNumber].setPagePointer( new Page() );
	vm.getRawPageData( pageDescriptor );
	assertThat( isAll( vm.Cache[cacheNumber].getPagePointer().getPage(), value ), is( true ) );
}
@Test
public void testPageIn() throws IOException
{
	long pageNumber = 10L;
	int cacheNumber = 17;
	byte value = (byte)0x40;

	writePageWithAll( pageNumber, cacheNumber, value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	//showCache( vm );
	vm.PageIn( pageDescriptor );
	assertThat( isAll( vm.Cache[cacheNumber].getPagePointer().getPage(), value ), is( true ) );
}
@Test
public void testGetPage()
{
	long pageNumber = 12L;
	int cacheNumber = 29;
	byte value = (byte)0x30;

	writePageWithAll( pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( false )
		.build();
	vm.PDT[vmStelling.getWk()][vmStelling.getZk()][vmStelling.isAanZet()? 1 : 0] = pageDescriptor;

	Page page = vm.GetPage( vmStelling, true );
	assertThat( isAll( page.getPage(), value ), is( true ) );
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
		.waar( Lokatie.InRAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( false )
		.build();
	vm.PDT[vmStelling.getWk()][vmStelling.getZk()][vmStelling.isAanZet()? 1 : 0] = pageDescriptor;

	Page page = vm.GetPage( vmStelling, false );
	assertThat( isAll( page.getPage(), value ), is( true ) );
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
		.waar( Lokatie.OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( false )
		.build();
	vm.PDT[vmStelling.getWk()][vmStelling.getZk()][vmStelling.isAanZet()? 1 : 0] = pageDescriptor;

	byte dbsRec = vm.Get( vmStelling );
	assertThat( dbsRec, is( value ) );
}
@Test
public void testPut()
{
	long pageNumber = 11L;
	int cacheNumber = 23;
	byte value = (byte)0xf0;

	writePageWithAll( pageNumber, cacheNumber, value );
	
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OpSchijf )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x04 )
		.zk( 0x31 )
		.s3( 0x00 )
		.s4( 0x07 )
		.aanZet( false )
		.build();
	vm.PDT[vmStelling.getWk()][vmStelling.getZk()][vmStelling.isAanZet()? 1 : 0] = pageDescriptor;

	byte dbsRec = 0x55;
	vm.Put( vmStelling, dbsRec );
	Page page = vm.Cache[pageDescriptor.getCacheNummer()].getPagePointer();
	assertThat( page.getPage()[vmStelling.getDbsAddress()], is( dbsRec ) );
}
@Test
public void testFreeRecord()
{
	long pageNumber = 9L;
	int cacheNumber = 21;
	byte value = (byte)0xe0;

	//writePageWithAll( pageNumber, cacheNumber, value );
	Page page = createPageWithAll( value );
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( cacheNumber )
		.schijfAdres( pageNumber * vm.PageSize )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x03 )
		.zk( 0x29 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( false )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.Generatie( 15554 )
		.PagePointer( page )
		.PDPointer( pageDescriptor )
		.Vuil( true )
		.build();
	vm.Cache[pageDescriptor.getCacheNummer()] = cacheEntry;
	vm.PDT[vmStelling.getWk()][vmStelling.getZk()][vmStelling.isAanZet()? 1 : 0] = pageDescriptor;

	vm.FreeRecord( vmStelling );
	assertThat( cacheEntry.getGeneratie(), is( 0L ) );
	assertThat( cacheEntry.isVuil(), is( false ) );
}
@Test
public void testFlushWithNothingChanged()
{
	vm.Flush();
}
@Test
public void testFlushWithSomePagesPresentButNoneVuil()
{
	Page page = createPageWithAllOnes();
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( 1 )
		.schijfAdres( 0L )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.Generatie( 2156 )
		.PDPointer( pageDescriptor )
		.PagePointer( page )
		.Vuil( false )
		.build();
	vm.Cache[1] = cacheEntry;
	
	pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( 15 )
		.schijfAdres( 4096L )
		.build();
	cacheEntry = CacheEntry.builder()
		.Generatie( 9500 )
		.PDPointer( pageDescriptor )
		.PagePointer( page )
		.Vuil( false )
		.build();
	vm.Cache[15] = cacheEntry;
	
	vm.Flush();
}
@Test
public void testFlushWithSomePagesPresentAndVuil()
{
	Page page = createPageWithAllOnes();
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( 1 )
		.schijfAdres( 0L )
		.build();
	CacheEntry cacheEntry = CacheEntry.builder()
		.Generatie( 2156 )
		.PDPointer( pageDescriptor )
		.PagePointer( page )
		.Vuil( true )
		.build();
	vm.Cache[1] = cacheEntry;
	
	pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.InRAM )
		.cacheNummer( 15 )
		.schijfAdres( 4096L )
		.build();
	cacheEntry = CacheEntry.builder()
		.Generatie( 9500 )
		.PDPointer( pageDescriptor )
		.PagePointer( page )
		.Vuil( true )
		.build();
	vm.Cache[15] = cacheEntry;
	
	vm.Flush();

	// @@NOG Lees de eerste twee paginas en check of die allemaal 1 zijn
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( false )
		.build();
	Page newPage = vm.GetPage( vmStelling, false );
	assertThat( isAllOne( newPage.getPage() ), is( true ) );
	
	vmStelling.setAanZet( true );
	newPage = vm.GetPage( vmStelling, false );
	assertThat( isAllOne( newPage.getPage() ), is( true ) );
}
@Test
public void testCloseWithNoDatabesePresent()
{
	vm.delete();
	vm.Close();
}
@Test
public void testCloseWithDatabaseOpen()
{
	vm.Close();
}
@Test
public void testOpen()
{
	vm.Open( DATABASE_NAME );
	assertThat( vm.Database, is( notNullValue() ) );
}
@Test
public void testCreateFile()
{
	vm.delete();
	vm.CreateFile( DATABASE_NAME );
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
