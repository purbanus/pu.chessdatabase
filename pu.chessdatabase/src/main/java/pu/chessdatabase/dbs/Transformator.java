package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.BoStelling;

public interface Transformator
{
public abstract VMStelling boStellingToVmStelling( BoStelling aBoStlling );
public abstract int vmStellingWkToBoStellingWk( int aVmStellingWk );
public abstract int vmStellingWkToLinear( int aVmStellingWk );
public abstract int vmStellingWkFromLinear( int aVmStellingWk );
public abstract int vmStellingStukToBoStellingStuk( int aVmStellingStuk );
public abstract BoStelling vmStellingToBoStelling( VMStelling aVmStelling );
}
