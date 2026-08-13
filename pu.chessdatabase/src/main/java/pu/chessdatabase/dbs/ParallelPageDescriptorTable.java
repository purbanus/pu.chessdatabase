package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Constants.*;

import pu.chessdatabase.bo.Config;

import lombok.Data;

@Data
public class ParallelPageDescriptorTable extends AbstractPageDescriptorTable
{
private PageDescriptor[] pageDescriptorTable;
ParallelPageDescriptorTable( Config aConfig )
{
	super( aConfig );
	initializePageDescriptorTable();
}
/**
 * Retourneert de pagedescriptor die bij de VMStelling hoort. In het bijzonder is de WK-positie bepalend:
 * de pageDescriptorTable is een lineaire List van PageDescriptors met dimensies 0-31, dus de WK moet
 * onderdeel zijn van een lineaire reeks van 0-31, en dus NIET een reeks volgens de WkMetPionnenIterator.
 */
PageDescriptor getPageDescriptor( int aWk )
{
	try
	{
		return getPageDescriptorTable()[aWk];
	}
	catch ( Exception e )
	{
		e.printStackTrace();
		throw e;
	}
}
@Override
public PageDescriptor getLinearPageDescriptor( VMStelling aVmStelling )
{
	try
	{
		return getPageDescriptor( aVmStelling.getWk() );
	}
	catch ( Exception e )
	{
		e.printStackTrace();
		throw e;
	}
}
@Override
public PageDescriptor getNonLinearPageDescriptor( VMStelling aVmStelling )
{
	return getPageDescriptor( getTransformator().vmStellingWkToLinear( aVmStelling.getWk() ) );
}
@Override
public void setPageDescriptor( VMStelling aVmStelling, PageDescriptor aPageDescriptor )
{
	getPageDescriptorTable()[aVmStelling.getWk()] = aPageDescriptor;
	if ( aPageDescriptor.getCacheNummer() == Integer.MAX_VALUE )
	{
		aPageDescriptor.setCacheNummer( aVmStelling.getWk() );
	}
}
@Override
public void iterateOverAllPageDescriptors( PageDescriptorFunction aPageDescriptorsFunction )
{
	for ( int wk : getConfig().heeftPionnen() ? WK_VELD_RANGE_MET_PIONNEN : WK_VELD_RANGE_ZONDER_PIONNEN )
	{
    	VMStelling vmStelling = VMStelling.builder()
    		.wk( wk )
    		.build();
		aPageDescriptorsFunction.doPass( vmStelling );
	}
}
@Override
void createPageDescriptorTable()
{
	setPageDescriptorTable( new PageDescriptor[getConfig().heeftPionnen() ? MAX_WK_MET_PIONNEN : MAX_WK_ZONDER_PIONNEN] );
}

}
