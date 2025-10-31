package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Bord.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.Data;

@SpringBootTest
@Data
public class TestBord
{
@Autowired private Config config;
Bord bord;

@BeforeEach
public void setup()
{
	bord = new Bord( getConfig().getAantalStukken(), getConfig().getStukken() );
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
	BoStelling stelling = BoStelling.alfaBuilder()
		.wk( "f1" )
		.zk( "g1" )
		.s3( "h1" )
		.s4( "a2" )
		.s5( "b2" )
		.build();
	bord.zetBordOp( stelling );
	assertThat( bord.getAlfaVeld( "f1" ), is( 0 ) );
	assertThat( bord.getAlfaVeld( "g1" ), is( 1 ) );
	assertThat( bord.getAlfaVeld( "h1" ), is( 2 ) );
	if ( getConfig().getAantalStukken() >= 4 )
	{
		assertThat( bord.getAlfaVeld( "a2" ), is( 3 ) );
	}
	if ( getConfig().getAantalStukken() >= 5 )
	{
		assertThat( bord.getAlfaVeld( "b2" ), is( 4 ) );
	}
	for ( int x = 0; x < 5; x++ )
	{
		assertThat( bord.isVeldLeeg( x ), is( true ) );
	}
	for ( int x = 0x12; x < 0x77; x++ )
	{
		assertThat( bord.isVeldLeeg( x ), is( true ) );
	}
}
@Test
public void testClrBord()
{
	BoStelling stelling = BoStelling.alfaBuilder()
		.wk( "f1" )
		.zk( "g1" )
		.s3( "h1" )
		.s4( "a2" )
		.s5( "b2" )
		.build();
	bord.zetBordOp( stelling );
	for ( int x = 0; x <= 4; x++ )
	{
		assertThat( bord.getVeld( x ), is( LEEG ) );
	}
	assertThat( bord.getAlfaVeld( "f1" ), is( 0 ) );
	assertThat( bord.getAlfaVeld( "g1" ), is( 1 ) );
	assertThat( bord.getAlfaVeld( "h1" ), is( 2 ) );
	if ( getConfig().getAantalStukken() >= 4 )
	{
		assertThat( bord.getAlfaVeld( "a2" ), is( 3 ) );
	}
	if ( getConfig().getAantalStukken() >= 5 )
	{
		assertThat( bord.getAlfaVeld( "b2" ), is( 4 ) );
	}
	for ( int x = 0x12; x <= MAX_BORD; x++ )
	{
		assertThat( bord.getVeld( x ), is( LEEG ) );
	}
	bord.clearBord( stelling );
	for ( int x = 0; x <= MAX_BORD; x++ )
	{
		assertThat( bord.getVeld( x ), is( LEEG ) );
	}
}

}
