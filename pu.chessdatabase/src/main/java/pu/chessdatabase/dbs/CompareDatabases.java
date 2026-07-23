package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.configuraties.ConfigImpl;
import pu.services.StopWatch;

public class CompareDatabases
{
VM vm1;
VM vm2;

public static void main( String [] args )
{
//	new CompareDatabases().run( Config.KDK, Config.TESTKDK );
//	new CompareDatabases().run( Config.KTK, Config.TESTKTK );
	new CompareDatabases().run( Config.KDKT, Config.TESTKDKT );
	new CompareDatabases().run( Config.KLLK, Config.TESTKLLK );
	new CompareDatabases().run( Config.KLPK, Config.TESTKLPK );
	//new CompareDatabases().run( Config.KDKTT, Config.TESTKDKTT );
}
private void run( ConfigImpl aConfigImpl1, ConfigImpl aConfigImpl2 )
{
	StopWatch timer = new StopWatch();
	vm1 = setupVm( aConfigImpl1 );
	vm2 = setupVm( aConfigImpl2 );
	vm1.getPageDescriptorTable().iterateOverAllPageDescriptors( this::compareDeDatabases );
	System.out.printf( "Compare %s met %s klaar, duurde %s\n", aConfigImpl1, aConfigImpl2, timer.getElapsedMs() );
	System.out.printf( "Aantal stellingen: %d waarvan ongelijk: %d\n", aantalStellingen, aantalStellingenOngelijk );
//	System.out.printf( "Databases: %s %s\n", vm1.getDatabaseName(), vm2.getDatabaseName() );
}
VM setupVm( ConfigImpl aConfigImpl )
{
	VM vm = new VM();
	Config config = new Config( vm );
	vm.setConfig( config );
	config.switchConfig( aConfigImpl );
	vm.setDatabaseName( config.getDatabaseName() );
	vm.open( "rw" );
	
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
	for ( int zk = 0; zk < VM.MAX_STUK; zk++ )
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
	for ( int s3 = 0; s3 < VM.MAX_STUK; s3++ )
	{
		vmStelling.setS3( s3 );
		if ( vm1.getConfig().getAantalStukken() == 3 )
		{
			compareStelling( vmStelling );
		}
		else
		{
			for ( int s4 = 0; s4 < VM.MAX_STUK; s4++ )
			{
				vmStelling.setS4( s4 );
				if ( vm1.getConfig().getAantalStukken() == 4 )
				{
					compareStelling( vmStelling );
				}
				else
				{
					for ( int s5 = 0; s5 < VM.MAX_STUK; s5++ )
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
			System.err.println( "Stelling ongelijk: " + aVmStelling + "real = " + vm1Rec + " test = " + vm2Rec );
			aantalStellingenGeprint++;
		}
		aantalStellingenOngelijk++;
	}
}

}