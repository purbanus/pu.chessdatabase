package pu.chessdatabase.bo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Stukken
{

private List<Stuk> stukken = new ArrayList<>();
private Config config;
public Stukken()
{
	this( Config.DEFAULT_CONFIG );
}
public Stukken( Config aConfig )
{
	super();
	config = aConfig;
	vulStukTabel();
}
public List<Stuk> getStukken()
{
	return stukken;
}
public void vulStukTabel()
{
	int index = 0;
	for ( StukDefinitie stukDefinitie : config.getStukDefinities() )
	{
		Stuk stuk = Stuk.builder()
			.stukNummer( index++ )
			.stukType( stukDefinitie.getStukType() )
			.kleur( stukDefinitie.getKleur() )
			.build();
		stukken.add( stuk );
	}
}

public Stuk getWk()
{
	return stukken.get( 0 );
}
public Stuk getZk()
{
	return stukken.get( 1 );
}
public Stuk getS3()
{
	return stukken.get( 2 );
}
public Stuk getS4()
{
	return stukken.get( 3 );
}
public Stuk getStukAtIndex( int aStukNummer)
{
	return stukken.get( aStukNummer );
}
}
