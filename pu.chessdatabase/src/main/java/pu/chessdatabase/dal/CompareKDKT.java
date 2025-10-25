package pu.chessdatabase.dal;

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
	vm1.setDatabaseName( "dbs/KDKT.DBS" );
	vm2.setDatabaseName( "dbs/KDKT2.DBS" );
	vm1.open();
	vm2.open();
	vm1.getPageDescriptorTable().iterateOverAllPageDescriptors( this::compareDeDatabases );
	System.out.println( "CompareKDKT klaar" );
}
void compareDeDatabases( VMStelling aVmStelling )
{
	VMStelling vmStelling = aVmStelling.clone();
	for ( int s3 = 0; s3 < VM.VELD_MAX; s3++ )
	{
		vmStelling.setS3( s3 );
		for ( int s4 = 0; s4 < VM.VELD_MAX; s4++ )
		{
			vmStelling.setS4( s4 );
			int vm1Rec = vm1.get( vmStelling );
			int vm2Rec = vm2.get( vmStelling );
			if ( vm1Rec != vm2Rec )
			{
				throw new RuntimeException( "De stellingen zijn niet gelijk: " + vmStelling + " vm1Rec = " + vm1Rec + " vm2Rec = " + vm2Rec );
			}
		}
	}
}
}
