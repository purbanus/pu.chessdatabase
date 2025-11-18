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
public Einde isEindStelling( BoStelling aBoStelling )
{
	BoStelling boStelling = getDbs().get( aBoStelling );
	boStelling.setSchaak( getGen().isSchaak( boStelling ) );
	if ( boStelling.getResultaat() == Resultaat.Illegaal )
	{
		return Einde.Illegaal;
	}
	List<BoStelling> gegenereerdeZetten = getGen().genereerZetten( aBoStelling );
	if ( gegenereerdeZetten.size() > 0 )
	{
		return Nog_niet;
	}
	return boStelling.isSchaak() ? Mat : Pat;
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
	setPlies( Plies.builder()
		.configString( getConfig().getConfig() )
		.userName( DEFAULT_USER_NAME )
		.started( LocalDateTime.now().truncatedTo( ChronoUnit.SECONDS ) )
		.currentPlyNumber( -1 ) // Is met @Builder.Default al -1
		.begonnen( false ) // Is met @Builder.Default al false
		.plies( new ArrayList<>() )
		.build()
	);
	BoStelling boStelling = getDbs().get( aStartStelling );
	boStelling.setSchaak( getGen().isSchaak( boStelling ) );
	getPlies().clear();
	getPlies().addPly( boStelling, isEindStelling( boStelling ) );
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

BoStelling vanCurrentPlyNaarToStelling( VanNaar aVanNaar )
{
	return vanNaarToStelling( getPlies().getCurrentPly().getBoStelling(), aVanNaar );
}
BoStelling vanNaarToStelling( Ply aPly, VanNaar aVanNaar )
{
	return vanNaarToStelling( aPly.getBoStelling(), aVanNaar );
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
	throw new RuntimeException( "Er kon geen stelling gevonden worden voor van=" + Integer.toHexString( aVanNaar.getVan() ) + " naar=" + Integer.toHexString( aVanNaar.getNaar() ) );
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
	getPlies().setToBegin();
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
		if ( ! getPlies().isAtLastPlyNumber() )
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
		BoStelling boStellingVan = getPlies().getCurrentPly().getBoStelling();
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
void checkPartijVoorZet( BoStelling boStellingNaar )
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
	if ( boStellingNaar == null )
	{
		throw new RuntimeException( "Er kon geen stelling bepaald worden waarnaartoe de ze leidt" );
	}
}

public BoStelling zet( String aVanNaar )
{
	return zet( new VanNaar( aVanNaar ) );
}
/**
 * ------------ Voer een zet uit nav een stelling -----------------------
 */
public BoStelling zetStelling( BoStelling aBoStelling )
{
	VanNaar vanNaar = stellingToVanNaar( getPlies().getCurrentPly().getBoStelling(), aBoStelling );
	return zet( vanNaar );
}
public BoStelling zet( VanNaar aVanNaar )
{
	BoStelling boStellingNaar = vanCurrentPlyNaarToStelling( aVanNaar );
	checkPartijVoorZet( boStellingNaar );

	boStellingNaar.setSchaak( getGen().isSchaak( boStellingNaar ) );
	Ply currentPly = getPlies().getCurrentPly();
	if ( aVanNaar.equals( currentPly.getVanNaar() ) )
	{
		getPlies().setVooruit();
	}
	else
	{
		getPlies().clearPliesFromNextPly();
		currentPly.setVanNaar( aVanNaar );
		currentPly.setSchaak( boStellingNaar.isSchaak() );
		getPlies().addPly( boStellingNaar, isEindStelling( boStellingNaar ) );
	}
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
	return aBoStelling.getWk() == naar || aBoStelling.getZk() == naar || aBoStelling.getS3() == naar || aBoStelling.getS4() == naar || aBoStelling.getS5() == naar;
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
	sb.append( aPly.getEinde() == Mat ? "#" : "" );
	sb.append( aPly.getEinde() == Pat ? "=" : "" );
	return sb.toString();
}
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
 * -------- Resultaat omzetten in string ------------------------------
 */
public ResultaatRecord getResultaatRecord()
{
	ResultaatRecord resultaatRec = new ResultaatRecord();
	resultaatRec.setRes2( "" );
	Ply Ply = getPlies().getCurrentPly();
	if ( Ply.getEinde() != Nog_niet )
	{
		resultaatRec.setRes1( Ply.getEinde().toString() );
	}
	else
	{
		Resultaat resultaat = Ply.getBoStelling().getResultaat();
		if ( resultaat != Resultaat.Illegaal )
		{
			resultaatRec.setRes1( resultaat.toString() );
		}
		if ( resultaat == Gewonnen || resultaat == Verloren )
		{
			resultaatRec.setRes2( "Mat in " + ( Ply.getBoStelling().getAantalZetten() - 1 ) );
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
 * ------- Huidige zetnummer omzetten in string -----------------------
 */
public String currentZetNummerToString()
{
	return zetNummerToString( getPlies().getCurrentPly().getZetNummer() );
}
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
 * -------- Hele zet omzetten naar string ( 55. Ke1-e2+  Ke7-d8+) -------
 */
ZetDocument createZetDocument( int aPlyNummer )
{
	// Als de eerste zet zwart is maken we puntje puntje puntje plus de  ply hierna
	Ply ply = getPlies().getPly( aPlyNummer );
	// Dit geldt toch alleen bij plynummer 0? Ja, maar alleen bij ply 0 kunnen we aangeroepen worden 
	// met zwart aan zet. Bij alle andere aanropjes is altijd wit aan zet.
	if ( ply.getBoStelling().getAanZet() == Zwart )
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
	if ( firstPly.getBoStelling().getAanZet() == Zwart )
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
 *   Bereken begin van vooruitzetten 
 */
VooruitRecord createVooruit()
{
	VooruitRecord vooruitRecord = VooruitRecord.getDefaultVooruitRecord();
	if ( getPlies().getCurrentPlyNumber() >= 0 )
	{
		vooruitRecord.setErIsVooruit( true );
		int currentPlyNumber = getPlies().getCurrentPlyNumber();
		if ( getPlies().getFirstPly().getBoStelling().getAanZet() == Wit )
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
		vooruitRecord.setHalverwege( getPlies().getCurrentPly().getBoStelling().getAanZet() == Zwart );
	}
	return vooruitRecord;
}
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
	BoStelling boStellingVan = getPlies().getCurrentPly().getBoStelling();
	List<BoStelling> gegenereerdeZetten = getGen().genereerZettenGesorteerd( boStellingVan );
	List<GegenereerdeZetDocument> zetten = new ArrayList<>();
	int zetNummer = 0;
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		Ply ply = Ply.builder()
			.zetNummer( zetNummer )
			.boStelling( boStellingVan )
			.einde( Nog_niet ) // @@NOG klopt dit??
			.vanNaar( stellingToVanNaar( boStellingVan, boStellingNaar ) )
			.schaak( getGen().isSchaak( boStellingNaar ) )
			.build();
		zetten.add( getGegenereerdeZetDocument( ply, boStellingNaar ) );
		zetNummer++;
	}
	return zetten;
}
/**
 * ----------- geef huidige stelling -------------------
 */
public BoStelling getStand()
{
	if ( isBegonnen() )
	{
		return getPlies().getCurrentPly().getBoStelling();
	}
	throw new RuntimeException( "De partij is nog niet begonnen, dus er is nog geen stand" );
}

}

