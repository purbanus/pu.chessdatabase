package pu.chessdatabase.bo.speel;

import static pu.chessdatabase.bo.Kleur.*;

import java.util.ArrayList;
import java.util.List;

import pu.chessdatabase.bo.BoStelling;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class Plies
{
@Setter( AccessLevel.PRIVATE ) private int currentPlyNumber = -1;
@Setter( AccessLevel.PRIVATE ) private boolean begonnen = false;
@Setter( AccessLevel.PRIVATE ) private List<Ply> plies = new ArrayList<>();

public int getSize()
{
	return getPlies().size();
}
public int getLastPlyNumber()
{
	return getPlies().size() - 1;
}
public void clear()
{
	getPlies().clear();
	setCurrentPlyNumber( -1 );
}
public void addPly( Ply aPly )
{
	getPlies().add( aPly );
	setBegonnen( true );
	setVooruit();
}
public Ply addPly( BoStelling aBoStelling, EindeType aEindeType )
{
	int zetNummer;
	if ( hasPlies() )
	{
		zetNummer = getCurrentPly().getZetNummer();
		if ( aBoStelling.getAanZet() == WIT )
		{
			zetNummer++;
		}
	}
	else
	{
		zetNummer = 1;
	}
	Ply newPly = Ply.builder()
		.boStelling( aBoStelling )
		.einde( aEindeType )
		.zetNummer( zetNummer )
//		.vanNaar( VanNaar.ILLEGAL_VAN_NAAR ) // Liever null want daar kun je gemakkelijk op testen
		.build();
	addPly( newPly );
	return newPly;
}
public boolean hasPly( int aPlyNumber )
{
	return aPlyNumber >= 0 && aPlyNumber < getPlies().size();
}
public Ply getPly( int aPlyNumber )
{
	if ( aPlyNumber > getLastPlyNumber() )
	{
		throw new RuntimeException( "Fout in createZetDocument: Plynummer > laatste zet" );
	}
	if ( aPlyNumber < 0 )
	{
		throw new RuntimeException( "Fout in createZetDocument: Plynummer negatief" );
	}
	return getPlies().get(  aPlyNumber );
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
	if ( getCurrentPlyNumber() < 0 )
	{
		throw new RuntimeException( "Fout in getCurrentPly: huidige Plynummer negatief" );
	}
	return getPlies().get( currentPlyNumber );
}
public Ply getPreviousPly()
{
	if ( getCurrentPlyNumber() < 0 )
	{
		throw new RuntimeException( "Fout in getPreviousPly: huidige plynummer negatief" );
	}
	if ( getCurrentPlyNumber() == 0 )
	{
		throw new RuntimeException( "Fout in getPreviousPly: er is geen vorige ply" );
	}
	return getPlies().get( getCurrentPlyNumber() - 1 );
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
public boolean isAtLastPlyNumber()
{
	return getCurrentPlyNumber() == getPlies().size() - 1;
}
public boolean isNaarBeginMag()
{
	return isBegonnen() && getCurrentPlyNumber() > 0;
}
public void setToBegin()
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "Fout in setToBegin: er is geen begin want de partij is nog niet begonnen" );
	}
	if ( getCurrentPlyNumber() <= 0 )
	{
		throw new RuntimeException( "Fout in setToBegin: we zijn al aan het begin" );
	}
	setCurrentPlyNumber( 0 );
}
public boolean isTerugMag()
{
	return isBegonnen() && getCurrentPlyNumber() > 0;
}
public void setTerug()
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "Fout in setTerug: er is geen zet terug want de partij is nog niet begonnen" );
	}
	if ( getCurrentPlyNumber() <= 0 )
	{
		throw new RuntimeException( "Fout in setTerug: er is geen zet terug want de partij is nog aan het begin" );
	}
	currentPlyNumber--;
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
	currentPlyNumber++;
}
public boolean isNaarEindeMag()
{
	return isBegonnen() && getCurrentPlyNumber() < getLastPlyNumber();
}
public void setNaarEinde()	
{
	if ( ! isBegonnen() )
	{
		throw new RuntimeException( "Fout in setToEnd: de partij is nog niet begonnen" );
	}
	if ( getCurrentPlyNumber() >= getLastPlyNumber() )
	{
		throw new RuntimeException( "Fout in setNaarEinde: we zijn al op de laatst gespeelde zet" );
	}
	setCurrentPlyNumber( getLastPlyNumber() );
}
public void clearPliesFromNextPly()
{
	int lastPlyNumber = getLastPlyNumber();
	for ( int x = getCurrentPlyNumber() + 1; x <= lastPlyNumber; x++ )
	{
		getPlies().remove( getCurrentPlyNumber() + 1 );
	}
}
public EindeType getCurrentEinde()
{
	return getCurrentPly().getEinde();
}
void setCurrentPlyNumberForTestingOnlhy( int aCurrentPlyNumber )
{
	currentPlyNumber = aCurrentPlyNumber;
}
}