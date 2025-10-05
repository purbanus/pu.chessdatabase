package pu.chessdatabase.service;

import pu.chessdatabase.bo.Kleur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
public class BoStellingKey
{
private int wk;
private int zk;
private int s3;
private int s4;
private Kleur aanZet;
}
