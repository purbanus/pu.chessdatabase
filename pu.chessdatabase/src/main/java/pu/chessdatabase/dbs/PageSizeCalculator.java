package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.CacheType.*;
import static pu.chessdatabase.dbs.Constants.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.Config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Component
@Data
public class PageSizeCalculator
{
public static final CacheType DEFAULT_CACHE_TYPE = Parallel;

private CacheType cacheType;
private final Config config;
@Getter( AccessLevel.PRIVATE ) 
private int pageSize3Stukken;
@Getter( AccessLevel.PRIVATE ) 
private int pageSize4Stukken;
@Getter( AccessLevel.PRIVATE ) 
private int pageSize5Stukken;
@Getter( AccessLevel.PRIVATE ) 
@EqualsAndHashCode.Exclude
private Map<Integer, Integer> pageSizeLookup = null;

@Autowired
public PageSizeCalculator( @Lazy Config aConfig )
{
	this( DEFAULT_CACHE_TYPE, aConfig );
}
public PageSizeCalculator( CacheType aCacheType, Config aConfig )
{
	super();
	setCacheType( aCacheType );	
	config = aConfig;
}
public void setCacheType( CacheType aCacheType )
{
	cacheType = aCacheType;
	setPageSizeLookup( null );
}
private Map<Integer, Integer> getPageSizeLookup()
{
	if ( pageSizeLookup == null )
	{
		calculateAllPageSizes();
		pageSizeLookup = new HashMap<>();
		pageSizeLookup.put( 3,  pageSize3Stukken );
		pageSizeLookup.put( 4,  pageSize4Stukken );
		pageSizeLookup.put( 5,  pageSize5Stukken );
	}
	return pageSizeLookup;
}
private void calculateAllPageSizes()
{
	if ( getCacheType() == Serial )
	{
		pageSize3Stukken = Math.powExact( 64, 1 );
		pageSize4Stukken = Math.powExact( 64, 2 );
		pageSize5Stukken = Math.powExact( 64, 3 );
	}
	else
	{
		pageSize3Stukken = Math.powExact( 64, 2 ) * 2;
		pageSize4Stukken = Math.powExact( 64, 3 ) * 2;
		pageSize5Stukken = Math.powExact( 64, 4 ) * 2;
	}
}
public int getPageSize( int aAantalStukken )
{
	return getPageSizeLookup().get( aAantalStukken );
}
public int getDatabaseSize( int aAantalStukken )
{
	if ( getCacheType() == Serial )
	{
		return ( getConfig().heeftPionnen() ? MAX_STUK : MAX_WK ) * 64 * 2 * getPageSize( aAantalStukken );
	}
	else
	{
		return ( getConfig().heeftPionnen() ? MAX_STUK : MAX_WK ) * getPageSize( aAantalStukken );
	}
}
}
