package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Lokatie.*;

import java.util.ArrayList;

import pu.chessdatabase.bo.Kleur;

import lombok.Data;

@Data
public class ParallelPageDescriptorTable extends AbstractPageDescriptorTable
{
private PageDescriptor[] pageDescriptorTable = new PageDescriptor[VM.MAX_WK];
ParallelPageDescriptorTable( PageSizeCalculator aPageSizeCalculator, int aAantalStukken )
{
	super( aPageSizeCalculator, aAantalStukken );
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
}
@Override
public void iterateOverAllPageDescriptors( PageDescriptorFunction aPageDescriptorsFunction )
{
	for ( int wk = 0; wk < VM.MAX_WK; wk++ )
	{
    	VMStelling vmStelling = VMStelling.builder()
    		.wk( wk )
    		.build();
		aPageDescriptorsFunction.doPass( vmStelling );
	}
}
long address = 0L; // @@NOG Dit is een multithread probleem(pje)
int index = 0;;
@Override 
public void initializePageDescriptorTable()
{
	setPageDescriptorTable( new PageDescriptor[VM.MAX_WK] );
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
