package pu.chessdatabase.dal;

import static pu.chessdatabase.dal.ResultaatType.*;
import static pu.chessdatabase.bo.Kleur .*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.ReportFunction;
import pu.services.Matrix;
import pu.services.Range;
import pu.services.Vector;

@Component
public class Dbs
{
public static final int MAX_RESULTAAT_TYPE = 4;
public static final int OKTANTEN = 8;

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
@Autowired private VMStellingIterator vmStellingIterator;

//Range<Integer> Veld = Range.of( 0, 0x77 );
//Range<Integer> OKtant = Range.of( 1, OKTANTEN );
//Range<Integer> OKtant_0 = Range.of( 0, OKTANTEN );
//Range<Integer> ResultaatRange = Range.of( 0, 3 );

Range veldRange = new Range( 0, 0x77 );
Range oktantRange = new Range( 1, OKTANTEN );
//Range oktant_0_Range = new Range( 0, OKTANTEN );
Range resultaatRange = new Range( 0, 3 );

int[][] transformatieTabel = new int [OKTANTEN + 1][veldRange.getMaximum() + 1];
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
/**
 * ------- Naam geven -------------------
 */
public String getDatabaseName()
{
	return vm.getDatabaseName();
}
public void setDatabaseName( String aDatabaseName )
{
	vm.setDatabaseName( aDatabaseName );
}

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
 * ------------- Tellerstand uitlezen ---------------------
 */
public long [] getTellers()
{
	return new long [] { report[0], report[1], report[2], report[3] };
}
public void setReport( int aFrequency, ReportFunction aReportProc )
{
	reportFrequentie = aFrequency;
	reportTeller = 0;
	reportProc = aReportProc;
}
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
public void createTransformatieTabel()
{
	Vector Vres;
	for ( int oktant = oktantRange.getMinimum(); oktant <= oktantRange.getMaximum(); oktant++ )
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
		.s5( transformatieTabel[aOktant][aStelling.getS5()] )
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
 * ----------- Lezen -----------------
 */
public BoStelling get( BoStelling aBoStelling )
{
	VMStelling vmStelling = cardinaliseer( aBoStelling );
	return getDirect( vmStelling, aBoStelling );
}
/**
 * ----------- Lezen zonder cardinaliseren -------
 */
// Die parm aBoStelling elimineren en gewoon een verse BoStelling retourneren
// Bijv vmStelling.getBoStelling() ==> Nee dat kan niet wantVmStelling is een heel anderre stelling
// dan BoStelling ivm spiegelingen en rotaties.
BoStelling getDirect( VMStelling aVMStelling, BoStelling aBoStelling )
{
	BoStelling boStelling = aBoStelling.clone();
	int VMrec = vm.get( aVMStelling );
	// @@LOW Erg onhandig! Je kunt hier niet Gen gebruiken want dan krijg je een circulaire 
	//       referentie: Gen gebruikt Dbs en Dbs gebruikt dan ook Gen. Er zijn twee oplossingen:
	//       - De isSchaak uit Gen tillen en in een aparte class stoppen (ik weet trouwens niet of
	//         dat gaat werken, of je dan geen circulaire referentie hebt.
	// Overal waar getDirect gebruikt wordt, isSchaak() aanroepen ==> Nee want dat is erg slecht voor de performance.
	// In de hele opbouwbeweging wordt niets met schaakjes gedaan, behalve in de nulde ronde.
	boStelling.setSchaak( false );
	if ( VMrec == VM.VM_ILLEGAAL )
	{
		boStelling.setResultaat( ILLEGAAL );
		boStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VM.VM_REMISE )
	{
		boStelling.setResultaat( REMISE );
		boStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VM.VM_SCHAAK )
	{
		// Waarom worden schaakjes als remise gezien?
		// ==> Omdat ze alleen in pass_0 VM_SCHAAK krijgen en dat betekent dat de stelling remise is,
		//     maar een potentiele matkandidaat
		boStelling.setResultaat( REMISE );
		boStelling.setAantalZetten( 0 );
		boStelling.setSchaak( true );
	}
	else if ( VMrec < VM.VERLIES_OFFSET )
	{
		boStelling.setResultaat( GEWONNEN );
		boStelling.setAantalZetten( VMrec );
	}
	else
	{
		boStelling.setResultaat( VERLOREN );
		boStelling.setAantalZetten( VMrec - VM.VERLIES_OFFSET );
	}
	return boStelling;
}
/**
 * ----------- Vrijgeven record ------------
 */
public void freeRecord( BoStelling aBoStelling )
{
	VMStelling vmStelling = cardinaliseer( aBoStelling );
	vm.freeRecord( vmStelling );
}
/**
 *  ------- Creeren nieuwe database ------
 */
public void create()
{
	vm.create();
}
/**
 * ------- Openen database --------------
 */
public void open()
{
	vm.open();
}
public void flush()
{
	vm.flush();
}
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
 * --------- Pass over stukken 3, 4 en 5 ----------------------------------
 */
public void pass345( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction )
{
	vmStellingIterator.iterateOverPieces( aBoStelling, aVmStelling, aPassFunction, this::call345 );
}
void call345( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction )
{
	BoStelling gotBoStelling = getDirect( aVmStelling, aBoStelling );
	// @@NOG Je kunt hier niet Gen.isSchaak() aanroepen dus moet het in de proc
	if ( gotBoStelling.getResultaat() == ResultaatType.REMISE )
	{
		aPassFunction.doPass( gotBoStelling.clone() );
	}
}
/**
 * --------- Pass over de remisestellingen met wit aan zet -------------
 */
void markeerWitPass( PassFunction aPassFunction )
{
	vmStellingIterator.iterateOverWkZkWit( aPassFunction, this::callPass345 );
}
void callPass345( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction )
{
	pass345( aBoStelling, aVmStelling, aPassFunction );
}
/**
 * --------- Pass over de remisestellingen met zwart aan zet -------------
 */
void markeerZwartPass( PassFunction aPassFunction )
{
	vmStellingIterator.iterateOverWkZk( ZWART, aPassFunction, this::callPass345 );
}
/**
 * --------- Pass over alle stellingen -------------
 */
void markeerWitEnZwartPass( PassFunction aPassFunction )
{
	vmStellingIterator.iterateOverAllPieces( aPassFunction, this::callWitEnZwart );
}

void callWitEnZwart( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction )
{
	BoStelling gotBoStelling = getDirect( aVmStelling, aBoStelling );
	// Je kunt hier niet Gen.isSchaak() aanroepen dus moet het in de proc
	// Je wilt het ook niet aanroepen want in de opbouwbeweging worden schaakjes niet gebruikt behalve in pass 0

	aPassFunction.doPass( gotBoStelling.clone() );
}
public void pass( PassType aPassType, PassFunction aPassProc )
{
	open();
	switch ( aPassType )
	{
		case MARKEER_WIT: markeerWitPass( aPassProc ); break;
		case MARKEER_ZWART: markeerZwartPass( aPassProc ); break;
		case MARKEER_WIT_EN_ZWART: markeerWitEnZwartPass( aPassProc ); break;
	}
	close();
}
}
