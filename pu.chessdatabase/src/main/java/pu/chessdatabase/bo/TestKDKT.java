package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.StukType.*;

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
	getStukDefinities().add( new StukDefinitie( DAME,   WIT ) );
	getStukDefinities().add( new StukDefinitie( TOREN,  ZWART ) );
	databaseName = "dbs/Pipo";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TESTKDKT";
}

}
