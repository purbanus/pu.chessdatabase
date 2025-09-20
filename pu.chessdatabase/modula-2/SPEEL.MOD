MODULE Speel;

(*@@@@@@@@@ getstukinfo naar gen? *)


IMPORT IO, Str, Window;
IMPORT Mnu, Entry, Win;
IMPORT Partij, Display;
FROM Lib IMPORT IncAddr;
FROM AlgDef IMPORT Wit, Zwart;
FROM Dbs IMPORT Stelling, Close;
(*@@@ verboden*) FROM Gen IMPORT AsciiVeld, AsciiToVeld;

FROM Entry IMPORT
	 Types, EMode;
	
FROM Mnu IMPORT
     MenuOptRec, OptNbr, MenuType, MenuFunc,
     Menu, NULLMENU,
     Create, SetExitKeys, SetCheck, Delete,
     GetCurMenu, GetCurOpt, GetAntOpties, ExecMenu
     ;

FROM Key IMPORT
	Null, Esc, Enter,
	F3,
	AltA, AltB, AltD, AltG, AltN, AltP, AltR, AltS, AltT, AltV, AltZ,
	GetKey;
	
CONST
    AntHoofdOpties = 8;
    AntDesktopOpties = 5;

    EindeKey       = F3;


TYPE
    HoofdOptiesArray   = ARRAY[1..AntHoofdOpties] OF MenuOptRec;
    DesktopOptiesArray = ARRAY[1..AntDesktopOpties] OF MenuOptRec;


VAR
    HoofdOpties      : HoofdOptiesArray;
    DesktopOpties    : DesktopOptiesArray;

    Exit             : ARRAY[0..10] OF CHAR;

    HoofdMenu        : Menu;
    DeskTopMenu      : Menu;

VAR
	CurStelling: Stelling;
	


(*
====================================================================================
		Deel 1: Invoeren
====================================================================================
*)


(*------- Kontroleer een ASCII veld ----------------------*)
PROCEDURE ChkVeld(V: Entry.Veld; Buf: Entry.BufPtr): BOOLEAN;
TYPE CharSet = SET OF CHAR;
BEGIN
	IF (CAP(Buf^[0]) IN CharSet{'A'..'H'}) AND (Buf^[1] IN CharSet{'1'..'8'}) THEN
		RETURN(TRUE);
	ELSE
		Entry.ErrMsg(V, 'Ongeldig veld');
		RETURN(FALSE);
	END;
END ChkVeld;


(*-------- Voer een stelling in ---------------------*)
PROCEDURE EnterStelling(VAR S: Stelling): CHAR;
VAR AscBuf: ARRAY SHORTCARD[1..4] OF AsciiVeld;
	KlrBuf: CHAR;
	D     : Entry.Descriptor;
	VeldV : ARRAY SHORTCARD[1..4] OF Entry.Veld;
	KleurV: Entry.Veld;
	c     : CHAR;
	x     : SHORTCARD;
BEGIN
	FOR x:=1 TO 4 DO
		AscBuf[x]:='';
	END;
	KlrBuf:='';
	D:=Entry.Create(30, 9, 5, 'Stelling');
	VeldV[1]:=Entry.AddField(D, 'Witte Koning' , '__' , ADR(AscBuf[1]), NZstring);
	VeldV[2]:=Entry.AddField(D, 'Witte Dame'   , '__' , ADR(AscBuf[3]), NZstring); (* opgepast: de volgorde is anders dan in de stelling *)
	VeldV[3]:=Entry.AddField(D, 'Zwarte Koning', '__' , ADR(AscBuf[2]), NZstring);
	VeldV[4]:=Entry.AddField(D, 'Zwarte Toren' , '__' , ADR(AscBuf[4]), NZstring);
	KleurV  :=Entry.AddField(D, 'Aan zet (w/z)', '_'  , ADR(KlrBuf   ), NZstring);
	FOR x:=1 TO 4 DO
		Entry.SetEval(VeldV[x], ChkVeld, Immed);
	END;
	Entry.SetValues(KleurV, 'wzWZ');
	LOOP
		c:=Entry.DataEntry(D, VeldV[1]);
		IF c = Esc THEN
			EXIT;
		END;
		c:=Null;
		FOR x:=1 TO 4 DO
			S.Velden[x]:=AsciiToVeld(AscBuf[x]);
		END;
		IF CAP(KlrBuf) = 'W' THEN
			S.AanZet := Wit;
		ELSE
			S.AanZet := Zwart;
		END;
		IF NOT Partij.IsLegaleStelling(S) THEN
			Entry.ErrMsg(VeldV[1], 'Illegale stelling');
		ELSE
			EXIT;
		END;
	END;
	Entry.Delete(D);
	RETURN(c);
END EnterStelling;


(*------- Zet ASCII (d2d4) om naar Van/Naar --------------*)
PROCEDURE AsciiToVanNaar(Buf: ARRAY OF CHAR): Partij.VanNaarType;
VAR VeldStr: AsciiVeld;
	VN     : Partij.VanNaarType;
BEGIN
	VeldStr[0]:=Buf[0];
	VeldStr[1]:=Buf[1];
	VN.Van:=AsciiToVeld(VeldStr);
	VeldStr[0]:=Buf[2];
	VeldStr[1]:=Buf[3];
	VN.Naar:=AsciiToVeld(VeldStr);
	RETURN(VN);
END AsciiToVanNaar;


(*------- Kontroleer twee ASCII velden (d2d4) --------------------*)
PROCEDURE Chk2Velden(V: Entry.Veld; Buf: Entry.BufPtr): BOOLEAN;
VAR B : Entry.BufPtr;
	VN: Partij.VanNaarType;
BEGIN
	B:=Buf;
	IF NOT ChkVeld(V, B) THEN
		RETURN(FALSE);
	END;
	IncAddr(B, 2);
	IF NOT ChkVeld(V, B) THEN
		RETURN(FALSE);
	END;
	VN:=AsciiToVanNaar(Buf^);
	IF NOT Partij.IsLegaal(VN) THEN
		Entry.ErrMsg(V, 'Illegale zet');
		RETURN(FALSE);
	END;
	RETURN(TRUE);
END Chk2Velden;


(*------- Voer een zet in -----------------------------*)
PROCEDURE EnterZet(VAR VN : Partij.VanNaarType): CHAR;
VAR Buf     : ARRAY[0..5] OF CHAR;
	D       : Entry.Descriptor;
	ZetV    : Entry.Veld;
	c       : CHAR;
	S       : Stelling;
	Proloog : ARRAY[0..6] OF CHAR;
	Prompt  : ARRAY[0..11] OF CHAR;
BEGIN
	Buf:='';
	S:=Partij.GetStand();
	CASE S.AanZet OF
	|	Wit  : Proloog:='Wit   ';
	|	Zwart: Proloog:='Zwart ';
	END;
	Str.Concat(Prompt, Proloog, Partij.CurZetNrToStr());
	Str.Concat(Prompt, Prompt, '.');
	D:=Entry.Create(0, 12, 1, '');
	ZetV  :=Entry.AddField(D, Prompt, '__-__' , ADR(Buf), String);
	Entry.SetEval(ZetV, Chk2Velden, Immed);
	c:=Entry.DataEntry(D, ZetV);
	IF c # Esc THEN
		c :=Null;
		VN:=AsciiToVanNaar(Buf);
	END;
	Entry.Delete(D);
	RETURN(c);
END EnterZet;


(*
=====================================================================================
		Deel 2: Menu
=====================================================================================
*)


(*------- Verander een desktop-optie --------------------*)
PROCEDURE ChgDeskTop(): CHAR;
VAR CurOpt: SHORTCARD;
	CheckOn: BOOLEAN;
BEGIN
	CurOpt:= Mnu.GetCurOpt(DeskTopMenu);
	CheckOn:=NOT Mnu.GetCheck(DeskTopMenu, CurOpt);
	Display.SetDeskTop(CurOpt, CheckOn);
	Mnu.SetCheck(DeskTopMenu, CurOpt, CheckOn);
	RETURN(Null);
END ChgDeskTop;


(*------- Kontrole of een partij begonnen is --------------*)
PROCEDURE ChkBegonnen(): BOOLEAN;
BEGIN
	IF NOT Partij.IsBegonnen() THEN
		Win.ErrMsg('Er is nog geen stelling opgezet');
		RETURN(FALSE);
	END;
	RETURN(TRUE);
END ChkBegonnen;


(*------- Kontrole of een partij ten einde is --------------*)
PROCEDURE ChkEinde(): BOOLEAN;
BEGIN
	IF Partij.IsEindePartij() # Partij.NogNiet THEN
		(********* Win.ErrMsg('De partij is ten einde'); *)
		RETURN(FALSE);
	END;
	RETURN(TRUE);
END ChkEinde;


(*------- Vraag om een zet en voer hem uit --------------*)
PROCEDURE Zet(): CHAR;
VAR c: CHAR;
	VN: Partij.VanNaarType;
BEGIN
	IF NOT (ChkEinde() AND ChkBegonnen()) THEN
		c:=Esc;
	ELSE
		c:=EnterZet(VN);
		IF c # Esc THEN
			IF NOT Partij.Zet(VN) THEN
				Win.ErrMsg('Er ging iets mis in Partij.Zet');
				c:=Esc;
			ELSE
				Display.WrDeskTop();
			END;
		END;
	END;
	RETURN(c);
END Zet;


(*------- Laat de computer een zet bedenken --------------*)
PROCEDURE Bedenk(): CHAR;
VAR c: CHAR;
BEGIN
	IF NOT (ChkBegonnen() AND ChkEinde()) THEN
		c:=Esc;
	ELSIF NOT Partij.Bedenk() THEN
		Win.ErrMsg('Er ging iets mis in Partij.Bedenk');
		c:=Esc;
	ELSE
		c:=Null;
		Display.WrDeskTop();
	END;
	RETURN(c);
END Bedenk;


(*------- OmBeurten (afwisselend mens - computer) --------------*)
PROCEDURE Omstebeurt(): CHAR;
VAR c: CHAR;
BEGIN
	LOOP
		c:=Zet();
		IF c=Esc THEN
			EXIT;
		END;
		c:=Bedenk();
		IF c=Esc THEN
			EXIT;
		END;
	END;
	RETURN(c);
END Omstebeurt;


(*------- Ga terug in de partij --------------*)
PROCEDURE Terug(): CHAR;
BEGIN
	IF ChkBegonnen() THEN
		IF Partij.ZetTerug() THEN
			Display.WrDeskTop();
		END;
	END;
	RETURN(Null);
END Terug;


(*------- Ga vooruit in de partij --------------*)
PROCEDURE Vooruit(): CHAR;
BEGIN
	IF ChkBegonnen() THEN
		IF Partij.ZetVooruit() THEN
			Display.WrDeskTop();
		END;
	END;
	RETURN(Null);
END Vooruit;


(*------- Begin een nieuwe partij --------------*)
PROCEDURE NewGame(): CHAR;
VAR c: CHAR;
	S: Stelling;
BEGIN
	c:=EnterStelling(S);
	IF c # Esc THEN
		Partij.NewGame(S);
		Display.WrDeskTop();
	END;
	RETURN(c);
END NewGame;


(*------- Laat de gegenereerde zetten zien --------------*)
PROCEDURE Genereer(): CHAR;
VAR c: CHAR;
BEGIN
	c:=Null;
	IF ChkBegonnen() THEN
		Display.WrGenZet();
		c:=GetKey();
		Window.Hide(Win.GenZet);
		IF c = Enter THEN
			c:=Null;
		END;
	END;
	RETURN(c);
END Genereer;


(*------- Opzetten menus ----------------------------*)
PROCEDURE SetupMenus();
VAR c:CHAR;
    x: INTEGER;
CONST
    DUMMY = MenuFunc(NULLPROC); (* opgepast want DUMMY is niet NULLPROC!!! *)
BEGIN                           (*                        ----             *)
    Exit[0]:=EindeKey;
    Exit[1]:=Null;

    (*-------------*)
    (* Desktopmenu *)
    (*-------------*)
    DesktopOpties:=DesktopOptiesArray
    (   (*********  Titel           Msg  Key  Alt  Fkey  Function  NxtMenu   Enab  HasChk Check ******)
        MenuOptRec('Bord'         , NIL, 'B', AltB, 00C, DUMMY,    NULLMENU, TRUE, TRUE,  TRUE),
        MenuOptRec('Aan zet'      , NIL, 'A', AltA, 00C, DUMMY,    NULLMENU, TRUE, TRUE,  TRUE),
        MenuOptRec('Resultaat'    , NIL, 'R', AltR, 00C, DUMMY,    NULLMENU, TRUE, TRUE,  TRUE),
        MenuOptRec('Partijverloop', NIL, 'P', AltP, 00C, DUMMY,    NULLMENU, TRUE, TRUE,  TRUE),
        MenuOptRec('Beste zetten' , NIL, 'Z', AltZ, 00C, DUMMY,    NULLMENU, TRUE, TRUE,  TRUE)
    );
    FOR x:=1 TO AntDesktopOpties DO
    	DesktopOpties[x].NextMenu:=NULLMENU;
    	DesktopOpties[x].Function:=ChgDeskTop;
    END;
    DeskTopMenu:=Create('', Pop, 56, 1, DesktopOpties);

    (*-----------*)
    (* Hoofdmenu *)
    (*-----------*)
    HoofdOpties:=HoofdOptiesArray
    (   (*********  Titel      Msg  Key  Alt  Fkey  Function  NxtMenu   Enab  HasChk Check ******)
        MenuOptRec('Speel'   , NIL, 'S', AltS, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE),
        MenuOptRec('Zet'     , NIL, 'Z', AltZ, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE),
        MenuOptRec('Bedenk'  , NIL, 'B', AltB, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE),
        MenuOptRec('Terug'   , NIL, 'T', AltT, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE),
        MenuOptRec('Vooruit' , NIL, 'V', AltV, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE),
        MenuOptRec('Newgame' , NIL, 'N', AltN, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE),
        MenuOptRec('Genereer', NIL, 'G', AltG, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE),
        MenuOptRec('Desktop' , NIL, 'D', AltD, 00C, DUMMY,    NULLMENU, TRUE, FALSE, FALSE)
    );
    HoofdOpties[1].Function := Omstebeurt;
    HoofdOpties[2].Function := Zet;
    HoofdOpties[3].Function := Bedenk;
    HoofdOpties[4].Function := Terug;
    HoofdOpties[5].Function := Vooruit;
    HoofdOpties[6].Function := NewGame;
    HoofdOpties[7].Function := Genereer;
    FOR x:=1 TO AntHoofdOpties DO
    	HoofdOpties[x].NextMenu:=NULLMENU;
    END;
    HoofdOpties[8].NextMenu  := DeskTopMenu;
    HoofdMenu:=Create('', Bar, 0, 24, HoofdOpties);
    SetExitKeys(HoofdMenu, Exit);
END SetupMenus;


VAR c: CHAR;
BEGIN
	SetupMenus();
	c:=NewGame();
	IF c # Esc THEN
		c:=Mnu.ExecMenu(HoofdMenu);
	END;
	Close();
	Window.PutOnTop(Window.FullScreen);
	Window.Clear();
END Speel.