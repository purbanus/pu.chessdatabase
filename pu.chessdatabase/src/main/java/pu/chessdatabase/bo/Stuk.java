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
private StukType Soort;
private Kleur Kleur;
private int Knummer;
private int AtlRicht;
private boolean Meer;
private int [] Richting;
public String getAfko()
{
	return Soort.getAfko();
}
}
