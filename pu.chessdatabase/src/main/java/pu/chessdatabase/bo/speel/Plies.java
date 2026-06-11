package pu.chessdatabase.bo.speel;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.dal.FlatDocument;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Plies implements Serializable
{
public static final int MAX_HELE_ZET_NUMMER = 130;
public static LocalDateTime timeStampToLocalDateTime( Timestamp aTimeStamp )
{
	// @@HIGH moet je hier niet meteen afronden op hele seconden?
	return aTimeStamp.toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
}
public static Plies fromFlatDocument( FlatDocument aFlatDocument )
{
	return builder()
		.id( aFlatDocument.getPliesId() )
		.configString( aFlatDocument.getConfigString() )
		.startStelling( BoStelling.fromFlatDocumentForPlies( aFlatDocument ) )
		.userName( aFlatDocument.getUserName() )
		.started( timeStampToLocalDateTime( aFlatDocument.getStarted() ) )
		.currentPlyNummer( aFlatDocument.getCurrentPlyNummer() )
		.begonnen( aFlatDocument.isBegonnen() )
		.plies( new ArrayList<>() )
		.build();
}

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Integer id;

@Column( nullable = false )
private String configString;

@Column( nullable = false )
private BoStelling startStelling;

@Column( nullable = false )
private String userName;

@Column( nullable = false )
private LocalDateTime started;

@Setter( AccessLevel.PRIVATE )
@Column( nullable = false )
@Builder.Default
private int currentPlyNummer = -1;

@Setter( AccessLevel.PRIVATE ) 
@Column( nullable = false )
@Builder.Default
private boolean begonnen = false;

@Setter( AccessLevel.PRIVATE ) 
@Builder.Default
@OneToMany(
	mappedBy = "plies",
	cascade = CascadeType.ALL, // Zodat de plys ook gesavet worden
	fetch = FetchType.LAZY 
)
@EqualsAndHashCode.Exclude
@OnDelete( action = OnDeleteAction.CASCADE )
private List<Ply> plies = new ArrayList<>();

public Plies( String aConfigString )
{
	super();
	configString = aConfigString;
	// @@HIGH Waarom doet hij niet wat hieboven staat?
	plies = new ArrayList<>();
	currentPlyNummer = -1;
}
public int getSize()
{
	return getPlies().size();
}
public int getLastPlyNummer()
{
	return getPlies().size() - 1;
}
public void setStarted( LocalDateTime aLocalDateTime )
{
	started = aLocalDateTime.truncatedTo( ChronoUnit.SECONDS );
}
public void clear()
{
	getPlies().clear();
	setCurrentPlyNummer( -1 );
}
public void addPly( Ply aPly )
{
	setCurrentPlyNummer( getPlies().size());
	aPly.setPlies( this );
	getPlies().add( aPly );
	setBegonnen( true );
}
public Ply addPly( BoStelling aBoStelling, VanNaar aVanNaar, Einde aEindeType )
{
	int plyNummer;
	plyNummer = getPlies().size();
	Ply newPly = Ply.builder()
		//.id is voor JPA
		.plies( this )
		.einde( aEindeType )
		.plyNummer( plyNummer )
		.vanNaar( aVanNaar )
		.boStelling( aBoStelling )
		.build();
	addPly( newPly );
	return newPly;
}
public boolean hasPly( int aPlyNummer )
{
	return aPlyNummer >= 0 && aPlyNummer < getPlies().size();
}
public Ply getPly( int aPlyNummer )
{
	if ( aPlyNummer > getLastPlyNummer() )
	{
		throw new RuntimeException( "Fout in getPly: Plynummer > laatste zet" );
	}
	if ( aPlyNummer < 0 )
	{
		throw new RuntimeException( "Fout in getPly: Plynummer negatief" );
	}
	return getPlies().get(  aPlyNummer );
}
public Ply getFirstPly()
{
	if ( ! hasPly( 0 ) )
	{
		throw new RuntimeException( "Fout in getFirstPly: er zijn geen plies dus ook geen eerste ply" );
	}
	return getPlies().get( 0 );
}
public Ply getCurrentPly()
{
	// @@NOG Dit is een zwaktebod. Je kunt beter alle referenties naar getCurrentPly afgaan
	//       en daar beslissen wat te doen. Onthoud: er IS geen ply!
	//       MEE EENS dus afgesterd. Nu oveeral waar dit een foute test oplevert:
	//       - kijken hoe erg het is
	//       - die situatie voorkomen
	if ( getCurrentPlyNummer() < 0 )
	{
//		return Ply.builder()
//			.plies( this )
//			.einde( Einde.Nog_niet )
//			.plyNummer( -1 )
//			.vanNaar( null )
//			.boStelling( getStartStelling() )
//			.build();
		throw new RuntimeException( "Fout in getCurrentPly: huidige Plynummer negatief" );
	}
	return getPlies().get( getCurrentPlyNummer() );
}
//public BoStelling getCurrentStand()
//{
//	if ( getCurrentPlyNummer() < 0 )
//	{
//		return getStartStelling();
//	}
//	return getPlies().get( currentPlyNummer ).getBoStelling();
//}
public boolean hasPreviousPly()
{
	return getCurrentPlyNummer() > 0;
}public Ply getPreviousPly()
{
	if ( getCurrentPlyNummer() < 0 )
	{
		throw new RuntimeException( "Fout in getPreviousPly: huidige plynummer negatief" );
	}
	if ( getCurrentPlyNummer() == 0 )
	{
		throw new RuntimeException( "Fout in getPreviousPly: er is geen vorige ply" );
	}
	return getPlies().get( getCurrentPlyNummer() - 1 );
}
public Ply getLastPly()
{
	if ( ! hasPlies() )
	{
		throw new RuntimeException( "Fout in getLastPly: er zijn geen plies dus ook geen laatste ply" );
	}
	return getPlies().get( getPlies().size() - 1 );
}

public Ply getSecondPly()
{
	if ( ! hasPly( 1 ) )
	{
		throw new RuntimeException( "Fout in getSecondPly: er is geen tweede ply" );
	}
	return getPlies().get( 1 );
}
public boolean hasPlies()
{
	return getPlies().size() > 0;
}
public boolean isAtLastPlyNummer()
{
	return getCurrentPlyNummer() == getPlies().size() - 1;
}
public BoStelling getStand()
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "De partij is nog niet begonnen, dus er is nog geen stand" );
	}
	if ( hasPlies() && getCurrentPlyNummer() >= 0 )
	{
		return getCurrentPly().getBoStelling();
	}
	else
	{
		return getStartStelling();
	}
}
public boolean isNaarBeginMag()
{
	return isBegonnen() && getCurrentPlyNummer() > 0;
}
public void setToBegin()
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "Fout in setToBegin: er is geen begin want de partij is nog niet begonnen" );
	}
	if ( getCurrentPlyNummer() <= 0 )
	{
		throw new RuntimeException( "Fout in setToBegin: we zijn al aan het begin" );
	}
	setCurrentPlyNummer( -1 );
}
public boolean isTerugMag()
{
	return isBegonnen() && getCurrentPlyNummer() > 0;
}
public void setTerug()
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "Fout in setTerug: er is geen zet terug want de partij is nog niet begonnen" );
	}
	if ( getCurrentPlyNummer() <= 0 )
	{
		throw new RuntimeException( "Fout in setTerug: er is geen zet terug want de partij is nog aan het begin" );
	}
	currentPlyNummer--;
}
public boolean isVooruitMag()
{
	return isBegonnen();
}
public void setVooruit()
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "Fout in setVooruit: er is geen zet vooruit want de partij is nog niet begonnen" );
	}
	currentPlyNummer++;
}
public boolean isNaarEindeMag()
{
	return isBegonnen() && getCurrentPlyNummer() < getLastPlyNummer();
}
public void setNaarEinde()	
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "Fout in setToEnd: de partij is nog niet begonnen" );
	}
	if ( getCurrentPlyNummer() >= getLastPlyNummer() )
	{
		throw new RuntimeException( "Fout in setNaarEinde: we zijn al op de laatst gespeelde zet" );
	}
	setCurrentPlyNummer( getLastPlyNummer() );
}
public void clearPliesFromNextPly()
{
	int lastPlyNummer = getLastPlyNummer();
	for ( int x = getCurrentPlyNummer() + 1; x <= lastPlyNummer; x++ )
	{
		getPlies().remove( getCurrentPlyNummer() + 1 );
	}
}
public Einde getCurrentEinde()
{
	if ( getCurrentPlyNummer() < 0 )
	{
		return Einde.Nog_niet;
	}
	return getCurrentPly().getEinde();
}
void setCurrentPlyNummerForTestingOnlhy( int aCurrentPlyNummer )
{
	currentPlyNummer = aCurrentPlyNummer;
}
}