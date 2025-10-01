package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBord
{
Bord bord;

@BeforeEach
public void setup()
{
	bord = new Bord();
}
@Test
public void testMaakBordLeeg()
{
	bord.maakBordLeeg();
	for ( int x = 0; x < 0x77; x++ )
	{
		assertThat( bord.isVeldLeeg( x ), is( true ) );
	}
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
	bord.zetBordOp( stelling );
	assertThat( bord.getVeld( 5 ), is( 1 ) );
	assertThat( bord.getVeld( 6 ), is( 2 ) );
	assertThat( bord.getVeld( 7 ), is( 3 ) );
	assertThat( bord.getVeld( 8 ), is( 4 ) );
	for ( int x = 0; x < 5; x++ )
	{
		assertThat( bord.isVeldLeeg( x ), is( true ) );
	}
	for ( int x = 9; x < 0x77; x++ )
	{
		assertThat( bord.isVeldLeeg( x ), is( true ) );
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
	bord.zetBordOp( stelling );
	System.out.println( bord );
	bord.clearBord( stelling );
	System.out.println();
	System.out.println( bord );
	for ( int x = 0; x < 0x78; x++ )
	{
		if ( ! bord.isVeldLeeg( x ) )
		{
			System.out.println( "Bord is niet helemaal leeg, veld=" + x + " Bord[x]=" + bord.bord[x] );
		}
		assertThat( bord.isVeldLeeg( x ), is( true ) );
	}
}

}
