package pu.chessdatabase.dal;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
public void testToString()
{
	VMStelling vmStelling  = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x11 )
		.s4( 0x23 )
		.aanZet( false )
		.build();
	System.out.println( vmStelling );
}

}
