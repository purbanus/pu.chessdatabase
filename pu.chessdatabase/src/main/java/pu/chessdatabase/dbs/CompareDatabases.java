package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.Config;
import pu.services.StopWatch;

public class CompareDatabases
{
VM vm1 = new VM();
VM vm2 = new VM();

public static void main( String [] args )
{
	new CompareDatabases().run( "KDK", "TestKDK" );
	new CompareDatabases().run( "KDKT", "TestKDKT" );
}
private void run( String aConfigString1, String aConfigString2 )
{
	StopWatch timer = new StopWatch();
	setupVm( vm1, aConfigString1 );
	setupVm( vm2, aConfigString2 );
	vm1.getPageDescriptorTable().iterateOverAllPageDescriptors( this::compareDeDatabases );
	System.out.println( "Compare " + aConfigString1 + " klaar, duurde " + timer.getElapsedMs() );
	System.out.printf( "Aantal stellingen: %d waarvan ongelijk: %d\n", aantalStellingen, aantalStellingenOngelijk );
}
void setupVm( VM aVm, String aConfigName )
{
	Config config = new Config( aVm );
	aVm.setConfig( config );
	config.switchConfig( aConfigName );
	aVm.setDatabaseName( config.getDatabaseName() );
	aVm.open();
}
int aantalStellingen = 0;
int aantalStellingenOngelijk = 0;
void compareDeDatabases( VMStelling aVmStelling )
{
	VMStelling vmStelling = aVmStelling.clone();
	for ( int s3 = 0; s3 < VM.VELD_MAX; s3++ )
	{
		vmStelling.setS3( s3 );
		if ( vm1.getConfig().getAantalStukken() == 3 )
		{
			compareStelling( vmStelling );
		}
		else
		{
			for ( int s4 = 0; s4 < VM.VELD_MAX; s4++ )
			{
				vmStelling.setS4( s4 );
				if ( vm1.getConfig().getAantalStukken() == 4 )
				{
					compareStelling( vmStelling );
				}
				else
				{
					for ( int s5 = 0; s5 < VM.VELD_MAX; s5++ )
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
		//System.err.println( "Stellingen ongelijk: " + vmStelling + "vm1Rec = " + vm1Rec + " vm2Rec = " + vm2Rec );
		aantalStellingenOngelijk++;
	}
}

}