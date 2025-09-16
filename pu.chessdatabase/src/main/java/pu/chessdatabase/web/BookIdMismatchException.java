package pu.chessdatabase.web;

public class BookIdMismatchException extends RuntimeException
{
public BookIdMismatchException()
{
	super( "Book id mismatch" );
}
public BookIdMismatchException( String aMessage )
{
	super( aMessage );
}
public BookIdMismatchException( String aMessage, Throwable aCause )
{
	super( aMessage, aCause );
}
}
