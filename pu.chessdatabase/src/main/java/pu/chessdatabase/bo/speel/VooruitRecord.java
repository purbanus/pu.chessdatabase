package pu.chessdatabase.bo.speel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VooruitRecord
{
public static VooruitRecord getDefaultVooruitRecord()
{
	VooruitRecord vooruitRecord = VooruitRecord.builder()
		.erIsVooruit( false )
		.start(  0 )
		.halverwege( false )
		.build();
	return vooruitRecord;
}
/**
 * Of er vooruitgezet kan worden. Dat is meestal zo, behalve als de partij nog niet begonnen is.
 */
private boolean erIsVooruit;
/**
 * Het zetnummer waarvanaf er vooruitgespeeld kan worden
 * @@NOG Ik snap dit niet, het is altijd gelijk aan het laatste zetnummer.
 *       misschien moet ik eens testen met terugzetten en vooruitzetten
 */
private int start;
// Of de ply halverwege is, dwz eindigt met Wit aan zet
private boolean halverwege;
}
