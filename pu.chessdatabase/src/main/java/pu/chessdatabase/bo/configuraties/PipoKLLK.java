package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class PipoKLLK extends ConfigImpl
{
private final String databaseName;
public PipoKLLK()
{
	super();
	getStukDefinities().add( new StukDefinitie( Loper,   Wit ) );
	getStukDefinities().add( new StukDefinitie( Loper,  Wit ) );
	getStukDefinities().add( new StukDefinitie( Geen,   Wit ) );
	databaseName = "dbs/PipoKLLK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "PipoKLLK";
}

}
