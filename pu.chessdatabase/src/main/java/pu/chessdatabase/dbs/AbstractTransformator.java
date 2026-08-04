package pu.chessdatabase.dbs;

import pu.services.Range;

public abstract class AbstractTransformator implements Transformator
{
/**==============================================================================================================
* Konversie WK notatie van VM naar Gen
*==============================================================================================================*/
// De WK moet in het eerste oktant zitten, dwz de veldwaarde moet tussen 0 en 9 zitten
// De CVT_WK transformeert hem dan naar een van de velden
// a1, b1, c1, d1,   0, 1, 2, 3  
//     b2, c2, d3,      4, 5, 6
//         c3, d3,         7, 8
//             d4,            9
// @@HIGH Dit klopt niet met pionnen. Wk krijgt dan een meer noirmale traansformatie
public static final int [] CVT_WK = {
	0x00,0x01,0x02,0x03,
		 0x11,0x12,0x13,
			  0x22,0x23,
				   0x33
};
/**==============================================================================================================
* Konversie stuk (niet-WK) notatie van VM naar Zgen
1*==============================================================================================================*/
public static final int [] CVT_STUK = {
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
	return CVT_STUK[aVmStellingStuk];
}

}
