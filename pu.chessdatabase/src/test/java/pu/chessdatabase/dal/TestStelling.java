package pu.chessdatabase.dal;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class TestStelling
{
@SuppressWarnings( "null" )
@Test
public void testShiftLeft()
{
	assertThat( (63 << 6) + 63, is( 4095 ) );
	assertThat( 63 << 6 + 63, is( 2016 ) ); // Waarschijnlijk een gigantisch overflow
	assertThat( new BigInteger( "63" ).shiftLeft( 69 ), is( new BigInteger( "37188636052598456057856") ) ); // Klopt dus
	assertThat( 63 << (6 + 63), is( 2016 ) );
}

}
