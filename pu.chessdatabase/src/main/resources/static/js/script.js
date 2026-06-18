$(document).ready(function()
{
	const lookup = new Map();

	lookup.set( "Witte Koning", "♔");
	lookup.set( "Witte Dame", "♕");
	lookup.set( "Witte Toren", "♖");
	lookup.set( "Witte Loper", "♗");
	lookup.set( "Witte Paard", "♘");
	lookup.set( "Witte Pion", "♙");
	lookup.set( "Zwarte Koning", "♚");
	lookup.set( "Zwarte Dame", "♛");
	lookup.set( "Zwarte Loper", "♝");
	lookup.set( "Zwarte Toren", "♜");
	lookup.set( "Zwarte Paard", "♞");
	lookup.set( "Zwarte Pion", "♟");

	var s3Stuk = document.getElementById( "s3Stuk" ).getAttribute( "value" );
	var s3Symbol = lookup.get( s3Stuk );
	var s3 = document.getElementById( "s3" ).getAttribute( "value" );
	document.getElementById( s3 ).textContent = s3Symbol;

	var s4Stuk = document.getElementById( "s4Stuk" ).getAttribute( "value" );
	var s4Symbol = lookup.get( s4Stuk );
	var s4 = document.getElementById( "s4" ).getAttribute( "value" );
	document.getElementById( s4 ).textContent = s4Symbol;

	var s5Stuk = document.getElementById( "s5Stuk" ).getAttribute( "value" );
	var s5Symbol = lookup.get( s5Stuk );
	var s5 = document.getElementById( "s5" ).getAttribute( "value" );
	document.getElementById( s5 ).textContent = s5Symbol;

	var wkStuk = document.getElementById( "wkStuk" ).getAttribute( "value" );
	var wkSymbol = lookup.get( wkStuk );
	var wk = document.getElementById( "wk" ).getAttribute( "value" );
	document.getElementById( wk ).textContent = wkSymbol;
	
	var zkStuk = document.getElementById( "zkStuk" ).getAttribute( "value" );
	var zkSymbol = lookup.get( zkStuk );
	var zk = document.getElementById( "zk" ).getAttribute( "value" );
	document.getElementById( zk ).textContent = zkSymbol;
	
	
});