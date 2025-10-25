package pu.chessdatabase.bo.speel;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartijReport
{
private boolean erZijnZetten;
private VooruitRecord vooruit;
@Builder.Default
private List<ZetDocument> zetten = new ArrayList<>(); // Liep in Modula-2 van 1 tot 130!
}
