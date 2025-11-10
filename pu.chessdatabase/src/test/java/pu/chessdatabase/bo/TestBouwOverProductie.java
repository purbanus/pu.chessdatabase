package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.PassType;

import lombok.Data;

@SuppressWarnings( "unused" ) // Dit gaat over die jupiter.api.Assertions
@SpringBootTest
@Data
public class TestBouwOverProductie
{
public static final boolean DO_PRINT = false;
@Autowired private Dbs dbs;
@Autowired private Bouw bouw;
@Autowired private Config config;
int grootste = Integer.MIN_VALUE;
List<BoStelling> grootsten = new ArrayList<>();
List<BoStelling> grootstenMinEen = new ArrayList<>();
String savedConfigString;

@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	dbs.open();
}
@AfterEach
public void destroy()
{
	dbs.close();
	config.switchConfig( savedConfigString );
}

void vindGrootste( BoStelling aBoStelling )
{
	int aantalZetten = aBoStelling.getAantalZetten();
	if ( aantalZetten > grootste )
	{
		grootste = aantalZetten;
	}
}
void vindGrootsten( BoStelling aBoStelling )
{
	if ( aBoStelling.getAantalZetten() == grootste )
	{
		grootsten.add(  aBoStelling );
	}
}
void vindGrootstenMinEen( BoStelling aBoStelling )
{
	if ( aBoStelling.getAantalZetten() == grootste - 1 )
	{
		grootstenMinEen.add(  aBoStelling );
	}
}
void doNothing( int aStellingTeller, int [][] aTellingen )
{
}
@Test
public void testGrootsteAantalZetten()
{
	getConfig().switchConfig( "KTK" );
	dbs.setReport( Integer.MAX_VALUE, this::doNothing, true );
	grootste = Integer.MIN_VALUE;
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootste );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootsten );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootstenMinEen );
	if ( DO_PRINT )
	{
		System.out.println( "Grootste aantal zetten tot mat: " + grootste );
		System.out.println( "Aantal stellingen: " + grootsten.size() );
		System.out.println(  grootsten );
		System.out.println( "Aantal min-1-stellingen: " + grootstenMinEen.size() );
		System.out.println(  grootstenMinEen );
	}
	assertThat( grootste, is (19 ) );
	assertThat( grootsten.size(), is (96 ) );
	assertThat( grootstenMinEen.size(), is (1119 ) );
}


}