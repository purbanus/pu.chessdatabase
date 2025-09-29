package pu.chessdatabase.bo.speel;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.EindeType.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Gen;
import pu.chessdatabase.bo.GegenereerdeZetten;
import pu.chessdatabase.bo.StukInfo;
import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;

/**================================================================================
Het partijrecord.
	Desc       - Beschrijving (voor de toekomst).
	CurPly     - Huidige ply, ply die op het bord staat.
	             Is 0 als de partij nog niet begonnen is of als er teruggezet wordt
	             tot de beginstand.
	LastPly    - Laatst bekende ply in de partij. Is groter dan curply als er
	             vooruitgezet of teruggezet is.
	             Is 0 als de partij nog niet begonnen is. Kan daarna nooit meer 0 worden.
	IsBegonnen - Er is een beginstelling ingevoerd (BOOLEAN)

	
De belangrijkste struktuur is Plies. Een aantal voorbeelden:

Voorbeeld a):  Wit begint

1. Ke2-e3 Ta1-a8
2. Ke3-e4 Kf6-g6

Plies ziet er als volgt uit:

                 Stelling       ZetNr       Van/naar
Plies[0]     Ke2Dh1Kf6Ta1 waz     1          e2 e3
Plies[1]     Ke3Dh1Kf6Ta1 zaz     1          a1 a8
Plies[2]     Ke3Dh1Kf6Ta8 waz     2          e3 e4
Plies[3]     Ke4Dh1Kf6Ta8 zaz     2          f6 g6
Plies[4]     Ke3Dh1Kg6Ta8 waz     3           ...

Voorbeeld b):  Zwart begint

1.   ...  Ta1-a8
2. Ke3-e4 Kf6-g6

Het plyarray ziet er als volgt uit:

                 Stelling       ZetNr       Van/naar
Plies[0]     Ke3Dh1Kf6Ta1 zaz     1          a1 a8
Plies[1]     Ke3Dh1Kf6Ta8 waz     2          e3 e4
Plies[2]     Ke4Dh1Kf6Ta8 zaz     2          f6 g6
Plies[3]     Ke3Dh1Kg6Ta8 waz     3           ...

Met andere woorden, in een plyrecord zit de stelling waaruit zetten
gegenereerd worden, plus de zet die uiteindelijk gedaan is. Het zetnummer
is gewoon het nummer dat afgedrukt moet worden.

===================================================================================
*/

@Service
public class Partij
{
public static final int MAX_HELE_ZET_NUMMER = 130;
public static final int MAX_PLY_NUMMER = 255;
public static final DecimalFormat THREE_DIGIT_FORMATTER = new DecimalFormat( "##0" );

private Dbs dbs;
@Autowired private Gen gen;

PlyRecord[] plies = new PlyRecord[256];
PartijEntry curPartij;
List<String> partijZetString = new ArrayList<>();
//private String plyString;
//private String zetNummerString;
//private String heleZetString;
//private String resulaatString;
/**
 * BEGIN
	Dbs.Open();
	InzPartij();
END Partij
 */
/**
 * Spring roept de constructor aan voordat hij de @AutoWired velden initialiseert.
 * Gelukkig kun je een construcor maken met als parm het veld dat je wilt initialiseren.
 * Day was in dit geval nodig omdat we dbs.Open() wilden aanroepen.
 */
public Partij( Dbs aDbs )
{
	super();
	dbs = aDbs;
	dbs.open();
	inzPartij();
}

/**
 * =====================================================================================
		Deel 1: Newgame
=====================================================================================
*)

PROCEDURE InzPartij();
VAR x: PlyNummer;
BEGIN
	CurPartij.Desc:='';
	CurPartij.CurPly  :=0;
	CurPartij.LastPly :=0;
	CurPartij.IsBegonnen:=FALSE;
	FOR x:=MIN(PlyNummer) TO MAX(PlyNummer) DO
		WITH Plies[x] DO
			Einde   :=NogNiet;
			ZetNr   :=MAX(HeleZetNummer);
			VN.Van  :=0FH;
			VN.Naar :=0FH;
			Schaak  :=FALSE;
		END;
	END;
END InzPartij;
 */
/**
 * (*------------ Initialiseren partij -----------------*)
 */
void inzPartij()
{
	curPartij = new PartijEntry();
	for ( int x = 0; x <= MAX_PLY_NUMMER; x++ )
	{
		plies[x] = PlyRecord.NULL_PLY_RECORD;
	}
}
/**
PROCEDURE IsLegaleStelling(S: Dbs.Stelling): BOOLEAN;
BEGIN
	Dbs.Get(S);
	IF S.Resultaat = Dbs.Illegaal THEN
		RETURN(FALSE);
	ELSE
		RETURN(TRUE);
	END;
END IsLegaleStelling;
 */
/**
 * ------- Kontrole op legale stelling -----
 */
public boolean isLegaleStelling( BoStelling aBoStelling )
{
	return dbs.get( aBoStelling ).getResultaat() != ResultaatType.ILLEGAAL;
}
/**
PROCEDURE IsEindStelling(S: Dbs.Stelling): EindeType;
VAR GZ: Gen.GenZrec;
BEGIN
	GZ:=Gen.GenZ(S);
	IF GZ.Aantal > 0 THEN
		RETURN(NogNiet);
	ELSIF Gen.IsSchaak(S) THEN
		RETURN(Mat);
	ELSE
		RETURN(Pat);
	END;
END IsEindStelling;
 */
/**
 * ------- Kijk of een stelling het einde van een partij is ------------
 */
public EindeType isEindStelling( BoStelling aBoStelling )
{
	if ( dbs.get( aBoStelling ).getResultaat() == ResultaatType.ILLEGAAL )
	{
		return EindeType.ILLEGAAL;
	}
	GegenereerdeZetten gegenereerdeZetten = gen.genereerZetten( aBoStelling );
	if ( gegenereerdeZetten.getAantal() > 0 )
	{
		return NOG_NIET;
	}
	return gen.isSchaak( aBoStelling ) ? MAT : PAT;
}
/**
PROCEDURE NewGame(StartS: Dbs.Stelling);
BEGIN
	IF IsLegaleStelling(StartS) THEN
		InzPartij();
		Dbs.Get(StartS);
		StartS.Schaak:=Gen.IsSchaak(StartS);
		Plies[0].S:=StartS;
		Plies[0].Einde:=IsEindStelling(StartS);
		Plies[0].ZetNr:=1;
		CurPartij.IsBegonnen:=TRUE;
	END;
END NewGame;
 */
/**
 * ------------ Een nieuwe partij beginnen ------------
 */
public void newGame( BoStelling aStartStelling )
{
	if ( isLegaleStelling( aStartStelling ) )
	{
		inzPartij();
		BoStelling boStelling = dbs.get( aStartStelling );
		boStelling.setSchaak( gen.isSchaak( boStelling ) );
		plies[0] = PlyRecord.builder()
			.boStelling( boStelling )
			.einde( isEindStelling( boStelling ) )
			.zetNr( 1 )
			.vanNaar( VanNaar.ILLEGAL_VAN_NAAR )
			.build();
		curPartij.setBegonnen( true );
	}
}
/**
 * =====================================================================================
		Deel 2: Info over de huidige stelling
=====================================================================================

PROCEDURE IsBegonnen(): BOOLEAN;
BEGIN
	RETURN(CurPartij.IsBegonnen);
END IsBegonnen;
 */
/**
 * ------- Is de partij begonnen -------------------
 */
public boolean isBegonnen()
{
	return curPartij.isBegonnen();
}
/**
PROCEDURE IsEindePartij(): EindeType;
BEGIN
	RETURN(Plies[CurPartij.CurPly].Einde);
END IsEindePartij;
 */
/**
 * ------- Kontrole op het einde van de partij -----
 */
public EindeType isEindePartij()
{
	return plies[curPartij.getCurPly()].getEinde();
}
/**
 * =====================================================================================
		Deel 3: Hulproutines om te zetten
=====================================================================================
*)

PROCEDURE StellingToVanNaar(Svan, Snaar: Dbs.Stelling): VanNaarType;
VAR VN: VanNaarType;
BEGIN
	IF Svan.WK # Snaar.WK THEN
		VN.Van :=Svan.WK;
		VN.Naar:=Snaar.WK;
	ELSIF Svan.ZK # Snaar.ZK THEN
		VN.Van :=Svan.ZK;
		VN.Naar:=Snaar.ZK;
	ELSIF Svan.s3 # Snaar.s3 THEN
		VN.Van :=Svan.s3;
		VN.Naar:=Snaar.s3;
	ELSE
		VN.Van :=Svan.s4;
		VN.Naar:=Snaar.s4;
	END;
	RETURN(VN);
END StellingToVanNaar;
 */
/**
 * ------------- Van/Naar bepalen uit twee stellingen -------
 */
VanNaar stellingToVanNaar( BoStelling aBoStellingVan, BoStelling aBoStellingNaar )
{
	if ( aBoStellingVan.getWk() != aBoStellingNaar.getWk() )
	{
		return new VanNaar( aBoStellingVan.getWk(), aBoStellingNaar.getWk() );
	}
	if ( aBoStellingVan.getZk() != aBoStellingNaar.getZk() )
	{
		return new VanNaar( aBoStellingVan.getZk(), aBoStellingNaar.getZk() );
	}
	if ( aBoStellingNaar.getS3() != aBoStellingNaar.getWk() && aBoStellingVan.getS3() != aBoStellingNaar.getS3() )
	{
		return new VanNaar( aBoStellingVan.getS3(), aBoStellingNaar.getS3() );
	}
	if ( aBoStellingNaar.getS4() != aBoStellingNaar.getZk() && aBoStellingVan.getS4() != aBoStellingNaar.getS4())
	{
		return new VanNaar( aBoStellingVan.getS4(), aBoStellingNaar.getS4() );
	}
	throw new RuntimeException( "De stellingen zijn gelijk in stellingToVanNaar()" );
}
/**
PROCEDURE VanNaarToStelling(VN: VanNaarType; VAR Snaar: Dbs.Stelling): BOOLEAN;
VAR Svan : Dbs.Stelling;
	GZ   : Gen.GenZrec;
	NewVN: VanNaarType;
	Sptr : Gen.StellingPtr;
	x    : Gen.AantalGzetten;
BEGIN
	Svan:=Plies[CurPartij.CurPly].S;
	GZ:=Gen.GenZ(Svan);
	IF GZ.Aantal > 0 THEN
		Sptr:=GZ.Sptr;
		FOR x:=1 TO GZ.Aantal DO
			NewVN:=StellingToVanNaar(Svan, Sptr^);
			IF NewVN = VN THEN
				Snaar:=Sptr^;
				RETURN(TRUE);
			END;
			IncAddr(Sptr, SIZE(Dbs.Stelling));
		END;
	END;
	RETURN(FALSE);
END VanNaarToStelling;
 */
/**
 * ----------- Stelling Bepalen uit Van/Naar -------------------
 */
BoStelling vanNaarToStelling( VanNaar aVanNaar )
{
	BoStelling boStellingVan = plies[curPartij.getCurPly()].getBoStelling();
	GegenereerdeZetten gegenereerdeZetten = gen.genereerZetten( boStellingVan );
	if ( gegenereerdeZetten.getAantal() > 0 )
	{
		for ( BoStelling boStelling : gegenereerdeZetten.getStellingen() )
		{
			VanNaar vanNaar = stellingToVanNaar( boStellingVan, boStelling );
			if ( vanNaar.equals( aVanNaar ) )
			{
				return boStelling;
			}
		}
	}
	return null;
}
/**
PROCEDURE IsLegaal(VN: VanNaarType): BOOLEAN;
VAR S: Dbs.Stelling;
BEGIN
	RETURN(VanNaarToStelling(VN, S));
END IsLegaal;
 */
/**
 * -------- Kontrole op legale zet -----------------
 */
boolean isLegaal( VanNaar aVanNaar )
{
	return vanNaarToStelling( aVanNaar ) != null;
}
/**
 * =====================================================================================
		Deel 4: Zetten
=====================================================================================

PROCEDURE ZetVooruit(): BOOLEAN;
BEGIN
	IF IsBegonnen() AND (CurPartij.CurPly < CurPartij.LastPly) THEN
		INC(CurPartij.CurPly);
		RETURN(TRUE);
	END;
	RETURN(FALSE);
END ZetVooruit;
 */
/**
 * (*------------ VooruitZetten --------------------------*)
 */
public boolean zetVooruit()
{
	if ( isBegonnen() && curPartij.getCurPly() < curPartij.getLastPly() )
	{
		curPartij.setCurPly( curPartij.getCurPly() + 1 );
		return true;
	}
	return false;
}
/**
PROCEDURE ZetTerug(): BOOLEAN;
BEGIN
	IF IsBegonnen() AND (CurPartij.CurPly > 0) THEN
		DEC(CurPartij.CurPly);
		RETURN(TRUE);
	END;
	RETURN(FALSE);
END ZetTerug;
 */
/**
 * ------------ TerugZetten ----------------------------
 */
public boolean zetTerug()
{
	if ( isBegonnen() && curPartij.getCurPly() > 0 )
	{
		curPartij.setCurPly( curPartij.getCurPly() - 1 );
		return true;
	}
	return false;
}
/**
PROCEDURE Zet(VNzet: VanNaarType): BOOLEAN;
VAR NaarS   : Dbs.Stelling;
	x       : PlyNummer;
BEGIN
	IF IsBegonnen() AND (IsEindePartij() = NogNiet) AND VanNaarToStelling(VNzet, NaarS) THEN
		WITH CurPartij DO
			IF CurPly < MAX(PlyNummer) THEN

				(* Voor oorspronkelijke (werkende) versie zie diskette 311090 *)

				NaarS.Schaak:=Gen.IsSchaak(NaarS);
				IF Plies[CurPly].VN # VNzet THEN
					FOR x:=CurPly+1 TO LastPly DO
						Plies[x]:=NullPly;
					END;
					LastPly:=CurPly + 1;
				END;
				WITH Plies[CurPly] DO
					VN    :=VNzet;
					Schaak:=NaarS.Schaak;
				END;
				INC(CurPly);
				IF (CurPly > LastPly) THEN
					LastPly:=CurPly;
				END;
				WITH Plies[CurPly] DO
					S    :=NaarS;
					Einde:=IsEindStelling(NaarS);
					ZetNr:=Plies[CurPly-1].ZetNr;
					IF S.AanZet = Wit THEN
						INC(ZetNr);
					END;
				END;
			END;
		END;
		RETURN(TRUE);
	END;
	RETURN(FALSE);
END Zet;
Ziehier de versie die ze noemden
PROCEDURE Zet(VN: VanNaarType): BOOLEAN;
VAR NaarS   : Dbs.Stelling;
	NewZetNr: HeleZetNummer;
	x       : PlyNummer;
BEGIN
	IF IsBegonnen() AND (IsEindePartij() = NogNiet) AND VanNaarToStelling(VN, NaarS) THEN
		WITH CurPartij DO
			IF CurPly < MAX(PlyNummer) THEN
				Plies[CurPly].VN:=VN;
				NaarS.Schaak:=Gen.IsSchaak(NaarS);
				Plies[CurPly].Schaak:=NaarS.Schaak;
				NewZetNr:=Plies[CurPly].ZetNr;
				IF NaarS.AanZet = Wit THEN
					INC(NewZetNr);
				END;
				INC(CurPly);                  (*Curply-1?? *)
				IF (CurPly > LastPly) OR (Plies[CurPly].VN # VN) THEN
					FOR x:=CurPly TO LastPly DO
						Plies[x]:=NullPly;
					END;
					LastPly:=CurPly;
				END;
				WITH Plies[CurPly] DO
					S    :=NaarS;
					Einde:=IsEindStelling(NaarS);
					ZetNr:=NewZetNr;
				END;
			END;
		END;
		RETURN(TRUE);
	END;
	RETURN(FALSE);
END Zet;
 */
/**
 * ------------ Voer een zet uit -----------------------
 */
void clearPliesVoorZet()
{
	for ( int x = curPartij.getCurPly(); x < curPartij.getLastPly(); x++ )
	{
		plies[x] = PlyRecord.NULL_PLY_RECORD;
	}
}
public boolean zet( VanNaar aVanNaar )
{
	BoStelling boStellingNaar = vanNaarToStelling( aVanNaar );
	if ( ! isBegonnen() || isEindePartij() != NOG_NIET || boStellingNaar == null )
	{
		return false;
	}
	if ( curPartij.getCurPly() >= MAX_PLY_NUMMER )
	{
		return false;
	}
	boStellingNaar.setSchaak( gen.isSchaak( boStellingNaar ) );
	PlyRecord curPlyRecord = plies[curPartij.getCurPly()];
	if ( ! curPlyRecord.getVanNaar().equals( aVanNaar ) )
	{
		clearPliesVoorZet();
		curPartij.setLastPly( curPartij.getCurPly() + 1 );
	}
	curPlyRecord.setVanNaar( aVanNaar );
	curPartij.setCurPly( curPartij.getCurPly() + 1 );
	if ( curPartij.getCurPly() > curPartij.getLastPly() )
	{
		curPartij.setLastPly( curPartij.getCurPly() );
	}
	plies[curPartij.getCurPly()] = PlyRecord.builder()
		.boStelling( boStellingNaar )
		.einde( isEindStelling( boStellingNaar ) )
		.zetNr( plies[curPartij.getCurPly() - 1].getZetNr() )
		// @@NOG Moeten deze niet??
		//.Schaak( boStellingNaar.isSchaak() )
		//.vanNaar( aVanNaar )
		.build();
	if ( boStellingNaar.getAanZet() == WIT )
	{
		PlyRecord newPlyRecord = plies[curPartij.getCurPly()];
		newPlyRecord.setZetNr( newPlyRecord.getZetNr() + 1 );
	}
	return true;
}

/**
PROCEDURE ZetStelling(S: Dbs.Stelling): BOOLEAN;
VAR VN: VanNaarType;
BEGIN
	VN:=StellingToVanNaar(Plies[CurPartij.CurPly].S, S);
	RETURN(Zet(VN));
END ZetStelling;
 */
/**
 * ------------ Voer een zet uit nav een stelling -----------------------
 */
public boolean zetStelling( BoStelling aBoStelling )
{
	VanNaar vanNaar = stellingToVanNaar( plies[curPartij.getCurPly()].getBoStelling(), aBoStelling );
	return zet( vanNaar );
}
/**
PROCEDURE Bedenk(): BOOLEAN;
VAR VanS: Dbs.Stelling;
	GZ  : Gen.GenZrec;
BEGIN
	IF IsBegonnen() AND (IsEindePartij() = NogNiet) THEN
		WITH CurPartij DO
			IF CurPly < MAX(PlyNummer) THEN
				VanS:=Plies[CurPartij.CurPly].S;
				GZ:=Gen.GenZsort(VanS);
				IF GZ.Aantal > 0 THEN
					RETURN(ZetStelling(GZ.Sptr^));
				END;
			END;
		END;
	END;
	RETURN(FALSE);
END Bedenk;
 */
/**
 * ------------ Bedenk zelf een zet -----------------------
 */
public boolean bedenk()
{
	if ( isBegonnen() && isEindePartij() == NOG_NIET )
	{
		if ( curPartij.getCurPly() < MAX_PLY_NUMMER )
		{
			BoStelling boStellingVan = plies[curPartij.getCurPly()].getBoStelling();
			GegenereerdeZetten gegenereerdeZetten = gen.genereerZettenGesorteerd( boStellingVan );
			if ( gegenereerdeZetten.getAantal() > 0 )
			{
				return zetStelling( gegenereerdeZetten.getStellingen().get( 0 ) );
			}
		}
	}
	return false;
}
/** =====================================================================================
		Deel 5: Rapportage
=====================================================================================

PROCEDURE WatStaatErOp(S: Dbs.Stelling; V: Dbs.Veld): CHAR;
VAR x: StukNummer;
	SI: StukInfoRec;
BEGIN
	FOR x:=MIN(StukNummer) TO MAX(StukNummer) DO
		SI:=Gen.GetStukInfo(S, x);
		IF SI.Veld = V THEN
			RETURN(SI.StukAfk);
		END;
	END;
	RETURN('?');
END WatStaatErOp;
 */
/**
 * --------- Wat staat er op een veld -------------------
 */
String watStaatErOp( BoStelling aBoStelling, int aVeld )
{
	for ( int x = gen.MIN_STUKNUMMER; x <= gen.MAX_STUKNUMMER; x++ )
	{
		StukInfo stukInfo = gen.getStukInfo( aBoStelling, x );
		if ( stukInfo.getVeld() == aVeld )
		{
			return stukInfo.getAfko();
		}
	}
	return "?";
}
/**
PROCEDURE PlyToStr(P: PlyRec): PlyStr;
VAR Ascii: Gen.AsciiVeld;
	Pstr : PlyStr;
BEGIN
	WITH P DO
		Pstr[0]:=WatStaatErOp(S, VN.Van);
		Ascii  :=Gen.VeldToAscii(VN.Van);
		Pstr[1]:=Ascii[0];
		Pstr[2]:=Ascii[1];
		Pstr[3]:='-';
		Ascii  :=Gen.VeldToAscii(VN.Naar);
		Pstr[4]:=Ascii[0];
		Pstr[5]:=Ascii[1];
		IF Schaak THEN
			Pstr[6]:='+';
		ELSE
			Pstr[6]:=' ';
		END;
		Pstr[7]:=00C;
	END;
	RETURN(Pstr);
END PlyToStr;
 */
/**
 * ---------- Ply omzetten in string (Kd1-d2+) --------------
 */
String plyToString( PlyRecord aPlyRecord )
{
	StringBuilder sb = new StringBuilder();
	sb.append( watStaatErOp( aPlyRecord.getBoStelling(), aPlyRecord.getVanNaar().getVan() ) );
	String van = gen.veldToAscii( aPlyRecord.getVanNaar().getVan() );
	sb.append( van ).append( "-" );
	String naar = gen.veldToAscii( aPlyRecord.getVanNaar().getNaar() );
	sb.append( naar ).append( aPlyRecord.isSchaak() ? "+" : " " );
	return sb.toString();
}
/**
PROCEDURE CurPlyToStr(): PlyStr;
BEGIN
	IF CurPartij.CurPly = 0 THEN
		RETURN('');
	ELSE
		RETURN(PlyToStr(Plies[CurPartij.CurPly-1]));
	END;
END CurPlyToStr;
 */
/*
 * -------- Huidige ply omzetten in string ------------------------------
 */
String curPlyToString()
{
	if ( curPartij.getCurPly() == 0 )
	{
		return "";
	}
	return plyToString( plies[curPartij.getCurPly() - 1] );
}
/**
PROCEDURE ResToStr(): ResultaatRec;
VAR RR: ResultaatRec;
	AantalStr: ResultaatStr;
	Dummy: BOOLEAN;
BEGIN
	RR.Res2:='';
	WITH Plies[CurPartij.CurPly] DO
		CASE Einde OF
		|	Mat: RR.Res1:='Mat';
		|	Pat: RR.Res1:='Pat';
		ELSE
			CASE S.Resultaat OF
			|	Dbs.Remise  : RR.Res1:='Remise';
			|	Dbs.Gewonnen: RR.Res1:='Gewonnen';
			|	Dbs.Verloren: RR.Res1:='Verloren';
			END;
			IF S.Resultaat # Dbs.Remise THEN
				Str.CardToStr(LONGCARD(S.Aantal-1), AantalStr, 10, Dummy);
				Str.Concat(RR.Res2, 'Mat in ', AantalStr);
			END;
		END;
	END;
	RETURN(RR);
END ResToStr;
 */
/**
 * -------- Resultaat omzetten in string ------------------------------
 */
ResultaatRecord resultaatToString() // @@NOG getResultaatRec?
{
	ResultaatRecord resultaatRec = new ResultaatRecord();
	resultaatRec.setRes2( "" );
	PlyRecord plyRecord = plies[curPartij.getCurPly()];
	if ( plyRecord.getEinde() != NOG_NIET )
	{
		resultaatRec.setRes1( plyRecord.getEinde().toString() );
	}
	else
	{
		ResultaatType resultaat = plyRecord.getBoStelling().getResultaat();
		if ( resultaat != ResultaatType.ILLEGAAL )
		{
			resultaatRec.setRes1( resultaat.toString() );
		}
		if ( resultaat == GEWONNEN || resultaat == VERLOREN )
		{
			resultaatRec.setRes2( "Mat in " + plyRecord.getBoStelling().getAantalZetten() );
		}
	}
	return resultaatRec;
}
/**
PROCEDURE ZetNrToStr(ZetNr: HeleZetNummer): ZetNrStr;
VAR S, Nr, Voor: ZetNrStr;
	Dummy: BOOLEAN;
BEGIN
	Str.CardToStr(LONGCARD(ZetNr), Nr, 10, Dummy);
	IF ZetNr < 10 THEN
		Voor:='  ';
	ELSIF ZetNr < 100 THEN
		Voor:=' ';
	ELSE
		Voor:='';
	END;
	Str.Concat(S, Voor, Nr);
	RETURN(S);
END ZetNrToStr;
 */
/**
 * -------- Zetnummer omzetten in string --------------------------------
 */
String zetNummerToString( int aZetNummer )
{
	return THREE_DIGIT_FORMATTER.format( aZetNummer );
}
/**
PROCEDURE CurZetNrToStr(): ZetNrStr;
BEGIN
	RETURN(ZetNrToStr(Plies[CurPartij.CurPly].ZetNr));
END CurZetNrToStr;
 */
/**
 * ------- Huidige zetnummer omzetten in string -----------------------
 */
public String currentZetNummerToString()
{
	return zetNummerToString( plies[curPartij.getCurPly()].getZetNr() );
}
/**
PROCEDURE HeleZetToStr(Ply: PlyNummer): HeleZetStr;
VAR Ply1Str, Ply2Str: PlyStr;
	H: HeleZetStr;
BEGIN
	IF Ply >= CurPartij.LastPly THEN
		Win.ErrMsg('Fout in HeleZetToStr: Plynummer > laatste zet');
		RETURN('');
	END;

	(*--- Wit *)
	IF Plies[Ply].S.AanZet # Wit THEN
		Win.ErrMsg('Fout in HeleZetToStr: Eerste zet is niet wit');
		RETURN('');
	END;
	Str.Concat(H, ZetNrToStr(Plies[Ply].ZetNr), '. ');
	Ply1Str:=PlyToStr(Plies[Ply]);

	(*--- Zwart *)
	INC(Ply);
	IF Ply >= CurPartij.LastPly THEN
		Ply2Str:='';
	ELSE
		IF Plies[Ply].S.AanZet # Zwart THEN
			Win.ErrMsg('Fout in HeleZetToStr: Tweede zet is niet zwart');
			RETURN('');
		END;
		Ply2Str:=PlyToStr(Plies[Ply]);
	END;

	(*--- Aan elkaar plakken *)
	Str.Concat(H, H, Ply1Str);
	Str.Concat(H, H, ' ');
	Str.Concat(H, H, Ply2Str);
	RETURN(H);
END HeleZetToStr;
 */
/**
 * -------- Hele zet omzetten naar string ( 55. Ke1-e2+  Ke7-d8+) -------
 */
String heleZetToString( int aPlyNummer )
{
	StringBuilder sb = new StringBuilder();
	if ( aPlyNummer >= curPartij.getLastPly() )
	{
		throw new RuntimeException( "Fout in HeleZetToStr: Plynummer > laatste zet" );
	}
	// Wit
	if ( plies[aPlyNummer].getBoStelling().getAanZet() != WIT )
	{
		throw new RuntimeException( "Fout in HeleZetToStr: Eerste zet is niet wit" );
	}
	sb.append( plyToString( plies[aPlyNummer] ) );
	
	// Zwart
	aPlyNummer++;
	if ( aPlyNummer < curPartij.getLastPly() )
	{
		if ( plies[aPlyNummer].getBoStelling().getAanZet() != ZWART )
		{
			throw new RuntimeException( "Fout in HeleZetToStr: Tweede zet is niet zwart" );
		}
		sb.append( " " ).append( plyToString( plies[aPlyNummer] ) );
	}
	return sb.toString();
}
/**
PROCEDURE PartijToStr(): PartijReport;
VAR PS         : HeleZetStr;
	PR         : PartijReport;
	ZetNr      : HeleZetNummer;
	StartPly, x: PlyNummer;
BEGIN
	ZetNr:=1; (*@@@ eigenlijk 0, maar dan zou je HeleZetNummer [0..130] moeten definieren *)
	PR.PartijZetten:=ADR(PSZ);
	PR.ErZijnZetten:=FALSE;
	IF IsBegonnen() AND (CurPartij.LastPly > 0) THEN
		PR.ErZijnZetten:=TRUE;

		(* Bijzonder geval als eerste zet zwart is *)
		IF Plies[0].S.AanZet = Zwart THEN
			PS:='  1.   ...   ';
			Str.Concat(PS, PS, PlyToStr(Plies[0]));
			PSZ[ZetNr]:=PS;
			StartPly:=1;
			INC(ZetNr);
		ELSE
			StartPly:=0;
		END;
		FOR x:=StartPly TO CurPartij.LastPly-1 BY 2 DO
			PSZ[ZetNr]:=HeleZetToStr(x);
			INC(ZetNr);
		END;

		(* Aan het einde kreet geven *)
		CASE Plies[CurPartij.LastPly].Einde OF
		|	Mat: PSZ[ZetNr]:='     Mat.';
		|	Pat: PSZ[ZetNr]:='     Pat.';
		ELSE
			IF ZetNr > 1 THEN
				DEC(ZetNr);
			END;
		END;
	
		(* Bereken begin van vooruitzetten *)
		PR.Vooruit.ErIsVooruit:=FALSE;
		IF CurPartij.CurPly > 0 THEN
			PR.Vooruit.ErIsVooruit:=TRUE;
			CASE Plies[0].S.AanZet OF
			|	Wit  : PR.Vooruit.Start := (CurPartij.CurPly-1) DIV 2 + 1;
			|	Zwart: PR.Vooruit.Start := (CurPartij.CurPly  ) DIV 2 + 1;
			END;
			PR.Vooruit.Halverwege:= Plies[CurPartij.CurPly].S.AanZet = Wit;
		END;
	END;
	PR.AantalZetten:=ZetNr;
	RETURN(PR);
END PartijToStr;
 */
/**
 * -------- Partij omzetten naar strings ---------------------------------
 */
public PartijReport partijToString()
{
	int zetNummer = 0; // Was 1 maar daar wil ik van af
	PartijReport partijReport = new PartijReport();
	partijReport.setPartijZetten( partijZetString );
	partijReport.setErZijnZetten( false );
	if ( isBegonnen() && curPartij.getLastPly() > 0 )
	{
		partijReport.setErZijnZetten( true );
		int startPly;
		
		// Bijzonder geval als eerste zet zwart is *)
		if ( plies[0].getBoStelling().getAanZet() == ZWART )
		{
			String heleZetString = "  1.   ...   ";
			heleZetString += plyToString( plies[0] );
			partijZetString.add( heleZetString );
			startPly = 1;
			zetNummer++;
		}
		else
		{
			startPly = 0;
		}
		for ( int x = startPly; x < curPartij.getLastPly(); x += 2 )
		{
			partijZetString.add( heleZetToString( x ) );
			zetNummer++;
		}
		// Aan het einde kreet geven
		switch ( plies[curPartij.getLastPly()].getEinde() )
		{
			case MAT: partijZetString.add( "     Mat." ); break;
			case PAT: partijZetString.add( "     Pat." ); break;
			//$CASES-OMITTED$
			default:
			{
				if ( zetNummer > 1 )
				{
					zetNummer--;
				}
				break;
			}
		}
		partijReport.getVooruit().setErIsVooruit( false );
		if ( curPartij.getCurPly() > 0 )
		{
			partijReport.getVooruit().setErIsVooruit( true );
			if ( plies[0].getBoStelling().getAanZet() == WIT )
			{
				partijReport.getVooruit().setStart( ( curPartij.getCurPly() - 1 ) / 2 + 1 );
			}
			else
			{
				partijReport.getVooruit().setStart( ( curPartij.getCurPly()     ) / 2 + 1 );
			}
			partijReport.getVooruit().setHalverwege( plies[curPartij.getCurPly()].getBoStelling().getAanZet() == WIT );
		}
	}
	partijReport.setAantalZetten( zetNummer );
	return partijReport;
}
/**
PROCEDURE GenZetToStr(ZetNr: HeleZetNummer; VanS, NaarS: Dbs.Stelling): HeleZetStr;
VAR P: PlyRec;
	H: HeleZetStr;
	ResStr: ARRAY[0..3] OF CHAR;
	AantalStr: ARRAY[0..2] OF CHAR;
	Dummy: BOOLEAN;
BEGIN
	P.ZetNr :=ZetNr;
	P.S     :=VanS;
	P.Einde :=NogNiet;
	P.VN    :=StellingToVanNaar(VanS, NaarS);
	P.Schaak:=Gen.IsSchaak(NaarS);
	IF NaarS.Resultaat = Dbs.Remise THEN
		ResStr:=' =';
	ELSE
		IF NaarS.Resultaat = Dbs.Gewonnen THEN
			ResStr:='-';
		ELSE
			ResStr:='+';
		END;
		Str.CardToStr(LONGCARD(NaarS.Aantal-1), AantalStr, 10, Dummy);
		Str.Concat(ResStr, ResStr, AantalStr);
	END;
	Str.Concat(H, ZetNrToStr(P.ZetNr), '. ');
	Str.Concat(H, H, PlyToStr(P));
	Str.Concat(H, H, '  ');
	Str.Concat(H, H, ResStr);
	RETURN(H);
END GenZetToStr;
 */
/**
 * -------- Gegenereerde zet omzetten naar string ( 55. Ke1-e2+ (+100) -------
 */
String genZetToString( int aZetNummer, BoStelling aBoStellingVan, BoStelling aBoStellingNaar )
{
	PlyRecord plyRecord = PlyRecord.builder()
		.zetNr( aZetNummer )
		.boStelling( aBoStellingVan )
		.einde( NOG_NIET ) // @@NOG klopt dit??
		.vanNaar( stellingToVanNaar( aBoStellingVan, aBoStellingNaar ) )
		.build();
	String resString;
	if ( aBoStellingNaar.getResultaat() == REMISE )
	{
		resString = " =";
	}
	else
	{
		resString = aBoStellingNaar.getResultaat() == GEWONNEN ? "-" : "+";
		resString += aBoStellingNaar.getAantalZetten() - 1;
	}
	StringBuilder sb = new StringBuilder();
	sb.append( zetNummerToString( plyRecord.getZetNr() ) ).append( ". " );
	sb.append( plyToString( plyRecord ) ).append(  "  " ).append( resString );
	return sb.toString();
}

/**
PROCEDURE GenToStr(Max: HeleZetNummer): GenReport;
VAR GR         : GenReport;
	ZetNr      : HeleZetNummer;
	GZ         : Gen.GenZrec;
	VanS       : Dbs.Stelling;
	GenZptr    : Gen.StellingPtr;
BEGIN
	VanS:=Plies[CurPartij.CurPly].S;
	GZ:=Gen.GenZsort(VanS);
    IF GZ.Aantal = 0 THEN
		ZetNr:=1;
    	PSZ[ZetNr]:='    (Geen zetten)';
    ELSE
    	GenZptr:=GZ.Sptr;
    	IF GZ.Aantal > Max THEN
    		GZ.Aantal:=Max;
    	END;
		FOR ZetNr:=1 TO GZ.Aantal DO
			PSZ[ZetNr]:=GenZetToStr(ZetNr, VanS, GenZptr^);
			IncAddr(GenZptr, SIZE(Dbs.Stelling));
		END;
		ZetNr:=GZ.Aantal;
	END;
	GR.AantalZetten:=ZetNr;
	GR.GenZetten   :=ADR(PSZ);
	RETURN(GR);
END GenToStr;
 */
/**
 * -------- Gegenereerde zetten omzetten naar strings ---------------------------------
 */
public GegenereerdeZettenReport gegenereerdeZettenToString( int aMax )
{
	BoStelling boStellingVan = plies[curPartij.getCurPly()].getBoStelling();
	GegenereerdeZetten gegenereerdeZetten = gen.genereerZettenGesorteerd( boStellingVan );
	int zetNummer = 0;
	if ( gegenereerdeZetten.getAantal() == 0 )
	{
		zetNummer = 1; // @@NOG Moet dat??
		partijZetString.add( "    (Geen zetten)" );
	}
	else
	{
		int gegenereerdAantal = gegenereerdeZetten.getAantal();
		if ( gegenereerdAantal > aMax )
		{
			gegenereerdAantal = aMax;
		}
		for ( BoStelling boStelling : gegenereerdeZetten.getStellingen() )
		{
			partijZetString.add( genZetToString( zetNummer, boStellingVan, boStelling ) );
		}
		zetNummer = gegenereerdAantal;
	}
	GegenereerdeZettenReport genReport = GegenereerdeZettenReport.builder()
		.aantalZetten( zetNummer )
		.gegenereerdeZetten( partijZetString )
		.build();
	return genReport;
}
/**
PROCEDURE GetStand(): Dbs.Stelling; (*@@@@@@@ of meer informatie? *)
BEGIN
	RETURN(Plies[CurPartij.CurPly].S);
END GetStand;
 */
/**
 * ----------- geef huidige stelling -------------------
 */
public BoStelling getStand()
{
	return plies[curPartij.getCurPly()].getBoStelling();
}
/**
PROCEDURE GetStukInfo(S: Dbs.Stelling; StukNr: StukNummer): StukInfoRec;
BEGIN
	RETURN(Gen.GetStukInfo(S, StukNr));
END GetStukInfo;
 */
/**
 * -------- Geef info over stuk ------------------------
 */
public StukInfo getStukInfo( BoStelling aBoStelling, int aStukNummer )
{
	return gen.getStukInfo( aBoStelling, aStukNummer );
}









}

