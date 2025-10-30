package pu.chessdatabase.bo.configuraties;

import pu.chessdatabase.bo.Kleur;

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
