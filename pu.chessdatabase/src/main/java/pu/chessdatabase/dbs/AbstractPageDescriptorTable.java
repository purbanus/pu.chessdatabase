package pu.chessdatabase.dbs;

import lombok.Data;

@Data
public abstract class AbstractPageDescriptorTable implements PageDescriptorTable
{
private final int aantalStukken;
private final PageSizeCalculator pageSizeCalculator;

AbstractPageDescriptorTable( PageSizeCalculator aPageSizeCalculator, int aAantalStukken )
{
	super();
	aantalStukken = aAantalStukken;
	pageSizeCalculator = aPageSizeCalculator;
}

}
