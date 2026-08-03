package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.configuraties.ConfigImpl;
import pu.services.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompareDatabases
{
VM vm1;
VM vm2;

public static void main( String [] args )
{
	new CompareDatabases().run( Config.KDK, Config.TESTKDK );
	new CompareDatabases().run( Config.KTK, Config.TESTKTK );
	new CompareDatabases().run( Config.KDKT, Config.TESTKDKT );
	new CompareDatabases().run( Config.KLLK, Config.TESTKLLK );
	new CompareDatabases().run( Config.KLPK, Config.TESTKLPK );
	new CompareDatabases().run( Config.KDKTT, Config.TESTKDKTT );
}
private void run( ConfigImpl aConfigImpl1, ConfigImpl aConfigImpl2 )
{
	StopWatch timer = new StopWatch();
	vm1 = setupVm( aConfigImpl1 );
	vm2 = setupVm( aConfigImpl2 );
	vm1.getPageDescriptorTable().iterateOverAllPageDescriptors( this::compareDeDatabases );
	LOG.info( "Compare {} met {} klaar, duurde {}", aConfigImpl1, aConfigImpl2, timer.getElapsedMs() );
	LOG.info( "Aantal stellingen: {} waarvan ongelijk: {}", aantalStellingen, aantalStellingenOngelijk );
}
VM setupVm( ConfigImpl aConfigImpl )
{
	Config config = new Config();
	PageSizeCalculator pageSizeCalculator = new PageSizeCalculator( config );
	VM vm = new VM( config );
	config.setVm( vm );
	config.setPageSizeCalculator( pageSizeCalculator );

	config.switchConfig( aConfigImpl );
	vm.setDatabaseName( config.getDatabaseName() );
	vm.open( "r" );
	
	return vm;
}
int aantalStellingen = 0;
int aantalStellingenOngelijk = 0;
int aantalStellingenGeprint = 0;
void compareDeDatabases( VMStelling aVmStelling )
{
	VMStelling vmStelling = aVmStelling.clone();
	switch ( vm1.getPageSizeCalculator().getCacheType() )
	{
		case Serial: compareDeDatabasesSerial( vmStelling ); break;
		case Parallel: compareDeDatabasesParallel( vmStelling ); break;
	}
}
void compareDeDatabasesSerial( VMStelling aVmStelling )
{
	comparDeDatabasesS3tmS5( aVmStelling );
}
void compareDeDatabasesParallel( VMStelling aVmStelling )
{
	VMStelling vmStelling = aVmStelling.clone();
	for ( int zk = 0; zk < Constants.MAX_STUK; zk++ )
	{
		vmStelling.setZk( zk );
		for ( Kleur aanZet : Kleur.values() )
		{
			vmStelling.setAanZet( aanZet );
			comparDeDatabasesS3tmS5( vmStelling );
		}
	}
}
private void comparDeDatabasesS3tmS5( VMStelling vmStelling )
{
	for ( int s3 = 0; s3 < Constants.MAX_STUK; s3++ )
	{
		vmStelling.setS3( s3 );
		if ( vm1.getConfig().getAantalStukken() == 3 )
		{
			compareStelling( vmStelling );
		}
		else
		{
			for ( int s4 = 0; s4 < Constants.MAX_STUK; s4++ )
			{
				vmStelling.setS4( s4 );
				if ( vm1.getConfig().getAantalStukken() == 4 )
				{
					compareStelling( vmStelling );
				}
				else
				{
					for ( int s5 = 0; s5 < Constants.MAX_STUK; s5++ )
					{
						vmStelling.setS5( s5 );
						compareStelling( vmStelling );
					}
				}
			}
		}
	}
}
void compareStelling( VMStelling aVmStelling )
{
	aantalStellingen++;
	int vm1Rec = vm1.get( aVmStelling );
	int vm2Rec = vm2.get( aVmStelling );
	if ( vm1Rec != vm2Rec )
	{
		if ( aantalStellingenGeprint < 500 )
		{
			LOG.error( "Stelling ongelijk: {} real = {} test = {}", aVmStelling, vm1Rec, vm2Rec );
			aantalStellingenGeprint++;
		}
		aantalStellingenOngelijk++;
	}
}

}