package pu.chessdatabase.dal;

import pu.chessdatabase.bo.ReportProc;
import pu.chessdatabase.util.Matrix;
import pu.chessdatabase.util.Range;
import pu.chessdatabase.util.Vector;

import org.springframework.stereotype.Service;

import pu.chessdatabase.bo.AlgDef;
import pu.chessdatabase.bo.BoStelling;

@Service
public class Dbs
{
public static final int MAX_RESULTAAT_TYPE = 4;
public static final int VMillegaal      = 0x0FF;
public static final int VMremise        = 0x000;
public static final int VMschaak        = 0x080;
public static final int VerliesOffset   = 0x080;
public static final int OKTANTEN = 8;

public static final String DFT_DBS_NAAM = "KDKT.DBS";
public static final int DFT_RPT_FREQ = 4096;

/**==============================================================================================================
* Konversie WK notatie van VM naar Zgen
*==============================================================================================================*/
public static final int [] CvtWK = {
	0x00,0x01,0x02,0x03,
		 0x11,0x12,0x13,
			  0x22,0x23,
				   0x33
};
/**==============================================================================================================
* Konversie stuk (niet-WK) notatie van VM naar Zgen
*==============================================================================================================*/
public static final int [] CvtStuk = {
	0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,
	0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,
	0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,
	0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,
	0x40,0x41,0x42,0x43,0x44,0x45,0x46,0x47,
	0x50,0x51,0x52,0x53,0x54,0x55,0x56,0x57,
	0x60,0x61,0x62,0x63,0x64,0x65,0x66,0x67,
	0x70,0x71,0x72,0x73,0x74,0x75,0x76,0x77
};
/**==============================================================================================================
* Oktantentabel. Deze wordt gebruikt om te kijken in welk oktant een stuk (inz. de witte koning) zich bevindt.
* 0 = foutkode, daar wordt in Cardinaliseer() op getest.
*==============================================================================================================*/
public static final int [] OktTabel = {
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   6,6,6,6,5,5,5,5
};
/**========================================================================================
* Transformatietabel voor WK. Nadat WK is getransformeerd naar het juiste oktant,
* moet hij nog naar de speciale VM-kodering (0..9) worden gebracht. Dat gebeurt hiermee
* 10 = foutkode, wordt in VM op getest.
*========================================================================================*/
public static final int [] TrfWK = {
	 0, 1, 2, 3,10,10,10,10,
	10, 4, 5, 6,10,10,10,10,
	10,10, 7, 8,10,10,10,10,
	10,10,10, 9,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10
};
public static final Matrix [] MatrixTabel = {
	null, // Dit heeft een matrix per oktant, en oktant 0 bestaat niet
	new Matrix( new Vector[] { new Vector( 1, 0), new Vector( 0, 1) }),
	new Matrix( new Vector[] { new Vector(-1, 0), new Vector( 0, 1) }),
	new Matrix( new Vector[] { new Vector( 0, 1), new Vector(-1, 0) }),
	new Matrix( new Vector[] { new Vector( 0,-1), new Vector(-1, 0) }),
	new Matrix( new Vector[] { new Vector(-1, 0), new Vector( 0,-1) }),
	new Matrix( new Vector[] { new Vector( 1, 0), new Vector( 0,-1) }),
	new Matrix( new Vector[] { new Vector( 0,-1), new Vector( 1, 0) }),
	new Matrix( new Vector[] { new Vector( 0, 1), new Vector( 1, 0) })
};
public static final Vector [] TranslatieTabel = new Vector [] {
	null, // Dit heeft een vector per oktant, en oktant 0 bestaat niet
	new Vector( 0, 0),
	new Vector( 7, 0),
	new Vector( 0, 7),
	new Vector( 7, 7),
	new Vector( 7, 7),
	new Vector( 0, 7),
	new Vector( 7, 0),
	new Vector( 0, 0)
};

//Range<Integer> Veld = Range.of( 0, 0x77 );
//Range<Integer> OKtant = Range.of( 1, OKTANTEN );
//Range<Integer> OKtant_0 = Range.of( 0, OKTANTEN );
//Range<Integer> ResultaatRange = Range.of( 0, 3 );

Range Veld = new Range( 0, 0x77 );
Range OKtant = new Range( 1, OKTANTEN );
Range OKtant_0 = new Range( 0, OKTANTEN );
Range ResultaatRange = new Range( 0, 3 );

private VM vm = new VM(); //@@NOG Auto inject want je mag er maar 1 hebben
int[][] TrfTabel = new int [OKTANTEN + 1][Veld.getMaximum() + 1];
private String DbsNaam;
public long [] Rpt = new long [4];
long [] ReportArray = new long [4];
int RptTeller;
int RptFreq;
ReportProc RptProc;

public Dbs()
{
	DbsNaam = DFT_DBS_NAAM;
	CreateTrfTabel();
	RptFreq = DFT_RPT_FREQ;
	RptProc = null;
	ClearTellers();

}
//void setRpt( long [] aReportArray )
//{
//	Rpt = aReportArray;
//}

/**
 * ===========================================================================
		Deel 1: Rapportage
===========================================================================

PROCEDURE ClearTellers();
VAR x: ResType;
BEGIN
	FOR x:=MIN(ResType) TO MAX(ResType) DO
		Rpt[x]:=0;
	END;
	RptTeller:=0;
END ClearTellers;
 */
/**
 * ------------ Tellers leegmaken -------------------------
 */
public void ClearTellers()
{
	for ( int x = 0; x < 4; x++ )
	{
		Rpt[x] = 0L;
	}
	RptTeller = 0;
}
/**
PROCEDURE GetTellers(): ReportArray;
BEGIN
	RETURN(Rpt);
END GetTellers;
*/
/**
 * ------------- Tellerstand uitlezen ---------------------
 */
public long [] GetTellers()
{
	return new long [] { Rpt[0], Rpt[1], Rpt[2], Rpt[3] };
}
/**
PROCEDURE SetReport(Freq: CARDINAL; R: ReportProc);
BEGIN
	RptFreq:=Freq;
	RptTeller:=0;
	RptProc:=R;
END SetReport;
 */
public void SetReport( int aFrequency, ReportProc aReportProc )
{
	RptFreq = aFrequency;
	RptTeller = 0;
	RptProc = aReportProc;
}
/**
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
*/
/**
 * -------- Bijwerken tellers ---------------------------------
 */
public void UpdateTellers( ResultaatType aResultaatType )
{
	if ( RptProc != null )
	{
		Rpt[aResultaatType.ordinal()]++;
		RptTeller++;
		if ( RptTeller >= RptFreq )
		{
			RptTeller = 0;
			RptProc.doReport( Rpt );
		}
	}
}
/**
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
 */
/**
 * Doen we niet, we hebben  classes Matrix en Vector die dat doen
 */
/**
 * PROCEDURE CreateTrfTabel();
VAR O		:	Oktant;
	x, y	: INTEGER;
	Vres	: Vector;
	OudVeld	: Veld;
	NewVeld	: VM.Veld;
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
 */
public void CreateTrfTabel()
{
	Vector Vres;
	for ( int oktant = 1; oktant <= OKTANTEN; oktant++ ) // @@NOG CHECK is die grens goed of moet het <= zijn
	{
		for ( int rij = 0; rij < 8; rij++ ) // @@NOG rijen?
		{
			for ( int kol = 0; kol < 8; kol++ ) // @@NOG kolommen?
			{
				Vres = new Vector( kol, rij );
				Vres = MatrixTabel[oktant].multiply( Vres );
				Vres = Vres.add( TranslatieTabel[oktant] );
				int oudVeld = kol + 16 * rij;
				int newVeld = Vres.get( 0 ) + 8 * Vres.get( 1 );
				TrfTabel[oktant][oudVeld] = newVeld;
			}
		}
	}
}
/**
 * ===========================================================================
		Deel 2: Konversie
===========================================================================

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
 */
/**
 * -------- Stelling van Dbs-formaat naar VM-formaat ------
 */
public VMStelling Cardinaliseer( BoStelling aStelling )
{
	int Okt = OktTabel[aStelling.getWk()];
	if ( Okt == 0 )
	{
		throw new RuntimeException( "Foutief oktant in Dbs.Cardinaliseer voor WK op " + Integer.toHexString( aStelling.getWk() ) );
	}
	VMStelling vmStelling = VMStelling.builder()
		.wk( TrfWK[TrfTabel[Okt][aStelling.getWk()]] )
		.zk( TrfTabel[Okt][aStelling.getZk()] )
		.s3( TrfTabel[Okt][aStelling.getS3()] )
		.s4( TrfTabel[Okt][aStelling.getS4()] )
		.aanZet( aStelling.isAanZet() )
		.build();
	return vmStelling;
}
/**
============================================================================
		Deel 3: Lezen en schrijven van database records
============================================================================	

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
 */
/**
 *----------- Schrijven ----------------- 
 */
public void Put( BoStelling aStelling )
{
	int VMRec = 0; // Is eigenlijk een byte
	VMStelling vmStelling = Cardinaliseer( aStelling );
	switch ( aStelling.getResultaat() )
	{
		case Illegaal : VMRec = VMillegaal; break;
		case Remise   : VMRec = aStelling.isSchaak() ? VMschaak : VMremise; break;
		case Gewonnen : VMRec = aStelling.getAantalZetten(); break;
		case Verloren: VMRec = aStelling.getAantalZetten() + VerliesOffset; break;
	}
	UpdateTellers( aStelling.getResultaat() );
	vm.Put( vmStelling, (byte) VMRec );
}
/**
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
 */
/**
 * ----------- Lezen -----------------
 */
public BoStelling Get( BoStelling aStelling )
{
	VMStelling vmStelling = Cardinaliseer( aStelling );
	return GetDirect( vmStelling, aStelling );
}
/**
 * (*----------- Lezen zonder cardinaliseren -------*)
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
 */
/**
 * ----------- Lezen zonder cardinaliseren -------
 */
BoStelling GetDirect( VMStelling aVMStelling, BoStelling aBoStelling )
{
	int VMrec = vm.Get( aVMStelling );
	aBoStelling.setSchaak( false );
	if ( VMrec == VMillegaal )
	{
		aBoStelling.setResultaat( ResultaatType.Illegaal );
		aBoStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VMremise )
	{
		aBoStelling.setResultaat( ResultaatType.Remise );
		aBoStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VMschaak )
	{
		aBoStelling.setResultaat( ResultaatType.Remise );
		aBoStelling.setAantalZetten( 0 );
		aBoStelling.setSchaak( true );
	}
	else if ( VMrec < VerliesOffset )
	{
		aBoStelling.setResultaat( ResultaatType.Gewonnen );
		aBoStelling.setAantalZetten( VMrec );
	}
	else
	{
		aBoStelling.setResultaat( ResultaatType.Verloren );
		aBoStelling.setAantalZetten( VMrec - VerliesOffset );
	}
	return aBoStelling;
}
/**
PROCEDURE FreeRecord(S: Stelling);
VAR VMS: VM.Stelling;
BEGIN
	VMS:=Cardinaliseer(S);
	VM.FreeRecord(VMS);
END FreeRecord;
 */
/**
 * ----------- Vrijgeven record ------------
 */
public void FreeRecord( BoStelling aBoStelling )
{
	VMStelling vmStelling = Cardinaliseer( aBoStelling );
	vm.FreeRecord( vmStelling );
}
/**
==========================================================================
		Deel 4: Bewerkingen op de gehele database
==========================================================================

PROCEDURE Name(Naam: ARRAY OF CHAR);
BEGIN
	Str.Copy(DbsNaam, Naam);
END Name;
 */
/**
 * ------- Naam geven -------------------
 */
public void Name( String aNaam )
{
	DbsNaam = aNaam;
}
/**
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
 */
/**
 *  ------- Creeren nieuwe database ------
 */
public void Create()
{
	if ( DbsNaam == null || DbsNaam.length() == 0 )
	{
		throw new RuntimeException( "Geen naam opgegeven voor de database" );
	}
	vm.Create( DbsNaam );
}
/**
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
 */
/**
 * ------- Openen database --------------
 */
public void Open()
{
	if ( DbsNaam == null || DbsNaam.length() == 0 )
	{
		throw new RuntimeException( "Geen naam opgegeven voor de database" );
	}
	vm.Open( DbsNaam );
}
/**
PROCEDURE Close();
BEGIN
	VM.Close();
END Close;
 */
/**
 * ------- Sluiten database -------------
 */
public void Close()
{
	vm.Close();
}
public void delete()
{
	vm.delete();
}
/**
==========================================================================
		Deel 5: Passes over de gehele database
==========================================================================

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
 */
/**
 * --------- Pass over stukken 3 en 4 ----------------------------------
 */
public void Pass34( BoStelling aBoStelling, VMStelling aVmStelling, PassProc aPassProc )
{
	aVmStelling.setS3( 0 );
	while ( aVmStelling.getS3() <= 63 )
	{
		aBoStelling.setS3( CvtStuk[aVmStelling.getS3()] );
		while ( aVmStelling.getS4() <= 63 )
		{
			// @@NOG CHECK is aBoStelling veranderd of moet je boStelling gebruiken? 
			@SuppressWarnings( "unused" )
			BoStelling boStelling = GetDirect( aVmStelling, aBoStelling ); // aBoStelling.s4 maakt nog niet uit
			if ( aBoStelling.getResultaat() == ResultaatType.Remise )
			{
				aBoStelling.setS4( CvtStuk[aVmStelling.getS4()] ); // Nu wel
				aPassProc.doPass( aBoStelling );
			}
			aVmStelling.setS4( aVmStelling.getS4() + 1 );
		}
		aVmStelling.setS3( aVmStelling.getS3() + 1 );
	}
	aVmStelling.setS3( aVmStelling.getS3() - 1 );
	aVmStelling.setS4( aVmStelling.getS4() - 1 );
	vm.FreeRecord( aVmStelling );
}
/**
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
*/
/**
 * --------- Pass over de remisestellingen met wit aan zet -------------
 */
void markeerWitPass( PassProc aPassProc )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( AlgDef.Wit );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( AlgDef.Wit );
	for ( int ZK = 0; ZK < 64; ZK++ )
	{
		vmStelling.setZk( ZK );
		boStelling.setZk( CvtStuk[ZK] );
		for ( int WK = 0; WK < 10; WK++ )
		{
			vmStelling.setWk( WK );
			boStelling.setWk( CvtWK[WK] );
			Pass34( boStelling, vmStelling, aPassProc );
		}
	}
}
/**
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
 */
/**
 * --------- Pass over de remisestellingen met zwart aan zet -------------
 */
void markeerZwartPass( PassProc aPassProc )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( AlgDef.Zwart );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( AlgDef.Zwart );
	for ( int WK = 0; WK < 10; WK++ )
	{
		vmStelling.setWk( WK );
		boStelling.setWk( CvtWK[WK] );
		for ( int ZK = 0; ZK < 64; WK++ )
		{
			vmStelling.setZk( ZK );
			boStelling.setZk( CvtStuk[ZK] );
			Pass34( boStelling, vmStelling, aPassProc );
		}
	}
}
/**
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
 */
/**
 * --------- Pass over alle stellingen -------------
 */
void markeerWitEnZwartPass( PassProc aPassProc )
{
	VMStelling vmStelling = new VMStelling();
	BoStelling boStelling = new BoStelling();
	vmStelling.setWk( 0 );
	while ( vmStelling.getWk() < 10 )
	{
		boStelling.setWk( CvtWK[vmStelling.getWk()] );
		vmStelling.setZk( 0 );
		while ( vmStelling.getZk() < 64 )
		{
			boStelling.setZk( CvtStuk[vmStelling.getZk()] );
			vmStelling.setS3( 0 );
			while ( vmStelling.getS3() < 64 )
			{
				boStelling.setS3( CvtStuk[vmStelling.getS3()] );
				vmStelling.setS4( 0 );
				while ( vmStelling.getS4() < 64 )
				{
					boStelling.setS4( CvtStuk[vmStelling.getS4()] );

					// Wit
					vmStelling.setAanZet( AlgDef.Wit );
					BoStelling gotBoStelling = GetDirect( vmStelling, boStelling ); // @@NOG Is die gotStelling nodig??
					gotBoStelling.setAanZet( AlgDef.Wit ); // @@NOG Waarom?
					aPassProc.doPass( gotBoStelling );
					
					// Zwart
					vmStelling.setAanZet( AlgDef.Zwart );
					gotBoStelling = GetDirect( vmStelling, boStelling ); // @@NOG Is die gotStelling nodig??
					gotBoStelling.setAanZet( AlgDef.Zwart ); // @@NOG Waarom?
					aPassProc.doPass( gotBoStelling );
		
					vmStelling.setS4( vmStelling.getS4() + 1 );
				}
				vmStelling.setS3( vmStelling.getS3() + 1 );
			}
			vmStelling.setZk( vmStelling.getZk() + 1 );
		}
		vmStelling.setWk( vmStelling.getWk() + 1 );
	}
}
/**
 * PROCEDURE Pass(T: PassType; P: PassProc);
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
 */
public void Pass( PassType aPassType, PassProc aPassProc )
{
	Open();
//	Window.PutOnTop(Win.CacheWin);
//	Window.Clear();
	switch ( aPassType )
	{
		case MarkeerWit: markeerWitPass( aPassProc ); break;
		case MarkeerZwart: markeerZwartPass( aPassProc ); break;
		case WitEnZwart: markeerWitEnZwartPass( aPassProc ); break;
	}
	Close(); // @@NOG Is dit geen onnodig tijdverlies?
}
}
