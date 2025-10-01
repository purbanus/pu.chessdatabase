package pu.chessdatabase.dal;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Kleur;

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
		.zk( Dbs.CVT_STUK[getWk()] )
		.s3( Dbs.CVT_STUK[getS3()] )
		.s4( Dbs.CVT_STUK[getS4()] )
		.aanZet( getAanZet() )
		.build();
}
@Override
public String toString()
{
	StringBuilder sb = new StringBuilder();
	sb.append( "WK=" ).append( Integer.toHexString( wk ) )
	.append( " ZK=" ).append( Integer.toHexString( zk ) )
	.append( " S3=" ).append( Integer.toHexString( s3 ) )
	.append( " S4=" ).append( Integer.toHexString( s4 ) )
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
