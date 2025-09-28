package pu.chessdatabase.bo.speel;

import org.eclipse.jdt.annotation.NonNull;

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
	.vanNaar( VanNaar.ILLEGAL_VAN_NAAR )
	.build();
private BoStelling boStelling;
private EindeType Einde;
private int ZetNr; // Liep in Modula van 1 tot 130!
private VanNaar vanNaar;
public boolean isSchaak()
{
	return getBoStelling().isSchaak();
}

}
