package pu.chessdatabase.dbs;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
public class CacheEntry
{
private PageDescriptor pageDescriptor;
@ToString.Exclude
private byte [] page;
private boolean vuil;
private long generatie;

public void clearPage()
{
	for ( int x = 0; x < page.length; x++ )
	{
		page[x] = (byte)0;
	}
}
}
