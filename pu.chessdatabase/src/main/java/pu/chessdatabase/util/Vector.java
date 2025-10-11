package pu.chessdatabase.util;

import lombok.Data;

@Data
public class Vector
{
final int[] elements;

private Vector( int [] aElements )
{
	super();
	elements = aElements;
}
public Vector( int aElement1, int aElement2 )
{
	this( new int [] { aElement1, aElement2 } );
}
public int get( int aIndex )
{
	return elements[aIndex];
}
public Vector add( Vector aVector )
{
	return new Vector( get( 0 ) + aVector.get( 0 ), get( 1 ) + aVector.get( 1 ) );
}
@Override
public String toString()
{
	return "[" + get( 0 ) + " " + get( 1 ) + "]";
}

}