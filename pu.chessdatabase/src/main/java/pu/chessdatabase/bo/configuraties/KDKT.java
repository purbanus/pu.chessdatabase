package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.bo.Stukken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode( callSuper=false )
public class KDKT extends ConfigImpl
{
private final String databaseName;
public KDKT()
{
	super();
	// HIGH Je moet hier altijd 5 stukken hebben. Maar danmoet je onderscheid maken tussen 'echte' stukken en 'geslagen' stukken
	//      Die geslagen stukken moeten dan altijd onder de bijbehorende koning gezet
	getStukDefinities().add( new StukDefinitie( DAME,   WIT ) );
	getStukDefinities().add( new StukDefinitie( TOREN,   ZWART ) );
	getStukDefinities().add( new StukDefinitie( GEEN,   WIT ) );
	databaseName = "dbs/KDKT.DBS";
	setStukken( new Stukken( this ) );
}
@Override
public String getName()
{
	return "KDKT";
}

}
