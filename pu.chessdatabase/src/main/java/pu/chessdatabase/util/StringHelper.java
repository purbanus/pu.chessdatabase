package pu.chessdatabase.util;

public class StringHelper
{
private StringHelper()
{
}
/**
 * Removes white space from end this string.
 * <p>
 * All characters that have codes less than or equal to
 * <code>'&#92;u0020'</code> (the space character) are considered to be
 * white space.
 * Apache commons lang3 StringUtils heeft trim() maar dat haalt whitespace aan beide zijden van een string weg, 
 * en dat willen we niet.
 * @return  this string, with trailing white space removed
 */
public static String trimTrailing(String s)
{
	if ( s == null )
	{
		return null;
	}
	int len = s.length();
	int origLen = len;
	while ( ( len > 0) && (s.charAt(len - 1) <= ' ') && s.charAt( len - 1 ) != '\u001A' )
	{
		len--;
	}
	return(len != origLen) ? s.substring(0, len) : s;
}

}
