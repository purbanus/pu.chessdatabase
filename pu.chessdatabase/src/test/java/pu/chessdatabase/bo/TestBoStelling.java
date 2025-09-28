package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import pu.chessdatabase.dal.ResultaatType;

@SuppressWarnings( "unused" )
public class TestBoStelling
{
// Let op: alle trailing spaces worden door de compiler weggehaald.
// Gelukkig bestaat er een \s waarmee je een niet-verwijderbare space
// definieert. FYI: alle spaces voor die \s worden evenmin verwijderd
public static final String BO_TO_STRING = """
WK=0 ZK=2 S3=11 S4=23 AanZet=W Resultaat=null AantalZetten=0 Schaak=false
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
	BoStelling boStelling  = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x11 )
		.s4( 0x23 )
		.aanZet( false )
		.build();
	assertThat( boStelling.toString().length(), is( BO_TO_STRING.length() ) );
	assertThat( boStelling.toString(), is( BO_TO_STRING) );
}
@Test
public void testClone()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 1 )
		.zk( 2 )
		.s3( 3 )
		.s4( 4 )
		.aanZet( true )
		.resultaat( ResultaatType.Gewonnen )
		.aantalZetten( 18 )
		.schaak(  true )
		.build();
	BoStelling boStelling2 = boStelling.clone();
	assertThat( boStelling2, is( boStelling ) );
}
}
