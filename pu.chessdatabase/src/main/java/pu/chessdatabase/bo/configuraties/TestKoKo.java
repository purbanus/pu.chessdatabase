package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class TestKoKo extends ConfigImpl
{
private final String databaseName;
public TestKoKo()
{
	super();
	getStukDefinities().add( new StukDefinitie( Pion,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Pion,   Zwart ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	databaseName = "dbs/TestKoKo.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TestKoKo";
}

}
