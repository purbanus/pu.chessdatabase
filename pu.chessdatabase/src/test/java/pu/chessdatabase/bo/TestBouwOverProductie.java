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
@Autowired private Dbs dbs;
@Autowired private Bouw bouw;
@Autowired private Config config;

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
int grootste = Integer.MIN_VALUE;
List<BoStelling> grootsten = new ArrayList<>();
List<BoStelling> grootstenMinEen = new ArrayList<>();
void vindGrootste( BoStelling aBoStelling )
{
	if ( aBoStelling.getAantalZetten() > grootste )
	{
		grootste = aBoStelling.getAantalZetten();
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

@Test
public void testGrootsteAantalZetten()
{
	getConfig().switchConfig( "KTK" );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootste );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootsten );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootstenMinEen );
	System.out.println( "Grootste aantal zetten tot mat: " + grootste );
	System.out.println( "Aantal stellingen: " + grootsten.size() );
	System.out.println(  grootsten );
	System.out.println( "Aantal min-1-stellingen: " + grootstenMinEen.size() );
	System.out.println(  grootstenMinEen );
}


}