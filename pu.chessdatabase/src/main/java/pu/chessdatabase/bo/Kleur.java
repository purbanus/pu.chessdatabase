package pu.chessdatabase.bo;

public enum Kleur
{
Wit( "W", "Witte" ), 
Zwart( "Z", "Zwarte" );

private String afko;
private String label;

Kleur( String aAfko, String aLabel )
{
	afko = aAfko;
	label = aLabel;
}
public String getAfko()
{
	return afko;
}
public String getLabel()
{
	return label;
}
}
