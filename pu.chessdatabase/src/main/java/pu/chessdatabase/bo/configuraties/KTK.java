package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class KTK extends ConfigImpl
{
private final String databaseName;
public KTK()
{
	super();
	getStukDefinities().add( new StukDefinitie( TOREN,   WIT ) );
	getStukDefinities().add( new StukDefinitie( GEEN,   WIT ) );
	getStukDefinities().add( new StukDefinitie( GEEN,   WIT ) );
	databaseName = "dbs/KTK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "KTK";
}

}
