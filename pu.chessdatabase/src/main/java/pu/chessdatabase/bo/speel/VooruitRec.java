package pu.chessdatabase.bo.speel;

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
public class VooruitRec
{
private boolean erIsVooruit;
private int start;
private boolean halverwege;
}
