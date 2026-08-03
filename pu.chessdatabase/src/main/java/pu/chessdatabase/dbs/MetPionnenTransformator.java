package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.configuraties.StukType.*;

import org.apache.commons.lang3.builder.ToStringExclude;

import pu.chessdatabase.bo.BoStelling;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class MetPionnenTransformator extends AbstractTransformator
{
@ToStringExclude
@EqualsAndHashCode.Exclude
private int [] naarVmTabel = new int [120];
public MetPionnenTransformator()
{
	super();
	createNaarVmTabel();
}
void createNaarVmTabel()
{
	int index = 0;
	for ( int rij = 0; rij < 8; rij++ )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			naarVmTabel[rij * 16 + kol] = index++;
		}
		if ( rij < 7 )
		{
			for ( int kol = 8; kol < 16; kol++ )
			{
				naarVmTabel[rij * 16 + kol] = 0xff;
			}
		}
	}
}
@Override
public int vmStellingWkToBoStellingWk( int aVmStellingWk )
{
	return CVT_STUK[aVmStellingWk];
}
@Override
public VMStelling boStellingToVmStelling( BoStelling aBoStelling )
{
	return VMStelling.builder()
		.wk( naarVmTabel[aBoStelling.getWk()] )
		.zk( naarVmTabel[aBoStelling.getZk()] )
		.s3( naarVmTabel[aBoStelling.getS3()] )
		.s4( naarVmTabel[aBoStelling.getS4()] )
		.s5( naarVmTabel[aBoStelling.getS5()] )
		.aanZet( aBoStelling.getAanZet() )
		.build();
}

}
