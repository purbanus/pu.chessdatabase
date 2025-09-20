package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pu.chessdatabase.util.Matrix;
import pu.chessdatabase.util.Vector;
public class TestMatrix
{
@BeforeAll
public static void printClassPath()
{
//	StringHelper.printClassPath();
}
@Test
public void testMultiply()
{
	Vector v1 = new Vector( 1, 2 );
	Vector v2 = new Vector( 3, 4 );
	Matrix matrix = new Matrix( new Vector [] {v1, v2} );
	Vector resultaat = matrix.multiply( new Vector( 5, 6 ) );
	// @@NOG Hoe vergelijk je Vectoren?
	assertThat( resultaat.get( 0 ), is( 17 ) );
	assertThat( resultaat.get( 1 ), is( 39 ) );
}
}
