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
public void iterateOverS3S4( BoStelling aBoStelling, VMStelling aVmStelling, PassFunction aPassProc, VMIterateFunction aVmIterateFunction )
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
			boStelling.setS4( Dbs.CVT_STUK[vmStelling.getS4()] ); // Nu wel

			aVmIterateFunction.doPass( boStelling, vmStelling, aPassProc );
			vmStelling.setS4( vmStelling.getS4() + 1 );
		}
		vmStelling.setS3( vmStelling.getS3() + 1 );
	}
	// Waar is dit voor nodig? --> Voor die freeRecord. Beide stukken zijn 0x40 en dat
	// Is niet legaal
	vmStelling.setS3( vmStelling.getS3() - 1 );
	vmStelling.setS4( vmStelling.getS4() - 1 );
	vm.freeRecord( vmStelling );
}
void iterateOverWkZk( Kleur aKleur, PassFunction aPassFunction, VMIterateFunction aVmIterateFunction )
{
	VMStelling vmStelling = new VMStelling();
	vmStelling.setAanZet( aKleur );
	BoStelling boStelling = new BoStelling();
	boStelling.setAanZet( aKleur );
	for ( int ZK = 0; ZK < 64; ZK++ )
	{
		vmStelling.setZk( ZK );
		boStelling.setZk( Dbs.CVT_STUK[ZK] );
		for ( int WK = 0; WK < 10; WK++ )
		{
			vmStelling.setWk( WK );
			boStelling.setWk( Dbs.CVT_WK[WK] );
			aVmIterateFunction.doPass( boStelling, vmStelling, aPassFunction );
		}
	}
}
void iterateOverAllPieces( PassFunction aPassFunction, VMIterateFunction aVmIterateFunction )
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
					aVmIterateFunction.doPass( boStelling, vmStelling, aPassFunction );
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
