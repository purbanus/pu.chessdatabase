package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.Resultaat.*;
import static pu.chessdatabase.dbs.VM.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.ReportFunction;

import lombok.Data;

@Component
@Data
public class VMStellingIterator
{
public static final boolean HOU_STELLINGEN_BIJ = false;
public static int [][] cloneArray( int [][] aArrayToClone )
{
	return new int [][] { { aArrayToClone[0][0], aArrayToClone[0][1], aArrayToClone[0][2], aArrayToClone[0][3] }, { aArrayToClone[1][0], aArrayToClone[1][1], aArrayToClone[1][2], aArrayToClone[1][3] } };
	// Deze werken niet voor tweedimensionale arrays
	//return aArrayToClone.clone();
	//return Arrays.copyOf( aArrayToClone, 4 );
}
public static int [][] newTellingen()
{
	return new int[Kleur.values().length][Resultaat.values().length];
}

private VM vm;
private Dbs dbs;
private Config config;
private int [][] tellingen = newTellingen();
private int stellingTeller;
List<BoStelling> stellingen = new ArrayList<>();
private int reportFrequency;
private ReportFunction reportFunction;
private boolean doAllPositions = false;

VMStellingIterator( @Lazy Dbs aDbs, VM aVm, Config aConfig )
{
	super();
	dbs = aDbs;
	vm = aVm;
	config = aConfig;
}
public void clearTellingen()
{
	tellingen = newTellingen();
	stellingTeller = 0;
}
public void setReport( int aReportFrequency, ReportFunction aReportFunction )
{
	reportFrequency = aReportFrequency;
	reportFunction = aReportFunction;
	clearTellingen();
}
void report()
{
	if ( reportFunction != null )
	{
		reportFunction.doReport( stellingTeller, tellingen );
	}
}
public void iterateParallel( PassFunction aPassFunction )
{
	List<VMIterateAction> actions = new ArrayList<>();
	for ( int wk : getVm().getWkVeldRange() )
	{
		actions.add( new VMIterateAction( this, aPassFunction, wk ) );
	}
    ForkJoinTask.invokeAll(actions );
}
/**
 * This iterates over zk with One Kleur. It is used by the parallelIterators.
 * @param aPassFunction The function that is called after each complete set of values
 */
public void iterateOverZkOneColor( int aWk, Kleur aKleur, PassFunction aPassFunction )
{
	VMStelling vmStelling = new VMStelling();
	BoStelling boStelling = new BoStelling();
	vmStelling.setWk( aWk );
	boStelling.setWk( Dbs.CVT_WK[aWk] );
	vmStelling.setAanZet( aKleur );
	boStelling.setAanZet( aKleur );
	for ( int zk : getVm().getStukVeldRange() )
	{
		vmStelling.setZk( zk );
		boStelling.setZk( Dbs.CVT_STUK[zk] );
		iterateOverPieces( boStelling, vmStelling, aPassFunction, true );
	}
	report();
}
/**
 * This iterates over wk and zk and Kleur. It is used by Dbs.markeerWitEnZwartPass, amongst others
 * @param aPassFunction The function that is called after each complete set of values
 */
public void iterateOverWkZkAndKleur( PassFunction aPassFunction )
{
	VMStelling vmStelling = new VMStelling();
	BoStelling boStelling = new BoStelling();
	for ( int wk : getVm().getWkVeldRange() )
	{
		vmStelling.setWk( wk );
		boStelling.setWk( Dbs.CVT_WK[wk] );
		for ( int zk : getVm().getStukVeldRange() )
		{
			vmStelling.setZk( zk );
			boStelling.setZk( Dbs.CVT_STUK[zk] );
			for ( Kleur aanZet : Kleur.values() )
			{
				vmStelling.setAanZet( aanZet );
				boStelling.setAanZet( aanZet );
				iterateOverPieces( boStelling, vmStelling, aPassFunction, false );
			}
		}
	}
	report();
}

/**
 * This iterates over wk and zk using one Kleur. It is used by Dbs.markeerWitPass and Dbs.markeeZwartPass,
 * amongst others
 * @param aKleur The Kleur for this iterator: Wit or Zwart.
 * @param aPassFunction The function that is called after each complete set of values
 */
public void iterateOverWkZkOneColour( Kleur aKleur, PassFunction aPassFunction )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( aKleur );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( aKleur );
	for ( int wk : getVm().getWkVeldRange() )
	{
		vmStelling.setWk( wk );
		boStelling.setWk( Dbs.CVT_WK[wk] );
		for ( int zk : getVm().getStukVeldRange() )
		{
			vmStelling.setZk( zk );
			boStelling.setZk( Dbs.CVT_STUK[zk] );
			iterateOverPieces( boStelling, vmStelling, aPassFunction, true );
		}
	}
	// Waar is dit voor nodig? 
	// --> Voor die freeRecord. Alle drie de stukken zijn 0x40 en dat is niet legaal
	vmStelling.setS3( 0 );
	vmStelling.setS4( 0 );
	vmStelling.setS5( 0 );
	vm.freeRecord( vmStelling );
	report();
}

/**
 * This iterates over the pieces other than wk, zk and Kleur. It is used only internally
 * @param aBoStelling The partially built BoStelling.
 * @param aVmStelling The partially built VMStelling.
 * @param aPassFunction The function that is called after each complete set of values
 * @Param aCountDouble Whether this Stelling should be counted doubly. It is <code>true</code> when we're iterating with only one color,
 * and <code>false</code> when we're iterating over all colors 
 */
void iterateOverPieces( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction, boolean aCountDouble )
{
	BoStelling boStelling = aBoStelling.clone();
	VMStelling vmStelling = aVmStelling.clone();
	for ( int s3 : getVm().getStukVeldRange() )
	{
		vmStelling.setS3( s3 );
		boStelling.setS3( Dbs.CVT_STUK[s3] );
		if ( getConfig().getAantalStukken() == 3 )
		{
			callForAllPieces( boStelling, vmStelling, aPassFunction, aCountDouble );
		}
		else
		{
			for ( int s4 : getVm().getStukVeldRange() )
			{
				vmStelling.setS4( s4 );
				boStelling.setS4( Dbs.CVT_STUK[s4] );
				if ( getConfig().getAantalStukken() == 4 )
				{
					callForAllPieces( boStelling, vmStelling, aPassFunction, aCountDouble );
				}
				else
				{
					for ( int s5 : getVm().getStukVeldRange() )
					{
						vmStelling.setS5( s5 );
						boStelling.setS5( Dbs.CVT_STUK[s5] );
						callForAllPieces( boStelling, vmStelling, aPassFunction, aCountDouble );
					}
				}
			}
		}
	}
}
void callForAllPieces( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction, boolean aCountDouble )
{
	BoStelling gotBoStelling = dbs.getDirect( aVmStelling, aBoStelling );
	if ( HOU_STELLINGEN_BIJ )
	{
		stellingen.add( gotBoStelling );
	}
	if ( aCountDouble )
	{
		tellingen [Wit.ordinal()][gotBoStelling.getResultaat().ordinal()]++;
		tellingen [Zwart.ordinal()][gotBoStelling.getResultaat().ordinal()]++;
		stellingTeller++;
	}
	else
	{
		tellingen [gotBoStelling.getAanZet().ordinal()][gotBoStelling.getResultaat().ordinal()]++;
	}
	stellingTeller++;
	
	if ( stellingTeller % reportFrequency == 0 )
	{
		report();
	}
	if ( gotBoStelling.getResultaat() == Remise || isDoAllPositions() )
	{
		aPassFunction.doPass( gotBoStelling );
	}
}
public void addResultaat( BoStelling aBoStelling )
{
	int kleurOrdinal = aBoStelling.getAanZet().ordinal();
	tellingen[kleurOrdinal][Remise.ordinal()]--;
	tellingen[kleurOrdinal][aBoStelling.getResultaat().ordinal()]++;
}
}
