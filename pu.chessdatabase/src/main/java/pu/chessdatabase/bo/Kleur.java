package pu.chessdatabase.bo;

public enum Kleur
{
Wit( "W" ), Zwart( "Z" );
private String afko;
Kleur( String aAfko )
{
	afko = aAfko;
}
public String getAfko()
{
	return afko;
}
}
