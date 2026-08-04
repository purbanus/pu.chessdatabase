package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import pu.chessdatabase.dal.FlatDocument;
import pu.chessdatabase.dbs.Resultaat;
import pu.chessdatabase.service.BoStellingKey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Embeddable
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
public AlfaBuilder resultaat( Resultaat aResultaat ) { boStelling.setResultaat( aResultaat ); return this; }
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
public static BoStelling fromFlatDocument( FlatDocument aFlatDocument )
{
	return BoStelling.builder()
		.wk( aFlatDocument.getWk() )
		.zk( aFlatDocument.getZk() )
		.s3( aFlatDocument.getS3() )
		.s4( aFlatDocument.getS4() )
		.s5( aFlatDocument.getS5() )
		.aanZet( Kleur.valueOf( aFlatDocument.getAanZet() ) )
		.resultaat( Resultaat.valueOf( aFlatDocument.getResultaat() ) )
		.aantalZetten( aFlatDocument.getAantalZetten() )
		.schaak( aFlatDocument.isSchaak() )
		.build();
}
public static BoStelling fromFlatDocumentForPlies( FlatDocument aFlatDocument )
{
	return BoStelling.builder()
		.wk( aFlatDocument.getPliesWk() )
		.zk( aFlatDocument.getPliesZk() )
		.s3( aFlatDocument.getPliesS3() )
		.s4( aFlatDocument.getPliesS4() )
		.s5( aFlatDocument.getPliesS5() )
		.aanZet( Kleur.valueOf( aFlatDocument.getPliesAanZet() ) )
		.resultaat( Resultaat.valueOf( aFlatDocument.getPliesResultaat() ) )
		.aantalZetten( aFlatDocument.getPliesAantalZetten() )
		.schaak( aFlatDocument.isPliesSchaak() )
		.build();
}
public static int getRij( int aVeld )
{
	return aVeld / 16;
}
public static int getKol( int aVeld )
{
	return aVeld % 16;
}
public static Kleur getVeldKleur( int aVeld )
{
	return ( getRij( aVeld ) + getKol( aVeld ) ) % 2 == 0 ? Zwart : Wit;
}

@Column( nullable = false )
private int wk;

@Column( nullable = false )
private int zk;

@Column( nullable = false )
private int s3;

@Column( nullable = false )
private int s4;

@Column( nullable = false )
private int s5;

@Column( nullable = false )
@Enumerated( EnumType.STRING )
private Kleur aanZet;

@Column( nullable = false )
@Enumerated( EnumType.STRING )
private Resultaat resultaat;

@Column( nullable = false )
private int aantalZetten;

@Column( nullable = false )
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
public Stuk getWkStuk()
{
	return Config.getStaticStukken().getWk();
}
public Stuk getZkStuk()
{
	return Config.getStaticStukken().getZk();
}
public Stuk getS3Stuk()
{
	return Config.getStaticStukken().getS3();
}
public Stuk getS4Stuk()
{
	return Config.getStaticStukken().getS4();
}
public Stuk getS5Stuk()
{
	return Config.getStaticStukken().getS5();
}
public String getWkString()
{
	return getWkStuk().getStukString();
}
public String getZkString()
{
	return getZkStuk().getStukString();
}
public String getS3String()
{
	return getS3Stuk().getStukString();
}
public String getS4String()
{
	return getS4Stuk().getStukString();
}
public String getS5String()
{
	return getS5Stuk().getStukString();
}
public boolean isS3Pion()
{
	return Config.getStaticStukken().getS3().getStukType() == Pion;
}
public boolean isS4Pion()
{
	return Config.getStaticStukken().getS4().getStukType() == Pion;
}
public boolean isS5Pion()
{
	return Config.getStaticStukken().getS5().getStukType() == Pion;
}
public String getWkAlfa()
{
	return Gen.veldToAlfa( wk );
}
public String getZkAlfa()
{
	return Gen.veldToAlfa( zk );
}
public String getS3Alfa()
{
	return Gen.veldToAlfa( s3 );
}
public String getS4Alfa()
{
	return Gen.veldToAlfa( s4 );
}
public String getS5Alfa()
{
	return Gen.veldToAlfa( s5 );
}
public void setWkAlfa( String aVeld )
{
	wk = Gen.alfaToVeld( aVeld );
}
public void setZkAlfa( String aVeld )
{
	zk = Gen.alfaToVeld( aVeld );
}
public void setS3Alfa( String aVeld )
{
	s3 = Gen.alfaToVeld( aVeld );
}
public void sets4Alfa( String aVeld )
{
	s4 = Gen.alfaToVeld( aVeld );
}
public void sets5Alfa( String aVeld )
{
	s5 = Gen.alfaToVeld( aVeld );
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
