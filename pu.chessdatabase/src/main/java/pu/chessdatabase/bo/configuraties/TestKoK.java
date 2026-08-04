package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class TestKOK extends ConfigImpl
{
private final String databaseName;
public TestKOK()
{
	super();
	getStukDefinities().add( new StukDefinitie( Pion,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	databaseName = "dbs/TestKOK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TESTKOK";
}

}
