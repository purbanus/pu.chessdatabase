package pu.chessdatabase.bo.speel;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.EindeType.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Gen;
import pu.chessdatabase.bo.Stuk;
import pu.chessdatabase.bo.StukInfo;
import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;

import lombok.Data;

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
Plies[3]     Ke4Dh1Kg6Ta8 waz     3           ...

Met andere woorden, in een Ply zit de stelling waaruit zetten
gegenereerd worden, plus de zet die uiteindelijk gedaan is. Het zetnummer
is gewoon het nummer dat afgedrukt moet worden.

===================================================================================
*/

@Component
@Data
public class Partij
{
public static final int MAX_HELE_ZET_NUMMER = 130;
public static final int MAX_PLY_NUMMER = 255;

private Dbs dbs;
@Autowired private Gen gen;

Plies plies = new Plies();

/**
 * BEGIN
	Dbs.Open();
	InzPartij();
END Partij
 */
/**
 * Spring roept de constructor aan voordat hij de @AutoWired velden initialiseert.
 * Gelukkig kun je een constructor maken met als parm het veld dat je wilt initialiseren.
 * Dat was in dit geval nodig omdat we dbs.Open() wilden aanroepen.
 */
public Partij( Dbs aDbs )
{
	super();
	dbs = aDbs;
	dbs.open();
}
/**
 * ------- Veld naar ASCII ----------------------------------
 */
public static String veldToAlfa( int aVeld )
{
	return Gen.veldToAlfa( aVeld );
}
/**
 * ------- ASCII naar veld -----------------------------------
 */
public static int alfaToVeld( String aAsciiVeld )
{
	return Gen.alfaToVeld( aAsciiVeld );
}
/**
 * ------- Hex integer naar veld -----------------------------------
 * In de websfeer is het handig om de stelling velden steeds als hex integer te representeren
 */
public static int hexGetalToVeld( int aHexGetal )
{
	return Integer.parseInt( String.valueOf( aHexGetal ), 16 );
}
public static int veldToHexGetal( int aDecimaalGetal )
{
	String hexString = Integer.toHexString( aDecimaalGetal );
	return Integer.parseInt( hexString );
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
	BoStelling boStelling = dbs.get( aBoStelling );
	return boStelling.getResultaat() != ResultaatType.ILLEGAAL;
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
	BoStelling boStelling = dbs.get( aBoStelling );
	boStelling.setSchaak( gen.isSchaak( boStelling ) );
	if ( boStelling.getResultaat() == ResultaatType.ILLEGAAL )
	{
		return EindeType.ILLEGAAL;
	}
	List<BoStelling> gegenereerdeZetten = gen.genereerZetten( aBoStelling );
	if ( gegenereerdeZetten.size() > 0 )
	{
		return NOG_NIET;
	}
	// return gen.isSchaak( aBoStelling ) ? MAT : PAT;
	return boStelling.isSchaak() ? MAT : PAT;
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
public BoStelling newGame( BoStelling aStartStelling )
{
	if ( ! isLegaleStelling( aStartStelling ) )
	{
		throw new RuntimeException( "Je kunt niet met een illegale stelling starten bij newGame()" );
	}
	setPlies( new Plies() );
	BoStelling boStelling = dbs.get( aStartStelling );
	boStelling.setSchaak( gen.isSchaak( boStelling ) );
	getPlies().clear();
	getPlies().addPly( Ply.builder()
		.boStelling( boStelling )
		.einde( isEindStelling( boStelling ) )
		.zetNummer( 1 )
		.vanNaar( null )
		.build()
		);
	return boStelling;
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
	return getPlies().isBegonnen();
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

BoStelling vanCurrentPlyNaarToStelling( VanNaar aVanNaar )
{
	return vanNaarToStelling( getPlies().getCurrentPly(), aVanNaar );
}
BoStelling vanNaarToStelling( Ply aPly, VanNaar aVanNaar )
{
	BoStelling boStellingVan = aPly.getBoStelling();
	List<BoStelling> gegenereerdeZetten = gen.genereerZetten( boStellingVan );
	if ( gegenereerdeZetten.size() > 0 )
	{
		for ( BoStelling boStellingNaar : gegenereerdeZetten )
		{
			VanNaar vanNaar = stellingToVanNaar( boStellingVan, boStellingNaar );
			if ( vanNaar.equals( aVanNaar ) )
			{
				return boStellingNaar;
			}
		}
	}
	throw new RuntimeException( "Er kon geen stelling gevonden worden voor van=" + Integer.toHexString( aVanNaar.getVan() ) + " naar=" + Integer.toHexString( aVanNaar.getNaar() ) );
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
boolean isLegalMove( VanNaar aVanNaar )
{
	// Dit throws een RuntimeException als er geen stelling gevoinden kon worden
	vanCurrentPlyNaarToStelling( aVanNaar );
	return true;
}
/**
 * =====================================================================================
		Deel 4: Zetten
=====================================================================================

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
public BoStelling zetNaarBegin()
{
	getPlies().setToBegin();
	return getStand();
}
public BoStelling zetTerug()
{
	getPlies().setTerug();
	return getStand();
}
/**
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
public BoStelling zetVooruit()
{
	if ( isBegonnen() )
	{
		if ( ! getPlies().isAtLastPlyNumber() )
		{
			getPlies().setVooruit();
		}
		else
		{
			if ( getPlies().getCurrentEinde() == NOG_NIET )
			{
				bedenk();
			}
		}
	}
	return getStand();
}
public BoStelling zetNaarEinde()
{
	getPlies().setNaarEinde();
	return getStand();
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
public BoStelling bedenk()
{
	if ( isBegonnen() && getPlies().getCurrentEinde() == NOG_NIET )
	{
		BoStelling boStellingVan = getPlies().getCurrentPly().getBoStelling();
		List<BoStelling> gegenereerdeZetten = gen.genereerZettenGesorteerd( boStellingVan );
		if ( gegenereerdeZetten.size() > 0 )
		{
			return zetStelling( gegenereerdeZetten.get( 0 ) );
		}
	}
	return null;
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
public BoStelling zet( String aVanNaar )
{
	return zet( new VanNaar( aVanNaar ) );
}
void checkPartijVoorZet( BoStelling boStellingNaar )
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "De partij is nog niet begonnen. Je kunt geen zet doen als de partij nog niet begonnen is." );
	}
	EindeType einde = getPlies().getCurrentEinde();
	if ( einde != NOG_NIET )
	{
		throw new RuntimeException( "De partij is geeindigd in " + einde.getNormaleSpelling() + ". Je kunt geen zetten meer doen." );
	}
	if ( boStellingNaar == null )
	{
		throw new RuntimeException( "Er kon geen stelling bepaald worden waarnaartoe de ze leidt" );
	}
}

public BoStelling zet( VanNaar aVanNaar )
{
	BoStelling boStellingNaar = vanCurrentPlyNaarToStelling( aVanNaar );
	checkPartijVoorZet( boStellingNaar );

	boStellingNaar.setSchaak( gen.isSchaak( boStellingNaar ) );
	Ply currentPly = getPlies().getCurrentPly();
	if ( ! aVanNaar.equals( currentPly.getVanNaar() ) )
	{
		getPlies().clearPliesFromNextPly();
	}
	currentPly.setVanNaar( aVanNaar );
	currentPly.setSchaak( boStellingNaar.isSchaak() );
	getPlies().addPly( boStellingNaar, isEindStelling( boStellingNaar ) );
	return boStellingNaar;
}
/**
 * Je zou natuurlijk bij het genereren een extra veld isSlagZet kunnen toevoegen,
 * dat je in addZet() vult. Maar dat is heel veel werk
 */
public boolean isSlagZet( BoStelling aBoStelling, VanNaar aVanNaar )
{
	// Als het 'naar' veld bezet is geldt het als een slagzet
	int naar = aVanNaar.getNaar();
	return aBoStelling.getWk() == naar || aBoStelling.getZk() == naar || aBoStelling.getS3() == naar || aBoStelling.getS4() == naar;
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
public BoStelling zetStelling( BoStelling aBoStelling )
{
	VanNaar vanNaar = stellingToVanNaar( getPlies().getCurrentPly().getBoStelling(), aBoStelling );
	return zet( vanNaar );
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
	for ( Stuk stuk : gen.getStukken().getStukken() )
	{
		StukInfo stukInfo = gen.getStukInfo( aBoStelling, stuk );
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
String plyToString( Ply aPly )
{
	StringBuilder sb = new StringBuilder();
	if ( aPly.getVanNaar() == null )
	{
		return "...";
	}
	sb.append( watStaatErOp( aPly.getBoStelling(), aPly.getVanNaar().getVan() ) );
	String van = veldToAlfa( aPly.getVanNaar().getVan() );
	sb.append( van ).append( isSlagZet( aPly.getBoStelling(), aPly.getVanNaar() ) ? "x" : "-" );
	String naar = veldToAlfa( aPly.getVanNaar().getNaar() );
	sb.append( naar ).append( aPly.isSchaak() ? "+" : " " );
	sb.append( aPly.getEinde() == MAT ? "#" : "" );
	sb.append( aPly.getEinde() == PAT ? "=" : "" );
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
String currentPlyToString()
{
	// Het gaat hier voornamelijk om de VanNaar, en die zit in de VORIGE ply
	Ply previousPly = getPlies().getPreviousPly();
	if ( previousPly == null )
	{
		return "";
	}
	return plyToString( previousPly );
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
		END;/7
		
	END;
	RETURN(RR);
END ResToStr;
 */
/**
 * -------- Resultaat omzetten in string ------------------------------
 */
public ResultaatRecord getResultaatRecord()
{
	ResultaatRecord resultaatRec = new ResultaatRecord();
	resultaatRec.setRes2( "" );
	Ply Ply = getPlies().getCurrentPly();
	if ( Ply.getEinde() != NOG_NIET )
	{
		resultaatRec.setRes1( Ply.getEinde().getNormaleSpelling() );
	}
	else
	{
		ResultaatType resultaat = Ply.getBoStelling().getResultaat();
		if ( resultaat != ResultaatType.ILLEGAAL )
		{
			resultaatRec.setRes1( resultaat.getNormaleSpelling() );
		}
		if ( resultaat == GEWONNEN || resultaat == VERLOREN )
		{
			resultaatRec.setRes2( "Mat in " + ( Ply.getBoStelling().getAantalZetten() - 1 ) );
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
	return String.format("%1$3s", aZetNummer );
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
	return zetNummerToString( getPlies().getCurrentPly().getZetNummer() );
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
public PartijReport getPartijReport()
{
	PartijReport partijReport = new PartijReport();
	partijReport.setErZijnZetten( false );
	if ( isBegonnen() )
	{
		partijReport.setErZijnZetten( true );
		partijReport.setZetten( createZetten() );
		partijReport.setVooruit( createVooruit() );
	}
	return partijReport;
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
ZetDocument createZetDocument( int aPlyNummer )
{
	// Als de eerste zet zwart is maken we puntje puntje puntje plus de  ply hierna
	Ply ply = getPlies().getPly( aPlyNummer );
	if ( ply.getBoStelling().getAanZet() == ZWART )
	{
		return ZetDocument.builder()
			.zetNummer( ply.getZetNummer() )
			.witZet( "..." )
			.zwartZet( plyToString( ply ) )
			.build();
	}
	String zwartZet = "...";
	if ( getPlies().hasPly( aPlyNummer + 1 ) )
	{
		zwartZet = plyToString( getPlies().getPly( aPlyNummer + 1 ) );
	}
	return ZetDocument.builder()
		.zetNummer( ply.getZetNummer() )
		.witZet( plyToString( ply ) )
		.zwartZet( zwartZet )
		.build();
}
List<ZetDocument> createZetten()
{
	List<ZetDocument> zetten = new ArrayList<>();
	int startPly = 0;
	// Als de eerste zet zwart is maken we puntje puntje puntje plus de  ply hierna
	Ply firstPly = getPlies().getFirstPly();
	if ( firstPly.getBoStelling().getAanZet() == ZWART )
	{
		zetten.add( ZetDocument.builder()
			.zetNummer( firstPly.getZetNummer() )
			.witZet( "..." )
			.zwartZet( plyToString( firstPly ) )
			.build()
		);
		startPly = 1;
	}
	for ( int x = startPly; x < getPlies().getLastPlyNumber(); x += 2 )
	{
		zetten.add( createZetDocument( x ) );
	}
	return zetten;
}
/**
		PR.Vooruit.ErIsVooruit:=FALSE;
		IF CurPartij.CurPly > 0 THEN
			PR.Vooruit.ErIsVooruit:=TRUE;
			CASE Plies[0].S.AanZet OF
			|	Wit  : PR.Vooruit.Start := (CurPartij.CurPly-1) DIV 2 + 1;
			|	Zwart: PR.Vooruit.Start := (CurPartij.CurPly  ) DIV 2 + 1;
			END;
			PR.Vooruit.Halverwege:= Plies[CurPartij.CurPly].S.AanZet = Wit;
		END;
 */
/**
 *   Bereken begin van vooruitzetten 
 */
VooruitRecord createVooruit()
{
	VooruitRecord vooruitRecord = VooruitRecord.getDefaultVooruitRecord();
	if ( getPlies().getCurrentPlyNumber() >= 0 )
	{
		vooruitRecord.setErIsVooruit( true );
		int currentPlyNumber = getPlies().getCurrentPlyNumber();
		if ( getPlies().getFirstPly().getBoStelling().getAanZet() == WIT )
		{
			vooruitRecord.setStart( ( currentPlyNumber - 1 ) / 2 + 1 );
		}
		else
		{
			vooruitRecord.setStart( ( currentPlyNumber     ) / 2 + 1 );
		}
		/* Zou je dit niet precies anderom moeten doen, dus == ZWART?
		 * De test is nu of de laatste stelling Wit aan zet heeft, maar wit heeft nog niet gezet!
		 */
		vooruitRecord.setHalverwege( getPlies().getCurrentPly().getBoStelling().getAanZet() == ZWART );
	}
	return vooruitRecord;
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
GegenereerdeZetDocument getGegenereerdeZetDocument( Ply aPly, BoStelling aBoStellingNaar )
{
	int matInHoeveel = aBoStellingNaar.getAantalZetten() - 1;
	String matInHoeveelString;
	if ( matInHoeveel == 0 )
	{
		matInHoeveelString = "Mat";
	}
	else
	{
		matInHoeveelString = "Mat in " + matInHoeveel;
	}
	return GegenereerdeZetDocument.builder()
		.zetNummer( aPly.getZetNummer() + 1 )
		.zet( plyToString( aPly ) )
		.resultaat( getGegenereerdeZetResultaat( aBoStellingNaar.getResultaat() ).getNormaleSpelling() )
		.matInHoeveel( aBoStellingNaar.getResultaat() == REMISE ? "..." : matInHoeveelString )
		.build();
}
ResultaatType getGegenereerdeZetResultaat( ResultaatType aResultaat )
{
	// We doen het hier precies andersom: GEWONNEN <-> VERLOREN, want dat is psychologisch beter.
	// Want stel dat wit gewonnen staat, dan zijn al die zetten VERLOREN, immers in al die zetten
	// is zwart aan zet. Wij willen dan GEWONNEN zien.
	if ( aResultaat == GEWONNEN )
	{
		return VERLOREN;
	}
	if ( aResultaat == VERLOREN )
	{
		return GEWONNEN;
	}
	return aResultaat;
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
public List<GegenereerdeZetDocument> getGegenereerdeZetten()
{
	BoStelling boStellingVan = getPlies().getCurrentPly().getBoStelling();
	List<BoStelling> gegenereerdeZetten = gen.genereerZettenGesorteerd( boStellingVan );
	List<GegenereerdeZetDocument> zetten = new ArrayList<>();
	int zetNummer = 0;
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		Ply ply = Ply.builder()
			.zetNummer( zetNummer )
			.boStelling( boStellingVan )
			.einde( NOG_NIET ) // @@NOG klopt dit??
			.vanNaar( stellingToVanNaar( boStellingVan, boStellingNaar ) )
			.schaak( gen.isSchaak( boStellingNaar ) )
			.build();
		zetten.add( getGegenereerdeZetDocument( ply, boStellingNaar ) );
		zetNummer++;
	}
	return zetten;
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
	return getPlies().getCurrentPly().getBoStelling();
}







}

