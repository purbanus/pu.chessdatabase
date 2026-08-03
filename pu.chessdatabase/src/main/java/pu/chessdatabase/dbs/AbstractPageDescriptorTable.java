package pu.chessdatabase.dbs;

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
public PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
public int getAantalStukken()
{
	return getConfig().getAantalStukken();
}
}
