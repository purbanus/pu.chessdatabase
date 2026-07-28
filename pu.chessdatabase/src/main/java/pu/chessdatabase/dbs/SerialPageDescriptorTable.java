package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Lokatie.*;

import pu.chessdatabase.bo.Kleur;

import lombok.Data;

@Data
public class SerialPageDescriptorTable extends AbstractPageDescriptorTable
{
private PageDescriptor[][][] pageDescriptorTable = new PageDescriptor[VM.MAX_WK][VM.MAX_STUK][VM.MAX_AANZET];
SerialPageDescriptorTable(  PageSizeCalculator aPageSizeCalculator, int aAantalStukken )
{
	super( aPageSizeCalculator, aAantalStukken );
	initializePageDescriptorTable();
}
@Override
public PageDescriptor getPageDescriptor( VMStelling aStelling )
{
	return getPageDescriptorTable()[aStelling.getWk()][aStelling.getZk()][aStelling.getAanZet().ordinal()];
}
@Override
public void setPageDescriptor( VMStelling aVmStelling, PageDescriptor aPageDescriptor )
{
	getPageDescriptorTable()[aVmStelling.getWk()][aVmStelling.getZk()][aVmStelling.getAanZet().ordinal()] = aPageDescriptor; 
}
@Override
public void iterateOverAllPageDescriptors( PageDescriptorFunction aPageDescriptorsFunction )
{
	for ( int wk = 0; wk < VM.MAX_WK; wk++ )
	{
		for ( int zk = 0; zk < VM.MAX_STUK; zk++ )
		{
			for ( Kleur aanZet : Kleur.values() )
			{
            	VMStelling vmStelling = VMStelling.builder()
            		.wk( wk )
            		.zk( zk )
            		.aanZet( aanZet )
            		.build();
 				aPageDescriptorsFunction.doPass( vmStelling );
			}
		}
	}
}
long address;
@Override
public void initializePageDescriptorTable()
{
	address = 0L;
	setPageDescriptorTable( new PageDescriptor[VM.MAX_WK][VM.MAX_STUK][VM.MAX_AANZET] );
	iterateOverAllPageDescriptors( this::initializePageDescriptor );
}
void initializePageDescriptor( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( OpSchijf )
		.schijfAdres( address )
		.cacheNummer( Integer.MAX_VALUE )
		.build();
	setPageDescriptor( aVmStelling, pageDescriptor );
	address += getPageSizeCalculator().getPageSize( getAantalStukken() );
}

}
