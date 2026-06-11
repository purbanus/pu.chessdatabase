package pu.chessdatabase.bo.speel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultaatRecord
{
private String resultaat; // Mat, Pat, Gewonnen, Verloren, Illegaal
private String matIn;
}
