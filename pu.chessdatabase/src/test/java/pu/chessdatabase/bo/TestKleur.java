package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;

import org.junit.jupiter.api.Test;

public class TestKleur
{
@Test
public void testValueOf()
{
	Kleur kleur;
	kleur = Kleur.valueOf( "WIT" );
	assertThat( kleur, is( WIT ) );
	assertThrows( IllegalArgumentException.class, () -> Kleur.valueOf( "Wit" ) );
	kleur = Kleur.valueOf( "ZWART" );
	assertThat( kleur, is( ZWART ) );
}
}
