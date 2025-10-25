package pu.chessdatabase.service;

import java.util.List;

import pu.chessdatabase.bo.speel.GegenereerdeZetDocument;
import pu.chessdatabase.bo.speel.ResultaatRecord;
import pu.chessdatabase.bo.speel.ZetDocument;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PartijDocument
{
private final int wk;
private final int zk;
private final int s3;
private final int s4;
private final String aanZet;
private final String wkStuk;
private final String zkStuk;
private final String s3Stuk;
private final String s4Stuk;
//private final BoStelling stelling;
private final ResultaatRecord resultaat;
private final List<ZetDocument> zetten;
private final List<GegenereerdeZetDocument> gegenereerdeZetten; 
private final boolean naarBeginMag;
private final boolean terugMag;
private final boolean vooruitMag;
private final boolean naarEindeMag;
}
