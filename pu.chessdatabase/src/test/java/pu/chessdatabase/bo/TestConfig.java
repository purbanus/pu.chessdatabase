package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.configuraties.KDKT;
import pu.chessdatabase.bo.configuraties.KLLK;
import pu.chessdatabase.bo.configuraties.KLPK;
import pu.chessdatabase.bo.configuraties.StukDefinitie;
import pu.chessdatabase.bo.configuraties.TestKDKT;

import lombok.Data;

@SpringBootTest
@Data
public class TestConfig
{
@Autowired private Config config;
String savedConfigName;
@BeforeEach
public void setup()
{
	savedConfigName = getConfig().getConfig();
}
@AfterEach
public void destroy()
{
	getConfig().switchConfig( savedConfigName );
}
@Test
public void testCurrentConfig()
{
	assertThat( getConfig().getConfig(), is( "KLPK" ) );

	getConfig().switchConfig( "kdkt" );
	assertThat( getConfig().getConfig(), is( "KDKT" ) );
}
@Test
public void testSwitchConfig()
{
	assertThrows( RuntimeException.class, () -> config.switchConfig( "PipoKoeie" ) );
	//@@HIGH de rest
}
@Test
public void testGetStukken()
{
	getConfig().switchConfig( "klpk" );
	List<Stuk> expectedStukken = new ArrayList<>();
	expectedStukken.add( Stuk.builder()
		.id( "wk" )
		.stukNummer( 0 )
		.stukType( KONING )
		.kleur( WIT )
		.build()
	);
	expectedStukken.add( Stuk.builder()
		.id( "zk" )
		.stukNummer( 1 )
		.stukType( KONING )
		.kleur( ZWART )
		.build()
	);
	expectedStukken.add( Stuk.builder()
		.id( "s3" )
		.stukNummer( 2 )
		.stukType( LOPER )
		.kleur( WIT )
		.build()
	);
	expectedStukken.add( Stuk.builder()
		.id( "s4" )
		.stukNummer( 3 )
		.stukType( PAARD )
		.kleur( WIT )
		.build()
	);
	assertThat( getConfig().getStukList(), is( expectedStukken ) );
}
@Test
public void testgetStukDefinities()
{
	getConfig().switchConfig( "kdkt" );
	List<StukDefinitie> expectedStukDefinities = new ArrayList<>();
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( KONING )
		.kleur( WIT )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( KONING )
		.kleur( ZWART )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( DAME )
		.kleur( WIT )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( TOREN )
		.kleur( ZWART )
		.build()
	);
	assertThat( getConfig().getStukDefinities(), is( expectedStukDefinities ) );
	
	getConfig().switchConfig( "klpk" );
	expectedStukDefinities = new ArrayList<>();
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( KONING )
		.kleur( WIT )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( KONING )
		.kleur( ZWART )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( LOPER )
		.kleur( WIT )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( PAARD )
		.kleur( WIT )
		.build()
	);
	assertThat( getConfig().getStukDefinities(), is( expectedStukDefinities ) );
}
@SuppressWarnings( "null" )
@Test
public void testGetConfigRegistry()
{
	assertThat( getConfig().getConfigImplRegistry().get( "KDKT" ), is( new KDKT() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLPK" ), is( new KLPK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLLK" ), is( new KLLK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "TESTKDKT" ), is( new TestKDKT() ) );
	getConfig().switchConfig( "KLLK" );
	assertThat( getConfig().getConfigImplRegistry().get( "KDKT" ), is( new KDKT() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLPK" ), is( new KLPK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLLK" ), is( new KLLK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "TESTKDKT" ), is( new TestKDKT() ) );
}
@Test
public void testToString()
{
	System.out.println( getConfig() );
}

}
