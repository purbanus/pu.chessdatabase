package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.springframework.beans.factory.annotation.Autowired;
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

private Dbs dbs;
private final Config config;
private int [][] tellingen = newTellingen();
private int stellingTeller;
List<BoStelling> stellingen = new ArrayList<>();
private int reportFrequency;
private ReportFunction reportFunction;
private boolean doAllPositions = false;

public VMStellingIterator( Config aConfig )
{
	super();
	config = aConfig;
}
@Autowired
VMStellingIterator( @Lazy Dbs aDbs, Config aConfig )
{
	super();
	dbs = aDbs;
	config = aConfig;
}
public Transformator getTransformator()
{
	return getConfig().getTransformator();
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
	if ( getReportFunction() != null )
	{
		getReportFunction().doReport( stellingTeller, tellingen );
	}
}
public void iterateParallel( PassFunction aPassFunction )
{
	List<VMIterateAction> actions = new ArrayList<>();
	for ( int wk : getConfig().heeftPionnen() ? STUK_VELD_RANGE : WK_VELD_RANGE )
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
	boStelling.setWk( getTransformator().vmStellingWkToBoStellingWk( aWk ) );
	vmStelling.setAanZet( aKleur );
	boStelling.setAanZet( aKleur );
	for ( int zk : STUK_VELD_RANGE )
	{
		vmStelling.setZk( zk );
		boStelling.setZk( getTransformator().vmStellingStukToBoStellingStuk( zk ) );
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
	for ( int wk : getConfig().heeftPionnen() ? STUK_VELD_RANGE : WK_VELD_RANGE )
	{
		vmStelling.setWk( wk );
		boStelling.setWk( getTransformator().vmStellingWkToBoStellingWk( wk ) );
		for ( int zk : STUK_VELD_RANGE )
		{
			vmStelling.setZk( zk );
			boStelling.setZk( getTransformator().vmStellingStukToBoStellingStuk( zk ) );
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
	for ( int wk : getConfig().heeftPionnen() ? STUK_VELD_RANGE : WK_VELD_RANGE )
	{
		vmStelling.setWk( wk );
		boStelling.setWk( getTransformator().vmStellingWkToBoStellingWk( wk ) );
		for ( int zk : STUK_VELD_RANGE )
		{
			vmStelling.setZk( zk );
			boStelling.setZk( getTransformator().vmStellingStukToBoStellingStuk( zk ) );
			iterateOverPieces( boStelling, vmStelling, aPassFunction, true );
		}
	}
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
	for ( int s3 :getConfig().getStukken().isS3Pion() ? PION_VELD_RANGE : STUK_VELD_RANGE )
	{
		vmStelling.setS3( s3 );
		boStelling.setS3( getTransformator().vmStellingStukToBoStellingStuk( s3 ) );
		if ( getConfig().getAantalStukken() == 3 )
		{
			callForAllPieces( boStelling, vmStelling, aPassFunction, aCountDouble );
		}
		else
		{
			for ( int s4 :getConfig().getStukken().isS3Pion() ? PION_VELD_RANGE : STUK_VELD_RANGE )
			{
				vmStelling.setS4( s4 );
				boStelling.setS4( getTransformator().vmStellingStukToBoStellingStuk( s4 ) );
				if ( getConfig().getAantalStukken() == 4 )
				{
					callForAllPieces( boStelling, vmStelling, aPassFunction, aCountDouble );
				}
				else
				{
					for ( int s5 :getConfig().getStukken().isS3Pion() ? PION_VELD_RANGE : STUK_VELD_RANGE )
					{
						vmStelling.setS5( s5 );
						boStelling.setS5( getTransformator().vmStellingStukToBoStellingStuk( s5 ) );
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
		tellingen [Wit  .ordinal()][gotBoStelling.getResultaat().ordinal()]++;
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
