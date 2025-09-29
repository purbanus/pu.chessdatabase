package pu.chessdatabase.bo.speel;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartijReport
{
private boolean erZijnZetten;
private int aantalZetten;
private VooruitRecord vooruit;
@Builder.Default
private List<String> PartijZetten = new ArrayList<>(); // Liep in Modula-2 van 1 tot 130!
}
