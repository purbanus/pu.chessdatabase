package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class KDK extends ConfigImpl
{
private final String databaseName;
public KDK()
{
	super();
	getStukDefinities().add( new StukDefinitie( DAME,   WIT ) );
	databaseName = "dbs/KDK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "KDK";
}

}
