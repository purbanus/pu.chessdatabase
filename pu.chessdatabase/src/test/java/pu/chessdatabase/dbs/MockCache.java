package pu.chessdatabase.dbs;

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
		String methodName = method.getName();
		if ( method.getName().equals( "setData" ) )
		{
			if ( contains( method.getParameterTypes(), "int" ) )
			{
				methodName = "setDataWithInt";
			}
			else
			{
				methodName = "setDataWithVmStelling";
			}
		}
		if ( method.getName().equals( "getData" ) )
		{
			if ( contains( method.getParameterTypes(), "int" ) )
			{
				methodName = "getDataWithInt";
			}
			else
			{
				methodName = "getDataWithVmStelling";
			}
		}
		methodLookup.put( methodName, method );
		method.setAccessible( true );
	}
}
private boolean contains( Class<?> [] aParameters, String aClassName )
{
	for ( Class<?> claxx : aParameters )
	{
		if ( claxx.getName().equals( aClassName ) )
		{
			return true;
		}
	}
	return false;
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
	catch ( NullPointerException e )
	{
		throw new RuntimeException( e );
	}
}
// @@HIGH test getStaticAantalStukken
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
int getPageSize()
{
	return (int) callMethod( "getPageSize" );
}
void initializeCache()
{
	callMethod( "initializeCache" );
}
int getFreeCacheEntry()
{
	return (int) callMethod( "getFreeCacheEntry" );
}
byte [] getPage( PageDescriptor aPageDescriptor )
{
	return (byte []) callMethod( "getPage", aPageDescriptor );
}
void setPage( PageDescriptor aPageDescriptor, byte [] aPage )
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
byte []  getPageFromDatabase( PageDescriptor aPageDescriptor )
{
	return (byte []) callMethod( "getPageFromDatabase", aPageDescriptor );
}
int getPositionWithinPage( VMStelling aVmStelling )
{
	return (int) callMethod( "getPositionWithinPage", aVmStelling );
}
byte getData( PageDescriptor aPageDescriptor, VMStelling aVmStelling )
{
	return (byte) callMethod( "getDataWithVmStelling", aPageDescriptor, aVmStelling );
}
byte getData( PageDescriptor aPageDescriptor, int aPositionWithPage )
{
	return (byte) callMethod( "getDataWithInt", aPageDescriptor, aPositionWithPage );
}
void setData( PageDescriptor aPageDescriptor, VMStelling aVmStelling, byte aData )
{
	callMethod( "setDataWithVmStelling", aPageDescriptor, aVmStelling, aData );
}
void setData( PageDescriptor aPageDescriptor, int aPositionWithinPage, byte aData )
{
	callMethod( "setDataWithInt", aPageDescriptor, aPositionWithinPage, aData );
}
void flush()
{
	callMethod( "flush" );
}

}
