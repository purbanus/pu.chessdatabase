package pu.chessdatabase.bo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.Dbs;

@SuppressWarnings( "unused" )
@SpringBootTest
public class DoBouwApart
{
@Autowired private Bouw bouw;
@Autowired private Dbs dbs;
//@Test
public void buildKDKT2()
{
	dbs.setDatabaseName( "KDKT2.DBS" );
	bouw.bouwDatabase();
}
}
