package pu.chessdatabase.dal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page
{
byte [] data = new byte [Cache.PAGE_SIZE];
public void clearPage()
{
//	for ( byte b : page )
//	{
//		b = 0;
//	}
	for ( int x = 0; x < data.length; x++ )
	{
		data[x] = (byte)0;
	}
}
}
