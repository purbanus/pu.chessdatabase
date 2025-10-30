package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;

import lombok.Data;

@Data
@SpringBootTest
public class TestPageDescriptorTable
{
@Autowired private Config config;
private PageDescriptorTable pageDescriptorTable;
private Cache cache;
@BeforeEach
public void setup()
{
	pageDescriptorTable = new PageDescriptorTable( config.getAantalStukken() );
	cache = new Cache( config.getAantalStukken() );
}
@Test
public void testGetSetPageDescriptor()
{
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.IN_RAM )
		.cacheNummer( 0x17 )
		.schijfAdres( 1_000_000 )
		.build();
	VMStelling vmStelling = VMStelling.builder()
		.wk( 0x03 )
		.zk( 0x29 )
		.s3( 0x01 )
		.s4( 0x17 )
		.aanZet( WIT )
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
	assertThat( numberOfPages, is( 10 * 64 * 2 ) );
}
public void countPages( VMStelling aVmStelling )
{
	numberOfPages++;
}
long address = 0L;
@Test
public void testInitializePageDescriptorTable()
{
//	StopWatch timer = new StopWatch();
	getPageDescriptorTable().initializePageDescriptorTable();
	getPageDescriptorTable().iterateOverAllPageDescriptors( this::testPageDescriptor );
//	System.out.println( "initializePageDescriptorTabel duurde " + timer.getElapsedNs() + (" = ") + timer.getLapTimeMs() );
}
void testPageDescriptor( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = getPageDescriptorTable().getPageDescriptor( aVmStelling );
	assertThat( pageDescriptor.getWaar(), is( Lokatie.OP_SCHIJF ) );
	assertThat( pageDescriptor.getSchijfAdres(), is( address ) );
	assertThat( pageDescriptor.getCacheNummer(), is( Integer.MAX_VALUE ) );
	address += getCache().getPageSize();
}

}