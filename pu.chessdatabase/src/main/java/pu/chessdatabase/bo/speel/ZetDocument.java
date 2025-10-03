package pu.chessdatabase.bo.speel;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@ToString
@Builder
public class ZetDocument
{
private int zetNummer;
private String witZet;
private String zwartZet;
}
