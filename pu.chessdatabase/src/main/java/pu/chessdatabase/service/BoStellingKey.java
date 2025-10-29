package pu.chessdatabase.service;

import pu.chessdatabase.bo.Kleur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BoStellingKey
{
private int wk;
private int zk;
private int s3;
private int s4;
private int s5;
private Kleur aanZet;
}
