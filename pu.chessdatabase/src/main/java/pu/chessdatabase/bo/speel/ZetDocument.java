package pu.chessdatabase.bo.speel;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ZetDocument
{
private int zetNummer;
private String witZet;
private String zwartZet;
}
