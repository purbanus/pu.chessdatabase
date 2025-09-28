package pu.chessdatabase.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class StukInfo
{
private int Veld;
private int X;
private int Y;
private Kleur Kleur;
private char StukAfk;
}
