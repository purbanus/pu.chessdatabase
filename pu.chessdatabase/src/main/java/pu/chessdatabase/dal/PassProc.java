package pu.chessdatabase.dal;

import pu.chessdatabase.bo.BoStelling;

@FunctionalInterface
public interface PassProc
{
public abstract void doPass( BoStelling aStelling);
}
