MODULE Ovflow;

(* testen van de GVD overflow *)

IMPORT IO, DbsDef, Dbs;

CONST
	BuitenBord      = BITSET(88H);
	Leeg = 0FFH;
		
TYPE
	StukNummer     = SHORTCARD [1..4];
	RichtingNummer = SHORTCARD [1..8];
	RichtingTabel  = ARRAY RichtingNummer OF SHORTCARD;
	StukRec = RECORD
		Richting   : RichtingTabel;
		AtlRicht   : RichtingNummer;
		Meer       : BOOLEAN;
		Soort      : DbsDef.Stuk;
		Kleur      : BOOLEAN;
	END;

VAR
    StukTabel : ARRAY StukNummer OF StukRec;
    Bord      : ARRAY Dbs.Veld OF SHORTCARD;


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

(*-------- Zet stukken op het bord ------------------------*)
PROCEDURE ZetBordOp(S: Dbs.Stelling);
BEGIN
	Bord[S.WK]:=1;
	Bord[S.ZK]:=2;
	Bord[S.s3]:=3;
	Bord[S.s4]:=4;
END ZetBordOp;


(*-------- Haal ze er weer vanaf ------------------------*)
PROCEDURE ClrBord(S: Dbs.Stelling);
BEGIN
	Bord[S.WK]:=Leeg;
	Bord[S.ZK]:=Leeg;
	Bord[S.s3]:=Leeg;
	Bord[S.s4]:=Leeg;
END ClrBord;


(*-------- Kijk of degene die aan zet is, schaak staat ----------*)		
PROCEDURE IsSchaak(S: Dbs.Stelling): BOOLEAN;
VAR Kveld, Sveld: SHORTCARD;  (* dit is dus een probleem *)
	Veld: SHORTCARD;
(*$O- *)

			PROCEDURE SchaakDoorStuk(StukNr: StukNummer): BOOLEAN;
			VAR x: RichtingNummer;
			BEGIN
				WITH StukTabel[StukNr] DO
					IF Kleur # S.AanZet THEN
						FOR x:=1 TO AtlRicht DO
							Veld:=Sveld + Richting[x];
							IF ((BITSET(Veld) * BuitenBord) = BITSET(0)) THEN
								IF Veld = Kveld THEN
									RETURN(TRUE);
								END;
								IF Meer AND (Bord[Veld] = Leeg) THEN
									REPEAT
										Veld:=Veld + Richting[x];
									UNTIL ((BITSET(Veld) * BuitenBord) # BITSET(0)) OR (Bord[Veld] # Leeg);
									IF Veld = Kveld THEN
										RETURN(TRUE);
									END;
								END;
							END;
						END;
					END;
				END;
				RETURN(FALSE);
			END SchaakDoorStuk;
(*$O= *)

BEGIN
	ZetBordOp(S);
	IF S.AanZet = DbsDef.Wit THEN
		Kveld:=S.WK;
	ELSE
		Kveld:=S.ZK;
	END;
	Sveld:=S.s3;
	IF SchaakDoorStuk(3) THEN
		ClrBord(S);
		RETURN(TRUE);
	END;
	Sveld:=S.s4;
	IF SchaakDoorStuk(4) THEN
		ClrBord(S);
		RETURN(TRUE);
	END;
	ClrBord(S);
	RETURN(FALSE);
END IsSchaak;

			
TYPE
	WKteller    = [0.. 9];
	StukTeller  = [0..63];
	CvtWKtype   = ARRAY WKteller   OF Dbs.Veld;
	CvtStukType = ARRAY StukTeller OF Dbs.Veld;
	
CONST
	CvtWK = CvtWKtype(
		00H,01H,02H,03H,
			11H,12H,13H,
				22H,23H,
					33H);

	CvtStuk = CvtStukType(
		00H,01H,02H,03H,04H,05H,06H,07H,
		10H,11H,12H,13H,14H,15H,16H,17H,
		20H,21H,22H,23H,24H,25H,26H,27H,
		30H,31H,32H,33H,34H,35H,36H,37H,
		40H,41H,42H,43H,44H,45H,46H,47H,
		50H,51H,52H,53H,54H,55H,56H,57H,
		60H,61H,62H,63H,64H,65H,66H,67H,
		70H,71H,72H,73H,74H,75H,76H,77H);


PROCEDURE Test();
VAR WK: WKteller;
	ZK, s3, s4: StukTeller;
	S: Dbs.Stelling;
	Dummy: BOOLEAN;
BEGIN
	FOR WK:=0 TO 9 DO
		S.WK:=CvtWK[WK];
		IO.WrInt(WK, 10); IO.WrLn();
		FOR ZK:=0 TO 63 DO
			S.ZK:=CvtStuk[ZK];
    		FOR s3:=0 TO 63 DO
    			S.s3:=CvtStuk[s3];
        		FOR s4:=0 TO 63 DO
        			S.s4:=CvtStuk[s4];
        			Dummy:=IsSchaak(S);
				END;
			END;
		END;
	END;
END Test;






PROCEDURE VulStukTabel();
VAR x: StukNummer;
BEGIN
	FOR x:=1 TO 4 DO
		WITH StukTabel[x] DO
			Soort:=DbsDef.Stukken[x];
			Kleur:=DbsDef.Kleuren[x];
			CASE Soort OF
			|	DbsDef.Koning: Richting:=Krichting; AtlRicht:=8; Meer:=FALSE;
			|	DbsDef.Dame  : Richting:=Krichting; AtlRicht:=8; Meer:=TRUE ;
			|	DbsDef.Toren : Richting:=Trichting; AtlRicht:=4; Meer:=TRUE ;
			|	DbsDef.Loper : Richting:=Lrichting; AtlRicht:=4; Meer:=TRUE ;
			|	DbsDef.Paard : Richting:=Prichting; AtlRicht:=8; Meer:=FALSE;
			END;
		END;
	END;
END VulStukTabel;

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
	Test();
END Ovflow.	