package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.configuraties.StukType.*;
import static pu.chessdatabase.dbs.Constants.*;

import org.apache.commons.lang3.builder.ToStringExclude;

import pu.chessdatabase.bo.BoStelling;
import pu.services.Matrix;
import pu.services.Range;
import pu.services.Vector;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
public static final int [] TRANSFORM_WK = {
	 0, 1, 2, 3,80,80,80,80,
	 4, 5, 6, 7,80,80,80,80,
	 8, 9,10,11,80,80,80,80,
	12,13,14,15,80,80,80,80,
	16,17,18,19,80,80,80,80,
	20,21,22,23,80,80,80,80,
	24,25,26,27,80,80,80,80,
	28,29,30,31,80,80,80,80
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
	return CVT_STUK[aVmStellingWk];
}
/**
 * -------- Stelling van Dbs-formaat naar VM-formaat ------
 */
@Override
public VMStelling boStellingToVmStelling( BoStelling aStelling )
{
	int oktant = getOktant( aStelling );
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
