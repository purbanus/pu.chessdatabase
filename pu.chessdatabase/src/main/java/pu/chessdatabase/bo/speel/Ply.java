package pu.chessdatabase.bo.speel;

import pu.chessdatabase.bo.BoStelling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * Als we 'van' en 'naar' hebben, dan bevat een Ply
 * - De BoStelling 'van'
 * - Of de BoStelling ten einde is
 * - Het zetnummer van de opky (een zet is twee ply)
 * - De VanNaar( 'van', 'naar' ) d.w.z. de VanNaar die gespeeld is vanuit de boStelling
 */
public class Ply
{
public static final Ply NULL_PLY = Ply.builder()
	.boStelling( BoStelling.NULL_STELLING )
	.einde( EindeType.NOG_NIET )
	.zetNummer( Partij.MAX_HELE_ZET_NUMMER )
	.vanNaar( VanNaar.ILLEGAL_VAN_NAAR )
	.build();

private BoStelling boStelling;
private EindeType einde;
private int zetNummer; // Liep in Modula van 1 tot 130!
private VanNaar vanNaar;
// Dit moet je niet doen, het is het schaak in de VORIGE ply dat geldt
//public boolean isSchaak()
//{
//	return getBoStelling().isSchaak();
//}
private boolean schaak;
}
