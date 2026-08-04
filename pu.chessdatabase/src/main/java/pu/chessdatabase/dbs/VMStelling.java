package pu.chessdatabase.dbs;

import static pu.chessdatabase.bo.configuraties.StukType.*;

import org.springframework.boot.context.config.ConfigData;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Gen;
//import pu.chessdatabase.bo.Gen;
import pu.chessdatabase.bo.Kleur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VMStelling implements Cloneable
{
public static class AlfaBuilder
{
private VMStelling vmStelling = new VMStelling();
public AlfaBuilder wk( String aWk ) { vmStelling.setWk( VM.alfaToVeld( aWk ) ); return this; }
public AlfaBuilder zk( String aZk ) { vmStelling.setZk( VM.alfaToVeld( aZk ) ); return this; }
public AlfaBuilder s3( String aS3 ) { vmStelling.setS3( VM.alfaToVeld( aS3 ) ); return this; }
public AlfaBuilder s4( String aS4 ) { vmStelling.setS4( VM.alfaToVeld( aS4 ) ); return this; }
public AlfaBuilder s5( String aS5 ) { vmStelling.setS5( VM.alfaToVeld( aS5 ) ); return this; }
public AlfaBuilder aanZet( Kleur aAanZet ) { vmStelling.setAanZet( aAanZet ); return this; }
public VMStelling build()
{
	return vmStelling;
}
}
public static AlfaBuilder alfaBuilder()
{
	return new AlfaBuilder();
}

private int wk;
private int zk;
private int s3;
private int s4;
private int s5;
private Kleur aanZet;

public void checkStelling()
{
	if ( wk < 0 || zk < 0 || s3 < 0 || s4 < 0 || s5 < 0 )
	{
		throw new RuntimeException( "Dit is geen geldige stelling: " + this );
	}
	if ( wk > 63 || zk > 63 || s3 > 63 || s4 > 63 || s5 > 63 )
	{
		throw new RuntimeException( "Dit is geen cardinaalstelling: " + this );
	}

	if ( ! Config.getStaticStukken().heeftPionnen() )
	{
		if ( wk > 9 )
		{
			throw new RuntimeException( "Dit is geen cardinaalstelling: " + this );
		}
	}
}
@Override
public VMStelling clone()
{
	try
	{
		return (VMStelling) super.clone();
	}
	catch ( CloneNotSupportedException e )
	{
		throw new RuntimeException( e );
	}
}
public BoStelling getBoStelling( Transformator aTransformator )
{
	return BoStelling.builder()
		.wk( aTransformator.vmStellingWkToBoStellingWk( getWk() ) )
		.zk( aTransformator.vmStellingStukToBoStellingStuk( getZk() ) )
		.s3( aTransformator.vmStellingStukToBoStellingStuk( getS3() ) )
		.s4( aTransformator.vmStellingStukToBoStellingStuk( getS4() ) )
		.s5( aTransformator.vmStellingStukToBoStellingStuk( getS5() ) )
		.aanZet( getAanZet() )
		.build();
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
	return VM.veldToAlfa( wk );
}
public String getZkAlfa()
{
	return VM.veldToAlfa( zk );
}
public String getS3Alfa()
{
	return VM.veldToAlfa( s3 );
}
public String getS4Alfa()
{
	return VM.veldToAlfa( s4 );
}
public void setWkAlfa( String aVeld )
{
	wk = VM.alfaToVeld( aVeld );
}
public void setZkAlfa( String aVeld )
{
	zk = VM.alfaToVeld( aVeld );
}
public void setS3Alfa( String aVeld )
{
	s3 = VM.alfaToVeld( aVeld );
}
public void sets4Alfa( String aVeld )
{
	s4 = VM.alfaToVeld( aVeld );
}
public void sets5Alfa( String aVeld )
{
	s5 = VM.alfaToVeld( aVeld );
}

@Override
public String toString()
{
	StringBuilder sb = new StringBuilder()
		.append( "WK="  ).append( VM.veldToAlfa( getWk() ) )
		.append( " ZK=" ).append( VM.veldToAlfa( getZk() ) )
		.append( " S3=" ).append( VM.veldToAlfa( getS3() ) )
		.append( " S4=" ).append( VM.veldToAlfa( getS4() ) )
		.append( " S5=" ).append( VM.veldToAlfa( getS5() ) )
		.append( " AanZet=" ).append( getAanZet().getAfko() ).append( "\n" );
	for ( int rij = 7; rij >= 0; rij-- )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			int veld = 8 * rij + kol;
			String veldString;
			// Omdat we eerst testen of het de wk is die oip het veld staat, komen andere stukken die op dat 
			// veld staan, niet in aanmerking.
			if ( veld == getWk() )
			{
				veldString = getWkString();
			}
			else if ( veld == getZk() )
			{
				veldString = getZkString();
			}
			else if ( veld == getS3() )
			{
				veldString = getS3String();
			}
			else if ( veld == getS4() )
			{
				veldString = getS4String(); 
				if ( veldString.equals( "WG" ) )
				{
					veldString = "..";
				}
			}
			else if ( veld == getS5() )
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
}
