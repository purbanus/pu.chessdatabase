package pu.chessdatabase.util;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class TestVector
{
@Test
public void testAdd()
{
	Vector v1 = new Vector( 1, 2 );
	Vector v2 = new Vector( 3, 4 );
	Vector actual = v1.add( v2 );
	Vector expected = new Vector( 4, 6 );
	assertThat( actual, is( expected ) );
}
}
