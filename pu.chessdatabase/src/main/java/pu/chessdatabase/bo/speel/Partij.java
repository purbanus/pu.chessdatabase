package pu.chessdatabase.bo.speel;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.Einde.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Gen;
import pu.chessdatabase.bo.Stuk;
import pu.chessdatabase.bo.StukInfo;
import pu.chessdatabase.dbs.Dbs;
import pu.chessdatabase.dbs.Resultaat;

import lombok.Data;

/**
 * De belangrijkste struktuur is Plies. Een aantal voorbeelden:

Voorbeeld a):  Wit begint

1. Ke2-e3 Ta1-a8
2. Ke3-e4 Kf6-g6

Plies ziet er als volgt uit:

                 Stelling         ZetNr       Van/naar
Startstelling	Ke2Dh1Kf6Ta1 waz
Plies[0]		Ke3Dh1Kf6Ta1 zaz     1         e2 e3 
Plies[1]		Ke3Dh1Kf6Ta8 waz     1         a1 a8 
Plies[2]		Ke4Dh1Kf6Ta8 zaz     2         e3 e4 
Plies[3]		Ke3Dh1Kg6Ta8 waz     2         f6 g6 
  ... 

Voorbeeld b):  Zwart begint

1.   ...  Ta1-a8
2. Ke3-e4 Kf6-g6

Plies ziet er als volgt uit:

                 Stelling          ZetNr     Van/naar
Startstelling	Ke3Dh1Kf6Ta1 zaz               
Plies[0]		Ke3Dh1Kf6Ta8 waz     1        a1 a8 
Plies[1]		Ke4Dh1Kf6Ta8 zaz     2        e3 e4 
Plies[2]		Ke3Dh1Kg6Ta8 waz     2        f6 g6 
 ... 
Met andere woorden, in een Ply zit de zet die gedaan is, plus de stelling 
die daaruit resulteeert. Het zetnummer is gewoon het nummer dat afgedrukt moet worden.
 */


@Component
@Data
public class Partij
{
public static final String DEFAULT_USER_NAME = "purbanus";

private Dbs dbs;

@Autowired private Gen gen;
@Autowired private Config config;

private Plies plies;

/**
 * Spring roept de constructor aan voordat hij de @AutoWired velden initialiseert.
 * Gelukkig kun je een constructor maken met als parm het veld dat je wilt initialiseren.
 * Dat was in dit geval nodig omdat we dbs.Open() wilden aanroepen.
 */
public Partij( Dbs aDbs, Config aConfig )
{
	super();
	dbs = aDbs;
	dbs.open();
	config = aConfig;
	plies = new Plies( config.getConfig() );
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
	// Check het getal. Getallen in die ranges kunnen niet naar een hexgetal vertaald worden
	// Bijvoorbeeld 0x01 (10) wordt als hexString "a" en dat snapt Integer.parseInt niet.
	if ( 
		   ( aDecimaalGetal >= 0x08 && aDecimaalGetal <=0x0f )
		|| ( aDecimaalGetal >= 0x18 && aDecimaalGetal <=0x1f )
		|| ( aDecimaalGetal >= 0x28 && aDecimaalGetal <=0x2f )
		|| ( aDecimaalGetal >= 0x38 && aDecimaalGetal <=0x3f )
		|| ( aDecimaalGetal >= 0x48 && aDecimaalGetal <=0x4f )
		|| ( aDecimaalGetal >= 0x58 && aDecimaalGetal <=0x5f )
		|| ( aDecimaalGetal >= 0x68 && aDecimaalGetal <=0x6f )
		|| ( aDecimaalGetal >= 0x78 )
	)
	{
		throw new RuntimeException( "getal buiten range in veldToHexGetal(): " + aDecimaalGetal );
	}
	String hexString = Integer.toHexString( aDecimaalGetal );
	return Integer.parseInt( hexString );
}

/**
 * ------- Kontrole op legale stelling -----
 */
public boolean isLegaleStelling( BoStelling aBoStelling )
{
	BoStelling boStelling = getDbs().get( aBoStelling );
	return boStelling.getResultaat() != Resultaat.Illegaal;
}
/**
 * ------- Kijk of een stelling het einde van een partij is ------------
 */
public Einde getEinde( BoStelling aBoStelling )
{
	if ( aBoStelling.getResultaat() == Resultaat.Illegaal )
	{
		return Einde.Illegaal;
	}
	List<BoStelling> gegenereerdeZetten = getGen().genereerZetten( aBoStelling );
	if ( gegenereerdeZetten.size() > 0 )
	{
		return Nog_niet;
	}
	return aBoStelling.isSchaak() ? Mat : Pat;
}
/**
 * ------------ Een nieuwe partij beginnen ------------
 */
public BoStelling newGame( BoStelling aStartStelling )
{
	if ( ! isLegaleStelling( aStartStelling ) )
	{
		throw new RuntimeException( "Je kunt niet met een illegale stelling starten bij newGame()" );
	}
	BoStelling boStelling = getDbs().get( aStartStelling );
	boStelling.setSchaak( getGen().isSchaak( boStelling ) );
	setPlies( Plies.builder()
		.configString( getConfig().getConfig() )
		.startStelling( boStelling )
		.userName( DEFAULT_USER_NAME )
		.started( LocalDateTime.now().truncatedTo( ChronoUnit.SECONDS ) )
		.currentPlyNummer( -1 ) // Is met @Builder.Default al -1
		.begonnen( true )
		.plies( new ArrayList<>() )
		.build()
	);
	getPlies().clear();
	//getPlies().addPly( boStelling, isEindStelling( boStelling ) );
	return boStelling;
}
/**
 * ------- Is de partij begonnen -------------------
 */
public boolean isBegonnen()
{
	return getPlies().isBegonnen();
}
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
	if ( aBoStellingNaar.getS3() != aBoStellingNaar.getWk() && aBoStellingNaar.getS3() != aBoStellingNaar.getZk() && aBoStellingVan.getS3() != aBoStellingNaar.getS3() )
	{
		return new VanNaar( aBoStellingVan.getS3(), aBoStellingNaar.getS3() );
	}
	if ( aBoStellingNaar.getS4() != aBoStellingNaar.getWk() && aBoStellingNaar.getS4() != aBoStellingNaar.getZk() && aBoStellingVan.getS4() != aBoStellingNaar.getS4() )
	{
		return new VanNaar( aBoStellingVan.getS4(), aBoStellingNaar.getS4() );
	}
	if ( aBoStellingNaar.getS5() != aBoStellingNaar.getWk() && aBoStellingNaar.getS5() != aBoStellingNaar.getZk() && aBoStellingVan.getS5() != aBoStellingNaar.getS5() )
	{
		return new VanNaar( aBoStellingVan.getS5(), aBoStellingNaar.getS5() );
	}
	throw new RuntimeException( "De stellingen zijn gelijk in stellingToVanNaar()" );
}
/**
 * ----------- Stelling Bepalen uit Van/Naar -------------------
 */

BoStelling vanCurrentStandNaarToStelling( VanNaar aVanNaar )
{
	return vanNaarToStelling( getStand(), aVanNaar );
}
BoStelling vanNaarToStelling( BoStelling aBoStellingVan, VanNaar aVanNaar )
{
	List<BoStelling> gegenereerdeZetten = getGen().genereerZetten( aBoStellingVan );
	if ( gegenereerdeZetten.size() > 0 )
	{
		for ( BoStelling boStellingNaar : gegenereerdeZetten )
		{
			VanNaar vanNaar = stellingToVanNaar( aBoStellingVan, boStellingNaar );
			if ( vanNaar.equals( aVanNaar ) )
			{
				return boStellingNaar;
			}
		}
	}
	throw new RuntimeException( "Er kon geen stelling gevonden worden voor " + aVanNaar );
}
/**
 * -------- Kontrole op legale zet -----------------
 */
boolean isLegalMove( BoStelling aBoStelling, VanNaar aVanNaar )
{
	// Dit throws een RuntimeException als er geen stelling gevonden kon worden
	vanNaarToStelling( aBoStelling, aVanNaar );
	return true;
}
/**
 * ------------ TerugZetten ----------------------------
 */
public BoStelling zetNaarBegin()
{
	getPlies().setNaarBegin();
	return getStand();
}
public BoStelling zetTerug()
{
	getPlies().setTerug();
	return getStand();
}
/**
 * (*------------ VooruitZetten --------------------------*)
 */
public BoStelling zetVooruit()
{
	if ( isBegonnen() )
	{
		if ( getPlies().hasPlies() )
		{
			if ( ! getPlies().isAtLastPlyNummer() )
			{
				getPlies().setVooruit();
			}
			else
			{
				if ( getPlies().getCurrentEinde() == Nog_niet )
				{
					bedenk();
				}
			}
		}
		else
		{
			if ( getPlies().getCurrentEinde() == Nog_niet )
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
 * ------------ Bedenk zelf een zet -----------------------
 */
public BoStelling bedenk()
{
	if ( isBegonnen() && getPlies().getCurrentEinde() == Nog_niet )
	{
		BoStelling boStellingVan = getPlies().getStand();
		List<BoStelling> gegenereerdeZetten = getGen().genereerZettenGesorteerd( boStellingVan );
		if ( gegenereerdeZetten.size() > 0 )
		{
			return zetStelling( gegenereerdeZetten.get( 0 ) );
		}
	}
	return null;
}
/**
 * ------------ Voer een zet uit -----------------------
 */
void checkPartijVoorZet( BoStelling aBoStelling )
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "De partij is nog niet begonnen. Je kunt geen zet doen als de partij nog niet begonnen is." );
	}
	Einde einde = getPlies().getCurrentEinde();
	if ( einde != Nog_niet )
	{
		throw new RuntimeException( "De partij is geeindigd in " + einde + ". Je kunt geen zetten meer doen." );
	}
	if ( aBoStelling == null )
	{
		throw new RuntimeException( "Er kon geen stelling bepaald worden waarnaartoe de ze leidt" );
	}
}
/**
 * ------------ Voer een zet uit nav een stelling -----------------------
 */
public BoStelling zetStelling( BoStelling aBoStelling )
{
	VanNaar vanNaar = stellingToVanNaar( getPlies().getStand(), aBoStelling );
	return zet( vanNaar );
}
public BoStelling zet( String aVanNaar )
{
	return zet( new VanNaar( aVanNaar ) );
}
public BoStelling zet( VanNaar aVanNaar )
{
	BoStelling boStellingNaar = vanCurrentStandNaarToStelling( aVanNaar );
	checkPartijVoorZet( boStellingNaar );
	boStellingNaar = getDbs().get( boStellingNaar );
	boStellingNaar.setSchaak( getGen().isSchaak( boStellingNaar ) );
	
	if ( getPlies().hasPlies() )
	{
		int nextPlyNummer = plies.getCurrentPlyNummer() + 1;
		if ( getPlies().hasPly( nextPlyNummer ) )
		{
			Ply nextPly = getPlies().getPly( nextPlyNummer );
			if ( nextPly.getVanNaar().equals( aVanNaar ) )
			{
				getPlies().setVooruit();
				return boStellingNaar;
			}
		}
		getPlies().clearPliesFromNextPly();
	}
	getPlies().addPly( boStellingNaar, aVanNaar, getEinde( boStellingNaar ) );
	return boStellingNaar;
}
/**
 * Je zou natuurlijk bij het genereren een extra veld isSlagZet kunnen toevoegen,
 * dat je in addZet() vult. Maar dat is heel veel werk
 */
public boolean isSlagZet( BoStelling aBoStelling, int aNaar )
{
	// Als het 'naar' veld bezet is geldt het als een slagzet
	return aBoStelling.getWk() == aNaar 
		|| aBoStelling.getZk() == aNaar 
		|| aBoStelling.getS3() == aNaar 
		|| aBoStelling.getS4() == aNaar 
		|| aBoStelling.getS5() == aNaar;
}
/**
 * --------- Wat staat er op een veld -------------------
 */
String watStaatErOp( BoStelling aBoStelling, Integer aVeld )
{
	for ( Stuk stuk : getGen().getStukken().getStukken() )
	{
		StukInfo stukInfo = getGen().getStukInfo( aBoStelling, stuk );
		if ( stukInfo.getVeld() == aVeld )
		{
			return stukInfo.getAfko();
		}
	}
	return "?";
}
/**
 * ---------- Ply omzetten in string (Kd1-d2+) --------------
 */
String plyToString( Ply aPly )
{
	// Dit kan niet meer gebeuren
//	if ( aPly.getVanNaar() == null )
//	{
//		return "...";
//	}
	StringBuilder sb = new StringBuilder();
	sb.append( watStaatErOp( aPly.getPreviousStelling(), aPly.getVanNaar().getVan() ) );
	String van = veldToAlfa( aPly.getVanNaar().getVan() );
	sb.append( van ).append( isSlagZet( aPly.getPreviousStelling(), aPly.getVanNaar().getNaar() ) ? "x" : "-" );
	String naar = veldToAlfa( aPly.getVanNaar().getNaar() );
	sb.append( naar ).append( aPly.isSchaak() ? "+" : " " );
	sb.append( aPly.getEinde() == Mat ? "#" : "" );
	sb.append( aPly.getEinde() == Pat ? "=" : "" );
	return sb.toString();
}
/*
 * -------- Huidige ply omzetten in string ------------------------------
 */
String currentPlyToString()
{
	return plyToString( getPlies().getCurrentPly() );
}
/**
 * -------- Resultaat omzetten in string ------------------------------
 */
public ResultaatRecord getResultaatRecord()
{
	ResultaatRecord resultaatRec = new ResultaatRecord();
	resultaatRec.setMatIn( "" );
	BoStelling boStelling = getStand();
	Einde einde = getEinde( boStelling );
	if ( einde != Nog_niet )
	{
		resultaatRec.setResultaat( einde.toString() );
	}
	else
	{
		Resultaat resultaat = boStelling.getResultaat();
		if ( resultaat != Resultaat.Illegaal )
		{
			resultaatRec.setResultaat( resultaat.toString() );
		}
		if ( resultaat == Gewonnen || resultaat == Verloren )
		{
			resultaatRec.setMatIn( "Mat in " + ( boStelling.getAantalZetten() - 1 ) );
		}
	}
	return resultaatRec;
}
/**
 * -------- Zetnummer omzetten in string --------------------------------
 */
String zetNummerToString( int aZetNummer )
{
	return String.format("%1$3s", aZetNummer );
}

/**
 * -------- Hele zet omzetten naar string ( 55. Ke1-e2+  Ke7-d8+) -------
 */
ZetDocument createZetDocument( int aPlyNummer )
{
	Ply ply = getPlies().getPly( aPlyNummer );
	if ( ply.getBoStelling().getAanZet() == Wit )
	{
		throw new RuntimeException( "Je mag createZetDocument niet meer aanroepen met een ply waarin Wit aan zet is"+ "" );
	}
	String zwartZet;
	if ( getPlies().hasPly( aPlyNummer + 1 ) )
	{
		zwartZet = plyToString( getPlies().getPly( aPlyNummer + 1 ) );
	}
	else
	{
		zwartZet = "...";
	}
	return ZetDocument.builder()
		.zetNummer( ply.getZetNummer() )
		.witZet( plyToString( ply ) )
		.zwartZet( zwartZet )
		.currrentZet( aPlyNummer == getPlies().getCurrentPlyNummer() )
		.build();
}
public List<ZetDocument> createZetten()
{
	List<ZetDocument> zetten = new ArrayList<>();
	if ( ! getPlies().hasPlies() )
	{
		return zetten;
	}
	int startPlyNummer = 0;
	// Als de eerste zet zwart is maken we puntje puntje puntje plus de  ply hierna
	Ply firstPly = getPlies().getFirstPly();
	if ( firstPly.getBoStelling().getAanZet() == Wit )
	{
		zetten.add( ZetDocument.builder()
			.zetNummer( firstPly.getZetNummer() )
			.witZet( "..." )
			.zwartZet( plyToString( firstPly ) )
			.build()
		);
		startPlyNummer = 1;
	}
	for ( int x = startPlyNummer; x <= getPlies().getLastPlyNummer(); x += 2 )
	{
		zetten.add( createZetDocument( x ) );
	}
	return zetten;
}
/**
 * -------- Gegenereerde zet omzetten naar string ( 55. Ke1-e2+ (+100) -------
 */
GegenereerdeZetDocument getGegenereerdeZetDocument( Ply aPly, BoStelling aBoStellingNaar, int aZetNummer )
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
		.zetNummer( aZetNummer )
		.zet( plyToString( aPly ) )
		.resultaat( getGegenereerdeZetResultaat( aBoStellingNaar.getResultaat() ).toString() )
		.matInHoeveel( aBoStellingNaar.getResultaat() == Remise ? "..." : matInHoeveelString )
		.build();
}
Resultaat getGegenereerdeZetResultaat( Resultaat aResultaat )
{
	// We doen het hier precies andersom: GEWONNEN <-> VERLOREN, want dat is psychologisch beter.
	// Want stel dat wit gewonnen staat, dan zijn al die zetten VERLOREN, immers in al die zetten
	// is zwart aan zet. Wij willen dan GEWONNEN zien.
	if ( aResultaat == Gewonnen )
	{
		return Verloren;
	}
	if ( aResultaat == Verloren )
	{
		return Gewonnen;
	}
	return aResultaat;
}

/**
 * -------- Gegenereerde zetten omzetten naar strings ---------------------------------
 */
public List<GegenereerdeZetDocument> getGegenereerdeZetten()
{
	BoStelling boStellingVan = getStand();
	List<BoStelling> gegenereerdeZetten = getGen().genereerZettenGesorteerd( boStellingVan );
	List<GegenereerdeZetDocument> zetten = new ArrayList<>();
	int zetNummer = 1;
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		boStellingNaar.setSchaak( getGen().isSchaak( boStellingNaar ) );
		Ply ply = Ply.builder()
			// .id is voor JPA
			.plies( getPlies() )
			.einde( Nog_niet ) // @@NOG klopt dit??
			.plyNummer( getPlies().getCurrentPlyNummer() + 1 )
			.vanNaar( stellingToVanNaar( boStellingVan, boStellingNaar ) )
			.boStelling( boStellingNaar )
			.build();
		zetten.add( getGegenereerdeZetDocument( ply, boStellingNaar, zetNummer ) );
		zetNummer++;
	}
	return zetten;
}
/**
 * ----------- geef huidige stelling -------------------
 */
public BoStelling getStand()
{
	return getPlies().getStand();
}
public Ply getCurrentPly()
{
	if ( getPlies().hasPlies() )
	{
		return getPlies().getCurrentPly();
	}
	throw new RuntimeException( "Er wordt gevraagd om de current ply maar er zijn nog geen plies!" );
}
}

