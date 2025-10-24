package pu.chessdatabase.bo;

public enum Kleur
{
WIT( "W", "Wit", "Witte" ), 
ZWART( "Z", "Zwart", "Zwarte" );

private String afko;
private String normaleSpelling;
private String label;

Kleur( String aAfko, String aNormaleSpelling, String aLabel )
{
	afko = aAfko;
	normaleSpelling = aNormaleSpelling;
	label = aLabel;
}
public String getAfko()
{
	return afko;
}
public String getNormaleSpelling()
{
	return normaleSpelling;
}
public String getLabel()
{
	return label;
}
}
