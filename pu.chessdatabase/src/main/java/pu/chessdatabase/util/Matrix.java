package pu.chessdatabase.util;

import lombok.ToString;

@ToString
public class Matrix
{
public final Vector [] elements;

public Matrix( Vector [] aVector )
{
	super();
	elements = aVector;
}
public Vector multiply( Vector aVector )
{
	return new Vector( 
		getElement( 0, 0 ) * aVector.elements[0] + getElement( 0, 1) * aVector.elements[1],
		getElement( 1, 0 ) * aVector.elements[0] + getElement( 1, 1) * aVector.elements[1]
	);
}
public int getElement( int aRow, int aColumn )
{
	return elements[aRow].elements[aColumn];
}
//@Override
//public String toString()
//{
//	return 7
//}

}
