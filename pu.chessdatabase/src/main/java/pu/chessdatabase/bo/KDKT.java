package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.StukType.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class KDKT extends Config
{
private final String databaseName;
public KDKT()
{
	super();
	stukDefinities.add( new StukDefinitie( DAME,   WIT ) );
	stukDefinities.add( new StukDefinitie( TOREN,  ZWART ) );
	databaseName = "KDKT.DBS";
}

}
