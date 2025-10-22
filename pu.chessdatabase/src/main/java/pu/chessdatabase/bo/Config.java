package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.StukType.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Config
{
public static final Config DEFAULT_CONFIG = new KDKT();
public static final int MIN_STUKNUMMER = 0;
public static final int MAX_STUKNUMMER = 3;
final List<StukDefinitie> stukDefinities = new ArrayList<>();

public Config()
{
	super();
	// Dit hebben ze allemaal
	stukDefinities.add( new StukDefinitie( KONING, WIT ) );
	stukDefinities.add( new StukDefinitie( KONING, ZWART ) );

}
public List<StukDefinitie> getStukDefinities()
{
	return stukDefinities;
}
public abstract String getDatabaseName();
}
