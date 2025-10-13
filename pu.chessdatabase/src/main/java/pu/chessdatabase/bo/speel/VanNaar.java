package pu.chessdatabase.bo.speel;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VanNaar
{
public static final @NonNull VanNaar ILLEGAL_VAN_NAAR = new VanNaar( 0x0f, 0x0f );
public static class AlfaBuilder
{
private VanNaar vanNaar = new VanNaar();
public AlfaBuilder van( String aVan ) { vanNaar.setVan( Partij.alfaToVeld( aVan ) ); return this; }
public AlfaBuilder naar( String aNaar ) { vanNaar.setNaar( Partij.alfaToVeld( aNaar ) ); return this; }
public VanNaar build()
{
	return vanNaar;
}
}
public static AlfaBuilder alfaBuilder()
{
	return new AlfaBuilder();
}
private int van;
private int naar;
public VanNaar( String aVan, String aNaar )
{
	super();
	van = Partij.alfaToVeld( aVan );
	naar = Partij.alfaToVeld( aNaar );
}
public VanNaar( String aVanNaar )
{
	super();
	String vanNaar = aVanNaar.trim().toLowerCase();
	char first = vanNaar.charAt( 0 );
	if ( first == 'k' || first == 'd' || first == 't' || first == 'l' || first == 'p' || first == 'o'  )
	{
		vanNaar = vanNaar.substring( 1 );
	}
	vanNaar = StringUtils.remove( vanNaar, ' ' );
	vanNaar = StringUtils.remove( vanNaar, '-' );
	vanNaar = StringUtils.remove( vanNaar, 'x' );
	vanNaar = StringUtils.remove( vanNaar, '+' );
	vanNaar = StringUtils.remove( vanNaar, '=' );
	vanNaar = StringUtils.remove( vanNaar, '#' );
	if ( vanNaar.length() != 4 )
	{
		throw new RuntimeException( "Ongeldige zet-notatie: " + aVanNaar );
	}
	van  = Partij.alfaToVeld( vanNaar.substring( 0, 2 ) );
	naar = Partij.alfaToVeld( vanNaar.substring( 2, 4 ) );
}

}
