package pu.chessdatabase.bo;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Bord
{
public static final int LEEG = 0xFF;
public static final int MAX_BORD = 0x77;

private int [] bord = new int[MAX_BORD + 1];
private int aantalStukken;
private Stukken stukken;
public Bord( int aAantalStukken, Stukken aStukken )
{
	super();
	aantalStukken = aAantalStukken;
	stukken = aStukken;
	maakBordLeeg();
}
public Bord( int aAantalStukken, Stukken aStukken, BoStelling aBoStelling )
{
	this( aAantalStukken, aStukken );
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
	for ( int x = 0; x <= MAX_BORD; x++ )
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
	bord[aStelling.getS3()] = 2;
	if ( getAantalStukken() >= 4 )
	{
		bord[aStelling.getS4()] = 3;
	}
	if ( getAantalStukken() >= 5 )
	{
		bord[aStelling.getS5()] = 4;
	}
	bord[aStelling.getWk()] = 0;
	bord[aStelling.getZk()] = 1;
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
	bord[aStelling.getS3()] = LEEG;
	bord[aStelling.getS4()] = LEEG;
	bord[aStelling.getS5()] = LEEG;
	bord[aStelling.getWk()] = LEEG;
	bord[aStelling.getZk()] = LEEG;
}
public int getVeld( int aVeld )
{
	return bord[aVeld];
}
public int getAlfaVeld( String aAlfaVeld )
{
	return bord[Gen.alfaToVeld(aAlfaVeld)];
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
			if      ( veld == 0 ) veldString = getStukken().getWk().getStukString();
			else if ( veld == 1 ) veldString = getStukken().getZk().getStukString();
			else if ( veld == 2 ) veldString = getStukken().getS3().getStukString();
			else if ( veld == 3 ) veldString = getStukken().getS4().getStukString();
			else if ( veld == 4 ) veldString = getStukken().getS5().getStukString();
			else veldString = ( veld < 16 ? "0":"" ) + Integer.toHexString( veld  );
			sb.append( veldString ).append( " " );
		}
		sb.append( "\n" );
	}
	return sb.toString();
}

}
