package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Constants.*;

import pu.chessdatabase.bo.BoStelling;
import pu.services.Matrix;
import pu.services.Range;
import pu.services.Vector;

import lombok.Data;

@Data
public class MetPionnenTransformator extends AbstractTransformator
{
public static final int OKTANTEN = 2;

/**==============================================================================================================
* Oktantentabel. Deze wordt gebruikt om te kijken in welk oktant een stuk (inz. de witte koning) zich bevindt.
* 0 = foutkode, daar wordt in Cardinaliseer() op getest.
* oktant 1 - Identieke transformatie
* oktant 2 - Spiegeling in de y-as
*==============================================================================================================*/
public static final int [] OKTANTEN_TABEL =
{
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2
};
/**========================================================================================
* Transformatietabel voor WK. Nadat WK is getransformeerd naar het juiste oktant,
* moet hij nog naar de speciale VM-kodering (0..9) worden gebracht. Dat gebeurt hiermee
* 80 = foutkode, wordt in VMStelling op getest.
*========================================================================================*/
//public static final int [] WK_FROM_VM_TO_BO = {
////	 0x00, 0x01, 0x02, 0x03,
////	 0x10, 0x11, 0x12, 0x13,
////	 0x20, 0x21, 0x22, 0x23,
////	 0x30, 0x31, 0x32, 0x33,
////	 0x40, 0x41, 0x42, 0x43,
////	 0x50, 0x51, 0x52, 0x53,
////	 0x60, 0x61, 0x62, 0x63,
////	 0x70, 0x71, 0x72, 0x73,
//	 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
//	 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
//	 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27,
//	 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
//	 0x40, 0x41, 0x34, 0x43, 0x44, 0x45, 0x46, 0x47,
//	 0x50, 0x51, 0x35, 0x53, 0x54, 0x55, 0x56, 0x57,
//	 0x60, 0x61, 0x16, 0x63, 0x64, 0x65, 0x66, 0x67,
//	 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77,
//};
public static final int [] WK_TO_LINEAR = 
{
	 0x00, 0x01, 0x02, 0x03, 0xff, 0xff, 0xff, 0xff,
	 0x04, 0x05, 0x06, 0x07, 0xff, 0xff, 0xff, 0xff,
	 0x08, 0x09, 0x0a, 0x0b, 0xff, 0xff, 0xff, 0xff,
	 0x0c, 0x0d, 0x0e, 0x0f, 0xff, 0xff, 0xff, 0xff,
	 0x10, 0x11, 0x12, 0x13, 0xff, 0xff, 0xff, 0xff,
	 0x14, 0x15, 0x16, 0x17, 0xff, 0xff, 0xff, 0xff,
	 0x18, 0x19, 0x1a, 0x1b, 0xff, 0xff, 0xff, 0xff,
	 0x1c, 0x1d, 0x1e, 0x1f, 0xff, 0xff, 0xff, 0xff,
};
public static final int [] WK_FROM_LINEAR = 
{
	 0x00, 0x01, 0x02, 0x03,
	 0x08, 0x09, 0x0a, 0x0b,
	 0x10, 0x11, 0x12, 0x13,
	 0x18, 0x19, 0x1a, 0x1b,
	 0x20, 0x21, 0x22, 0x23,
	 0x28, 0x29, 0x2a, 0x2b,
	 0x30, 0x31, 0x32, 0x33,
	 0x38, 0x39, 0x3a, 0x3b,
};
public static final Matrix [] MATRIX_TABEL =
{
	null, // Dit heeft een matrix per oktant, en oktant 0 bestaat niet
	// De identiteitsmatrix
	new Matrix( new Vector[] { new Vector( 1, 0 ), new Vector( 0, 1 ) } ),
	// De spiegeling in de y-as matrix
	new Matrix( new Vector[] { new Vector(-1, 0 ), new Vector( 0, 1 ) } ),
};
public static final Vector [] TRANSLATIE_TABEL = new Vector [] {
	null, // Dit heeft een vector per oktant, en oktant 0 bestaat niet
	new Vector( 0, 0),
	new Vector( 7, 0),
};
public static final Range OKTANT_RANGE = new Range( 1, OKTANTEN );
int[][] transformatieTabel = new int [OKTANTEN + 1][VM_VELD_RANGE.getMaximum() + 1];
public MetPionnenTransformator()
{
	super();
	createTransformatieTabel();
}
void createTransformatieTabel()
{
	Vector Vres;
	for ( int oktant : OKTANT_RANGE )
	{
		for ( int rij : RIJ_RANGE )
		{
			for ( int kol: KOL_RANGE )
			{
				Vres = new Vector( kol, rij );
				Vres = MATRIX_TABEL[oktant].multiply( Vres );
				Vres = Vres.add( TRANSLATIE_TABEL[oktant] );
				int oudVeld = kol + 16 * rij;
				int newVeld = Vres.get( 0 ) + 8 * Vres.get( 1 );
				transformatieTabel[oktant][oudVeld] = newVeld;
			}
		}
	}
}

@Override
public int vmStellingWkToBoStellingWk( int aVmStellingWk )
{
	if ( ! WK_MET_PIONNEN_ITERATOR.contains( aVmStellingWk ) )
	{
		throw new RuntimeException( String.format( "De wk is niet een van de wk's in WkMetPionnenIterator: %d", aVmStellingWk ) );
	}
	return TRANSFORM_STUK[aVmStellingWk];
}
@Override
public int vmStellingWkToLinear( int aVmStellingWk )
{
	int wkLinear = WK_TO_LINEAR[aVmStellingWk];
	if ( wkLinear == 0xff )
	{
		throw new RuntimeException( String.format( "De wk is niet een van de wk's in WkMetPionnenIterator: %d", aVmStellingWk ) );
	}
	return wkLinear;
}
@Override
public int vmStellingWkFromLinear( int aVmStellingWk )
{
	if ( aVmStellingWk < 0 || aVmStellingWk >= MAX_WK_MET_PIONNEN )
	{
		throw new RuntimeException( String.format( "De wk zit niet in de range van de lineaire wk's: 0-31: %d", aVmStellingWk ) );
	}
	return WK_FROM_LINEAR[aVmStellingWk];
}

/**
 * -------- Stelling van Bo-formaat naar VM-formaat ------
 */
@Override
public VMStelling boStellingToVmStelling( BoStelling aStelling )
{
	return spiegelEnRoteer( aStelling );
}
VMStelling spiegelEnRoteer( BoStelling aStelling )
{
	int oktant = getOktant( aStelling );
	return spiegelEnRoteer( aStelling, oktant );
}
VMStelling spiegelEnRoteer( BoStelling aStelling, int aOktant )
{
	return VMStelling.builder()
		.wk( transformatieTabel[aOktant][aStelling.getWk()] )
		.zk( transformatieTabel[aOktant][aStelling.getZk()] )
		.s3( transformatieTabel[aOktant][aStelling.getS3()] )
		.s4( transformatieTabel[aOktant][aStelling.getS4()] )
		.s5( transformatieTabel[aOktant][aStelling.getS5()] )
		.aanZet( aStelling.getAanZet() )
		.build();
}
int getOktant( BoStelling aBoStelling )
{
	int oktant = OKTANTEN_TABEL[aBoStelling.getWk()];
	if ( oktant < OKTANT_RANGE.getMinimum() || oktant > OKTANT_RANGE.getMaximum() )
	{
		throw new RuntimeException( "Foutief oktant in Dbs.spiegelEnRoteer voor WK op " + Integer.toHexString( aBoStelling.getWk() ) );
	}
	return oktant;
}

}
