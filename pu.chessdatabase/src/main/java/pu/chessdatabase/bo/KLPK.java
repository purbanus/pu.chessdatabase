package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.StukType.*;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class KLPK extends ConfigImpl
{
private final String databaseName;
public KLPK()
{
	super();
	getStukDefinities().add( new StukDefinitie( LOPER,   WIT ) );
	getStukDefinities().add( new StukDefinitie( PAARD,  WIT ) );
	databaseName = "dbs/KLPK.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "KLPK";
}

}
