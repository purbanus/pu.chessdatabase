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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.PassType;

@SuppressWarnings( "unused" ) // Dit gaat over die jupiter.api.Assertions
@SpringBootTest
public class TestBouwOverProductie
{
@Autowired private Dbs dbs;
@Autowired private Bouw bouw;

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

//@Test
public void testGrootsteAantalZetten()
{
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootste );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::vindGrootsten );
	System.out.println( "Grootste aantal zetten tot mat: " + grootste );
	System.out.println( "Aantal stellingen: " + grootsten.size() );
	System.out.println(  grootsten );
}


}