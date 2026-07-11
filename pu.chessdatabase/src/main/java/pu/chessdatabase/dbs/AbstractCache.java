package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Lokatie.*;
import static pu.chessdatabase.dbs.CacheType.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class AbstractCache implements Cache
{
public static final boolean REPORT_FLUSH = false; 
private final int aantalStukken;
private RandomAccessFile database = null;
private List<CacheEntry> cacheEntries = new ArrayList<>();
@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PRIVATE ) 
private long generatieTeller;
private PageSizeCalculator pageSizeCalculator;
AbstractCache( PageSizeCalculator aPageSizeCalculator, int aAantalStukken, RandomAccessFile aDatabase )
{
	super();
	pageSizeCalculator = aPageSizeCalculator;
	database = aDatabase;
	aantalStukken = aAantalStukken;
	initializeCache();
	setGeneratieTeller( 1L );
}
abstract void initializeCache();
abstract int getCacheSize();
abstract void pageIn( PageDescriptor aPageDescriptor );
long incrementGeneratieTeller()
{
	return ++generatieTeller;
}
@Override
public int getPageSize()
{
	return getPageSizeCalculator().getPageSize( getAantalStukken() );
}
@Override
public long getDatabaseSize()
{
	return getPageSizeCalculator().getDatabaseSize( getAantalStukken() );
}
@Override
public byte [] getPage( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getCacheNummer() >= getCacheSize() )
	{
		System.out.println( "Got him! Hij is " + aPageDescriptor.getCacheNummer() );
	}
	CacheEntry cacheEntry = getCacheEntries().get( aPageDescriptor.getCacheNummer() );
	return cacheEntry.getPage();
}
@Override
public byte [ ] getPageFromDatabase( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getWaar() == OpSchijf )
	{
		pageIn( aPageDescriptor );
	}
	return getPage( aPageDescriptor );
}
//@@NOG private maken want wordt alleen in tests gebruikt. Helaas ook in TestVM, dus nog ff niet
@SuppressWarnings( "unused" )
private void setPage( PageDescriptor aPageDescriptor, byte [] aPage )
{
	getCacheEntries().get( aPageDescriptor.getCacheNummer() ).setPage( aPage );
}
protected boolean isVuil( PageDescriptor aPageDescriptor )
{
	return getCacheEntries().get( aPageDescriptor.getCacheNummer() ).isVuil();
}
@Override
public void setVuil( PageDescriptor aPageDescriptor, boolean aVuil )
{
	getCacheEntries().get( aPageDescriptor.getCacheNummer() ).setVuil( aVuil );
}
protected void getRawPageData( PageDescriptor aPageDescriptor )
{
    try
	{
		getDatabase().seek( aPageDescriptor.getSchijfAdres() );
		int pageSize = getPageSize();
		int aantal = getDatabase().read( getPage( aPageDescriptor ), 0, pageSize );
		if ( aantal != pageSize )
		{
			throw new RuntimeException( "Ernstig: VM.GetPage heeft " + aantal + " records gelezen. Dat zouden er " + pageSize + " moeten zijn" );
		}
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
}
@Override
public CacheEntry getCacheEntry( PageDescriptor aPageDescriptor )
{
	return getCacheEntries().get( aPageDescriptor.getCacheNummer() );
}
@Override
public void setCacheEntry( PageDescriptor aPageDescriptor, CacheEntry aCacheEntry )
{
	getCacheEntries().set( aPageDescriptor.getCacheNummer(), aCacheEntry );
}
protected void putRawPageData( PageDescriptor aPageDescriptor )
{
	try
	{
		getDatabase().seek( aPageDescriptor.getSchijfAdres() );
	    //getDatabase().write( Cache[aPageDescriptor.getCacheNummer()].getPage().getPage(), 0, PAGE_SIZE );
		byte [] page = getPage( aPageDescriptor );
	    getDatabase().write( page, 0, getPageSize() );
	    // @@HIGH moet hier niet vuil=false gedaan worden?
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
}
/**
 *------------ Pagina schrijven naar de schijf ------
 */
@Override
public void pageOut( PageDescriptor aPageDescriptor )
{
    if ( aPageDescriptor != null && isVuil( aPageDescriptor ) )
    {
        putRawPageData( aPageDescriptor );
        setVuil( aPageDescriptor, false );
    }
}
/**
 *  ------- Haal pagina op uit de cache ---------
 */
@Override
public byte getData( PageDescriptor aPageDescriptor, VMStelling aVmStelling )
{
    return getData( aPageDescriptor, getPositionWithinPage( aVmStelling ) );
}
byte getData( PageDescriptor aPageDescriptor, int aPositionWithinPage )
{
    return getPage( aPageDescriptor )[aPositionWithinPage];
}
@Override
public void setData( PageDescriptor aPageDescriptor, VMStelling aVmStelling, byte aData )
{
    setData( aPageDescriptor, getPositionWithinPage( aVmStelling ), aData );
}
void setData( PageDescriptor aPageDescriptor, int aPositionWithinPage, byte aData )
{
	// @@HIGH Zou het niet beter zijn om hier CacheEntry te gebruiken, voor de performance?
    getPage( aPageDescriptor )[aPositionWithinPage] = aData;
	setVuil( aPageDescriptor, true );
}
@Override
public void flush()
{
	if ( REPORT_FLUSH )
	{
		System.out.print( "Flush called" );
	}
	int teller = 0;
	for ( CacheEntry cacheEntry : getCacheEntries() )
	{
		if ( cacheEntry.getPageDescriptor() != null && cacheEntry.getPageDescriptor().getCacheNummer() != Integer.MAX_VALUE )
		{
			pageOut( cacheEntry.getPageDescriptor() );
			cacheEntry.setGeneratie( 0 );
			if ( REPORT_FLUSH )
			{
				teller++;
			}
		}
	}
	if ( REPORT_FLUSH )
	{
		System.out.println( " , pageOut in flush " + teller + " keer" );
	}
	setGeneratieTeller( 1 );
}
}
