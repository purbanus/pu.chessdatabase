package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.CacheType.*;

import java.io.RandomAccessFile;

import pu.chessdatabase.bo.Config;

public interface Cache
{
public abstract RandomAccessFile getDatabase();
public abstract void setDatabase( RandomAccessFile aRandomAccessFile );
public abstract int getPageSize();
public abstract long getDatabaseSize();
public abstract byte [] getPage( PageDescriptor aPageDescriptor );
public abstract byte [] getPageFromDatabase( PageDescriptor aPageDescriptor );
public abstract int getPositionWithinPage( VMStelling aVmStelling );
public abstract void setVuil( PageDescriptor aPageDescriptor, boolean aVuil );
public abstract byte getData( PageDescriptor aPageDescriptor, VMStelling aVmStelling );
public abstract void setData( PageDescriptor aPageDescriptor, VMStelling aVmStelling, byte aData );
public abstract void pageOut( PageDescriptor aPageDescriptor );
public abstract CacheEntry getCacheEntry( PageDescriptor aPageDescriptor );
// Alleen om te testen!!
public abstract void setCacheEntry( PageDescriptor aPageDescriptor, CacheEntry aCacheEntry );
public abstract void flush();

public static Cache create( Config aConfig, RandomAccessFile aDatabase )
{
	if ( aDatabase == null )
	{
		throw new RuntimeException( "Database mag niet null zijn" );
	}
	if ( aConfig.getPageSizeCalculator().getCacheType() == Serial )
	{
		return new SerialCache( aConfig, aDatabase );
	}
	else
	{
		return new ParallelCache( aConfig, aDatabase );
	}
}

}
