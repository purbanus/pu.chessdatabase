package pu.chessdatabase.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping( "/session" )
public class SessionController
{

// Create a session and store an attribute
@GetMapping( "/create" )
public String createSession( HttpSession session )
{
	// Set a session attribute (e.g., username)
	session.setAttribute( "username", "purbanus" );

	// Retrieve and return the session ID
	String sessionId = session.getId();
	return "Session created with ID: " + sessionId;
}

// Retrieve session attribute
@GetMapping( "/get" )
public String getSession( HttpSession session )
{
	// Get the session attribute (username)
	String username = (String) session.getAttribute( "username" );

	// If no session exists, return an error message
	if ( username == null )
	{
		return "No session found!";
	}

	// Return session data to the client
	return "Session found with username: " + username;
}

// Invalidate the session
@GetMapping( "/invalidate" )
public String invalidateSession( HttpSession session )
{
	// Invalidate the session
	session.invalidate();
	return "Session invalidated!";
}
}