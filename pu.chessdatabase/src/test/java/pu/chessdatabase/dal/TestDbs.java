package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Bouw;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Gen;
import pu.services.Vector;

import lombok.Data;


@SpringBootTest
@Data
public class TestDbs
{
private static final String DATABASE_NAME = "dbs/Pipo";
@Autowired private Dbs dbs;
@Autowired private Bouw bouw;
@Autowired private VM vm;
@Autowired private Gen gen;
@Autowired private Config config;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( "TestKDKT", false ); // false want de database bestaat nog niet dus VM kan m niet openen
	dbs.create(); // Doet ook Open, dus initialiseert de tabellen
}
@AfterEach
public void destroy()
{
	assertThat( dbs.getDatabaseName(), is( DATABASE_NAME ) );
	dbs.delete();
	config.switchConfig( savedConfigString );
}
@Test
public void testResultaatRange()
{
	assertThat( dbs.resultaatRange.getMinimum(), is( 0 ) );
	assertThat( dbs.resultaatRange.getMaximum(), is( 3 ) );
}

@Test
public void testClearTellers()
{
	dbs.clearTellers();
	for ( int x = dbs.resultaatRange.getMinimum(); x < dbs.resultaatRange.getMaximum() + 1; x++ )
	{
		assertThat( dbs.report[x], is( 0L ) );
	}
	assertThat( dbs.reportTeller, is( 0 ) );
}
static boolean messageGegeven = false;
private void doReport( long [] aReportArray )
{
	if ( !messageGegeven )
	{
		System.out.println( "Dit is een ReportProc" );
	}
	messageGegeven = true;
}
//public void doReport2( long [] aReportArray )
//{
//	System.out.println( aReportArray );
//}
@Test
public void testSetReport()
{
	messageGegeven = false;
	dbs.setReport( 5000, this::doReport );
	assertThat( dbs.reportFrequentie, is( 5000 ) );
	// Je kunt lambdas niet vergelijken, behalve misschien met Serializable; viond ik overdone
	//assertThat( dbs.RptProc, is( this::doReport ) );
}

@Test
public void testUpdateTellers()
{
	messageGegeven = false;
	dbs.setReport( 5000, this::doReport );
	dbs.report = new long [] { 1L, 2L, 3L, 4L };
	dbs.updateTellers( ResultaatType.GEWONNEN );
	assertThat( dbs.report[ResultaatType.GEWONNEN.ordinal()], is( 3L ) );
	assertThat( dbs.reportTeller, is( 1 ) );
}
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
@Test
public void testCreateTrfTabel()
{
	// Laten we beginnen in oktant 1. Alles is identiek behalve dat VMStelling maar 8 kolommen per rij heeft. 
	int oktant = 1;
	for ( int rij = 0; rij < 8; rij++ )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			assertThat( dbs.transformatieTabel[oktant][kol + 16 * rij], is( kol + 8 * rij ) );
		}
	}
	// Oktant 2 is een spiegeling over de y-as
	oktant = 2;
	for ( int rij = 0; rij < 8; rij++ )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			Vector vector = new Vector( kol, rij );
			Vector resVector = dbs.MATRIX_TABEL[oktant].multiply( vector );
			resVector = resVector.add( dbs.TRANSLATIE_TABEL[oktant] );
			int oudVeld = kol + 16 * rij;
			int newVeld = resVector.get( 0 ) + 8 * resVector.get( 1 );
			//System.out.print( Integer.toHexString( oudVeld ) + "->" + Integer.toHexString( newVeld ) + " " );
			assertThat( dbs.transformatieTabel[oktant][oudVeld], is( newVeld ) );
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
	for ( int oktant = 1; oktant <= dbs.OKTANTEN; oktant++ )
	{
		for ( int x = dbs.veldRange.getMinimum(); x < dbs.veldRange.getMaximum(); x++ )
		{
			System.out.print( Integer.toHexString( dbs.transformatieTabel[oktant][x] ) + " " );
		}
		System.out.println();
	}
}
@Test
public void testCardinaliseer()
{
	// Test Oktant 1: behalve dan dat de WK nog 
	// een speciale afbeelding krijgt om 'm in de VM-notatie te krijgen; hij gaat van 9 naar 4.
}
int transFormVeld( int aVeld )
{
	return 0;
}
@Test
public void testSpiegelEnRoteerAlleenWk()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0 )
		.zk( 0 )
		.s3( 0 )
		.s4( 0 )
		.aanZet( WIT )
		.build();
	VMStelling expectedVmStelling = VMStelling.builder()
		.wk( 0 )
		.zk( 0 )
		.s3( 0 )
		.s4( 0 )
		.aanZet( WIT )
		.build();
	
	boStelling.setWk( 0x11 );
	// De WK staat in oktant 1, dit krijgt een identieke afbeelding,
	VMStelling actualVmStelling = dbs.spiegelEnRoteer( boStelling );
	expectedVmStelling.setWk( 0x09 );
	assertThat( actualVmStelling, is( actualVmStelling ) );

	boStelling.setWk( 0x06 );
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as van het midden van het bord
	actualVmStelling = dbs.spiegelEnRoteer( boStelling );
	expectedVmStelling.setWk( 0x01 );
	assertThat( actualVmStelling, is( actualVmStelling ) );

	boStelling = BoStelling.builder()
		.wk( 0x17 )
		.zk( 0x11 )
		.s3( 0x57 )
		.s4( 0x20 )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 3. Dit krijgt een rotatie over +90 graden
	actualVmStelling = dbs.spiegelEnRoteer( boStelling );
	actualVmStelling = VMStelling.builder()
		.wk( 0x01 )
		.zk( 0x31 )
		.s3( 0x05 )
		.s4( 0x3a )
		.aanZet( WIT )
		.build();
	assertThat( actualVmStelling, is( actualVmStelling ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x57 )
		.zk( 0x11 )
		.s3( 0x43 )
		.s4( 0x20 )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 4. Dit krijgt een rotatie van 180 graden om het middelpunt
	actualVmStelling = dbs.spiegelEnRoteer( boStelling );
	actualVmStelling = VMStelling.builder()
		.wk( 0x01 )
		.zk( 0x31 )
		.s3( 0x05 )
		.s4( 0x3a )
		.aanZet( WIT )
		.build();
	assertThat( actualVmStelling, is( actualVmStelling ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.build();
	// De WK zit in oktant 8. Dit krijgt een spiegeling in de diagonaal a1-h8 
	actualVmStelling = dbs.spiegelEnRoteer( boStelling );
	actualVmStelling = VMStelling.builder()
		.wk( 0x01 )
		.zk( 0x11 )
		.s3( 0x00 )
		.s4( 0x19 )
		.aanZet( WIT )
		.build();
	assertThat( actualVmStelling, is( actualVmStelling ) );
	
}
public static final int [] OKTANTEN_TABEL = {
	   1,1,1,1,2,2,2,2,0,0,0,0,0,0,0,0,
	   8,1,1,1,2,2,2,3,0,0,0,0,0,0,0,0,
	   8,8,1,1,2,2,3,3,0,0,0,0,0,0,0,0,
	   8,8,8,1,2,3,3,3,0,0,0,0,0,0,0,0,
	   7,7,7,6,5,4,4,4,0,0,0,0,0,0,0,0,
	   7,7,6,6,5,5,4,4,0,0,0,0,0,0,0,0,
	   7,6,6,6,5,5,5,4,0,0,0,0,0,0,0,0,
	   6,6,6,6,5,5,5,5
	};

@Test
public void testSpiegelEnRoteer()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "a3" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 1, dit krijgt een identieke afbeelding,
	assertThat( dbs.getOktant( boStelling ), is( 1 ) );
	VMStelling vmStelling = dbs.spiegelEnRoteer( boStelling );
	VMStelling newVmStelling = VMStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "a3" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "g1" )
		.zk( "g3" )
		.s3( "h1" )
		.s4( "h3" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 2. Dit krijgt een spiegeling in de y-as
	assertThat( dbs.getOktant( boStelling ), is( 2 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "b1" )
		.zk( "b3" )
		.s3( "a1" )
		.s4( "a3" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "h4" )
		.zk( "h6" )
		.s3( "g4" )
		.s4( "g6" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 3. Dit krijgt een rotatie over -90 graden
	assertThat( dbs.getOktant( boStelling ), is( 3 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d1" )
		.zk( "f1" )
		.s3( "d2" )
		.s4( "f2" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "h5" )
		.zk( "h7" )
		.s3( "g5" )
		.s4( "g7" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 4. Dit krijgt een spiegeling in de diagonaal a8-h1
	assertThat( dbs.getOktant( boStelling ), is( 4 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d1" )
		.zk( "b1" )
		.s3( "d2" )
		.s4( "b2" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "f5" )
		.zk( "f7" )
		.s3( "e5" )
		.s4( "e7" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 4. Dit krijgt een spiegeling in de diagonaal a8-h1
	assertThat( dbs.getOktant( boStelling ), is( 4 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d3" )
		.zk( "b3" )
		.s3( "d4" )
		.s4( "b4" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	boStelling = BoStelling.alfaBuilder()
		.wk( "e5" )
		.zk( "e3" )
		.s3( "f5" )
		.s4( "f3" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 5. Dit krijgt een spiegeling in de x-as gevolgd door een spiegeling in de y-as
	assertThat( dbs.getOktant( boStelling ), is( 5 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "c4" )
		.s4( "c6" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "d5" )
		.zk( "d3" )
		.s3( "e5" )
		.s4( "e3" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 6. Dit krijgt een spiegeling in de x-as
	assertThat( dbs.getOktant( boStelling ), is( 6 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "e4" )
		.s4( "e6" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "c5" )
		.zk( "c3" )
		.s3( "d5" )
		.s4( "d3" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 7. Dit krijgt een rotatie over +90 graden
	assertThat( dbs.getOktant( boStelling ), is( 7 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d3" )
		.zk( "f3" )
		.s3( "d4" )
		.s4( "f4" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "c4" )
		.zk( "d4" )
		.s3( "c6" )
		.s4( "d6" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 8. Dit krijgt een spiegeling in de diagonaal a1-h8
	assertThat( dbs.getOktant( boStelling ), is( 8 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "d3" )
		.zk( "d4" )
		.s3( "f3" )
		.s4( "f4" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );

	// Oude stijl
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.build();
	// De WK zit in oktant 8. Dit krijgt een spiegeling in de diagonaal a1-h8 
	assertThat( dbs.getOktant( boStelling ), is( 8 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	newVmStelling = VMStelling.builder()
		.wk( 0x01 )
		.zk( 0x11 )
		.s3( 0x00 )
		.s4( 0x19 )
		.aanZet( WIT )
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
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 5, maar we gaan roteren in oktant 3
	VMStelling vmStelling = dbs.spiegelEnRoteer( boStelling, 3 );
	VMStelling newVmStelling = VMStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "c4" )
		.s3( "e3" )
		.s4( "c3" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	// Nogmaals over -90 graden roteren
	boStelling = boStelling.alfaBuilder()
		.wk( "e4" )
		.zk( "c4" )
		.s3( "e3" )
		.s4( "c3" )
		.aanZet( WIT )
		.build();
	vmStelling = dbs.spiegelEnRoteer( boStelling, 3 );
	VMStelling oktant3Stelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "c4" )
		.s4( "c6" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( oktant3Stelling ) );
	
	// Vergelijken met de transformatie in oktant 5
	boStelling = BoStelling.alfaBuilder()
		.wk( "e5" )
		.zk( "e3" )
		.s3( "f5" )
		.s4( "f3" )
		.aanZet( WIT )
		.build();
	// De WK staat in oktant 5. Dit krijgt een spiegeling in de x-as gevolgd door een spiegeling in de y-as
	assertThat( dbs.getOktant( boStelling ), is( 5 ) );
	vmStelling = dbs.spiegelEnRoteer( boStelling );
	VMStelling oktant5Stelling = VMStelling.alfaBuilder()
		.wk( "d4" )
		.zk( "d6" )
		.s3( "c4" )
		.s4( "c6" )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling, is( oktant3Stelling ) );
	assertThat( oktant3Stelling, is( oktant5Stelling ) );
}

@Test
public void testPut()
{
//	VMillegaal      = 0x0FF;
//	VMremise        = 0x000;
//	VMschaak        = 0x080;
//	VerliesOffset   = 0x080;
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.resultaat( ResultaatType.ILLEGAAL )
		.aantalZetten( 0 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	BoStelling newBoStelling = dbs.get( boStelling );
	newBoStelling.setSchaak( gen.isSchaak( newBoStelling ) );
	// Dit moet je niet doen want het is altijd true!!
	// assertThat( newBoStelling, is( boStelling ) );
	assertThat( newBoStelling.getResultaat(), is( ResultaatType.ILLEGAAL ) );
	assertThat( newBoStelling.getAantalZetten(), is( 0 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.resultaat( ResultaatType.REMISE )
		.aantalZetten( 0 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( ResultaatType.REMISE ) );
	assertThat( newBoStelling.getAantalZetten(), is( 0 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.resultaat( ResultaatType.GEWONNEN )
		.aantalZetten( 13 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( ResultaatType.GEWONNEN ) );
	assertThat( newBoStelling.getAantalZetten(), is( 13 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.resultaat( ResultaatType.VERLOREN )
		.aantalZetten( 27 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( ResultaatType.VERLOREN ) );
	assertThat( newBoStelling.getAantalZetten(), is( 27 ) );
	assertThat( newBoStelling.isSchaak(), is( false ) );
	
	boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.resultaat( ResultaatType.REMISE )
		.aantalZetten( 27 )
		.schaak( true )
		.build();
	dbs.put( boStelling );
	
	newBoStelling = dbs.get( boStelling );
	assertThat( newBoStelling.getResultaat(), is( ResultaatType.REMISE ) );
	assertThat( newBoStelling.getAantalZetten(), is( 0 ) );
	assertThat( newBoStelling.isSchaak(), is( true ) );

}
@Test
public void testGet()
{
	// Is hierboven al flink getest
}
@Test
public void testGetDirect()
{
	// Is hierboven al flink getest
}
@Test
public void testFreeRecord()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x10 )
		.zk( 0x12 )
		.s3( 0x00 )
		.s4( 0x13 )
		.aanZet( WIT )
		.resultaat( ResultaatType.VERLOREN )
		.aantalZetten( 27 )
		.schaak( false )
		.build();
	dbs.put( boStelling );
	dbs.freeRecord( boStelling );
	// Dit is verder in TestVM al getest
}
@Test
public void testSetDatabaseName()
{
	dbs.setDatabaseName( "Mamaloe" );
	assertThat( dbs.getDatabaseName(), is( "Mamaloe" ) );
	dbs.setDatabaseName( DATABASE_NAME );
}
@Test
public void testCreate()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testOpen()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testClose()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testDelete()
{
	// Dit is verder in TestVM al getest
}
@Test
public void testPass34()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x00 )
		.s3( 0x11 )
		.s4( 0x13 )
		.aanZet( WIT )
		.resultaat( ResultaatType.GEWONNEN )
		.aantalZetten( 13 )
		.schaak( false )
		.build();
	VMStelling vmStelling = dbs.cardinaliseer( boStelling );
	dbs.pass34( boStelling, vmStelling, getBouw()::isIllegaal );
	dbs.flush();
	vmStelling.setAanZet( WIT );
	Page page = vm.getPage( vmStelling );
	assertThat( TestHelper.isAll( page.getData(), (byte)0xff ), is( true ) );
	
	vmStelling.setAanZet( ZWART );
	page = vm.getPage( vmStelling );
	assertThat( TestHelper.isAll( page.getData(), (byte)0xff ), is( true ) );
}
void set0x0b( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( ResultaatType.GEWONNEN );
	aBoStelling.setAantalZetten( 0x0b );
	dbs.put( aBoStelling );
}
void set0x8b( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( ResultaatType.GEWONNEN );
	aBoStelling.setAantalZetten( 0x8b );
	dbs.put( aBoStelling );
}
@Test
public void testMarkeerWitPassMet0x0b()
{
	dbs.markeerWitPass( this::set0x0b );
	dbs.flush();
	
	// De even pagina's moeten nu allmaal 0x0b zijn, oftewel alle pagina's met wit aan zet
	VMStelling vmStelling = new VMStelling();
	Page page;
	for ( int wk = 0; wk < 10; wk++)
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			vmStelling.setAanZet( WIT );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x0b ), is( true ) );
			vmStelling.setAanZet( ZWART );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x00 ), is( true ) );
		}
	}
}
@Test
public void testMarkeerWitPassMet0x8b()
{
	dbs.markeerWitPass( this::set0x8b );
	dbs.flush();
	
	// De even pagina's moeten nu allmaal 0x0b zijn, oftewel alle pagina's met wit aan zet
	VMStelling vmStelling = new VMStelling();
	Page page;
	for ( int wk = 0; wk < 10; wk++)
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			vmStelling.setAanZet( WIT );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x8b ), is( true ) );
			vmStelling.setAanZet( ZWART );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x00 ), is( true ) );
		}
	}
}

void set0x11( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( ResultaatType.GEWONNEN );
	aBoStelling.setAantalZetten( 0x11 );
	dbs.put( aBoStelling );
}
@Test
public void testMarkeerZwartPass()
{
	dbs.markeerZwartPass( this::set0x11 );
	dbs.flush();
	
	// De even pagina's moeten nu allmaal 0x11 zijn, oftewel alle pagina's met zwart aan zet
	VMStelling vmStelling = new VMStelling();
	Page page;
	for ( int wk = 0; wk < 10; wk++ )
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			vmStelling.setAanZet( WIT );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x00 ), is( true ) );
			vmStelling.setAanZet( ZWART );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x11 ), is( true ) );
		}
	}
}
void set0x34( BoStelling aBoStelling )
{
	aBoStelling.setResultaat( ResultaatType.GEWONNEN );
	aBoStelling.setAantalZetten( 0x34 );
	dbs.put( aBoStelling );
}
@Test
public void testMarkeerWitEnZwartPass()
{
	dbs.markeerWitEnZwartPass( this::set0x34 );
	dbs.flush();
	
	// Alle pagina's moeten nu 0x34 zijn
	VMStelling vmStelling = new VMStelling();
	Page page;
	vmStelling.setAanZet( ZWART );
	for ( int wk = 0; wk < 10; wk++ )
	{
		vmStelling.setWk( wk );
		for ( int zk = 0; zk < 64; zk++ )
		{
			vmStelling.setZk( zk );
			vmStelling.setAanZet( WIT );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x34 ), is( true ) );
			vmStelling.setAanZet( ZWART );
			page = vm.getPage( vmStelling );
			assertThat( TestHelper.isAll( page.getData(), (byte)0x34 ), is( true ) );
		}
	}
}
@Test
public void testPass()
{
	dbs.pass( PassType.MARKEER_WIT, this::set0x0b );
	dbs.pass( PassType.MARKEER_ZWART, this::set0x0b );
	dbs.pass( PassType.MARKEER_WIT_EN_ZWART, this::set0x0b );
	// Die markeer-methodes zijn hierboven al getest, we checken hier alleen of de aanroepjes goed gaan
}






/*
public static final int [] OktTabel = {
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
/*
 7  8  9  a  b  c  d  e 0 0 0 0 0 0 0 0 
 f 10 11 12 13 14 15 16 0 0 0 0 0 0 0 0 
17 18 19 1a 1b 1c 1d 1e 0 0 0 0 0 0 0 0 
1f 20 21 22 23 24 25 26 0 0 0 0 0 0 0 0 
27 28 29 2a 2b 2c 2d 2e 0 0 0 0 0 0 0 0 
2f 30 31 32 33 34 35 36 0 0 0 0 0 0 0 0 
37 38 39 3a 3b 3c 3d 3e 0 0 0 0 0 0 0 0 
3f 40 41 42 43 44 45 

38 37 36 35 34 33 32 31 0 0 0 0 0 0 0 0 40 3f 3e 3d 3c 3b 3a 39 0 0 0 0 0 0 0 0 48 47 46 45 44 43 42 41 0 0 0 0 0 0 0 0 50 4f 4e 4d 4c 4b 4a 49 0 0 0 0 0 0 0 0 58 57 56 55 54 53 52 51 0 0 0 0 0 0 0 0 60 5f 5e 5d 5c 5b 5a 59 0 0 0 0 0 0 0 0 68 67 66 65 64 63 62 61 0 0 0 0 0 0 0 0 70 6f 6e 6d 6c 6b 6a 
3f 37 2f 27 1f 17 f 7 0 0 0 0 0 0 0 0 40 38 30 28 20 18 10 8 0 0 0 0 0 0 0 0 41 39 31 29 21 19 11 9 0 0 0 0 0 0 0 0 42 3a 32 2a 22 1a 12 a 0 0 0 0 0 0 0 0 43 3b 33 2b 23 1b 13 b 0 0 0 0 0 0 0 0 44 3c 34 2c 24 1c 14 c 0 0 0 0 0 0 0 0 45 3d 35 2d 25 1d 15 d 0 0 0 0 0 0 0 0 46 3e 36 2e 26 1e 16 
3f 37 2f 27 1f 17 f 7 0 0 0 0 0 0 0 0 3e 36 2e 26 1e 16 e 6 0 0 0 0 0 0 0 0 3d 35 2d 25 1d 15 d 5 0 0 0 0 0 0 0 0 3c 34 2c 24 1c 14 c 4 0 0 0 0 0 0 0 0 3b 33 2b 23 1b 13 b 3 0 0 0 0 0 0 0 0 3a 32 2a 22 1a 12 a 2 0 0 0 0 0 0 0 0 39 31 29 21 19 11 9 1 0 0 0 0 0 0 0 0 38 30 28 20 18 10 8 
38 37 36 35 34 33 32 31 0 0 0 0 0 0 0 0 30 2f 2e 2d 2c 2b 2a 29 0 0 0 0 0 0 0 0 28 27 26 25 24 23 22 21 0 0 0 0 0 0 0 0 20 1f 1e 1d 1c 1b 1a 19 0 0 0 0 0 0 0 0 18 17 16 15 14 13 12 11 0 0 0 0 0 0 0 0 10 f e d c b a 9 0 0 0 0 0 0 0 0 8 7 6 5 4 3 2 1 0 0 0 0 0 0 0 0 0 ffffffff fffffffe fffffffd fffffffc fffffffb fffffffa 
7 8 9 a b c d e 0 0 0 0 0 0 0 0 ffffffff 0 1 2 3 4 5 6 0 0 0 0 0 0 0 0 fffffff7 fffffff8 fffffff9 fffffffa fffffffb fffffffc fffffffd fffffffe 0 0 0 0 0 0 0 0 ffffffef fffffff0 fffffff1 fffffff2 fffffff3 fffffff4 fffffff5 fffffff6 0 0 0 0 0 0 0 0 ffffffe7 ffffffe8 ffffffe9 ffffffea ffffffeb ffffffec ffffffed ffffffee 0 0 0 0 0 0 0 0 ffffffdf ffffffe0 ffffffe1 ffffffe2 ffffffe3 ffffffe4 ffffffe5 ffffffe6 0 0 0 0 0 0 0 0 ffffffd7 ffffffd8 ffffffd9 ffffffda ffffffdb ffffffdc ffffffdd ffffffde 0 0 0 0 0 0 0 0 ffffffcf ffffffd0 ffffffd1 ffffffd2 ffffffd3 ffffffd4 ffffffd5 
0 8 10 18 20 28 30 38 0 0 0 0 0 0 0 0 ffffffff 7 f 17 1f 27 2f 37 0 0 0 0 0 0 0 0 fffffffe 6 e 16 1e 26 2e 36 0 0 0 0 0 0 0 0 fffffffd 5 d 15 1d 25 2d 35 0 0 0 0 0 0 0 0 fffffffc 4 c 14 1c 24 2c 34 0 0 0 0 0 0 0 0 fffffffb 3 b 13 1b 23 2b 33 0 0 0 0 0 0 0 0 fffffffa 2 a 12 1a 22 2a 32 0 0 0 0 0 0 0 0 fffffff9 1 9 11 19 21 29 

 */
}
