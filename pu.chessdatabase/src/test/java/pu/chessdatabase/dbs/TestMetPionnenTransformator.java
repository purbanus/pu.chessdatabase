package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
@Test
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
	actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
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
	VMStelling vmStelling = getTransformator().spiegelEnRoteer( boStelling );
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
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
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

}
