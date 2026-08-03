package pu.chessdatabase.dbs;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.services.Matrix;
import pu.services.Range;
import pu.services.Vector;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AlleenStukkenTransformator extends AbstractTransformator
{
public static final int OKTANTEN = 8;

/**==============================================================================================================
* Oktantentabel. Deze wordt gebruikt om te kijken in welk oktant een stuk (inz. de witte koning) zich bevindt.
* 0 = foutkode, daar wordt in Cardinaliseer() op getest.
* oktant 1 - Identieke transformatie
* oktant 2 - Spiegeling in de y-as
* oktant 3 - Rotatie van -90 graden
* oktant 4 - Spiegeling in de diagonaal a8-h1
* oktant 5 - Spiegeling in de x-as gevolgd door een spiegeling in de y-as,
*            oftewel een rotatie over 180 graden
* oktant 6 - Spiegeling in de x-as
* oktant 7 - Rotatie over +90 graden
* oktant 8 - Spiegeling in de diagonaal a1-h8
*==============================================================================================================*/
public static final int [] OKTANTEN_TABEL = {
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   6,6,6,6,5,5,5,5
};
/*
 * Zo ziet hij eruit met de a-lijn onderaan
   6,6,6,6,5,5,5,5
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
 */

/**========================================================================================
* Transformatietabel voor WK. Nadat WK is getransformeerd naar het juiste oktant,
* moet hij nog naar de speciale VM-kodering (0..9) worden gebracht. Dat gebeurt hiermee
* 10 = foutkode, wordt in VMStelling op getest.
*========================================================================================*/
public static final int [] TRANSFORM_WK = {
	 0, 1, 2, 3,10,10,10,10,
	10, 4, 5, 6,10,10,10,10,
	10,10, 7, 8,10,10,10,10,
	10,10,10, 9,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10
};
public static final Matrix [] MATRIX_TABEL = {
	null, // Dit heeft een matrix per oktant, en oktant 0 bestaat niet
	new Matrix( new Vector[] { new Vector( 1, 0), new Vector( 0, 1) }),
	new Matrix( new Vector[] { new Vector(-1, 0), new Vector( 0, 1) }),
	new Matrix( new Vector[] { new Vector( 0, 1), new Vector(-1, 0) }),
	new Matrix( new Vector[] { new Vector( 0,-1), new Vector(-1, 0) }),
	new Matrix( new Vector[] { new Vector(-1, 0), new Vector( 0,-1) }),
	new Matrix( new Vector[] { new Vector( 1, 0), new Vector( 0,-1) }),
	new Matrix( new Vector[] { new Vector( 0,-1), new Vector( 1, 0) }),
	new Matrix( new Vector[] { new Vector( 0, 1), new Vector( 1, 0) })
};
public static final Vector [] TRANSLATIE_TABEL = new Vector [] {
	null, // Dit heeft een vector per oktant, en oktant 0 bestaat niet
	new Vector( 0, 0),
	new Vector( 7, 0),
	new Vector( 0, 7),
	new Vector( 7, 7),
	new Vector( 7, 7),
	new Vector( 0, 7),
	new Vector( 7, 0),
	new Vector( 0, 0)
};
Range oktantRange = new Range( 1, OKTANTEN );
Range rijRange = new Range( 0, 7 );
Range kolRange = new Range( 0, 7 );
@ToStringExclude
@EqualsAndHashCode.Exclude
int[][] transformatieTabel = new int [OKTANTEN + 1][Dbs.VELD_RANGE.getMaximum() + 1];

public AlleenStukkenTransformator()
{
	super();
	createTransformatieTabel();
}
void createTransformatieTabel()
{
	Vector Vres;
	for ( int oktant : oktantRange )
	{
		for ( int rij : rijRange )
		{
			for ( int kol: kolRange )
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
	return CVT_WK[aVmStellingWk];
}


/**
 * -------- Stelling van Dbs-formaat naar VM-formaat ------
 */
@Override
public VMStelling boStellingToVmStelling( BoStelling aStelling )
{
	int oktant = OKTANTEN_TABEL[aStelling.getWk()];
	int trfWk = transformatieTabel[oktant][aStelling.getWk()];
	@SuppressWarnings( "unused" )
	int trftrfWk = TRANSFORM_WK[trfWk];
	
	VMStelling vmStelling = spiegelEnRoteer( aStelling );
	vmStelling.setWk( TRANSFORM_WK[ vmStelling.getWk()] );
	return vmStelling;
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
int getOktant( BoStelling aStelling )
{
	int oktant = OKTANTEN_TABEL[aStelling.getWk()];
	if ( oktant < 1 || oktant > 8)
	{
		throw new RuntimeException( "Foutief oktant in Dbs.spiegelEnRoteer voor WK op " + Integer.toHexString( aStelling.getWk() ) );
	}
	return oktant;
}

}
