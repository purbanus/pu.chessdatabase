IMPLEMENTATION MODULE Dbs;


IMPORT Str, Window, IO;
IMPORT VM, Win, Key;
FROM AlgDef IMPORT Wit, Zwart;


CONST
	VMillegaal      = 0FFH;
	VMremise        = 000H;
	VMschaak        = 080H;
	VerliesOffset   = 080H;
	Oktanten        = 8;


(*---------- Konversietabellen ---------------------*)
TYPE
	Oktant          = SHORTCARD [1..Oktanten];         (* Oktantnummer *)
	Oktant_0        = SHORTCARD [0..Oktanten];         (* Oktantnummer inclusief 0 om fouten op te vangen *)
	OktTabelType    = ARRAY Veld OF Oktant;            (* Oktantentabel *)
	TrfTabelType    = ARRAY Oktant, Veld OF VM.Veld;   (* Transformatietabel *)
	TrfWKtype       = ARRAY VM.Veld OF VM.WKveld;      (* Speciale behandeling van WK tbv VM *)
	CvtWKtype       = ARRAY VM.WKveld  OF Dbs.Veld;    (* Konv. VM naar Zgen *)
	CvtStukType     = ARRAY VM.Veld    OF Dbs.Veld;    (* Konv. VM naar Zgen *)
	

CONST

	(*==============================================================================================================
	 * Konversie WK notatie van VM naar Zgen
	 *==============================================================================================================*)
	CvtWK = CvtWKtype(
		00H,01H,02H,03H,
			11H,12H,13H,
				22H,23H,
					33H);

	(*==============================================================================================================
	 * Konversie stuk (niet-WK) notatie van VM naar Zgen
	 *==============================================================================================================*)	
	 CvtStuk = CvtStukType(
		00H,01H,02H,03H,04H,05H,06H,07H,
		10H,11H,12H,13H,14H,15H,16H,17H,
		20H,21H,22H,23H,24H,25H,26H,27H,
		30H,31H,32H,33H,34H,35H,36H,37H,
		40H,41H,42H,43H,44H,45H,46H,47H,
		50H,51H,52H,53H,54H,55H,56H,57H,
		60H,61H,62H,63H,64H,65H,66H,67H,
		70H,71H,72H,73H,74H,75H,76H,77H);

	(*==============================================================================================================
	 * Oktantentabel. Deze wordt gebruikt om te kijken in welk oktant een stuk (inz. de witte koning) zich bevindt.
	 * 0 = foutkode, daar wordt in Cardinaliseer() op getest.
	 *==============================================================================================================*)
	OktTabel = OktTabelType(
		1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
        8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
        8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
        8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
        7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
        7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
        7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
        6,6,6,6,5,5,5,5);

	(*========================================================================================
	 * Transformatietabel voor WK. Nadat WK is getransformeerd naar het juiste oktant,
	 * moet hij nog naar de speciale VM-kodering (0..9) worden gebracht. Dat gebeurt hiermee
	 * 10 = foutkode, wordt in VM op getest.
	 *========================================================================================*)
	TrfWK = TrfWKtype(
		 0, 1, 2, 3,10,10,10,10,
		10, 4, 5, 6,10,10,10,10,
		10,10, 7, 8,10,10,10,10,
		10,10,10, 9,10,10,10,10,
		10,10,10,10,10,10,10,10,
		10,10,10,10,10,10,10,10,
		10,10,10,10,10,10,10,10,
		10,10,10,10,10,10,10,10);


VAR
	TrfTabel	: TrfTabelType;
	DbsNaam		: ARRAY[0..64] OF CHAR;

VAR
	Rpt         : ReportArray;
	RptTeller   : CARDINAL;
	RptFreq     : CARDINAL;
	RptProc     : ReportProc;


(*
===========================================================================
		Deel 1: Rapportage
===========================================================================
*)


(*------------ Tellers leegmaken -------------------------*)
PROCEDURE ClearTellers();
VAR x: 0;
BEGIN
	FOR x:=MIN(ResType) TO MAX(ResType) DO
		Rpt[x]:=0;
	END;
	RptTeller:=0;
END ClearTellers;


(*------------- Tellerstand uitlezen ---------------------*)
PROCEDURE GetTellers(): ReportArray;
BEGIN
	RETURN(Rpt);
END GetTellers;


(*---------- Instellen Rapportage ---------------*)
PROCEDURE SetReport(Freq: CARDINAL; R: ReportProc);
BEGIN
	RptFreq:=Freq;
	RptTeller:=0;
	RptProc:=R;
END SetReport;


(*-------- Bijwerken tellers ---------------------------------*)
PROCEDURE UpdateTellers(R: ResType);
BEGIN
	IF RptProc # NULLPROC THEN
		INC(Rpt[R]);
		INC(RptTeller);
		IF RptTeller >= RptFreq THEN
			RptTeller:=0;
			RptProc(Rpt);
		END;
	END;
END UpdateTellers;


(*
===========================================================================
		Deel 2: Konversie
===========================================================================
*)


(*-------- Stelling van BSZ-formaat naar VM-formaat ------*)
PROCEDURE Cardinaliseer(S: Stelling): VM.Stelling;
VAR Okt: Oktant_0;
	VMS: VM.Stelling;
	c  : CHAR;
BEGIN
	Okt:=OktTabel[S.WK];
	IF Okt=0 THEN
		Win.Message('Foutief oktant in Dbs.Cardinaliseer', 'Het programma wordt gestopt');
		c:=Key.GetKey();
		HALT();
	END;
	VMS.WK:=TrfWK[TrfTabel[Okt, S.WK]];
	VMS.ZK:=TrfTabel[Okt, S.ZK];
	VMS.s3:=TrfTabel[Okt, S.s3];
	VMS.s4:=TrfTabel[Okt, S.s4];
	VMS.AanZet:=S.AanZet;
	RETURN(VMS);
END Cardinaliseer;	
	
	
(*
============================================================================
		Deel 3: Lezen en schrijven van database records
============================================================================	
*)


(*----------- Schrijven -----------------*)
PROCEDURE Put(S: Stelling);
VAR VMS  : VM.Stelling;
	VMrec: VM.DbsRec;
BEGIN
	VMS:=Cardinaliseer(S);
	CASE S.Resultaat OF
	|	Illegaal: VMrec:=VMillegaal;
	|	Remise	: IF S.Schaak THEN VMrec:=VMschaak; ELSE VMrec:=VMremise; END;
	|	Gewonnen: VMrec:=S.Aantal;
	|	Verloren: VMrec:=S.Aantal + VerliesOffset;
	END;
	UpdateTellers(S.Resultaat);
	VM.Put(VMS, VMrec);
END Put;


(*----------- Lezen -----------------*)
PROCEDURE Get(VAR S: Stelling);
VAR VMS  : VM.Stelling;
	VMrec: VM.DbsRec;
BEGIN
	VMS:=Cardinaliseer(S);
	VMrec:=VM.Get(VMS);
	S.Schaak:=FALSE;
	IF VMrec = VMillegaal THEN
		S.Resultaat:=Illegaal; S.Aantal:=0;
	ELSIF VMrec = VMremise THEN
		S.Resultaat:=Remise  ; S.Aantal:=0;
	ELSIF VMrec = VMschaak THEN
		S.Resultaat:=Remise  ; S.Aantal:=0; S.Schaak:=TRUE;
	ELSIF VMrec < VerliesOffset THEN
		S.Resultaat:=Gewonnen; S.Aantal:=VMrec;
	ELSE
		S.Resultaat:=Verloren; S.Aantal:=VMrec - VerliesOffset;
	END;
END Get;


(*----------- Lezen zonder cardinaliseren -------*)
PROCEDURE GetDirect(VMS: VM.Stelling; VAR S: Stelling);
VAR VMrec: VM.DbsRec;
BEGIN
	VMrec:=VM.Get(VMS);
	S.Schaak:=FALSE;
	IF VMrec = VMillegaal THEN
		S.Resultaat:=Illegaal; S.Aantal:=0;
	ELSIF VMrec = VMremise THEN
		S.Resultaat:=Remise  ; S.Aantal:=0;
	ELSIF VMrec = VMschaak THEN
		S.Resultaat:=Remise  ; S.Aantal:=0; S.Schaak:=TRUE;
	ELSIF VMrec < VerliesOffset THEN
		S.Resultaat:=Gewonnen; S.Aantal:=VMrec;
	ELSE
		S.Resultaat:=Verloren; S.Aantal:=VMrec - VerliesOffset;
	END;
END GetDirect;


(*----------- Vrijgeven record ------------*)
PROCEDURE FreeRecord(S: Stelling);
VAR VMS: VM.Stelling;
BEGIN
	VMS:=Cardinaliseer(S);
	VM.FreeRecord(VMS);
END FreeRecord;



(*
==========================================================================
		Deel 4: Bewerkingen op de gehele database
==========================================================================
*)


(*------- Naam geven -------------------*)
PROCEDURE Name(Naam: ARRAY OF CHAR);
BEGIN
	Str.Copy(DbsNaam, Naam);
END Name;


(*------- Creeren nieuwe database ------*)
PROCEDURE Create();
VAR c: CHAR;
BEGIN
	IF Str.Length(DbsNaam) = 0 THEN
		Win.Message('Geen naam opgegeven voor database', 'Het programma wordt gestopt');
		c:=Key.GetKey();
		HALT();
	END;
	VM.Create(DbsNaam);
END Create;


(*------- Openen database --------------*)
PROCEDURE Open();
VAR c: CHAR;
BEGIN
	IF Str.Length(DbsNaam) = 0 THEN
		Win.Message('Geen naam opgegeven voor database', 'Het programma wordt gestopt');
		c:=Key.GetKey();
		HALT();
	END;
	VM.Open(DbsNaam);
END Open;


(*------- Sluiten database -------------*)
PROCEDURE Close();
BEGIN
	VM.Close();
END Close;


(*
==========================================================================
		Deel 5: Passes over de gehele database
==========================================================================
*)


(*--------- Pass over stukken 3 en 4 ----------------------------------*)
PROCEDURE Pass34(S: Stelling; VMS: VM.Stelling; P:PassProc);
VAR VMrec: VM.DbsRec;
BEGIN
	VMS.s3:=0;
	WHILE VMS.s3 <= 63 DO
		S.s3:=CvtStuk[VMS.s3];
        VMS.s4:=0;
        WHILE VMS.s4 <= 63 DO
			GetDirect(VMS, S);         (* S.s4 maakt nog niet uit *)
			IF S.Resultaat = Remise THEN
       			S.s4:=CvtStuk[VMS.s4]; (* nu wel *)
				P(S);
       		END;
       		INC(VMS.s4);
		END;
		INC(VMS.s3);
	END;
	DEC(VMS.s3);
	DEC(VMS.s4);
	VM.FreeRecord(VMS);
END Pass34;


(*--------- Pass over de remisestellingen met wit aan zet -------------*)
PROCEDURE Wpass(P: PassProc);
VAR S  : Stelling;
	VMS: VM.Stelling;
	WK : VM.WKveld;
	ZK : VM.Veld;
BEGIN
	VMS.AanZet := Wit;
	S.AanZet   := Wit;
	FOR ZK:=0 TO 63 DO
		VMS.ZK:=ZK;
		S.ZK:=CvtStuk[ZK];
		FOR WK:=0 TO 9 DO
			VMS.WK:=WK;
			S.WK:=CvtWK[WK];
			Pass34(S, VMS, P);
		END;
	END;
END Wpass;


(*--------- Pass over de remisestellingen met zwart aan zet -------------*)
PROCEDURE Zpass(P: PassProc);
VAR S  : Stelling;
	VMS: VM.Stelling;
	WK : VM.WKveld;
	ZK : VM.Veld;
BEGIN
	VMS.AanZet := Zwart;
	S.AanZet   := Zwart;
	FOR WK:=0 TO 9 DO
		VMS.WK:=WK;
		S.WK:=CvtWK[WK];
		FOR ZK:=0 TO 63 DO
			VMS.ZK:=ZK;
			S.ZK:=CvtStuk[ZK];
			Pass34(S, VMS, P);
		END;
	END;
END Zpass;



(*@@@@@ hier kun je nog besparen met een funktie GetRaw *)
(*--------- Pass over alle stellingen -------------*)
(************************** buiten gebruik
PROCEDURE WenZpass(P: PassProc);
VAR S  : Stelling;
	VMS: VM.Stelling;
	WK : VM.WKveld;
	ZK, s3, s4: VM.Veld;
BEGIN
	FOR WK:=0 TO 9 DO
		S.WK:=CvtWK[WK];
		FOR ZK:=0 TO 63 DO
			S.ZK:=CvtStuk[ZK];
    		FOR s3:=0 TO 63 DO
    			S.s3:=CvtStuk[s3];
        		FOR s4:=0 TO 63 DO
        			S.s4:=CvtStuk[s4];
					S.AanZet:=Wit;
        			Get(S);
        			IF S.Resultaat # Illegaal THEN
        				P(S);
        			END;
					S.AanZet:=Zwart;
        			Get(S);
        			IF S.Resultaat # Illegaal THEN
	        			P(S);
	        		END;
				END;
			END;
			FreeRecord(S);
		END;
	END;
END WenZpass;
************************)

(*--------- Pass over alle stellingen -------------*)
PROCEDURE WenZpass(P: PassProc);
VAR S  : Stelling;
	VMS: VM.Stelling;
BEGIN
	VMS.WK:=0;
	WHILE VMS.WK <= 9 DO
		S.WK:=CvtWK[VMS.WK];
		VMS.ZK:=0;
		WHILE VMS.ZK <= 63 DO
			S.ZK:=CvtStuk[VMS.ZK];
			VMS.s3:=0;
			WHILE VMS.s3 <= 63 DO
    			S.s3:=CvtStuk[VMS.s3];
    			VMS.s4:=0;
    			WHILE VMS.s4 <= 63 DO
        			S.s4:=CvtStuk[VMS.s4];
        			(*wit*)
					VMS.AanZet:=Wit;
        			GetDirect(VMS, S);
					S.AanZet:=Wit;
       				P(S);
       				(*zwart*)
        			VMS.AanZet:=Zwart;
        			GetDirect(VMS, S);
					S.AanZet:=Zwart;
        			P(S);
	        		INC(VMS.s4);
				END;
				INC(VMS.s3);
			END;
			INC(VMS.ZK);
		END;
		INC(VMS.WK);
	END;
END WenZpass;


PROCEDURE Pass(T: PassType; P: PassProc);
BEGIN
	Open();
	Window.PutOnTop(Win.CacheWin);
	Window.Clear();
	CASE T OF
	|	MarkeerWit  : Wpass(P);
	|	MarkeerZwart: Zpass(P);
	|	WitEnZwart  : WenZpass(P);
	END;
	Close();
END Pass;

	

(*===========================================================================================
 * De transformatietabel
 *
 * Een veld is een vector (lijn, rij). Voor elk oktant is een formule waarmee
 * het veld van de WK in het hoofdoktant afgebeeld wordt.
 * De algemene formule is x' = Ax + T, waarbij
 * x  = te transformeren veld
 * A  = matrix voor rotatie en/of spiegeling
 * T  = een translatievector, nodig omdat de natuurlijke schaakcoordinaten niet de
 *      oorsprong in het middelpunt van het bord hebben
 * x' = het resultaatveld
 *
 * Voor elk oktant heb je dus nodig een matrix (Matrixtabel) en een translatievector
 *
 * De transformatietabel (TrfTabel), die hier wordt opgebouwd geeft voor elk oktant
 * een bordvol velden waarin staat wat het getransformeerde veld wordt.
 *==========================================================================================*)

TYPE
	Vector = ARRAY[0..1] OF INTEGER;
	Matrix = ARRAY [0..1] OF Vector;
	MatrixTabelType = ARRAY Oktant OF Matrix;
	VectorTabelType = ARRAY Oktant OF Vector;
	
CONST
	MatrixTabel = MatrixTabelType(
		Matrix( Vector( 1, 0), Vector( 0, 1)),
		Matrix( Vector(-1, 0), Vector( 0, 1)),
		Matrix( Vector( 0, 1), Vector(-1, 0)),
		Matrix( Vector( 0,-1), Vector(-1, 0)),
		Matrix( Vector(-1, 0), Vector( 0,-1)),
		Matrix( Vector( 1, 0), Vector( 0,-1)),
		Matrix( Vector( 0,-1), Vector( 1, 0)),
		Matrix( Vector( 0, 1), Vector( 1, 0)));
									
	TranslatieTabel = VectorTabelType(
		Vector( 0, 0),
		Vector( 7, 0),
		Vector( 0, 7),
		Vector( 7, 7),
		Vector( 7, 7),
		Vector( 0, 7),
		Vector( 7, 0),
		Vector( 0, 0));
									
PROCEDURE CreateTrfTabel();
VAR O		:	Oktant;
	x, y	: INTEGER;
	Vres	: Vector;
	OudVeld	: Veld;
	NewVeld	: VM.Veld;
	
			PROCEDURE MatrixVerm(M: Matrix; V: Vector): Vector;
			VAR Vres: Vector;
			BEGIN
				Vres[0]:=M[0, 0]*V[0] + M[0, 1]*V[1];
				Vres[1]:=M[1, 0]*V[0] + M[1, 1]*V[1];
				RETURN(Vres);
			END MatrixVerm;
			
			PROCEDURE VectorOptel(V1, V2: Vector): Vector;
			VAR Vres: Vector;
			BEGIN
				Vres[0]:=V1[0] + V2[0];
				Vres[1]:=V1[1] + V2[1];
				RETURN(Vres);
			END VectorOptel;
			
BEGIN
	FOR O:=1 TO Oktanten DO
		FOR y:=0 TO 7 DO
			FOR x:=0 TO 7 DO
				Vres[0]:=x;
				Vres[1]:=y;
				Vres:=MatrixVerm(MatrixTabel[O], Vres);
				Vres:=VectorOptel(Vres, TranslatieTabel[O]);
				OudVeld:=Veld(x + 16*y);
				NewVeld:=VM.Veld(Vres[0] + 8*Vres[1]);
				TrfTabel[O, OudVeld]:=NewVeld;
			END;
		END;
	END;
END CreateTrfTabel;

	
BEGIN
	DbsNaam:=DftDbsNaam;
	CreateTrfTabel();
	RptFreq:=DftRptFreq;
	RptProc:=NULLPROC;
	ClearTellers();
END Dbs.