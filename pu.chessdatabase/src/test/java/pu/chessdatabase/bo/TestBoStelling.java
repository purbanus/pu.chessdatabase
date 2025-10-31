package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.text.MessageFormat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.ResultaatType;
@SuppressWarnings( "unused" )
@SpringBootTest
public class TestBoStelling
{
String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
}
@AfterEach
public void destroy()
{
	config.switchConfig( savedConfigString );
}

@Autowired private Config config;

@Test
public void testGetVeldKleur()
{
	assertThat( BoStelling.getVeldKleur( 0x00 ), is( ZWART ) );
	assertThat( BoStelling.getVeldKleur( 0x01 ), is( WIT ) );
	assertThat( BoStelling.getVeldKleur( 0x02 ), is( ZWART ) );
	assertThat( BoStelling.getVeldKleur( 0x07 ), is( WIT ) );
	assertThat( BoStelling.getVeldKleur( 0x10 ), is( WIT ) );
	assertThat( BoStelling.getVeldKleur( 0x17 ), is( ZWART ) );
	assertThat( BoStelling.getVeldKleur( 0x20 ), is( ZWART ) );
	assertThat( BoStelling.getVeldKleur( 0x27 ), is( WIT ) );
	assertThat( BoStelling.getVeldKleur( 0x70 ), is( WIT ) );
	assertThat( BoStelling.getVeldKleur( 0x77 ), is( ZWART ) );
}
// Let op: alle trailing spaces worden door de compiler weggehaald.
// Gelukkig bestaat er een \s waarmee je een niet-verwijderbare space
// definieert. FYI: alle spaces voor die \s worden evenmin verwijderd
public static final String BO_TO_STRING = """
WK=a1 ZK=c1 S3=b2 S4=d3 S5=a1 AanZet={5} Resultaat=null AantalZetten=0 Schaak=false
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. {3} .. .. .. ..\s
.. {2} .. .. .. .. .. ..\s
{0} .. {1} .. .. .. .. ..\s
""";
public static final String BO_TO_STRING_EXTRA = """
WK=a1 ZK=c1 S3=b2 S4=d3 S5=a1 AanZet={5} Resultaat={6} AantalZetten={7} Schaak={8}
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. {3} .. .. .. ..\s
.. {2} .. .. .. .. .. ..\s
{0} .. {1} .. .. .. .. ..\s
""";
@Test
public void testToString()
{
	BoStelling boStelling  = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "b2" )
		.s4( "d3" )
		.s5( "a1" )
		.aanZet( WIT )
		.build();

	config.switchConfig( "KDKT" );
	String boStringText = MessageFormat.format( BO_TO_STRING, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		config.getStukken().getS5().getStukString(), // @@HIGH Nog effe niet
		boStelling.getAanZet().getAfko()
	);
	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
	assertThat( boStelling.toString(), is( boStringText ) );
	
	config.switchConfig( "KLPK" );
	boStringText = MessageFormat.format( BO_TO_STRING, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		config.getStukken().getS5().getStukString(), // @@HIGH Nog effe niet
		boStelling.getAanZet().getAfko()
	);
	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
	assertThat( boStelling.toString(), is( boStringText ) );

	config.switchConfig( "KLLK" );
	boStringText = MessageFormat.format( BO_TO_STRING, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		config.getStukken().getS5().getStukString(), // @@HIGH Nog effe niet
		boStelling.getAanZet().getAfko()
	);
	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
	assertThat( boStelling.toString(), is( boStringText ) );
}
@Test
public void testClone()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 1 )
		.zk( 2 )
		.s3( 3 )
		.s4( 4 )
		.aanZet( ZWART )
		.resultaat( ResultaatType.GEWONNEN )
		.aantalZetten( 18 )
		.schaak( true )
		.build();
	BoStelling boStelling2 = boStelling.clone();
	assertThat( boStelling2, is( boStelling ) );
}
//@Test
//public void testAlfaBuilderMets5Oph8()
//{
//	BoStelling boStelling  = BoStelling.alfaBuilder()
//		.wk( "a1" )
//		.zk( "c1" )
//		.s3( "b2" )
//		.s4( "d3" )
//		.s5( "h8" )
//		.aanZet( WIT )
//		.build();
//	config.switchConfig( "KDKT" );
//	String boStringText = MessageFormat.format( BO_TO_STRING, 
//		Config.getStaticStukken().getWk().getStukString(),
//		config.getStukken().getZk().getStukString(),
//		config.getStukken().getS3().getStukString(),
//		config.getStukken().getS4().getStukString(),
//		config.getStukken().getS5().getStukString(), // @@HIGH Nog effe niet
//		boStelling.getAanZet().getAfko()
//	);
//	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
//	assertThat( boStelling.toString(), is( boStringText ) );
//	
//	boStelling  = BoStelling.alfaBuilder()
//		.wk( "a1" )
//		.zk( "c1" )
//		.s3( "b2" )
//		.s4( "d3" )
//		.s5( "h8" )
//		.aanZet( WIT )
//		.resultaat( ResultaatType.GEWONNEN )
//		.aantalZetten( 19 )
//		.schaak( true )
//		.build();
//	boStringText = MessageFormat.format( BO_TO_STRING_EXTRA, 
//		config.getStukken().getWk().getStukString(),
//		config.getStukken().getZk().getStukString(),
//		config.getStukken().getS3().getStukString(),
//		config.getStukken().getS4().getStukString(),
//		config.getStukken().getS5().getStukString(), // @@HIGH Nog effe niet
//		boStelling.getAanZet().getAfko(),
//		boStelling.getResultaat(),
//		boStelling.getAantalZetten(),
//		boStelling.isSchaak()
//	);
//	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
////	String boStellingToString = boStelling.toString();
////	for ( int x = 0; x < boStringText.length(); x++ )
////	{
////		if ( boStringText.charAt( x ) != boStellingToString.charAt( x ) )
////		{
////			System.out.println( "Op positie " + x + " " + boStringText.charAt( x ) + " " + boStellingToString.charAt( x ) );
////		}
////	}
//	assertThat( boStelling.toString(), is( boStringText ) );
//}
@Test
public void testAlfaBuilderMets5OpWk()
{
	BoStelling boStelling  = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "b2" )
		.s4( "d3" )
		.s5( "a1" )
		.aanZet( WIT )
		.build();
	config.switchConfig( "KDKT" );
	String boStringText = MessageFormat.format( BO_TO_STRING, 
		Config.getStaticStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		config.getStukken().getS5().getStukString(), // @@HIGH Nog effe niet
		boStelling.getAanZet().getAfko()
	);
	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
	assertThat( boStelling.toString(), is( boStringText ) );
	
	boStelling  = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "b2" )
		.s4( "d3" )
		.s5( "a1" )
		.aanZet( WIT )
		.resultaat( ResultaatType.GEWONNEN )
		.aantalZetten( 19 )
		.schaak( true )
		.build();
	boStringText = MessageFormat.format( BO_TO_STRING_EXTRA, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		config.getStukken().getS5().getStukString(), // @@HIGH Nog effe niet
		boStelling.getAanZet().getAfko(),
		boStelling.getResultaat(),
		boStelling.getAantalZetten(),
		boStelling.isSchaak()
	);
	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
	assertThat( boStelling.toString(), is( boStringText ) );
}
@Test
public void testMetGeslagenStuk()
{
	config.switchConfig( "KDKT" );

	String localToString = """
		WK=a1 ZK=c1 S3=a1 S4=d3 S5=a1 AanZet={5} Resultaat=null AantalZetten=0 Schaak=false
		.. .. .. .. .. .. .. ..\s
		.. .. .. .. .. .. .. ..\s
		.. .. .. .. .. .. .. ..\s
		.. .. .. .. .. .. .. ..\s
		.. .. .. .. .. .. .. ..\s
		.. .. .. {3} .. .. .. ..\s
		.. .. .. .. .. .. .. ..\s
		{0} .. {1} .. .. .. .. ..\s
		""";
	BoStelling boStelling  = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "a1" )
		.s4( "d3" )
		.s5( "a1" )
		.aanZet( WIT )
		.build();
	String localToStringText = MessageFormat.format( localToString, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		config.getStukken().getS5().getStukString(),
		boStelling.getAanZet().getAfko()
	);
	System.out.println( boStelling );
	assertThat( boStelling.toString().length(), is( localToStringText.length() ) );
	assertThat( boStelling.toString(), is( localToStringText ) );
}

}
