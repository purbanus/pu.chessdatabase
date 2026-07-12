package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class TestKLPK extends ConfigImpl
{
private final String databaseName;
public TestKLPK()
{
	super();
	getStukDefinities().add( new StukDefinitie( Loper,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Paard,  Wit ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	databaseName = "dbs/TestKLPK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "TestKLPK";
}

}
