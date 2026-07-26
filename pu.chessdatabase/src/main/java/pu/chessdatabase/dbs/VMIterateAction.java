package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.Kleur.*;

import java.util.concurrent.RecursiveAction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VMIterateAction extends RecursiveAction
{
private final VMStellingIterator vmStellingIterator;
private final PassFunction passFunction;
private final int wk;
public VMIterateAction( VMStellingIterator aVmStellingIterator, PassFunction aPassFunction, int aWk )
{
	super();
	vmStellingIterator = aVmStellingIterator;
	passFunction = aPassFunction;
	wk = aWk;
}

@Override
protected void compute()
{
	vmStellingIterator.iterateOverZkOneColor( wk, Wit, passFunction );
	vmStellingIterator.iterateOverZkOneColor( wk, Zwart, passFunction );
    LOG.info( String.format( "wk=%d was processed by %s", wk, Thread.currentThread().getName() ) );
}

}
