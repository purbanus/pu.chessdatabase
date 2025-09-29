package pu.chessdatabase.bo.speel;

import org.eclipse.jdt.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class VanNaar
{
public static final @NonNull VanNaar ILLEGAL_VAN_NAAR = new VanNaar( 0x0f, 0x0f );
private int van;
private int naar;
}
