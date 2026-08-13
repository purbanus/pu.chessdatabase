package pu.chessdatabase.dbs;

import java.util.Iterator;

import lombok.Data;

@Data
public class WkMetPionnenIterator implements Iterable<Integer>
{
private int [] wks = { 
	 0,  1,  2,  3, 
	 8,  9, 10, 11, 
	16, 17, 18, 19, 
	24, 25, 26, 27, 
	32, 33, 34, 35, 
	40, 41, 42, 43, 
	48, 49, 50, 51, 
	56, 57, 58, 59
};
public boolean contains( int aWk )
{
	for ( int wk : this )
	{
		if ( wk == aWk )
		{
			return true;
		}
	}
	return false;
}
@Override
public Iterator<Integer> iterator()
{
	Iterator<Integer> it = new Iterator<>()
	{
		private int index = 0;
		@Override
		public boolean hasNext()
		{
			return index < wks.length;
		}
		@Override
		public Integer next()
		{
			return wks[index++];
		}
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	};
	return it;
}

}
