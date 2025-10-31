package pu.chessdatabase.bo.configuraties;

import java.util.List;

import pu.chessdatabase.bo.Richtingen;

public enum StukType
{

KONING( "K", "Koning", Richtingen.KRICHTING, false ), 
DAME( "D", "Dame", Richtingen.KRICHTING, true ), 
TOREN( "T", "Toren", Richtingen.TRICHTING, true ), 
LOPER( "L", "Loper", Richtingen.LRICHTING, true ), 
PAARD( "P",  "Paard", Richtingen.PRICHTING, false ),
GEEN( "G",  "Geen stuk", Richtingen.GRICHTING, false );
// geen pion!

private String afko;
private String label;
private List<Integer> richtingen;
private boolean meer;

StukType( String aAfko, String aLabel, List<Integer> aRichtingen, boolean aMeer )
{
	//Richtingen richtingenVoorStukTypes = new Richtingen();
	afko = aAfko;
	label = aLabel;
	richtingen = aRichtingen;
	meer = aMeer;
}
public String getAfko()
{
	return afko;
}
public String getLabel()
{
	return label;
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
