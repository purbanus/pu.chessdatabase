package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.BoStelling;

@FunctionalInterface
public interface PassFunction
{
public abstract void doPass( BoStelling aBoStelling);
}
