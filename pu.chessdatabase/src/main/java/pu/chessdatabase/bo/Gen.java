package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.StukType.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;

@Component
public class Gen
{
public static final BitSet BUITENBORD = bitSetOfInt( 0x88 );
public static final BitSet NUL = bitSetOfInt( 0x00 );

public static final String [] NOTATIE = new String [] {
	"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "??", "??", "??", "??", "??", "??", "??", "??",
	"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "??", "??", "??", "??", "??", "??", "??", "??",
	"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "??", "??", "??", "??", "??", "??", "??", "??",
	"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "??", "??", "??", "??", "??", "??", "??", "??",
	"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "??", "??", "??", "??", "??", "??", "??", "??",
	"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "??", "??", "??", "??", "??", "??", "??", "??",
	"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "??", "??", "??", "??", "??", "??", "??", "??",
	"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
};
@Autowired private Config config;
@Autowired private Dbs dbs;

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
	veldSet.and( BUITENBORD );
	return veldSet;
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
public static String veldToAlfa( int aVeld )
{
	if ( aVeld < 0 || aVeld >= 120 )
	{
		throw new RuntimeException( "Veld moet tussen 0 en 119 liggen: " + aVeld );
	}
	String alfaVeld = NOTATIE[aVeld];
	if ( "??".equals( alfaVeld ) ) 
	{
		throw new RuntimeException( "Dit is geen geldig veld: " + aVeld + ". Het ligt buiten het bord." );
	}
	return NOTATIE[aVeld];
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
public static int alfaToVeld( String aAlfaVeld )
{
	if ( aAlfaVeld.length() != 2 )
	{
		throw new RuntimeException( "AlfaVeld moet 2 lang zijn: " + aAlfaVeld );
	}
	String capAlfaVeld = aAlfaVeld.toUpperCase();
	char capAlfaIndex0 = capAlfaVeld.charAt( 0 );
	if ( capAlfaIndex0 < 'A' || capAlfaIndex0 > 'H' )
	{
		throw new RuntimeException( "De kolom (de letter) moet tussen A en H liggen" );
	}
	char capAlfaIndex1 = capAlfaVeld.charAt( 1 );
	if ( capAlfaIndex1 < '1' || capAlfaIndex1 > '8' )
	{
		throw new RuntimeException( "De rij (het cijfer) moet tussen 1 en 8 liggen" );
	}
	return capAlfaVeld.charAt( 0 ) - 'A' + 16 * ( capAlfaVeld.charAt( 1 ) - '1' );
}

public Gen()
{
}
public Stukken getStukken()
{
	return config.getStukken();
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
public boolean isGeomIllegaal( BoStelling aBoStelling )
{
	if ( ( aBoStelling.getWk() == aBoStelling.getZk() ) || ( aBoStelling.getS3() == aBoStelling.getS4()     ) ) return true;
	if ( ( aBoStelling.getWk() == aBoStelling.getS3() ) && ( getStukken().getS3().getKleur() != WIT   ) ) return true;
	if ( ( aBoStelling.getWk() == aBoStelling.getS4() ) && ( getStukken().getS4().getKleur() != WIT   ) ) return true;
	if ( ( aBoStelling.getZk() == aBoStelling.getS3() ) && ( getStukken().getS3().getKleur() != ZWART ) ) return true;
	if ( ( aBoStelling.getZk() == aBoStelling.getS4() ) && ( getStukken().getS4().getKleur() != ZWART ) ) return true;
	if ( getStukken().getS3().getStukType() == LOPER && getStukken().getS4().getStukType() == LOPER
		&& aBoStelling.getVeldKleur( aBoStelling.getS3() ) == aBoStelling.getVeldKleur( aBoStelling.getS4() ) )
	{
		return true;
	}
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
public boolean isKKSchaak( BoStelling aBoStelling )
{
	for ( int richting : Richtingen.KRICHTING )
	{
		if ( aBoStelling.getZk() == aBoStelling.getWk() + richting )
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

boolean isSchaakDoorStuk( Stuk aStuk, int aKoningsVeld, int aStukVeld, Bord aBord )
{
	for ( int richting : aStuk.getRichtingen() )
	{
		int veld = aStukVeld + richting;
		if ( aStuk.isMeer() )
		{
			while ( veldToBitSetAndBuitenBord( veld ).equals( NUL ) && aBord.isVeldLeeg( veld ) )
			{
				veld += richting;
			}
		}
		if ( veld == aKoningsVeld )
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
 * Als dat allemaal waar is wordt isSchaakDoorStuk aangeroepen
*/
public boolean checkSchaakDoorStuk( BoStelling aStelling, Stuk aStuk, int aKoningsVeld, int aStukVeld, Bord aBord )
{
	if ( ( aStukVeld != aStelling.getWk() ) && ( aStukVeld != aStelling.getZk() ) && ( aStuk.getKleur() != aStelling.getAanZet() ) )
	{
		if ( isSchaakDoorStuk( aStuk, aKoningsVeld, aStukVeld, aBord ) )
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
 *  Dat hele schaakjes gedoe is volgens mij om illegale stellingen te ontdekken
 */
public boolean isSchaak( BoStelling aStelling )
{
	Bord bord = new Bord( aStelling );
	int KVeld = aStelling.getAanZet() == WIT ? aStelling.getWk() : aStelling.getZk();
	if ( checkSchaakDoorStuk( aStelling, getStukken().getS3(), KVeld, aStelling.getS3(), bord ) )
	{
		return true;
	}
	if ( checkSchaakDoorStuk( aStelling, getStukken().getS4(), KVeld, aStelling.getS4(), bord ) )
	{
		return true;
	}
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
// Geen gegenereerdeZetten als parm maar gewoon de nieuwe stelling retourneren
// ==> Nee, want dan moet je soms null retourmerem (Of Optional<BoStelling gebruiken, maar dat maakt het alleen maar erger)
void addZet( final BoStelling aBoStelling, Stuk aStuk, int aNaar, ZetSoort aZetsoort, int aKoningsVeld, int aStukVeld, List<BoStelling> aGegenereerdeZetten )
{
	BoStelling boStelling = aBoStelling.clone();
	if ( aZetsoort == ZetSoort.SLAGZET )
	{
		//---- Stop het geslagen stuk "onder" de koning ----
		if ( boStelling.getS3() == aNaar )
		{
			// @@HIGH Kun je hier niet boStelling.setS3( aKoningsVeld ) doen?
			Stuk geslagenStuk = getStukken().getS3();
			if ( geslagenStuk.getKoningsNummer() == 0 )
			{
				boStelling.setS3( boStelling.getWk() );
			}
			else
			{
				boStelling.setS3( boStelling.getZk() );
			}
		}
		else if ( boStelling.getS4() == aNaar )
		{
			// @@HIGH En hier mut.mut?
			Stuk geslagenStuk = getStukken().getS4();
			if ( geslagenStuk.getKoningsNummer() == 0 )
			{
				boStelling.setS4( boStelling.getWk() );
			}
			else
			{
				boStelling.setS4( boStelling.getZk() );
			}
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
	switch ( aStuk.getStukNummer() )
	{
		case 0: boStelling.setWk( aNaar ); break;
		case 1: boStelling.setZk( aNaar ); break;
		case 2: boStelling.setS3( aNaar ); break;
		case 3: boStelling.setS4( aNaar ); break;
	}
	boStelling.setAanZet( boStelling.getAanZet() == WIT ? ZWART : WIT );
	BoStelling gotBoStelling = dbs.get( boStelling );
	
	// Niet doen, dit heeft een verschrikkelijke invloed op de performance! van zo'n 70 sec naar 330 sec
	// boStelling.setSchaak( isSchaak( boStelling ) );
	if ( gotBoStelling.getResultaat() != ResultaatType.ILLEGAAL )
	{
		aGegenereerdeZetten.add( gotBoStelling );
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
List<BoStelling> genereerZettenPerStuk( BoStelling aBoStelling, Stuk aStuk, int aKoningsVeld, int aStukVeld, Bord aBord )
{
	List<BoStelling> gegenereerdeZetten = new ArrayList<>();
	for ( int richting : aStuk.getRichtingen() )
	{
		int veld = aStukVeld + richting;
		if ( aStuk.isMeer() )
		{
			while ( veldToBitSetAndBuitenBord( veld ).equals( NUL ) && aBord.isVeldLeeg( veld ) )
			{
//				void AddZet( Stelling aStelling, int aStukNr, int Naar, ZetSoort aZsoort, int aKVeld, int aSVeld, GenZRec GZ )
				addZet( aBoStelling, aStuk, veld, ZetSoort.GEWOON, aKoningsVeld, aStukVeld, gegenereerdeZetten );
				veld += richting;
			}
		}
		if ( veldToBitSetAndBuitenBord( veld ).equals( NUL ) )
		{
			if ( aBord.isVeldLeeg( veld ) )
			{
				addZet( aBoStelling, aStuk, veld, ZetSoort.GEWOON, aKoningsVeld, aStukVeld, gegenereerdeZetten );
			}
			else
			{
				if ( getStukken().getStukAtIndex( aBord.getVeld( veld ) ).getKleur() != aBoStelling.getAanZet() )
				{
					addZet( aBoStelling, aStuk, veld, ZetSoort.SLAGZET, aKoningsVeld, aStukVeld, gegenereerdeZetten );
				}
			}
		}
	}
	return gegenereerdeZetten;
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
public List<BoStelling> genereerZetten( BoStelling aStelling )
{
	List<BoStelling> gegenereerdeZetten = new ArrayList<>();
	Bord bord = new Bord( aStelling );
	int stukVeld;
	int koningsVeld;

	//-------- Koningszetten --------
	if ( aStelling.getAanZet() == WIT )
	{
		stukVeld = aStelling.getWk();
		koningsVeld = aStelling.getWk();
		gegenereerdeZetten.addAll( genereerZettenPerStuk( aStelling, getStukken().getWk(), koningsVeld, stukVeld, bord ) );
	}
	else
	{
		stukVeld = aStelling.getZk();
		koningsVeld = aStelling.getZk();
		gegenereerdeZetten.addAll( genereerZettenPerStuk( aStelling, getStukken().getZk(), koningsVeld, stukVeld, bord ) );
	}
	//--------- Stukzetten ----------
	if ( ( getStukken().getS3().getKleur() == aStelling.getAanZet() ) && ( aStelling.getS3() != koningsVeld ) )
	{
		stukVeld = aStelling.getS3();
		koningsVeld = getStukken().getS3().getKoningsNummer() == 0 ? aStelling.getWk() : aStelling.getZk();
		gegenereerdeZetten.addAll( genereerZettenPerStuk( aStelling, getStukken().getS3(), koningsVeld, stukVeld, bord ) );
	}
	if ( ( getStukken().getS4().getKleur() == aStelling.getAanZet() ) && ( aStelling.getS4() != koningsVeld ) )
	{
		stukVeld = aStelling.getS4();
		koningsVeld = getStukken().getS4().getKoningsNummer() == 0 ? aStelling.getWk() : aStelling.getZk();
		gegenereerdeZetten.addAll( genereerZettenPerStuk( aStelling, getStukken().getS4(), koningsVeld, stukVeld, bord ) );
	}
	return gegenereerdeZetten;
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
/* 
 * Je moet het zo bekijken: als hier zwart aan zet is, dan bekijk je de zetten vanuit het oogpunt van wit
 * - Bij Zwart aan zet is de volgorde Gewonnen, kleinste aantal zetten, Remise, Verloren met grootste aantal zetten
 * - Bij Wit   aan zet is de volgorde Verloren, kleinste aantal zetten, Remise, Gewonnen met grootste aantal zetten 
 * 
 * ResultaatType = ILLEGAAL( "Illegaal" ), GEWONNEN( "Gewonnen" ), REMISE( "Remise"), VERLOREN( "Verloren" );
 */
Comparator<BoStelling> stellingComparator = new Comparator<>()
{
	@Override
	public int compare( BoStelling L, BoStelling R )
	{
		int compare = L.getResultaat().compareTo( R.getResultaat() );
		if ( compare != 0 )
		{
			return L.getAanZet() == ZWART ? compare : -compare;
		}
		if ( L.getAantalZetten() == R.getAantalZetten() )
		{
			return 0;
		}
		if ( L.getResultaat() == ResultaatType.GEWONNEN )
		{
			if ( L.getAantalZetten() > R.getAantalZetten() )
			{
				return L.getAanZet() == ZWART ? 1 : -1;
			}
			else
			{
				return L.getAanZet() == ZWART ? -1 : 1;
			}
		}
		if ( L.getResultaat() == ResultaatType.VERLOREN )
		{
			if ( L.getAantalZetten() > R.getAantalZetten() )
			{
				return L.getAanZet() == ZWART ? -1 : 1;
			}
			else
			{
				return L.getAanZet() == ZWART ? 1 : -1;
			}
		}
		return 0;
		
	}
};
public List<BoStelling> genereerZettenGesorteerd( BoStelling aStelling )
{
	List<BoStelling> gegenereerdeZetten = genereerZetten( aStelling );
	gegenereerdeZetten.sort( stellingComparator );
	// @@LOW Met zwart aan zet geeft hij de juiste volgorde, met wit precies de omgekeerde
	// Dus omdat ik geen zin heb om dat sorteren opnieuw te doen, doen we hier een reverse()
	if ( aStelling.getAanZet() == WIT )
	{
		gegenereerdeZetten = gegenereerdeZetten.reversed();
	}
	return gegenereerdeZetten;
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
public StukInfo getStukInfo( BoStelling aStelling, Stuk aStuk )
{
	int veld = -1;
	switch ( aStuk.getStukNummer() )
	{
		case 0: veld = aStelling.getWk(); break;
		case 1: veld = aStelling.getZk(); break;
		case 2: veld = aStelling.getS3(); break;
		case 3: veld = aStelling.getS4(); break;
	}
	StukInfo stukInfo = StukInfo.builder()
		.stuk( aStuk )
		.veld( veld )
		.x( 1 + veld % 16 )
		.y( 1 + veld / 16 )
		.build();
	return stukInfo;
}

}