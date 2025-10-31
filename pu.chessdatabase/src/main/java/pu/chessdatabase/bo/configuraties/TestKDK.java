package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class TestKDK extends ConfigImpl
{
private final String databaseName;
public TestKDK()
{
	super();
	getStukDefinities().add( new StukDefinitie( DAME,   WIT ) );
	getStukDefinities().add( new StukDefinitie( GEEN,   WIT ) );
	getStukDefinities().add( new StukDefinitie( GEEN,   WIT ) );
	databaseName = "dbs/Pipo3";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TESTKDK";
}

}
