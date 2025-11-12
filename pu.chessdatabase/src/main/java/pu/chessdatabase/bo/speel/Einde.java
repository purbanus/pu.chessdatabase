package pu.chessdatabase.bo.speel;

public enum EindeType
{
NOG_NIET( "Nog niet"), MAT( "Mat" ), PAT( "Pat" ), ILLEGAAL( "Illegaal" );
private String normaleSpelling;
EindeType( String aNormaleSpelling )
{
	normaleSpelling = aNormaleSpelling;
}
public String getNormaleSpelling()
{
	return normaleSpelling;
}
}
