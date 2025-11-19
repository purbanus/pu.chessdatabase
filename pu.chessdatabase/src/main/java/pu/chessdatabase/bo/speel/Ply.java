package pu.chessdatabase.bo.speel;

import org.springframework.transaction.annotation.Transactional;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.dal.FlatDocument;
import pu.chessdatabase.dbs.Resultaat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Transactional
@Builder
/**
 * Als we 'van' en 'naar' hebben, dan bevat een Ply
 * - De BoStelling 'van'
 * - Of de BoStelling ten einde is
 * - Het zetnummer van de opky (een zet is twee ply)
 * - De VanNaar( 'van', 'naar' ) d.w.z. de VanNaar die gespeeld is vanuit de boStelling
 */
public class Ply
{
//public static final Ply NULL_PLY = Ply.builder()
//	.id( null )
//	.einde( EindeType.NOG_NIET )
//	.zetNummer( Plies.MAX_HELE_ZET_NUMMER )
//	//.boStelling( BoStelling.NULL_STELLING )
//	.build();
//static
//{
//	NULL_PLY.setBoStelling( BoStelling.NULL_STELLING );
//}
public static Ply fromFlatDocument( FlatDocument aFlatDocument )
{
	return Ply.builder()
		.id( aFlatDocument.getPlyId() )
		.einde( Einde.valueOf( aFlatDocument.getEinde() ) )
		.zetNummer( aFlatDocument.getZetNummer() )
		.schaak( aFlatDocument.isSchaak() )
		.vanNaar( new VanNaar( aFlatDocument.getVan(), aFlatDocument.getNaar() ) )
		.boStelling( BoStelling.fromFlatDocument( aFlatDocument ) )
		.build();
}
public static class Builder
{
	private Ply ply = new Ply();
	public Builder id( int aId ) { ply.setId( aId ); return this; }
	public Builder plies( Plies aPlies ) { ply.setPlies( aPlies ); return this; }
	public Builder einde( Einde aEinde ) { ply.setEinde( aEinde ); return this; }
	public Builder zetNummer( int aZetNummer ) { ply.setZetNummer( aZetNummer ); return this; }
	public Builder schaak( boolean aSchaak ) { ply.setSchaak( aSchaak ); return this; }
	public Builder vanNaar( VanNaar aVanNaar ) { ply.setVanNaar( aVanNaar ); return this; }
	public Builder boStelling( BoStelling aBoStelling ) { ply.setBoStelling( aBoStelling ); return this; }
	public Ply build()
	{
		return ply;
	}
}
public static Builder builder()
{
	return new Builder();
}

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Integer id;

@Column( nullable = false )
@Enumerated( EnumType.STRING )
private Einde einde;

@Column( nullable = false )
private int zetNummer; // Liep in Modula van 1 tot 130!

//Dit moet je niet doen, het is het schaak in de VORIGE ply dat geldt
//public boolean isSchaak()
//{
//	return getBoStelling().isSchaak();
//}
@Column( nullable = false )
private boolean schaak = false;

//private VanNaar vanNaar;
@Column( nullable = true )
private Integer van;

@Column( nullable = true )
private Integer naar;

// private BoStelling boStelling;
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

//Advies van JPA: Ga altijd voor ManyToOne relaties, niet voor OneToMany
@ManyToOne(
	cascade = CascadeType.ALL,
	fetch = FetchType.LAZY
)
@JoinColumn(
	name = "plies_id",
////	referencedColumnName = "id",
	foreignKey = @ForeignKey( name = "FK_Ply_Plies" )
)
//@EqualsAndHashCode.Exclude
@ToString.Exclude
private Plies plies;

public VanNaar getVanNaar()
{
	if ( van == null )
	{
		return null;
	}
	return new VanNaar( van, naar );
}
public void setVanNaar( VanNaar aVanNaar )
{
	if ( aVanNaar == null )
	{
		van = null;
		naar = null;
	}
	else
	{
		van = aVanNaar.getVan();
		naar = aVanNaar.getNaar();
	}
}
public BoStelling getBoStelling()
{
	return BoStelling.builder()
		.wk( wk )
		.zk( zk )
		.s3( s3 )
		.s4( s4 )
		.s5( s5 )
		.aanZet( aanZet )
		.resultaat( resultaat )
		.aantalZetten( aantalZetten )
		.build();
}
public void setBoStelling( BoStelling aBoStelling )
{
	if ( aBoStelling == null )
	{
		throw new RuntimeException( "Je kunt niet een null BoSAtelling in een ply stoppen" );
	}
	wk = aBoStelling.getWk();
	zk = aBoStelling.getZk();
	s3 = aBoStelling.getS3();
	s4 = aBoStelling.getS4();
	s5 = aBoStelling.getS5();
	aanZet = aBoStelling.getAanZet();
	resultaat = aBoStelling.getResultaat();
	aantalZetten = aBoStelling.getAantalZetten();
}
}
