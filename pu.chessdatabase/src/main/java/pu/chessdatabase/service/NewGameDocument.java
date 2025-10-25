package pu.chessdatabase.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewGameDocument
{
private final String wk;
private final String zk;
private final String s3;
private final String s4;
private final String aanZet;
private final String wkLabel;
private final String zkLabel;
private final String s3Label;
private final String s4Label;
}
