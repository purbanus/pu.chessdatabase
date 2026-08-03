package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.Richtingen.*;
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
import pu.chessdatabase.bo.configuraties.KOK;
import pu.chessdatabase.bo.configuraties.KTK;
import pu.chessdatabase.bo.configuraties.PipoKDK;
import pu.chessdatabase.bo.configuraties.PipoKDKT;
import pu.chessdatabase.bo.configuraties.PipoKDKTT;
import pu.chessdatabase.bo.configuraties.PipoKLLK;
import pu.chessdatabase.bo.configuraties.PipoKLPK;
import pu.chessdatabase.bo.configuraties.PipoKOK;
import pu.chessdatabase.bo.configuraties.PipoKTK;
import pu.chessdatabase.bo.configuraties.StukDefinitie;
import pu.chessdatabase.bo.configuraties.TestKDK;
import pu.chessdatabase.bo.configuraties.TestKDKT;
import pu.chessdatabase.bo.configuraties.TestKDKTT;
import pu.chessdatabase.bo.configuraties.TestKLLK;
import pu.chessdatabase.bo.configuraties.TestKLPK;
import pu.chessdatabase.bo.configuraties.TestKOK;
import pu.chessdatabase.bo.configuraties.TestKTK;
import pu.chessdatabase.dbs.VM;

import lombok.Data;

@SpringBootTest
@Data
public class TestConfig
{
@Autowired private Config config;
@Autowired private VM vm;
String savedConfigString;
@BeforeEach
public void setup()
{
	getConfig().switchConfig( Config.KLPK );
	savedConfigString = getConfig().getConfig();
}
@AfterEach
public void destroy()
{
	getConfig().switchConfig( savedConfigString );
}
@Test
public void testCurrentConfig()
{
	assertThat( getConfig().getConfig(), is( "KLPK" ) );

	getConfig().switchConfig( Config.KDKT );
	assertThat( getConfig().getConfig(), is( "KDKT" ) );
}
@Test
public void testGetConfigImplRegistry()
{
	Map<String, ConfigImpl> configImplRegistry = getConfig().getConfigImplRegistry();
	assertThat( configImplRegistry.get( "KDK"), is( new KDK() ) );
	assertThat( configImplRegistry.get( "KOK"), is( new KOK() ) );
	assertThat( configImplRegistry.get( "KTK" ), is( new KTK() ) );
	assertThat( configImplRegistry.get( "KDKT" ), is( new KDKT() ) );
	assertThat( configImplRegistry.get( "KLPK" ), is( new KLPK() ) );
	assertThat( configImplRegistry.get( "KLLK" ), is( new KLLK() ) );
	assertThat( configImplRegistry.get( "KDKTT" ), is( new KDKTT() ) );
	assertThat( configImplRegistry.get( "TESTKDK" ), is( new TestKDK() ) );
	assertThat( configImplRegistry.get( "TESTKOK" ), is( new TestKOK() ) );
	assertThat( configImplRegistry.get( "TESTKTK" ), is( new TestKTK() ) );
	assertThat( configImplRegistry.get( "TESTKDKT" ), is( new TestKDKT() ) );
	assertThat( configImplRegistry.get( "TESTKLLK" ), is( new TestKLLK() ) );
	assertThat( configImplRegistry.get( "TESTKLPK" ), is( new TestKLPK() ) );
	assertThat( configImplRegistry.get( "TESTKDKTT" ), is( new TestKDKTT() ) );
	assertThat( configImplRegistry.get( "PIPOKDK"), is( new PipoKDK() ) );
	assertThat( configImplRegistry.get( "PIPOKOK"), is( new PipoKOK() ) );
	assertThat( configImplRegistry.get( "PIPOKTK" ), is( new PipoKTK() ) );
	assertThat( configImplRegistry.get( "PIPOKDKT" ), is( new PipoKDKT() ) );
	assertThat( configImplRegistry.get( "PIPOKLPK" ), is( new PipoKLPK() ) );
	assertThat( configImplRegistry.get( "PIPOKLLK" ), is( new PipoKLLK() ) );
	assertThat( configImplRegistry.get( "PIPOKDKTT" ), is( new PipoKDKTT() ) );

	getConfig().switchConfig( Config.KLLK );
	assertThat( getConfig().getConfigImplRegistry().get( "KDKT" ), is( new KDKT() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLLK" ), is( new KLLK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "KLPK" ), is( new KLPK() ) );
	assertThat( getConfig().getConfigImplRegistry().get( "TESTKDKT" ), is( new TestKDKT() ) );
}
@Test
public void testSwitchConfig()
{
	assertThrows( RuntimeException.class, () -> config.switchConfig( "PipoKoeie" ) );
	getConfig().switchConfig( Config.KLLK );
	assertThat( getConfig().getConfig(), is( "KLLK" ) );
	assertThat( getConfig().getConfigImpl(), is( new KLLK() ) );
	assertThat( getVm().getDatabaseName(), is( "dbs/KLLK.DBS" ) );
	assertThat( getVm().isOpen(), is( true ) );
}
@Test
public void testGetStukList()
{
	getConfig().switchConfig( Config.KLPK );
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
	getConfig().switchConfig( Config.KLPK );
	List<Stuk> stukken = getConfig().getStukken().getStukken();
	Stuk stuk = stukken.get( 0 );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getId(), is( "wk" ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( KRICHTING ) );
	assertThat( stuk.getStukNummer(), is( 0 ) );
	assertThat( stuk.getStukType(), is( Koning ) );

	stuk = stukken.get( 1 );
	assertThat( stuk.getAfko(), is( "K" ) );
	assertThat( stuk.getId(), is( "zk" ) );
	assertThat( stuk.getKleur(), is( Zwart ) );
	assertThat( stuk.getKoningsNummer(), is( 1 ) );
	assertThat( stuk.getRichtingen(), is( KRICHTING ) );
	assertThat( stuk.getStukNummer(), is( 1 ) );
	assertThat( stuk.getStukType(), is( Koning ) );

	stuk = stukken.get( 2 );
	assertThat( stuk.getAfko(), is( "L" ) );
	assertThat( stuk.getId(), is( "s3" ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( LRICHTING ) );
	assertThat( stuk.getStukNummer(), is( 2 ) );
	assertThat( stuk.getStukType(), is( Loper ) );

	stuk = stukken.get( 3 );
	assertThat( stuk.getAfko(), is( "P" ) );
	assertThat( stuk.getId(), is( "s4" ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( PRICHTING ) );
	assertThat( stuk.getStukNummer(), is( 3 ) );
	assertThat( stuk.getStukType(), is( Paard ) );

	stuk = stukken.get( 4 );
	assertThat( stuk.getAfko(), is( "G" ) );
	assertThat( stuk.getId(), is( "s5" ) );
	assertThat( stuk.getKleur(), is( Wit ) );
	assertThat( stuk.getKoningsNummer(), is( 0 ) );
	assertThat( stuk.getRichtingen(), is( GRICHTING ) );
	assertThat( stuk.getStukNummer(), is( 4 ) );
	assertThat( stuk.getStukType(), is( Geen ) );

}
@Test
public void testgetStukDefinities()
{
	getConfig().switchConfig( Config.KDKT );
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
	
	getConfig().switchConfig( Config.KLPK );
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
	getConfig().switchConfig( Config.KDK );
	assertThat( getConfig().getDatabaseName(), is( "dbs/KDK.DBS" ) );
	getConfig().switchConfig( Config.KDKT );
	assertThat( getConfig().getDatabaseName(), is( "dbs/KDKT.DBS" ) );
	getConfig().switchConfig( Config.KDKTT );
	assertThat( getConfig().getDatabaseName(), is( "dbs/KDKTT.DBS" ) );
}
@Test
public void testgetAantalStukken()
{
	getConfig().switchConfig( Config.KDK );
	assertThat( getConfig().getAantalStukken(), is( 3 ) );
	getConfig().switchConfig( Config.KDKT );
	assertThat( getConfig().getAantalStukken(), is( 4 ) );
	getConfig().switchConfig( Config.KDKTT );
	assertThat( getConfig().getAantalStukken(), is( 5 ) );

}
@Test
public void testGetConfig()
{

	getConfig().switchConfig( Config.KDK );
	assertThat( getConfig().getConfig(), is( "KDK" ) );
	getConfig().switchConfig( Config.KDKT );
	assertThat( getConfig().getConfig(), is( "KDKT" ) );
	getConfig().switchConfig( Config.KDKTT );
	assertThat( getConfig().getConfig(), is( "KDKTT" ) );
}


//@Test
public void testToString()
{
	System.out.println( getConfig() );
}

}
