package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.PassType.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dbs.Dbs;
import pu.chessdatabase.dbs.PassType;

import lombok.Data;

@SuppressWarnings( "unused" ) // Dit gaat over die jupiter.api.Assertions
@SpringBootTest
@Data
public class TestBouwOverProductie
{
public static final boolean DO_PRINT = false;
@Autowired private Gen gen;
@Autowired private Dbs dbs;
@Autowired private Bouw bouw;
@Autowired private Config config;
int grootste = Integer.MIN_VALUE;
List<BoStelling> grootsten = new ArrayList<>();
List<BoStelling> grootstenMinEen = new ArrayList<>();
String savedConfigString;

@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	dbs.open();
}
@AfterEach
public void destroy()
{
	dbs.close();
	config.switchConfig( savedConfigString );
}

void vindGrootste( BoStelling aBoStelling )
{
	int aantalZetten = aBoStelling.getAantalZetten();
	if ( aantalZetten > grootste )
	{
		grootste = aantalZetten;
	}
}
void vindGrootsten( BoStelling aBoStelling )
{
	if ( aBoStelling.getAantalZetten() == grootste )
	{
		grootsten.add(  aBoStelling );
	}
}
void vindGrootstenMinEen( BoStelling aBoStelling )
{
	if ( aBoStelling.getAantalZetten() == grootste - 1 )
	{
		grootstenMinEen.add(  aBoStelling );
	}
}
void doNothing( int aStellingTeller, int [][] aTellingen )
{
}
@Test
public void testGrootsteAantalZetten()
{
	getConfig().switchConfig( "KTK" );
	dbs.setReport( Integer.MAX_VALUE, this::doNothing, true );
	grootste = Integer.MIN_VALUE;
	dbs.pass( PassType.MarkeerWitEnZwart, this::vindGrootste );
	dbs.pass( PassType.MarkeerWitEnZwart, this::vindGrootsten );
	dbs.pass( PassType.MarkeerWitEnZwart, this::vindGrootstenMinEen );
	if ( DO_PRINT )
	{
		System.out.println( "Grootste aantal zetten tot mat: " + grootste );
		System.out.println( "Aantal stellingen: " + grootsten.size() );
		System.out.println(  grootsten );
		System.out.println( "Aantal min-1-stellingen: " + grootstenMinEen.size() );
		System.out.println(  grootstenMinEen );
	}
	assertThat( grootste, is (19 ) );
	assertThat( grootsten.size(), is (96 ) );
	assertThat( grootstenMinEen.size(), is (1119 ) );
}
@Test
public void testTelAllesKDKAanHetEinde()
{
//	if ( DO_PRINT )
	{
		System.out.println( "methode testTelAlles\n" );
	}
	doTestTelAllesAanHetEinde( "TestKDK" );
	doTestTelAllesAanHetEinde( "KDK" );
}
@Test
public void testTelAllesKDKTAanHetEinde()
{
//	if ( DO_PRINT )
	{
		System.out.println( "methode testTelAlles\n" );
	}
	//doTestTelAllesAanHetEinde( "KDK" );
	doTestTelAllesAanHetEinde( "TestKDKT" );
	doTestTelAllesAanHetEinde( "KDKT" );
	//doTestTelAllesAanHetEinde( "KDKTT" );
}
void doTestTelAllesAanHetEinde( String aConfigString )
{
	System.out.println( "\nTel alles in " + aConfigString + "\n" );
	getConfig().switchConfig( aConfigString );
	
	dbs.open();
	bouw.telAndPrintAlles( true );
}
@Test 
public void testIsIllegaal5Stukken_3()
{
	config.switchConfig( "TestKDKTT" );
	
	// Bug van 18-06-2026
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "a8" )
		.s4( "h8" )
		.s5( "h8" )
		.aanZet( Zwart )
		.build();
	assertThat( gen.isGeometrischIllegaal( boStelling ), is( false ) );
	BoStelling gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
}

@Test
public void testBug2026_06_18()
{
	config.switchConfig( "KDKTT" );
	
	// Bug van 18-06-2026
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "a8" )
		.s4( "h8" )
		.s5( "h8" )
		.aanZet( Zwart )
		.build();
	BoStelling gotBoStelling = dbs.get( boStelling );
	assertThat( gotBoStelling.getResultaat(), is( Remise ) );
}

}