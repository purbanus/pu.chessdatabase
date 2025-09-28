package pu.chessdatabase.bo;

public enum StukType
{
Koning( "K" ), Dame( "D" ), Toren( "T" ), Loper( "L" ), Paard( "P" ); // geen pion!
private String afko;
StukType( String aAfko )
{
	afko = aAfko;
}
public String getAfko()
{
	return afko;
}
}
