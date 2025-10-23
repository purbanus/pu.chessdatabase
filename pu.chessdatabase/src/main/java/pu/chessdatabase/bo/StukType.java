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
