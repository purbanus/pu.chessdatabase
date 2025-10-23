package pu.chessdatabase.bo;

import java.util.ArrayList;
import java.util.List;

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
public void vulStukTabel()
{
	int index = 0;
	for ( StukDefinitie stukDefinitie : getConfigImpl().getStukDefinities() )
	{
		Stuk stuk = Stuk.builder()
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
public Stuk getStukAtIndex( int aStukNummer)
{
	return getStukken().get( aStukNummer );
}
}
