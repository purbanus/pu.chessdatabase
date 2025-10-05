package pu.chessdatabase.web;

import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.service.BoStellingKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class NewGameResponse
{
private String wkAlfa;
private String zkAlfa;
private String s3Alfa;
private String s4Alfa;
private String aanZet;
public BoStellingKey getBoStellingKey()
{
	return BoStellingKey.builder()
		.wk( Partij.alfaToVeld( wkAlfa ) )
		.zk( Partij.alfaToVeld( zkAlfa ) )
		.s3( Partij.alfaToVeld( s3Alfa ) )
		.s4( Partij.alfaToVeld( s4Alfa ) )
		.aanZet( Kleur.fromString( aanZet ) )
		.build();
}
}
