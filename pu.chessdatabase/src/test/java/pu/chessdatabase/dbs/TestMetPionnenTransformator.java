package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.MetPionnenTransformator.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.services.Vector;

import lombok.Data;

@SpringBootTest
@Data
public class TestMetPionnenTransformator
{
//public static final int [] NAAR_VM_STELLING = 
//{
//	0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
//	0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
//	0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
//	0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,0x1f,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
//	0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
//	0x28,0x29,0x2a,0x2b,0x2c,0x2d,0x2e,0x2f,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
//	0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
//	0x38,0x39,0x3a,0x3b,0x3c,0x3d,0x3e,0x3f,
//};

@Autowired private Config config;
private MetPionnenTransformator transformator = new MetPionnenTransformator();

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( Config.PipoKDKT );
}
@AfterEach
public void destroy()
{
	config.switchConfig( savedConfigString );
}
@Test
public void testCreateTransformatieTabel()
{
	// Laten we beginnen in oktant 1. Alles is identiek behalve dat VMStelling maar 8 kolommen per rij heeft. 
	int oktant = 1;
	for ( int rij : RIJ_RANGE )
	{
		for ( int kol : KOL_RANGE )
		{
			assertThat( getTransformator().transformatieTabel[oktant][kol + 16 * rij], is( kol + 8 * rij ) );
		}
	}
	// Oktant 2 is een spiegeling over de y-as
	oktant = 2;
	for ( int rij : RIJ_RANGE )
	{
		for ( int kol : KOL_RANGE )
		{
			Vector vector = new Vector( kol, rij );
			Vector resVector = getTransformator().MATRIX_TABEL[oktant].multiply( vector );
			resVector = resVector.add( getTransformator().TRANSLATIE_TABEL[oktant] );
			int oudVeld = kol + 16 * rij;
			int newVeld = resVector.get( 0 ) + 8 * resVector.get( 1 );
			//System.out.print( Integer.toHexString( oudVeld ) + "->" + Integer.toHexString( newVeld ) + " " );
			assertThat( getTransformator().transformatieTabel[oktant][oudVeld], is( newVeld ) );
		}
		//System.out.println();
	}
}
//@Test
public void printTrfTabel()
{
	for ( int oktant : OKTANT_RANGE )
	{
		for ( int x : VM_VELD_RANGE )
		{
			System.out.print( Integer.toHexString( getTransformator().transformatieTabel[oktant][x] ) + " " );
		}
		System.out.println();
	}
}
@Test
public void testVmStellingWkToBoStellingWk()
{
	assertThat( getTransformator().vmStellingWkToBoStellingWk(  0 ), is( 0x00 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk(  1 ), is( 0x01 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk(  2 ), is( 0x02 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk(  3 ), is( 0x03 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 4 ) );
	
	assertThat( getTransformator().vmStellingWkToBoStellingWk(  8 ), is( 0x10 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk(  9 ), is( 0x11 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 10 ), is( 0x12 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 11 ), is( 0x13 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 12 ) );
	
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 16 ), is( 0x20 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 17 ), is( 0x21 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 18 ), is( 0x22 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 19 ), is( 0x23 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 20 ) );

	assertThat( getTransformator().vmStellingWkToBoStellingWk( 24 ), is( 0x30 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 25 ), is( 0x31 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 26 ), is( 0x32 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 27 ), is( 0x33 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 28 ) );

	assertThat( getTransformator().vmStellingWkToBoStellingWk( 32 ), is( 0x40 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 33 ), is( 0x41 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 34 ), is( 0x42 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 35 ), is( 0x43 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 36 ) );

	assertThat( getTransformator().vmStellingWkToBoStellingWk( 40 ), is( 0x50 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 41 ), is( 0x51 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 42 ), is( 0x52 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 43 ), is( 0x53 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 44 ) );

	assertThat( getTransformator().vmStellingWkToBoStellingWk( 48 ), is( 0x60 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 49 ), is( 0x61 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 50 ), is( 0x62 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 51 ), is( 0x63 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 52 ) );

	assertThat( getTransformator().vmStellingWkToBoStellingWk( 56 ), is( 0x70 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 57 ), is( 0x71 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 58 ), is( 0x72 ) );
	assertThat( getTransformator().vmStellingWkToBoStellingWk( 59 ), is( 0x73 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToBoStellingWk( 60 ) );
}
@Test
public void testVmStellingWkToLinear()
{
	assertThat( getTransformator().vmStellingWkToLinear( 0 ), is( 0x00 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 1 ), is( 0x01 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 2 ), is( 0x02 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 3 ), is( 0x03 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 4 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 8 ), is( 0x04 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 9 ), is( 0x05) );
	assertThat( getTransformator().vmStellingWkToLinear( 10 ), is( 0x06 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 11 ), is( 0x07 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 12 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 16 ), is( 0x08 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 17 ), is( 0x09 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 18 ), is( 0x0a ) );
	assertThat( getTransformator().vmStellingWkToLinear( 19 ), is( 0x0b ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 20 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 24 ), is( 0x0c ) );
	assertThat( getTransformator().vmStellingWkToLinear( 25 ), is( 0x0d ) );
	assertThat( getTransformator().vmStellingWkToLinear( 26 ), is( 0x0e ) );
	assertThat( getTransformator().vmStellingWkToLinear( 27 ), is( 0x0f ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 12 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 16 ), is( 0x08 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 17 ), is( 0x09 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 18 ), is( 0x0a ) );
	assertThat( getTransformator().vmStellingWkToLinear( 19 ), is( 0x0b ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 20 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 24 ), is( 0x0c ) );
	assertThat( getTransformator().vmStellingWkToLinear( 25 ), is( 0x0d ) );
	assertThat( getTransformator().vmStellingWkToLinear( 26 ), is( 0x0e ) );
	assertThat( getTransformator().vmStellingWkToLinear( 27 ), is( 0x0f ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 28 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 32 ), is( 0x10 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 33 ), is( 0x11 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 34 ), is( 0x12 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 35 ), is( 0x13 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 36 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 40 ), is( 0x14 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 41 ), is( 0x15 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 42 ), is( 0x16 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 43 ), is( 0x17 ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 44 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 48 ), is( 0x18 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 49 ), is( 0x19 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 50 ), is( 0x1a ) );
	assertThat( getTransformator().vmStellingWkToLinear( 51 ), is( 0x1b ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 52 ) );
	assertThat( getTransformator().vmStellingWkToLinear( 56 ), is( 0x1c ) );
	assertThat( getTransformator().vmStellingWkToLinear( 57 ), is( 0x1d ) );
	assertThat( getTransformator().vmStellingWkToLinear( 58 ), is( 0x1e ) );
	assertThat( getTransformator().vmStellingWkToLinear( 59 ), is( 0x1f ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkToLinear( 60 ) );
}
@Test
public void testVmStellingWkFromLinear()
{
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkFromLinear( -31415 ) );

	assertThat( getTransformator().vmStellingWkFromLinear(  0 ), is( 0x00 ) );
	assertThat( getTransformator().vmStellingWkFromLinear(  1 ), is( 0x01 ) );
	assertThat( getTransformator().vmStellingWkFromLinear(  2 ), is( 0x02 ) );
	assertThat( getTransformator().vmStellingWkFromLinear(  3 ), is( 0x03 ) );

	assertThat( getTransformator().vmStellingWkFromLinear(  4 ), is( 0x08 ) );
	assertThat( getTransformator().vmStellingWkFromLinear(  5 ), is( 0x09 ) );
	assertThat( getTransformator().vmStellingWkFromLinear(  6 ), is( 0x0a ) );
	assertThat( getTransformator().vmStellingWkFromLinear(  7 ), is( 0x0b ) );

	assertThat( getTransformator().vmStellingWkFromLinear(  8 ), is( 0x10 ) );
	assertThat( getTransformator().vmStellingWkFromLinear(  9 ), is( 0x11 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 10 ), is( 0x12 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 11 ), is( 0x13 ) );

	assertThat( getTransformator().vmStellingWkFromLinear( 12 ), is( 0x18 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 13 ), is( 0x19 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 14 ), is( 0x1a ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 15 ), is( 0x1b ) );

	assertThat( getTransformator().vmStellingWkFromLinear( 16 ), is( 0x20 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 17 ), is( 0x21 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 18 ), is( 0x22 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 19 ), is( 0x23 ) );
	
	assertThat( getTransformator().vmStellingWkFromLinear( 20 ), is( 0x28 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 21 ), is( 0x29 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 22 ), is( 0x2a ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 23 ), is( 0x2b ) );
	
	assertThat( getTransformator().vmStellingWkFromLinear( 24 ), is( 0x30 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 25 ), is( 0x31 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 26 ), is( 0x32 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 27 ), is( 0x33 ) );
	
	assertThat( getTransformator().vmStellingWkFromLinear( 28 ), is( 0x38 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 29 ), is( 0x39 ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 30 ), is( 0x3a ) );
	assertThat( getTransformator().vmStellingWkFromLinear( 31 ), is( 0x3b ) );
	assertThrows( RuntimeException.class, () -> getTransformator().vmStellingWkFromLinear( 32 ) );
}

@Test
public void testSpiegelEnRoteerAlleenWk()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	VMStelling expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	
	boStelling.setWkAlfa( "b2" );
	// De WK staat in oktant 1, dit krijgt een identieke afbeelding,
	VMStelling actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
	expectedVmStelling.setWkAlfa( "b2" );
	assertThat( actualVmStelling, is( expectedVmStelling ) );

	boStelling.setWkAlfa( "g1" );
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as van het midden van het bord
	actualVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	expectedVmStelling.setWkAlfa( "b1" );
	expectedVmStelling.setZkAlfa( "h1" );
	expectedVmStelling.setS3Alfa( "h1" ); 
	expectedVmStelling.sets4Alfa( "h1" );
	expectedVmStelling.sets5Alfa( "h1" );
	assertThat( actualVmStelling, is( expectedVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "h2" )
		.zk( "b2" )
		.s3( "h6" )
		.s4( "a3" )
		.s5( "b2" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as van het midden van het bord
	actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "g2" )
		.s3( "a6" )
		.s4( "h3" )
		.s5( "g2" )
		.aanZet( Wit )
		.build();
	assertThat( actualVmStelling, is( expectedVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "h6" )
		.zk( "b2" )
		.s3( "d5" )
		.s4( "a3" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as van het midden van het bord
	actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a6" )
		.zk( "g2" )
		.s3( "e5" )
		.s4( "h3" )
		.s5( "h1" )
		.aanZet( Wit )
		.build();
	assertThat( actualVmStelling, is( expectedVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "c2" )
		.s3( "a1" )
		.s4( "d2" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 1, dit krijgt een identieke afbeelding,
	actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "c2" )
		.s3( "a1" )
		.s4( "d2" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	assertThat( actualVmStelling, is( expectedVmStelling ) );
}
@Test
public void testSpiegelEnRoteer()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "a3" )
		.s5( "b1" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 1, dit krijgt een identieke afbeelding,
	assertThat( getTransformator().getOktant( boStelling ), is( 1 ) );
	VMStelling vmStelling = getTransformator().boStellingToVmStelling( boStelling );
	VMStelling newVmStelling = VMStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "a3" )
		.s5( "b1" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "g1" )
		.zk( "g3" )
		.s3( "h1" )
		.s4( "h3" )
		.s5( "g1" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as
	assertThat( getTransformator().getOktant( boStelling ), is( 2 ) );
	vmStelling = getTransformator().boStellingToVmStelling( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "a3" )
		.s5( "b1" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
}
@Test
public void bug20260806()
{
	// 1e oktant
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	VMStelling vmStelling = VMStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	VMStelling newVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	assertThat( newVmStelling, is( vmStelling ) );
	// 1e oktant

	boStelling = BoStelling.alfaBuilder()
		.wk( "e1" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	vmStelling = VMStelling.alfaBuilder()
		.wk( "d1" )
		.zk( "h1" )
		.s3( "h1" )
		.s4( "h1" )
		.s5( "h1" )
		.aanZet( Wit )
		.build();
	newVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	assertThat( newVmStelling, is( vmStelling ) );
}
@Test
public void testBoStellingToVmStelling()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	VMStelling expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "a1" )
		.s3( "a1" )
		.s4( "a1" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	
	boStelling.setWkAlfa( "b2" );
	// De WK staat in oktant 1, dit krijgt een identieke afbeelding,
	VMStelling actualVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	expectedVmStelling.setWkAlfa( "b2" );
	assertThat( actualVmStelling, is( expectedVmStelling ) );

	boStelling.setWkAlfa( "g1" );
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as van het midden van het bord
	actualVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	expectedVmStelling.setWkAlfa( "b1" );
	expectedVmStelling.setZkAlfa( "h1" );
	expectedVmStelling.setS3Alfa( "h1" ); 
	expectedVmStelling.sets4Alfa( "h1" );
	expectedVmStelling.sets5Alfa( "h1" );
	assertThat( actualVmStelling, is( expectedVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "h2" )
		.zk( "b2" )
		.s3( "h6" )
		.s4( "a3" )
		.s5( "b2" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as van het midden van het bord
	actualVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "g2" )
		.s3( "a6" )
		.s4( "h3" )
		.s5( "g2" )
		.aanZet( Wit )
		.build();
	assertThat( actualVmStelling, is( expectedVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "h6" )
		.zk( "b2" )
		.s3( "d5" )
		.s4( "a3" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as van het midden van het bord
	actualVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a6" )
		.zk( "g2" )
		.s3( "e5" )
		.s4( "h3" )
		.s5( "h1" )
		.aanZet( Wit )
		.build();
	assertThat( actualVmStelling, is( expectedVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "c2" )
		.s3( "a1" )
		.s4( "d2" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 1, dit krijgt een identieke afbeelding,
	actualVmStelling = getTransformator().boStellingToVmStelling( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "a2" )
		.zk( "c2" )
		.s3( "a1" )
		.s4( "d2" )
		.s5( "a1" )
		.aanZet( Wit )
		.build();
	assertThat( actualVmStelling, is( expectedVmStelling ) );
}

}
