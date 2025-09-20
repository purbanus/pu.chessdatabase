package pu.chessdatabase.util;

import java.util.Random;

public class RandomAlphabetic
{
Random random = new Random();

void setRandom( Random aRandom )
{
	random = aRandom;
}

public String randomAlphabetic( int aSize )
{
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'

    @SuppressWarnings( "null" )
	String generatedString = random.ints(leftLimit, rightLimit + 1)
      .limit( aSize )
      .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
      .toString();

    return generatedString;
}
}
