package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.Lokatie.*;

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
@Override
public PageDescriptor getPageDescriptor( VMStelling aStelling )
{
	return getPageDescriptorTable()[aStelling.getWk()];
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
	for ( int wk : getConfig().heeftPionnen() ? STUK_VELD_RANGE : WK_VELD_RANGE )
	{
    	VMStelling vmStelling = VMStelling.builder()
    		.wk( wk )
    		.build();
		aPageDescriptorsFunction.doPass( vmStelling );
	}
}
long address = 0L;
int index = 0;
@Override 
public void initializePageDescriptorTable()
{
	setPageDescriptorTable( new PageDescriptor[getConfig().heeftPionnen() ? MAX_STUK : MAX_WK] );
	iterateOverAllPageDescriptors( this::initializePageDescriptor );
}
void initializePageDescriptor( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.schijfAdres( address )
		.cacheNummer( index )
		.build();
	setPageDescriptor( aVmStelling, pageDescriptor );
	address += getPageSizeCalculator().getPageSize( getAantalStukken() );
	index++;
}

}
