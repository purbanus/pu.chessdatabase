package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
//import static org.hamcrest.MatcherAssert.*;
//import static org.hamcrest.Matchers.*;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.ResultaatType.*;

import java.util.ArrayList;
import java.util.List;

//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.dbs.Dbs;

@SpringBootTest
public class TestDbsOverProductie
{
@Autowired private Dbs dbs;

@BeforeEach
public void setup()
{
	dbs.open();
}
@AfterEach
public void destroy()
{
	dbs.close();
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
	if ( aBoStelling.getResultaat() == REMISE )
	{
		remiseStellingen.add( aBoStelling );
		aantalRemiseStellingen++;
		if ( aantalRemiseStellingen % 10000 == 0 )
		{
			System.out.println( aantalRemiseStellingen );
			//System.out.println( remiseStellingen.get( 0 ) );
		}
		if ( aantalRemiseStellingen == 109175 )
		{
			System.out.println( aantalRemiseStellingen );
			System.out.println( remiseStellingen );
		}
	}
}
//@Test
public void testRemiseStellingen()
{
	remiseStellingen = new ArrayList<>();
	dbs.markeerWitEnZwartPass( this::getRemiseStelling );
	System.out.println( remiseStellingen.size() );
	// @@NOG Waarom werkt dit GVD niet? Ik krijg allemaal Illegale stellingen
	// en heel andere dan ik bij debug zie
//	System.out.println( remiseStellingen );
}
List<BoStelling> gewonnenStellingenMetWitAanZet = new ArrayList<>();
List<BoStelling> gewonnenStellingenMetZwartAanZet = new ArrayList<>();
List<BoStelling> verlorenStellingenMetWitAanZet = new ArrayList<>();
List<BoStelling> verlorenStellingenMetZwartAanZet = new ArrayList<>();
int aantalStellingen = 0;
void getGewonnenOfVerlorenStelling( BoStelling aBoStelling )
{
	if ( aBoStelling.getResultaat() == GEWONNEN || aBoStelling.getResultaat() == VERLOREN )
	{
		aantalStellingen++;
		if ( aantalStellingen % 10000 == 0 )
		{
			System.out.println( aantalStellingen );
		}
		if ( aBoStelling.getResultaat() == GEWONNEN )
		{
			if ( aBoStelling.getAanZet() == WIT)
			{
				gewonnenStellingenMetWitAanZet.add( aBoStelling );
			}
			else
			{
				gewonnenStellingenMetZwartAanZet.add( aBoStelling );
			}
		}
		if ( aBoStelling.getResultaat() == VERLOREN )
		{
			if ( aBoStelling.getAanZet() == WIT)
			{
				verlorenStellingenMetWitAanZet.add( aBoStelling );
			}
			else
			{
				verlorenStellingenMetZwartAanZet.add( aBoStelling );
			}
		}
		for ( BoStelling boStelling : verlorenStellingenMetWitAanZet )
		{
			if ( boStelling.getResultaat() == GEWONNEN )
			{
				System.out.println( "Gewonnen gevonden" );
			}
		}
	}
}
//@Test
public void testGewonnenOfVerlorenStellingen()
{
	dbs.markeerWitEnZwartPass( this::getGewonnenOfVerlorenStelling );
	int index = 0;
	for ( BoStelling boStelling : verlorenStellingenMetWitAanZet )
	{ 
		System.out.println( index + " " + boStelling );
		index++;
		if ( index >= 1000 )
		{
			break;
		}
	}
}

}
