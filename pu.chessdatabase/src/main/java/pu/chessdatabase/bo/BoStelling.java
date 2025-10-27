package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;

import pu.chessdatabase.dal.ResultaatType;
import pu.chessdatabase.service.BoStellingKey;

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
@EqualsAndHashCode
public class BoStelling implements Cloneable
{
public static class AlfaBuilder
{
private BoStelling boStelling = new BoStelling();
public AlfaBuilder wk( String aWk ) { boStelling.setWk( Gen.alfaToVeld( aWk ) ); return this; }
public AlfaBuilder zk( String aZk ) { boStelling.setZk( Gen.alfaToVeld( aZk ) ); return this; }
public AlfaBuilder s3( String aS3 ) { boStelling.setS3( Gen.alfaToVeld( aS3 ) ); return this; }
public AlfaBuilder s4( String aS4 ) { boStelling.setS4( Gen.alfaToVeld( aS4 ) ); return this; }
public AlfaBuilder aanZet( Kleur aAanZet ) { boStelling.setAanZet( aAanZet ); return this; }
public AlfaBuilder resultaat( ResultaatType aResultaat ) { boStelling.setResultaat( aResultaat ); return this; }
public AlfaBuilder aantalZetten( int aAantalZetten ) { boStelling.setAantalZetten( aAantalZetten ); return this; }
public AlfaBuilder schaak( boolean aSchaak ) { boStelling.setSchaak( aSchaak ); return this; }
public BoStelling build()
{
	return boStelling;
}
}
public static AlfaBuilder alfaBuilder()
{
	return new AlfaBuilder();
}
public static final BoStelling NULL_STELLING = BoStelling.builder()
	.wk( 0 )
	.zk( 0 )
	.s3( 0 )
	.s4( 0 )
	.aanZet( WIT )
	.resultaat( ResultaatType.ILLEGAAL )
	.aantalZetten( 0 )
	.schaak( false )
	.build();
public static Kleur getVeldKleur( int aVeld )
{
	int rij = aVeld / 16;
	int kol = aVeld % 16;
	return ( rij + kol ) % 2 == 0 ? ZWART : WIT;
}

/**
 * CASE : BOOLEAN OF
        |	TRUE : WK, ZK, s3, s4: Veld;
        |	FALSE: Velden        : VeldArr;
 */
private final boolean StellingType = true;
private int wk;
private int zk;
private int s3;
private int s4;
private Kleur aanZet;
private ResultaatType resultaat;
private int aantalZetten; // Was SHORTCARD, een 8 bits unsigned int
private boolean schaak;

@Override
public BoStelling clone()
{
	try
	{
		return (BoStelling) super.clone();
	}
	catch ( CloneNotSupportedException e )
	{
		throw new RuntimeException( e );
	}
}
public String getWkString()
{
	return Config.getStaticStukken().getWk().getStukString();
}
public String getZkString()
{
	return Config.getStaticStukken().getZk().getStukString();
}
public String getS3String()
{
	return Config.getStaticStukken().getS3().getStukString();
}
public String getS4String()
{
	return Config.getStaticStukken().getS4().getStukString();
}
@Override
public String toString()
{
	StringBuilder sb = new StringBuilder();
	sb
	.append( "WK="  ).append( Gen.veldToAlfa( wk ) )
	.append( " ZK=" ).append( Gen.veldToAlfa( zk ) )
	.append( " S3=" ).append( Gen.veldToAlfa( s3 ) )
	.append( " S4=" ).append( Gen.veldToAlfa( s4 ) )
	.append( " AanZet=" ).append( aanZet.getAfko() )
	.append( " Resultaat=" ).append( resultaat )
	.append( " AantalZetten=" ).append( aantalZetten )
	.append( " Schaak=" ).append( schaak ).append( "\n" );
	for ( int rij = 7; rij >= 0; rij-- )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			int veld = 16 * rij + kol;
			String veldString;
			if ( veld == wk ) veldString = getWkString();
			else if ( veld == zk ) veldString = getZkString();
			else if ( veld == s3 ) veldString = getS3String();
			else if ( veld == s4 ) veldString = getS4String();
			else veldString = "..";
			sb.append( veldString ).append( " " );
		}
		sb.append( "\n" );
	}
	return sb.toString();
}
public BoStellingKey getBoStellingKey()
{
	return BoStellingKey.builder()
		.wk( wk )
		.zk( zk )
		.s3( s3 )
		.s4( s4 )
		.aanZet( aanZet )
		.build();
}

}
