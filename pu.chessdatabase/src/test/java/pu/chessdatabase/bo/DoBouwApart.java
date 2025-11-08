package pu.chessdatabase.bo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
@Autowired private Config config;
String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
}
@AfterEach
public void destroy()
{
	config.switchConfig( savedConfigString );
}
//@Test
public void buildKDKT()
{
	config.switchConfig( "KDKT", false );
	bouw.bouwDatabase();
}
//@Test
public void buildKDKT2()
{
	config.switchConfig( "KDKT", false );
	dbs.setDatabaseName( "dbs/KDKT2.DBS" );
	bouw.bouwDatabase();
}
//@Test
public void buildKLPK()
{
	config.switchConfig( "KLPK", false );
	bouw.bouwDatabase();
}
//@Test
public void buildKLLK()
{
	config.switchConfig( "KLLK", false );
	bouw.bouwDatabase();
}
}
