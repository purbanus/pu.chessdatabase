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
 * - Het zetnummer van de ply (een zet is twee ply)
 * - De VanNaar( 'van', 'naar' ) d.w.z. de VanNaar die gespeeld is vanuit de boStelling
 */
public class Ply
{
public static Ply fromFlatDocument( FlatDocument aFlatDocument )
{
	return Ply.builder()
		.id( aFlatDocument.getPlyId() )
		//.plies(xxx) @@zit niet in FlatDocument
		.einde( Einde.valueOf( aFlatDocument.getEinde() ) )
		.zetNummer( aFlatDocument.getZetNummer() )
		.plySchaak( aFlatDocument.isSchaak() )
		.vanNaar( new VanNaar( aFlatDocument.getVan(), aFlatDocument.getNaar() ) )
		.boStelling( BoStelling.fromFlatDocument( aFlatDocument ) )
		.build();
}
//public static class Builder
//{
//	private Ply ply = new Ply();
//	public Builder id( int aId ) { ply.setId( aId ); return this; }
//	public Builder plies( Plies aPlies ) { ply.setPlies( aPlies ); return this; }
//	public Builder einde( Einde aEinde ) { ply.setEinde( aEinde ); return this; }
//	public Builder zetNummer( int aZetNummer ) { ply.setZetNummer( aZetNummer ); return this; }
//	public Builder schaak( boolean aSchaak ) { ply.setSchaak( aSchaak ); return this; }
//	public Builder vanNaar( VanNaar aVanNaar ) { ply.setVanNaar( aVanNaar ); return this; }
//	public Builder boStelling( BoStelling aBoStelling ) { ply.setBoStelling( aBoStelling ); return this; }
//	public Ply build()
//	{
//		return ply;
//	}
//}
//public static Builder builder()
//{
//	return new Builder();
//}

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Integer id;

@Column( nullable = false )
@Enumerated( EnumType.STRING )
private Einde einde;

@Column( nullable = false )
private int zetNummer; // Liep in Modula van 1 tot 130!

//@@NOG Dit nog eens goed doordenken: waarom is het niet het schaak van de ONDERHAVIGE ply?
//Dit moet je niet doen, het is het schaak in de VORIGE ply dat geldt
//public boolean isSchaak()
//{
//	return getBoStelling().isSchaak();
//}
@Column( nullable = false )
private boolean plySchaak = false;

@Column( nullable = true )
private VanNaar vanNaar;

@Column( nullable = false )
private BoStelling boStelling;

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

}
