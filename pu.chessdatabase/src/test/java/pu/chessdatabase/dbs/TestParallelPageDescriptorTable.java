package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.CacheType.*;
import static pu.chessdatabase.dbs.Lokatie.*;

import org.junit.jupiter.api.AfterEach;
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
@Autowired VM vm;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	getConfig().setCacheType( Parallel );
	// Dit is o.a. nodig om VM te initialiseren
	getConfig().switchConfig( Config.PIPOKDKT );
}
@AfterEach
public void destroy()
{
	config.switchConfig( savedConfigString );
}
PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
PageDescriptorTable getPageDescriptorTable()
{
	return getVm().getPageDescriptorTable();
}
Cache getCache()
{
	return getVm().getCache();
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
int numberOfPages;
@Test
public void testIterateOverAllPageDescriptors()
{
	numberOfPages = 0;
	getConfig().switchConfig( Config.KDKT );
	getPageDescriptorTable().iterateOverAllPageDescriptors( this::countPages );
	assertThat( numberOfPages, is( 10 ) );
	
	numberOfPages = 0;
	getConfig().switchConfig( Config.KLOK );
	getPageDescriptorTable().iterateOverAllPageDescriptors( this::countPages );
	assertThat( numberOfPages, is( 64 ) );
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