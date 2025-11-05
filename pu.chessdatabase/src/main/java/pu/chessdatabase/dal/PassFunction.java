package pu.chessdatabase.dal;

import pu.chessdatabase.bo.BoStelling;

@FunctionalInterface
public interface PassFunction
{
public abstract void doPass( BoStelling aBoStelling);
}
