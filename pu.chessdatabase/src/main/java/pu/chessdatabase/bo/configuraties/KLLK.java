package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class KLLK extends ConfigImpl
{
private final String databaseName;
public KLLK()
{
	super();
	getStukDefinities().add( new StukDefinitie( LOPER,   WIT ) );
	getStukDefinities().add( new StukDefinitie( LOPER,  WIT ) );
	databaseName = "dbs/KLLK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "KLLK";
}

}
