package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Stuk
{
private String id;
private int stukNummer;
private StukType stukType;
private Kleur kleur;
public String getAfko()
{
	return getStukType().getAfko();
}
public int getKoningsNummer()
{
	return getKleur() == WIT ? 0 : 1;
}
public boolean isMeer()
{
	return getStukType().isMeer();
}
public List<Integer> getRichtingen()
{
	return getStukType().getRichtingen();
}
public String getStukString()
{
	return getKleur().getAfko() + getAfko();
}
public String getLabel()
{
	return getKleur().getLabel() + " " + getStukType().getLabel();
}
}
