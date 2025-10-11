package pu.chessdatabase.dal;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import pu.chessdatabase.bo.BoStelling;

public class TestVmStelling
{
@SuppressWarnings( "null" )
@Test
public void testShiftLeft()
{
	assertThat( (63 << 6) + 63, is( 4095 ) ); // 4032 + 63
	assertThat( 63 << 6 + 63, is( 2016 ) ); // Waarschijnlijk een gigantisch overflow, zie hieronder
	assertThat( new BigInteger( "63" ).shiftLeft( 69 ), is( new BigInteger( "37188636052598456057856") ) ); // Klopt dus
	assertThat( 63 << (6 + 63), is( 2016 ) );
}
@Test
public void testGetBoStelling()
{
	VMStelling vmStelling  = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x11 )
		.s4( 0x23 )
		.aanZet( WIT )
		.build();
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x21 )
		.s4( 0x43 )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling.getBoStelling(), is( boStelling ) );
	
	vmStelling = VMStelling.alfaBuilder()
		.wk( "f1" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "d4" )
		.aanZet( ZWART )
		.build();
	boStelling = BoStelling.alfaBuilder()
		.wk( "c2" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "d4" )
		.aanZet( ZWART )
		.build();
	assertThat( vmStelling.getBoStelling(), is( boStelling ) );
}
public static final String VM_TO_STRING = """
WK=a1 ZK=c1 S3=b2 S4=d3 AanZet=W
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. ZT .. .. .. ..\s
.. WD .. .. .. .. .. ..\s
WK .. ZK .. .. .. .. ..\s
""";

@Test
public void testToString()
{
	VMStelling vmStelling  = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "b2" )
		.s4( "d3" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling.toString(), is( VM_TO_STRING ) );
}

}
