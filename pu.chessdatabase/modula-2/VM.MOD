(*$M Dbs *) (* Code segment *)
IMPLEMENTATION MODULE VM;

IMPORT IO, FIO, Str, Storage, Window;
IMPORT Win, Key;

(*
******************************************************************************
Systeem: Chess Data base voor 4 stukken
Module : VM - Virtual memory manager
Doel   : Implementeren van een virtual memory systeem voor de chess
         data base. Alle lees- en schrijfbewerkingen op de data base
         gaan door VM, die kijkt of het record nog in zijn cache zit.
         Een record is een byte, en de database bestaat uit 5,12 Mrecords.
******************************************************************************
*)

CONST
    RecordSize    = 1;                  (* Een byte per record *)
    CacheSize     = 30;                 (* Aantal pagina's     *)
    CacheSizeDiv2 = CacheSize DIV 2;    (* Voor rapportagescherm *)
    PageSize      = 4096;               (* Bytes per page *)


TYPE
    CardinaalGetal = LONGCARD; (* Adres van een record *)
    Page = ARRAY[0..PageSize-1] OF BYTE;
    PagePointer = POINTER TO Page;
    PDpointer   = POINTER TO PageDescriptor;

    Lokatie = (OpSchijf, InRAM);
    PageDescriptor = RECORD
        Waar       : Lokatie;
        CacheNummer: CARDINAL; (*@@@@ ook pointer?? **)
        SchijfAdres: CardinaalGetal;
    END;
    CacheEntry = RECORD
        PDptr      : PDpointer;
        PagePtr    : PagePointer;
        Vuil       : BOOLEAN;
        Generatie  : LONGCARD;
    END;

VAR
    PDT   : ARRAY WKveld, Veld, BOOLEAN OF PageDescriptor;
    Cache : ARRAY[1..CacheSize] OF CacheEntry;
    DataBase : FIO.File;
    GeneratieTeller: LONGCARD;


(*
********************************************************************************
Procedures: InzPDT
            InzCache
Doel      : Initialisatie van de gegevensstrukturen
********************************************************************************
*)

(*--------- Initialisatie Page Descriptor Table ----------------------*)
PROCEDURE (*$N*) InzPDT();
VAR WK    : WKveld;
    ZK    : Veld;
    AanZet: BOOLEAN;
    Adres : LONGCARD;
BEGIN
    Adres:=0;
    FOR WK:=0 TO 9 DO
        FOR ZK:=0 TO 63 DO
            FOR AanZet:=MIN(BOOLEAN) TO MAX(BOOLEAN) DO
                WITH PDT[WK,ZK, AanZet] DO
                    Waar       :=OpSchijf;
                    SchijfAdres:=Adres;
                    CacheNummer:=MAX(CARDINAL);
                END;
                INC(Adres, PageSize);
            END;
        END;
    END;
END InzPDT;


(*------ Initialisatie cache-----------------------------*)
PROCEDURE (*$N*) InzCache();
VAR x: CARDINAL;
BEGIN
    WITH Cache[1] DO
        FOR x:=0 TO PageSize-1 DO
            PagePtr^[x]:=0;
        END;
    END;
    FOR x:=1 TO CacheSize DO
        WITH Cache[x] DO
            PDptr:=NIL;
            Vuil:=FALSE;
            Generatie:=0;
            IF x # 1 THEN
                PagePtr^:=Cache[1].PagePtr^;
            END;
        END;
    END;
END InzCache;

(*
******************************************************************************
Procedures: IOresult
            ChkFile
Doel      : Rapportage omtrent de schijf.
            IOresult geeft het resultaat van de laatste schijfoperatie. Voorlopig
            wordt de standaard funktie in FIO aangeroepen.
            ChkFile kijkt of een bestand bestaat op schijf.
******************************************************************************
*)

(*----------- Rapporteer eventuele fouten -------------------*)
PROCEDURE (*$N*) IOresult(): CARDINAL;
VAR Msg: ARRAY[0..80] OF CHAR;
BEGIN
    RETURN(FIO.IOresult());
(**********
    IORes:=FIO.IOresult();
    CASE IORes OF
    |    0: ; (* alles in orde *)
    |    1: ; (* bestand niet gevonden*)
         (*@@@@@@@@ Uitzoeken wat de andere kodes betekenen @@@@@@@*)
    ELSE
    	Msg:=Str.Concat('Fout bij lezen van bestand ', Naam);
    	Win.Message(Msg,'Druk een toets in');
    	Key.GetKey();
    	Win.CloseMessage();
    END;
******)
END IOresult;


(*--------- Kijk of een bestand bestaat ------------------*)
PROCEDURE (*$N*) ChkFile(Naam: ARRAY OF CHAR): CARDINAL;
VAR IOres: CARDINAL;
    F : FIO.File;
BEGIN
    F:=FIO.Open(Naam);
    IOres:=IOresult();
    IF IOres = 0 THEN
        FIO.Close(F);
    END;
    RETURN(IOres);
END ChkFile;

(*
********************************************************************************
Procecdures: Report
			 GetFreeCacheEntry
             PageOut
             PageIn
Doel       : Deze routines onderhouden de Page Descriptor Table en de cache
			 - Report print info over de cache
             - GetFreeCacheEntry kijkt welke pagina het eerst in aanmerking komt om
               herbruikt te worden
             - PageOut schrijft een eventuele vuile pagina naar de schijf
             - PageIn haalt een nieuwe pagina op naar de cache, en zorgt eventueel
               dat een vuile pagina eerst weg wordt geschreven.

N.B.         PageIn en PageOut kunnen vrijelijk worden aangeroepen door andere
             routines binnen het moduul.
             Van de datastrukturen mogen alleen Vuil en Generatie door de andere
             routines gebruikt worden en eventueel veranderd.
***********************************************************************************
*)
(*------- Print info ---------------------------------*)
TYPE
	RepWKtype = ARRAY WKveld, [0..1] OF CHAR;
	RepZKtype = ARRAY Veld  , [0..1] OF CHAR;
	RepAZtype = ARRAY BOOLEAN OF CHAR;

CONST
	(*------ Witte koning ------------*)
	RepWK = RepWKtype(
		'a1', 'b1', 'c1', 'd1', 'b2', 'c2', 'd2', 'c3', 'd3', 'd4');
	
	(*------ Zwarte koning -----------*)
	RepZK = RepZKtype(
		'a1', 'b1', 'c1', 'd1', 'e1', 'f1', 'g1', 'h1',
		'a2', 'b2', 'c2', 'd2', 'e2', 'f2', 'g2', 'h2',
		'a3', 'b3', 'c3', 'd3', 'e3', 'f3', 'g3', 'h3',
		'a4', 'b4', 'c4', 'd4', 'e4', 'f4', 'g4', 'h4',
		'a5', 'b5', 'c5', 'd5', 'e5', 'f5', 'g5', 'h5',
		'a6', 'b6', 'c6', 'd6', 'e6', 'f6', 'g6', 'h6',
		'a7', 'b7', 'c7', 'd7', 'e7', 'f7', 'g7', 'h7',
		'a8', 'b8', 'c8', 'd8', 'e8', 'f8', 'g8', 'h8');
	
	(*------ Aan zet -----------------*)
	RepAZ = RepAZtype(
		'W', 'Z');

PROCEDURE (*$N*) Report(PD: PDpointer; S: Stelling);
VAR CacheNr: CARDINAL;
BEGIN
	CacheNr:=PD^.CacheNummer;
	Window.Use(Win.CacheWin);
	Window.GotoXY(2 + 10 * ((CacheNr-1) DIV CacheSizeDiv2), 2 + (CacheNr-1) MOD CacheSizeDiv2);
	IO.WrCard(CacheNr, 2);
	IO.WrStr (' ');
	IO.WrStr (RepWK[S.WK]);
	IO.WrStr (RepZK[S.ZK]);
	IO.WrChar(RepAZ[S.AanZet]);
END Report;


(*------- Haal een vrij cachenummer ------------------*)
PROCEDURE (*$N*) GetFreeCacheEntry(): CARDINAL;
VAR x: CARDINAL;
    C: CacheEntry;
    LaagsteGeneratie  , LaagsteSchoneGeneratie  : LONGCARD;
    LaagsteGeneratieNr, LaagsteSchoneGeneratieNr: CARDINAL;
BEGIN
    (*---- laagste generatienummers -------*)
    LaagsteGeneratie        :=MAX(LONGCARD);
    LaagsteSchoneGeneratie  :=MAX(LONGCARD);
    LaagsteGeneratieNr      :=MAX(CARDINAL);
    LaagsteSchoneGeneratieNr:=MAX(CARDINAL);
    FOR x:=1 TO CacheSize DO
        C:=Cache[x];
        IF C.Generatie < LaagsteGeneratie THEN
            LaagsteGeneratie  :=C.Generatie;
            LaagsteGeneratieNr:=x;
        END;
        IF (NOT C.Vuil) AND (C.Generatie < LaagsteSchoneGeneratie) THEN
            LaagsteSchoneGeneratie  :=C.Generatie;
            LaagsteSchoneGeneratieNr:=x;
        END;
    END;

    (*----- bij voorkeur schone cache entry nemen ------*)
    IF LaagsteSchoneGeneratieNr <> MAX(CARDINAL) THEN
        RETURN(LaagsteSchoneGeneratieNr);
    ELSE
        RETURN(LaagsteGeneratieNr);
    END;
END GetFreeCacheEntry;


(*------------ Pagina schrijven naar de schijf ------*)
PROCEDURE (*$N*) PageOut(PD: PDpointer);
BEGIN
    IF (PD # NIL) AND (Cache[PD^.CacheNummer].Vuil) THEN
        Window.GotoXY(1, 1);
        FIO.Seek(DataBase, PD^.SchijfAdres);
        FIO.WrBin(DataBase, Cache[PD^.CacheNummer].PagePtr^, PageSize);
        Cache[PD^.CacheNummer].Vuil := FALSE;
    END;
END PageOut;


(*----------- Pagina ophalen van de schijf ---------*)
PROCEDURE (*$N*) PageIn(PD: PDpointer; S: Stelling);
VAR Msg      : ARRAY[0..80] OF CHAR;
    DummyOK  : BOOLEAN;
    Aantal   : CARDINAL;
    AantalStr: ARRAY[0..10] OF CHAR;
    c        : CHAR;
    CacheNr  : CARDINAL;
BEGIN
    (* test boolean S.AanZet *)
	IF (SHORTCARD(S.AanZet) # 0) AND (SHORTCARD(S.AanZet) # 1) THEN
        Win.Message('Niet-boolean ontvangen in VM.PageIn', 'Het programma wordt afgebroken');
        c:=Key.GetKey();
        Win.CloseMessage();
        HALT();
	END;

    IF PD^.Waar = OpSchijf THEN
        PD^.CacheNummer:=GetFreeCacheEntry();
    END;
    Report(PD, S);
    WITH Cache[PD^.CacheNummer] DO

        (*-------- Update oude page descriptor -------*)
        PageOut(PDptr);
        IF PDptr # NIL THEN
            PDptr^.Waar := OpSchijf;
            PDptr^.CacheNummer:=MAX(CARDINAL);
        END;

        (*-------- Ophalen nieuwe pagina -------------*)
        FIO.Seek(DataBase, PD^.SchijfAdres);
        Aantal:=FIO.RdBin(DataBase, PagePtr^, PageSize);
        IF Aantal # PageSize THEN
            Str.CardToStr(LONGCARD(Aantal), AantalStr, 10, DummyOK);
            Str.Concat(Msg, 'Ernstig: VM.GetPage heeft ', AantalStr);
            Str.Concat(Msg, Msg, ' records gelezen.');
            Win.Message(Msg, 'Druk een toets in');
            c:=Key.GetKey();
            Win.CloseMessage();
        END;

        (*-------- Update cache ----------------------*)
        PDptr:=PD;
        Vuil:=FALSE;
        Generatie:=GeneratieTeller;
        INC(GeneratieTeller);

        (*-------- Update Page descriptor ------------*)
        PD^.Waar:=InRAM;
    END;
END PageIn;


(*------- Haal pagina op uit de cache ---------*)
PROCEDURE (*$N*) GetPage(S: Stelling; MaakVuil: BOOLEAN): PagePointer;
VAR PD: PDpointer;
BEGIN
	
    PD:=ADR(PDT[S.WK, S.ZK, S.AanZet]);
    IF PD^.Waar # InRAM THEN
        PageIn(PD, S);
    END;
    IF MaakVuil THEN
        Cache[PD^.CacheNummer].Vuil:=TRUE;
    END;
    RETURN(Cache[PD^.CacheNummer].PagePtr);
END GetPage;


(*------------ Ophalen database record --------------*)
PROCEDURE (*$N*) Get(S: Stelling): DbsRec;
VAR P: PagePointer;
    c: CHAR;
BEGIN
    IF (S.WK > 9) OR (S.ZK > 63) OR (S.s3 > 63) OR (S.s4 > 63) THEN
        Win.Message('Niet-cardinaalstelling ontvangen in VM.Get', 'Druk een toets in');
        c:=Key.GetKey();
        Win.CloseMessage();
        RETURN(0FFH);
    END;
    P:=GetPage(S, FALSE);
    RETURN(P^[(CARDINAL(S.s3) << 6) + CARDINAL(S.s4)]);
END Get;


(*--------- Wegschrijven database record -----------*)
PROCEDURE (*$N*) Put(S: Stelling; Rec: DbsRec);
VAR P: PagePointer;
    c: CHAR;
BEGIN
    IF (S.WK > 9) OR (S.ZK > 63) OR (S.s3 > 63) OR (S.s4 > 63) THEN
        Win.Message('Niet-cardinaalstelling ontvangen in VM.Put', 'Druk een toets in');
        c:=Key.GetKey();
        Win.CloseMessage();
        c:=Key.GetKey();
        RETURN;
    END;
    P:=GetPage(S, TRUE);
    P^[(CARDINAL(S.s3) << 6) + CARDINAL(S.s4)] := Rec;
END Put;


(*-------- Cache entry vrijmaken --------------------*)
PROCEDURE (*$N*) FreeRecord(S: Stelling);
VAR PD: PDpointer;
    c : CHAR;
BEGIN
    IF (S.WK > 9) OR (S.ZK > 63) OR (S.s3 > 63) OR (S.s4 > 63) THEN
        Win.Message('Niet-cardinaalstelling ontvangen in VM.FreeRecord', 'Druk een toets in');
        c:=Key.GetKey();
        Win.CloseMessage();
        RETURN;
    END;
    PD:=ADR(PDT[S.WK, S.ZK, S.AanZet]);
    IF PD^.Waar = InRAM THEN
        PageOut(PD);
        Cache[PD^.CacheNummer].Generatie := 0;
    END;
END FreeRecord;


(*-------- Hele cache vrijmaken ---------------------*)
PROCEDURE (*$N*) Flush();
VAR x: CARDINAL;
BEGIN
    FOR x:=1 TO CacheSize DO
        WITH Cache[x] DO
            PageOut(PDptr);
            Generatie:=0;
        END;
    END;
    GeneratieTeller:=1;
END Flush;


(*--------- Sluiten van de database --------------*)
PROCEDURE (*$N*) Close();
BEGIN
	IF DataBase # MAX(CARDINAL) THEN
		Flush();
		FIO.Close(DataBase);
		DataBase:=MAX(CARDINAL);
	END;
END Close;


(*----------- Openen van een database -------------*)
PROCEDURE (*$N*) Open(Naam: ARRAY OF CHAR);
VAR Msg: ARRAY[0..80] OF CHAR;
	c  : CHAR;
BEGIN
    Close();
    IF ChkFile(Naam) # 0 THEN
        Str.Copy(Msg, Naam);
        Str.Append(Msg, ' kon niet gevonden worden.');
        Win.Message(Msg, 'Het programma wordt afgebroken');
        c:=Key.GetKey();
        Win.CloseMessage();
        HALT();
    END;
    DataBase:=FIO.Open(Naam);
    InzPDT();   (*@@@@@ later frequentie hiervan verlagen *)
    InzCache();
END Open;


(*---------- Leegmaken cq creeren van de database -------*)
PROCEDURE (*$N*) Create(Naam: ARRAY OF CHAR);
VAR S		: Stelling;
    PD		: PageDescriptor;
    WK		: WKveld;
    ZK		: Veld;
    AanZet	: BOOLEAN;
    DummyF	: FIO.File;
BEGIN
    Close();
    Window.PutOnTop(Win.CacheWin);
    Window.Clear();
    IF ChkFile(Naam) <> 0 THEN
        DummyF:=FIO.Create(Naam);
        FIO.Close(DummyF);
    END;
    Open(Naam);
    FOR WK:=0 TO 9 DO
        FOR ZK:=0 TO 63 DO
            FOR AanZet:=MIN(BOOLEAN) TO MAX(BOOLEAN) DO
            	S.WK:=WK;
            	S.ZK:=ZK;
            	S.AanZet:=AanZet;
                PD:=PDT[WK, ZK, AanZet];
                PD.CacheNummer:=1;
                Cache[1].Vuil:=TRUE;
                Report(ADR(PD), S);
                PageOut(ADR(PD));
            END;
        END;
    END;
END Create;


(*---------- Maken (alloceren) cache -------------*)
PROCEDURE (*$N*) CreateCache();
VAR x: CARDINAL;
	c: CHAR;
BEGIN
    FOR x:=1 TO CacheSize DO
        WITH Cache[x] DO
            IF NOT Storage.Available(SIZE(Page)) THEN
                Win.Message('Niet genoeg ruimte voor de cache!', 'Het programma wordt afgebroken');
                c:=Key.GetKey();
                Win.CloseMessage();
                HALT();
            END;
            Storage.ALLOCATE(PagePtr, SIZE(Page));
        END;
    END;
END CreateCache;


BEGIN
    FIO.IOcheck:=FALSE;
    GeneratieTeller:=1;
    CreateCache();
    DataBase:=MAX(CARDINAL);
END VM.
