package pu.chessdatabase.bo;

import pu.chessdatabase.dal.ResultaatType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BoStelling
{
/**
 * CASE : BOOLEAN OF
        |	TRUE : WK, ZK, s3, s4: Veld;
        |	FALSE: Velden        : VeldArr;
 */
private final boolean StellingType = true;
private int WK;
private int ZK;
private int S3;
private int S4;
private boolean AanZet;
private ResultaatType Resultaat;
private int AantalZetten; // Was SHORTCARD, een 8 bits unsigned int
private boolean Schaak;

}
