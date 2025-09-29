package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import java.util.BitSet;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;

@Component
public class Gen
{
// Bij Stukken en Kleuren wordt element 0 niet gebruikt
public static final int MIN_STUKNUMMER = 1;
public static final int MAX_STUKNUMMER = 4;
public static final int MIN_KLEURNUMMER = 1;
public static final int MAX_KLEURNUMMER = 4;
//public static final int MAX_STUKKEN = 4;

public static final StukType [] Stukken = { null, StukType.Koning, StukType.Koning, StukType.Dame, StukType.Toren };
public static final Kleur [] Kleuren = { null, Wit, Zwart, Wit, Zwart };
public static final BitSet BuitenBord = bitSetOfInt( 0x88 );
public static final BitSet Nul = bitSetOfInt( 0x00 );
public static final int Leeg = 0xFF;

public static final int [] Krichting = new int [] { 0x01, 0x11, 0x10, 0x0F,	-0x01,-0x11,-0x10,-0x0F };
public static final int [] Trichting = new int [] {	0x01, 0x10,-0x01,-0x10,	0x00, 0x00, 0x00, 0x00 }; //@@NOG Je kunt die laatste 4 gewoon weglaten denk ik
public static final int [] Lrichting = new int [] { 0x11, 0x0F,-0x11,-0x0F,	0x00, 0x00, 0x00, 0x00 };
public static final int [] Prichting = new int [] { 0x12, 0x21, 0x1F, 0x0E,	-0x12,-0x21,-0x1F,-0x0E };

public static final String [] Notatie = new String [] {
	"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "??", "??", "??", "??", "??", "??", "??", "??",
	"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "??", "??", "??", "??", "??", "??", "??", "??",
	"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "??", "??", "??", "??", "??", "??", "??", "??",
	"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "??", "??", "??", "??", "??", "??", "??", "??",
	"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "??", "??", "??", "??", "??", "??", "??", "??",
	"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "??", "??", "??", "??", "??", "??", "??", "??",
	"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "??", "??", "??", "??", "??", "??", "??", "??",
	"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
};
@Autowired private Dbs dbs;

/**
 * Bord
 * 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
 * 10 11 12 13 14 15 16 17 18 19 1A 1B 1C 1D 1E 1F
 * 20 21 22 23 24 25 26 27 28 29 2A 2B 2C 2D 2E 2F
 * etc
 */
Stuk [] StukTabel = new Stuk [5]; // 0 wordt niet gebruikt
int [] Bord = new int[0x78];
public void printBord()
{
	StringBuilder sb = new StringBuilder();
	for ( int rij = 7; rij >= 0; rij-- )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			int index = 16 * rij + kol;
			int veld = Bord[index];
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
	System.out.println( sb );
}

static BitSet bitSetOfInt( int aInt )
{
	return bitSetOfByte( (byte) aInt );
}
static BitSet bitSetOfByte( byte aByte )
{
	byte [] bytes = new byte [] { aByte };
	return BitSet.valueOf( bytes );
}
static BitSet veldToBitSetAndBuitenBord( int Veld )
{
	BitSet veldSet = bitSetOfInt( Veld );
	veldSet.and( BuitenBord );
	return veldSet;
}

public Gen()
{
	VulStukTabel();
	MaakBordLeeg();
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
public void MaakBordLeeg()
{
	for ( int x = 0; x < 0x78; x++ )
	{
		Bord[x] = Leeg;
	}
}
/**
 * PROCEDURE VulStukTabel();
VAR x: StukNummer;
BEGIN
	FOR x:=1 TO 4 DO
		WITH StukTabel[x] DO
			Soort:=Stukken[x];
			Kleur:=Kleuren[x];
			CASE Soort OF
			|	Koning: Richting:=Krichting; AtlRicht:=8; Meer:=FALSE; StukAfk:='K';
			|	Dame  : Richting:=Krichting; AtlRicht:=8; Meer:=TRUE ; StukAfk:='D';
			|	Toren : Richting:=Trichting; AtlRicht:=4; Meer:=TRUE ; StukAfk:='T';
			|	Loper : Richting:=Lrichting; AtlRicht:=4; Meer:=TRUE ; StukAfk:='L';
			|	Paard : Richting:=Prichting; AtlRicht:=8; Meer:=FALSE; StukAfk:='P';
			END;
			IF Kleur = Wit THEN
				Knummer:=1;
			ELSE
				Knummer:=2;
			END;
		END;
	END;
END VulStukTabel;
 */
public void VulStukTabel()
{
	// StukTabel[0] is er niet
	for ( int x = 1; x <= 4; x++ )
	{
		Stuk stuk = Stuk.builder()
			.Soort( Stukken[x] )
			.Kleur( Kleuren[x] )
			.Knummer( Kleuren[x] == Wit ? 1 : 2 )
			.build();
		StukTabel[x] = stuk;
		switch ( stuk.getSoort() )
		{
			case StukType.Koning: stuk.setRichting( Krichting ); stuk.setAtlRicht( 8 ); stuk.setMeer( false ); break;
			case StukType.Dame  : stuk.setRichting( Krichting ); stuk.setAtlRicht( 8 ); stuk.setMeer( true  ); break;
			case StukType.Toren : stuk.setRichting( Trichting ); stuk.setAtlRicht( 4 ); stuk.setMeer( true  ); break;
			case StukType.Loper : stuk.setRichting( Lrichting ); stuk.setAtlRicht( 4 ); stuk.setMeer( true  ); break;
			case StukType.Paard : stuk.setRichting( Prichting ); stuk.setAtlRicht( 8 ); stuk.setMeer( false ); break;
		}
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
public void ZetBordOp( BoStelling aStelling )
{
	// eerst de stukken, dan kunnen ze eventueel uitgeveegd worden door de koningen 
	Bord[aStelling.getS3()] = 3;
	Bord[aStelling.getS4()] = 4;
	Bord[aStelling.getWk()] = 1;
	Bord[aStelling.getZk()] = 2;
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

public void ClrBord( BoStelling aStelling )
{
	Bord[aStelling.getS3()] = Leeg;
	Bord[aStelling.getS4()] = Leeg;
	Bord[aStelling.getWk()] = Leeg;
	Bord[aStelling.getZk()] = Leeg;
}
/**
PROCEDURE IsGeomIllegaal(S: Dbs.Stelling): BOOLEAN;
BEGIN
	WITH S DO
		IF (WK = ZK) OR (s3 = s4) THEN RETURN(TRUE) END;
		IF (WK = s3) AND (Kleuren[3] # Wit  ) THEN RETURN(TRUE) END;
		IF (WK = s4) AND (Kleuren[4] # Wit  ) THEN RETURN(TRUE) END;
		IF (ZK = s3) AND (Kleuren[3] # Zwart) THEN RETURN(TRUE) END;
		IF (ZK = s4) AND (Kleuren[4] # Zwart) THEN RETURN(TRUE) END;
		RETURN(FALSE);
	END;
END IsGeomIllegaal;
 */
/**
 * ------------ Kijk of een stelling geometrisch illegaal is ------------
 */
public boolean IsGeomIllegaal( BoStelling S )
{
	if ( ( S.getWk() == S.getZk() ) || ( S.getS3() == S.getS4()     ) ) return true;
	if ( ( S.getWk() == S.getS3() ) && ( Kleuren[3] != Wit   ) ) return true;
	if ( ( S.getWk() == S.getS4() ) && ( Kleuren[4] != Wit   ) ) return true;
	if ( ( S.getZk() == S.getS3() ) && ( Kleuren[3] != Zwart ) ) return true;
	if ( ( S.getZk() == S.getS4() ) && ( Kleuren[4] != Zwart ) ) return true;
	return false;
}
/**
PROCEDURE IsKKschaak(S: Dbs.Stelling): BOOLEAN;
VAR x : RichtingNummer;
BEGIN
(*$O-*) (* Overflow check *)
	FOR x:=1 TO 8 DO
		IF S.ZK = S.WK + Krichting[x] THEN
			RETURN(TRUE);
		END;
	END;
	RETURN(FALSE);
(*O=*) (* Overflow check *)
END IsKKschaak;
 */
/**
 * -------- Kijk of de koningen elkaar schaak geven --------
 */
public boolean isKKSchaak( BoStelling S )
{
	for ( int x = 0; x < 8; x++ )
	{
		if ( S.getZk() == S.getWk() + Krichting[x] )
		{
			return true;
		}
	}
	return false;
}
/**
	PROCEDURE SchaakDoorStuk(StukNr: StukNummer): BOOLEAN;
	VAR x: RichtingNummer;
	BEGIN
(*$O-*) (* Overflow check *)
		WITH StukTabel[StukNr] DO
			FOR x:=1 TO AtlRicht DO
				Veld:=Sveld + Richting[x];
				IF Meer THEN
					WHILE ((BITSET(Veld) * BuitenBord) = BITSET(0)) AND (Bord[Veld] = Leeg) DO
						Veld:=Veld + Richting[x];
					END;
				END;
				IF Veld = Kveld THEN
					RETURN(TRUE);
				END;
			END;
		END;
		RETURN(FALSE);
	END SchaakDoorStuk;
(*$O=*) (* Overflow check *)
 */
/**
 * -------- Kijk of degene die aan zet is, schaak staat ----------		
 */

boolean isSchaakDoorStuk( Stuk aStuk, int aKoningsVeld, int aStukVeld )
{
	for ( int x = 0; x < aStuk.getAtlRicht(); x++ )
	{
		int Veld = aStukVeld + aStuk.getRichting()[x];
		if ( aStuk.isMeer() )
		{
			while ( veldToBitSetAndBuitenBord( Veld ).equals( Nul ) && Bord[Veld] == Leeg )
			{
				Veld += aStuk.getRichting()[x];
			}
		}
		if ( Veld == aKoningsVeld )
		{
			return true;
		}
	}
	return false;
}

/**
PROCEDURE IsSchaak(S: Dbs.Stelling): BOOLEAN;
VAR Kveld, Sveld, Veld: WerkVeld;

BEGIN
	ZetBordOp(S);
	IF S.AanZet = Wit THEN
		Kveld:=S.WK;
	ELSE
		Kveld:=S.ZK;
	END;
	IF (S.s3 # S.WK) AND (S.s3 # S.ZK) AND (StukTabel[3].Kleur # S.AanZet) THEN
		Sveld:=S.s3;
		IF SchaakDoorStuk(3) THEN
			ClrBord(S);
			RETURN(TRUE);
		END;
	END;
	IF (S.s4 # S.WK) AND (S.s4 # S.ZK) AND (StukTabel[4].Kleur # S.AanZet) THEN
		Sveld:=S.s4;
		IF SchaakDoorStuk(4) THEN
			ClrBord(S);
			RETURN(TRUE);
		END;
	END;
	ClrBord(S);
	RETURN(FALSE);
END IsSchaak;
 */
/**
 * CheckSchaakDoorStuk checkt drie dingen
 * - Of het stuk niet geslagen is door wit
 * - Of het stuk niet geslagen is door zwart
 * - Of het stuk niet aan zet is
 * Als dat allemaal waar is wordt is
*/
public boolean CheckSchaakDoorStuk( BoStelling aStelling, Stuk aStuk, int aKoningsVeld, int aStukVeld )
{
	if ( ( aStukVeld != aStelling.getWk() ) && ( aStukVeld != aStelling.getZk() ) && ( aStuk.getKleur() != aStelling.getAanZet() ) )
	{
		if ( isSchaakDoorStuk( aStuk, aKoningsVeld, aStukVeld ) )
		{
			return true;
		}
	}
	return false;
}
/**
 * -------- Kijk of degene die aan zet is, schaak staat ----------		
 */
/**
 * Dat hele schaakjes gedoe is volgens mij om illegale stellingen te ontdekken
 * @@NOG maar dat moet nog bewezen worden; die java code is er nog niet.
 */
public boolean isSchaak( BoStelling aStelling )
{
	ZetBordOp( aStelling );
	int KVeld = aStelling.getAanZet() == Wit ? aStelling.getWk() : aStelling.getZk();
	if ( CheckSchaakDoorStuk( aStelling, StukTabel[3], KVeld, aStelling.getS3() ) )
	{
		ClrBord( aStelling );
		return true;
	}
	if ( CheckSchaakDoorStuk( aStelling, StukTabel[4], KVeld, aStelling.getS4() ) )
	{
		ClrBord( aStelling );
		return true;
	}
	ClrBord( aStelling );
	return false;
}
/**
PROCEDURE AddZet(S: Dbs.Stelling; StukNr: StukNummer; Naar: WerkVeld; Zsoort: ZetSoort);
VAR x: StukNummer;
BEGIN
	IF Zsoort = SlagZet THEN
		(*---- Stop het geslagen stuk "onder" de koning ----*)
		IF S.s3 = Naar THEN
			S.s3:=S.Velden[StukTabel[3].Knummer];
		ELSIF S.s4 = Naar THEN
			S.s4:=S.Velden[StukTabel[4].Knummer];
		END;
	END;
	(*------- Sjouw geslagen stukken mee bij koningszetten -----------*)
	IF Sveld = Kveld THEN
		IF S.s3 = Kveld THEN
			S.s3:=Naar;
		END;
		IF S.s4 = Kveld THEN
			S.s4:=Naar;
		END;
	END;
	(*------ Verzet het stuk ---------*)
	S.Velden[StukNr]:=Naar;
	S.AanZet:=NOT S.AanZet;
	Dbs.Get(S);
	IF S.Resultaat # Dbs.Illegaal THEN
		INC(GZ.Aantal);
		GenZtabel[GZ.Aantal]:=S;
	END;
END AddZet;
 */
// @@NOG geen GenZRec als parm maar gewoon de nieuwe stelling retourneren
void AddZet( final BoStelling aBoStelling, int aStukNr, int aNaar, ZetSoort aZetsoort, int aKoningsVeld, int aStukVeld, GenZRec aGenZRec )
{
	BoStelling boStelling = aBoStelling.clone();
	if ( aZetsoort == ZetSoort.SlagZet )
	{
		//---- Stop het geslagen stuk "onder" de koning ----
		// Je gaat er hier van uit dat s3 wit is en s4 zwart
		if ( boStelling.getS3() == aNaar )
		{
			boStelling.setS3( boStelling.getWk() );
		}
		else if ( boStelling.getS4() == aNaar )
		{
			boStelling.setS4( boStelling.getZk() );
		}
	}
	//------- Sjouw geslagen stukken mee bij koningszetten -----------
	if ( aStukVeld == aKoningsVeld )
	{
		if ( boStelling.getS3() == aKoningsVeld )
		{
			boStelling.setS3( aNaar );
		}
		if ( boStelling.getS4() == aKoningsVeld )
		{
			boStelling.setS4( aNaar );
		}
	}
	//------ Verzet het stuk ---------
	switch ( aStukNr )
	{
		case 1: boStelling.setWk( aNaar ); break;
		case 2: boStelling.setZk( aNaar ); break;
		case 3: boStelling.setS3( aNaar ); break;
		case 4: boStelling.setS4( aNaar ); break;
	}
	boStelling.setAanZet( boStelling.getAanZet() == Wit ? Zwart : Wit );
	dbs.Get( boStelling );
	if ( boStelling.getResultaat() != ResultaatType.Illegaal )
	{
		aGenZRec.add( boStelling );
	}
}
/**
PROCEDURE GenZperStuk(StukNr: StukNummer);

VAR x: RichtingNummer;
BEGIN
(*$O-*) (* Overflow check *)
	WITH StukTabel[StukNr] DO
		FOR x:=1 TO AtlRicht DO
			Veld:=Sveld + Richting[x];
			IF Meer THEN
				WHILE ((BITSET(Veld) * BuitenBord) = BITSET(0)) AND (Bord[Veld] = Leeg) DO
					AddZet(S, StukNr, Veld, Gewoon);
					Veld:=Veld + Richting[x];
				END;
			END;
			IF (BITSET(Veld) * BuitenBord) = BITSET(0) THEN
				IF Bord[Veld] = Leeg THEN
					AddZet(S, StukNr, Veld, Gewoon);
				ELSE
					IF StukTabel[Bord[Veld]].Kleur # S.AanZet THEN
						AddZet(S, StukNr, Veld, SlagZet);
					END;
				END;
			END;
		END;
	END;
END GenZperStuk;
(*$O=*) (* Overflow check *)
 */
GenZRec GenZPerStuk( BoStelling aBoStelling, int aStukNummer, int aKoningsVeld, int aStukVeld )
{
	GenZRec genZRec = new GenZRec();
	Stuk stuk = StukTabel[aStukNummer];
	for ( int x = 0; x < stuk.getAtlRicht(); x++ )
	{
		int Veld = aStukVeld + stuk.getRichting()[x];
		if ( stuk.isMeer() )
		{
			while ( veldToBitSetAndBuitenBord( Veld ).equals( Nul ) && Bord[Veld] == Leeg )
			{
//				void AddZet( Stelling aStelling, int aStukNr, int Naar, ZetSoort aZsoort, int aKVeld, int aSVeld, GenZRec GZ )
				AddZet( aBoStelling, aStukNummer, Veld, ZetSoort.Gewoon, aKoningsVeld, aStukVeld, genZRec );
				Veld += stuk.getRichting()[x];
			}
		}
		if ( veldToBitSetAndBuitenBord( Veld ).equals( Nul ) )
		{
			if ( Bord[Veld] == Leeg )
			{
				AddZet( aBoStelling, aStukNummer, Veld, ZetSoort.Gewoon, aKoningsVeld, aStukVeld, genZRec );
			}
			else
			{
				if ( StukTabel[Bord[Veld]].getKleur() != aBoStelling.getAanZet() )
				{
					AddZet( aBoStelling, aStukNummer, Veld, ZetSoort.SlagZet, aKoningsVeld, aStukVeld, genZRec );
				}
			}
		}
	}
	return genZRec;
}
/**
PROCEDURE GenZ(S: Dbs.Stelling): GenZrec;
VAR Sveld, Kveld, Veld: WerkVeld;
	GZ: GenZrec;
BEGIN
	GZ.Aantal:=0;
	GZ.Sptr  :=ADR(GenZtabel);
	ZetBordOp(S);
	(*-------- Koningszetten --------*)
	IF S.AanZet = Wit THEN
		Sveld:=S.WK;
		Kveld:=S.WK;
		GenZperStuk(1);
	ELSE
		Sveld:=S.ZK;
		Kveld:=S.ZK;
		GenZperStuk(2);
	END;
	(*--------- Stukzetten ----------*)
	IF (StukTabel[3].Kleur = S.AanZet) AND (S.s3 # Kveld) THEN
		Sveld:=S.s3;
		GenZperStuk(3);
	END;
	IF (StukTabel[4].Kleur = S.AanZet) AND (S.s4 # Kveld) THEN
		Sveld:=S.s4;
		GenZperStuk(4);
	END;
	ClrBord(S);
	RETURN(GZ);
END GenZ;
 */
/**
 * -------- Genereer zetten ----------		
 */
public GenZRec GenZ( BoStelling aStelling )
{
	GenZRec genZRec = new GenZRec();
	ZetBordOp( aStelling );
	int stukVeld;
	int koningsVeld;

	//-------- Koningszetten --------
	if ( aStelling.getAanZet() == Wit )
	{
		stukVeld = aStelling.getWk();
		koningsVeld = aStelling.getWk();
		GenZRec genZRecPerStuk = GenZPerStuk( aStelling, 1, koningsVeld, stukVeld );
		genZRec.addAll( genZRecPerStuk );
	}
	else
	{
		stukVeld = aStelling.getZk();
		koningsVeld = aStelling.getZk();
		GenZRec genZRecPerStuk = GenZPerStuk( aStelling, 2, koningsVeld, stukVeld );
		genZRec.addAll( genZRecPerStuk );
	}
	//--------- Stukzetten ----------
	if ( ( StukTabel[3].getKleur() == aStelling.getAanZet() ) && ( aStelling.getS3() != koningsVeld ) )
	{
		stukVeld = aStelling.getS3();
		koningsVeld = aStelling.getWk();
		GenZRec genZRecPerStuk = GenZPerStuk( aStelling, 3, koningsVeld, stukVeld );
		genZRec.addAll( genZRecPerStuk );
	}
	if ( ( StukTabel[4].getKleur() == aStelling.getAanZet() ) && ( aStelling.getS4() != koningsVeld ) )
	{
		stukVeld = aStelling.getS4();
		koningsVeld = aStelling.getZk();
		GenZRec genZRecPerStuk = GenZPerStuk( aStelling, 4, koningsVeld, stukVeld );
		genZRec.addAll( genZRecPerStuk );
	}
	ClrBord( aStelling );
	return genZRec;
}
/**
 * PROCEDURE GenZsort(S: Dbs.Stelling): GenZrec;
VAR GZ: GenZrec;
BEGIN
	GZ:=GenZ(S);
	QSort(CARDINAL(GZ.Aantal), KleinerDan, Swap);
	RETURN(GZ);
END GenZsort;
 */
/**
 * -------- Genereer zetten gesorteerd ----------		
 */
/* Dit is incorrect. Je moet sowieso onderscheid maken tussen wit en zwart aan zet.
 * - Bij Zwart aan zet is de volgorde Verloren, kleinste aantal zetten, Remise, Gewonnen met grootste aantal zetten
 * - Bij Wit @@NOG precies andersom???
 */
Comparator<BoStelling> stellingComparator = new Comparator<>()
{
	@Override
	public int compare( BoStelling L, BoStelling R )
	{
		// @@NOG Dit klopt nog niet erg, zie de tests
		int compare = L.getResultaat().compareTo( R.getResultaat() );
		if ( compare != 0 )
		{
			return compare;
		}
		if ( L.getResultaat() == ResultaatType.Gewonnen )
		{
			if ( L.getAantalZetten() > R.getAantalZetten() )
			{
				return 1;
			}
		}
		if ( L.getResultaat() == ResultaatType.Verloren )
		{
			if ( L.getAantalZetten() < R.getAantalZetten() )
			{
				return -1;
			}
		}
		return 0;
		
	}
};
public GenZRec GenZSort( BoStelling aStelling )
{
	GenZRec genZRec = GenZ( aStelling );
	genZRec.getSptr().sort( stellingComparator );
	return genZRec;
}
public boolean isPat()
{
	//@@NOG
	return false;
}
/**
PROCEDURE VeldToAscii(V: Dbs.Veld): AsciiVeld;
BEGIN
	RETURN(Notatie[V]);
END VeldToAscii;
 */
/**
 * ------- Veld naar ASCII ----------------------------------
 */
public String VeldToAscii( int aVeld )
{
	return Notatie[aVeld];
}
/**
PROCEDURE AsciiToVeld(A: AsciiVeld): Dbs.Veld;
BEGIN
	A[0]:=CAP(A[0]);
	RETURN(Dbs.Veld(ORD(A[0]) - ORD('A') + 16 * (ORD(A[1]) - ORD('1'))));
END AsciiToVeld;
 */
/**
 * ------- ASCII naar veld -----------------------------------
 */
public int AsciiToVeld( String aAsciiVeld )
{
	if ( aAsciiVeld.length() != 2 )
	{
		throw new RuntimeException( "AsciiVeld moet 2 lang zijn: " + aAsciiVeld );
	}
	String asciiVeldCap = StringUtils.capitalize( aAsciiVeld );
	return asciiVeldCap.charAt( 0 ) - 'A' + 16 * ( asciiVeldCap.charAt( 1 ) - '1' );
}
/**
PROCEDURE GetStukInfo(S: Dbs.Stelling; Nr: StukNummer): StukInfoRec;
VAR SI: StukInfoRec;
BEGIN
	SI.Veld   :=S.Velden[Nr];
	SI.X      :=1 + S.Velden[Nr] MOD 16;
	SI.Y      :=1 + S.Velden[Nr] DIV 16;
	SI.Kleur  :=StukTabel[Nr].Kleur;
	SI.StukAfk:=StukTabel[Nr].StukAfk;
	RETURN(SI);
END GetStukInfo;
 */
/**
 * ------- Lever info over stuk ------------------------------
 */
public StukInfo GetStukInfo( BoStelling aStelling, int aStukNummer )
{
	int veld = -1;
	switch ( aStukNummer )
	{
		case 1: veld = aStelling.getWk(); break;
		case 2: veld = aStelling.getZk(); break;
		case 3: veld = aStelling.getS3(); break;
		case 4: veld = aStelling.getS4(); break;
	}
	StukInfo stukInfo = StukInfo.builder()
		.stuk( StukTabel[aStukNummer] )
		.veld( veld )
		.x( 1 + veld % 16 )
		.y( 1 + veld / 16 )
		.build();
	return stukInfo;
}
}