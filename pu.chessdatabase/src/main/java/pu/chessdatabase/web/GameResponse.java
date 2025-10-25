package pu.chessdatabase.web;

import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.service.BoStellingKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameResponse
{
private int wk;
private int zk;
private int s3;
private int s4;
private String aanZet;
public BoStellingKey getBoStellingKey()
{
	return BoStellingKey.builder()
		.wk( Partij.hexGetalToVeld( wk ) ) 
		.zk( Partij.hexGetalToVeld( zk ) ) 
		.s3( Partij.hexGetalToVeld( s3 ) ) 
		.s4( Partij.hexGetalToVeld( s4 ) ) 
		.aanZet( Kleur.valueOf( aanZet.toUpperCase() ) )
		.build();
}
}
