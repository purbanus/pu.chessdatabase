package pu.chessdatabase.service;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewGameDocument
{
@Value
@Builder
public static class Stuk
{
	private String name;
	private String label;
	private String veld;
}
private final List<String> configList;
private final String config;
private final List<Stuk> stukken;
//private final String wk;
//private final String zk;
//private final String s3;
//private final String s4;
private final String aanZet;
}
