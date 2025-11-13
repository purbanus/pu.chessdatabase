package pu.chessdatabase.bo.configuraties;

import java.util.List;

import pu.chessdatabase.bo.Richtingen;

public enum StukType
{

Koning( "K", "Koning", Richtingen.KRICHTING, false ), 
Dame( "D", "Dame", Richtingen.KRICHTING, true ), 
Toren( "T", "Toren", Richtingen.TRICHTING, true ), 
Loper( "L", "Loper", Richtingen.LRICHTING, true ), 
Paard( "P",  "Paard", Richtingen.PRICHTING, false ),
Geen( "G",  "Geen stuk", Richtingen.GRICHTING, false );
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
