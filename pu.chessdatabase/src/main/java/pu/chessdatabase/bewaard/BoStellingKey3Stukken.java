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
public class BoStellingKey3Stukken extends BoStellingKey
{
private int wk;
private int zk;
private int s3;
private Kleur aanZet;
}
