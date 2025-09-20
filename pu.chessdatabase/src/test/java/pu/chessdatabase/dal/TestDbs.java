package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import pu.chessdatabase.util.Vector;

public class TestDbs
{
Dbs dbs = new Dbs(); // @@NOG Auto

@Test
public void testResultaatRange()
{
	assertThat( dbs.ResultaatRange.getMinimum(), is( 0 ) );
	assertThat( dbs.ResultaatRange.getMaximum(), is( 3 ) );
}

@Test
public void testClearTellers()
{
	dbs.ClearTellers();
	for ( int x = dbs.ResultaatRange.getMinimum(); x < dbs.ResultaatRange.getMaximum() + 1; x++ )
	{
		assertThat( dbs.Rpt[x], is( 0L ) );
	}
	assertThat( dbs.RptTeller, is( 0 ) );
}
@Test
public void testCreateTrfTabel()
{
	// Laten we beginnen in oktant 1
	int oktant = 1;
	
	for ( int rij = 0; rij < 8; rij++ ) // @@NOG rijen?
	{
		for ( int kol = 0; kol < 8; kol++ ) // @@NOG kolommen?
		{
			Vector Vres = new Vector( kol, rij );
			Vres = dbs.MatrixTabel[oktant].multiply( Vres );
			// MatrixTabel[1] = new Matrix( new Vector[] { new Vector( 1, 0), new Vector( 0, 1) }),
			// Oftewel Matrix (1 0)
			//                (0 1)
			// Oftewel de identity matrix
			Vres = Vres.add( dbs.TranslatieTabel[oktant] );
			// TranslatieTabel[1] = 	new Vector( 0, 0),
			int oudVeld = kol + 16 * rij;
			int newVeld = Vres.get( 0 ) + 8 * Vres.get( 1 );
			dbs.TrfTabel[oktant][oudVeld] = newVeld;
		}
	}

}
//@Test
public void printTrfTabel()
{
	for ( int oktant = 1; oktant < dbs.OKTANTEN; oktant++ ) // @@NOG CHECK is die grens goed of moet het <= zijn
	{
		for ( int x = dbs.Veld.getMinimum(); x < dbs.Veld.getMaximum(); x++ )
		{
//			Vres = new Vector( x, y );
//			Vres = MatrixTabel[oktant].multiply( Vres );
//			Vres = Vres.add( TranslatieTabel[oktant] );
//			int oudVeld = x + 16 * y;
//			int newVeld = Vres.get( 0 ) + 8 * Vres.get( 1 );
//			TrfTabel[oktant][oudVeld] = newVeld;
			System.out.print( Integer.toHexString( dbs.TrfTabel[oktant][x] ) + " " );
		}
		System.out.println();
	}
}
/*
public static final int [] OktTabel = {
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   6,6,6,6,5,5,5,5
};
*/
/*
 7  8  9  a  b  c  d  e 0 0 0 0 0 0 0 0 
 f 10 11 12 13 14 15 16 0 0 0 0 0 0 0 0 
17 18 19 1a 1b 1c 1d 1e 0 0 0 0 0 0 0 0 
1f 20 21 22 23 24 25 26 0 0 0 0 0 0 0 0 
27 28 29 2a 2b 2c 2d 2e 0 0 0 0 0 0 0 0 
2f 30 31 32 33 34 35 36 0 0 0 0 0 0 0 0 
37 38 39 3a 3b 3c 3d 3e 0 0 0 0 0 0 0 0 
3f 40 41 42 43 44 45 

38 37 36 35 34 33 32 31 0 0 0 0 0 0 0 0 40 3f 3e 3d 3c 3b 3a 39 0 0 0 0 0 0 0 0 48 47 46 45 44 43 42 41 0 0 0 0 0 0 0 0 50 4f 4e 4d 4c 4b 4a 49 0 0 0 0 0 0 0 0 58 57 56 55 54 53 52 51 0 0 0 0 0 0 0 0 60 5f 5e 5d 5c 5b 5a 59 0 0 0 0 0 0 0 0 68 67 66 65 64 63 62 61 0 0 0 0 0 0 0 0 70 6f 6e 6d 6c 6b 6a 
3f 37 2f 27 1f 17 f 7 0 0 0 0 0 0 0 0 40 38 30 28 20 18 10 8 0 0 0 0 0 0 0 0 41 39 31 29 21 19 11 9 0 0 0 0 0 0 0 0 42 3a 32 2a 22 1a 12 a 0 0 0 0 0 0 0 0 43 3b 33 2b 23 1b 13 b 0 0 0 0 0 0 0 0 44 3c 34 2c 24 1c 14 c 0 0 0 0 0 0 0 0 45 3d 35 2d 25 1d 15 d 0 0 0 0 0 0 0 0 46 3e 36 2e 26 1e 16 
3f 37 2f 27 1f 17 f 7 0 0 0 0 0 0 0 0 3e 36 2e 26 1e 16 e 6 0 0 0 0 0 0 0 0 3d 35 2d 25 1d 15 d 5 0 0 0 0 0 0 0 0 3c 34 2c 24 1c 14 c 4 0 0 0 0 0 0 0 0 3b 33 2b 23 1b 13 b 3 0 0 0 0 0 0 0 0 3a 32 2a 22 1a 12 a 2 0 0 0 0 0 0 0 0 39 31 29 21 19 11 9 1 0 0 0 0 0 0 0 0 38 30 28 20 18 10 8 
38 37 36 35 34 33 32 31 0 0 0 0 0 0 0 0 30 2f 2e 2d 2c 2b 2a 29 0 0 0 0 0 0 0 0 28 27 26 25 24 23 22 21 0 0 0 0 0 0 0 0 20 1f 1e 1d 1c 1b 1a 19 0 0 0 0 0 0 0 0 18 17 16 15 14 13 12 11 0 0 0 0 0 0 0 0 10 f e d c b a 9 0 0 0 0 0 0 0 0 8 7 6 5 4 3 2 1 0 0 0 0 0 0 0 0 0 ffffffff fffffffe fffffffd fffffffc fffffffb fffffffa 
7 8 9 a b c d e 0 0 0 0 0 0 0 0 ffffffff 0 1 2 3 4 5 6 0 0 0 0 0 0 0 0 fffffff7 fffffff8 fffffff9 fffffffa fffffffb fffffffc fffffffd fffffffe 0 0 0 0 0 0 0 0 ffffffef fffffff0 fffffff1 fffffff2 fffffff3 fffffff4 fffffff5 fffffff6 0 0 0 0 0 0 0 0 ffffffe7 ffffffe8 ffffffe9 ffffffea ffffffeb ffffffec ffffffed ffffffee 0 0 0 0 0 0 0 0 ffffffdf ffffffe0 ffffffe1 ffffffe2 ffffffe3 ffffffe4 ffffffe5 ffffffe6 0 0 0 0 0 0 0 0 ffffffd7 ffffffd8 ffffffd9 ffffffda ffffffdb ffffffdc ffffffdd ffffffde 0 0 0 0 0 0 0 0 ffffffcf ffffffd0 ffffffd1 ffffffd2 ffffffd3 ffffffd4 ffffffd5 
0 8 10 18 20 28 30 38 0 0 0 0 0 0 0 0 ffffffff 7 f 17 1f 27 2f 37 0 0 0 0 0 0 0 0 fffffffe 6 e 16 1e 26 2e 36 0 0 0 0 0 0 0 0 fffffffd 5 d 15 1d 25 2d 35 0 0 0 0 0 0 0 0 fffffffc 4 c 14 1c 24 2c 34 0 0 0 0 0 0 0 0 fffffffb 3 b 13 1b 23 2b 33 0 0 0 0 0 0 0 0 fffffffa 2 a 12 1a 22 2a 32 0 0 0 0 0 0 0 0 fffffff9 1 9 11 19 21 29 

 */
}
