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
import pu.chessdatabase.bo.configuraties.KLoK;
import pu.chessdatabase.bo.configuraties.KLPK;
import pu.chessdatabase.bo.configuraties.KoK;
import pu.chessdatabase.bo.configuraties.KTK;
import pu.chessdatabase.bo.configuraties.PipoKDK;
import pu.chessdatabase.bo.configuraties.PipoKDKT;
import pu.chessdatabase.bo.configuraties.PipoKDKTT;
import pu.chessdatabase.bo.configuraties.PipoKLLK;
import pu.chessdatabase.bo.configuraties.PipoKLoK;
import pu.chessdatabase.bo.configuraties.PipoKLPK;
import pu.chessdatabase.bo.configuraties.PipoKoK;
import pu.chessdatabase.bo.configuraties.PipoKTK;
import pu.chessdatabase.bo.configuraties.StukDefinitie;
import pu.chessdatabase.bo.configuraties.TestKDK;
import pu.chessdatabase.bo.configuraties.TestKDKT;
import pu.chessdatabase.bo.configuraties.TestKDKTT;
import pu.chessdatabase.bo.configuraties.TestKLLK;
import pu.chessdatabase.bo.configuraties.TestKLoK;
import pu.chessdatabase.bo.configuraties.TestKLPK;
import pu.chessdatabase.bo.configuraties.TestKoK;
import pu.chessdatabase.bo.configuraties.TestKTK;
import pu.chessdatabase.dbs.CacheType;
import pu.chessdatabase.dbs.PageSizeCalculator;
import pu.chessdatabase.dbs.Transformator;
import pu.chessdatabase.dbs.VM;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@Data
public class Config
{
private static final ConfigImpl DEFAULT_CONFIG_IMPL = new KLPK();
public static final KDK KDK = new KDK();
public static final KoK KoK = new KoK();
public static final KTK KTK = new KTK();
public static final KDKT KDKT = new KDKT();
public static final KLLK KLLK = new KLLK();
public static final KLoK KLoK = new KLoK();
public static final KLPK KLPK = new KLPK();
public static final KDKTT KDKTT = new KDKTT();
public static final TestKDK TestKDK = new TestKDK();
public static final TestKoK TestKoK = new TestKoK();
public static final TestKTK TestKTK = new TestKTK();
public static final TestKDKT TestKDKT = new TestKDKT();
public static final TestKLLK TestKLLK = new TestKLLK();
public static final TestKLoK TestKLoK = new TestKLoK();
public static final TestKLPK TestKLPK = new TestKLPK();
public static final TestKDKTT TestKDKTT = new TestKDKTT();
public static final PipoKDK PipoKDK = new PipoKDK();
public static final PipoKoK PipoKoK = new PipoKoK();
public static final PipoKTK PipoKTK = new PipoKTK();
public static final PipoKDKT PipoKDKT = new PipoKDKT();
public static final PipoKLLK PipoKLLK = new PipoKLLK();
public static final PipoKLoK PipoKLoK = new PipoKLoK();
public static final PipoKLPK PipoKLPK = new PipoKLPK();
public static final PipoKDKTT PipoKDKTT = new PipoKDKTT();
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
@EqualsAndHashCode.Exclude
private PageSizeCalculator pageSizeCalculator;
private CacheType cacheType;
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
public Config( @Lazy VM aVm, @Lazy PageSizeCalculator aPageSizeCalculator )
{
	super();
	vm = aVm;
	pageSizeCalculator = aPageSizeCalculator;
	//Als je setCacheType() doet krijg je een Spring-fout. Is ook niet zo belangrijk want die lookup-tabel zal al wel null zijn
	cacheType = aPageSizeCalculator.getCacheType();
	staticStukList = getStukList();
	staticStukken = getStukken();
}
Map<String, ConfigImpl> getConfigImplRegistry()
{
	if ( configImplRegistry == null )
	{
		configImplRegistry = new HashMap<>();
		configImplRegistry.put( "KDK", KDK );
		configImplRegistry.put( "KoK", KoK );
		configImplRegistry.put( "KTK", KTK );
		configImplRegistry.put( "KDKT", KDKT );
		configImplRegistry.put( "KLoK", KLoK );
		configImplRegistry.put( "KLPK", KLPK );
		configImplRegistry.put( "KLLK", KLLK );
		configImplRegistry.put( "KDKTT", KDKTT );
		configImplRegistry.put( "TestKDK", TestKDK );
		configImplRegistry.put( "TestKoK", TestKoK );
		configImplRegistry.put( "TestKTK", TestKTK );
		configImplRegistry.put( "TestKDKT", TestKDKT );
		configImplRegistry.put( "TestKLLK", TestKLLK );
		configImplRegistry.put( "TestKLoK", TestKLoK );
		configImplRegistry.put( "TestKLPK", TestKLPK );
		configImplRegistry.put( "TestKDKTT", TestKDKTT );
		configImplRegistry.put( "PipoKDK", PipoKDK );
		configImplRegistry.put( "PipoKoK", PipoKoK );
		configImplRegistry.put( "PipoKTK", PipoKTK );
		configImplRegistry.put( "PipoKDKT", PipoKDKT );
		configImplRegistry.put( "PipoKLLK", PipoKLLK );
		configImplRegistry.put( "PipoKLoK", PipoKLoK );
		configImplRegistry.put( "PipoKLPK", PipoKLPK );
		configImplRegistry.put( "PipoKDKTT", PipoKDKTT );
	}
	return configImplRegistry;
}
void switchConfig( ConfigImpl aNewConfig, boolean aSwitchVM )
{
	setConfigImpl( aNewConfig );
	getPageSizeCalculator().setPageSizeLookup( null );
	staticStukList = getStukList();
	staticStukken = getStukken();
	if ( aSwitchVM )
	{
		getVm().switchConfig();
	}
	else
	{
		vm.setDatabaseName( getDatabaseName() );
	}
}
public void switchConfig( String aConfigString, boolean aSwitchVM )
{
	ConfigImpl switchToConfigImpl = getConfigImplRegistry().get( aConfigString );
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
public void setCacheType( CacheType aCacheType )
{
	cacheType = aCacheType;
	getPageSizeCalculator().setCacheType( aCacheType );
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
public boolean heeftPionnen()
{
	return getStukken().heeftPionnen();
}
public int getAantalPionnen()
{
	return getStukken().getAantalPionnen();
}
public Transformator getTransformator()
{
	return getConfigImpl().getTransformator();
}
@Override
public String toString()
{
	return getConfig() + " databaseName=" + getDatabaseName();
}
}
