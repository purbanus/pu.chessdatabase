package pu.chessdatabase.bo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dbs.Dbs;

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
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 3 stukken

//@Test
public void buildKDK()
{
	config.switchConfig( Config.KDK, false );
	bouw.bouwDatabase();
}
@Test
public void buildKTK()
{
	config.switchConfig( Config.KTK, false );
	bouw.bouwDatabase();
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 4 stukken

//@Test
public void buildKDKT()
{	config.switchConfig( Config.KDKT, false );
	bouw.bouwDatabase();
}
//@Test
public void buildKLPK()
{
	config.switchConfig( Config.KLPK, false );
	bouw.bouwDatabase();
}
//@Test
public void buildKLLK()
{
	config.switchConfig( Config.KLLK, false );
	bouw.bouwDatabase();
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 5 stukken

//@Test
// Als je deze activeert, zet dan HOU_STELLINGEN_BIJ op false, anders loop je uit het geheugen
public void buildKDKTT()
{
	config.switchConfig( Config.KDKTT, false );
	bouw.bouwDatabase();
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Bouw van de testdatabases

@Test
public void buildTestKDK()
{
	config.switchConfig( Config.TESTKDK, false );
	bouw.bouwDatabase();
}
@Test
public void buildTestKTK()
{
	config.switchConfig( Config.TESTKTK, false );
	bouw.bouwDatabase();
}
//@Test
public void buildTestKDKT()
{
	config.switchConfig( Config.TESTKDKT, false );
	bouw.bouwDatabase();
}
//@Test
public void buildTestKDKTT()
{
	config.switchConfig( Config.TESTKDKTT, false );
	bouw.bouwDatabase();
}
}
