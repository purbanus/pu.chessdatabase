package pu.chessdatabase.bo.speel;

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
public class PartijEntry
{
public PartijEntry()
{
	description = "";
	currentPly = 0;
	lastPly = 0;
	begonnen = false;
}
private String description;
private int currentPly;
private int lastPly;
private boolean begonnen;
}
