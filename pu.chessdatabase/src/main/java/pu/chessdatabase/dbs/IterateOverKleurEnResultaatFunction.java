package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.Kleur;

@FunctionalInterface
public interface IterateOverKleurEnResultaatFunction
{
public abstract void doPass( Kleur aKleur, ResultaatType aResultaat );
}
