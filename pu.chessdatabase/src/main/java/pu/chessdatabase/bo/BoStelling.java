package pu.chessdatabase.bo;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;
import pu.chessdatabase.dal.VMStelling;

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
public static BoStelling fromVmStelling( VMStelling aVmStelling )
{
	return builder()
		.wk( Dbs.CvtWK[aVmStelling.getWk()] )
		.zk( Dbs.CvtStuk[aVmStelling.getZk()] )
		.s3( Dbs.CvtStuk[aVmStelling.getS3()] )
		.s4( Dbs.CvtStuk[aVmStelling.getS4()] )
		.aanZet( aVmStelling.isAanZet() )
		.build();
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
private boolean aanZet;
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
@Override
public String toString()
{
	StringBuilder sb = new StringBuilder();
	sb.append( "WK=" ).append( Integer.toHexString( wk ) )
	.append( " ZK=" ).append( Integer.toHexString( zk ) )
	.append( " S3=" ).append( Integer.toHexString( s3 ) )
	.append( " S4=" ).append( Integer.toHexString( s4 ) )
	.append( " AanZet=" ).append( aanZet == false ? "W" : "Z" )
	.append( " Resultaat=" ).append( resultaat )
	.append( " AantalZetten=" ).append( aantalZetten )
	.append( " Schaak=" ).append( schaak ).append( "\n" );
	for ( int rij = 7; rij >= 0; rij-- )
	{
		if ( rij < 8 )
		{
			for ( int kol = 0; kol < 8; kol++ )
			{
				int veld = 16 * rij + kol;
				String veldString;
				if ( veld == wk ) veldString = "WK";
				else if ( veld == zk ) veldString = "ZK";
				else if ( veld == s3 ) veldString = "WD";
				else if ( veld == s4 ) veldString = "ZT";
				else veldString = "..";
				sb.append( veldString ).append( " " );
			}
		}
		sb.append( "\n" );
	}
	return sb.toString();
}
}
