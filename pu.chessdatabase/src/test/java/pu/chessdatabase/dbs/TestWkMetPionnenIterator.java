package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import static pu.chessdatabase.dbs.Constants.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;

import lombok.Data;

@SpringBootTest
@Data
public class TestWkMetPionnenIterator
{
@Autowired private Config config;
private WkMetPionnenIterator iterator = new WkMetPionnenIterator();

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( Config.PipoKDKT );
}
@AfterEach
public void destroy()
{
	config.switchConfig( savedConfigString );
}
@Test
public void testContains()
{
	assertTrue( iterator.contains( 0 ) );
	assertTrue( iterator.contains( 1 ) );
	assertTrue( iterator.contains( 2 ) );
	assertTrue( iterator.contains( 3 ) );
	assertFalse( iterator.contains( 4 ) );
	assertFalse( iterator.contains( 5 ) );
	assertFalse( iterator.contains( 6 ) );
	assertFalse( iterator.contains( 7 ) );
	assertTrue( iterator.contains( 0x30 ) );
	assertTrue( iterator.contains( 0x31 ) );
	assertTrue( iterator.contains( 0x32 ) );
	assertTrue( iterator.contains( 0x33 ) );
	assertFalse( iterator.contains( 0x34 ) );
	assertFalse( iterator.contains( 0x35 ) );
	assertFalse( iterator.contains( 0x36 ) );
	assertFalse( iterator.contains( 0x37 ) );
}
@Test
public void testWkMetPionnenIterator()
{
	int [] values = new int[32];
	int index = 0;
	for ( int wk : WK_MET_PIONNEN_ITERATOR )
	{
		values[index++] = wk;
	}
	assertArrayEquals( values, WK_MET_PIONNEN_ITERATOR.getWks() );

	// Kun je die static iterator meerdere keren gebruiken? Ja hoor!
	values = new int[32];
	index = 0;
	for ( int wk : WK_MET_PIONNEN_ITERATOR )
	{
		values[index++] = wk;
	}
	assertArrayEquals( values, WK_MET_PIONNEN_ITERATOR.getWks() );
}

}
