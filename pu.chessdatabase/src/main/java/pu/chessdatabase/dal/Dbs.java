package pu.chessdatabase.dal;

import static pu.chessdatabase.bo.Kleur.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.ReportFunction;
import pu.chessdatabase.util.Matrix;
import pu.chessdatabase.util.Range;
import pu.chessdatabase.util.Vector;

@Component
public class Dbs
{
public static final int MAX_RESULTAAT_TYPE = 4;
public static final int OKTANTEN = 8;

public static final String DFT_DBS_NAAM = "KDKT.DBS";
public static final int DFT_RPT_FREQ = 4096;

/**==============================================================================================================
* Konversie WK notatie van VM naar Gen
*==============================================================================================================*/
// De WK moet in het eerste oktant zitten, dwz de veldwaarde moet tussen 0 en 9 zitten
// De CVT_WK transformeert hem dan naar een van de velden
// a1, b1, c1, d1,   0, 1, 2, 3  
//     b2, c2, d3,      4, 5, 6
//         c3, d3,         7, 8
//             d4,            9
public static final int [] CVT_WK = {
	0x00,0x01,0x02,0x03,
		 0x11,0x12,0x13,
			  0x22,0x23,
				   0x33
};
/**==============================================================================================================
* Konversie stuk (niet-WK) notatie van VM naar Zgen
*==============================================================================================================*/
public static final int [] CVT_STUK = {
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
* oktant 1 - Identieke transformatie
* oktant 2 - Spiegeling in de y-as
* oktant 3 - Rotatie van -90 graden
* oktant 4 - Spiegeling in de diagonaal a8-h1
* oktant 5 - Spiegeling in de x-as gevolgd door een spiegeling in de y-as,
*            oftewel een rotatie over 180 graden
* oktant 6 - Spiegeling in de x-as
* oktant 7 - Rotatie over +90 graden
* oktant 8 - Spiegeling in de diagonaal a1-h8
*==============================================================================================================*/
public static final int [] OKTANTEN_TABEL = {
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   6,6,6,6,5,5,5,5
};
/*
 * Zo ziet hij eruit met de a-lijn onderaan
   6,6,6,6,5,5,5,5
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
 */

/**========================================================================================
* Transformatietabel voor WK. Nadat WK is getransformeerd naar het juiste oktant,
* moet hij nog naar de speciale VM-kodering (0..9) worden gebracht. Dat gebeurt hiermee
* 10 = foutkode, wordt in VM op getest.
*========================================================================================*/
public static final int [] TRANSFORM_WK = {
	 0, 1, 2, 3,10,10,10,10,
	10, 4, 5, 6,10,10,10,10,
	10,10, 7, 8,10,10,10,10,
	10,10,10, 9,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10,
	10,10,10,10,10,10,10,10
};
public static final Matrix [] MATRIX_TABEL = {
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
public static final Vector [] TRANSLATIE_TABEL = new Vector [] {
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

@Autowired private VM vm;

//Range<Integer> Veld = Range.of( 0, 0x77 );
//Range<Integer> OKtant = Range.of( 1, OKTANTEN );
//Range<Integer> OKtant_0 = Range.of( 0, OKTANTEN );
//Range<Integer> ResultaatRange = Range.of( 0, 3 );

Range veldRange = new Range( 0, 0x77 );
Range oktantRange = new Range( 1, OKTANTEN );
Range oktant_0_Range = new Range( 0, OKTANTEN );
Range resultaatRange = new Range( 0, 3 );

int[][] transformatieTabel = new int [OKTANTEN + 1][veldRange.getMaximum() + 1];
private String dbsNaam = null;
public long [] report = new long [4];
long [] reportArray = new long [4];
int reportTeller;
int reportFrequentie;
ReportFunction reportProc;

public Dbs()
{
	createTransformatieTabel();
	reportFrequentie = DFT_RPT_FREQ;
	reportProc = null;
	clearTellers();
}
public String getDbsNaam()
{
	if ( dbsNaam == null )
	{
		dbsNaam = DFT_DBS_NAAM;
	}
	return dbsNaam;
}
/**
*PROCEDURE Name(Naam: ARRAY OF CHAR);
BEGIN
	Str.Copy(DbsNaam, Naam);
END Name;
 */
/**
 * ------- Naam geven -------------------
 */
public void setDbsNaam( String aNaam )
{
	dbsNaam = aNaam;
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
public void clearTellers()
{
	for ( int x = 0; x < 4; x++ )
	{
		report[x] = 0L;
	}
	reportTeller = 0;
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
public long [] getTellers()
{
	return new long [] { report[0], report[1], report[2], report[3] };
}
/**
PROCEDURE SetReport(Freq: CARDINAL; R: ReportProc);
BEGIN
	RptFreq:=Freq;
	RptTeller:=0;
	RptProc:=R;
END SetReport;
 */
public void setReport( int aFrequency, ReportFunction aReportProc )
{
	reportFrequentie = aFrequency;
	reportTeller = 0;
	reportProc = aReportProc;
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
public void updateTellers( ResultaatType aResultaatType )
{
	if ( reportProc != null )
	{
		report[aResultaatType.ordinal()]++;
		reportTeller++;
		if ( reportTeller >= reportFrequentie )
		{
			reportTeller = 0;
			reportProc.doReport( report );
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
public void createTransformatieTabel()
{
	Vector Vres;
	for ( int oktant = oktantRange.getMinimum(); oktant <= oktantRange.getMaximum(); oktant++ ) // @@NOG CHECK is die grens goed of moet het <= zijn
	{
		for ( int rij = 0; rij < 8; rij++ )
		{
			for ( int kol = 0; kol < 8; kol++ )
			{
				Vres = new Vector( kol, rij );
				Vres = MATRIX_TABEL[oktant].multiply( Vres );
				Vres = Vres.add( TRANSLATIE_TABEL[oktant] );
				int oudVeld = kol + 16 * rij;
				int newVeld = Vres.get( 0 ) + 8 * Vres.get( 1 );
				transformatieTabel[oktant][oudVeld] = newVeld;
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
public VMStelling cardinaliseer( BoStelling aStelling )
{
	int oktant = OKTANTEN_TABEL[aStelling.getWk()];
	int trfWk = transformatieTabel[oktant][aStelling.getWk()];
	@SuppressWarnings( "unused" )
	int trftrfWk = TRANSFORM_WK[trfWk];
	
	VMStelling vmStelling = spiegelEnRoteer( aStelling );
	vmStelling.setWk( TRANSFORM_WK[ vmStelling.getWk()] );
	return vmStelling;
//	int okt = OKTANTEN_TABEL[aStelling.getWk()];
//	if ( okt == 0 )
//	{
//		throw new RuntimeException( "Foutief oktant in Dbs.Cardinaliseer voor WK op " + Integer.toHexString( aStelling.getWk() ) );
//	}
//	int trfWk = transformatieTabel[okt][aStelling.getWk()];
//	int trftrfWk = TRANSFORM_WK[trfWk];
//	VMStelling vmStelling = VMStelling.builder()
//		.wk( TRANSFORM_WK[transformatieTabel[okt][aStelling.getWk()]] )
//		.zk( transformatieTabel[okt][aStelling.getZk()] )
//		.s3( transformatieTabel[okt][aStelling.getS3()] )
//		.s4( transformatieTabel[okt][aStelling.getS4()] )
//		.aanZet( aStelling.getAanZet() )
//		.build();
//	return vmStelling;
}
public VMStelling spiegelEnRoteer( BoStelling aStelling )
{
	int oktant = getOktant( aStelling );
	return spiegelEnRoteer( aStelling, oktant );
}
VMStelling spiegelEnRoteer( BoStelling aStelling, int aOktant )
{
	return VMStelling.builder()
		.wk( transformatieTabel[aOktant][aStelling.getWk()] )
		.zk( transformatieTabel[aOktant][aStelling.getZk()] )
		.s3( transformatieTabel[aOktant][aStelling.getS3()] )
		.s4( transformatieTabel[aOktant][aStelling.getS4()] )
		.aanZet( aStelling.getAanZet() )
		.build();
}

int getOktant( BoStelling aStelling )
{
	int oktant = OKTANTEN_TABEL[aStelling.getWk()];
	if ( oktant < 1 || oktant > 8)
	{
		throw new RuntimeException( "Foutief oktant in Dbs.spiegelEnRoteer voor WK op " + Integer.toHexString( aStelling.getWk() ) );
	}
	return oktant;
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
public void put( BoStelling aStelling )
{
	int VMRec = 0;
	VMStelling vmStelling = cardinaliseer( aStelling );
	switch ( aStelling.getResultaat() )
	{
		case ILLEGAAL: VMRec = VM.VM_ILLEGAAL; break;
		// Waarom worden schaakjes als remise gezien?
		// ==> Omdat ze alleen in pass_0 VM_SCHAAK krijgen en dat betekent dat de stelling remise is,
		//     maar een potentiele matkandidaat
		case REMISE  : VMRec = aStelling.isSchaak() ? VM.VM_SCHAAK : VM.VM_REMISE; break;
		case GEWONNEN: VMRec = aStelling.getAantalZetten(); break;
		case VERLOREN: VMRec = aStelling.getAantalZetten() + VM.VERLIES_OFFSET; break;
	}
	updateTellers( aStelling.getResultaat() );
	vm.put( vmStelling, VMRec );
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
public BoStelling get( BoStelling aStelling )
{
	VMStelling vmStelling = cardinaliseer( aStelling );
	return getDirect( vmStelling, aStelling );
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
// @@NOG Die parm aBoStelling elimineren en gewoon een verse BoStelling retourneren
//       Bijv vmStelling.getBoStelling();
BoStelling getDirect( VMStelling aVMStelling, BoStelling aBoStelling )
{
	BoStelling boStelling = aBoStelling.clone();
	int VMrec = vm.get( aVMStelling );
	// @@NOG Erg onhandig! Je kunt hier niet Gen gebruiken want dan krijg je een circulaire 
	//       referentie: Gen gebruikt Dbs en Dbs gebruikt dan ook Gen. Er zijn twee oplossingen:
	//       - De isSchaak uit Gen tillen en in een aparte class stoppen (ik weet trouwens niet of
	//         dat gaat werken, of je dan geen circulaire referentie hebt.
	//       - Overal waar getDirect gebruikt wordt, isSchaak() aanroepen
	//aBoStelling.setSchaak( gen.isSchaak( aBoStelling) );
	boStelling.setSchaak( false );
	if ( VMrec == VM.VM_ILLEGAAL )
	{
		boStelling.setResultaat( ResultaatType.ILLEGAAL );
		boStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VM.VM_REMISE )
	{
		boStelling.setResultaat( ResultaatType.REMISE );
		boStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VM.VM_SCHAAK )
	{
		// Waarom worden schaakjes als remise gezien?
		// ==> Omdat ze alleen in pass_0 VM_SCHAAK krijgen en dat betekent dat de stelling remise is,
		//     maar een potentiele matkandidaat
		boStelling.setResultaat( ResultaatType.REMISE );
		boStelling.setAantalZetten( 0 );
		boStelling.setSchaak( true );
	}
	else if ( VMrec < VM.VERLIES_OFFSET )
	{
		boStelling.setResultaat( ResultaatType.GEWONNEN );
		boStelling.setAantalZetten( VMrec );
	}
	else
	{
		boStelling.setResultaat( ResultaatType.VERLOREN );
		boStelling.setAantalZetten( VMrec - VM.VERLIES_OFFSET );
	}
	return boStelling;
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
public void freeRecord( BoStelling aBoStelling )
{
	VMStelling vmStelling = cardinaliseer( aBoStelling );
	vm.freeRecord( vmStelling );
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
public void name( String aNaam )
{
	dbsNaam = aNaam;
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
public void create()
{
	if ( getDbsNaam() == null || getDbsNaam().length() == 0 )
	{
		throw new RuntimeException( "Geen naam opgegeven voor de database" );
	}
	vm.create( getDbsNaam() );
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
public void open()
{
	if ( getDbsNaam() == null || getDbsNaam().length() == 0 )
	{
		throw new RuntimeException( "Geen naam opgegeven voor de database" );
	}
	vm.open( getDbsNaam() );
}
public void flush()
{
	vm.flush();
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
public void close()
{
	vm.close();
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
public void pass34( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassProc )
{
	BoStelling boStelling = aBoStelling.clone();
	VMStelling vmStelling = aVmStelling.clone();
	vmStelling.setS3( 0 );
	while ( vmStelling.getS3() < 64 )
	{
		boStelling.setS3( CVT_STUK[vmStelling.getS3()] );
		vmStelling.setS4( 0 );
		while ( vmStelling.getS4() < 64 )
		{
			boStelling.setS4( CVT_STUK[vmStelling.getS4()] ); // Nu wel

			// @@NOG CHECK is aBoStelling veranderd of moet je boStelling gebruiken? 
			BoStelling gotBoStelling = getDirect( vmStelling, boStelling ); //1 aBoStelling.s4 maakt nog niet uit
			// @@NOG Je kunt hier niet Gen.isSchaak() aanroependus moet het in de proc
			if ( gotBoStelling.getResultaat() == ResultaatType.REMISE )
			{
				aPassProc.doPass( gotBoStelling.clone() );
			}
			vmStelling.setS4( vmStelling.getS4() + 1 );
		}
		vmStelling.setS3( vmStelling.getS3() + 1 );
	}
	vmStelling.setS3( vmStelling.getS3() - 1 );
	vmStelling.setS4( vmStelling.getS4() - 1 );
	vm.freeRecord( vmStelling );
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
void markeerWitPass( PassFunction aPassProc )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( WIT );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( WIT );
	for ( int ZK = 0; ZK < 64; ZK++ )
	{
		vmStelling.setZk( ZK );
		boStelling.setZk( CVT_STUK[ZK] );
		for ( int WK = 0; WK < 10; WK++ )
		{
			vmStelling.setWk( WK );
			boStelling.setWk( CVT_WK[WK] );
			pass34( boStelling, vmStelling, aPassProc );
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
void markeerZwartPass( PassFunction aPassProc )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( ZWART );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( ZWART );
	for ( int WK = 0; WK < 10; WK++ )
	{
		vmStelling.setWk( WK );
		boStelling.setWk( CVT_WK[WK] );
		for ( int ZK = 0; ZK < 64; ZK++ )
		{
			vmStelling.setZk( ZK );
			boStelling.setZk( CVT_STUK[ZK] );
			pass34( boStelling, vmStelling, aPassProc );
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
void markeerWitEnZwartPass( PassFunction aPassProc )
{
	VMStelling vmStelling = new VMStelling();
	BoStelling boStelling = new BoStelling();
	vmStelling.setWk( 0 );
	while ( vmStelling.getWk() < 10 )
	{
		boStelling.setWk( CVT_WK[vmStelling.getWk()] );
		vmStelling.setZk( 0 );
		while ( vmStelling.getZk() < 64 )
		{
			boStelling.setZk( CVT_STUK[vmStelling.getZk()] );
			vmStelling.setS3( 0 );
			while ( vmStelling.getS3() < 64 )
			{
				boStelling.setS3( CVT_STUK[vmStelling.getS3()] );
				vmStelling.setS4( 0 );
				while ( vmStelling.getS4() < 64 )
				{
					boStelling.setS4( CVT_STUK[vmStelling.getS4()] );

					// Wit
					vmStelling.setAanZet( WIT );
					BoStelling gotBoStelling = getDirect( vmStelling, boStelling ); // @@NOG Is die gotStelling nodig??
					// @@NOG Je kunt hier niet Gen.isSchaak() aanroepen dus moet het in de proc
					gotBoStelling.setAanZet( WIT ); // @@NOG Waarom?
					aPassProc.doPass( gotBoStelling.clone() );
					
					// Zwart
					vmStelling.setAanZet( ZWART );
					gotBoStelling = getDirect( vmStelling, boStelling ); // @@NOG Is die gotStelling nodig??
					// @@NOG Je kunt hier niet Gen.isSchaak() aanroepen dus moet het in de proc
					gotBoStelling.setAanZet( ZWART ); // @@NOG Waarom?
					aPassProc.doPass( gotBoStelling.clone() );
		
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
public void pass( PassType aPassType, PassFunction aPassProc )
{
	open();
//	Window.PutOnTop(Win.CacheWin);
//	Window.Clear();
	switch ( aPassType )
	{
		case MARKEER_WIT: markeerWitPass( aPassProc ); break;
		case MARKEER_ZWART: markeerZwartPass( aPassProc ); break;
		case MARKEER_WIT_EN_ZWART: markeerWitEnZwartPass( aPassProc ); break;
	}
	close();
}
}
