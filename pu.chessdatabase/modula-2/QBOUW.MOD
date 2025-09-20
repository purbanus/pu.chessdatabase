MODULE Qbouw;

IMPORT Dbs, Bouw, Key, Gen, Win, Window, IO;
FROM Dbs IMPORT ResType, PassType;
FROM AlgDef IMPORT Wit, Zwart;
FROM Lib IMPORT IncAddr;

TYPE
	NotatieType = ARRAY SHORTCARD[0..77H], [0..1] OF CHAR;
	AZtype = ARRAY BOOLEAN,[0..4] OF CHAR;

CONST
	Notatie = NotatieType(
		'a1', 'b1', 'c1', 'd1', 'e1', 'f1', 'g1', 'h1', '??', '??', '??', '??', '??', '??', '??', '??',
		'a2', 'b2', 'c2', 'd2', 'e2', 'f2', 'g2', 'h2', '??', '??', '??', '??', '??', '??', '??', '??',
		'a3', 'b3', 'c3', 'd3', 'e3', 'f3', 'g3', 'h3', '??', '??', '??', '??', '??', '??', '??', '??',
		'a4', 'b4', 'c4', 'd4', 'e4', 'f4', 'g4', 'h4', '??', '??', '??', '??', '??', '??', '??', '??',
		'a5', 'b5', 'c5', 'd5', 'e5', 'f5', 'g5', 'h5', '??', '??', '??', '??', '??', '??', '??', '??',
		'a6', 'b6', 'c6', 'd6', 'e6', 'f6', 'g6', 'h6', '??', '??', '??', '??', '??', '??', '??', '??',
		'a7', 'b7', 'c7', 'd7', 'e7', 'f7', 'g7', 'h7', '??', '??', '??', '??', '??', '??', '??', '??',
		'a8', 'b8', 'c8', 'd8', 'e8', 'f8', 'g8', 'h8');
	AZ = AZtype(
		'(Waz)', '(Zaz)');	

VAR
	c                : CHAR;
	PassNr           : CARDINAL;
	Ver              : ARRAY SHORTCARD OF LONGCARD;
	Wres, Zres       : ARRAY Dbs.ResType, Dbs.AantalZetten OF LONGCARD;
	HoogsteAantal    : SHORTCARD;
	ZomaarEenStelling: Dbs.Stelling;
	
	
(*-------- Print een stelling --------*)
PROCEDURE WrStelling(S: Dbs.Stelling);
BEGIN
	IO.WrStr(Notatie[S.WK]);
	IO.WrStr(Notatie[S.ZK]);
	IO.WrStr(Notatie[S.s3]);
	IO.WrStr(Notatie[S.s4]);
	IO.WrStr(AZ[S.AanZet]);
END WrStelling;


(*------- Loze lus over alle stellingen -------------*)
PROCEDURE DoeNiks(S: Dbs.Stelling);
BEGIN
END DoeNiks;

PROCEDURE Qpass();
BEGIN
	Dbs.Pass(WitEnZwart, DoeNiks);
END Qpass;





(*--------- Test pass 0 ---------------*)
PROCEDURE Qpass_0();
BEGIN
	Bouw.Pass_0();
	Bouw.ReportNewPass('Einde pass 0');
END Qpass_0;



(*------------- Tel verloren stellingen per W-koningsveld ---------*)
PROCEDURE TelVer(S: Dbs.Stelling);
BEGIN
	IF S.Resultaat = Verloren THEN
		INC(Ver[S.WK]);
	END;
END TelVer;
PROCEDURE TelAlleVer();
VAR x: SHORTCARD;
BEGIN
	FOR x:=0 TO 63 DO
		Ver[x]:=0;
	END;
	Dbs.Pass(WitEnZwart, TelVer);
	Window.PutOnTop(Window.FullScreen);
	FOR x:=0 TO 63 DO
		IF Ver[x] # 0 THEN
			IO.WrShtCard(x, 5); IO.WrLngCard(Ver[x], 12); IO.WrLn();
		END;
	END;
END TelAlleVer;


(*------------- Tel resultaten  ---------*)
PROCEDURE Tel_1(S: Dbs.Stelling);
BEGIN
	IF S.AanZet = Wit THEN
		INC(Wres[S.Resultaat, S.Aantal]);
	ELSE
		INC(Zres[S.Resultaat, S.Aantal]);
	END;
END Tel_1;
PROCEDURE Tel();
VAR R: ResType;
	A: SHORTCARD;
	RegelTeller: SHORTCARD;
BEGIN
	FOR R:=MIN(ResType) TO MAX(ResType) DO
		FOR A:=0 TO 40 DO
			Wres[R, A]:=0;
			Zres[R, A]:=0;
		END;
	END;
	Dbs.Pass(WitEnZwart, Tel_1);
	Window.PutOnTop(Window.FullScreen);
	Window.Clear();
	IO.WrStr('Resultaten'); IO.WrLn(); IO.WrLn();
	IO.WrStr('Ill  '); IO.WrLngCard(Wres[Illegaal,0], 8); IO.WrLngCard(Zres[Illegaal,0], 8); IO.WrLn();
	IO.WrStr('Rem  '); IO.WrLngCard(Wres[Remise  ,0], 8); IO.WrLngCard(Zres[Remise  ,0], 8); IO.WrLn();
	RegelTeller:=20;
	LOOP
		FOR A:=1 TO 40 DO
			(* Wit *)
			IO.WrShtCard(A, 5);
			FOR R:=MIN(ResType) TO MAX(ResType) DO
				IO.WrLngCard(Wres[R, A], 8);
			END;
			(* Zwart *)
			IO.WrStr(' | ');
			FOR R:=MIN(ResType) TO MAX(ResType) DO
				IO.WrLngCard(Zres[R, A], 8);
			END;
			IO.WrLn();
			DEC(RegelTeller);
			IF RegelTeller = 0 THEN
				RegelTeller:=20;
				c:=Key.GetKey();
			END;
		END;
		IF c=Key.Esc THEN
			EXIT;
		END;
	END;
END Tel;


(*------------- Tel resultaten van KDK ---------*)
PROCEDURE TelKDK_1(S: Dbs.Stelling);
BEGIN
	IF (S.ZK = S.s4) THEN
		IF S.AanZet = Wit THEN
			INC(Wres[S.Resultaat, S.Aantal]);
		ELSE
			INC(Zres[S.Resultaat, S.Aantal]);
		END;
	END;
END TelKDK_1;
PROCEDURE TelKDK();
VAR R: ResType;
	A: SHORTCARD;
BEGIN
	FOR R:=MIN(Dbs.ResType) TO MAX(Dbs.ResType) DO
		FOR A:=0 TO 20 DO
			Wres[R, A]:=0;
			Zres[R, A]:=0;
		END;
	END;
	Dbs.Pass(WitEnZwart, TelKDK_1);
	Window.PutOnTop(Window.FullScreen);
	Window.Clear();
	IO.WrStr('Resultaten KDK'); IO.WrLn(); IO.WrLn();
	IO.WrStr('Ill  '); IO.WrLngCard(Wres[Illegaal,0], 8); IO.WrLngCard(Zres[Illegaal,0], 8); IO.WrLn();
	IO.WrStr('Rem  '); IO.WrLngCard(Wres[Remise  ,0], 8); IO.WrLngCard(Zres[Remise  ,0], 8); IO.WrLn();
	FOR A:=1 TO 20 DO
		(* Wit *)
		R:=Gewonnen;
		IO.WrShtCard(A, 5);
		IO.WrLngCard(Wres[R, A], 8);
		Wres[R, A]:=Wres[R, A] + Wres[R, A-1];
		IO.WrLngCard(Wres[R, A], 8);
		(* Zwart *)
		IO.WrStr(' | ');
		R:=Verloren;
		IO.WrLngCard(Zres[R, A], 8);
		Zres[R, A]:=Zres[R, A] + Zres[R, A-1];
		IO.WrLngCard(Zres[R, A], 8);
		IO.WrLn();
	END;
END TelKDK;
	

(*------------- Tel resultaten van KKT ---------*)
PROCEDURE TelKKT_1(S: Dbs.Stelling);
BEGIN
	IF (S.WK = S.s3) THEN
		IF S.AanZet = Wit THEN
			INC(Wres[S.Resultaat, S.Aantal]);
		ELSE
			INC(Zres[S.Resultaat, S.Aantal]);
		END;
	END;
END TelKKT_1;
PROCEDURE TelKKT();
VAR R: ResType;
	A: SHORTCARD;
BEGIN
	FOR R:=MIN(Dbs.ResType) TO MAX(Dbs.ResType) DO
		FOR A:=0 TO 20 DO
			Wres[R, A]:=0;
			Zres[R, A]:=0;
		END;
	END;
	Dbs.Pass(WitEnZwart, TelKKT_1);
	Window.PutOnTop(Window.FullScreen);
	Window.Clear();
	IO.WrStr('Resultaten KKT'); IO.WrLn(); IO.WrLn();
	IO.WrStr('Ill  '); IO.WrLngCard(Wres[Illegaal,0], 8); IO.WrLngCard(Zres[Illegaal,0], 8); IO.WrLn();
	IO.WrStr('Rem  '); IO.WrLngCard(Wres[Remise  ,0], 8); IO.WrLngCard(Zres[Remise  ,0], 8); IO.WrLn();
	FOR A:=1 TO 20 DO
		(* Wit *)
		R:=Verloren;
		IO.WrShtCard(A, 5);
		IO.WrLngCard(Wres[R, A], 8);
		Wres[R, A]:=Wres[R, A] + Wres[R, A-1];
		IO.WrLngCard(Wres[R, A], 8);
		(* Zwart *)
		IO.WrStr(' | ');
		R:=Gewonnen;
		IO.WrLngCard(Zres[R, A], 8);
		Zres[R, A]:=Zres[R, A] + Zres[R, A-1];
		IO.WrLngCard(Zres[R, A], 8);
		IO.WrLn();
	END;
END TelKKT;



(*----------- Print KDK stellingen met aantal=11 ---------*)
PROCEDURE PrintKDK11_1(S: Dbs.Stelling);
			PROCEDURE DrukAf();
			BEGIN
				Window.PutOnTop(Window.FullScreen);
				IF S.AanZet = Wit THEN
					IO.WrStr('Wit aan zet   ');
				ELSE
					IO.WrStr('Zwart aan zet ');
				END;
				IO.WrShtCard(S.WK, 5);
				IO.WrShtCard(S.ZK, 5);
				IO.WrShtCard(S.s3, 5);
				IO.WrShtCard(S.s4, 5);
				IO.WrLn();
			END DrukAf;
			
BEGIN
	IF S.ZK = S.s4 THEN
	(* of is dit beter? *)
		S.AanZet:=Wit;
		Dbs.Get(S);
		IF S.Aantal = 11 THEN
			DrukAf();
		END;
		S.AanZet:=Zwart;
		Dbs.Get(S);
		IF S.Aantal = 11 THEN
			DrukAf();
		END;
	END;
END PrintKDK11_1;
PROCEDURE PrintKDK11();
VAR S: Dbs.Stelling;
BEGIN
	Window.PutOnTop(Window.FullScreen);
	IO.WrStr('Alle KDK stellingen met aantal = 11'); IO.WrLn();
	Dbs.Pass(WitEnZwart, PrintKDK11_1);
	Window.PutOnTop(Window.FullScreen);
END PrintKDK11;


(*--------- Kontroleer het markeer-algoritme ------------*)
PROCEDURE ChkMarkeer(S: Dbs.Stelling);
VAR GZ: Gen.GenZrec;
	MinGewonnen, MaxVerloren: Dbs.AantalZetten;
	x : Gen.AantalGzetten;
	OldS: Dbs.Stelling;
BEGIN
	OldS:=S;
	GZ:=Gen.GenZ(S);
	IF GZ.Aantal = 0 THEN
		(* Moet mat zijn *)
		S.Resultaat:=Verloren;
		S.Aantal:=1;
	ELSE
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
		END;
	END;
	(* kontroleer resultaat en aantal *)
	IF (S.Resultaat # OldS.Resultaat) OR (S.Aantal # OldS.Aantal) THEN
		Window.PutOnTop(Window.FullScreen);
		WrStelling(S);
		IO.WrStr(' Oud: ');
		CASE OldS.Resultaat OF
		|	Gewonnen: IO.WrStr('G');
		|	Verloren: IO.WrStr('V');
		ELSE
			IO.WrStr('Fout1 ');
		END;
		IO.WrShtCard(OldS.Aantal, 2);
		IO.WrStr(' Nieuw: ');
		CASE S.Resultaat OF
		|	Illegaal: IO.WrStr('Fout2 ');
		|	Gewonnen: IO.WrStr('G');
		|	Remise  : IO.WrStr('=');
		|	Verloren: IO.WrStr('V');
		END;
		IO.WrShtCard(S.Aantal, 2);
		IO.WrLn();
	END;
END ChkMarkeer;
PROCEDURE Chk_1(S: Dbs.Stelling);
BEGIN
	IF S.WK = S.s3 THEN
		IF (S.Resultaat = Gewonnen) OR (S.Resultaat = Verloren) THEN
			ChkMarkeer(S);
		END;
	END;
END Chk_1;
PROCEDURE ChkMarkeerAlg();
BEGIN
	Window.Use(Window.FullScreen);
	IO.WrStr('Fouten in het markeer-algoritme'); IO.WrLn();
	Dbs.Pass(MarkeerWit  , Chk_1);
	Dbs.Pass(MarkeerZwart, Chk_1);
	Window.PutOnTop(Window.FullScreen);
END ChkMarkeerAlg;

(**********************
PROCEDURE TelHoogste_1(S: Dbs.Stelling);
BEGIN
	S.AanZet:=Wit;
	Dbs.Get(S);
	IF S.Aantal > HoogsteAantal THEN
		HoogsteAantal:=S.Aantal;
	END;
	S.AanZet:=Zwart;
	Dbs.Get(S);
	IF S.Aantal > HoogsteAantal THEN
		HoogsteAantal:=S.Aantal;
	END;
END TelHoogste_1;
PROCEDURE TelHoogste();
VAR S: Dbs.Stelling;
BEGIN
	Window.Use(Window.FullScreen);
	IO.WrStr('Hoogste aantal zetten'); IO.WrLn();
	HoogsteAantal:=0;
	Bouw.Pass(S, TelHoogste_1);
	Window.PutOnTop(Window.FullScreen);
	IO.WrShtCard(HoogsteAantal, 5);
END TelHoogste;
******************)


BEGIN
	Bouw.TelAlles();
(*****************
	PassNr:=18;
	WHILE NOT IO.KeyPressed() DO
		INC(PassNr);
		Bouw.Pass_n(PassNr);
	END;
*****************)
END Qbouw.