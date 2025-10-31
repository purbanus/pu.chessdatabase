package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.configuraties.StukType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pu.chessdatabase.bo.configuraties.ConfigImpl;
import pu.chessdatabase.bo.configuraties.StukDefinitie;

import lombok.Data;

@Data
public class Stukken
{

private List<Stuk> stukken = new ArrayList<>();
private ConfigImpl configImpl;
public Stukken( ConfigImpl aConfigImpl )
{
	super();
	configImpl = aConfigImpl;
	vulStukTabel();
}
public List<Stuk> getStukken()
{
	return stukken;
}
public List<Stuk> getRealStukken()
{
//	List<Stuk> realStukken = getStukken().stream()
//		.filter( Stuk::getStukType == GEEN )
//		.collect(toList() );
	List<Stuk> realStukken = new ArrayList<>();
	for ( Stuk stuk : getStukken() )
	{
		if ( stuk.getStukType() != GEEN )
		{
			realStukken.add( stuk );
		}
	}
	return realStukken;
}
public int getAantalStukken()
{
	return getRealStukken().size();
}
public void vulStukTabel()
{
	Map<Integer, String> stukIdLookup = new HashMap<>();
	stukIdLookup.put( 0, "wk" );
	stukIdLookup.put( 1, "zk" );
	stukIdLookup.put( 2, "s3" );
	stukIdLookup.put( 3, "s4" );
	stukIdLookup.put( 4, "s5" );
	int index = 0;
	for ( StukDefinitie stukDefinitie : getConfigImpl().getStukDefinities() )
	{
		Stuk stuk = Stuk.builder()
			.id( stukIdLookup.get( index ) )
			.stukNummer( index++ )
			.stukType( stukDefinitie.getStukType() )
			.kleur( stukDefinitie.getKleur() )
			.build();
		getStukken().add( stuk );
	}
}

public Stuk getWk()
{
	return getStukken().get( 0 );
}
public Stuk getZk()
{
	return getStukken().get( 1 );
}
public Stuk getS3()
{
	return getStukken().get( 2 );
}
public Stuk getS4()
{
	return getStukken().get( 3 );
}
public Stuk getS5()
{
	// HIGH Je moet hier altijd 5 stukken hebben. Maar danmoet je onderscheid maken tussen 'echte' stukken en 'geslagen' stukken
	//      Die geslagen stukken moeten dan altijd onder de bijbehorende koning gezet
	return getStukken().get( 4 );
}
public Stuk getStukAtIndex( int aStukNummer)
{
	try
	{
		return getStukken().get( aStukNummer );
	}
	catch ( Throwable e )
	{
		throw new RuntimeException( e );
	}
}
}
