package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static pu.chessdatabase.dbs.CacheType.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.Data;

@Data
public class TestPageSizeCalculator
{
private PageSizeCalculator pageSizeCalculator;
@BeforeEach
public void setup()
{
}
@Test
public void testGetPageSizeSerial()
{
	pageSizeCalculator = new PageSizeCalculator( Serial  );
	assertThat( pageSizeCalculator.getPageSize( 3 ), is( 64 ) );
	assertThat( pageSizeCalculator.getPageSize( 4 ), is( 64 * 64 ) );
	assertThat( pageSizeCalculator.getPageSize( 5 ), is( 64 * 64 * 64 ) );
}
@Test
public void testGetPageSizeParallel()
{
	pageSizeCalculator = new PageSizeCalculator( Parallel  );
	assertThat( pageSizeCalculator.getPageSize( 3 ), is( 64 * 64 * 2 ) );
	assertThat( pageSizeCalculator.getPageSize( 4 ), is( 64 * 64 * 64 * 2  ) );
	assertThat( pageSizeCalculator.getPageSize( 5 ), is( 64 * 64 * 64 * 64 * 2  ) );
}
@Test
public void testGetDatabaseSizeSerial()
{
	pageSizeCalculator = new PageSizeCalculator( Serial  );
	assertThat( pageSizeCalculator.getDatabaseSize( 3 ), is( 10 * 64 * 64 * 2 ) );
	assertThat( pageSizeCalculator.getDatabaseSize( 4 ), is( 10 * 64 * 64 * 64 * 2) );
	assertThat( pageSizeCalculator.getDatabaseSize( 5 ), is( 10 * 64 * 64 * 64 * 64 * 2 ) );

	// Zelfde als Serial!
	pageSizeCalculator = new PageSizeCalculator( Parallel  );
	assertThat( pageSizeCalculator.getDatabaseSize( 3 ), is( 10 * 64 * 64 * 2 ) );
	assertThat( pageSizeCalculator.getDatabaseSize( 4 ), is( 10 * 64 * 64 * 64 * 2) );
	assertThat( pageSizeCalculator.getDatabaseSize( 5 ), is( 10 * 64 * 64 * 64 * 64 * 2 ) );
}

}