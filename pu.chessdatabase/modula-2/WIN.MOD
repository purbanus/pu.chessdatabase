(*@@@@@@@@@@@@@@ de procs moeten naar mylib *)

IMPLEMENTATION MODULE Win;

IMPORT Window, Lib, IO, Str;

FROM Key IMPORT
     Esc, GetKey;

FROM Window IMPORT
     WinDef, WinType, FullScreen,
     SingleFrame, DoubleFrame,
     CenterUpperTitle;



PROCEDURE Max(a, b: INTEGER): INTEGER;
BEGIN
	IF a>b THEN RETURN(a) ELSE RETURN(b) END;
END Max;

(*
*******************************************************************************
*   Procedure: SetupWindows
*   Doel     : De diverse windows worden gedefinieerd. De grootte wordt vast-
*              gesteld en tevens de kleur.
*******************************************************************************
*)
PROCEDURE SetupWindows();
BEGIN
	(*-------------------------------------  x1  y1  x2  y2  FG               BG               CrsOn  WrpOn  Hidd   FrmOn  FrameType    FrameFG          FrameBG -------*)
	CacheWin           := Window.Open(WinDef( 0,  0, 20, 17, CacheFG        , CacheBG        , FALSE, FALSE, TRUE , TRUE , SingleFrame, CacheFG        , CacheBG        ));
    BouwWin            := Window.Open(WinDef(21,  0, 70,  8, BouwFG         , BouwBG         , FALSE, FALSE, TRUE , TRUE , SingleFrame, BouwFG         , BouwBG         ));
	Bord               := Window.Open(WinDef( 1,  1, 18, 10, BordFG         , BordBG         , FALSE, FALSE, TRUE , TRUE , DoubleFrame, BordFG         , BordBG         ));
	BordAchtergrond    := Window.Open(WinDef( 0,  1, 19, 10, BordFG         , BordBG         , FALSE, FALSE, TRUE , FALSE, SingleFrame, BordFG         , BordBG         ));
	AanZet             := Window.Open(WinDef( 0, 11, 19, 11, AanZetFG       , AanZetBG       , FALSE, FALSE, TRUE , FALSE, SingleFrame, AanZetFG       , AanZetBG       ));
	Resultaat          := Window.Open(WinDef( 0, 12, 19, 13, ResultaatFG    , ResultaatBG    , FALSE, FALSE, TRUE , FALSE, SingleFrame, ResultaatFG    , ResultaatBG    ));
	PartijVerloop      := Window.Open(WinDef(34,  1, 79, 24, PartijVerloopFG, PartijVerloopBG, FALSE, FALSE, TRUE , TRUE , SingleFrame, PartijVerloopFG, PartijVerloopBG));
	GenZet             := Window.Open(WinDef(34,  1, 79, 24, GenZetFG       , GenZetBG       , FALSE, FALSE, TRUE , TRUE , SingleFrame, GenZetFG       , GenZetBG       ));
	BesteZetten        := Window.Open(WinDef( 0, 15, 19, 24, BesteZettenFG  , BesteZettenBG  , FALSE, FALSE, TRUE , TRUE , SingleFrame, BesteZettenFG  , BesteZettenBG  ));
	Status             := Window.Open(WinDef( 0, 18, 79, 25, StatusFG       , StatusBG       , FALSE, FALSE, TRUE , TRUE , SingleFrame, StatusFG       , StatusBG       ));

    Window.SetTitle(CacheWin        , ' Cache '              , CenterUpperTitle);
	Window.SetTitle(BouwWin         , ' Stellingen '         , CenterUpperTitle);
	Window.SetTitle(PartijVerloop   , ' Partijverloop '      , CenterUpperTitle);
	Window.SetTitle(GenZet          , ' Gegenereerde zetten ', CenterUpperTitle);
	Window.SetTitle(BesteZetten     , ' Beste 8 '            , CenterUpperTitle);

END SetupWindows;


(*
*********************************************************************************
* Procedures om een tweeregelige boodschap op het scherm te zetten
*********************************************************************************
*)
VAR MessageOnScreen: BOOLEAN;
    MessageWin: WinType;

PROCEDURE OpenMessageWin(Msg1, Msg2: ARRAY OF CHAR);
VAR c   : CHAR;
    Wdef: WinDef;
    Len : CARDINAL;
    Ysiz: CARDINAL;
BEGIN
    IF Str.Length(Msg2)>0 THEN
    	Ysiz:=3;
    ELSE
    	Ysiz:=2;
    END;
    Wdef:=WinDef(0, 10, 0, 10,
                 MsgFG, MsgBG,
                 FALSE, FALSE, FALSE, TRUE,
                 SingleFrame, MsgFG, MsgBG);
    Len:=2 + Max(Str.Length(Msg1), Str.Length(Msg2));
    Wdef.X1:=40-(Len DIV 2);
    Wdef.X2:=40+(Len DIV 2);
    Wdef.Y2:=Wdef.Y1+Ysiz;
    MessageWin:=Window.Open(Wdef);
    MessageOnScreen:=TRUE;
END OpenMessageWin;

PROCEDURE CloseMessage();
BEGIN
    IF MessageOnScreen THEN
        Window.Close(MessageWin);
        MessageOnScreen:=FALSE;
    END;
END CloseMessage;

PROCEDURE Message(Msg1, Msg2: ARRAY OF CHAR);
BEGIN
    IF NOT MessageOnScreen THEN
        OpenMessageWin(Msg1, Msg2);
    END;
    IF Str.Length(Msg1)>0 THEN
        Window.GotoXY(1, 1);
        IO.WrStr(Msg1);
    END;
    IF Str.Length(Msg2)>0 THEN
        Window.GotoXY(1, 2);
        IO.WrStr(Msg2);
    END;
END Message;

(*
********************************************************************************
*   Procedure: ErrMsg
*   Doel     : Toon een foutboodschap, loei de sirene en wacht tot de gebruiker
*              een toets indrukt.
********************************************************************************
*)
PROCEDURE ErrMsg(Msg: ARRAY OF CHAR);
VAR c: CHAR;
BEGIN
    Message(Msg, 'Druk een toets in');
    REPEAT
        Lib.Sound(500);
        Lib.Delay(20);
        Lib.NoSound();
        Lib.Delay(100);
    UNTIL IO.KeyPressed();
    Lib.NoSound();
    c:=GetKey();
    CloseMessage();
END ErrMsg;

(*
********************************************************************************
*   Function: GetJN  (tijdelijk)
*   Doel    : Stel een vraag en haal een ja of nee antwoord van het toetsenbord.
*             Er wordt niet gewacht op Enter. Tikt de gebruiker iets anders in dan
*             ja of nee dan wordt geduldig gewacht tot het wel zo is.
*             Wordt de ESCAPE toets ingedrukt dan wordt #27 teruggegeven als functie-
*             waarde.
********************************************************************************
*)
PROCEDURE GetJN(Prompt: ARRAY OF CHAR): CHAR;
TYPE
    CharSet = SET OF CHAR;
VAR
    c   : CHAR;
BEGIN
    Message(Prompt, '');
    REPEAT
        c:=CAP(GetKey());
    UNTIL c IN CharSet{'J', 'N', Esc};
    CloseMessage();
    RETURN(c);
END GetJN;


BEGIN
    Window.PutOnTop(FullScreen);
    Window.Clear();
    SetupWindows();
    MessageOnScreen:=FALSE;
END Win.