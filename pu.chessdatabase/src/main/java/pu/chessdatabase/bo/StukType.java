package pu.chessdatabase.bo;

import java.util.List;

public enum StukType
{

KONING( "K", Richtingen.KRICHTING, false ), 
DAME( "D", Richtingen.KRICHTING, true ), 
TOREN( "T", Richtingen.TRICHTING, true ), 
LOPER( "L", Richtingen.LRICHTING, true ), 
PAARD( "P", Richtingen.PRICHTING, false );
// geen pion!

//public static final List<Integer> KRICHTING = Arrays.asList( 0x01, 0x11,  0x10,  0x0F, -0x01, -0x11, -0x10, -0x0F );
//public static final List<Integer> TRICHTING = Arrays.asList( 0x01, 0x10, -0x01, -0x10 );
//public static final List<Integer> LRICHTING = Arrays.asList( 0x11, 0x0F, -0x11, -0x0F );
//public static final List<Integer> PRICHTING = Arrays.asList( 0x12, 0x21,  0x1F,  0x0E, -0x12, -0x21, -0x1F, -0x0E );

private String afko;
private List<Integer> richtingen;
private boolean meer;
StukType( String aAfko, List<Integer> aRichtingen, boolean aMeer )
{
	//Richtingen richtingenVoorStukTypes = new Richtingen();
	afko = aAfko;
	richtingen = aRichtingen;
	meer = aMeer;
}
public String getAfko()
{
	return afko;
}
public List<Integer> getRichtingen()
{
	return richtingen;
}
public boolean isMeer()
{
	return meer;
}
}
