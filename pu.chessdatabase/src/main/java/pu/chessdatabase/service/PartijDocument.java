package pu.chessdatabase.service;

import java.util.List;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.speel.GegenereerdeZetDocument;
import pu.chessdatabase.bo.speel.ResultaatRecord;
import pu.chessdatabase.bo.speel.ZetDocument;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PartijDocument
{
//private final String wk;
//private final String zk;
//private final String s3;
//private final String s4;
//private final String aanZet;
private final BoStelling stelling;
private final ResultaatRecord resultaat;
private final List<ZetDocument> zetten;
private final List<GegenereerdeZetDocument> gegenereerdeZetten;
}
