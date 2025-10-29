package pu.chessdatabase.bewaard;

import pu.chessdatabase.bo.Kleur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoStellingKey4Stukken extends BoStellingKey
{
private int wk;
private int zk;
private int s3;
private int s4;
private Kleur aanZet;
}
