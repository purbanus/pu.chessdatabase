package pu.chessdatabase.dal;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import lombok.Value;

@Value
public class FlatDocument
{
private final int pliesId;
private final String configString;
private final String userName;
// Geen LocalDateTime gebruiken: geeft een ClassCastException
// zelfs met mijn eigen Converter
private final Timestamp started;
private final int currentPlyNumber;
private final boolean begonnen;
private final int plyId;
private final String einde;
private final int zetNummer;
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
