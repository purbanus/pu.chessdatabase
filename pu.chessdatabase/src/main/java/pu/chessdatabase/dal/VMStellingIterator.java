package pu.chessdatabase.dal;

import static pu.chessdatabase.bo.Kleur.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Kleur;

import lombok.Data;

@Component
@Data
public class VMStellingIterator
{
@Autowired private VM vm;
@Autowired private Config config;
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
public void iterateOverWkZkWit( PassFunction aPassFunction, VMIteratorFunction aVmIteratorFunction )
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
			aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
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
			aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
		}
		else
		{
			for ( int s4 = 0; s4 < 64; s4++ )
			{
				vmStelling.setS4( s4 );
				boStelling.setS4( Dbs.CVT_STUK[s4] );
				if ( getConfig().getAantalStukken() == 4 )
				{
					aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
				}
				else
				{
					for ( int s5 = 0; s5 < 64; s5++ )
					{
						vmStelling.setS5( s5 );
						boStelling.setS5( Dbs.CVT_STUK[s5] );
		
						aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
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
public void iterateOverAllPieces( PassFunction aPassFunction, VMIteratorFunction aVmIteratorFunction )
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
						aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
					}
					else
					{
						for ( int s4 = 0; s4 < 64; s4++ )
						{
							vmStelling.setS4( s4 );
							boStelling.setS4( Dbs.CVT_STUK[s4] );
							if ( getConfig().getAantalStukken() == 4 )
							{
								aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
							}
							else
							{
								for ( int s5 = 0; s5 < 64; s5++ )
								{
									vmStelling.setS5( s5 );
									boStelling.setS5( Dbs.CVT_STUK[s5] );
									aVmIteratorFunction.doPass( boStelling, vmStelling, aPassFunction );
								}
							}
						}
					}
				}
			}
		}
	}
}

}
