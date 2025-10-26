package pu.chessdatabase.dal;

import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import lombok.Data;

@Data
public class MockCache
{

Cache delegate;
private Map<String, Method> methodLookup = new HashedMap<>();

public MockCache( Cache aCache )
{
	super();
	delegate = aCache;
	initializeMethods();
}
private void initializeMethods()
{
	Method [] methods = getDelegate().getClass().getDeclaredMethods();
	for ( Method method : methods )
	{
		methodLookup.put( method.getName(), method );
		method.setAccessible( true );
	}
}
Object callMethod( String aMethodName, Object...aParams )
{
	try
	{
		Method method = methodLookup.get( aMethodName );
		return method.invoke( getDelegate(), aParams );
	}
	catch ( IllegalAccessException e )
	{
		throw new RuntimeException( e );
	}
	catch ( InvocationTargetException e )
	{
		throw new RuntimeException( e );
	}
	catch ( SecurityException e )
	{
		throw new RuntimeException( e );
	}
}
RandomAccessFile getDatabase()
{
	return (RandomAccessFile) callMethod( "getDatabase" );
}
@SuppressWarnings( "unchecked" )
List<CacheEntry> getCache()
{
	return (List<CacheEntry>) callMethod( "getCache" );
}
long getGeneratieTeller()
{
	return (long) callMethod( "getGeneratieTeller" );
}

void initializeCache()
{
	callMethod( "initializeCache" );
}
int getFreeCacheEntry()
{
	return (int) callMethod( "getFreeCacheEntry" );
}
Page getPage( PageDescriptor aPageDescriptor )
{
	return (Page) callMethod( "getPage", aPageDescriptor );
}
byte [] getPageData( PageDescriptor aPageDescriptor )
{
	return (byte []) callMethod( "getPageData", aPageDescriptor );
}
void setPage( PageDescriptor aPageDescriptor, Page aPage )
{
	callMethod( "setPage", aPageDescriptor, aPage );
}
boolean isVuil( PageDescriptor aPageDescriptor )
{
	return (boolean) callMethod( "isVuil", aPageDescriptor );
}
void setVuil( PageDescriptor aPageDescriptor, boolean aVuil )
{
	callMethod( "setVuil", aPageDescriptor, aVuil );
}
CacheEntry getCacheEntry( PageDescriptor aPageDescriptor )
{
	return (CacheEntry) callMethod( "getCacheEntry", aPageDescriptor );
}
void setCacheEntry( PageDescriptor aPageDescriptor, CacheEntry aCacheEntry )
{
	callMethod( "setCacheEntry", aPageDescriptor, aCacheEntry );
}
void getRawPageData( PageDescriptor aPageDescriptor )
{
	callMethod( "getRawPageData", aPageDescriptor );
}
void putRawPageData( PageDescriptor aPageDescriptor )
{
	callMethod( "putRawPageData", aPageDescriptor );
}
void pageOut( PageDescriptor aPageDescriptor )
{
	callMethod( "pageOut", aPageDescriptor );
}
void pageIn( PageDescriptor aPageDescriptor )
{
	callMethod( "pageIn", aPageDescriptor );
}
Page getPageFromDatabase( PageDescriptor aPageDescriptor )
{
	return (Page) callMethod( "getPageFromDatabase", aPageDescriptor );
}
void setData( PageDescriptor aPageDescriptor, int aPositionWithinPage, byte aData )
{
	callMethod( "setData", aPageDescriptor, aPositionWithinPage, aData );
}
void flush()
{
	callMethod( "flush" );
}
}
