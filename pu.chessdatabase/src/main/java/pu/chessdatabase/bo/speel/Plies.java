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
		zetNummer = getCurrentPly().getZetNr();
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
		.zetNr( zetNummer )
//		.vanNaar( VanNaar.ILLEGAL_VAN_NAAR ) // Liever null want daar kun je gemakkelijk op testen
		.build();
	addPly( newPly );
	return newPly;
}
public boolean hasPly( int aPlyNumber )
{
	return aPlyNumber <= getPlies().size() - 1;
}
public Ply getPly( int aPlyNumber )
{
	if ( aPlyNumber > getLastPlyNumber() )
	{
		throw new RuntimeException( "Fout in createZetDocument: Plynummer > laatste zet" );
	}
	return getPlies().get(  aPlyNumber );
}
public Ply getCurrentPly()
{
	if ( getCurrentPlyNumber() < 0 )
	{
		return null;
	}
	return getPlies().get( currentPlyNumber );
}
public Ply getPreviousPly()
{
	if ( getCurrentPlyNumber() <= 0 )
	{
		return null;
	}
	return getPlies().get( getCurrentPlyNumber() - 1 );
}
public Ply getFirstPly()
{
	if ( hasPly( 0 ) )
	{
		return getPlies().get( 0 );
	}
	return null;
}
public Ply getSecondPly()
{
	if ( hasPly( 1 ) )
	{
		return getPlies().get( 1 );
	}
	return null;
}
public Ply getLastPly()
{
	if ( hasPlies() )
	{
		return getPlies().get( getPlies().size() - 1 );
	}
	return null;
}
public boolean hasPlies()
{
	return getPlies().size() > 0;
}
public boolean isAtLastPlyNumber()
{
	return getCurrentPlyNumber() == getPlies().size() - 1;
}
public void setToBegin()
{
	if ( isBegonnen() )
	{
		setCurrentPlyNumber( 0 );
	}
}
public void setTerug()
{
	if ( isBegonnen() )
	{
		currentPlyNumber--;
	}
}
public void setVooruit()
{
	if ( isBegonnen() )
	{
		currentPlyNumber++;
	}
}
public void setToEnd()	
{
	if ( isBegonnen() && ! isAtLastPlyNumber() )
	{
		setCurrentPlyNumber( getLastPlyNumber() );
	}
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
}