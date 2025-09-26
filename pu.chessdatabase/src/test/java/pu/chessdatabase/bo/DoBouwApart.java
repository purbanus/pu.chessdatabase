package pu.chessdatabase.bo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DoBouwApart
{
@Autowired private Bouw bouw;
@Test
public void buildDeDatabase()
{
	bouw.bouwDatabase();
}
}
