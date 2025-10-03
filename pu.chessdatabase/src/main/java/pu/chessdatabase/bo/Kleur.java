package pu.chessdatabase.bo;

public enum Kleur
{
WIT( "W" ), ZWART( "Z" );
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
Kleur( String aAfko )
{
	afko = aAfko;
}
public String getAfko()
{
	return afko;
}
}
