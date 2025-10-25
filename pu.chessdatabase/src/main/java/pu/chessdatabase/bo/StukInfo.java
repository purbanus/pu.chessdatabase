package pu.chessdatabase.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StukInfo
{
private Stuk stuk;
private int veld;
private int x;
private int y;
public Kleur getKleur()
{
	return getStuk().getKleur();
}
public String getAfko()
{
	return getStuk().getAfko();
}
}
