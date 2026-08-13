package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.BoStelling;

import lombok.Data;

@Data
public abstract class AbstractTransformator implements Transformator
{
/**==============================================================================================================
* Konversie stuk (niet-WK) notatie van VM naar Zgen
1*==============================================================================================================*/
public static final int [] TRANSFORM_STUK = {
	0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,
	0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,
	0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,
	0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,
	0x40,0x41,0x42,0x43,0x44,0x45,0x46,0x47,
	0x50,0x51,0x52,0x53,0x54,0x55,0x56,0x57,
	0x60,0x61,0x62,0x63,0x64,0x65,0x66,0x67,
	0x70,0x71,0x72,0x73,0x74,0x75,0x76,0x77
};
public AbstractTransformator()
{
	super();
}
@Override
public int vmStellingStukToBoStellingStuk( int aVmStellingStuk )
{
	return TRANSFORM_STUK[aVmStellingStuk];
}

/**
 * -------- Stelling van VM-formaat naar Bo-formaat ------
 */
@Override
public BoStelling vmStellingToBoStelling( VMStelling aVmStelling )
{
	return BoStelling.builder()
		.wk( vmStellingWkToBoStellingWk    (   aVmStelling.getWk() ) )
		.zk( vmStellingStukToBoStellingStuk( aVmStelling.getZk() ) )
		.s3( vmStellingStukToBoStellingStuk( aVmStelling.getS3() ) )
		.s4( vmStellingStukToBoStellingStuk( aVmStelling.getS4() ) )
		.s5( vmStellingStukToBoStellingStuk( aVmStelling.getS5() ) )
		.aanZet( aVmStelling.getAanZet() )
		.build();
}

}
