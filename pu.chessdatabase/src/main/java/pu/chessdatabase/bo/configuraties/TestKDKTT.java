package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class TestKDKTT extends ConfigImpl
{
private final String databaseName;
public TestKDKTT()
{
	super();
	getStukDefinities().add( new StukDefinitie( DAME,   WIT ) );
	getStukDefinities().add( new StukDefinitie( TOREN,   ZWART ) );
	getStukDefinities().add( new StukDefinitie( TOREN,   ZWART ) );
	databaseName = "dbs/Pipo5";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TESTKDKTT";
}

}
