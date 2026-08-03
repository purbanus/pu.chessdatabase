package pu.chessdatabase.dbs;

import pu.chessdatabase.bo.Config;

// @@ Gek hoor, dat wordt duidelij hierbenede gebruikt!
//import static pu.chessdatabase.dbs.CacheType.*;

public interface PageDescriptorTable
{
public abstract void initializePageDescriptorTable();
public abstract void iterateOverAllPageDescriptors( PageDescriptorFunction aPageDescriptorsFunction );
public abstract PageDescriptor getPageDescriptor( VMStelling aStelling );
public abstract void setPageDescriptor( VMStelling aVmStelling, PageDescriptor aPageDescriptor );

public static PageDescriptorTable create( Config aConfig )
{
	switch ( aConfig.getPageSizeCalculator().getCacheType() )
	{
		case Serial  : return new SerialPageDescriptorTable  ( aConfig );
		case Parallel: return new ParallelPageDescriptorTable( aConfig);
		default: throw new RuntimeException( "Ongeldig CacheType: " + aConfig.getPageSizeCalculator().getCacheType() );
	}
}
}
