package pu.chessdatabase.bewaard;

import static pu.chessdatabase.bo.Kleur.*;

import pu.chessdatabase.dal.ResultaatType;
import pu.chessdatabase.service.BoStellingKey;
import pu.chessdatabase.service.BoStellingKey3Stukken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * public static final String [] Notatie = new String [] {
	"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "??", "??", "??", "??", "??", "??", "??", "??",
	"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "??", "??", "??", "??", "??", "??", "??", "??",
	"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "??", "??", "??", "??", "??", "??", "??", "??",
	"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "??", "??", "??", "??", "??", "??", "??", "??",
	"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "??", "??", "??", "??", "??", "??", "??", "??",
	"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "??", "??", "??", "??", "??", "??", "??", "??",
	"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "??", "??", "??", "??", "??", "??", "??", "??",
	"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
};

 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoStelling3Stukken extends BoStelling implements Cloneable
{
public static class Builder
{
private BoStelling3Stukken boStelling = new BoStelling3Stukken();
public Builder wk( int aWk ) { boStelling.setWk( aWk ); return this; }
public Builder zk( int aZk ) { boStelling.setZk( aZk ); return this; }
public Builder s3( int aS3 ) { boStelling.setS3( aS3 ); return this; }
public Builder aanZet( Kleur aAanZet ) { boStelling.setAanZet( aAanZet ); return this; }
public Builder resultaat( ResultaatType aResultaat ) { boStelling.setResultaat( aResultaat ); return this; }
public Builder aantalZetten( int aAantalZetten ) { boStelling.setAantalZetten( aAantalZetten ); return this; }
public Builder schaak( boolean aSchaak ) { boStelling.setSchaak( aSchaak ); return this; }
public BoStelling3Stukken build()
{
	return boStelling;
}
}
public static Builder builder()
{
	return new Builder();
}
public static class AlfaBuilder
{
private BoStelling3Stukken boStelling = new BoStelling3Stukken();
public AlfaBuilder wk( String aWk ) { boStelling.setWk( Gen.alfaToVeld( aWk ) ); return this; }
public AlfaBuilder zk( String aZk ) { boStelling.setZk( Gen.alfaToVeld( aZk ) ); return this; }
public AlfaBuilder s3( String aS3 ) { boStelling.setS3( Gen.alfaToVeld( aS3 ) ); return this; }
public AlfaBuilder aanZet( Kleur aAanZet ) { boStelling.setAanZet( aAanZet ); return this; }
public AlfaBuilder resultaat( ResultaatType aResultaat ) { boStelling.setResultaat( aResultaat ); return this; }
public AlfaBuilder aantalZetten( int aAantalZetten ) { boStelling.setAantalZetten( aAantalZetten ); return this; }
public AlfaBuilder schaak( boolean aSchaak ) { boStelling.setSchaak( aSchaak ); return this; }
public BoStelling3Stukken build()
{
	return boStelling;
}
}
public static AlfaBuilder alfaBuilder()
{
	return new AlfaBuilder();
}
public static final BoStelling3Stukken NULL_STELLING = BoStelling3Stukken.builder()
	.wk( 0 )
	.zk( 0 )
	.s3( 0 )
	.aanZet( WIT )
	.resultaat( ResultaatType.ILLEGAAL )
	.aantalZetten( 0 )
	.schaak( false )
	.build();

private int s3;

public String getS3String()
{
	return Config.getStaticStukken().getS3().getStukString();
}

@Override
public String toString()
{
	StringBuilder sb = new StringBuilder();
	sb
	.append( "WK="  ).append( Gen.veldToAlfa( getWk() ) )
	.append( " ZK=" ).append( Gen.veldToAlfa( getZk() ) )
	.append( " S3=" ).append( Gen.veldToAlfa( getS3() ) )
	.append( getToStringDetails() );
	for ( int rij = 7; rij >= 0; rij-- )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			int veld = 16 * rij + kol;
			String veldString;
			if ( veld == getWk() ) veldString = getWkString();
			else if ( veld == getZk() ) veldString = getZkString();
			else if ( veld == getS3() ) veldString = getS3String();
			else veldString = "..";
			sb.append( veldString ).append( " " );
		}
		sb.append( "\n" );
	}
	return sb.toString();
}
@Override
public BoStellingKey3Stukken getBoStellingKey()
{
	return BoStellingKey3Stukken.builder()
		.wk( getWk() )
		.zk( getZk() )
		.s3( getS3() )
		.aanZet( getAanZet() )
		.build();
}

}
