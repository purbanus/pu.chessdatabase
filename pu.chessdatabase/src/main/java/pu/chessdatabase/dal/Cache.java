package pu.chessdatabase.dal;

import org.apache.commons.collections4.map.MultiKeyMap;

import pu.services.StopWatch;
public class Cache
{
public static final int PAGE_SIZE = 4096;               // Bytes per page

PageDescriptor[][][] pageDescriptorTabel = new PageDescriptor[10][64][2];
@SuppressWarnings( "rawtypes" )
MultiKeyMap pageDescriptors = new MultiKeyMap<>();
public Cache()
{
	initializePageDescriptorTabel();
	initializePageDescriptors();
}
private void initializePageDescriptorTabel()
{
	StopWatch timer = new StopWatch();
	long Adres = 0;
	for ( int wk = 0; wk < 10; wk++ )
	{
		for ( int zk = 0; zk < 64; zk++ )
		{
			for ( int aanZet = 0; aanZet < 2; aanZet++ )
			{
				pageDescriptorTabel[wk][zk][aanZet] = PageDescriptor.builder()
					.waar( Lokatie.OP_SCHIJF )
					.schijfAdres( Adres )
					.cacheNummer( Integer.MAX_VALUE )
					.build();
				Adres += PAGE_SIZE;
			}
		}
	}
	System.out.println( "initializePageDescriptorTabel duurde " + timer.getElapsedNs() + (" = ") + timer.getLapTimeMs() );
}
@SuppressWarnings( "unchecked" )
private void initializePageDescriptors()
{
	StopWatch timer = new StopWatch();
	long Adres = 0;
	for ( int wk = 0; wk < 10; wk++ )
	{
		for ( int zk = 0; zk < 64; zk++ )
		{
			for ( int aanZet = 0; aanZet < 2; aanZet++ )
			{
				pageDescriptors.put( wk, zk, aanZet, PageDescriptor.builder()
					.waar( Lokatie.OP_SCHIJF )
					.schijfAdres( Adres )
					.cacheNummer( Integer.MAX_VALUE )
					.build() 
				);
				Adres += PAGE_SIZE;
			}
		}
	}
	System.out.println( "initializePageDescriptors duurde " + timer.getElapsedNs() + (" = ") + timer.getLapTimeMs() );
}



}