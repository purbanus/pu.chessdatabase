package pu.chessdatabase.bo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
import pu.chessdatabase.dal.VM;

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
@Setter( AccessLevel.NONE ) 
@ToString.Exclude
private VM vm;

@Setter( AccessLevel.NONE ) 
private Map<String, ConfigImpl> configImplRegistry = null;

@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PACKAGE ) 
private ConfigImpl configImpl = DEFAULT_CONFIG_IMPL;

public Config( @Lazy VM aVm)
{
	super();
	vm = aVm;
	staticStukList = getStukList();
	staticStukken = getStukken();
}
VM getVm()
{
	return vm;
}
Map<String, ConfigImpl> getConfigImplRegistry()
{
	if ( configImplRegistry == null )
	{
		configImplRegistry = new HashMap<>();
		configImplRegistry.put( "KDK", new KDK() );
		configImplRegistry.put( "KTK", new KTK() );
		configImplRegistry.put( "KDKT", new KDKT() );
		configImplRegistry.put( "KLPK", new KLPK() );
		configImplRegistry.put( "KLLK", new KLLK() );
		configImplRegistry.put( "KDKTT", new KDKTT() );
		configImplRegistry.put( "TESTKDK", new TestKDK() );
		configImplRegistry.put( "TESTKDKT", new TestKDKT() );
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
public List<Stuk> getStukList()
{
	return getConfigImpl().getStukken().getStukken();
}
public Stukken getStukken()
{
	return getConfigImpl().getStukken();
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
	return Arrays.asList( new String [] { "KDK", "KTK", "KDKT", "KLPK", "KLLK" } );
}
@Override
public String toString()
{
	return getConfig() + " databaseName=" + getDatabaseName();
}
}
