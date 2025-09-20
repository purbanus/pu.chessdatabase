package pu.chessdatabase.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class GenZ
{
private int Aantal;
private BoStelling stelling; // m.z. array of stelling?? 
}
