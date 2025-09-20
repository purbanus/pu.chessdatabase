package pu.chessdatabase.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class Stuk
{
private int [] Richting;
private int AtlRicht;
private boolean Meer;
private StukType Soort;
private boolean Kleur;
private int Knummer;
private char StukAfk;
}
