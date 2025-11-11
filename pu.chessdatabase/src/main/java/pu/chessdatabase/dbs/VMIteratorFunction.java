package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.BoStelling;

@FunctionalInterface
public interface VMIteratorFunction
{
public abstract void doPass( BoStelling aStelling, VMStelling aVmStelling, PassFunction aPassFunction );
}
