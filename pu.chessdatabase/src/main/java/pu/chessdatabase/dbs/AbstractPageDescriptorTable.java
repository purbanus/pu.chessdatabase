package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.CacheType.*;
import static pu.chessdatabase.dbs.Lokatie.*;

import pu.chessdatabase.bo.Config;

import lombok.Data;

@Data
public abstract class AbstractPageDescriptorTable implements PageDescriptorTable
{
private final Config config;

AbstractPageDescriptorTable( Config aConfig )
{
	super();
	config = aConfig;
}
public CacheType getCacheType()
{
	return getConfig().getCacheType();
}
public PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
public int getAantalStukken()
{
	return getConfig().getAantalStukken();
}
public Transformator getTransformator()
{
	return getConfig().getTransformator();
}

long address;
int index;
abstract void createPageDescriptorTable();
@Override 
public void initializePageDescriptorTable()
{
	address = 0L;
	index = 0;
	createPageDescriptorTable();
	iterateOverAllPageDescriptors( this::initializePageDescriptor );
}
void initializePageDescriptor( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.schijfAdres( address )
		.cacheNummer( getCacheType() == Serial ? Integer.MAX_VALUE : index )
		.build();
	setPageDescriptor( aVmStelling, pageDescriptor );
	address += getPageSizeCalculator().getPageSize( getAantalStukken() );
	index++;
}

}
