package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.CacheType.*;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class PageSizeCalculator
{
public static final CacheType DEFAULT_CACHE_TYPE = Serial;

@Getter( AccessLevel.PACKAGE ) 
private final CacheType cacheType;
@Getter( AccessLevel.PRIVATE ) 
private final int pageSize3Stukken;
@Getter( AccessLevel.PRIVATE ) 
private final int pageSize4Stukken;
@Getter( AccessLevel.PRIVATE ) 
private final int pageSize5Stukken;
@Getter( AccessLevel.PRIVATE ) 
private Map<Integer, Integer> pageSizeLookup = null;
public PageSizeCalculator()
{
	this( DEFAULT_CACHE_TYPE );
}
public PageSizeCalculator( CacheType aCacheType )
{
	super();
	cacheType = aCacheType;	
	if ( aCacheType == Serial )
	{
		pageSize3Stukken = 64;
		pageSize4Stukken = 64 * 64;
		pageSize5Stukken = 64 * 64 * 64;
	}
	else
	{
		pageSize3Stukken = 64 * 64 * 2;
		pageSize4Stukken = 64 * 64 * 64 * 2;
		pageSize5Stukken = 64 * 64 * 64 * 64 * 2;
	}
}
public Map<Integer, Integer> getPageSizeLookup()
{
	if ( pageSizeLookup == null )
	{
		pageSizeLookup = new HashMap<>();
		pageSizeLookup.put( 3,  pageSize3Stukken );
		pageSizeLookup.put( 4,  pageSize4Stukken );
		pageSizeLookup.put( 5,  pageSize5Stukken );		
	}
	return pageSizeLookup;
}
public int getPageSize( int aAantalStukken )
{
	return getPageSizeLookup().get( aAantalStukken );
}
public int getDatabaseSize( int aAantalStukken )
{
	if ( getCacheType() == Serial )
	{
		return 10 * 64 * 2 * getPageSize( aAantalStukken );
	}
	else
	{
		return 10 * getPageSize( aAantalStukken );
	}
}
}
