package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import pu.services.StopWatch;

@SuppressWarnings( "unused" )
public class TestCache
{
Cache cache = new Cache();

@Test
public void testCtor()
{
	@SuppressWarnings( "hiding" )
	Cache cache = new Cache();
}
@Test
public void testGetPDT()
{
	StopWatch timer = new StopWatch();
	for ( int wk = 0; wk < 10; wk++ )
	{
		for ( int zk = 0; zk < 64; zk++ )
		{
			for ( int aanZet = 0; aanZet < 2; aanZet++ )
			{
				PageDescriptor pageDescriptor = cache.pageDescriptorTabel[wk][zk][aanZet];
			}
		}
	}
	System.out.println( "testGetPDT duurde " + timer.getElapsedNs() + (" = ") + timer.getLapTimeMs() );
}
@Test
public void testGetPDs()
{
	StopWatch timer = new StopWatch();
	for ( int wk = 0; wk < 10; wk++ )
	{
		for ( int zk = 0; zk < 64; zk++ )
		{
			for ( int aanZet = 0; aanZet < 2; aanZet++ )
			{
				PageDescriptor pageDescriptor = cache.pageDescriptorTabel[wk][zk][aanZet];
			}
		}
	}
	System.out.println( "testGetPDs duurde " + timer.getElapsedNs() + (" = ") + timer.getLapTimeMs() );
}
}
