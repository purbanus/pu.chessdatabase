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
public class GenZRec
{
@Builder.Default
private List<BoStelling> Sptr = new ArrayList<>(); // Dit was een StellingPtr, met als commentaar m.z. array of stelling *)
public int getAantal()
{
	return Sptr.size();
}
public void add( BoStelling aBoStelling )
{
	Sptr.add( aBoStelling );
}
public void addAll( GenZRec aGenZRec )
{
	Sptr.addAll( aGenZRec.getSptr() );
}
}
