package pu.chessdatabase.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
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
