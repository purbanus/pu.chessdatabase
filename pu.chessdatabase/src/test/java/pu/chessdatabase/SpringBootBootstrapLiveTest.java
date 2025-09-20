package pu.chessdatabase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import pu.chessdatabase.bo.Book;
import pu.chessdatabase.util.RandomAlphabetic;
import pu.chessdatabase.util.RandomNumeric;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class SpringBootBootstrapLiveTest
{

@LocalServerPort
private int port = 8082;

private String API_ROOT;

private RandomAlphabetic randomAlphabetic = new RandomAlphabetic();

private RandomNumeric randomNumeric = new RandomNumeric();

@BeforeEach
public void setUp()
{
	API_ROOT = "http://localhost:" + port + "/api/books";
	RestAssured.port = port;
}

public String randomAlphabetic( int aSize )
{
	return randomAlphabetic.randomAlphabetic( aSize );
}
public int randomNumeric( int aSize )
{
	return randomNumeric.randomNumeric( aSize );
}
private Book createRandomBook()
{
	final Book book = new Book();
	book.setTitle( randomAlphabetic( 10 ) );
	book.setAuthor( randomAlphabetic( 15 ) );
	return book;
}

private String createBookAsUri( Book book )
{
	final Response response = RestAssured.given()
	    .contentType( MediaType.APPLICATION_JSON_VALUE )
	    .body( book )
	    .post( API_ROOT );
	return API_ROOT + "/" + response.jsonPath()
	    .get( "id" );
}

// Get all books

@Test
public void whenGetAllBooks_thenOK()
{
	Response response = RestAssured.get( API_ROOT );

	assertEquals( HttpStatus.OK.value(), response.getStatusCode() );
}

// Get book by title

@Test
public void whenGetBooksByTitle_thenOK()
{
	Book book = createRandomBook();
	createBookAsUri( book );
	Response response = RestAssured.get( API_ROOT + "/title/" + book.getTitle() );

	assertEquals( HttpStatus.OK.value(), response.getStatusCode() );
	assertTrue( response.as( List.class )
	    .size() > 0 );
}

// Get book by id, hoewel, title?

@Test
public void whenGetCreatedBookById_thenOK()
{
	Book book = createRandomBook();
	String location = createBookAsUri( book );
	Response response = RestAssured.get( location );

	assertEquals( HttpStatus.OK.value(), response.getStatusCode() );
	assertEquals( book.getTitle(), response.jsonPath()
	    .get( "title" ) );
}

// Get non-existing book

@Test
public void whenGetNotExistBookById_thenNotFound()
{
	Response response = RestAssured.get( API_ROOT + "/" + randomNumeric( 4 ) );

	assertEquals( HttpStatus.NOT_FOUND.value(), response.getStatusCode() );
}

// Next, we’ll test creating a new book:

@Test
public void whenCreateNewBook_thenCreated()
{
	Book book = createRandomBook();
	Response response = RestAssured.given()
	    .contentType( MediaType.APPLICATION_JSON_VALUE )
	    .body( book )
	    .post( API_ROOT );

	assertEquals( HttpStatus.CREATED.value(), response.getStatusCode() );
}

@Test
public void whenInvalidBook_thenError()
{
	Book book = createRandomBook();
	book.setAuthor( null );
	Response response = RestAssured.given()
	    .contentType( MediaType.APPLICATION_JSON_VALUE )
	    .body( book )
	    .post( API_ROOT );

	assertEquals( HttpStatus.BAD_REQUEST.value(), response.getStatusCode() );
}

// Then we’ll update an existing book:

@Test
public void whenUpdateCreatedBook_thenUpdated()
{
	Book book = createRandomBook();
	String location = createBookAsUri( book );
	book.setId( Long.parseLong( location.split( "api/books/" )[1] ) );
	book.setAuthor( "newAuthor" );
	Response response = RestAssured.given()
	    .contentType( MediaType.APPLICATION_JSON_VALUE )
	    .body( book )
	    .put( location );

	assertEquals( HttpStatus.OK.value(), response.getStatusCode() );

	response = RestAssured.get( location );

	assertEquals( HttpStatus.OK.value(), response.getStatusCode() );
	assertEquals( "newAuthor", response.jsonPath()
	    .get( "author" ) );
}

// And we can delete a book:

@Test
public void whenDeleteCreatedBook_thenOk()
{
	Book book = createRandomBook();
	String location = createBookAsUri( book );
	Response response = RestAssured.delete( location );

	assertEquals( HttpStatus.OK.value(), response.getStatusCode() );

	response = RestAssured.get( location );
	assertEquals( HttpStatus.NOT_FOUND.value(), response.getStatusCode() );
}

}