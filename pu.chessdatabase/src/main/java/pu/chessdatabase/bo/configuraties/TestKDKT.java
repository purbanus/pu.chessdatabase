package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class TestKDKT extends ConfigImpl
{
private final String databaseName;
public TestKDKT()
{
	super();
	getStukDefinities().add( new StukDefinitie( Dame,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Toren,  Zwart ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	databaseName = "dbs/Pipo4";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TESTKDKT";
}

}
