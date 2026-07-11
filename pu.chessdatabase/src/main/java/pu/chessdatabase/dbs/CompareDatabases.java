package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.Config;
import pu.services.StopWatch;

public class CompareDatabases
{
VM vm1;
VM vm2;

public static void main( String [] args )
{
	new CompareDatabases().run( "KDK", "TestKDK" );
	new CompareDatabases().run( "KDKT", "TestKDKT" );
}
private void run( String aConfigString1, String aConfigString2 )
{
	StopWatch timer = new StopWatch();
	vm1 = setupVm( aConfigString1 );
	vm2 = setupVm( aConfigString2 );
	vm1.getPageDescriptorTable().iterateOverAllPageDescriptors( this::compareDeDatabases );
	System.out.println( "Compare " + aConfigString1 + " klaar, duurde " + timer.getElapsedMs() );
	System.out.printf( "Aantal stellingen: %d waarvan ongelijk: %d\n", aantalStellingen, aantalStellingenOngelijk );
	System.out.printf( "Databases: %s %s\n", vm1.getDatabaseName(), vm2.getDatabaseName() );
}
VM setupVm( String aConfigName )
{
	VM vm = new VM();
	Config config = new Config( vm );
	vm.setConfig( config );
	config.switchConfig( aConfigName );
	vm.setDatabaseName( config.getDatabaseName() );
	vm.open();
	return vm;
}
int aantalStellingen = 0;
int aantalStellingenOngelijk = 0;
int aantalStellingenGeprint = 0;
void compareDeDatabases( VMStelling aVmStelling )
{
	VMStelling vmStelling = aVmStelling.clone();
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