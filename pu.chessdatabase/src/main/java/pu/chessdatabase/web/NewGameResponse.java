package pu.chessdatabase.web;

import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.service.BoStellingKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewGameResponse
{
private String wkAlfa;
private String zkAlfa;
private String s3Alfa;
private String s4Alfa;
private String s5Alfa;
private String aanZet;
public BoStellingKey getBoStellingKey()
{
	return BoStellingKey.builder()
		.wk( Partij.alfaToVeld( wkAlfa ) )
		.zk( Partij.alfaToVeld( zkAlfa ) )
		.s3( Partij.alfaToVeld( s3Alfa ) )
		.s4( Partij.alfaToVeld( s4Alfa ) )
		.s5( Partij.alfaToVeld( s5Alfa ) )
		.aanZet( Kleur.valueOf( aanZet.toUpperCase() ) )
		.build();
}
}
