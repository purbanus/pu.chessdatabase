package pu.chessdatabase.bo.speel;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class TestVanNaar
{
@SuppressWarnings( "null" )
@Test
public void testCtorWithInts()
{
	VanNaar expectedVanNaar = VanNaar.builder()
		.van( 0x11 )
		.naar( 0x33 )
		.build();
	assertThat( new VanNaar( 0x11, 0x33 ), is( expectedVanNaar ) );
}
@SuppressWarnings( "null" )
@Test
public void testCtorWithStrings()
{
	VanNaar expectedVanNaar = VanNaar.alfaBuilder()
		.van( "b2" )
		.naar( "d4" )
		.build();
	assertThat( new VanNaar( "b2", "d4" ), is( expectedVanNaar ) );
}
@SuppressWarnings( "null" )
@Test
public void testCtorWithString()
{
	VanNaar expectedVanNaar = VanNaar.alfaBuilder()
		.van( "b2" )
		.naar( "d4" )
		.build();
	assertThat( new VanNaar( "b2d4" ), is( expectedVanNaar ) );
	assertThat( new VanNaar( "  b2d4   " ), is( expectedVanNaar ) );
	assertThat( new VanNaar( "  b 2 d4   " ), is( expectedVanNaar ) );
	assertThat( new VanNaar( "Db2-d4" ), is( expectedVanNaar ) );
	assertThat( new VanNaar( "Tb2xd4" ), is( expectedVanNaar ) );
	assertThat( new VanNaar( "Lb2xd4=  #" ), is( expectedVanNaar ) );
	assertThat( new VanNaar( "Pb2xd4=  #" ), is( expectedVanNaar ) );
}
}