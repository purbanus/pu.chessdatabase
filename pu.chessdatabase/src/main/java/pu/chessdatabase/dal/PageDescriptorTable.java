package pu.chessdatabase.dal;

import pu.chessdatabase.bo.Kleur;

import lombok.Data;

@Data
public class PageDescriptorTable
{
public static final int MAX_WK = 10;
public static final int MAX_STUK = 64;
public static final int MAX_AANZET = 2;
private PageDescriptor[][][] pageDescriptorTable = new PageDescriptor[MAX_WK][MAX_STUK][MAX_AANZET];

public PageDescriptorTable()
{
	super();
	initializePageDescriptorTable();
}
PageDescriptor getPageDescriptor( VMStelling aStelling )
{
	return getPageDescriptorTable()[aStelling.getWk()][aStelling.getZk()][aStelling.getAanZet().ordinal()];
}
void setPageDescriptor( VMStelling aVmStelling, PageDescriptor aPageDescriptor )
{
	getPageDescriptorTable()[aVmStelling.getWk()][aVmStelling.getZk()][aVmStelling.getAanZet().ordinal()] = aPageDescriptor; 
}
void iterateOverAllPageDescriptors( VMStellingFunction aPageDescriptorsFunction )
{
	for ( int wk = 0; wk < MAX_WK; wk++ )
	{
		for ( int zk = 0; zk < MAX_STUK; zk++ )
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
long address; // @@NOG Dit is een multithread probleem(pje)
void initializePageDescriptorTable()
{
	address = 0L;
	iterateOverAllPageDescriptors( this::initializePageDescriptor );
}
void initializePageDescriptor( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = PageDescriptor.builder()
		.waar( Lokatie.OP_SCHIJF )
		.schijfAdres( address )
		.cacheNummer( Integer.MAX_VALUE )
		.build();
	setPageDescriptor( aVmStelling, pageDescriptor );
	address += Cache.PAGE_SIZE;
}

}
