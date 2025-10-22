package pu.chessdatabase.bo;

import static pu.chessdatabase.bo.Kleur.*;

import java.util.List;

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
private int stukNummer;
private StukType stukType;
private Kleur kleur;
public String getAfko()
{
	return stukType.getAfko();
}
public int getKoningsNummer()
{
	return kleur == WIT ? 0 : 1;
}
public boolean isMeer()
{
	return stukType.isMeer();
}
public List<Integer> getRichtingen()
{
	return stukType.getRichtingen();
}

}
