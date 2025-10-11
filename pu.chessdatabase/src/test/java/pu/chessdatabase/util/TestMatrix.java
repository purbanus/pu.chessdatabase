package pu.chessdatabase.util;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.dal.Dbs;

public class TestMatrix
{
@Autowired private Dbs dbs;
@Autowired private Partij partij;

@Test
public void testMultiplyVector()
{
	Vector v1 = new Vector( 1, 2 );
	Vector v2 = new Vector( 3, 4 );
	Matrix matrix = new Matrix( new Vector [] { v1, v2 } );
	Vector actual = matrix.multiply( new Vector( 5, 6 ) );
	Vector expected = new Vector( 17, 39 );
	assertThat( actual, is( expected ) );
}
@Test
public void testMultiplyMatrix()
{
	Vector v1 = new Vector( 1, 2 );
	Vector v2 = new Vector( 3, 4 );
	Matrix matrixA = new Matrix( new Vector [] { v1, v2 } );
	v1 = new Vector( 5, 6 );
	v2 = new Vector( 7, 8 );
	Matrix matrixB = new Matrix( new Vector [] { v1, v2 } );
	Matrix actual = matrixA.multiply( matrixB );
	v1 = new Vector( 19, 22 );
	v2 = new Vector( 43, 50 );
	Matrix expected = new Matrix( new Vector [] { v1, v2 } );
	assertThat( actual, is( expected ) );
}

int [][] transformeerTabel( int [][] tabel, Matrix matrix, Vector aTranslatieVector )
{
	int [][] getransformeerdeTabel = new int[8][8];
	for ( int rij = 0; rij < tabel.length; rij++ )
	{
		for ( int kol = 0; kol < tabel.length; kol++ )
		{
			Vector transformed = matrix.multiply( new Vector( rij, kol ) ).add( aTranslatieVector );
			try
			{
				getransformeerdeTabel[rij][kol] = tabel[transformed.get( 0 )][transformed.get( 1 )];
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	return getransformeerdeTabel;
}
private void printMatrices( int aOktant, int[][] aTabel, int[][] aGetransformeerdeTabel )
{
	System.out.println( "Oktant: " + aOktant );
	for ( int rij = 0; rij < aTabel.length; rij++ )
	{
		printRij( aTabel, rij );
		System.out.print( "    " );
		printRij( aGetransformeerdeTabel, rij );
		System.out.println();
	}
	System.out.println();
}
void printRij( int [] [] aTabel, int rij )
{
	for ( int kol = 0; kol < aTabel.length; kol++ )
	{
		try
		{
			int veld = partij.veldToHexGetal( aTabel[rij][kol] );
			if ( veld < 10 )
			{
				System.out.print( " " );
			}
			System.out.print( veld + " " );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
/* Zo ziet hij eruit met de a-lijn onderaan
6,6,6,6,5,5,5,5
7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
Oktant 1 = Identiteit
Oktant 2 = Spiegeling in de x-as 
Oktant 3 = Rotatie om +90 graden (tegenkloks)
Oktant 4 = Spiegeling in de diagonaal a1-h8
Oktant 5 = Rotatie van +180 graden, oftewel spiegeling in de x-as gevolgd door een spiegeling in de y-as,
           of andersom
Oktant 6 = Spiegeling in de y-as
Oktant 7 = Rotatie om +270 graden (ofwel -90)
Oktant 8 = Spiegeling in de diagonaal a8-h1
*/

//@Test
public void testTransformaties()
{
	int [][] tabel = new int [][] {
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }, 
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }, 
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
		new int [] {0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00 },
		new int [] {0x00, 0x00, 0x08, 0x09, 0x00, 0x00, 0x00, 0x00 },
		new int [] {0x00, 0x05, 0x06, 0x07, 0x00, 0x00, 0x00, 0x00 },
		new int [] {0x01, 0x02, 0x03, 0x04, 0x00, 0x00, 0x00, 0x00 },
	};
	for ( int oktant = 1; oktant <= 8; oktant++ )
	{
		Matrix matrix = dbs.MATRIX_TABEL[oktant];
		Vector translatieVector = dbs.TRANSLATIE_TABEL[oktant];
		int [][] getransformeerdeTabel = transformeerTabel( tabel, matrix, translatieVector );
		printMatrices( oktant, tabel, getransformeerdeTabel );
	}
}
//@Test
public void testSpiegelingInDeYas()
{
	int [][] tabel = new int [][] {
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }, 
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }, 
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
		new int [] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
		new int [] {0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00 },
		new int [] {0x00, 0x00, 0x00, 0x00, 0x09, 0x08, 0x00, 0x00 },
		new int [] {0x00, 0x00, 0x00, 0x00, 0x07, 0x06, 0x05, 0x00 },
		new int [] {0x00, 0x00, 0x00, 0x00, 0x04, 0x03, 0x02, 0x01 },
	};
	Matrix matrix = dbs.MATRIX_TABEL[2];
	Vector translatieVector = dbs.TRANSLATIE_TABEL[2];
	int [] [] getransformeerdeTabel = transformeerTabel( tabel, matrix, translatieVector );
	printMatrices( 2, tabel, getransformeerdeTabel );
}
@Test
public void testMultiplyMatrices()
{
	Vector v1 = new Vector( 1, 0 );
	Vector v2 = new Vector( 0, -1 );
	Matrix matrixA = new Matrix( new Vector [] { v1, v2 } );
	v1 = new Vector( -1, 0 );
	v2 = new Vector( 0, 1 );
	Matrix matrixB = new Matrix( new Vector [] { v1, v2 } );
	Matrix actual = matrixA.multiply( matrixB );
	v1 = new Vector( -1, 0 );
	v2 = new Vector( 0, -1 );
	Matrix expected = new Matrix( new Vector [] { v1, v2 } );
	assertThat( actual, is( expected ) );

	Matrix actual2 = matrixB.multiply( matrixA );
	assertThat( actual2, is( expected ) );
}

}
