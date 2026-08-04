package pu.chessdatabase.dbs;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.AlleenStukkenTransformator.*;

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
public class TestAlleenStukkenTransformator
{
private AlleenStukkenTransformator transformator = new AlleenStukkenTransformator();

/**
 * public static final int [] OktTabel = {
   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
   6,6,6,6,5,5,5,5
};
 */
@Autowired private Config config;

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
/*
0 -1 -2 -3 -4 -5 -6 -7 
8 7 6 5 4 3 2 1 
16 15 14 13 12 11 10 9 
24 23 22 21 20 19 18 17 
32 31 30 29 28 27 26 25 
40 39 38 37 36 35 34 33 
48 47 46 45 44 43 42 41 
56 55 54 53 52 51 50 49 

0->0 1->ffffffff 2->fffffffe 3->fffffffd 4->fffffffc 5->fffffffb 6->fffffffa 7->fffffff9 
10->8 11->7 12->6 13->5 14->4 15->3 16->2 17->1 
20->10 21->f 22->e 23->d 24->c 25->b 26->a 27->9 
30->18 31->17 32->16 33->15 34->14 35->13 36->12 37->11 
40->20 41->1f 42->1e 43->1d 44->1c 45->1b 46->1a 47->19 
50->28 51->27 52->26 53->25 54->24 55->23 56->22 57->21 
60->30 61->2f 62->2e 63->2d 64->2c 65->2b 66->2a 67->29 
70->38 71->37 72->36 73->35 74->34 75->33 76->32 77->31 

0->7 1->6 2->5 3->4 4->3 5->2 6->1 7->0 
10->f 11->e 12->d 13->c 14->b 15->a 16->9 17->8 
20->17 21->16 22->15 23->14 24->13 25->12 26->11 27->10 
30->1f 31->1e 32->1d 33->1c 34->1b 35->1a 36->19 37->18 
40->27 41->26 42->25 43->24 44->23 45->22 46->21 47->20 
50->2f 51->2e 52->2d 53->2c 54->2b 55->2a 56->29 57->28 
60->37 61->36 62->35 63->34 64->33 65->32 66->31 67->30 
70->3f 71->3e 72->3d 73->3c 74->3b 75->3a 76->39 77->38 

 */
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
	// De WK staat in oktant 3. Dit krijgt een rotatie over +90 graden
	actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b7" )
		.s3( "f1" )
		.s4( "c8" )
		.s5( "b7" )
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
	// De WK staat in oktant 4. Dit krijgt een rotatie van 180 graden om het middelpunt
	actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "c1" )
		.zk( "g7" )
		.s3( "d5" )
		.s4( "f8" )
		.s5( "h8" )
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
	// De WK zit in oktant 8. Dit krijgt een spiegeling in de diagonaal a1-h8 
	actualVmStelling = getTransformator().spiegelEnRoteer( boStelling );
	expectedVmStelling = VMStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "b4" )
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
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "h4" )
		.zk( "h6" )
		.s3( "g4" )
		.s4( "g6" )
		.s5( "h4" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 3. Dit krijgt een rotatie over -90 graden
	assertThat( getTransformator().getOktant( boStelling ), is( 3 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d1" )
		.zk( "f1" )
		.s3( "d2" )
		.s4( "f2" )
		.s5( "d1" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "h5" )
		.zk( "h7" )
		.s3( "g5" )
		.s4( "g7" )
		.s5( "h5" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 4. Dit krijgt een spiegeling in de diagonaal a8-h1
	assertThat( getTransformator().getOktant( boStelling ), is( 4 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d1" )
		.zk( "b1" )
		.s3( "d2" )
		.s4( "b2" )
		.s5( "d1" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "f5" )
		.zk( "f7" )
		.s3( "e5" )
		.s4( "e7" )
		.s5( "f5" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 4. Dit krijgt een spiegeling in de diagonaal a8-h1
	assertThat( getTransformator().getOktant( boStelling ), is( 4 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d3" )
		.zk( "b3" )
		.s3( "d4" )
		.s4( "b4" )
		.s5( "d3" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "e5" )
		.zk( "e3" )
		.s3( "f5" )
		.s4( "f3" )
		.s5( "e5" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 5. Dit krijgt een spiegeling in de x-as gevolgd door een spiegeling in de y-as
	assertThat( getTransformator().getOktant( boStelling ), is( 5 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "c4" )
		.s4( "c6" )
		.s5( "d4" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "d5" )
		.zk( "d3" )
		.s3( "e5" )
		.s4( "e3" )
		.s5( "d5" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 6. Dit krijgt een spiegeling in de x-as
	assertThat( getTransformator().getOktant( boStelling ), is( 6 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "e4" )
		.s4( "e6" )
		.s5( "d4" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "c5" )
		.zk( "c3" )
		.s3( "d5" )
		.s4( "d3" )
		.s5( "c5" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 7. Dit krijgt een rotatie over +90 graden
	assertThat( getTransformator().getOktant( boStelling ), is( 7 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d3" )
		.zk( "f3" )
		.s3( "d4" )
		.s4( "f4" )
		.s5( "d3" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "c4" )
		.zk( "d4" )
		.s3( "c6" )
		.s4( "d6" )
		.s5( "c4" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 8. Dit krijgt een spiegeling in de diagonaal a1-h8
	assertThat( getTransformator().getOktant( boStelling ), is( 8 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d3" )
		.zk( "d4" )
		.s3( "f3" )
		.s4( "f4" )
		.s5( "d3" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	// Oude stijl
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.s5( 0x10 )
		.aanZet( Wit )
		.build();
	// De WK zit in oktant 8. Dit krijgt een spiegeling in de diagonaal a1-h8 
	assertThat( getTransformator().getOktant( boStelling ), is( 8 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.builder()
		.wk( 0x01 )
		.zk( 0x11 )
		.s3( 0x00 )
		.s4( 0x19 )
		.s5( 0x01 )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
}
@Test
public void testSpiegelEnRoteerOktant5()
{
	// We proberen te bewijzen dat oktant 5 twee keer over -90 graden roteert
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "e5" )
		.zk( "e3" )
		.s3( "f5" )
		.s4( "f3" )
		.s5( "e5" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 5, maar we gaan roteren in oktant 3
	VMStelling vmStelling = getTransformator().spiegelEnRoteer( boStelling, 3 );
	VMStelling newVmStelling = VMStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "c4" )
		.s3( "e3" )
		.s4( "c3" )
		.s5( "e4" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	// Nogmaals over -90 graden roteren
	boStelling = boStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "c4" )
		.s3( "e3" )
		.s4( "c3" )
		.s5( "e4" )
		.aanZet( Wit )
		.build();
	vmStelling = getTransformator().spiegelEnRoteer( boStelling, 3 );
	VMStelling oktant3Stelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "c4" )
		.s4( "c6" )
		.s5( "d4" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( oktant3Stelling ) );
	
	// Vergelijken met de transformatie in oktant 5
	boStelling = BoStelling.alfaBuilder()
		.wk( "e5" )
		.zk( "e3" )
		.s3( "f5" )
		.s4( "f3" )
		.s5( "e5" )
		.aanZet( Wit )
		.build();
	// De WK staat in oktant 5. Dit krijgt een spiegeling in de x-as gevolgd door een spiegeling in de y-as
	assertThat( getTransformator().getOktant( boStelling ), is( 5 ) );
	vmStelling = getTransformator().spiegelEnRoteer( boStelling );
	VMStelling oktant5Stelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "c4" )
		.s4( "c6" )
		.s5( "d4" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( oktant3Stelling ) );
	assertThat( oktant3Stelling, is( oktant5Stelling ) );
}

}
