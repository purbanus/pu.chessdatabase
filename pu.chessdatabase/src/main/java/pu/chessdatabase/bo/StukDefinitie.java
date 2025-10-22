package pu.chessdatabase.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode

public class StukDefinitie
{
private StukType stukType;
private Kleur kleur;
}
