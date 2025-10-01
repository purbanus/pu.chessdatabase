package pu.chessdatabase.dal;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;

@SpringBootTest
public class TestDbsOverProductie
{
@Autowired private Dbs dbs;

@BeforeEach
public void setup()
{
	dbs.name( dbs.DFT_DBS_NAAM );
	dbs.open();
}
@AfterEach
public void destroy()
{
}

List<BoStelling> remiseStellingen;
int aantalRemiseStellingen = 0;
void getRemiseStelling( BoStelling aBoStelling )
{
	if (   aBoStelling.getS3() == aBoStelling.getWk() 
		|| aBoStelling.getS3() == aBoStelling.getZk()
		|| aBoStelling.getS4() == aBoStelling.getWk()
		|| aBoStelling.getS4() == aBoStelling.getZk() )
	{
		return;
	}
	if ( aBoStelling.getResultaat() == ResultaatType.REMISE )
	{
		remiseStellingen.add( aBoStelling );
		//System.out.println( remiseStellingen );
		aantalRemiseStellingen++;
		if ( aantalRemiseStellingen % 10000 == 0 )
		{
			System.out.println( aantalRemiseStellingen );
			System.out.println( remiseStellingen.get( 0 ) );
		}
		if ( aantalRemiseStellingen == 109175 )
		{
			System.out.println( aantalRemiseStellingen );
			System.out.println( remiseStellingen );
		}
	}
}
@Test
public void testRemiseStellingen()
{
	remiseStellingen = new ArrayList<>();
	dbs.markeerWitEnZwartPass( this::getRemiseStelling );
	System.out.println( remiseStellingen.size() );
	// @@NOG Waarom werkt dit GVD niet? Ik krijg allemaal Illegale stellingen
	// en heel andere dan ik bij debug zie
//	System.out.println( remiseStellingen );
}

}
