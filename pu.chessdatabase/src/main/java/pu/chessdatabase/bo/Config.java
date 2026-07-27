package pu.chessdatabase.bo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.configuraties.ConfigImpl;
import pu.chessdatabase.bo.configuraties.KDK;
import pu.chessdatabase.bo.configuraties.KDKT;
import pu.chessdatabase.bo.configuraties.KDKTT;
import pu.chessdatabase.bo.configuraties.KLLK;
import pu.chessdatabase.bo.configuraties.KLPK;
import pu.chessdatabase.bo.configuraties.KTK;
import pu.chessdatabase.bo.configuraties.PipoKDK;
import pu.chessdatabase.bo.configuraties.PipoKDKT;
import pu.chessdatabase.bo.configuraties.PipoKDKTT;
import pu.chessdatabase.bo.configuraties.PipoKLLK;
import pu.chessdatabase.bo.configuraties.PipoKLPK;
import pu.chessdatabase.bo.configuraties.PipoKTK;
import pu.chessdatabase.bo.configuraties.StukDefinitie;
import pu.chessdatabase.bo.configuraties.TestKDK;
import pu.chessdatabase.bo.configuraties.TestKDKT;
import pu.chessdatabase.bo.configuraties.TestKDKTT;
import pu.chessdatabase.bo.configuraties.TestKLLK;
import pu.chessdatabase.bo.configuraties.TestKLPK;
import pu.chessdatabase.bo.configuraties.TestKTK;
import pu.chessdatabase.dbs.VM;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@Data
public class Config
{
private static final ConfigImpl DEFAULT_CONFIG_IMPL = new KLPK();
public static final KDK KDK = new KDK();
public static final KTK KTK = new KTK();
public static final KDKT KDKT = new KDKT();
public static final KLLK KLLK = new KLLK();
public static final KLPK KLPK = new KLPK();
public static final KDKTT KDKTT = new KDKTT();
public static final TestKDK TESTKDK = new TestKDK();
public static final TestKTK TESTKTK = new TestKTK();
public static final TestKDKT TESTKDKT = new TestKDKT();
public static final TestKLLK TESTKLLK = new TestKLLK();
public static final TestKLPK TESTKLPK = new TestKLPK();
public static final TestKDKTT TESTKDKTT = new TestKDKTT();
public static final PipoKDK PIPOKDK = new PipoKDK();
public static final PipoKTK PIPOKTK = new PipoKTK();
public static final PipoKDKT PIPOKDKT = new PipoKDKT();
public static final PipoKLLK PIPOKLLK = new PipoKLLK();
public static final PipoKLPK PIPOKLPK = new PipoKLPK();
public static final PipoKDKTT PIPOKDKTT = new PipoKDKTT();
private static List<Stuk> staticStukList = DEFAULT_CONFIG_IMPL.getStukken().getStukken();
private static Stukken staticStukken = DEFAULT_CONFIG_IMPL.getStukken();
public static List<Stuk> getStaticStukList()
{
	return staticStukList;
}
public static Stukken getStaticStukken()
{
	return staticStukken;
}
@ToString.Exclude
private VM vm;

@Setter( AccessLevel.NONE ) 
private Map<String, ConfigImpl> configImplRegistry = null;

@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PACKAGE ) 
private ConfigImpl configImpl = DEFAULT_CONFIG_IMPL;

// Als deze ctor bestaat dan moet je die ZAndere ctor annoteren met @Autowired!!
public Config()
{
	super();
	// Vegeet nniet om setVm te doen hierna
}
public void setVm( VM aVm )
{
	vm = aVm;
}
@Autowired
public Config( @Lazy VM aVm)
{
	super();
	vm = aVm;
	staticStukList = getStukList();
	staticStukken = getStukken();
}
Map<String, ConfigImpl> getConfigImplRegistry()
{
	if ( configImplRegistry == null )
	{
		configImplRegistry = new HashMap<>();
		configImplRegistry.put( "KDK", KDK );
		configImplRegistry.put( "KTK", KTK );
		configImplRegistry.put( "KDKT", KDKT );
		configImplRegistry.put( "KLPK", KLPK );
		configImplRegistry.put( "KLLK", KLLK );
		configImplRegistry.put( "KDKTT", KDKTT );
		configImplRegistry.put( "TESTKDK", TESTKDK );
		configImplRegistry.put( "TESTKTK", TESTKTK );
		configImplRegistry.put( "TESTKDKT", TESTKDKT );
		configImplRegistry.put( "TESTKLLK", TESTKLLK );
		configImplRegistry.put( "TESTKLPK", TESTKLPK );
		configImplRegistry.put( "TESTKDKTT", TESTKDKTT );
		configImplRegistry.put( "PIPOKDK", PIPOKDK );
		configImplRegistry.put( "PIPOKDKT", PIPOKDKT );
		configImplRegistry.put( "PiPOKLLK", PIPOKLLK );
		configImplRegistry.put( "PiPOKLPK", PIPOKLPK );
		configImplRegistry.put( "PIPOKDKTT", PIPOKDKTT );
	}
	return configImplRegistry;
}
void switchConfig( ConfigImpl aNewConfig, boolean aSwitchVM )
{
	setConfigImpl( aNewConfig );
	if ( aSwitchVM )
	{
		getVm().switchConfig();
	}
	else
	{
		vm.setDatabaseName( getDatabaseName() );
	}
	staticStukList = getStukList();
	staticStukken = getStukken();
}
public void switchConfig( String aConfigString, boolean aSwitchVM )
{
	ConfigImpl switchToConfigImpl = getConfigImplRegistry().get( aConfigString.toUpperCase() );
	if ( switchToConfigImpl == null )
	{
		throw new RuntimeException( "Ongeldige configString: " + aConfigString );
	}
	switchConfig( switchToConfigImpl, aSwitchVM );
}
public void switchConfig( String aConfigString )
{
	 switchConfig( aConfigString, true );
}
public void switchConfig( ConfigImpl aConfigImpl )
{
	 switchConfig( aConfigImpl, true );
}
public List<Stuk> getStukList()
{
	return getConfigImpl().getStukken().getStukken();
}
public Stukken getStukken()
{
	return getConfigImpl().getStukken();
}
public List<Stuk> getSortedStukList()
{
	return getStukken().getSortedStukken();
}
public List<Stuk> getRealStukken()
{
	return getStukken().getRealStukken();
}
public List<Stuk> getFakeStukken()
{
	return getStukken().getFakeStukken();
}
List<StukDefinitie> getStukDefinities()
{
	return getConfigImpl().getStukDefinities();
}
public String getDatabaseName()
{
	return getConfigImpl().getDatabaseName();
}
public int getAantalStukken()
{
	return getStukken().getAantalStukken();
}
public String getConfig()
{
	return getConfigImpl().getName();
}
public List<String> getAvailableConfigs()
{
	return Arrays.asList( new String [] { "KDK", "KTK", "KDKT", "KLPK", "KLLK", "KDKTT" } );
}
@Override
public String toString()
{
	return getConfig() + " databaseName=" + getDatabaseName();
}
}
