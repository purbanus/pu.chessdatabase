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
public class PlyRecord
{
public static final PlyRecord NULL_PLY_RECORD = PlyRecord.builder()
	.boStelling( BoStelling.NULL_STELLING )
	.Einde( EindeType.NogNiet )
	.ZetNr( Partij.MAX_HELE_ZET_NUMMER )
	.vanNaar( new VanNaar( 0x0f, 0x0f ) )
	.Schaak( false )
	.build();
private BoStelling boStelling;
private EindeType Einde;
private int ZetNr; // Liep in Modula van 1 tot 130!
private VanNaar vanNaar;
private boolean Schaak;

}
