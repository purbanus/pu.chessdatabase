package pu.chessdatabase.dbs;

import pu.chessdatabase.dbs.CacheType.*;

import java.io.RandomAccessFile;

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

public static Cache create( PageSizeCalculator aPageSizeCalculator, int aAantalStukken, RandomAccessFile aDatabase )
{
	if ( aPageSizeCalculator.getCacheType() == CacheType.Serial )
	{
		return new SerialCache( aPageSizeCalculator, aAantalStukken, aDatabase );
	}
	else
	{
		return new ParallelCache( aPageSizeCalculator, aAantalStukken, aDatabase );
	}
}

}
