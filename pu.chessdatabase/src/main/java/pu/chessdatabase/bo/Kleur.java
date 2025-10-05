package pu.chessdatabase.bo;

public enum Kleur
{
WIT( "W", "Wit" ), ZWART( "Z", "Zwart" );
public static Kleur fromString( String aKleurString )
{
	String kleurString = aKleurString.toLowerCase();
	switch ( kleurString )
	{
		case "wit": return WIT;
		case "zwart": return ZWART;
		default: throw new RuntimeException( "Ongeldige kleur: " + aKleurString );
	}
}
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
