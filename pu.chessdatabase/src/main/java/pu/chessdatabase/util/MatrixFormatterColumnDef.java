package pu.chessdatabase.util;

/**
 * Insert the type's description here.
 * Creation date: (17-11-2001 23:51:18)
 * @author: Administrator
 */
class MatrixFormatterColumnDef
{
	private int width;
	private MatrixFormatter.Alignment alignment;
	public final MatrixFormatter matrixFormatter;
	
public MatrixFormatterColumnDef( MatrixFormatter aMatrixFormatter )
{
	this( aMatrixFormatter, MatrixFormatter.WIDTH_UNKNOWN, MatrixFormatter.ALIGN_LEFT );
}
public MatrixFormatterColumnDef( MatrixFormatter aMatrixFormatter, int aWidth )
{
	this( aMatrixFormatter, aWidth, MatrixFormatter.ALIGN_LEFT );
}
public MatrixFormatterColumnDef( MatrixFormatter aMatrixFormatter, int aWidth, MatrixFormatter.Alignment aAlignment )
{
	super();
	matrixFormatter = aMatrixFormatter;
	width = aWidth;
	alignment = aAlignment;
}
public MatrixFormatterColumnDef( MatrixFormatter aMatrixFormatter, MatrixFormatter.Alignment aAlignment )
{
	this( aMatrixFormatter, MatrixFormatter.WIDTH_UNKNOWN, aAlignment );
}
public MatrixFormatter.Alignment getAlignment()
{
	return alignment;
}
public final MatrixFormatter getMatrixFormatter()
{
	return matrixFormatter;
}
public int getWidth()
{
	return width;
}
public void setAlignment( MatrixFormatter.Alignment aAlignment )
{
	alignment = aAlignment;
}
public void setWidth(int aWidth)
{
	width = aWidth;
}
}
