package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.CacheType.*;

public interface PageDescriptorTable
{
public abstract void initializePageDescriptorTable();
public abstract void iterateOverAllPageDescriptors( PageDescriptorFunction aPageDescriptorsFunction );
public abstract PageDescriptor getPageDescriptor( VMStelling aStelling );
public abstract void setPageDescriptor( VMStelling aVmStelling, PageDescriptor aPageDescriptor );

public static PageDescriptorTable create( PageSizeCalculator aPageSizeCalculator, int aAantalStukken )
{
	switch ( aPageSizeCalculator.getCacheType() )
	{
		case Serial  : return new SerialPageDescriptorTable  ( aPageSizeCalculator, aAantalStukken );
		case Parallel: return new ParallelPageDescriptorTable( aPageSizeCalculator, aAantalStukken );
		default: throw new RuntimeException( "Ongeldig CacheType: " + aPageSizeCalculator.getCacheType() );
	}
}
}
