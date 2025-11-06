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
private final List<Stuk> realStukken;
private final List<Stuk> fakeStukken;
private final String aanZet;
}
