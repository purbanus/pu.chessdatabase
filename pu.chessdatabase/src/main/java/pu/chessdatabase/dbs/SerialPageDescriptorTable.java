package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Constants.*;

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
public PageDescriptor getLinearPageDescriptor( VMStelling aVmStelling )
{
	return getPageDescriptorTable()[aVmStelling.getWk()][aVmStelling.getZk()][aVmStelling.getAanZet().ordinal()];
}
@Override
public PageDescriptor getNonLinearPageDescriptor( VMStelling aVmStelling )
{
	return getLinearPageDescriptor( aVmStelling );
}
@Override
public void setPageDescriptor( VMStelling aVmStelling, PageDescriptor aPageDescriptor )
{
	getPageDescriptorTable()[aVmStelling.getWk()][aVmStelling.getZk()][aVmStelling.getAanZet().ordinal()] = aPageDescriptor; 
}
@Override
public void iterateOverAllPageDescriptors( PageDescriptorFunction aPageDescriptorsFunction )
{
	for ( int wk : getConfig().heeftPionnen() ? WK_VELD_RANGE_MET_PIONNEN : WK_VELD_RANGE_ZONDER_PIONNEN )
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
@Override
public void createPageDescriptorTable()
{
	setPageDescriptorTable( new PageDescriptor[getConfig().heeftPionnen() ? MAX_WK_MET_PIONNEN : MAX_WK_ZONDER_PIONNEN][MAX_STUK][MAX_AANZET] );
}
}
