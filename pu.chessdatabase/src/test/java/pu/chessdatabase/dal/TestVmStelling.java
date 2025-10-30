package pu.chessdatabase.dal;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static pu.chessdatabase.bo.Kleur.*;

import java.math.BigInteger;
import java.text.MessageFormat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;

@SpringBootTest
public class TestVmStelling
{
public static final String VM_TO_STRING = """
WK=a1 ZK=c1 S3=b2 S4=d3 AanZet={4}
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. .. .. .. .. ..\s
.. .. .. {3} .. .. .. ..\s
.. {2} .. .. .. .. .. ..\s
{0} .. {1} .. .. .. .. ..\s
""";

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
@Autowired private Config config;

@SuppressWarnings( "null" )
@Test
public void testShiftLeft()
{
	assertThat( (63 << 6) + 63, is( 4095 ) ); // 4032 + 63
	assertThat( 63 << 6 + 63, is( 2016 ) ); // Waarschijnlijk een gigantisch overflow, zie hieronder
	assertThat( new BigInteger( "63" ).shiftLeft( 69 ), is( new BigInteger( "37188636052598456057856") ) ); // Klopt dus
	assertThat( 63 << (6 + 63), is( 2016 ) );
}
@Test
public void testGetBoStelling()
{
	// @@HIGH Fouten oplossen, en config-afhankelijk maken
	VMStelling vmStelling  = VMStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x11 )
		.s4( 0x23 )
		.aanZet( WIT )
		.build();
	BoStelling boStelling = BoStelling.builder()
		.wk( 0x00 )
		.zk( 0x02 )
		.s3( 0x21 )
		.s4( 0x43 )
		.aanZet( WIT )
		.build();
	assertThat( vmStelling.getBoStelling(), is( boStelling ) );
	
	vmStelling = VMStelling.alfaBuilder()
		.wk( "f1" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "d4" )
		.aanZet( ZWART )
		.build();
	boStelling = BoStelling.alfaBuilder()
		.wk( "c2" )
		.zk( "h3" )
		.s3( "g8" )
		.s4( "d4" )
		.aanZet( ZWART )
		.build();
	assertThat( vmStelling.getBoStelling(), is( boStelling ) );
}

@Test
public void testToString()
{
	// @@HIGH Fouten oplossen, en config-afhankelijk maken
	VMStelling vmStelling  = VMStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "c1" )
		.s3( "b2" )
		.s4( "d3" )
		.aanZet( WIT )
		.build();
	config.switchConfig( "KDKT" );
	String boStringText = MessageFormat.format( VM_TO_STRING, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		vmStelling.getAanZet().getAfko()
	);
	assertThat( vmStelling.toString().length(), is( boStringText.length() ) );
	assertThat( vmStelling.toString(), is( boStringText ) );
	
	config.switchConfig( "KLPK" );
	boStringText = MessageFormat.format( VM_TO_STRING, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		vmStelling.getAanZet().getAfko()
	);
	assertThat( vmStelling.toString().length(), is( boStringText.length() ) );
	assertThat( vmStelling.toString(), is( boStringText ) );

	config.switchConfig( "KLLK" );
	boStringText = MessageFormat.format( VM_TO_STRING, 
		config.getStukken().getWk().getStukString(),
		config.getStukken().getZk().getStukString(),
		config.getStukken().getS3().getStukString(),
		config.getStukken().getS4().getStukString(),
		vmStelling.getAanZet().getAfko()
	);
	assertThat( vmStelling.toString().length(), is( boStringText.length() ) );
	assertThat( vmStelling.toString(), is( boStringText ) );
}

}
