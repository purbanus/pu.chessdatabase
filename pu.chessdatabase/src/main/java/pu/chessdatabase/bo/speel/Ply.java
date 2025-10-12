package pu.chessdatabase.bo.speel;

import pu.chessdatabase.bo.BoStelling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
/**
 * Als we 'van' en 'naar' hebben, dan bevat een PlyRecord
 * - De BoStelling 'van'
 * - Of de BoStelling ten einde is
 * - Het zetnummer van de opky (een zet is twee ply)
 * - De VanNaar( 'van', 'naar' ) d.w.z. de VanNaar die gespeeld is vanuit de boStelling
 */
public class PlyRecord
{
public static PlyRecord getNullPlyRecord()
{
	PlyRecord plyRecord = PlyRecord.builder()
		.boStelling( BoStelling.NULL_STELLING )
		.einde( EindeType.NOG_NIET )
		.zetNr( Partij.MAX_HELE_ZET_NUMMER )
		.vanNaar( VanNaar.ILLEGAL_VAN_NAAR )
		.build();
	return plyRecord;
}
private BoStelling boStelling;
private EindeType einde;
private int zetNr; // Liep in Modula van 1 tot 130!
private VanNaar vanNaar;
// Dit moet je niet doen, het is het schaak in de VORIGE ply dat geldt
//public boolean isSchaak()
//{
//	return getBoStelling().isSchaak();
//}
private boolean schaak;
}
