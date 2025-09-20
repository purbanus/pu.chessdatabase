IMPLEMENTATION MODULE Partij;
(*@@@@@@@@@@@@ Wanneer is een partij ten einde *)
(* voor GenZ moet je een array alloceren op de heap *)
(*@@@@@@@@@@@@ Fout: als je de comileert met volatile aan, dan wordt
                     elke zet die je ingeeft als fout aangemerkt *)


IMPORT Str;
IMPORT Win;
IMPORT Dbs, Gen;
FROM AlgDef IMPORT Wit, Zwart, StukNummer;
FROM Lib IMPORT IncAddr;


TYPE
	PlyRec = RECORD
		S        : Dbs.Stelling;
		Einde    : EindeType;
		ZetNr    : HeleZetNummer;
		VN       : VanNaarType;
		Schaak   : BOOLEAN;
	END;
	PartijRec = RECORD
		Desc            : ARRAY[0..64] OF CHAR;
		CurPly          : PlyNummer;
		LastPly         : PlyNummer;
		IsBegonnen      : BOOLEAN;
	END;
	
CONST
	NullPly = PlyRec(Dbs.Stelling(TRUE,0,0,0,0,FALSE,Dbs.Illegaal,0,FALSE), NogNiet ,1 ,
	                 VanNaarType(0FH, 0FH), FALSE);
VAR
	Plies    : ARRAY PlyNummer OF PlyRec;
	CurPartij: PartijRec;
	PSZ      : PartijZetStr;
	

(*================================================================================
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
	Plies[3]     Ke3Dh1Kg6Ta8 waz     3           ...

	Met andere woorden, in een plyrecord zit de stelling waaruit zetten
	gegenereerd worden, plus de zet die uiteindelijk gedaan is. Het zetnummer
	is gewoon het nummer dat afgedrukt moet worden.
	
===================================================================================
*)

(*
=====================================================================================
		Deel 1: Newgame
=====================================================================================
*)

(*------------ Initialiseren partij -----------------*)
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


(*------- Kontrole op legale stelling -----*)
PROCEDURE IsLegaleStelling(S: Dbs.Stelling): BOOLEAN;
BEGIN
	Dbs.Get(S);
	IF S.Resultaat = Dbs.Illegaal THEN
		RETURN(FALSE);
	ELSE
		RETURN(TRUE);
	END;
END IsLegaleStelling;


(*------- Kijk of een stelling het einde van een partij is ------------*)
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


(*------------ Een nieuwe partij beginnen ------------*)
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


(*
=====================================================================================
		Deel 2: Info over de huidige stelling
=====================================================================================
*)


(*------- Is de partij begonnen -------------------*)
PROCEDURE IsBegonnen(): BOOLEAN;
BEGIN
	RETURN(CurPartij.IsBegonnen);
END IsBegonnen;


(*------- Kontrole op het einde van de partij -----*)
PROCEDURE IsEindePartij(): EindeType;
BEGIN
	RETURN(Plies[CurPartij.CurPly].Einde);
END IsEindePartij;


(*
=====================================================================================
		Deel 3: Hulproutines om te zetten
=====================================================================================
*)

(*------------- Van/Naar bepalen uit twee stellingen -------*)
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
		

(*----------- Stelling Bepalen uit Van/Naar -------------------*)
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


(*-------- Kontrole op legale zet -----------------*)
PROCEDURE IsLegaal(VN: VanNaarType): BOOLEAN;
VAR S: Dbs.Stelling;
BEGIN
	RETURN(VanNaarToStelling(VN, S));
END IsLegaal;


(*
=====================================================================================
		Deel 4: Zetten
=====================================================================================
*)

(*------------ VooruitZetten --------------------------*)
PROCEDURE ZetVooruit(): BOOLEAN;
BEGIN
	IF IsBegonnen() AND (CurPartij.CurPly < CurPartij.LastPly) THEN
		INC(CurPartij.CurPly);
		RETURN(TRUE);
	END;
	RETURN(FALSE);
END ZetVooruit;


(*------------ TerugZetten ----------------------------*)
PROCEDURE ZetTerug(): BOOLEAN;
BEGIN
	IF IsBegonnen() AND (CurPartij.CurPly > 0) THEN
		DEC(CurPartij.CurPly);
		RETURN(TRUE);
	END;
	RETURN(FALSE);
END ZetTerug;


(*------------ Voer een zet uit -----------------------*)
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
	

(*------------ Voer een zet uit nav een stelling -----------------------*)
PROCEDURE ZetStelling(S: Dbs.Stelling): BOOLEAN;
VAR VN: VanNaarType;
BEGIN
	VN:=StellingToVanNaar(Plies[CurPartij.CurPly].S, S);
	RETURN(Zet(VN));
END ZetStelling;


(*------------ Bedenk zelf een zet -----------------------*)
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
	

(*
=====================================================================================
		Deel 5: Rapportage
=====================================================================================
*)

(*--------- Wat staat er op een veld -------------------*)
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


(*---------- Ply omzetten in string (Kd1-d2+) --------------*)
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


(*-------- Huidige ply omzetten in string ------------------------------*)
PROCEDURE CurPlyToStr(): PlyStr;
BEGIN
	IF CurPartij.CurPly = 0 THEN
		RETURN('');
	ELSE
		RETURN(PlyToStr(Plies[CurPartij.CurPly-1]));
	END;
END CurPlyToStr;


(*-------- Resultaat omzetten in string ------------------------------*)
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
		END;
	END;
	RETURN(RR);
END ResToStr;


(*-------- Zetnummer omzetten in string --------------------------------*)
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


(*------- Huidige zetnummer omzetten in string -----------------------*)
PROCEDURE CurZetNrToStr(): ZetNrStr;
BEGIN
	RETURN(ZetNrToStr(Plies[CurPartij.CurPly].ZetNr));
END CurZetNrToStr;


(*-------- Hele zet omzetten naar string ( 55. Ke1-e2+  Ke7-d8+) -------*)
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


(*-------- Partij omzetten naar strings ---------------------------------*)
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


(*-------- Gegenereerde zet omzetten naar string ( 55. Ke1-e2+ (+100) -------*)
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



(*-------- Gegenereerde zetten omzetten naar strings ---------------------------------*)
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

	
(*----------- geef huidige stelling -------------------*)
PROCEDURE GetStand(): Dbs.Stelling; (*@@@@@@@ of meer informatie? *)
BEGIN
	RETURN(Plies[CurPartij.CurPly].S);
END GetStand;


(*-------- Geef info over stuk ------------------------*)
PROCEDURE GetStukInfo(S: Dbs.Stelling; StukNr: StukNummer): StukInfoRec;
BEGIN
	RETURN(Gen.GetStukInfo(S, StukNr));
END GetStukInfo;


BEGIN
	Dbs.Open();
	InzPartij();
END Partij.