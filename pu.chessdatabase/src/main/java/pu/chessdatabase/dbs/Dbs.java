package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.Kleur .*;
import static pu.chessdatabase.dbs.Resultaat.*;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.ReportFunction;
import pu.services.Matrix;
import pu.services.Range;
import pu.services.Vector;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@Data
public class Dbs
{
//public static final int MAX_RESULTAAT_TYPE = 4;
public static void iterateOverKleurEnResultaat( IterateOverKleurEnResultaatFunction aIterateOverKleurEnResultaatFunction )
{
	for ( Kleur kleur : Kleur.values() )
	{
		for ( Resultaat resultaat : Resultaat.values() )
		{
			aIterateOverKleurEnResultaatFunction.doPass( kleur, resultaat );
		}
	}
}
public static Range VELD_RANGE = new Range( 0, 0x77 );
public static Range RESULTAAT_RANGE = new Range( 0, 3 );

private final VM vm;
private final VMStellingIterator vmStellingIterator;
private final Config config;

public Dbs( VM aVm, Config aConfig )
{
	super();
	vm = aVm;
	vmStellingIterator = null;
	config = aConfig;
}

@Autowired
public Dbs( VM aVm, VMStellingIterator aVmStellingIterator, Config aConfig )
{
	super();
	vm = aVm;
	vmStellingIterator = aVmStellingIterator;
	config = aConfig;
}
public void setReport( int aReportFrequency, ReportFunction aReportFunction )
{
	vmStellingIterator.setReport( aReportFrequency, aReportFunction );
}
public void setDoAllPositions( boolean aDoAllPositions )
{
	vmStellingIterator.setDoAllPositions( aDoAllPositions );
}

/**
 * ------- Naam geven -------------------
 */
public String getDatabaseName()
{
	return vm.getDatabaseName();
}
public long getDatabaseSize()
{
	return vm.getDatabaseSize();
}
public void setDatabaseName( String aDatabaseName )
{
	vm.setDatabaseName( aDatabaseName );
}
public Transformator getTransformator()
{
	return getConfig().getTransformator();
}
/**
 *----------- Schrijven ----------------- 
 */
public void put( BoStelling aBoStelling )
{
	int VMRec = 0;
	VMStelling vmStelling = getTransformator().boStellingToVmStelling( aBoStelling );
	switch ( aBoStelling.getResultaat() )
	{
		case Illegaal: 
			VMRec = VM.VM_ILLEGAAL; break;
		// Waarom worden schaakjes als remise gezien?
		// ==> Omdat ze alleen in pass_0 VM_SCHAAK krijgen en dat betekent dat de stelling weliswaar remise is,
		//     maar wel een potentiele matkandidaat
		case Remise  : VMRec = aBoStelling.isSchaak() ? VM.VM_SCHAAK : VM.VM_REMISE; break;
		case Gewonnen: VMRec = aBoStelling.getAantalZetten(); break;
		case Verloren: VMRec = aBoStelling.getAantalZetten() + VM.VERLIES_OFFSET; break;
	}
	vm.put( vmStelling, VMRec );
	vmStellingIterator.addResultaat( aBoStelling );
}
/**
 * ----------- Lezen -----------------
 */
public BoStelling get( BoStelling aBoStelling )
{
	VMStelling vmStelling = getTransformator().boStellingToVmStelling( aBoStelling );
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

	// In de hele opbouwbeweging wordt niets met schaakjes gedaan, behalve in de nulde ronde, om matjes op te sporen
	boStelling.setSchaak( false );
	if ( VMrec == VM.VM_ILLEGAAL )
	{
		boStelling.setResultaat( Illegaal );
		boStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VM.VM_REMISE )
	{
		boStelling.setResultaat( Remise );
		boStelling.setAantalZetten( 0 );
	}
	else if ( VMrec == VM.VM_SCHAAK )
	{
		// Waarom worden schaakjes als remise gezien?
		// ==> Omdat ze alleen in pass_0 VM_SCHAAK krijgen en dat betekent dat de stelling weliswaar remise is,
		//     maar een potentiele matkandidaat
		boStelling.setResultaat( Remise );
		boStelling.setAantalZetten( 0 );
		boStelling.setSchaak( true );
	}
	else if ( VMrec < VM.VERLIES_OFFSET )
	{
		boStelling.setResultaat( Gewonnen );
		boStelling.setAantalZetten( VMrec );
	}
	else
	{
		boStelling.setResultaat( Verloren );
		boStelling.setAantalZetten( VMrec - VM.VERLIES_OFFSET );
	}
	return boStelling;
}
/**
 * ----------- Vrijgeven record ------------
 */
public void freeRecord( BoStelling aBoStelling )
{
	VMStelling vmStelling = getTransformator().boStellingToVmStelling( aBoStelling );
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
	open( "r" );
}
public void open( String aMode )
{
	vm.open( aMode );
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
 * --------- Pass over de remisestellingen met wit aan zet -------------
 */
void markeerWitPass( PassFunction aPassFunction )
{
	getVmStellingIterator().iterateOverWkZkOneColour( Wit, aPassFunction );
}
/**
 * --------- Pass over de remisestellingen met zwart aan zet -------------
 */
void markeerZwartPass( PassFunction aPassFunction )
{
	getVmStellingIterator().iterateOverWkZkOneColour( Zwart, aPassFunction );
}
/**
 * --------- Pass over alle stellingen -------------
 */
void markeerWitEnZwartPass( PassFunction aPassFunction )
{
	getVmStellingIterator().iterateOverWkZkAndKleur( aPassFunction );
}
void markeerParallel( PassFunction aPassFunction )
{
	getVmStellingIterator().iterateParallel( aPassFunction );
}
public void pass( PassType aPassType, PassFunction aPassFunction )
{
	pass( aPassType, aPassFunction, "r" );
}
public void pass( PassType aPassType, PassFunction aPassFunction, String aOpenMode )
{
	open( aOpenMode );
	switch ( aPassType )
	{
		case MarkeerWit: markeerWitPass( aPassFunction ); break;
		case MarkeerZwart: markeerZwartPass( aPassFunction ); break;
		case MarkeerWitEnZwart: markeerWitEnZwartPass( aPassFunction ); break;
		case MarkeerParallel: markeerParallel( aPassFunction );
	}
	close();
}
}
