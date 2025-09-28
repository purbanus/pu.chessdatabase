package pu.chessdatabase.bo.speel;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.AlgDef;
import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.dal.Dbs;

@SpringBootTest
public class TestPartij
{
@Autowired private Partij partij;
@Autowired private Dbs dbs;

@BeforeEach
public void setup()
{
	dbs.Name( dbs.DFT_DBS_NAAM );
	dbs.Open();
}
@AfterEach
public void destroy()
{
	dbs.Close();
}

@Test
public void testInzPartij()
{
	partij.inzPartij();
	// Dit werkt niet: om een of and're reden vindt-ie dat isNotNull null is
	//assertThat( partij.curPartij, isNotNull() );
	assertThat( partij.curPartij, is( notNullValue() ) );
	assertNotNull( partij.curPartij );
	for ( PlyRecord plyRecord : partij.plies )
	{
		assertThat( plyRecord, is( PlyRecord.NULL_PLY_RECORD ) );
	}
}
@Test
public void testIsLegaleStelling()
{
	BoStelling boStelling = BoStelling.builder()
		.wk( 0 )
		.zk( 0 )
		.s3( 0 )
		.s4( 0 )
		.aanZet( AlgDef.Wit )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.aanZet( AlgDef.Wit )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
	boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x77 )
		.s3( 0x11 )
		.s4( 0x66 )
		.aanZet( AlgDef.Wit )
		.build();
	boStelling = BoStelling.builder()
		.wk( 5 )
		.zk( 6 )
		.s3( 7 )
		.s4( 8 )
		.aanZet( AlgDef.Wit )
		.build();
	assertThat( partij.isLegaleStelling( boStelling ), is( false ) );
}
}
