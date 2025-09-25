package pu.chessdatabase.dal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.springframework.stereotype.Service;

import pu.chessdatabase.util.Range;

//import org.apache.commons.lang3.Range;

/**
 * ******************************************************************************
Systeem: Chess Data base voor 4 stukken
Module : VM - Virtual memory manager
Doel   : Implementeren van een virtual memory systeem voor de chess
         data base. Alle lees- en schrijfbewerkingen op de data base
         gaan door VM, die kijkt of het record nog in zijn cache zit.
         Een record is een byte, en de database bestaat uit 5,12 Mrecords.
******************************************************************************
 */
@Service
public class VM
{
static final int CacheSize     = 30;                 // Aantal pagina"s
@SuppressWarnings( "unused" )
private static final int CacheSizeDiv2 = CacheSize / 2;      // Voor rapportagescherm
static final int PageSize      = 4096;               // Bytes per page

Range WKveld = new Range( 0, 9 );
Range Veld = new Range( 0, 63 );

PageDescriptor[][][] PDT = new PageDescriptor[10][64][2];
CacheEntry [] Cache = new CacheEntry[CacheSize];
File databaseFile;
RandomAccessFile Database = null;
long GeneratieTeller;

public VM()
{
    //FIO.IOcheck:=FALSE;
    GeneratieTeller = 1L;
    CreateCache();
    Database = null;
    databaseFile = null;
}
/**
 * ********************************************************************************
Procedures: InzPDT
            InzCache
Doel      : Initialisatie van de gegevensstrukturen
********************************************************************************
 */
/**
 * FOR WK:=0 TO 9 DO
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
 */
public void InzPDT()
{
long Adres = 0;
for ( int WK = 0; WK < 10; WK++ )
{
	for ( int ZK = 0; ZK < 64; ZK++ )
	{
		for ( int AanZet = 0; AanZet < 2; AanZet++ )
		{
			PageDescriptor pageDescriptor = PageDescriptor.builder()
				.waar( Lokatie.OpSchijf )
				.schijfAdres( Adres )
				.cacheNummer( Integer.MAX_VALUE )
				.build();
			PDT[WK][ZK][AanZet] = pageDescriptor;
			Adres += PageSize;
		}
	}
}
}
/**
 * (*------ Initialisatie cache-----------------------------*)
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
 */
public void InzCache()
{
	// Cache is een array van 30 CacheEntry
	// In een CacheEntry zit een PageDescriptor en een Page, onder meer
	// Er is GEEN cachEntry 0!
	// Er was hier nog een truc voor de performance, namelijk eerst cache[1]" page vullen met nullen
	// en die daarna copieren naar de andere cacheEntries. Die truc laten we hier achterwege
	//for ( CacheEntry cacheEntry : Cache )
	for ( int x = 1; x < CacheSize; x++)
	{
		Cache[x] = CacheEntry.builder()
			.PDPointer( null )
			.PagePointer( new Page() )
			.Vuil( false )
			.Generatie( 0 )
			.build();
		Cache[x].getPagePointer().clearPage();
	}
}
/**
******************************************************************************
Procedures: IOresult
            ChkFile
Doel      : Rapportage omtrent de schijf.
            IOresult geeft het resultaat van de laatste schijfoperatie. Voorlopig
            wordt de standaard funktie in FIO aangeroepen.
            ChkFile kijkt of een bestand bestaat op schijf.
******************************************************************************

(*----------- Rapporteer eventuele fouten -------------------*)
PROCEDURE (*$N*) IOresult(): CARDINAL;
VAR Msg: ARRAY[0..80] OF CHAR;
BEGIN
    RETURN(FIO.IOresult());
*/
// @@NOG IOResult????
/**
 * (*--------- Kijk of een bestand bestaat ------------------*)
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
 */
public void ChkFile( String aFileNaam )
{
	File file = new File( aFileNaam );
	if ( ! file.exists() )
	{
		throw new RuntimeException( "File bestaat niet: " + aFileNaam );
	}
	if ( ! file.canRead() )
	{
		throw new RuntimeException( "Kan file niet lezen: " + aFileNaam );
	}
	if ( ! file.canWrite() )
	{
		throw new RuntimeException( "Kan niet naar file schrijven: " + aFileNaam );
	}
}
/*------ Witte koning ------------*/
@SuppressWarnings( "unused" )
private static final String [] RepWK = {"a1", "b1", "c1", "d1", "b2", "c2", "d2", "c3", "d3", "d4" };

/*------ Zwarte koning -----------*/
@SuppressWarnings( "unused" )
private static final String [] RepZK = {
		"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
		"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
		"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
		"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
		"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
		"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
		"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
		"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
	};

/*------ Aan zet -----------------*/
@SuppressWarnings( "unused" )
private static final String [] RepAZ = { "W", "Z" };

/**
 * ********************************************************************************
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

 * PROCEDURE (*$N*) Report(PD: PDpointer; S: Stelling);
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
 */
// @@NOG Report, wat doen we ermee?
public void Report( PageDescriptor aPageDescriptor, VMStelling aStelling )
{
	// @@NOG effe niks
	/*
	CacheNr:=PD^.CacheNummer;
	Window.Use(Win.CacheWin);
	Window.GotoXY(2 + 10 * ((CacheNr-1) DIV CacheSizeDiv2), 2 + (CacheNr-1) MOD CacheSizeDiv2);
	IO.WrCard(CacheNr, 2);
	IO.WrStr (' ');
	IO.WrStr (RepWK[S.WK]);
	IO.WrStr (RepZK[S.ZK]);
	IO.WrChar(RepAZ[S.AanZet]);
	 */
}

/**
 * (*------- Haal een vrij cachenummer ------------------*)
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
 */
public int GetFreeCacheEntry()
{
    //---- laagste generatienummers -------
    long LaagsteGeneratie        = Long.MAX_VALUE;
    long LaagsteSchoneGeneratie  = Long.MAX_VALUE;
    int LaagsteGeneratieNr      = Integer.MAX_VALUE;
    int LaagsteSchoneGeneratieNr= Integer.MAX_VALUE;
    for ( int x = 1; x < CacheSize; x++ ) 
    {
    	CacheEntry C = Cache[x];
        if ( C.getGeneratie() < LaagsteGeneratie )
        {
            LaagsteGeneratie  = C.getGeneratie();
            LaagsteGeneratieNr = x;
        }
        if ( ! C.isVuil() && ( C.getGeneratie() < LaagsteSchoneGeneratie ) )
        {
            LaagsteSchoneGeneratie  = C.getGeneratie();
            LaagsteSchoneGeneratieNr = x;
        }
    }

    //----- bij voorkeur schone cache entry nemen ------
    if ( LaagsteSchoneGeneratieNr != Integer.MAX_VALUE )
    {
        return LaagsteSchoneGeneratieNr;
    }
    else
    {
    	return LaagsteGeneratieNr;
    }
}

void getRawPageData( PageDescriptor aPageDescriptor )
{
    try
	{
		Database.seek( aPageDescriptor.getSchijfAdres() );
		int Aantal = Database.read( Cache[aPageDescriptor.getCacheNummer()].getPagePointer().getPage(), 0, PageSize );
		if ( Aantal != PageSize )
		{
			throw new RuntimeException( "Ernstig: VM.GetPage heeft " + Aantal + " records gelezen. Dat zouden er " + PageSize + " moeten zijn" );
		}
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}

}
void putRawPageData( PageDescriptor aPageDescriptor )
{
	try
	{
		Database.seek( aPageDescriptor.getSchijfAdres() );
	    Database.write( Cache[aPageDescriptor.getCacheNummer()].getPagePointer().getPage(), 0, PageSize );
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
}

/**
 * (*------------ Pagina schrijven naar de schijf ------*)
PROCEDURE (*$N*) PageOut(PD: PDpointer);
BEGIN
    IF (PD # NIL) AND (Cache[PD^.CacheNummer].Vuil) THEN
        Window.GotoXY(1, 1);
        FIO.Seek(DataBase, PD^.SchijfAdres);
        FIO.WrBin(DataBase, Cache[PD^.CacheNummer].PagePtr^, PageSize);
        Cache[PD^.CacheNummer].Vuil := FALSE;
    END;
END PageOut;
 * @throws IOException 
 */
//*------------ Pagina schrijven naar de schijf ------
void PageOut( PageDescriptor aPageDescriptor )
{
    if ( aPageDescriptor != null && Cache[aPageDescriptor.getCacheNummer()].isVuil() )
    {
    	// Window.GotoXY(1, 1);
        putRawPageData( aPageDescriptor );
        Cache[aPageDescriptor.getCacheNummer()].setVuil( false );
    }
}
/**
 * (*------------ Pagina ophalen van schijf ------*)
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
 */
void PageIn( PageDescriptor aPageDescriptor )
{
    if ( aPageDescriptor.getWaar() == Lokatie.OpSchijf )
    {
    	aPageDescriptor.setCacheNummer( GetFreeCacheEntry() );
    }
    CacheEntry cacheEntry = Cache[aPageDescriptor.getCacheNummer()];
    
    //-------- Update oude page descriptor -------
    PageDescriptor oudePageDescriptor = cacheEntry.getPDPointer();
    PageOut( oudePageDescriptor );
    if ( oudePageDescriptor != null )
    {
        oudePageDescriptor.setWaar( Lokatie.OpSchijf );
        oudePageDescriptor.setCacheNummer( Integer.MAX_VALUE );
    }

    //-------- Ophalen nieuwe pagina -------------
 	getRawPageData( aPageDescriptor );

    //-------- Update cache ----------------------
    cacheEntry.setPDPointer( aPageDescriptor );
    cacheEntry.setVuil( false );
    cacheEntry.setGeneratie( GeneratieTeller );
    GeneratieTeller++;

    //-------- Update Page descriptor ------------
    aPageDescriptor.setWaar( Lokatie.InRAM );
}
/**
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
 */
/**
 *  ------- Haal pagina op uit de cache ---------
 */
Page GetPage( VMStelling aStelling, boolean aMaakVuil )
{
	aStelling.checkStelling();
	PageDescriptor pageDescriptor = PDT[aStelling.getWk()][aStelling.getZk()][aStelling.isAanZet()? 1 : 0];
	if ( pageDescriptor.getWaar() == Lokatie.OpSchijf )
	{
		Report( pageDescriptor, aStelling );
		PageIn( pageDescriptor );
	}
	if ( aMaakVuil )
	{
		Cache[pageDescriptor.getCacheNummer()].setVuil( true );
	}
	Page page = Cache[pageDescriptor.getCacheNummer()].getPagePointer();
	return page;
}
/**
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
 */
/**
 *  ------------ Ophalen database record --------------
 */
public int Get( VMStelling aStelling )
{
	aStelling.checkStelling();
    Page page = GetPage( aStelling, false );
    byte vmRec = page.getPage()[aStelling.getPositionWithinPage()];
    return Byte.toUnsignedInt( vmRec );
}
/**
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
 */
/**
 * --------- Wegschrijven database record -----------
 */
public void Put( VMStelling aStelling, int aDbsRec )
{
	// Dit gebeurt al in GetPage
	// aStelling.checkStelling();
    Page page = GetPage( aStelling, true );
    byte vmRec = (byte)( aDbsRec & 0xff );
    page.getPage()[aStelling.getPositionWithinPage()] = vmRec;
}
/**
**PROCEDURE (*$N*) FreeRecord(S: Stelling);
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
/**
 *  -------- Cache entry vrijmaken --------------------
 */
public void FreeRecord( VMStelling aStelling )
{
	// En na de clear, page en pageDescriptor leegmaken?
	// - PD niet, die is permanent
	aStelling.checkStelling();
	PageDescriptor PD = PDT[aStelling.getWk()][aStelling.getZk()][aStelling.isAanZet() ? 1 : 0];
	if ( PD.getWaar() == Lokatie.InRAM )
	{
		PageOut( PD ); // Checkt of de page vuil is
		Cache[PD.getCacheNummer()].setGeneratie( 0 );
	}
}
/**
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
*/
/**
 * -------- Hele cache vrijmaken ---------------------
 */
public void Flush()
{
	for ( int x = 1; x < CacheSize; x++ )
	{
		if ( Cache[x].getPDPointer() != null && Cache[x].getPDPointer().getCacheNummer() != Integer.MAX_VALUE )
		{
			PageOut( Cache[x].getPDPointer() );
			Cache[x].setGeneratie( 0 );
		}
	}
	GeneratieTeller = 1;
}
/**
PROCEDURE (*$N*) Close();
BEGIN
	IF DataBase # MAX(CARDINAL) THEN
		Flush();
		FIO.Close(DataBase);
		DataBase:=MAX(CARDINAL);
	END;
END Close;
/**
 *  --------- Sluiten van de database --------------
 */
public void Close()
{
	if ( Database != null )
	{
		Flush();
		try
		{
			Database.close();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
		Database = null;
		//databaseFile = null;
	}
}
/**
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
 */
/**
 *  ----------- Openen van een database -------------
 */
public void Open( String aNaam )
{
	Close();
	ChkFile( aNaam ); // Throws RuntimeException-als er iets niet goed is
	try
	{
		/**Zie doc for mode = "rwd" or "rws". "rw" betekent volgens mij dat een update direct naar
		 * schijf wordt geschreven. "rwd" en "rws" cachen dat enigszins.
		 */
		Database = new RandomAccessFile( databaseFile, "rw" ); 
	}
	catch ( FileNotFoundException e )
	{
		throw new RuntimeException( e );
	}
    InzPDT(); //@@@@@ later frequentie hiervan verlagen 
    InzCache();
}
/**
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
 */
/**
 *  ---------- Leegmaken cq creeren van de database -------
 */
void CreateFile( String aNaam )
{
	databaseFile = new File( aNaam );
	if ( ! databaseFile.exists() )
	{
		try
		{
			databaseFile.createNewFile();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
}
public void Create( String aNaam )
{
    PageDescriptor PD;
    Close();
    //Window.PutOnTop(Win.CacheWin);
    //Window.Clear();
    CreateFile( aNaam );
    Open( aNaam );
    for ( int wk = WKveld.getMinimum(); wk < WKveld.getMaximum() + 1; wk++ )
    {
        for ( int zk = Veld.getMinimum(); zk < Veld.getMaximum() + 1; zk++ )
        {
            for ( int AanZet = 0; AanZet < 2; AanZet++ )
            {
            	VMStelling stelling = VMStelling.builder()
            		.wk( wk )
            		.zk( zk )
            		.s3( 0 )
            		.s4( 0 )
            		.aanZet( AanZet == 0 ? false : true )
            		.build();
                PD = PDT[wk][zk][AanZet];
                PD.setCacheNummer( 1 );
                Cache[1].setVuil( true );
                Report( PD, stelling );
                PageOut( PD );
            }
        }
    }
}
void delete()
{
	File file = databaseFile;
	Close();
	if ( file != null )
	{
		file.delete();
	}
	databaseFile = null;
	Database = null;
}
/**
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
 */
/**
 * ---------- Maken (alloceren) cache -------------*)
 */
void CreateCache()
{
	// In Java gebeurt dit vanzelf
}

}
