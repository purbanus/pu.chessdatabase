package pu.chessdatabase.util;

import lombok.Data;

@Data
public class Matrix
{
/**
 * Dit zijn de twee rijvectoren
 */
private final Vector [] elements;

public Matrix( Vector [] aVector )
{
	super();
	elements = aVector;
}
public Vector multiply( Vector aVector )
{
	return new Vector( 
		getElement( 0, 0 ) * aVector.get( 0 ) + getElement( 0, 1) * aVector.get( 1 ),
		getElement( 1, 0 ) * aVector.get( 0 ) + getElement( 1, 1) * aVector.get( 1 )
	);
}
public Matrix multiply( Matrix aMatrix )
{
	return new Matrix(
		new Vector [] 
		{ 
			new Vector( 
				getElement( 0, 0 ) * aMatrix.getElement( 0, 0 ) + getElement( 0, 1 ) * aMatrix.getElement(  1, 0 ),
				getElement( 0, 0 ) * aMatrix.getElement( 0, 1 ) + getElement( 0, 1 ) * aMatrix.getElement(  1, 1 )
			),
			new Vector( 
				getElement( 1, 0 ) * aMatrix.getElement( 0, 0 ) + getElement( 1, 1 ) * aMatrix.getElement(  1, 0 ),
				getElement( 1, 0 ) * aMatrix.getElement( 0, 1 ) + getElement( 1, 1 ) * aMatrix.getElement(  1, 1 )
			),
		}
	);
}
public Vector getRowVector( int aRow )
{
	return getElements()[aRow];
}
public int getElement( int aRow, int aColumn )
{
	return getElements()[ aRow ].get( aColumn );
}
@Override
public String toString()
{
	return elements[0] + "\n" + elements[1];
//	return "[" + getElement( 0, 0 ) + " " + getElement( 0, 1 ) + "]\n" +
//		   "[" + getElement( 1, 0 ) + " " + getElement( 1, 1 ) + "]";
}

}
