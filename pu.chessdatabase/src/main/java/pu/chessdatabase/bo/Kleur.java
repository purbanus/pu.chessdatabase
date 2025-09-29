package pu.chessdatabase.bo;

public enum Kleur
{
WIT( "W" ), ZWART( "Z" );
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
