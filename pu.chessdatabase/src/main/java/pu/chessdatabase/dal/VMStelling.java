package pu.chessdatabase.dal;

import pu.chessdatabase.bo.BoStelling;
//import pu.chessdatabase.bo.Gen;
import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.BoStelling.AlfaBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VMStelling implements Cloneable
{
public static class AlfaBuilder
{
private VMStelling vmStelling = new VMStelling();
public AlfaBuilder wk( String aWk ) { vmStelling.setWk( VM.alfaToVeld( aWk ) ); return this; };
public AlfaBuilder zk( String aZk ) { vmStelling.setZk( VM.alfaToVeld( aZk ) ); return this; };
public AlfaBuilder s3( String aS3 ) { vmStelling.setS3( VM.alfaToVeld( aS3 ) ); return this; };
public AlfaBuilder s4( String aS4 ) { vmStelling.setS4( VM.alfaToVeld( aS4 ) ); return this; };
public AlfaBuilder aanZet( Kleur aAanZet ) { vmStelling.setAanZet( aAanZet ); return this; };
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
private Kleur aanZet;

public void checkStelling()
{
	if ( wk > 9 || zk > 63 || s3 > 63 || s4 > 63 )
	{
		throw new RuntimeException( "Dit is geen cardinaalstelling: " + this );
	}
	if ( wk < 0 || zk < 0 || s3 < 0 || s4 < 0 )
	{
		throw new RuntimeException( "Dit is geen geldige stelling: " + this );
	}
}
public int getPositionWithinPage()
{
	return (getS3() << 6) + getS4();
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
public BoStelling getBoStelling()
{
	return BoStelling.builder()
		.wk( Dbs.CVT_WK[getWk()] )
		.zk( Dbs.CVT_STUK[getZk()] )
		.s3( Dbs.CVT_STUK[getS3()] )
		.s4( Dbs.CVT_STUK[getS4()] )
		.aanZet( getAanZet() )
		.build();
}
@Override

public String toString()
{
	StringBuilder sb = new StringBuilder();
	sb
	.append( "WK="  ).append( VM.veldToAlfa( wk ) )
	.append( " ZK=" ).append( VM.veldToAlfa( zk ) )
	.append( " S3=" ).append( VM.veldToAlfa( s3 ) )
	.append( " S4=" ).append( VM.veldToAlfa( s4 ) )
	.append( " AanZet=" ).append( aanZet.getAfko() ).append( "\n" );
	for ( int rij = 7; rij >= 0; rij-- )
	{
		for ( int kol = 0; kol < 8; kol++ )
		{
			int veld = 8 * rij + kol;
			String veldString;
			if ( veld == wk ) veldString = "WK";
			else if ( veld == zk ) veldString = "ZK";
			else if ( veld == s3 ) veldString = "WD";
			else if ( veld == s4 ) veldString = "ZT";
			else veldString = "..";
			sb.append( veldString ).append( " " );
		}
		sb.append( "\n" );
	}
	return sb.toString();
}
}
