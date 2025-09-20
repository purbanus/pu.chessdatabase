package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import pu.chessdatabase.util.Vector;

public class TestVector
{
@Test
public void testAdd()
{
	Vector v1 = new Vector( 1, 2 );
	Vector v2 = new Vector( 3, 4 );
	Vector resultaat = v1.add( v2 );
	// @@NOG Hoe vergelijk je Vectoren?
	assertThat( resultaat.get( 0 ), is( 4 ) );
	assertThat( resultaat.get( 1 ), is( 6 ) );
}
}
