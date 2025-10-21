package pu.chessdatabase.bo;

public enum Kleur
{
WIT( "W", "Wit" ), ZWART( "Z", "Zwart" );

private String afko;
private String normaleSpelling;
Kleur( String aAfko, String aNormaleSpelling )
{
	afko = aAfko;
	normaleSpelling = aNormaleSpelling;
}
public String getAfko()
{
	return afko;
}
public String getNormaleSpelling()
{
	return normaleSpelling;
}
}
