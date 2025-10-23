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
import static org.hamcrest.MatcherAssert.*;
@SuppressWarnings( "unused" )
@SpringBootTest
public class TestBoStelling
{
String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getName();
}
@AfterEach
public void destroy()
{
	config.switchConfig( savedConfigString );
}

@Autowired private Config config;

// Let op: alle trailing spaces worden door de compiler weggehaald.
// Gelukkig bestaat er een \s waarmee je een niet-verwijderbare space
// definieert. FYI: alle spaces voor die \s worden evenmin verwijderd
public static final String BO_TO_STRING = """
WK=a1 ZK=c1 S3=b2 S4=d3 AanZet={4} Resultaat=null AantalZetten=0 Schaak=false
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
WK=a1 ZK=c1 S3=b2 S4=d3 AanZet={4} Resultaat={5} AantalZetten={6} Schaak={7}
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
		.aanZet( WIT )
		.build();

	config.switchConfig( "KDKT" );
	String boStringText = MessageFormat.format( BO_TO_STRING, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
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
@Test
public void testAlfaBuilder()
{
	BoStelling boStelling  = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "b2" )
		.s4( "d3" )
		.aanZet( WIT )
		.build();
	config.switchConfig( "KDKT" );
	String boStringText = MessageFormat.format( BO_TO_STRING, 
		Config.getStaticStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		boStelling.getAanZet().getAfko()
	);
	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
	assertThat( boStelling.toString(), is( boStringText ) );
	
	boStelling  = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "b2" )
		.s4( "d3" )
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
		boStelling.getAanZet().getAfko(),
		boStelling.getResultaat(),
		boStelling.getAantalZetten(),
		boStelling.isSchaak()
	);
	assertThat( boStelling.toString().length(), is( boStringText.length() ) );
	assertThat( boStelling.toString(), is( boStringText ) );
}
}
