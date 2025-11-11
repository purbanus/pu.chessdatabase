package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.Config;
import pu.services.StopWatch;

public class CompareKDKT
{
VM vm1 = new VM();
VM vm2 = new VM();

public static void main( String [] args )
{
	new CompareKDKT().run();
}
private void run()
{
	StopWatch timer = new StopWatch();
	setupVm( vm1, "KDKT", "dbs/KDKT.DBS" );
	setupVm( vm2, "KDKT", "dbs/KDKT2.DBS" );
	vm1.getPageDescriptorTable().iterateOverAllPageDescriptors( this::compareDeDatabases );
	System.out.println( "CompareKDKT klaar, duurde " + timer.getElapsedMs() );
	System.out.printf( "Aantal stellingen: %d waarvan ongelijk: %d", aantalStellingen, aantalStellingenOngelijk );
}
void setupVm( VM aVm, String aConfigName, String aDatabaseName )
{
	Config config1 = new Config( aVm );
	aVm.setConfig( config1 );
	config1.switchConfig( aConfigName );
	aVm.setDatabaseName( aDatabaseName );
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
		for ( int s4 = 0; s4 < VM.VELD_MAX; s4++ )
		{
			aantalStellingen++;
			vmStelling.setS4( s4 );
			int vm1Rec = vm1.get( vmStelling );
			int vm2Rec = vm2.get( vmStelling );
			if ( vm1Rec != vm2Rec )
			{
				//System.err.println( "Stellingen ongelijk: " + vmStelling + "vm1Rec = " + vm1Rec + " vm2Rec = " + vm2Rec );
				aantalStellingenOngelijk++;
			}
		}
	}
}
}
