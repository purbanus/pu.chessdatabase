	package pu.chessdatabase.dal;

import static pu.chessdatabase.bo.Kleur.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Kleur;

import lombok.Data;

@Component
@Data
public class VMStellingIterator
{
@Autowired private VM vm;
public void iterateOverStukken( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassProc, VMIteratorFunction aVmIteratorFunction )
{
	BoStelling boStelling = aBoStelling.clone();
	VMStelling vmStelling = aVmStelling.clone();
	vmStelling.setS3( 0 );
	while ( vmStelling.getS3() < 64 )
	{
		boStelling.setS3( Dbs.CVT_STUK[vmStelling.getS3()] );
		vmStelling.setS4( 0 );
		while ( vmStelling.getS4() < 64 )
		{
			boStelling.setS4( Dbs.CVT_STUK[vmStelling.getS4()] );
			vmStelling.setS5( 0 );
			while ( vmStelling.getS5() < 64 )
			{
				boStelling.setS5( Dbs.CVT_STUK[vmStelling.getS4()] );

				aVmIteratorFunction.doPass( boStelling, vmStelling, aPassProc );
				vmStelling.setS5( vmStelling.getS5() + 1 );
			}
			vmStelling.setS4( vmStelling.getS4() + 1 );
		}
		vmStelling.setS3( vmStelling.getS3() + 1 );
	}
	// Waar is dit voor nodig? --> Voor die freeRecord. Alle drie de stukken zijn 0x40
	// en dat is niet legaal
	vmStelling.setS3( vmStelling.getS3() - 1 );
	vmStelling.setS4( vmStelling.getS4() - 1 );
	vmStelling.setS5( vmStelling.getS5() - 1 );
	vm.freeRecord( vmStelling );
}
public void iterateOverWkZk( VMSimpleIteratorFunction aVmIteratorFunction )
{
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
public void iterateOverAllPieces( PassFunction aPassFunction, VMIteratorFunction aVmIteratorFunction )
{
	VMStelling vmStelling = new VMStelling();
	BoStelling boStelling = new BoStelling();
	vmStelling.setWk( 0 );
	while ( vmStelling.getWk() < 10 )
	{
		boStelling.setWk( Dbs.CVT_WK[vmStelling.getWk()] );
		vmStelling.setZk( 0 );
		while ( vmStelling.getZk() < 64 )
		{
			boStelling.setZk( Dbs.CVT_STUK[vmStelling.getZk()] );
			vmStelling.setS3( 0 );
			while ( vmStelling.getS3() < 64 )
			{
				boStelling.setS3( Dbs.CVT_STUK[vmStelling.getS3()] );
				vmStelling.setS4( 0 );
				while ( vmStelling.getS4() < 64 )
				{
					boStelling.setS4( Dbs.CVT_STUK[vmStelling.getS4()] );
					vmStelling.setS5( 0 );
					while ( vmStelling.getS5() < 64 )
					{
						boStelling.setS5( Dbs.CVT_STUK[vmStelling.getS5()] );
						aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
						vmStelling.setS5( vmStelling.getS5() + 1 );
					}
					vmStelling.setS4( vmStelling.getS4() + 1 );
				}
				vmStelling.setS3( vmStelling.getS3() + 1 );
			}
			vmStelling.setZk( vmStelling.getZk() + 1 );
		}
		vmStelling.setWk( vmStelling.getWk() + 1 );
	}
}

}
