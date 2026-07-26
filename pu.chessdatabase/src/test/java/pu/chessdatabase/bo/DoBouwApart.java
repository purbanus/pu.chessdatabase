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
	config.switchConfig( Config.KDK );
	bouw.bouwDatabase();
}
//@Test
public void buildKTK()
{
	config.switchConfig( Config.KTK );
	bouw.bouwDatabase();
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 4 stukken

//@Test
public void buildKDKT()
{	config.switchConfig( Config.KDKT );
	bouw.bouwDatabase();
}
//@Test
public void buildKLLK()
{
	config.switchConfig( Config.KLLK );
	bouw.bouwDatabase();
}
//@Test
public void buildKLPK()
{
	config.switchConfig( Config.KLPK );
	bouw.bouwDatabase();
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 5 stukken

//@Test
// Als je deze activeert, zet dan HOU_STELLINGEN_BIJ op false, anders loop je uit het geheugen
public void buildKDKTT()
{
	config.switchConfig( Config.KDKTT );
	bouw.bouwDatabase();
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Bouw van de testdatabases

//@Test
public void buildTestKDK()
{
	config.switchConfig( Config.TESTKDK );
	bouw.bouwDatabase();
}
//@Test
public void buildTestKTK()
{
	config.switchConfig( Config.TESTKTK );
	bouw.bouwDatabase();
}
//@Test
public void buildTestKDKT()
{
	config.switchConfig( Config.TESTKDKT );
	bouw.bouwDatabase();
}
//@Test
public void buildTestKLLK()
{
	config.switchConfig( Config.TESTKLLK );
	bouw.bouwDatabase();
}
//@Test
public void buildTestKLPK()
{
	config.switchConfig( Config.TESTKLPK );
	bouw.bouwDatabase();
}
//@Test
public void buildTestKDKTT()
{
	config.switchConfig( Config.TESTKDKTT );
	bouw.bouwDatabase();
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Bouw van de pipodatabases

//@Test
public void buildPipoKDK()
{
	config.switchConfig( Config.PIPOKDK );
	bouw.bouwDatabase();
}
//@Test
public void buildPipoKTK()
{
	config.switchConfig( Config.PIPOKTK );
	bouw.bouwDatabase();
}
//@Test
public void buildPipoKDKT()
{
	config.switchConfig( Config.PIPOKDKT );
	bouw.bouwDatabase();
}
//@Test
public void buildPipoKLLK()
{
	config.switchConfig( Config.PIPOKLLK );
	bouw.bouwDatabase();
}
//@Test
public void buildPipoKLPK()
{
	config.switchConfig( Config.PIPOKLPK );
	bouw.bouwDatabase();
}
//@Test
public void buildPipoKDKTT()
{
	config.switchConfig( Config.PIPOKDKTT );
	bouw.bouwDatabase();
}
}
