IMPLEMENTATION MODULE Display;

IMPORT IO, Window, Str;
IMPORT Win, Key;
IMPORT Partij;
FROM Window IMPORT RelCoord;
FROM Lib IMPORT IncAddr;
FROM AlgDef IMPORT Wit, Zwart, StukNummer;
FROM Dbs IMPORT Stelling;
(*@@@ verboden*) FROM Gen IMPORT AsciiToVeld, BordCoord;

CONST
	VertZetten      = SHORTCARD(22);

TYPE
	DeskTopBool = ARRAY SHORTCARD[1..5] OF BOOLEAN;
	
		
VAR
	CurStelling: Stelling;
	DeskTop    : DeskTopBool;
	

(*
====================================================================================
		Deel 1: het bord
====================================================================================
*)


(*---------- Initialiseer bord eerste keer ------------*)
PROCEDURE InzBord();
VAR x, y: BordCoord;
BEGIN
	WITH CurStelling DO
		WK:=0; ZK:=0; s3:=0; s4:=0;
	END;
	Window.Use(Win.Bord);
	Window.TextColor(Black);
	Window.Clear();
	FOR y:=8 TO 1 BY -1 DO
		Window.GotoXY(1, RelCoord(9-y));
		FOR x:=1 TO 8 DO
			IF ODD(x+y) THEN
				Window.TextBackground(WitVeldKleur);
			ELSE
				Window.TextBackground(ZwartVeldKleur);
			END;
			IO.WrStr('  ');
		END;
	END;
END InzBord;

			
(*-------- Zet nieuwe stelling op bord -----------*)
PROCEDURE WrBord();
VAR SI: Partij.StukInfoRec;
	x : StukNummer;
BEGIN
	Window.PutOnTop(Win.Bord);
	Window.PutBeneath(Win.BordAchtergrond, Win.Bord);

	(* Weghalen huidige stelling *)
	Window.TextColor(Black);
	FOR x:=MIN(StukNummer) TO MAX(StukNummer) DO
		SI:=Partij.GetStukInfo(CurStelling, x);
		IF ODD(SI.X + SI.Y) THEN
			Window.TextBackground(WitVeldKleur);
		ELSE
			Window.TextBackground(ZwartVeldKleur);
		END;
		Window.GotoXY(RelCoord(2*SI.X), RelCoord(9-SI.Y));
		IO.WrChar(' ');
	END;
	
    (* Nieuwe stelling opzetten *)
	CurStelling:=Partij.GetStand();

	(* In omgekeerde volgorde ivm geslagen stukken *)
	FOR x:=MAX(StukNummer) TO MIN(StukNummer) BY -1 DO
		SI:=Partij.GetStukInfo(CurStelling, x);
		IF ODD(SI.X + SI.Y) THEN
			Window.TextBackground(WitVeldKleur);
		ELSE
			Window.TextBackground(ZwartVeldKleur);
		END;
		IF SI.Kleur = Wit THEN
			Window.TextColor(WitStukKleur);
		ELSE
			Window.TextColor(ZwartStukKleur);
		END;
		Window.GotoXY(RelCoord(2*SI.X), RelCoord(9-SI.Y));
		IO.WrChar(SI.StukAfk);
	END;
END WrBord;


PROCEDURE WrAanZet();
VAR S: Stelling;
BEGIN
	Window.PutOnTop(Win.AanZet);
	Window.Clear();
	S:=Partij.GetStand();
	IF S.AanZet = Wit THEN
		Window.TextColor(White);
		        (*01234567890123456789*)
		IO.WrStr('     Wit aan zet ');
	ELSE
		Window.TextColor(Black);
		IO.WrStr('    Zwart aan zet');
	END;
END WrAanZet;


PROCEDURE WrResultaat();
VAR ResRec: Partij.ResultaatRec;
BEGIN
	ResRec:=Partij.ResToStr();
	Window.PutOnTop(Win.Resultaat);
	Window.Clear();
	Window.DirectWrite(7, 1, ADR(ResRec.Res1), Str.Length(ResRec.Res1));
	Window.DirectWrite(7, 2, ADR(ResRec.Res2), Str.Length(ResRec.Res2));
END WrResultaat;


(*
===================================================================================
		Deel 2: Partijverloop en gegenereerde zetten
===================================================================================
*)


PROCEDURE WrPartijVerloop();
VAR P         : Partij.PartijReport;
	Zptr      : Partij.PartijZetStrPtr;
	x         : Partij.HeleZetNummer;
	Xpos, Ypos: RelCoord;
BEGIN
	P:=Partij.PartijToStr();
	Window.PutOnTop(Win.PartijVerloop);
	Window.TextColor(Win.PartijVerloopFG);
	Window.TextBackground(Win.PartijVerloopBG);
	Window.Clear();
	IF P.ErZijnZetten THEN
		FOR x:=1 TO P.AantalZetten DO  (* eigenlijk tegen de regels *)
			Window.DirectWrite(RelCoord(1 + (1 + SIZE(Partij.HeleZetStr)) * ((x-1) DIV VertZetten)),
			                   RelCoord(1 + (x-1) MOD VertZetten),
		    	               ADR(P.PartijZetten^[x]), Str.Length(P.PartijZetten^[x]));
		END;
		IF P.Vooruit.ErIsVooruit THEN
			Window.TextColor(White);
			Window.TextBackground(Blue);
			x:=P.Vooruit.Start;
			Zptr:=ADR(P.PartijZetten^[x]);
			IncAddr(Zptr, 5);
			Xpos:=RelCoord(6 + (1 + SIZE(Partij.HeleZetStr)) * ((x-1) DIV VertZetten));
			Ypos:=RelCoord(1 + (x-1) MOD VertZetten);
			IF P.Vooruit.Halverwege THEN
				INC(Xpos, 8);
				IncAddr(Zptr, 8);
			END;
			Window.DirectWrite(Xpos, Ypos, Zptr, 7);
		END;
	END;
END WrPartijVerloop;


PROCEDURE WrGenZet_1(Max: Partij.HeleZetNummer; W: Window.WinType);
VAR P: Partij.GenReport;
(*	Zptr: Partij.PartijZetStrPtr;*)
	x   : Partij.HeleZetNummer;
BEGIN
	P:=Partij.GenToStr(Max);
	(* N.B. de putontop hierna is noodzakelijk omdat VM een Window.Use(CacheWin)
	   doet. @@@@@@@@@@@@@@@@@@*)
	Window.PutOnTop(W);
	Window.Clear();
	FOR x:=1 TO P.AantalZetten DO  (* eigenlijk tegen de regels *)
		Window.DirectWrite(RelCoord(1 + (1 + SIZE(Partij.HeleZetStr)) * ((x-1) DIV VertZetten)),
		                   RelCoord(1 + (x-1) MOD VertZetten),
		                   ADR(P.GenZetten^[x]), Str.Length(P.GenZetten^[x]));
	END;
END WrGenZet_1;


PROCEDURE WrGenZet();
BEGIN
	WrGenZet_1(2*VertZetten, Win.GenZet);
END WrGenZet;


PROCEDURE WrBesteZetten();
BEGIN
	WrGenZet_1(8, Win.BesteZetten);
END WrBesteZetten;


	
PROCEDURE SetDeskTop(Nr: SHORTCARD; On: BOOLEAN);
BEGIN
	DeskTop[Nr]:=On;
	IF NOT On THEN
		CASE Nr OF
		|	1: Window.Hide(Win.Bord);
		       Window.Hide(Win.BordAchtergrond);
		|	2: Window.Hide(Win.AanZet);
		|	3: Window.Hide(Win.Resultaat);
		|	4: Window.Hide(Win.PartijVerloop);
		|	5: Window.Hide(Win.BesteZetten);
		END;
	END;
	WrDeskTop();
END SetDeskTop;



PROCEDURE WrDeskTop();
VAR x: CARDINAL;
BEGIN
	IF DeskTop[1] THEN WrBord();          END;
	IF DeskTop[2] THEN WrAanZet();        END;
	IF DeskTop[3] THEN WrResultaat();     END;
	IF DeskTop[4] THEN WrPartijVerloop(); END;
	IF DeskTop[5] THEN WrBesteZetten();   END;
END WrDeskTop;

VAR x: SHORTCARD;
BEGIN
	InzBord();
	FOR x:=1 TO 5 DO
		DeskTop[x]:=TRUE;
	END;
END Display.
