package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class TestKTK extends ConfigImpl
{
private final String databaseName;
public TestKTK()
{
	super();
	getStukDefinities().add( new StukDefinitie( Toren,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	databaseName = "dbs/TestKTK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TESTKTK";
}

}
