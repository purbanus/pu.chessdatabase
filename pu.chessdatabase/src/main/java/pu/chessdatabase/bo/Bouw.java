package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.PassType;
import pu.services.StopWatch;

@Component
public class Bouw
{
public static final long MEG = 1048576;
public static final boolean HOU_STELLINGEN_BIJ = false;

@Autowired private Dbs dbs;
@Autowired private Gen gen;

int passNr;
private int rptFreq = dbs.DFT_RPT_FREQ;
boolean passNchanges;

private long [] rptPrev = new long[4];
private long [] rptTot  = new long[4];

public Bouw()
{
	passNchanges = true;
	passNr = 0;
	//Window.PutOnTop(Win.BouwWin);
}
/**
 * ==========================================================================
		Deel 1: Rapportage
===========================================================================
*)
PROCEDURE InzReport();
VAR x: Dbs.ResType;
BEGIN
	FOR x:=MIN(ResType) TO MAX(ResType) DO
		RptPrev[x]:=0;
		RptTot [x]:=0;
	END;
	RptPrev[Remise]:=5*Meg;
	RptTot [Remise]:=5*Meg;
END InzReport;
 */
/**
 * (*------------ Initialisatie rapportage ------------------*)
 */
//void inzReport()
//{
//	for ( ResultaatType resultaatType : ResultaatType.values() )
//	{
//		rptPrev[resultaatType.ordinal()] = 0;
//		rptTot [resultaatType.ordinal()] = 0;
//	}
//	rptPrev[REMISE.ordinal()] = 5 * MEG;
//	rptTot [REMISE.ordinal()] = 5 * MEG;
//}
/**
PROCEDURE SetTotals(RA: Dbs.ReportArray);
BEGIN
	RptTot:=RA;
	(*...
	FOR x:=MIN(ResType) TO MAX(ResType) DO
		RptTot [x]:=RA[x];
	END;
    *)
END SetTotals;
 */
/**
 * ---------- Geef de eindtotalen een beginwaarde ----------------
 */
//void setTotals( long [] aReportArray )
//{
//	rptTot = aReportArray;
////	for ( int x = 0; x < 4; x++ )
////	{
////		RptTot[x] = aReportArray[x];
////	}
//}
/**
PROCEDURE ShowThisPass(RA: Dbs.ReportArray);
BEGIN
	Window.Use(Win.BouwWin);
	RA[Remise]:= -RA[Illegaal] - RA[Gewonnen] - RA[Verloren];
	Window.GotoXY(12, 2); IO.WrLngInt(RA[Illegaal], 10);
	Window.GotoXY(12, 3); IO.WrLngInt(RA[Remise  ], 10);
	Window.GotoXY(12, 4); IO.WrLngInt(RA[Gewonnen], 10);
	Window.GotoXY(12, 5); IO.WrLngInt(RA[Verloren], 10);
END ShowThisPass;
 */
/**
 * -------- Bijwerken tellers deze pass -----------------------
 */
//void showThisPass( long [] aReportArray )
//{
//	//Window.Use(Win.BouwWin);
//	aReportArray[REMISE.ordinal()] = 
//		- aReportArray[ILLEGAAL.ordinal()]
//		- aReportArray[GEWONNEN.ordinal()]
//		- aReportArray[VERLOREN.ordinal()];
////	Window.GotoXY(12, 2); IO.WrLngInt(RA[Illegaal], 10);
////	Window.GotoXY(12, 3); IO.WrLngInt(RA[Remise  ], 10);
////	Window.GotoXY(12, 4); IO.WrLngInt(RA[Gewonnen], 10);
////	Window.GotoXY(12, 5); IO.WrLngInt(RA[Verloren], 10);
//}
/**
 * (*-------- Totalen laten zien --------------------------------*)
PROCEDURE ShowTotals(RA: Dbs.ReportArray);
BEGIN
	Window.Use(Win.BouwWin);
	Window.GotoXY(36, 2); IO.WrLngInt(RA[Illegaal], 12);
	Window.GotoXY(36, 3); IO.WrLngInt(RA[Remise  ], 12);
	Window.GotoXY(36, 4); IO.WrLngInt(RA[Gewonnen], 12);
	Window.GotoXY(36, 5); IO.WrLngInt(RA[Verloren], 12);
END ShowTotals;
 */
/**
 * -------- Totalen laten zien --------------------------------
 */
//void showTotals( long [] aReportArray )
//{
////	Window.Use(Win.BouwWin);
////	Window.GotoXY(36, 2); IO.WrLngInt(RA[Illegaal], 12);
////	Window.GotoXY(36, 3); IO.WrLngInt(RA[Remise  ], 12);
////	Window.GotoXY(36, 4); IO.WrLngInt(RA[Gewonnen], 12);
////	Window.GotoXY(36, 5); IO.WrLngInt(RA[Verloren], 12);
//}
/**
PROCEDURE ReportNewPass(PassText: ARRAY OF CHAR);
VAR x: ResType;
BEGIN
	FOR x:=MIN(ResType) TO MAX(ResType) DO
    	RptTot [x]:=RptTot [x] + RptPrev[x];
	END;
	RptPrev:=Dbs.GetTellers();
	RptPrev[Remise]:= -RptPrev[Illegaal] - RptPrev[Gewonnen] - RptPrev[Verloren];
	RptTot [Remise]:= -RptTot [Illegaal] - RptTot [Gewonnen] - RptTot [Verloren];
	Dbs.ClearTellers();
	Window.Use(Win.BouwWin);
	Window.Clear();
	Window.GotoXY(12, 1); IO.WrStr(' Deze pass');
	Window.GotoXY(24, 1); IO.WrStr('    Vorige');
	Window.GotoXY(36, 1); IO.WrStr('    Totaal');
	Window.GotoXY( 2, 2); IO.WrStr('Illegaal  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Illegaal], 12); IO.WrLngInt(RptTot[Illegaal], 12);
	Window.GotoXY( 2, 3); IO.WrStr('Remise    '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Remise  ], 12); IO.WrLngInt(RptTot[Remise  ], 12);
	Window.GotoXY( 2, 4); IO.WrStr('Gewonnen  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Gewonnen], 12); IO.WrLngInt(RptTot[Gewonnen], 12);
	Window.GotoXY( 2, 5); IO.WrStr('Verloren  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Verloren], 12); IO.WrLngInt(RptTot[Verloren], 12);
	Window.GotoXY( 2, 7); IO.WrStr('Pass '); IO.WrCard(PassNr, 3); IO.WrChar(' '); IO.WrStr(PassText);
END ReportNewPass;
 */
/**
 * ------------ Bijwerken tellers als een nieuwe pass begint --------
 */
//void reportNewPass( String aPassText )
//{
//	for ( ResultaatType resultaatType : ResultaatType.values() )
//	{
//		rptTot[resultaatType.ordinal()] = rptTot[resultaatType.ordinal()] + rptPrev[resultaatType.ordinal()];
//	}
//	rptPrev = dbs.getTellers();
//	rptPrev[REMISE.ordinal()] = 
//		- rptPrev[ILLEGAAL.ordinal()]
//		- rptPrev[GEWONNEN.ordinal()]
//		- rptPrev[VERLOREN.ordinal()];
//	rptTot[REMISE.ordinal()] = 
//		- rptTot[ILLEGAAL.ordinal()]
//		- rptTot[GEWONNEN.ordinal()]
//		- rptTot[VERLOREN.ordinal()];
//	dbs.clearTellers(); // @@NOG Op de een of and're manier maakt hij nu de getallen in RptPrev allemaal nul
////	Window.Use(Win.BouwWin);
////	Window.Clear();
////	Window.GotoXY(12, 1); IO.WrStr(' Deze pass');
////	Window.GotoXY(24, 1); IO.WrStr('    Vorige');
////	Window.GotoXY(36, 1); IO.WrStr('    Totaal');
////	Window.GotoXY( 2, 2); IO.WrStr('Illegaal  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Illegaal], 12); IO.WrLngInt(RptTot[Illegaal], 12);
////	Window.GotoXY( 2, 3); IO.WrStr('Remise    '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Remise  ], 12); IO.WrLngInt(RptTot[Remise  ], 12);
////	Window.GotoXY( 2, 4); IO.WrStr('Gewonnen  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Gewonnen], 12); IO.WrLngInt(RptTot[Gewonnen], 12);
////	Window.GotoXY( 2, 5); IO.WrStr('Verloren  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Verloren], 12); IO.WrLngInt(RptTot[Verloren], 12);
////	Window.GotoXY( 2, 7); IO.WrStr('Pass '); IO.WrCard(PassNr, 3); IO.WrChar(' '); IO.WrStr(PassText);
/**
PROCEDURE IsIllegaal(S: Dbs.Stelling);
BEGIN
	IF Gen.IsGeomIllegaal(S) OR Gen.IsKKschaak(S) THEN
		S.Resultaat:= Illegaal;
		S.AanZet:=Wit;
		Dbs.Put(S);
		S.AanZet:=Zwart;
		Dbs.Put(S);
	END;
END IsIllegaal;
 */
/**
 * ------------ Kijk of een stelling illegaal is ---------------
 */

List<BoStelling> illegaalStellingen = new ArrayList<>();
List<BoStelling> stellingenMetSchaak = new ArrayList<>();
List<BoStelling> matStellingen = new ArrayList<>();
List<BoStelling> matStellingenMetWit = new ArrayList<>();
List<BoStelling> matStellingenMetZwart = new ArrayList<>();
public void isIllegaal( BoStelling aBoStelling )
{
	BoStelling boStelling = aBoStelling.clone();
	if ( gen.isGeomIllegaal( boStelling ) || gen.isKKSchaak( boStelling ) )
	{
		boStelling.setResultaat( ILLEGAAL );
		boStelling.setAanZet( WIT );
		if ( HOU_STELLINGEN_BIJ )
		{
			illegaalStellingen.add( boStelling );
		}
		dbs.put( boStelling );

		boStelling = boStelling.clone();
		boStelling.setAanZet( ZWART );
		if ( HOU_STELLINGEN_BIJ )
		{
			illegaalStellingen.add( boStelling );
		}
		dbs.put( boStelling );
	}
}
/**
PROCEDURE Schaakjes(Swaz: Dbs.Stelling);
VAR Szaz: Dbs.Stelling;
BEGIN
	
    (* Er wordt gebruik van gemaakt dat op dit moment alle remise stellingen   *)
    (* zowel voor wit als voor zwart remise zijn;                              *)
    (* dat geldt trouwens ook voor illegale stellingen.                        *)

	IF Swaz.Resultaat = Remise THEN
		Szaz:=Swaz;
		Szaz.AanZet:=Zwart;
		(*---- Wit aan zet ----- *)
    	IF Gen.IsSchaak(Swaz) THEN
    		Swaz.Schaak:=TRUE;
    		Szaz.Schaak:=TRUE;
    		Szaz.Resultaat:=Illegaal;
    	END;
		(*---- Zwart aan zet ----*)
    	IF Gen.IsSchaak(Szaz) THEN
    		Swaz.Schaak:=TRUE;
    		Szaz.Schaak:=TRUE;
			Swaz.Resultaat:=Illegaal;
		END;
		IF Swaz.Schaak THEN
	    	Dbs.Put(Swaz);
    	END;
		IF Szaz.Schaak THEN
	    	Dbs.Put(Szaz);
    	END;
    END;
END Schaakjes;
 */
/**
 *------------- Kontroleer schaakjes ---------------------------
 */
	// @@HIGH Ik begrijp niet hoe dit werkt
public void schaakjes( BoStelling aBoStellingMetWitAanZet )
{
	/*
	 * Er wordt gebruik van gemaakt dat op dit moment alle remise stellingen   
     * zowel voor wit als voor zwart remise zijn;                              
     * dat geldt trouwens ook voor illegale stellingen.                        
	 */
	if ( aBoStellingMetWitAanZet.getResultaat() == REMISE )
	{
		BoStelling boStellingMetWitAanZet = aBoStellingMetWitAanZet.clone();
		BoStelling boStellingMetZwartAanZet = aBoStellingMetWitAanZet.clone();
		boStellingMetZwartAanZet.setAanZet( ZWART );
//		boStellingMetWitAanZet.setSchaak( gen.isSchaak( boStellingMetWitAanZet ) );
//		boStellingMetZwartAanZet.setSchaak( gen.isSchaak( boStellingMetZwartAanZet ) );

		// Wit aan zet
//		if ( boStellingMetWitAanZet.isSchaak() )
		if ( gen.isSchaak( boStellingMetWitAanZet ) )
		{
			boStellingMetWitAanZet.setSchaak( true );
			boStellingMetZwartAanZet.setSchaak( true );
			boStellingMetZwartAanZet.setResultaat( ILLEGAAL );
		}
		// Zwart aan zet
//		if ( boStellingMetZwartAanZet.isSchaak() )
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
}

/**
PROCEDURE IsMat(S: Dbs.Stelling);
VAR GZ: Gen.GenZrec;
BEGIN
	IF (S.Resultaat = Remise) AND (S.Schaak = TRUE) THEN
		GZ:=Gen.GenZ(S);
		IF GZ.Aantal = 0 THEN
			S.Resultaat:=Verloren;
			S.Aantal   :=1;
			Dbs.Put(S);
		END;
	END;
END IsMat;
 */
/**
 *----------- Kijk of een stelling mat is --------------------
 */
public void isMat( BoStelling aBoStelling )
{
	BoStelling boStelling = aBoStelling.clone();
	if ( boStelling.getResultaat() == REMISE && boStelling.isSchaak() == true )
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
//			if ( boStelling.getAanZet() == WIT )
//			{
//				matStellingenMetWit.add( boStelling );
//			}
//			else
//			{
//				matStellingenMetZwart.add( boStelling );
//			}
			dbs.put( boStelling );
		}
	}
}
/**
PROCEDURE Pass_0();
BEGIN
	PassNr:=0;
	Dbs.SetReport(4096, ShowThisPass);
	Dbs.ClearTellers();
	InzReport();
	ReportNewPass('Reserveren schijfruimte');
	Dbs.Create();

	ReportNewPass('Illegaal');
	Dbs.Pass(MarkeerWit, IsIllegaal);

	ReportNewPass('Schaakjes');
	Dbs.Pass(MarkeerWit, Schaakjes);

	Dbs.SetReport( 100, ShowThisPass);
	ReportNewPass('Matstellingen');
	Dbs.Pass(MarkeerWit  , IsMat);
	Dbs.Pass(MarkeerZwart, IsMat);
END Pass_0;
 */
/**
 * ------------ Pass 0: Initialisatie database ---------------
 */
void pass_0()
{
	illegaalStellingen = new ArrayList<>();
	stellingenMetSchaak = new ArrayList<>();
	matStellingen = new ArrayList<>();
	
	passNr = 0;
//	dbs.setReport( rptFreq, this::showThisPass );
	dbs.clearTellers();
//	inzReport();
//	reportNewPass( "Reserveren schijfruimte" );
	dbs.create();

//	reportNewPass( "Illegaal" );
	dbs.pass( PassType.MARKEER_WIT, this::isIllegaal );

//	reportNewPass( "Schaakjes" );
	dbs.pass( PassType.MARKEER_WIT, this::schaakjes );

//	dbs.setReport( 100, this::showThisPass );
//	reportNewPass( "Matstellingen" );
	dbs.pass( PassType.MARKEER_WIT  , this::isMat );
	dbs.pass( PassType.MARKEER_ZWART, this::isMat );
}
/**
 * ==============================================================================
		Deel 3: Verzamel statistische informatie
==============================================================================
*)

VAR ResTeller: CARDINAL;
PROCEDURE Tel(S: Dbs.Stelling);
BEGIN
	INC(RptTot[S.Resultaat]);
	INC(ResTeller);
	IF ResTeller>=4096 THEN
		ShowTotals(RptTot);
		ResTeller:=0;
	END;
END Tel;
 */
/**
 * ----------- Tel resultaten ----------------------------
 */
int resTeller;
void tel( BoStelling aBoStelling )
{
	rptTot[aBoStelling.getResultaat().ordinal()]++;
	resTeller++;
	if ( resTeller >= rptFreq )
	{
//		showTotals( rptTot );
		resTeller = 0;
	}
}
/**
PROCEDURE TelAlles();
VAR S: Dbs.Stelling;
	x: Dbs.ResType;
BEGIN
	InzReport();
	RptPrev[Remise] := 0;
	RptTot [Remise] := 0;
	ReportNewPass('Tellen van alle stellingen');
	ResTeller:=0;
	Dbs.Pass(WitEnZwart, Tel);
END TelAlles;
 */
/**
 * ----------- Tel alle stellingen in de database --------------
 */
void telAlles()
{
//	inzReport();
	rptPrev[REMISE.ordinal()] = 0;
	rptTot[REMISE.ordinal()] = 0;
//	reportNewPass( "Tellen van alle stellingen" );
	resTeller = 0;
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::tel );
}
long[][] tellersMetKleur;
void telMetKleur( BoStelling aBoStelling )
{
	int aanZet = aBoStelling.getAanZet() == ZWART ? 1 : 0;
	tellersMetKleur[aanZet][aBoStelling.getResultaat().ordinal()]++;
	resTeller++;
	if ( resTeller >= rptFreq )
	{
//		showTotals( rptTot );
		resTeller = 0;
	}
}
void telAllesMetKleur()
{
	tellersMetKleur = new long[2][4];
//	reportNewPass( "Tellen van alle stellingen" );
	resTeller = 0;
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::telMetKleur );
}
void printAllesMetKleur()
{
	telAllesMetKleur();
	System.out.println( "Illegaal met wit aan zet  : " + tellersMetKleur[0][0] );
	System.out.println( "Gewonnen met wit aan zet  : " + tellersMetKleur[0][1] );
    System.out.println( "Remise met wit aan zet    : " + tellersMetKleur[0][2] );
    System.out.println( "Verloren met wit aan zet  : " + tellersMetKleur[0][3] );
    System.out.println( "Illegaal met zwart aan zet: " + tellersMetKleur[1][0] );
	System.out.println( "Gewonnen met zwart aan zet: " + tellersMetKleur[1][1] );
    System.out.println( "Remise met zwart aan zet  : " + tellersMetKleur[1][2] );
    System.out.println( "Verloren met zwart aan zet: " + tellersMetKleur[1][3] );
}
/**
 * ================================================================================
		Deel 4: Markeer stellingen gewonnen of verloren
=================================================================================

PROCEDURE Markeer(S: Dbs.Stelling);
VAR GZ: Gen.GenZrec;
	MinGewonnen, MaxVerloren: Dbs.AantalZetten;
	x : Gen.AantalGzetten;
BEGIN
	GZ:=Gen.GenZ(S);
	IF GZ.Aantal = 0 THEN
		RETURN;
	END;
	S.Resultaat:=Verloren;
	MinGewonnen:=MAX(Dbs.AantalZetten);
	MaxVerloren:=MIN(Dbs.AantalZetten);
	FOR x:=1 TO GZ.Aantal DO
		CASE GZ.Sptr^.Resultaat OF
		|	Verloren:
				S.Resultaat:=Gewonnen;
				IF GZ.Sptr^.Aantal < MinGewonnen THEN
					MinGewonnen:=GZ.Sptr^.Aantal;
				END;
		|	Gewonnen:
				IF GZ.Sptr^.Aantal > MaxVerloren THEN
					MaxVerloren:=GZ.Sptr^.Aantal;
				END;
		|	Remise  :
				IF S.Resultaat = Verloren THEN
					S.Resultaat:=Remise;
				END;
		END;
		IncAddr(GZ.Sptr, SIZE(Dbs.Stelling));
	END;
	IF S.Resultaat # Remise THEN
		IF S.Resultaat = Gewonnen THEN
			S.Aantal:=MinGewonnen + 1;
		ELSE
			S.Aantal:=MaxVerloren;
		END;
		Dbs.Put(S);
		PassNchanges:=TRUE;
	END;
END Markeer;
 */
/**
 * ------- Markeer een stelling gewonnen/verloren -----------
 */
List<BoStelling> changes = new ArrayList<>();
/**
 * Markeer kan aangeroepen worden met alleen remisestellingen.
 * Zie het commentaar tussendoor voor meer details.
 * @param aBoStelling De stelling die we gaan mrkeren
 */
void markeer( BoStelling aBoStelling )
{
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
PROCEDURE Pass_n();
BEGIN
	Dbs.SetReport(100, ShowThisPass);
	ReportNewPass('Wit aan zet  ');
	Dbs.Pass(MarkeerWit, Markeer);
	
	ReportNewPass('Zwart aan zet');
	Dbs.Pass(MarkeerZwart, Markeer);
END Pass_n;
 */
/**
---------- Pass over de stellingen die nog niet gewonnen/verloren zijn ------------
 */
void pass_n()
{
//	dbs.setReport( 100, this::showThisPass );
//	reportNewPass( "Wit aan zet" );
	dbs.pass( PassType.MARKEER_WIT, this::markeer );

//	reportNewPass( "Zwart aan zet" );
	dbs.pass( PassType.MARKEER_ZWART, this::markeer );
}
/**
PROCEDURE BouwDataBase(StartPass: CARDINAL);
BEGIN
	PassNr:=StartPass;
	TelAlles();
	WHILE PassNchanges AND NOT IO.KeyPressed() DO
		PassNchanges:=FALSE;
		Pass_n();
		INC(PassNr);
	END;
END BouwDataBase;
 */
/**
 * ---------- Markeer tot er niets meer verandert ------------------
 */
public void bouwDatabase()
{
	System.out.println( "We bouwen op: " + dbs.getDatabaseName() );
	StopWatch overallTimer = new StopWatch();
	passNr = 0;
	pass_0();
	telAlles();
	printAllesMetKleur();
	while ( passNchanges )
	{
		StopWatch timer = new StopWatch();
		passNchanges = false;
		pass_n();
		passNr++;
		System.out.println( "Pass: " + passNr + " duurde " + timer.getElapsedMs() );
		printAllesMetKleur();
	}
	System.out.println( "Totaaltijd: " + overallTimer.getElapsedMs() );
}

void delete()
{
	dbs.delete();
}
}
