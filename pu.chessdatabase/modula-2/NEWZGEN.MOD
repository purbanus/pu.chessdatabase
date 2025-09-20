IMPLEMENTATION MODULE NewZgen;

IMPORT DbsDef;

CONST
	BuitenBordTest  = BITSET(088H);
	Leeg            = 000H;
	BuitenBord      = 0FFH;
	Einde           = 0FFH;
		
TYPE
	WerkVeld        = SHORTCARD;
	VeldType        = SHORTCARD [0..63]; (*@@ moet uit dbs komen *)
	
	(*----- Richtingen voor opbouw van tabellen ----------*)
	
	RichtingNummer  = SHORTCARD [1..8];
	RichtingTabel   = ARRAY RichtingNummer OF WerkVeld;

	(*----- Startpunten voor het genereren van zetten -------*)
	
	StartPuntNummer = SHORTCARD [1..8];
	StartTabel      = ARRAY VeldType OF POINTER TO VeldType;
	StartPunten     = ARRAY RichtingNummer OF POINTER TO StartTabel;
	
	(*------ Stukrecord ----------------*)
	StukRec = RECORD
		Start      : StartPunten;
		AtlStart   : StartPuntNummer;
		Richting   : RichtingTabel;
		AtlRicht   : RichtingNummer;
		Meer       : BOOLEAN;
		Soort      : DbsDef.StukType;
		Kleur      : BOOLEAN;
		Knummer    : DbsDef.StukNummer;
	END;

VAR
    StukTabel : ARRAY DbsDef.StukNummer OF StukRec;
    Bord      : ARRAY Dbs.Veld OF SHORTCARD;
    GenZtabel : ARRAY SHORTCARD[1..MAX(AantalGzetten)] OF Dbs.Stelling;


(*-------- Maak hele bord leeg -------------*)
PROCEDURE ClrBord();
VAR x: SHORTCARD;
BEGIN
	FOR x:=0 TO 77H DO
		Bord[x]:=Leeg;
	END;
END ClrBord;


(*-------- Zet stukken op het bord ------------------------*)
PROCEDURE ZetStukkenOp(S: Dbs.Stelling);
BEGIN
	(* eerst de stukken, dan kunnen ze eventueel uitgeveegd worden door de koningen *)
	Bord[S.s3]:=3;
	Bord[S.s4]:=4;
	Bord[S.WK]:=1;
	Bord[S.ZK]:=2;
END ZetStukkenOp;


(*-------- Haal ze er weer vanaf ------------------------*)
PROCEDURE VerwijderStukken(S: Dbs.Stelling);
BEGIN
	Bord[S.WK]:=Leeg;
	Bord[S.ZK]:=Leeg;
	Bord[S.s3]:=Leeg;
	Bord[S.s4]:=Leeg;
END VerwijderStukken;


(*-------- Kijk of de koningen elkaar schaak geven --------*)
PROCEDURE IsKKschaak(S: Dbs.Stelling): BOOLEAN;
VAR x : RichtingNummer;
BEGIN
(*$O-*) (* Overflow check *)
	FOR x:=1 TO 8 DO
(*@@@@@@@@@	wat hiermee te doen	IF S.ZK = S.WK + Krichting[x] THEN
			RETURN(TRUE);
		END; @@@@@@@@************)
	END;
	RETURN(FALSE);
(*O=*) (* Overflow check *)
END IsKKschaak;
			

(*-------- Kijk of degene die aan zet is, schaak staat ----------*)		
PROCEDURE IsSchaak(S: Dbs.Stelling): BOOLEAN;
VAR Kveld, Sveld, Veld: WerkVeld;

			PROCEDURE SchaakDoorStuk(StukNr: DbsDef.StukNummer): BOOLEAN;
			VAR x: RichtingNummer;
			BEGIN
(*$O-*) (* Overflow check *)
				WITH StukTabel[StukNr] DO
					FOR x:=1 TO AtlRicht DO
						Veld:=Sveld + Richting[x];
						IF Meer THEN
							WHILE ((BITSET(Veld) * BuitenBordTest) = BITSET(0)) AND (Bord[Veld] = Leeg) DO
								Veld:=Veld + Richting[x];
							END;
						END;
						IF Veld = Kveld THEN
							RETURN(TRUE);
						END;
					END;
				END;
				RETURN(FALSE);
			END SchaakDoorStuk;
(*$O=*) (* Overflow check *)

BEGIN
	ZetStukkenOp(S);
	IF S.AanZet = DbsDef.Wit THEN
		Kveld:=S.WK;
	ELSE
		Kveld:=S.ZK;
	END;
	IF (S.s3 # S.WK) AND (S.s3 # S.ZK) AND (StukTabel[3].Kleur # S.AanZet) THEN
		Sveld:=S.s3;
		IF SchaakDoorStuk(3) THEN
			VerwijderStukken(S);
			RETURN(TRUE);
		END;
	END;
	IF (S.s4 # S.WK) AND (S.s4 # S.ZK) AND (StukTabel[4].Kleur # S.AanZet) THEN
		Sveld:=S.s4;
		IF SchaakDoorStuk(4) THEN
			VerwijderStukken(S);
			RETURN(TRUE);
		END;
	END;
	VerwijderStukken(S);
	RETURN(FALSE);
END IsSchaak;


VAR GZ: GenZrec;


PROCEDURE AddKzet(S: Dbs.Stelling; StukNr: DbsDef.StukNummer; Kveld, Naar: WerkVeld; SlagZet: BOOLEAN);
VAR x: DbsDef.StukNummer;                                   (* van? *)
BEGIN
	IF SlagZet THEN
	(*---- Stop het geslagen stuk "onder" de koning ----*)
		IF S.s3 = Naar THEN
			S.s3:=S.Velden[StukTabel[3].Knummer];
		ELSIF S.s4 = Naar THEN
			S.s4:=S.Velden[StukTabel[4].Knummer];
		END;
	END;
	(*------- Sjouw geslagen stukken mee bij koningszetten -----------*)
	IF S.s3 = Kveld THEN
		S.s3:=Naar;
	END;
	IF S.s4 = Kveld THEN
		S.s4:=Naar;
	END;
	(*------ Verzet de koning ---------*)
	S.Velden[StukNr]:=Naar;
	S.AanZet:=NOT S.AanZet;
	Dbs.Get(S);
	IF S.Resultaat # Dbs.Illegaal THEN
		INC(GZ.Aantal);
		GenZtabel[GZ.Aantal]:=S;
	END;
END AddKzet;


PROCEDURE AddGewZet(S: Dbs.Stelling; StukNr: DbsDef.StukNummer; Naar: WerkVeld; SlagZet: BOOLEAN);
VAR x: DbsDef.StukNummer;
BEGIN
	IF SlagZet THEN
	(*---- Stop het geslagen stuk "onder" de koning ----*)
		IF S.s3 = Naar THEN
			S.s3:=S.Velden[StukTabel[3].Knummer];
		ELSIF S.s4 = Naar THEN
			S.s4:=S.Velden[StukTabel[4].Knummer];
		END;
	END;
	(*------ Verzet het stuk ---------*)
	S.Velden[StukNr]:=Naar;
	S.AanZet:=NOT S.AanZet;
	Dbs.Get(S);
	IF S.Resultaat # Dbs.Illegaal THEN
		INC(GZ.Aantal);
		GenZtabel[GZ.Aantal]:=S;
	END;
END AddGewZet;


(*-------- Genereer zetten ----------*)		
PROCEDURE GenZ(S: Dbs.Stelling): GenZrec;
VAR Sveld, Kveld, Veld: WerkVeld;
			
			

			PROCEDURE GenZperStuk(StukNr: DbsDef.StukNummer);
			VAR x: RichtingNummer;
			BEGIN
(*$O-*) (* Overflow check *)
				WITH StukTabel[StukNr] DO
					FOR x:=1 TO AtlRicht DO
						Veld:=Sveld + Richting[x];
						IF Meer THEN
							WHILE ((BITSET(Veld) * BuitenBordTest) = BITSET(0)) AND (Bord[Veld] = Leeg) DO
								AddGewZet(S, StukNr, Veld, FALSE);
								Veld:=Veld + Richting[x];
							END;
						END;
						IF (BITSET(Veld) * BuitenBordTest) = BITSET(0) THEN
							IF Bord[Veld] = Leeg THEN
								AddGewZet(S, StukNr, Veld, FALSE);
							ELSE
								IF StukTabel[Bord[Veld]].Kleur # S.AanZet THEN
									AddGewZet(S, StukNr, Veld, TRUE);
								END;
							END;
						END;
					END;
				END;
			END GenZperStuk;
(*$O=*) (* Overflow check *)

BEGIN
	GZ.Aantal:=0;
	GZ.Sptr  :=ADR(GenZtabel);
	ZetStukkenOp(S);
	(*-------- Koningszetten --------*)
	IF S.AanZet = DbsDef.Wit THEN
		Sveld:=S.WK;
		Kveld:=S.WK;
		GenZperStuk(1);
	ELSE
		Sveld:=S.ZK;
		Kveld:=S.ZK;
		GenZperStuk(2);
	END;
	(*--------- Stukzetten ----------*)
	IF (StukTabel[3].Kleur = S.AanZet) AND (S.s3 # Kveld) THEN
		Sveld:=S.s3;
		GenZperStuk(3);
	END;
	IF (StukTabel[4].Kleur = S.AanZet) AND (S.s4 # Kveld) THEN
		Sveld:=S.s4;
		GenZperStuk(4);
	END;
	VerwijderStukken(S);
	RETURN(GZ);
END GenZ;



(*--------- Kijk of degene die aan zet is, pat staat -------------*)
PROCEDURE IsPat(S: Dbs.Stelling): BOOLEAN;
BEGIN
	RETURN(FALSE);
END IsPat;


(*
=================================================================================
		Aanmaak van de tabellen voor de zetgenerator
=================================================================================
*)

(*-------- Richtingen volgens de uitgebreide notatie -------*)
CONST
	Krichting = RichtingTabel(
		-11H,-10H,-0FH,
		-01H,      01H,
		 0FH, 10H, 11H);
	Trichting = RichtingTabel(
		-10H,-01H, 01H, 10H,
		 00H, 00H, 00H, 00H);
	Lrichting = RichtingTabel(
		-11H,-0FH, 0FH, 11H,
		 00H, 00H, 00H, 00H);
	Prichting = RichtingTabel(
		-21H,-1FH,-12H,-0EH,
		 0EH, 12H, 1FH, 21H);
		

(*-------- Konversie uitgebreide notatie --> gewoon --------*)
TYPE
	KonVeldTabel = ARRAY SHORTCARD[0..77H] OF WerkVeld;
VAR
	KonVeld: KonVeldTabel;
	

TYPE
	MeerStartTabel = ARRAY SHORTCARD[1..4] OF StartTabel;
	

(*-------- Containers voor zetseries -------------*)
TYPE
	KPcontainer = ARRAY VeldType, SHORTCARD[1..9] OF VeldType;
	TLcontainer = ARRAY SHORTCARD[1..4], VeldType, SHORTCARD[1..8] OF VeldType;
VAR
	Kcontainer : KPcontainer;
	Pcontainer : KPcontainer;
	Tcontainer : TLcontainer;
	Lcontainer : TLcontainer;
	

(*-------- Starttabellen -------------*)
VAR
	Kstart, Pstart: StartTabel;
	Tstart, Lstart: MeerStartTabel;


(*-------- Vul konversietabel uitgebreid --> gewoon ---------*)
PROCEDURE VulKonVeldTabel();
VAR x, y, Nr: SHORTCARD;
BEGIN
	FOR x:=1 TO 77H DO
		KonVeld[x]:=0FFH;
	END;
	Nr:=0;
	FOR x:=0 TO 7 DO
		FOR y:=0 TO 7 DO
			KonVeld[16*x+y]:=Nr;
			INC(Nr);
		END;
	END;
END VulKonVeldTabel;
(*-------- Vul Koning- of Paardcontainer --------------*)
PROCEDURE VulKPcontainer(Start: StartTabel; VAR C: KPcontainer; Richting: RichtingTabel);
VAR x: RichtingNummer;
	StartV, WerkV: WerkVeld;
	SerieNummer: SHORTCARD;
BEGIN
	FOR StartV:=0 TO 63 DO
		Start[StartV] := ADR(C[StartV]);
		SerieNummer:=1;
		FOR x:=1 TO 8 DO
			WerkV:=StartV+Richting[x];
			IF (BITSET(WerkV) * BuitenBordTest) = BITSET(0) THEN
				C[StartV, SerieNummer]:=KonVeld[WerkV];
				INC(SerieNummer);
			END;
		END;
		C[StartV, SerieNummer]:=Einde;
	END;
END VulKPcontainer;
		

(*-------- Vul Toren- of Lopercontainer --------------*)
PROCEDURE VulTLcontainer(Start: MeerStartTabel; VAR C: TLcontainer; Richting: RichtingTabel);
VAR x: SHORTCARD;
	StartV, WerkV: WerkVeld;
	SerieNummer: SHORTCARD;
BEGIN
	FOR x:=1 TO 4 DO
		FOR StartV:=0 TO 63 DO
			Start[x, StartV] := ADR(C[x, StartV]);
			SerieNummer:=1;
			WerkV:=StartV+Richting[x];
			WHILE (BITSET(WerkV) * BuitenBordTest) = BITSET(0) DO
				C[x, StartV, SerieNummer]:=KonVeld[WerkV];
				INC(SerieNummer);
				INC(WerkV, Richting[x]);
			END;
			C[x, StartV, SerieNummer]:=Einde;
		END;
	END;
END VulTLcontainer;
			

(*------- Vul de stuktabel met basisinformatie ------*)
PROCEDURE VulStukTabel();
VAR x: DbsDef.StukNummer;
BEGIN
	FOR x:=1 TO 4 DO
		WITH StukTabel[x] DO
			Soort:=DbsDef.Stukken[x];
			Kleur:=DbsDef.Kleuren[x];
			CASE Soort OF
			|	DbsDef.Koning: AtlStart:=1; Richting:=Krichting; AtlRicht:=8; Meer:=FALSE;
			|	DbsDef.Dame  : AtlStart:=8; Richting:=Krichting; AtlRicht:=8; Meer:=TRUE ;
			|	DbsDef.Toren : AtlStart:=4; Richting:=Trichting; AtlRicht:=4; Meer:=TRUE ;
			|	DbsDef.Loper : AtlStart:=4; Richting:=Lrichting; AtlRicht:=4; Meer:=TRUE ;
			|	DbsDef.Paard : AtlStart:=1; Richting:=Prichting; AtlRicht:=8; Meer:=FALSE;
			END;
			IF Kleur = DbsDef.Wit THEN
				Knummer:=1;
			ELSE
				Knummer:=2;
			END;
		END;
	END;
END VulStukTabel;


(*------- Vul de stuktabel met startinformatie ------*)
PROCEDURE VulStukTabelMetStart();
VAR x: DbsDef.StukNummer;
	y: SHORTCARD;
BEGIN
	FOR x:=1 TO 4 DO
		WITH StukTabel[x] DO
			FOR y:=1 TO 8 DO
				Start[y]:=NIL;
			END;
			CASE Soort OF
			|	DbsDef.Koning:
					Start[1]:=ADR(Kstart);
			|	DbsDef.Dame  :
					FOR y:=1 TO 4 DO
						Start[y]:=ADR(Tstart[y]);
					END;
					FOR y:=1 TO 4 DO
						Start[4+y]:=ADR(Lstart[y]);
					END;
			|	DbsDef.Toren :
					FOR y:=1 TO 4 DO
						Start[y]:=ADR(Tstart[y]);
					END;
			|	DbsDef.Loper :
					FOR y:=1 TO 4 DO
						Start[y]:=ADR(Lstart[y]);
					END;
			|	DbsDef.Paard :
					Start[1]:=ADR(Pstart);
			END;
		END;
	END;
END VulStukTabelMetStart;


BEGIN
	VulKPcontainer(Kstart, Kcontainer, Krichting);
	VulKPcontainer(Pstart, Pcontainer, Prichting);
	VulTLcontainer(Tstart, Tcontainer, Trichting);
	VulTLcontainer(Lstart, Lcontainer, Lrichting);
	VulStukTabel();
	ClrBord();
END NewZgen.	