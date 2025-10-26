package pu.chessdatabase.web;

import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.service.BoStellingKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SwitchConfigResponse
{
private String config;
}
