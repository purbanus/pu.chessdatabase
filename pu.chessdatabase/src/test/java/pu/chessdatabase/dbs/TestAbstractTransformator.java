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
import pu.chessdatabase.bo.Kleur;
import pu.services.Vector;

import lombok.Data;

@SpringBootTest
@Data
public class TestAbstractTransformator
{
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
public void testVmStellingToBoStelling()
{
	VMStelling vmStelling = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "b1" )
		.s3( "c1" )
		.s4( "d1" )
		.s5( "e1" )
		.aanZet( Wit )
		.build();
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "b1" )
		.s3( "c1" )
		.s4( "d1" )
		.s5( "e1" )
		.aanZet( Wit )
		.build();
	BoStelling gotBoStelling = getTransformator().vmStellingToBoStelling( vmStelling );
	assertThat( gotBoStelling, is( boStelling ) );

	vmStelling = VMStelling.alfaBuilder()
		.wk( "d1" )
		.zk( "e1" )
		.s3( "f1" )
		.s4( "g1" )
		.s5( "h1" )
		.aanZet( Wit )
		.build();
	boStelling = BoStelling.alfaBuilder()
		.wk( "d1" )
		.zk( "e1" )
		.s3( "f1" )
		.s4( "g1" )
		.s5( "h1" )
		.aanZet( Wit )
		.build();
	gotBoStelling = getTransformator().vmStellingToBoStelling( vmStelling );
	assertThat( gotBoStelling, is( boStelling ) );
}
//@Test // @@HIGH Deze test doet er een kleine 30 seconden over. Waarom eigenlijk?
public void testVmStellingToBoStellingGrondig()
{
	for ( int wk : WK_MET_PIONNEN_ITERATOR )
	{
		for ( int zk : STUK_VELD_RANGE )
		{
			for ( Kleur kleur : Kleur.values() )
			{
				for ( int s3 : STUK_VELD_RANGE )
				{
					for ( int s4 : STUK_VELD_RANGE )
					{
						for ( int s5 : STUK_VELD_RANGE )
						{
							VMStelling vmStelling = VMStelling.builder()
								.wk( wk )
								.zk( zk )
								.s3( s3 )
								.s4( s4 )
								.s5( s5 )
								.aanZet( kleur )
								.build();
							BoStelling boStelling = BoStelling.builder()
								.wk( getTransformator().vmStellingWkToBoStellingWk( wk ) )
								.zk( getTransformator().vmStellingStukToBoStellingStuk( zk ) )
								.s3( getTransformator().vmStellingStukToBoStellingStuk( s3 ) )
								.s4( getTransformator().vmStellingStukToBoStellingStuk( s4 ) )
								.s5( getTransformator().vmStellingStukToBoStellingStuk( s5 ) )
								.aanZet( kleur )
								.build();
							BoStelling gotBoStelling = getTransformator().vmStellingToBoStelling( vmStelling );
							// @@LOW Dit is niet zo'n goede test want vmStellingToBoStelling doet getzelfde als bij onze boStelling
							assertThat( gotBoStelling, is( boStelling ) );
						}
					}
				}
			}
		}
	}
}
}
