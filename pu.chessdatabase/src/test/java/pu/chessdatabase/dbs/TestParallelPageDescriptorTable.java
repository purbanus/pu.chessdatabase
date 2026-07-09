package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static pu.chessdatabase.bo.Kleur.Wit;
import static pu.chessdatabase.dbs.Lokatie.*;
import static pu.chessdatabase.dbs.CacheType.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;

import lombok.Data;

@Data
@SpringBootTest
public class TestParallelPageDescriptorTable
{
@Autowired private Config config;
private PageDescriptorTable pageDescriptorTable;
private PageSizeCalculator pageSizeCalculator = new PageSizeCalculator( Parallel );
private Cache cache;
@BeforeEach
public void setup()
{
	pageDescriptorTable = PageDescriptorTable.create( pageSizeCalculator, config.getAantalStukken() );
	cache = Cache.create(pageSizeCalculator, config.getAantalStukken(), null );
}
@Test
public void testGetSetPageDescriptor()
{
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( InRam )
		.cacheNummer( 7 )
		.schijfAdres( 1_000_000 )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x03 )
		.zk( 0x29 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( Wit )
		.build();
	getPageDescriptorTable().setPageDescriptor( vmStelling, pageDescriptor );
	PageDescriptor gotPageDescriptor = getPageDescriptorTable().getPageDescriptor( vmStelling );
	assertThat( gotPageDescriptor, is( pageDescriptor ) );
}
int numberOfPages = 0;
@Test
public void testIterateOverAllPageDescriptors()
{
	getPageDescriptorTable().iterateOverAllPageDescriptors( this::countPages );
	assertThat( numberOfPages, is( 10 ) );
}
public void countPages( VMStelling aVmStelling )
{
	numberOfPages++;
}
long address;
int index;
@Test
public void testInitializePageDescriptorTable()
{
//	StopWatch timer = new StopWatch();
	address = 0L;
	index = 0;
	// getPageDescriptorTable().initializePageDescriptorTable(); // Dit is al gebeurd vuh de xetup
	getPageDescriptorTable().iterateOverAllPageDescriptors( this::testPageDescriptor );
//	System.out.println( "initializePageDescriptorTabel duurde " + timer.getElapsedNs() + (" = ") + timer.getLapTimeMs() );
}
void testPageDescriptor( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = getPageDescriptorTable().getPageDescriptor( aVmStelling );
	assertThat( pageDescriptor.getWaar(), is( OpSchijf ) );
	assertThat( pageDescriptor.getSchijfAdres(), is( address ) );
	assertThat( pageDescriptor.getCacheNummer(), is( index ) );
	address += getCache().getPageSize();
	index++;
}

}