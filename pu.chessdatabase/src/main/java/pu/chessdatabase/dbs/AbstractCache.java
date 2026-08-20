package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Lokatie.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import pu.chessdatabase.bo.Config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class AbstractCache implements Cache
{
public static final boolean REPORT_FLUSH = false; 
private RandomAccessFile database = null;
private List<CacheEntry> cacheEntries = new ArrayList<>();
@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PRIVATE ) 
private long generatieTeller;
private Config config;
AbstractCache( Config aConfig, RandomAccessFile aDatabase )
{
	super();
	if ( aDatabase == null )
	{
		throw new RuntimeException( "Database mag niet null zijn" );
	}
	config = aConfig;
	database = aDatabase;
	initializeCache();
	setGeneratieTeller( 1L );
}
abstract void initializeCache();
abstract int getCacheSize();
abstract void pageIn( PageDescriptor aPageDescriptor );
public PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
public int getAantalStukken()
{
	return getConfig().getAantalStukken();
}
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
public void ensurePageIsInRam( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor.getWaar() == OpSchijf )
	{
		pageIn( aPageDescriptor );
	}
}
protected void getRawPageData( PageDescriptor aPageDescriptor )
{
    try
	{
		getDatabase().seek( aPageDescriptor.getSchijfAdres() );
		int pageSize = getPageSize();
		int aantal = getDatabase().read( getPageBytes( aPageDescriptor ), 0, pageSize );
		if ( aantal == -1 )
		{
			throw new RuntimeException( String.format( "Ernstig: VM.getPage heeft -1 records gelezen. Dat betekent vermoedelijk dat de database leeg is, of in ieder geval te klein" ) );
		}
		if ( aantal != pageSize )
		{
			throw new RuntimeException( String.format( "Ernstig: VM.getPage heeft %d records gelezen. Dat zouden er %d moeten zijn", aantal, pageSize ) );
		}
	}
	catch ( IOException e )
	{
		throw new RuntimeException( e );
	}
}
/**
 * ------------ Pagina schrijven naar de schijf ------
 */
@Override
public void pageOut( PageDescriptor aPageDescriptor )
{
	if ( aPageDescriptor != null && isVuil( aPageDescriptor ) )
	{
		putRawPageData( aPageDescriptor );
	}
}
protected void putRawPageData( PageDescriptor aPageDescriptor )
{
	try
	{
		getDatabase().seek( aPageDescriptor.getSchijfAdres() );
		byte [] page = getPageBytes( aPageDescriptor );
	    getDatabase().write( page, 0, getPageSize() );
		setVuil( aPageDescriptor, false );
	}
	catch ( Exception e )
	{
		throw new RuntimeException( e );
	}
}
byte [] getPageBytes( PageDescriptor aPageDescriptor )
{
	CacheEntry cacheEntry = getCacheEntries().get( aPageDescriptor.getCacheNummer() );
	return cacheEntry.getPage();
}
@Override
public byte [] getPage( PageDescriptor aPageDescriptor )
{
	ensurePageIsInRam( aPageDescriptor );
	return getPageBytes( aPageDescriptor );
}
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
private CacheEntry getCacheEntryByNumber( int aCacheNummer )
{
	return getCacheEntries().get( aCacheNummer );
}
@Override
public CacheEntry getCacheEntry( PageDescriptor aPageDescriptor )
{
	return getCacheEntryByNumber( aPageDescriptor.getCacheNummer() );
}
@Override
public void setCacheEntry( PageDescriptor aPageDescriptor, CacheEntry aCacheEntry )
{
	getCacheEntries().set( aPageDescriptor.getCacheNummer(), aCacheEntry );
}
/**
 *  ------- Haal positie op uit de database ---------
 */
@Override
public int get( PageDescriptor aPageDescriptor, VMStelling aVmStelling )
{
	aVmStelling.checkStelling();
	ensurePageIsInRam( aPageDescriptor );
    
	byte vmRec = getPage( aPageDescriptor )[getPositionWithinPage( aVmStelling )];
    return Byte.toUnsignedInt( vmRec );
}
/**
 * --------- Wegschrijven poaitie naar database -----------
 */
@Override
public void put( PageDescriptor aPageDescriptor, VMStelling aVmStelling, int aDbsRec )
{
	aVmStelling.checkStelling();
	ensurePageIsInRam( aPageDescriptor );
    
    byte vmRec = (byte)( aDbsRec & 0xff );
	// @@HIGH Zou het niet beter zijn om hier CacheEntry te gebruiken, voor de performance?
    getPage( aPageDescriptor )[getPositionWithinPage( aVmStelling )] = vmRec;
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
