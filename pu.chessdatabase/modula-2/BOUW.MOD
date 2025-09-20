IMPLEMENTATION MODULE Bouw;


IMPORT Window, IO;
IMPORT Win;
IMPORT Dbs, Gen;

FROM Lib IMPORT IncAddr;
FROM Dbs IMPORT ResType, PassType;
FROM AlgDef IMPORT Wit, Zwart;

CONST
	Meg = 1048576;
	
VAR
	PassNr      : CARDINAL;
	PassNchanges: BOOLEAN;

VAR
	RptPrev     : Dbs.ReportArray;
	RptTot      : Dbs.ReportArray;

(*
===========================================================================
		Deel 1: Rapportage
===========================================================================
*)


(*------------ Initialisatie rapportage ------------------*)
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



(*---------- Geef de eindtotalen een beginwaarde ----------------*)
PROCEDURE SetTotals(RA: Dbs.ReportArray);
BEGIN
	RptTot:=RA;
	(*...
	FOR x:=MIN(ResType) TO MAX(ResType) DO
		RptTot [x]:=RA[x];
	END;
    *)
END SetTotals;


(*-------- Bijwerken tellers deze pass -----------------------*)
PROCEDURE ShowThisPass(RA: Dbs.ReportArray);
BEGIN
	Window.Use(Win.BouwWin);
	RA[Remise]:= -RA[Illegaal] - RA[Gewonnen] - RA[Verloren];
	Window.GotoXY(12, 2); IO.WrLngInt(RA[Illegaal], 10);
	Window.GotoXY(12, 3); IO.WrLngInt(RA[Remise  ], 10);
	Window.GotoXY(12, 4); IO.WrLngInt(RA[Gewonnen], 10);
	Window.GotoXY(12, 5); IO.WrLngInt(RA[Verloren], 10);
END ShowThisPass;


(*-------- Totalen laten zien --------------------------------*)
PROCEDURE ShowTotals(RA: Dbs.ReportArray);
BEGIN
	Window.Use(Win.BouwWin);
	Window.GotoXY(36, 2); IO.WrLngInt(RA[Illegaal], 12);
	Window.GotoXY(36, 3); IO.WrLngInt(RA[Remise  ], 12);
	Window.GotoXY(36, 4); IO.WrLngInt(RA[Gewonnen], 12);
	Window.GotoXY(36, 5); IO.WrLngInt(RA[Verloren], 12);
END ShowTotals;


(*------------ Bijwerken tellers als een nieuwe pass begint --------*)
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


(*
==============================================================================
		Deel 2: Pass 0
==============================================================================
*)			

(*------------ Kijk of een stelling illegaal is ---------------*)
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


(*------------- Kontroleer schaakjes ---------------------------*)
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


(*----------- Kijk of een stelling mat is --------------------*)
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



(*------------ Pass 0: Initialisatie database ---------------*)
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


(*
==============================================================================
		Deel 3: Verzamel statistische informatie
==============================================================================
*)

(*----------- Tel resultaten ----------------------------*)
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


(*----------- Tel alle stellingen in de database --------------*)
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


(*
================================================================================
		Deel 4: Markeer stellingen gewonnen of verloren
=================================================================================
*)

	
(*-------- Markeer een stelling gewonnen/verloren -----------*)
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
	


(*---------- Pass over de stellingen die nog niet gewonnen/verloren zijn ------------*)
PROCEDURE Pass_n();
BEGIN
	Dbs.SetReport(100, ShowThisPass);
	ReportNewPass('Wit aan zet  ');
	Dbs.Pass(MarkeerWit, Markeer);
	
	ReportNewPass('Zwart aan zet');
	Dbs.Pass(MarkeerZwart, Markeer);
END Pass_n;


(*---------- Markeer tot er niets meer verandert ------------------*)
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


BEGIN
	PassNchanges:=TRUE;
	PassNr:=0;
	Window.PutOnTop(Win.BouwWin);
END Bouw.