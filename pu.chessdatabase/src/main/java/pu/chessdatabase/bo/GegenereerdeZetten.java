package pu.chessdatabase.bo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GegenereerdeZetten
{
@Builder.Default
private List<BoStelling> stellingen = new ArrayList<>(); // Dit was een StellingPtr, met als commentaar m.z. array of stelling *)
public int getAantal()
{
	return stellingen.size();
}
public void add( BoStelling aBoStelling )
{
	stellingen.add( aBoStelling );
}
public void addAll( GegenereerdeZetten aGenZRec )
{
	stellingen.addAll( aGenZRec.getStellingen() );
}
public void reverse()
{
	stellingen = stellingen.reversed();
}
}
