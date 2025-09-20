IMPLEMENTATION MODULE Gen;

(* 05-06-90 Fout in de compiler: je kunt overflow check niet op tijd uitzetten *)


FROM AlgDef IMPORT Wit, Zwart, StukNummer;
IMPORT Dbs; (*@@@@@@ Moet eigenlijk ook niet *)
FROM Lib IMPORT QSort;

CONST
	BuitenBord      = BITSET(88H);
	Leeg = 0FFH;
		
TYPE
	WerkVeld       = SHORTCARD;
	RichtingNummer = SHORTCARD [1..8];
	RichtingTabel  = ARRAY RichtingNummer OF SHORTCARD;
	StukRec = RECORD
		Richting   : RichtingTabel;
		AtlRicht   : RichtingNummer;
		Meer       : BOOLEAN;
		Soort      : StukType;
		Kleur      : BOOLEAN;
		Knummer    : StukNummer;
		StukAfk    : CHAR;
	END;
	ZetSoort       = (Gewoon, SlagZet);
	NotatieType    = ARRAY SHORTCARD[0..77H] OF AsciiVeld;

VAR
    StukTabel : ARRAY StukNummer OF StukRec;
    Bord      : ARRAY Dbs.Veld OF SHORTCARD;
    GenZtabel : ARRAY SHORTCARD[1..MAX(AantalGzetten)] OF Dbs.Stelling;


CONST
	Krichting = RichtingTabel(
		 01H, 11H, 10H, 0FH,
		-01H,-11H,-10H,-0FH);
	Trichting = RichtingTabel(
		 01H, 10H,-01H,-10H,
		 00H, 00H, 00H, 00H);
	Lrichting = RichtingTabel(
		 11H, 0FH,-11H,-0FH,
		 00H, 00H, 00H, 00H);
	Prichting = RichtingTabel(
		 12H, 21H, 1FH, 0EH,
		-12H,-21H,-1FH,-0EH);


	Notatie = NotatieType(
		'a1', 'b1', 'c1', 'd1', 'e1', 'f1', 'g1', 'h1', '??', '??', '??', '??', '??', '??', '??', '??',
		'a2', 'b2', 'c2', 'd2', 'e2', 'f2', 'g2', 'h2', '??', '??', '??', '??', '??', '??', '??', '??',
		'a3', 'b3', 'c3', 'd3', 'e3', 'f3', 'g3', 'h3', '??', '??', '??', '??', '??', '??', '??', '??',
		'a4', 'b4', 'c4', 'd4', 'e4', 'f4', 'g4', 'h4', '??', '??', '??', '??', '??', '??', '??', '??',
		'a5', 'b5', 'c5', 'd5', 'e5', 'f5', 'g5', 'h5', '??', '??', '??', '??', '??', '??', '??', '??',
		'a6', 'b6', 'c6', 'd6', 'e6', 'f6', 'g6', 'h6', '??', '??', '??', '??', '??', '??', '??', '??',
		'a7', 'b7', 'c7', 'd7', 'e7', 'f7', 'g7', 'h7', '??', '??', '??', '??', '??', '??', '??', '??',
		'a8', 'b8', 'c8', 'd8', 'e8', 'f8', 'g8', 'h8');



(*-------- Zet stukken op het bord ------------------------*)
PROCEDURE ZetBordOp(S: Dbs.Stelling);
BEGIN
	(* eerst de stukken, dan kunnen ze eventueel uitgeveegd worden door de koningen *)
	Bord[S.s3]:=3;
	Bord[S.s4]:=4;
	Bord[S.WK]:=1;
	Bord[S.ZK]:=2;
END ZetBordOp;


(*-------- Haal ze er weer vanaf ------------------------*)
PROCEDURE ClrBord(S: Dbs.Stelling);
BEGIN
	Bord[S.WK]:=Leeg;
	Bord[S.ZK]:=Leeg;
	Bord[S.s3]:=Leeg;
	Bord[S.s4]:=Leeg;
END ClrBord;


(*------------ Kijk of een stelling geometrisch illegaal is ------------*)
PROCEDURE IsGeomIllegaal(S: Dbs.Stelling): BOOLEAN;
BEGIN
	WITH S DO
		IF (WK = ZK) OR (s3 = s4) THEN RETURN(TRUE) END;
		IF (WK = s3) AND (Kleuren[3] # Wit  ) THEN RETURN(TRUE) END;
		IF (WK = s4) AND (Kleuren[4] # Wit  ) THEN RETURN(TRUE) END;
		IF (ZK = s3) AND (Kleuren[3] # Zwart) THEN RETURN(TRUE) END;
		IF (ZK = s4) AND (Kleuren[4] # Zwart) THEN RETURN(TRUE) END;
		RETURN(FALSE);
	END;
END IsGeomIllegaal;


(*-------- Kijk of de koningen elkaar schaak geven --------*)
PROCEDURE IsKKschaak(S: Dbs.Stelling): BOOLEAN;
VAR x : RichtingNummer;
BEGIN
(*$O-*) (* Overflow check *)
	FOR x:=1 TO 8 DO
		IF S.ZK = S.WK + Krichting[x] THEN
			RETURN(TRUE);
		END;
	END;
	RETURN(FALSE);
(*O=*) (* Overflow check *)
END IsKKschaak;
			

(*-------- Kijk of degene die aan zet is, schaak staat ----------*)		
PROCEDURE IsSchaak(S: Dbs.Stelling): BOOLEAN;
VAR Kveld, Sveld, Veld: WerkVeld;

			PROCEDURE SchaakDoorStuk(StukNr: StukNummer): BOOLEAN;
			VAR x: RichtingNummer;
			BEGIN
(*$O-*) (* Overflow check *)
				WITH StukTabel[StukNr] DO
					FOR x:=1 TO AtlRicht DO
						Veld:=Sveld + Richting[x];
						IF Meer THEN
							WHILE ((BITSET(Veld) * BuitenBord) = BITSET(0)) AND (Bord[Veld] = Leeg) DO
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
	ZetBordOp(S);
	IF S.AanZet = Wit THEN
		Kveld:=S.WK;
	ELSE
		Kveld:=S.ZK;
	END;
	IF (S.s3 # S.WK) AND (S.s3 # S.ZK) AND (StukTabel[3].Kleur # S.AanZet) THEN
		Sveld:=S.s3;
		IF SchaakDoorStuk(3) THEN
			ClrBord(S);
			RETURN(TRUE);
		END;
	END;
	IF (S.s4 # S.WK) AND (S.s4 # S.ZK) AND (StukTabel[4].Kleur # S.AanZet) THEN
		Sveld:=S.s4;
		IF SchaakDoorStuk(4) THEN
			ClrBord(S);
			RETURN(TRUE);
		END;
	END;
	ClrBord(S);
	RETURN(FALSE);
END IsSchaak;




(*-------- Genereer zetten ----------*)		
PROCEDURE GenZ(S: Dbs.Stelling): GenZrec;
VAR Sveld, Kveld, Veld: WerkVeld;
	GZ: GenZrec;
			
			PROCEDURE AddZet(S: Dbs.Stelling; StukNr: StukNummer; Naar: WerkVeld; Zsoort: ZetSoort);
			VAR x: StukNummer;
			BEGIN
				IF Zsoort = SlagZet THEN
					(*---- Stop het geslagen stuk "onder" de koning ----*)
					IF S.s3 = Naar THEN
						S.s3:=S.Velden[StukTabel[3].Knummer];
					ELSIF S.s4 = Naar THEN
						S.s4:=S.Velden[StukTabel[4].Knummer];
					END;
				END;
				(*------- Sjouw geslagen stukken mee bij koningszetten -----------*)
				IF Sveld = Kveld THEN
					IF S.s3 = Kveld THEN
						S.s3:=Naar;
					END;
					IF S.s4 = Kveld THEN
						S.s4:=Naar;
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
			END AddZet;
			

			PROCEDURE GenZperStuk(StukNr: StukNummer);
			VAR x: RichtingNummer;
			BEGIN
(*$O-*) (* Overflow check *)
				WITH StukTabel[StukNr] DO
					FOR x:=1 TO AtlRicht DO
						Veld:=Sveld + Richting[x];
						IF Meer THEN
							WHILE ((BITSET(Veld) * BuitenBord) = BITSET(0)) AND (Bord[Veld] = Leeg) DO
								AddZet(S, StukNr, Veld, Gewoon);
								Veld:=Veld + Richting[x];
							END;
						END;
						IF (BITSET(Veld) * BuitenBord) = BITSET(0) THEN
							IF Bord[Veld] = Leeg THEN
								AddZet(S, StukNr, Veld, Gewoon);
							ELSE
								IF StukTabel[Bord[Veld]].Kleur # S.AanZet THEN
									AddZet(S, StukNr, Veld, SlagZet);
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
	ZetBordOp(S);
	(*-------- Koningszetten --------*)
	IF S.AanZet = Wit THEN
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
	ClrBord(S);
	RETURN(GZ);
END GenZ;


(*-------- Genereer zetten gesorteerd ----------*)		
PROCEDURE KleinerDan(L, R: CARDINAL): BOOLEAN;
VAR SL, SR: Stelling;
BEGIN
	SL:=GenZtabel[SHORTCARD(L)];
	SR:=GenZtabel[SHORTCARD(R)];
	IF SL.Resultaat > SR.Resultaat THEN
		RETURN(TRUE);
	ELSIF SL.Resultaat < SR.Resultaat THEN
		RETURN(FALSE);
	ELSE
		CASE SL.Resultaat OF
		|	Dbs.Gewonnen: IF SL.Aantal > SR.Aantal THEN RETURN(TRUE); END;
		|	Dbs.Verloren: IF SL.Aantal < SR.Aantal THEN RETURN(TRUE); END;
		END;
		RETURN(FALSE);
	END;
END KleinerDan;

PROCEDURE Swap(L, R: CARDINAL);
VAR Hlp: Stelling;
BEGIN
	Hlp:=GenZtabel[SHORTCARD(L)];
	GenZtabel[SHORTCARD(L)]:=GenZtabel[SHORTCARD(R)];
	GenZtabel[SHORTCARD(R)]:=Hlp;
END Swap;

PROCEDURE GenZsort(S: Dbs.Stelling): GenZrec;
VAR GZ: GenZrec;
BEGIN
	GZ:=GenZ(S);
	QSort(CARDINAL(GZ.Aantal), KleinerDan, Swap);
	RETURN(GZ);
END GenZsort;


(*--------- Kijk of degene die aan zet is, pat staat -------------*)
PROCEDURE IsPat(S: Dbs.Stelling): BOOLEAN;
BEGIN
	RETURN(FALSE);
END IsPat;



PROCEDURE VulStukTabel();
VAR x: StukNummer;
BEGIN
	FOR x:=1 TO 4 DO
		WITH StukTabel[x] DO
			Soort:=Stukken[x];
			Kleur:=Kleuren[x];
			CASE Soort OF
			|	Koning: Richting:=Krichting; AtlRicht:=8; Meer:=FALSE; StukAfk:='K';
			|	Dame  : Richting:=Krichting; AtlRicht:=8; Meer:=TRUE ; StukAfk:='D';
			|	Toren : Richting:=Trichting; AtlRicht:=4; Meer:=TRUE ; StukAfk:='T';
			|	Loper : Richting:=Lrichting; AtlRicht:=4; Meer:=TRUE ; StukAfk:='L';
			|	Paard : Richting:=Prichting; AtlRicht:=8; Meer:=FALSE; StukAfk:='P';
			END;
			IF Kleur = Wit THEN
				Knummer:=1;
			ELSE
				Knummer:=2;
			END;
		END;
	END;
END VulStukTabel;


(*------- Veld naar ASCII ----------------------------------*)
PROCEDURE VeldToAscii(V: Dbs.Veld): AsciiVeld;
BEGIN
	RETURN(Notatie[V]);
END VeldToAscii;


(*------- ASCII naar veld -----------------------------------*)
PROCEDURE AsciiToVeld(A: AsciiVeld): Dbs.Veld;
BEGIN
	A[0]:=CAP(A[0]);
	RETURN(Dbs.Veld(ORD(A[0]) - ORD('A') + 16 * (ORD(A[1]) - ORD('1'))));
END AsciiToVeld;


(*------- Lever info over stuk ------------------------------*)
PROCEDURE GetStukInfo(S: Dbs.Stelling; Nr: StukNummer): StukInfoRec;
VAR SI: StukInfoRec;
BEGIN
	SI.Veld   :=S.Velden[Nr];
	SI.X      :=1 + S.Velden[Nr] MOD 16;
	SI.Y      :=1 + S.Velden[Nr] DIV 16;
	SI.Kleur  :=StukTabel[Nr].Kleur;
	SI.StukAfk:=StukTabel[Nr].StukAfk;
	RETURN(SI);
END GetStukInfo;


(*-------- Maak het bord leeg -------------------------------*)
PROCEDURE MaakBordLeeg();
VAR x: SHORTCARD;
BEGIN
	FOR x:=0 TO 77H DO
		Bord[x]:=Leeg;
	END;
END MaakBordLeeg;

BEGIN
	VulStukTabel();
	MaakBordLeeg();
END Gen.	