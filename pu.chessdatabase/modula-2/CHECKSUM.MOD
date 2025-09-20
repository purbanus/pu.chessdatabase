(*$M Dbs*) (* Code segment *)
MODULE CheckSum;

IMPORT Window, IO, VM, Dbs, Win;

VAR
	S         : VM.Stelling;
	WK        : VM.WKveld;
	ZK, s3, s4: VM.Veld;
	Tel       : LONGCARD;
	
BEGIN
	Dbs.Open();
	Window.PutOnTop(Win.Status);
	IO.WrStr('Er wordt een checksum gemaakt');
	Window.PutOnTop(Win.CacheWin);
	FOR WK:=0 TO 9 DO
		S.WK:=WK;
		FOR ZK:=0 TO 63 DO
			S.ZK:=ZK;
			FOR s3:=0 TO 63 DO
				S.s3:=s3;
				FOR s4:=0 TO 63 DO
					S.s4:=s4;
					S.AanZet:=FALSE;
					Tel:=Tel+LONGCARD(VM.Get(S));
					S.AanZet:=TRUE;
					Tel:=Tel+LONGCARD(VM.Get(S));
				END;
			END;
			S.AanZet:=FALSE;
			VM.FreeRecord(S);
			S.AanZet:=TRUE;
			VM.FreeRecord(S);
		END;
	END;
	Window.PutOnTop(Win.Status);
	IO.WrLn();
	IO.WrStr('De checksum is');
	IO.WrLngCard(Tel, 20);
	IO.WrLn();
	IF Tel = 727897886 THEN
		IO.WrStr('En dit is ok')
	ELSE
		IO.WrStr('Maar daar klopt geen hout van!!!! Neem kontakt op met Peter U.');
	END;
END CheckSum.
					