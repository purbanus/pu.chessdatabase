package pu.chessdatabase.bo.configuraties;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import java.util.ArrayList;
import java.util.List;

import pu.chessdatabase.bo.Stukken;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

// @@LOW Ik zie haakjes in de toString(). Waar komen die vandaan?
@Data
public abstract class ConfigImpl
{
public static final int MIN_STUKNUMMER = 0;
public static final int MAX_STUKNUMMER = 3;

private final List<StukDefinitie> stukDefinities = new ArrayList<>();

@Setter( AccessLevel.PROTECTED )
private Stukken stukken;
public ConfigImpl()
{
	super();
	// Dit hebben ze allemaal
	stukDefinities.add( new StukDefinitie( KONING, WIT ) );
	stukDefinities.add( new StukDefinitie( KONING, ZWART ) );
}
public abstract String getDatabaseName();
public abstract String getName();
}
