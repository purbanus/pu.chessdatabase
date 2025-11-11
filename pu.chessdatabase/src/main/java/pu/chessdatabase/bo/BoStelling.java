package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;

import pu.chessdatabase.dbs.ResultaatType;
import pu.chessdatabase.service.BoStellingKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
public class BoStelling implements Cloneable
{
public static class AlfaBuilder
{
private BoStelling boStelling = new BoStelling();
public AlfaBuilder wk( String aWk ) { boStelling.setWk( Gen.alfaToVeld( aWk ) ); return this; }
public AlfaBuilder zk( String aZk ) { boStelling.setZk( Gen.alfaToVeld( aZk ) ); return this; }
public AlfaBuilder s3( String aS3 ) { boStelling.setS3( Gen.alfaToVeld( aS3 ) ); return this; }
public AlfaBuilder s4( String aS4 ) { boStelling.setS4( Gen.alfaToVeld( aS4 ) ); return this; }
public AlfaBuilder s5( String aS5 ) { boStelling.setS5( Gen.alfaToVeld( aS5 ) ); return this; }
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
	.s5( 0 )
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

private final boolean StellingType = true;
private int wk;
private int zk;
private int s3;
private int s4;
private int s5;
private Kleur aanZet;
private ResultaatType resultaat;
private int aantalZetten;
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
public void normaliseer( int aAantalStukken )
{
	if ( aAantalStukken == 3 )
	{
		// s4 en s5 zijn witte stukken, dat dus onder de witte koning wordt gezet
		setS4( getWk() );
		setS5( getWk() );
	}
	if ( aAantalStukken == 4 )
	{
		// s5 is een wit stuk, dat dus onder de witte koning wordt gezet
		setS5( getWk() );
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
public String getS5String()
{
	return Config.getStaticStukken().getS5().getStukString();
}
@Override
public String toString()
{
	StringBuilder sb = new StringBuilder()
		.append( "WK="  ).append( Gen.veldToAlfa( wk ) )
		.append( " ZK=" ).append( Gen.veldToAlfa( zk ) )
		.append( " S3=" ).append( Gen.veldToAlfa( s3 ) )
		.append( " S4=" ).append( Gen.veldToAlfa( s4 ) )
		.append( " S5=" ).append( Gen.veldToAlfa( s5 ) )
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
			// Omdat we eerst testen of het de wk is die oip het veld staat, komen andere stukken die op dat 
			// veld staan, niet in aanmerking.
			if ( veld == wk )
			{
				veldString = getWkString();
			}
			else if ( veld == zk )
			{
				veldString = getZkString();
			}
			else if ( veld == s3 )
			{
				veldString = getS3String();
			}
			else if ( veld == s4 )
			{
				veldString = getS4String(); 
				if ( veldString.equals( "WG" ) )
				{
					veldString = "..";
				}
			}
			else if ( veld == s5 )
			{
				veldString = getS5String(); 
				if ( veldString.equals( "WG" ) )
				{
					veldString = "..";
				}
			}
			else
			{
				veldString = "..";
			}
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
		.s5( s5 )
		.aanZet( aanZet )
		.build();
}

}
