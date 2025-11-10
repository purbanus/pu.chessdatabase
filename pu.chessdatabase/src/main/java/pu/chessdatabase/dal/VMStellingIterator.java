package pu.chessdatabase.dal;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dal.ResultaatType.*;

import java.util.ArrayList;
import java.util.List;

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
	return new int[Kleur.values().length][ResultaatType.values().length];
}

@Autowired private VM vm;
private Dbs dbs;
@Autowired private Config config;
private int [][] tellingen = newTellingen();
private int stellingTeller;
private int reportFrequency;
private ReportFunction reportFunction;
private boolean doAllPositions = false;

VMStellingIterator( @Lazy Dbs aDbs )
{
	super();
	dbs = aDbs;
}
public void clearTellingen()
{
	tellingen = newTellingen();
	stellingTeller = 0;
}
public void setReport( int aReportFrequency, ReportFunction aReportFunction, boolean aDoAllPositions )
{
	reportFrequency = aReportFrequency;
	reportFunction = aReportFunction;
	doAllPositions = aDoAllPositions;
	clearTellingen();
}
public void iterateOverWkZk( VMSimpleIteratorFunction aVmIteratorFunction )
{
	// @@HIGH Moet je hier niet over AanZet itereren?
	VMStelling vmStelling = new VMStelling();
	for ( int wk = 0; wk < 10; wk++)
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			aVmIteratorFunction.doPass( vmStelling );
		}
	}
	reportFunction.doReport( stellingTeller, tellingen );
}
public void iterateOverWkZkWit( PassFunction aPassFunction )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( WIT );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( WIT );
	for ( int zk = 0; zk < 64; zk++ )
	{
		vmStelling.setZk( zk );
		boStelling.setZk( Dbs.CVT_STUK[zk] );
		for ( int wk = 0; wk < 10; wk++ )
		{
			vmStelling.setWk( wk );
			boStelling.setWk( Dbs.CVT_WK[wk] );
			iterateOverPiecesOnlyWhite( boStelling, vmStelling, aPassFunction );
		}
	}
	reportFunction.doReport( stellingTeller, tellingen );

}
public void iterateOverWkZk( Kleur aKleur, PassFunction aPassFunction, VMIteratorFunction aVmIteratorFunction )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( aKleur );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( aKleur );
	for ( int zk = 0; zk < 64; zk++ )
	{
		vmStelling.setZk( zk );
		boStelling.setZk( Dbs.CVT_STUK[zk] );
		for ( int wk = 0; wk < 10; wk++ )
		{
			vmStelling.setWk( wk );
			boStelling.setWk( Dbs.CVT_WK[wk] );
			aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
		}
	}
}

// @@HIGH Je kunt dit aanroepen in iterateOverAllPieces
public void iterateOverPieces( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction, VMIteratorFunction aVmIteratorFunction )
{
	BoStelling boStelling = aBoStelling.clone();
	VMStelling vmStelling = aVmStelling.clone();
	for ( int s3 = 0; s3 < 64; s3++ )
	{
		vmStelling.setS3( s3 );
		boStelling.setS3( Dbs.CVT_STUK[s3] );
		if ( getConfig().getAantalStukken() == 3 )
		{
			callForAllPiecesOnlyWhite( boStelling, vmStelling, aPassFunction );
		}
		else
		{
			for ( int s4 = 0; s4 < 64; s4++ )
			{
				vmStelling.setS4( s4 );
				boStelling.setS4( Dbs.CVT_STUK[s4] );
				if ( getConfig().getAantalStukken() == 4 )
				{
					callForAllPiecesOnlyWhite( boStelling, vmStelling, aPassFunction );
				}
				else
				{
					for ( int s5 = 0; s5 < 64; s5++ )
					{
						vmStelling.setS5( s5 );
						boStelling.setS5( Dbs.CVT_STUK[s5] );
						callForAllPiecesOnlyWhite( aBoStelling, aVmStelling, aPassFunction );
					}
				}
			}
		}
	}
	// Waar is dit voor nodig? 
	// --> Voor die freeRecord. Alle drie de stukken zijn 0x40 en dat is niet legaal
	vmStelling.setS3( 0 );
	vmStelling.setS4( 0 );
	vmStelling.setS5( 0 );
	vm.freeRecord( vmStelling );
}
public void iterateOverAllPieces( PassFunction aPassFunction )
{
	VMStelling vmStelling = new VMStelling();
	BoStelling boStelling = new BoStelling();
	for ( int wk = 0; wk < 10; wk++ )
	{
		vmStelling.setWk( wk );
		boStelling.setWk( Dbs.CVT_WK[wk] );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			boStelling.setZk( Dbs.CVT_STUK[zk] );
			for ( Kleur aanZet : Kleur.values() )
			{
				vmStelling.setAanZet( aanZet );
				boStelling.setAanZet( aanZet );
				for ( int s3 = 0; s3 < 64; s3++ )
				{
					vmStelling.setS3( s3 );
					boStelling.setS3( Dbs.CVT_STUK[s3] );
					if ( getConfig().getAantalStukken() == 3 )
					{
						callForAllPieces( boStelling, vmStelling, aPassFunction );
					}
					else
					{
						for ( int s4 = 0; s4 < 64; s4++ )
						{
							vmStelling.setS4( s4 );
							boStelling.setS4( Dbs.CVT_STUK[s4] );
							if ( getConfig().getAantalStukken() == 4 )
							{
								callForAllPieces( boStelling, vmStelling, aPassFunction );
							}
							else
							{
								for ( int s5 = 0; s5 < 64; s5++ )
								{
									vmStelling.setS5( s5 );
									boStelling.setS5( Dbs.CVT_STUK[s5] );
									callForAllPieces( boStelling, vmStelling, aPassFunction );
								}
							}
						}
					}
				}
			}
		}
	}
	reportFunction.doReport( stellingTeller, tellingen );
}
List<BoStelling> stellingen = new ArrayList<>();
void callForAllPieces( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction )
{
	BoStelling gotBoStelling = dbs.getDirect( aVmStelling, aBoStelling );
	if ( HOU_STELLINGEN_BIJ )
	{
		stellingen.add( gotBoStelling );
	}
	tellingen [gotBoStelling.getAanZet().ordinal()][gotBoStelling.getResultaat().ordinal()]++;
	stellingTeller++;
	if ( getReportFunction() != null && stellingTeller % reportFrequency == 0 )
	{
		reportFunction.doReport( stellingTeller, tellingen );
	}
	if ( gotBoStelling.getResultaat() == REMISE || isDoAllPositions() )
	{
		aPassFunction.doPass( gotBoStelling );
	}
}
void callForAllPiecesOnlyWhite( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassFunction )
{
	BoStelling gotBoStelling = dbs.getDirect( aVmStelling, aBoStelling );
	if ( HOU_STELLINGEN_BIJ )
	{
		stellingen.add( gotBoStelling );
	}
	tellingen [WIT.ordinal()][gotBoStelling.getResultaat().ordinal()]++;
	tellingen [ZWART.ordinal()][gotBoStelling.getResultaat().ordinal()]++;
	stellingTeller++;
	stellingTeller++;
	if ( getReportFunction() != null && stellingTeller % reportFrequency == 0 )
	{
		reportFunction.doReport( stellingTeller, tellingen );
	}
	if ( gotBoStelling.getResultaat() == REMISE || isDoAllPositions() )
	{
		aPassFunction.doPass( gotBoStelling );
	}
}
public void addResultaat( BoStelling aBoStelling )
{
	int kleurOrdinal = aBoStelling.getAanZet().ordinal();
	tellingen[kleurOrdinal][REMISE.ordinal()]--;
	tellingen[kleurOrdinal][aBoStelling.getResultaat().ordinal()]++;
}
}
