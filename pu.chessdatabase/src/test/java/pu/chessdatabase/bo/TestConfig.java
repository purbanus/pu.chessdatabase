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
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.configuraties.ConfigImpl;
import pu.chessdatabase.bo.configuraties.KDK;
import pu.chessdatabase.bo.configuraties.KDKT;
import pu.chessdatabase.bo.configuraties.KDKTT;
import pu.chessdatabase.bo.configuraties.KLLK;
import pu.chessdatabase.bo.configuraties.KLPK;
import pu.chessdatabase.bo.configuraties.KTK;
import pu.chessdatabase.bo.configuraties.StukDefinitie;
import pu.chessdatabase.bo.configuraties.TestKDK;
import pu.chessdatabase.bo.configuraties.TestKDKT;
import pu.chessdatabase.dbs.VM;

import lombok.Data;

@SpringBootTest
@Data
public class TestConfig
{
@Autowired private Config config;
@Autowired private VM vm;
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
public void testGetConfigImplRegistry()
{
	Map<String, ConfigImpl> configImplRegistry = getConfig().getConfigImplRegistry();
	assertThat( configImplRegistry.get( "KDK"), is( new KDK() ) );
	assertThat( configImplRegistry.get( "KTK" ), is( new KTK() ) );
	assertThat( configImplRegistry.get( "KDKT" ), is( new KDKT() ) );
	assertThat( configImplRegistry.get( "KLPK" ), is( new KLPK() ) );
	assertThat( configImplRegistry.get( "KLLK" ), is( new KLLK() ) );
	assertThat( configImplRegistry.get( "KDKTT" ), is( new KDKTT() ) );
	assertThat( configImplRegistry.get( "TESTKDK" ), is( new TestKDK() ) );
	assertThat( configImplRegistry.get( "TESTKDKT" ), is( new TestKDKT() ) );
	getConfig().switchConfig( "KLLK" );
	assertThat( getConfig().getConfigImplRegistry().get( "KDKT" ), is( new KDKT() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLPK" ), is( new KLPK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLLK" ), is( new KLLK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "TESTKDKT" ), is( new TestKDKT() ) );
}
@Test
public void testSwitchConfig()
{
	assertThrows( RuntimeException.class, () -> config.switchConfig( "PipoKoeie" ) );
	getConfig().switchConfig( "KLLK" );
	assertThat( getConfig().getConfig(), is( "KLLK" ) );
	assertThat( getConfig().getConfigImpl(), is( new KLLK() ) );
	assertThat( getVm().getDatabaseName(), is( "dbs/KLLK.DBS" ) );
	assertThat( getVm().isOpen(), is( true ) );
}
@Test
public void testGetStukList()
{
	getConfig().switchConfig( "klpk" );
	List<Stuk> expectedStukken = new ArrayList<>();
	expectedStukken.add( Stuk.builder()
		.id( "wk" )
		.stukNummer( 0 )
		.stukType( Koning )
		.kleur( Wit )
		.build()
	);
	expectedStukken.add( Stuk.builder()
		.id( "zk" )
		.stukNummer( 1 )
		.stukType( Koning )
		.kleur( Zwart )
		.build()
	);
	expectedStukken.add( Stuk.builder()
		.id( "s3" )
		.stukNummer( 2 )
		.stukType( Loper )
		.kleur( Wit )
		.build()
	);
	expectedStukken.add( Stuk.builder()
		.id( "s4" )
		.stukNummer( 3 )
		.stukType( Paard )
		.kleur( Wit )
		.build()
	);
	expectedStukken.add( Stuk.builder()
		.id( "s5" )
		.stukNummer( 4 )
		.stukType( Geen )
		.kleur( Wit )
		.build()
	);
	assertThat( getConfig().getStukList(), is( expectedStukken ) );
}
@Test
public void testGetStukken()
{
	getConfig().switchConfig( "klpk" );
}
@Test
public void testgetStukDefinities()
{
	getConfig().switchConfig( "kdkt" );
	List<StukDefinitie> expectedStukDefinities = new ArrayList<>();
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Koning )
		.kleur( Wit )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Koning )
		.kleur( Zwart )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Dame )
		.kleur( Wit )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Toren )
		.kleur( Zwart )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Geen )
		.kleur( Wit )
		.build()
	);
	assertThat( getConfig().getStukDefinities(), is( expectedStukDefinities ) );
	
	getConfig().switchConfig( "klpk" );
	expectedStukDefinities = new ArrayList<>();
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Koning )
		.kleur( Wit )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Koning )
		.kleur( Zwart )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Loper )
		.kleur( Wit )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Paard )
		.kleur( Wit )
		.build()
	);
	expectedStukDefinities.add( StukDefinitie.builder()
		.stukType( Geen )
		.kleur( Wit )
		.build()
	);
	assertThat( getConfig().getStukDefinities(), is( expectedStukDefinities ) );
}
@Test
public void testgetDatabaseName()
{
	getConfig().switchConfig( "kdk" );
	assertThat( getConfig().getDatabaseName(), is( "dbs/KDK.DBS" ) );
	getConfig().switchConfig( "kdkt" );
	assertThat( getConfig().getDatabaseName(), is( "dbs/KDKT.DBS" ) );
	getConfig().switchConfig( "kdktt", false );
	assertThat( getConfig().getDatabaseName(), is( "dbs/KDKTT.DBS" ) );
}
@Test
public void testgetAantalStukken()
{
	getConfig().switchConfig( "kdk" );
	assertThat( getConfig().getAantalStukken(), is( 3 ) );
	getConfig().switchConfig( "kdkt" );
	assertThat( getConfig().getAantalStukken(), is( 4 ) );
	getConfig().switchConfig( "kdktt", false );
	assertThat( getConfig().getAantalStukken(), is( 5 ) );

}
@Test
public void testGetConfig()
{
	getConfig().switchConfig( "kdk" );
	assertThat( getConfig().getConfig(), is( "KDK" ) );
	getConfig().switchConfig( "kdkt" );
	assertThat( getConfig().getConfig(), is( "KDKT" ) );
	getConfig().switchConfig( "kdktt" );
	assertThat( getConfig().getConfig(), is( "KDKTT" ) );
}


//@Test
public void testToString()
{
	System.out.println( getConfig() );
}

}
