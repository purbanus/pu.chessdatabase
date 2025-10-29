package pu.chessdatabase.bewaard;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
//import pu.chessdatabase.bo.Gen;
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
public class VMStelling5Stukken extends VMStelling implements Cloneable
{
public static class AlfaBuilder
{
private VMStelling5Stukken vmStelling = new VMStelling5Stukken();
public AlfaBuilder wk( String aWk ) { vmStelling.setWk( VM.alfaToVeld( aWk ) ); return this; }
public AlfaBuilder zk( String aZk ) { vmStelling.setZk( VM.alfaToVeld( aZk ) ); return this; }
public AlfaBuilder s3( String aS3 ) { vmStelling.setS3( VM.alfaToVeld( aS3 ) ); return this; }
public AlfaBuilder s4( String aS4 ) { vmStelling.setS4( VM.alfaToVeld( aS4 ) ); return this; }
public AlfaBuilder s5( String aS5 ) { vmStelling.setS5( VM.alfaToVeld( aS5 ) ); return this; }
public AlfaBuilder aanZet( Kleur aAanZet ) { vmStelling.setAanZet( aAanZet ); return this; }
public VMStelling5Stukken build()
{
	return vmStelling;
}
}
public static AlfaBuilder alfaBuilder()
{
	return new AlfaBuilder();
}

private int s3;
private int s4;
private int s5;
@Override
public void checkStelling()
{
	super.checkStelling();
	if ( getS3() > 63 || getS4() > 63 || getS5() > 63 )
	{
		throw new RuntimeException( "Dit is geen cardinaalstelling: " + this );
	}
	if ( getS3() < 0 || getS4() < 0 || getS5() < 0 )
	{
		throw new RuntimeException( "Dit is geen geldige stelling: " + this );
	}
}
@Override
public int getPositionWithinPage()
{
	return ( getS3() << 12 ) + ( getS4() << 6 ) + getS5();
}
@Override
public BoStelling getBoStelling()
{
	return BoStelling.builder()
		.wk( Dbs.CVT_WK[getWk()] )
		.zk( Dbs.CVT_STUK[getZk()] )
		.s3( Dbs.CVT_STUK[getS3()] )
		.s4( Dbs.CVT_STUK[getS4()] )
		.s5( Dbs.CVT_STUK[getS5()] )
		.aanZet( getAanZet() )
		.build();
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
	StringBuilder sb = new StringBuilder();
	sb
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
			if ( veld == getWk() ) veldString = getWkString();
			else if ( veld == getZk() ) veldString = getZkString();
			else if ( veld == getS3() ) veldString = getS3String();
			else if ( veld == getS4() ) veldString = getS4String();
			else if ( veld == getS5() ) veldString = getS5String();
			else veldString = "..";
			sb.append( veldString ).append( " " );
		}
		sb.append( "\n" );
	}
	return sb.toString();
}
}
