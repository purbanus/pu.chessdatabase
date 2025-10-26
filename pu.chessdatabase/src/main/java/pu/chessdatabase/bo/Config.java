package pu.chessdatabase.bo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
		configImplRegistry.put( "KDKT", new KDKT() );
		configImplRegistry.put( "KLPK", new KLPK() );
		configImplRegistry.put( "KLLK", new KLLK() );
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
//public int getAantalStukken()
//{
//	return getStukList().size();
//}
//public List<Integer> getAvailableAantalStukken()
//{
//	return Arrays.asList( new Integer [] { 4 } );
//}
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
	// @@HIGH Ik zie haakjes in de toString()
	return getConfig() + " databaseName=" + getDatabaseName();
}
}
