package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import org.junit.jupiter.api.Test;

import pu.chessdatabase.bo.configuraties.KLPK;
import pu.chessdatabase.bo.configuraties.StukType;
import pu.chessdatabase.bo.configuraties.TestKDKT;

//@SpringBootTest
public class TestStukken
{
@Test
public void testVulStukTabelKDKT()
{
	Stukken stukken = new Stukken( new TestKDKT() );
	Stuk stuk;
	
	stuk = stukken.getWk();
	assertThat( stuk.getStukType(), is( StukType.KONING ) );
	assertThat( stuk.getKleur(), is( WIT ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "WK" ) );
	
	stuk = stukken.getZk();
	assertThat( stuk.getStukType(), is( StukType.KONING ) );
	assertThat( stuk.getKleur(), is( ZWART ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "ZK" ) );

	stuk = stukken.getS3();
	//if ( Config.getConfig().equals( stuk ))
	assertThat( stuk.getStukType(), is( StukType.DAME ) );
	assertThat( stuk.getKleur(), is( WIT ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "D" ) );
	assertThat( stuk.getStukString(), is( "WD" ) );
	
	stuk = stukken.getS4();
	assertThat( stuk.getStukType(), is( StukType.TOREN ) );
	assertThat( stuk.getKleur(), is( ZWART ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.TRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 4 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "T" ) );
	assertThat( stuk.getStukString(), is( "ZT" ) );
}
@Test
public void testVulStukTabelKLPK()
{
	Stukken stukken = new Stukken( new KLPK() );
	Stuk stuk;
	
	stuk = stukken.getWk();
	assertThat( stuk.getStukType(), is( StukType.KONING ) );
	assertThat( stuk.getKleur(), is( WIT ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "WK" ) );
	
	stuk = stukken.getZk();
	assertThat( stuk.getStukType(), is( StukType.KONING ) );
	assertThat( stuk.getKleur(), is( ZWART ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "ZK" ) );

	stuk = stukken.getS3();
	//if ( Config.getConfig().equals( stuk ))
	assertThat( stuk.getStukType(), is( StukType.LOPER ) );
	assertThat( stuk.getKleur(), is( WIT ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.LRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 4 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "L" ) );
	assertThat( stuk.getStukString(), is( "WL" ) );
	
	stuk = stukken.getS4();
	assertThat( stuk.getStukType(), is( StukType.PAARD ) );
	assertThat( stuk.getKleur(), is( WIT ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( Richtingen.PRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "P" ) );
	assertThat( stuk.getStukString(), is( "WP" ) );

}

}
