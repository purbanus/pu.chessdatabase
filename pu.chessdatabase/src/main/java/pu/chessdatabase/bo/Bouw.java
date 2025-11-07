package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.PassType;
import pu.chessdatabase.dal.VMStellingIterator;
import pu.services.StopWatch;

import lombok.Data;

@Component
@Data
public class Bouw
{

public static final long MEG = 1048576;
public static final boolean HOU_STELLINGEN_BIJ = false;

@Autowired private Dbs dbs;
@Autowired private Gen gen;
@Autowired private Config config;
private VMStellingIterator vmStellingIterator;

int passNumber;
List<BoStelling> illegaleStellingen = new ArrayList<>();
List<BoStelling> stellingenMetSchaak = new ArrayList<>();
List<BoStelling> matStellingen = new ArrayList<>();
List<BoStelling> changes = new ArrayList<>();

boolean passNchanges;

public Bouw( VMStellingIterator VMStellingIterator )
{
	passNchanges = true;
	passNumber = 0;
	vmStellingIterator = VMStellingIterator;
}
void reportNewPass( String aPassText )
{
	reportNewPass( aPassText, true );
}
void reportNewPass( String aPassText, boolean aDoPrint )
{
	if ( aDoPrint )
	{
		System.out.println( "\n" + aPassText + "\n" );
		dbs.setReport( getReportFrequency(), this::showTellers );
	}
	else
	{
		dbs.setReport( getReportFrequency(), this::showNothing );
	}
}
int getReportFrequency()
{
	switch ( getConfig().getAantalStukken() )
	{
		case 3: return (int) dbs.getDatabaseSize() / 10;
		case 4: return (int) dbs.getDatabaseSize() / 10;
		case 5: return (int) dbs.getDatabaseSize() / 100;
	}
	throw new RuntimeException( "Ongeldig aantal stukken in Config: " + getConfig().getAantalStukken() );
}
public void showNothing( int aStellingTeller, int [][] aTellingen )
{
}
public void showTellers( int aStellingTeller, int [][] aTellingen )
{
	System.out.println( "Tellingen na " + aStellingTeller + " stellingen" );
	printAlles( aTellingen );
}

/**
 * ----------- Tel resultaten ----------------------------
 */
void tel( BoStelling aBoStelling )
{
	// Hoeft niks meer te doen; al het tellen gebeurt in VMStellingIterator
}
void telAlles()
{
	telAlles( true );
}
void telAlles( boolean aDoPrint)
{
	if ( aDoPrint )
	{
		System.out.println( "Tellen van alle stellingen" );
	}
	vmStellingIterator.clearTellingen();
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::tel );
}
void telAndPrintAlles( boolean aDoPrint )
{
	telAlles( aDoPrint );
	if ( aDoPrint )
	{
		printAlles( vmStellingIterator.getTellingen() );
	}
}
void printAlles( int [][] aArrayl)
{
	System.out.println( "Illegaal met wit aan zet  : " + aArrayl[0][0] );
	System.out.println( "Gewonnen met wit aan zet  : " + aArrayl[0][1] );
    System.out.println( "Remise met wit aan zet    : " + aArrayl[0][2] );
    System.out.println( "Verloren met wit aan zet  : " + aArrayl[0][3] );
    System.out.println( "Illegaal met zwart aan zet: " + aArrayl[1][0] );
	System.out.println( "Gewonnen met zwart aan zet: " + aArrayl[1][1] );
    System.out.println( "Remise met zwart aan zet  : " + aArrayl[1][2] );
    System.out.println( "Verloren met zwart aan zet: " + aArrayl[1][3] );
}
/**
 * ------------ Kijk of een stelling illegaal is ---------------
 */

public void isIllegaal( BoStelling aBoStelling )
{
	BoStelling boStelling = aBoStelling.clone();
	if ( gen.isGeometrischIllegaal( boStelling ) || gen.isKKSchaak( boStelling ) )
	{
		boStelling.setResultaat( ILLEGAAL );
		boStelling.setAanZet( WIT );
		if ( HOU_STELLINGEN_BIJ )
		{
			illegaleStellingen.add( boStelling );
		}
		dbs.put( boStelling );

		boStelling = boStelling.clone();
		boStelling.setAanZet( ZWART );
		if ( HOU_STELLINGEN_BIJ )
		{
			illegaleStellingen.add( boStelling );
		}
		dbs.put( boStelling );
	}
}
/**
 *------------- Kontroleer schaakjes ---------------------------
 */
// @@LOW Dit kun je combineren met isMat. Dan kun je isMat over alleen de witte stellingen draaien
public void schaakjes( BoStelling aBoStellingMetWitAanZet )
{
	/*
	 * Er wordt gebruik van gemaakt dat op dit moment alle remise stellingen   
     * zowel voor wit als voor zwart remise zijn;                              
     * dat geldt trouwens ook voor illegale stellingen.                        
	 */
	BoStelling boStellingMetWitAanZet = aBoStellingMetWitAanZet.clone();
	BoStelling boStellingMetZwartAanZet = aBoStellingMetWitAanZet.clone();
	boStellingMetZwartAanZet.setAanZet( ZWART );

	// Wit aan zet
	if ( gen.isSchaak( boStellingMetWitAanZet ) )
	{
		boStellingMetWitAanZet.setSchaak( true );
		boStellingMetZwartAanZet.setSchaak( true );
		boStellingMetZwartAanZet.setResultaat( ILLEGAAL );
	}
	// Zwart aan zet
	if ( gen.isSchaak( boStellingMetZwartAanZet ) )
	{
		boStellingMetZwartAanZet.setSchaak( true );
		boStellingMetWitAanZet.setSchaak( true );
		boStellingMetWitAanZet.setResultaat( ILLEGAAL );
	}
	if ( boStellingMetWitAanZet.isSchaak() )
	{
		if ( HOU_STELLINGEN_BIJ )
		{
			stellingenMetSchaak.add( boStellingMetWitAanZet );
		}
		dbs.put( boStellingMetWitAanZet );
	}
	if ( boStellingMetZwartAanZet.isSchaak() )
	{
		if ( HOU_STELLINGEN_BIJ )
		{
			stellingenMetSchaak.add( boStellingMetZwartAanZet );
		}
		dbs.put( boStellingMetZwartAanZet );
	}
}

/**
 *----------- Kijk of een stelling mat is --------------------
 */
public void isMat( BoStelling aBoStelling )
{
	BoStelling boStelling = aBoStelling.clone();
	// Er komen alleen remisestellingen binnen
	if ( boStelling.isSchaak() == true )
	{
		List<BoStelling> gegenereerdeZetten = gen.genereerZetten( boStelling );
		if ( gegenereerdeZetten.size() == 0 )
		{
			boStelling.setResultaat( VERLOREN );
			boStelling.setAantalZetten( 1 );
			if ( HOU_STELLINGEN_BIJ )
			{
				matStellingen.add( boStelling );
			}
			dbs.put( boStelling );
		}
	}
}
/**
 * ------------ Pass 0: Initialisatie database ---------------
 */
void pass_0()
{
	pass_0( true );
}
void pass_0( boolean aDoPrint )
{
	illegaleStellingen = new ArrayList<>();
	stellingenMetSchaak = new ArrayList<>();
	matStellingen = new ArrayList<>();
	
	passNumber = 0;
	// Hier geen reportNewPass doen want de Cache is er pas in VM na de create!
	dbs.create();

	reportNewPass( "Markeren illegale stellingen", aDoPrint );
	dbs.pass( PassType.MARKEER_WIT, this::isIllegaal );

	reportNewPass( "Markeren schaakjes", aDoPrint );
	dbs.pass( PassType.MARKEER_WIT, this::schaakjes );

	reportNewPass( "Markeren matstellingen", aDoPrint );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART  , this::isMat );
}
/**
 * ------- Markeer een stelling gewonnen/verloren -----------
 */
/**
 * Markeer kan aangeroepen worden met alleen remisestellingen.
 * Zie het commentaar tussendoor voor meer details.
 * @param aBoStelling De stelling die we gaan mrkeren
 */
void markeer( BoStelling aBoStelling )
{

	if ( aBoStelling.getResultaat() != REMISE )
	{
		//telMetKleur( aBoStelling );
		return;
	}

	BoStelling boStellingVan = aBoStelling.clone();
	List<BoStelling> gegenereerdeZetten = gen.genereerZetten( boStellingVan );
	if ( gegenereerdeZetten.size() == 0 )
	{
		// Je zou zeggen, moet er hier niet mat of pat gegeven worden? Maar
		// - Alle matstelliongen zijn als zodanig gemarkeerd, namelijk als VERLOREN met aantalzetten = 1
		// - patstellingen zijn niet als zodanig gemarkeerd in de database. Als mensen willen weten
		//   of een bepaalde stelling Pat is, moeten ze zetten genereren en als dataantal nul is en het
		//   en het is geen schaak, is het Pat anders Mat	
		// Patstellingen blijven hier dus remise en dat klopt. Matstellingen komen hier niet binnen
		// want alleen remisestellingen komen hier binnen.
		return;
	}
	// We beginnen met VERLOREN omdat dat het meest pessimistische resultaat is
	boStellingVan.setResultaat( VERLOREN ); 

	int minGewonnen = Integer.MAX_VALUE;
	int maxVerloren = Integer.MIN_VALUE;
	for ( BoStelling boStellingNaar : gegenereerdeZetten )
	{
		int aantal = boStellingNaar.getAantalZetten();
		switch( boStellingNaar.getResultaat() )
		{
			case VERLOREN:
			{
				// Als degene die aan zet is: 
				// - tot nu toe verloren staat, is gewonnen gewoon beter
				// - tot nu toe remise staat, is gewonnen gewoon beter
				// - tot nu toe gewonnen staat, kan setgewonnen geen kwaadis gewonnen gewoon beter
				boStellingVan.setResultaat( GEWONNEN );
				if ( aantal < minGewonnen )
				{
					minGewonnen = aantal;
				}
				break;
			}
			case GEWONNEN:
			{
				// Als degene die aan zet is: 
				// - tot nu toe verloren staat, kan setVerloren geen kwaad 
				// - tot nu toe remise staat, houden we het op remise
				// - tot nu toe gewonnen staat, houden we het op gewonnen
				// Dus niet dit dus:
				// boStellingVan.setResultaat( VERLOREN );
				if ( aantal > maxVerloren )
				{
					maxVerloren = aantal;
				}
				break;
			}
			case REMISE:
			{
				// Dit hangt er van af wie er aan zet is.
				// Wit aan zet:
				// - als hij verloren staat tot nu toe, is remize te prefereren
				// - als hij gewonnen staat, doet remise er niet toe
				// zwart aan zet:
				// - als hij verloren staat tot nu toe, is remize te prefereren
				// - als hij gewonnen staat, doet remise er niet toe
				// Dus ik ben het hier nu mee eens
				if ( boStellingVan.getResultaat() == VERLOREN )
				{
					boStellingVan.setResultaat( REMISE );
				}
				break;
			}
			//$CASES-OMITTED$
			default:
			{
				throw new RuntimeException( "Ongeldige switch case " + boStellingNaar.getResultaat() );
			}
		}
	}
	//telMetKleur( boStellingVan );
	if ( boStellingVan.getResultaat() != REMISE )
	{
		if ( boStellingVan.getResultaat() == GEWONNEN )
		{
			boStellingVan.setAantalZetten( minGewonnen + 1 );
		}
		else
		{
			boStellingVan.setAantalZetten( maxVerloren );
		}
		if ( HOU_STELLINGEN_BIJ )
		{
			changes.add( boStellingVan );
		}
		dbs.put( boStellingVan );
		passNchanges = true;
	}
}
/**
---------- Pass over de stellingen die nog niet gewonnen/verloren zijn ------------
 */
void pass_n()
{
	reportNewPass( "Pass " + passNumber );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::markeer );
}
/**
 * ---------- Markeer tot er niets meer verandert ------------------
 */
public void bouwDatabase()
{
	System.out.println( "We bouwen op: " + dbs.getDatabaseName() );
	StopWatch timer = new StopWatch();
	passNumber = 0;
	pass_0();
	System.out.println( "Pass: " + passNumber + " duurde " + timer.getLapTimeMs() );
	while ( passNchanges )
	{
		passNchanges = false;
		pass_n();
		passNumber++;
		System.out.println( "Pass: " + passNumber + " duurde " + timer.getLapTimeMs() );
	}
	System.out.println( "Totaaltijd: " + timer.getElapsedMs() );
}

}
