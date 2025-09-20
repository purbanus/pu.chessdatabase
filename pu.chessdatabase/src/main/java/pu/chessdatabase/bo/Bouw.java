package pu.chessdatabase.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;

@Service
public class Bouw
{
public static final long MEG = 1048576;

int PassNr;
boolean PassNchanges;
@Autowired private Dbs dbs;
@Autowired private Gen gen;

long [] RptPrev = new long[4];
long [] RptTot  = new long[4];

public Bouw()
{
	PassNchanges = true;
	PassNr = 0;
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
void inzReport()
{
	for ( ResultaatType resultaatType : ResultaatType.values() )
	{
		RptPrev[resultaatType.ordinal()] = 0;
		RptTot [resultaatType.ordinal()] = 0;
	}
	RptPrev[ResultaatType.Remise.ordinal()] = 5 * MEG;
	RptTot [ResultaatType.Remise.ordinal()] = 5 * MEG;
}
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
void setTotals( long [] aReportArray )
{
	RptTot = aReportArray;
//	for ( int x = 0; x < 4; x++ )
//	{
//		RptTot[x] = aReportArray[x];
//	}
}
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
void showThisPass( long [] aReportArray )
{
	//Window.Use(Win.BouwWin);
	aReportArray[ResultaatType.Remise.ordinal()] = 
		- aReportArray[ResultaatType.Illegaal.ordinal()]
		- aReportArray[ResultaatType.Gewonnen.ordinal()]
		- aReportArray[ResultaatType.Verloren.ordinal()];
//	Window.GotoXY(12, 2); IO.WrLngInt(RA[Illegaal], 10);
//	Window.GotoXY(12, 3); IO.WrLngInt(RA[Remise  ], 10);
//	Window.GotoXY(12, 4); IO.WrLngInt(RA[Gewonnen], 10);
//	Window.GotoXY(12, 5); IO.WrLngInt(RA[Verloren], 10);
}
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
void showTotals( long [] aReportArray )
{
//	Window.Use(Win.BouwWin);
//	Window.GotoXY(36, 2); IO.WrLngInt(RA[Illegaal], 12);
//	Window.GotoXY(36, 3); IO.WrLngInt(RA[Remise  ], 12);
//	Window.GotoXY(36, 4); IO.WrLngInt(RA[Gewonnen], 12);
//	Window.GotoXY(36, 5); IO.WrLngInt(RA[Verloren], 12);
}
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
void reportNewPass( String aPassText )
{
	for ( ResultaatType resultaatType : ResultaatType.values() )
	{
		RptTot[resultaatType.ordinal()] = RptTot[resultaatType.ordinal()] + RptPrev[resultaatType.ordinal()];
	}
	RptPrev = dbs.GetTellers();
	RptPrev[ResultaatType.Remise.ordinal()] = 
		- RptPrev[ResultaatType.Illegaal.ordinal()]
		- RptPrev[ResultaatType.Gewonnen.ordinal()]
		- RptPrev[ResultaatType.Verloren.ordinal()];
	RptTot[ResultaatType.Remise.ordinal()] = 
		- RptTot[ResultaatType.Illegaal.ordinal()]
		- RptTot[ResultaatType.Gewonnen.ordinal()]
		- RptTot[ResultaatType.Verloren.ordinal()];
	dbs.ClearTellers(); // @@NOG Op de een of and're manier maakt hij nu de getallen in RptPrev allemaal nul
//	Window.Use(Win.BouwWin);
//	Window.Clear();
//	Window.GotoXY(12, 1); IO.WrStr(' Deze pass');
//	Window.GotoXY(24, 1); IO.WrStr('    Vorige');
//	Window.GotoXY(36, 1); IO.WrStr('    Totaal');
//	Window.GotoXY( 2, 2); IO.WrStr('Illegaal  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Illegaal], 12); IO.WrLngInt(RptTot[Illegaal], 12);
//	Window.GotoXY( 2, 3); IO.WrStr('Remise    '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Remise  ], 12); IO.WrLngInt(RptTot[Remise  ], 12);
//	Window.GotoXY( 2, 4); IO.WrStr('Gewonnen  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Gewonnen], 12); IO.WrLngInt(RptTot[Gewonnen], 12);
//	Window.GotoXY( 2, 5); IO.WrStr('Verloren  '); IO.WrStr('         0'); IO.WrLngInt(RptPrev[Verloren], 12); IO.WrLngInt(RptTot[Verloren], 12);
//	Window.GotoXY( 2, 7); IO.WrStr('Pass '); IO.WrCard(PassNr, 3); IO.WrChar(' '); IO.WrStr(PassText);
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
}
void isIllegaal( BoStelling aStelling )
{
	if ( gen.IsGeomIllegaal( aStelling ) || gen.isKKSchaak( aStelling ) )
	{
		aStelling.setResultaat( ResultaatType.Illegaal );
		aStelling.setAanZet( AlgDef.Wit );
		dbs.Put( aStelling );
		aStelling.setAanZet( AlgDef.Zwart );
		dbs.Put( aStelling );
	}
}
void delete()
{
	dbs.delete();
}
}
