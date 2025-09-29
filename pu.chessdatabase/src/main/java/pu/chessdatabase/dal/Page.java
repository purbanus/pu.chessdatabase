package pu.chessdatabase.dal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page
{
byte [] page = new byte [VM.PAGE_SIZE];
public void clearPage()
{
//	for ( byte b : page )
//	{
//		b = 0;
//	}
	for ( int x = 0; x < page.length; x++ )
	{
		page[x] = (byte)0;
	}
}
}
