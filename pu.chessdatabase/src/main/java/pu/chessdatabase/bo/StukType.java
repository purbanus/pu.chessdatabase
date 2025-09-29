package pu.chessdatabase.bo;

public enum StukType
{
KONING( "K" ), DAME( "D" ), TOREN( "T" ), LOPER( "L" ), PAARD( "P" ); // geen pion!
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
