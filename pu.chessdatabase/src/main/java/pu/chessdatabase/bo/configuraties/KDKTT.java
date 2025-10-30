package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class KDKTT extends ConfigImpl
{
private final String databaseName;
public KDKTT()
{
	super();
	getStukDefinities().add( new StukDefinitie( DAME,   WIT ) );
	getStukDefinities().add( new StukDefinitie( TOREN,   ZWART ) );
	getStukDefinities().add( new StukDefinitie( TOREN,   ZWART ) );
	databaseName = "dbs/KDKTT.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "KDKTT";
}

}
