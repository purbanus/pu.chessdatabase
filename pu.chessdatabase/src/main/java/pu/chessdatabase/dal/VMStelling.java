package pu.chessdatabase.dal;

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
public class VMStelling
{
private int WK;
private int ZK;
private int s3;
private int s4;
private boolean AanZet;

public void checkStelling()
{
	if ( WK > 9 || ZK > 63 || s3 > 63 || s4 > 63 )
	{
		throw new RuntimeException( "Dit is geen cardinaalstelling: " + this );
	}
	if ( WK < 0 || ZK < 0 || s3 < 0 || s4 < 0 )
	{
		throw new RuntimeException( "Dit is geen geldige stelling: " + this );
	}
}
public int getDbsAddress()
{
	return (getS3() << 6) + getS4();
}
}
