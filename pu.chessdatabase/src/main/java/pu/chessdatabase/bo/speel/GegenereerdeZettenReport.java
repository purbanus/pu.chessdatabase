package pu.chessdatabase.bo.speel;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
public class GegenereerdeZettenReport
{
private int aantalZetten;
@Builder.Default
private List<String> gegenereerdeZetten = new ArrayList<>(); // Liep in Modula-2 van 1 tot 130!
}
