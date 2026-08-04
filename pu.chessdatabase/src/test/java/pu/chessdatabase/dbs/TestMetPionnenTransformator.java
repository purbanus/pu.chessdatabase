package pu.chessdatabase.dbs;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static pu.chessdatabase.bo.Kleur.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;

import lombok.Data;

@SpringBootTest
@Data
public class TestMetPionnenTransformator
{
public static final int [] NAAR_VM_STELLING = 
{
	0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
	0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
	0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
	0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,0x1f,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
	0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
	0x28,0x29,0x2a,0x2b,0x2c,0x2d,0x2e,0x2f,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
	0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
	0x38,0x39,0x3a,0x3b,0x3c,0x3d,0x3e,0x3f,
};

@Autowired private Config config;
private MetPionnenTransformator transformator = new MetPionnenTransformator();

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
public void testCreateNaarVmTabel()
{
//	for ( int x = 0; x < transformator.getNaarVmTabel().length; x++ )
//	{
//		//System.out.printf( "actual=%d expected=%d %s\n", transformator.getNaarVmTabel()[x], transformator.NAAR_VM_STELLING[x], transformator.getNaarVmTabel()[x] != transformator.NAAR_VM_STELLING[x] ? "ongelijk" : "" );
//		assertThat( "index=" + x, transformator.getNaarVmTabel()[x], is ( transformator.NAAR_VM_STELLING[x] ) );
//	}
	assertThat( transformator.getNaarVmTabel(), is( NAAR_VM_STELLING ) );
}
@Test
public void testBoStellingToVmStelling()
{
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	VMStelling vmStelling = getTransformator().boStellingToVmStelling( boStelling );
	VMStelling newVmStelling = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "e6" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	vmStelling = getTransformator().boStellingToVmStelling( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "e6" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
	
	getConfig().switchConfig( Config.PipoKoK );
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b5" )
		.aanZet( Wit )
		.build();
	boStelling.toString();
	vmStelling = getTransformator().boStellingToVmStelling( boStelling );
	newVmStelling = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b5" )
		.aanZet( Wit )
		.build();
	assertThat( vmStelling, is( newVmStelling ) );
}
}
