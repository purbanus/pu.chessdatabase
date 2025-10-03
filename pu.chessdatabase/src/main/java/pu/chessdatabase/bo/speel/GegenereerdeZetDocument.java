package pu.chessdatabase.bo.speel;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@ToString
@Builder
public class GegenereerdeZetDocument
{
private int zetNummer;
private String zet;
private String resultaat;
private String matInHoeveel;
}
