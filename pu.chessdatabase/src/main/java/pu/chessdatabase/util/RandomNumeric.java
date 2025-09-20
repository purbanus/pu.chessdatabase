package pu.chessdatabase.util;

import java.util.Random;

public class RandomNumeric
{
Random random = new Random();

void setRandom( Random aRandom )
{
	random = aRandom;
}
public int randomNumeric( int aSize )
{
    int leftLimit = '0';
    int rightLimit = '9';

    @SuppressWarnings( "null" )
	String generatedString = random.ints(leftLimit, rightLimit + 1)
      .limit( aSize )
      .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
      .toString();
    
    return Integer.parseInt( generatedString );
}
}
