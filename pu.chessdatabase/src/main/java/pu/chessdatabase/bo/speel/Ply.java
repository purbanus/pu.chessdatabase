package pu.chessdatabase.bo.speel;

import static pu.chessdatabase.bo.Kleur.*;

import org.springframework.transaction.annotation.Transactional;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.dal.FlatDocument;

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
		.plyNummer( aFlatDocument.getPlyNummer() )
		.vanNaar( new VanNaar( aFlatDocument.getVan(), aFlatDocument.getNaar() ) )
		.boStelling( BoStelling.fromFlatDocument( aFlatDocument ) )
		.build();
}
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Integer id;

@Column( nullable = false )
@Enumerated( EnumType.STRING )
private Einde einde;

@Column( nullable = false )
private int plyNummer;

@Column( nullable = false )
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

public boolean isSchaak()
{
	return getBoStelling().isSchaak();
}
public int getZetNummer()
{
	if ( ! getPlies().hasPlies() )
	{
		return -1;
	}
	int zetNummer;
	if ( getPlies().getFirstPly().getBoStelling().getAanZet() == Wit )
	{
		zetNummer = ( getPlyNummer() + 1 ) / 2 + 1;
	}
	else
	{
		zetNummer = getPlyNummer() / 2 + 1;
	}
	return zetNummer;
}
public BoStelling getPreviousStelling()
{
	if ( getPlyNummer() > 0 )
	{
		return getPlies().getPly( getPlyNummer() - 1 ).getBoStelling();
	}
	else
	{
		return getPlies().getStartStelling();
	}
}
}
