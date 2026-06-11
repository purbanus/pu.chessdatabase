package pu.chessdatabase.dal;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import lombok.Value;

@Value
public class FlatDocument
{
private final int pliesId;
private final String configString;
private final boolean pliesSchaak;
private final int pliesWk;
private final int pliesZk;
private final int pliesS3;
private final int pliesS4;
private final int pliesS5;
private final String pliesAanZet;
private final String pliesResultaat;
private final int pliesAantalZetten;
private final String userName;
// Geen LocalDateTime gebruiken: geeft een ClassCastException
// zelfs met mijn eigen Converter
private final Timestamp started;
private final int currentPlyNummer;
private final boolean begonnen;
private final int plyId;
private final String einde;
private final int plyNummer;
@Column( nullable = true )
private final Integer van;
@Column( nullable = true )
private final Integer naar;
private final boolean schaak;
private final int wk;
private final int zk;
private final int s3;
private final int s4;
private final int s5;
private final String aanZet;
private final String resultaat;
private final int aantalZetten;
}
