package pu.chessdatabase.web;

public class BookNotFoundException extends RuntimeException
{
public BookNotFoundException()
{
	super( "Book not found" );
}
public BookNotFoundException( String aMessage )
{
	super( aMessage );
}
public BookNotFoundException( String aMessage, Throwable aCause )
{
	super( aMessage, aCause );
}
}
