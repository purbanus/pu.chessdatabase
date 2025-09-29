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
private StukType soort;
private Kleur kleur;
private int koningsNummer;
private int aantalRichtingen;
private boolean meer;
private int [] richtingen;
public String getAfko()
{
	return soort.getAfko();
}
}
