package pu.chessdatabase.bo;

import static pu.chessdatabase.dbs.Constants.*;

import pu.services.Range;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Bord
{
public static final int LEEG = 0xFF;
public static final int MAX_BORD = 0x78;
public static final Range BORD_RANGE = new Range( 0, MAX_BORD - 1 );
private int [] bord = new int[MAX_BORD];
private Stukken stukken;
public Bord( Stukken aStukken )
{
	super();
	stukken = aStukken;
	maakBordLeeg();
}
public Bord( Stukken aStukken, BoStelling aBoStelling )
{
	this( aStukken );
	zetBordOp( aBoStelling );
}
public int getAantalStukken()
{
	return getStukken().getAantalStukken();
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
	for ( int x : BORD_RANGE )
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
public void setVeld( int aVeld, int aValue )
{
	bord[aVeld] = aValue;
}
public int getAlfaVeld( String aAlfaVeld )
{
	return bord[Gen.alfaToVeld(aAlfaVeld)];
}
public boolean isVeldLeeg( int aVeld )
{
	return bord[aVeld] == LEEG;
}
public int getRij( int aVeld )
{
	return aVeld / 16;
}
public int getKol( int aVeld )
{
	return aVeld % 16;
}
@Override
public String toString()
{
	StringBuilder sb = new StringBuilder();
	for ( int rij = 7; rij >= 0; rij-- )
	{
		for ( int kol : KOL_RANGE )
		{
			int index = 16 * rij + kol;
			int veld = bord[index];
			String veldString;
			switch ( veld )
			{
				case 0: veldString = getStukken().getWk().getStukString(); break;
				case 1: veldString = getStukken().getZk().getStukString(); break;
				case 2: veldString = getStukken().getS3().getStukString(); break;
				case 3: veldString = getStukken().getS4().getStukString(); break;
				case 4: veldString = getStukken().getS5().getStukString(); break;
				default: veldString = "..";
			}
			sb.append( veldString ).append( " " );
		}
		sb.append( "\n" );
	}
	return sb.toString();
}

}
