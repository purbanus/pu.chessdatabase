MODULE BoolTest;

IMPORT IO;

VAR T, F, Mi, Ma: BOOLEAN;

BEGIN
	T:=TRUE;
	F:=FALSE;
	Mi:=MIN(BOOLEAN);
	Ma:=MAX(BOOLEAN);
	IO.WrStr('TRUE:  '); IO.WrShtCard(SHORTCARD(T ), 3); IO.WrLn();
	IO.WrStr('FALSE: '); IO.WrShtCard(SHORTCARD(F ), 3); IO.WrLn();
	IO.WrStr('MIN:   '); IO.WrShtCard(SHORTCARD(Mi), 3); IO.WrLn();
	IO.WrStr('MAX:   '); IO.WrShtCard(SHORTCARD(Ma), 3); IO.WrLn();
END BoolTest.