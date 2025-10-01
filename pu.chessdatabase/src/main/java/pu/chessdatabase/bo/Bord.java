package pu.chessdatabase.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Bord
{
public static final int LEEG = 0xFF;

private int [] bord = new int[0x78];

public Bord()
{
	super();
	maakBordLeeg();
}
public Bord( BoStelling aBoStelling )
{
	this();
	zetBordOp( aBoStelling );
}

/**
PROCEDURE MaakBordLeeg();
VAR x: SHORTCARD;
BEGIN
	FOR x:=0 TO 77H DO
		Bord[x]:=Leeg;
	END;
END MaakBordLeeg;
 */
/**
 * -------- Maak het bord leeg -------------------------------
 */
public void maakBordLeeg()
{
	for ( int x = 0; x < 0x78; x++ )
	{
		bord[x] = LEEG;
	}
}
/**
PROCEDURE ZetBordOp(S: Dbs.Stelling);
BEGIN
	(* eerst de stukken, dan kunnen ze eventueel uitgeveegd worden door de koningen *)
	Bord[S.s3]:=3;
	Bord[S.s4]:=4;
	Bord[S.WK]:=1;
	Bord[S.ZK]:=2;
END ZetBordOp;
 */
/**
 * -------- Zet stukken op het bord ------------------------
 */

public void zetBordOp( BoStelling aStelling )
{
	// eerst de stukken, dan kunnen ze eventueel uitgeveegd worden door de koningen 
	bord[aStelling.getS3()] = 3;
	bord[aStelling.getS4()] = 4;
	bord[aStelling.getWk()] = 1;
	bord[aStelling.getZk()] = 2;
}
/**
PROCEDURE ClrBord(S: Dbs.Stelling);
BEGIN
	Bord[S.Wk]:=Leeg;
	Bord[S.ZK]:=Leeg;
	Bord[S.s3]:=Leeg;
	Bord[S.s4]:=Leeg;
END ClrBord;
*/
/**
 * Haal ze er weer vanaf ------------------------
 */

public void clearBord( BoStelling aStelling )
{
	bord[aStelling.getS3()] = Bord.LEEG;
	bord[aStelling.getS4()] = Bord.LEEG;
	bord[aStelling.getWk()] = Bord.LEEG;
	bord[aStelling.getZk()] = Bord.LEEG;
}
public int getVeld( int aVeld )
{
	return bord[aVeld];
}
public boolean isVeldLeeg( int aVeld )
{
	return bord[aVeld] == LEEG;
}
@Override
public String toString()
{
	StringBuilder sb = new StringBuilder();
	for ( int rij = 7; rij >= 0; rij-- )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			int index = 16 * rij + kol;
			int veld = bord[index];
			String veldString;
			if ( veld == 1 ) veldString = "WK";
			else if ( veld == 2 ) veldString = "ZK";
			else if ( veld == 3 ) veldString = "WD";
			else if ( veld == 4 ) veldString = "ZT";
			else veldString = ( veld < 16 ? "0":"" ) + Integer.toHexString( veld  );
			sb.append( veldString ).append( " " );
		}
		sb.append( "\n" );
	}
	return sb.toString();
}

}
