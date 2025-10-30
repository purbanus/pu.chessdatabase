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
private static List<Stuk> stukList = DEFAULT_CONFIG_IMPL.getStukken().getStukken();
private static Stukken stukken = DEFAULT_CONFIG_IMPL.getStukken();
public static List<Stuk> getStaticStukList()
{
	return stukList;
}
public static Stukken getStaticStukken()
{
	return stukken;
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
	stukList = getStukList();
	stukken = getStukken();

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
	// @@HIGH Het is gevaarlijk om het uit stukList te halen. Het kan best zzijn dat we daar altijd 5 stukken in zetten!
	return getStukList().size();
}
public String getConfig()
{
	return getConfigImpl().getName();
}
public List<String> getAvailableConfigs()
{
	return Arrays.asList( new String [] { "KDKT", "KLPK", "KLLK" } );
}
@Override
public String toString()
{
	return getConfig() + " databaseName=" + getDatabaseName();
}
}
