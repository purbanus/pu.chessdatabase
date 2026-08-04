package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.dbs.CacheType.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;

import lombok.Data;

@Data
@SpringBootTest
public class TestPageSizeCalculator
{
@Autowired private Config config;
@BeforeEach
public void setup()
{
}
public PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
@Test
public void testGetPageSizeSerial()
{
	getConfig().setPageSizeCalculator( new PageSizeCalculator( Serial, getConfig() ) );
	
	getConfig().switchConfig( Config.KLoK );
	assertThat( getPageSizeCalculator().getPageSize( 3 ), is( 64 ) );
	assertThat( getPageSizeCalculator().getPageSize( 4 ), is( 64 * 64 ) );
	assertThat( getPageSizeCalculator().getPageSize( 5 ), is( 64 * 64 * 64 ) );

	getConfig().switchConfig( Config.KLLK );
	assertThat( getPageSizeCalculator().getPageSize( 3 ), is( 64 ) );
	assertThat( getPageSizeCalculator().getPageSize( 4 ), is( 64 * 64 ) );
	assertThat( getPageSizeCalculator().getPageSize( 5 ), is( 64 * 64 * 64 ) );
}
@Test
public void testGetPageSizeParallel()
{
	getConfig().setPageSizeCalculator( new PageSizeCalculator( Parallel, getConfig() ) );

	getConfig().switchConfig( Config.KLoK );
	assertThat( getPageSizeCalculator().getPageSize( 3 ), is( 64 * 64 * 2 ) );
	assertThat( getPageSizeCalculator().getPageSize( 4 ), is( 64 * 64 * 64 * 2  ) );
	assertThat( getPageSizeCalculator().getPageSize( 5 ), is( 64 * 64 * 64 * 64 * 2  ) );
	
	getConfig().switchConfig( Config.KLLK );
	assertThat( getPageSizeCalculator().getPageSize( 3 ), is( 64 * 64 * 2 ) );
	assertThat( getPageSizeCalculator().getPageSize( 4 ), is( 64 * 64 * 64 * 2  ) );
	assertThat( getPageSizeCalculator().getPageSize( 5 ), is( 64 * 64 * 64 * 64 * 2  ) );

}
@Test
public void testGetDatabaseSizeSerialZonderPionnen()
{
	getConfig().switchConfig( Config.PipoKDKT );
	
	getConfig().setPageSizeCalculator( new PageSizeCalculator( Serial, getConfig() ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 3 ), is( 10 * 64 * 64 * 2 ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 4 ), is( 10 * 64 * 64 * 64 * 2) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 5 ), is( 10 * 64 * 64 * 64 * 64 * 2 ) );

	// Zelfde als Serial!
	getConfig().setPageSizeCalculator( new PageSizeCalculator( Parallel, getConfig() ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 3 ), is( 10 * 64 * 64 * 2 ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 4 ), is( 10 * 64 * 64 * 64 * 2) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 5 ), is( 10 * 64 * 64 * 64 * 64 * 2 ) );
}
@Test
public void testGetDatabaseSizeSerialMetPionnen()
{
	getConfig().switchConfig( Config.PipoKLoK );

	getConfig().setPageSizeCalculator( new PageSizeCalculator( Serial, getConfig() ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 3 ), is( 64 * 64 * 64 * 2 ) );          //       524.288
	assertThat( getPageSizeCalculator().getDatabaseSize( 4 ), is( 64 * 64 * 64 * 64 * 2 ) );     //    33.554.432
	assertThat( getPageSizeCalculator().getDatabaseSize( 5 ), is( 64 * 64 * 64 * 64 * 64 * 2) ); // 2.147.483.648

	// Zelfde als Serial!
	getConfig().setPageSizeCalculator( new PageSizeCalculator( Parallel, getConfig() ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 3 ), is( 64 * 64 * 64 * 2 ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 4 ), is( 64 * 64 * 64 * 64 * 2 ) );
	assertThat( getPageSizeCalculator().getDatabaseSize( 5 ), is( 64 * 64 * 64 * 64 * 64 * 2) );
}
}