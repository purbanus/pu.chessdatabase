package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.Richtingen.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import pu.chessdatabase.bo.configuraties.KDK;
import pu.chessdatabase.bo.configuraties.KDKT;
import pu.chessdatabase.bo.configuraties.KDKTT;
import pu.chessdatabase.bo.configuraties.KLPK;
import pu.chessdatabase.bo.configuraties.TestKDKT;

import lombok.Data;

@Data
public class TestStukken
{
@Test
public void testVulStukTabelKDKT()
{
	Stukken stukken = new Stukken( new TestKDKT() );
	Stuk stuk;
	
	stuk = stukken.getWk();
	assertThat( stuk.getStukType(), is( Koning ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "WK" ) );
	
	stuk = stukken.getZk();
	assertThat( stuk.getStukType(), is( Koning ) );
	assertThat( stuk.getKleur(), is( Zwart ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "ZK" ) );

	stuk = stukken.getS3();
	assertThat( stuk.getStukType(), is( Dame ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "D" ) );
	assertThat( stuk.getStukString(), is( "WD" ) );
	
	stuk = stukken.getS4();
	assertThat( stuk.getStukType(), is( Toren ) );
	assertThat( stuk.getKleur(), is( Zwart ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( TRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 4 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "T" ) );
	assertThat( stuk.getStukString(), is( "ZT" ) );

	stuk = stukken.getS5();
	assertThat( stuk.getStukType(), is( Geen ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( GRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 0 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "G" ) );
	assertThat( stuk.getStukString(), is( "WG" ) );
}
@Test
public void testVulStukTabelKLPK()
{
	Stukken stukken = new Stukken( new KLPK() );
	Stuk stuk;
	
	stuk = stukken.getWk();
	assertThat( stuk.getStukType(), is( Koning ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "WK" ) );
	
	stuk = stukken.getZk();
	assertThat( stuk.getStukType(), is( Koning ) );
	assertThat( stuk.getKleur(), is( Zwart ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( KRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getStukString(), is( "ZK" ) );

	stuk = stukken.getS3();
	//if ( Config.getConfig().equals( stuk ))
	assertThat( stuk.getStukType(), is( Loper ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( LRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 4 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getAfko(), is( "L" ) );
	assertThat( stuk.getStukString(), is( "WL" ) );
	
	stuk = stukken.getS4();
	assertThat( stuk.getStukType(), is( Paard ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( PRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "P" ) );
	assertThat( stuk.getStukString(), is( "WP" ) );

	stuk = stukken.getS5();
	assertThat( stuk.getStukType(), is( Geen ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( GRICHTING ) );
	assertThat( stuk.getRichtingen().size(), is( 0 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getAfko(), is( "G" ) );
	assertThat( stuk.getStukString(), is( "WG" ) );
}
@Test
public void testGetSortedStukken()
{
	Stukken stukken = new Stukken( new KDK() );
	List<Stuk> stukList = stukken.getSortedStukken();
	assertThat( stukList.size(), is( 5 ) );
	assertThat( stukList.get( 0 ).getStukString(), is( "WK" ) );
	assertThat( stukList.get( 1 ).getStukString(), is( "WD" ) );
	assertThat( stukList.get( 2 ).getStukString(), is( "WG" ) );
	assertThat( stukList.get( 3 ).getStukString(), is( "WG" ) );
	assertThat( stukList.get( 4 ).getStukString(), is( "ZK" ) );

	stukken = new Stukken( new KDKT() );
	stukList = stukken.getSortedStukken();
	assertThat( stukList.size(), is( 5 ) );
	assertThat( stukList.get( 0 ).getStukString(), is( "WK" ) );
	assertThat( stukList.get( 1 ).getStukString(), is( "WD" ) );
	assertThat( stukList.get( 2 ).getStukString(), is( "WG" ) );
	assertThat( stukList.get( 3 ).getStukString(), is( "ZK" ) );
	assertThat( stukList.get( 4 ).getStukString(), is( "ZT" ) );

	stukken = new Stukken( new KDKTT() );
	stukList = stukken.getSortedStukken();
	assertThat( stukList.size(), is( 5 ) );
	assertThat( stukList.get( 0 ).getStukString(), is( "WK" ) );
	assertThat( stukList.get( 1 ).getStukString(), is( "WD" ) );
	assertThat( stukList.get( 2 ).getStukString(), is( "ZK" ) );
	assertThat( stukList.get( 3 ).getStukString(), is( "ZT" ) );
	assertThat( stukList.get( 4 ).getStukString(), is( "ZT" ) );
}
@Test
public void testGetRealStukken()
{
	Stukken stukken = new Stukken( new KDK() );
	assertThat( stukken.getRealStukken().size(), is( 3 ) );
	assertThat( stukken.getRealStukken().get( 0 ).getStukString(), is( "WK" ) );
	assertThat( stukken.getRealStukken().get( 1 ).getStukString(), is( "WD" ) );
	assertThat( stukken.getRealStukken().get( 2 ).getStukString(), is( "ZK" ) );

	stukken = new Stukken( new KLPK() );
	assertThat( stukken.getRealStukken().size(), is( 4 ) );
	assertThat( stukken.getRealStukken().get( 0 ).getStukString(), is( "WK" ) );
	assertThat( stukken.getRealStukken().get( 1 ).getStukString(), is( "WL" ) );
	assertThat( stukken.getRealStukken().get( 2 ).getStukString(), is( "WP" ) );
	assertThat( stukken.getRealStukken().get( 3 ).getStukString(), is( "ZK" ) );

	stukken = new Stukken( new KDKTT() );
	assertThat( stukken.getRealStukken().size(), is( 5 ) );
	assertThat( stukken.getRealStukken().get( 0 ).getStukString(), is( "WK" ) );
	assertThat( stukken.getRealStukken().get( 1 ).getStukString(), is( "WD" ) );
	assertThat( stukken.getRealStukken().get( 2 ).getStukString(), is( "ZK" ) );
	assertThat( stukken.getRealStukken().get( 3 ).getStukString(), is( "ZT" ) );
	assertThat( stukken.getRealStukken().get( 4 ).getStukString(), is( "ZT" ) );
}
@Test
public void testGetFakeStukken()
{
	Stukken stukken = new Stukken( new KDK() );
	assertThat( stukken.getFakeStukken().size(), is( 2 ) );

	stukken = new Stukken( new KLPK() );
	assertThat( stukken.getFakeStukken().size(), is( 1 ) );

	stukken = new Stukken( new KDKTT() );
	assertThat( stukken.getFakeStukken().size(), is( 0 ) );
}
@Test
public void testGetAantalStukken()
{
	Stukken stukken = new Stukken( new KDK() );
	assertThat( stukken.getAantalStukken(), is( 3 ) );

	stukken = new Stukken( new KLPK() );
	assertThat( stukken.getAantalStukken(), is( 4 ) );

	stukken = new Stukken( new KDKTT() );
	assertThat( stukken.getAantalStukken(), is( 5 ) );
}

}