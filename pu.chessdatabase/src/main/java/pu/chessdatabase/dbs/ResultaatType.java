package pu.chessdatabase.dbs;

public enum ResultaatType
{
ILLEGAAL( "Illegaal" ), GEWONNEN( "Gewonnen" ), REMISE( "Remise"), VERLOREN( "Verloren" );
private String normaleSpelling;
ResultaatType( String aNormaleSpelling )
{
	normaleSpelling = aNormaleSpelling;
}
public String getNormaleSpelling()
{
	return normaleSpelling;
}

}
