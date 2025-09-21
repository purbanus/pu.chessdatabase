package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestGen
{
@Autowired private Gen gen = new Gen();

@Test
public void testMaakBordLeeg()
{
	gen.MaakBordLeeg();
	for ( int x = 0; x < 0x77; x++ )
	{
		assertThat( gen.Bord[x], is( gen.Leeg ) );
	}
}
@Test
public void testVulStukTabel()
{
	gen.VulStukTabel();
	Stuk stuk = gen.StukTabel[1];
	assertThat( stuk.getSoort(), is( StukType.Koning ) );
	assertThat( stuk.isKleur(), is( AlgDef.Wit ) );
	assertThat( stuk.getKnummer(), is( 1 ) );
	assertThat( stuk.getRichting(), is( gen.Krichting ) );
	assertThat( stuk.getAtlRicht(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getStukAfk(), is( 'K' ) );
	
	stuk = gen.StukTabel[2];
	assertThat( stuk.getSoort(), is( StukType.Koning ) );
	assertThat( stuk.isKleur(), is( AlgDef.Zwart ) );
	assertThat( stuk.getKnummer(), is( 2 ) );
	assertThat( stuk.getRichting(), is( gen.Krichting ) );
	assertThat( stuk.getAtlRicht(), is( 8 ) );
	assertThat( stuk.isMeer(), is( false ) );
	assertThat( stuk.getStukAfk(), is( 'K' ) );

	stuk = gen.StukTabel[3];
	assertThat( stuk.getSoort(), is( StukType.Dame ) );
	assertThat( stuk.isKleur(), is( AlgDef.Wit ) );
	assertThat( stuk.getKnummer(), is( 1 ) );
	assertThat( stuk.getRichting(), is( gen.Krichting ) );
	assertThat( stuk.getAtlRicht(), is( 8 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getStukAfk(), is( 'D' ) );
	
	stuk = gen.StukTabel[4];
	assertThat( stuk.getSoort(), is( StukType.Toren ) );
	assertThat( stuk.isKleur(), is( AlgDef.Zwart ) );
	assertThat( stuk.getKnummer(), is( 2 ) );
	assertThat( stuk.getRichting(), is( gen.Trichting ) );
	assertThat( stuk.getAtlRicht(), is( 4 ) );
	assertThat( stuk.isMeer(), is( true ) );
	assertThat( stuk.getStukAfk(), is( 'T' ) );

}
@Test
public void testZetBordOp()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.Bord[5], is( 1 ) );
	assertThat( gen.Bord[6], is( 2 ) );
	assertThat( gen.Bord[7], is( 3 ) );
	assertThat( gen.Bord[8], is( 4 ) );
	for ( int x = 0; x < 4; x++ )
	{
		assertThat( gen.Bord[x], is( gen.Leeg ) );
	}
	for ( int x = 9; x < 0x77; x++ )
	{
		assertThat( gen.Bord[x], is( gen.Leeg ) );
	}
}
@Test
public void testClrBord()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	gen.ZetBordOp( stelling );
	gen.ClrBord( stelling );
	for ( int x = 0; x < 0x77; x++ )
	{
		assertThat( gen.Bord[x], is( gen.Leeg ) );
	}
}
@Test
public void testIsGeomIllegaal()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 5 )
		.s3( 7 )
		.s4( 8 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 7 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 5 )
		.s4( 7 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 5 )
		.build();
	assertThat( gen.IsGeomIllegaal( stelling ), is( true ) );
}
@Test
public void testIsKKSchaak()
{
	//Zie Gen.java voor een (paar) coordinaten, na Notatie
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( false ) );
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x00 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x10 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( true ) );
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x21 )
		.build();
	assertThat( gen.isKKSchaak( stelling ), is( true ) );
}

@Test
public void testIsSchaakDoorStuk()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x33 ), is( false ) );
	gen.ClrBord( stelling );

	// T links
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x10 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x10 ), is( true ) );
	gen.ClrBord( stelling );

	// T uiterst rechts
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x17 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x17 ), is( true ) );
	gen.ClrBord( stelling );

	// T nog steeds uiterst rechts, maar D ertussen
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x15 )
		.s4( 0x17 )
		.aanZet( AlgDef.Zwart )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[4], 0x11, 0x17 ), is( false ) );
	gen.ClrBord( stelling );

	// Check of Z schaak staat
	stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x20 )
		.s4( 0x77 )
		.aanZet( AlgDef.Wit )
		.build();
	gen.ZetBordOp( stelling );
	assertThat( gen.isSchaakDoorStuk( gen.StukTabel[3], 0x27, 0x20 ), is( true ) );
	gen.ClrBord( stelling );
	
}
@Test
public void testAddZet()
{
	// @@@NOG
}
@Test
public void testGenZetPerStuk()
{
	// @@@NOG
}
@Test
public void testGenZet()
{
	// @@@NOG
}
@Test
public void testGenZetSort()
{
	// @@@NOG
}
@Test
public void testIsPat()
{
	// @@@NOG
}
@Test
public void testVeldToAscii()
{
	assertThat( gen.VeldToAscii( 0x00 ), is( "a1" ) );
	assertThat( gen.VeldToAscii( 0x07 ), is( "h1" ) );
	assertThat( gen.VeldToAscii( 0x08 ), is( "??" ) );
	assertThat( gen.VeldToAscii( 0x70 ), is( "a8" ) );
	assertThat( gen.VeldToAscii( 0x77 ), is( "h8" ) );
}
@Test
public void testAsciiToVeld()
{
	assertThat( gen.AsciiToVeld( "a1" ), is( 0x00 ) );
	assertThat( gen.AsciiToVeld( "h1" ), is( 0x07 ) );
	assertThat( gen.AsciiToVeld( "a8" ), is( 0x70 ) );
	assertThat( gen.AsciiToVeld( "h8" ), is( 0x77 ) );
	// @@NOG hoe werken die exceptions nou?
	//assertThrows( RuntimeException.class, () -> { gen.AsciiToVeld( "a9" ); } );
}
@Test
public void testGetStukInfo()
{
	BoStelling stelling = BoStelling.builder()
		.wk( 0x11 )
		.zk( 0x27 )
		.s3( 0x76 )
		.s4( 0x33 )
		.aanZet( AlgDef.Zwart )
		.build();
	assertThat( gen.GetStukInfo( stelling, 1 ).getVeld(), is( 0x11 ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).getX(), is( 2 ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).getY(), is( 2 ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).isKleur(), is( AlgDef.Wit ) );
	assertThat( gen.GetStukInfo( stelling, 1 ).getStukAfk(), is( 'K' ) );

	assertThat( gen.GetStukInfo( stelling, 2 ).getVeld(), is( 0x27 ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).getX(), is( 8 ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).getY(), is( 3 ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).isKleur(), is( AlgDef.Zwart ) );
	assertThat( gen.GetStukInfo( stelling, 2 ).getStukAfk(), is( 'K' ) );

	assertThat( gen.GetStukInfo( stelling, 3 ).getVeld(), is( 0x76 ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).getX(), is( 7 ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).getY(), is( 8 ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).isKleur(), is( AlgDef.Wit ) );
	assertThat( gen.GetStukInfo( stelling, 3 ).getStukAfk(), is( 'D' ) );

	assertThat( gen.GetStukInfo( stelling, 4 ).getVeld(), is( 0x33 ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).getX(), is( 4 ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).getY(), is( 4 ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).isKleur(), is( AlgDef.Zwart ) );
	assertThat( gen.GetStukInfo( stelling, 4 ).getStukAfk(), is( 'T' ) );


}

}
