package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.Lokatie.*;

import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Kleur;

import lombok.Data;

@Data
public class SerialPageDescriptorTable extends AbstractPageDescriptorTable
{
private PageDescriptor[][][] pageDescriptorTable;
SerialPageDescriptorTable( Config aConfig )
{
	super( aConfig );
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
	for ( int wk : getConfig().heeftPionnen() ? STUK_VELD_RANGE : WK_VELD_RANGE )
	{
		for ( int zk : STUK_VELD_RANGE )
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
	setPageDescriptorTable( new PageDescriptor[getConfig().heeftPionnen() ? MAX_STUK : MAX_WK][MAX_STUK][MAX_AANZET] );
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
