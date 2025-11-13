package pu.chessdatabase.bo.speel;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import static pu.chessdatabase.bo.speel.Einde.*;
import static pu.chessdatabase.bo.Kleur.*;

import org.junit.jupiter.api.Test;

public class TestEnumToString
{
@Test
public void testEnumToString()
{
	assertThat( Nog_niet.toString(), is( "Nog_niet" ) );
	assertThat( Mat.toString(), is( "Mat" ) );
	assertThat( Wit.toString(), is( "Wit" ) );
	assertThat( Zwart.toString(), is( "Zwart" ) );
}
}
