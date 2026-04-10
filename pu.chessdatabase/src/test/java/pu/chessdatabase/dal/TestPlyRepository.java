package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
//import static org.hamcrest.MatcherAssert.*;
//import static org.hamcrest.Matchers.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static pu.chessdatabase.bo.Kleur.*;
//import static pu.chessdatabase.bo.speel.Einde.*;
//import static pu.chessdatabase.dbs.Resultaat.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import pu.chessdatabase.bo.BoStelling;
//import pu.chessdatabase.bo.Config;
//import pu.chessdatabase.bo.speel.Partij;
//import pu.chessdatabase.bo.speel.Plies;
//import pu.chessdatabase.bo.speel.Ply;
//import pu.chessdatabase.bo.speel.VanNaar;
//
//import jakarta.transaction.Transactional;

//@SpringBootTest
//@Transactional
public class TestPlyRepository
{
//@Autowired private PlyRepository repository;
//@Autowired private PliesDao dao;
//@Autowired private Config config;

//@Test
//@Transactional
//public void testDeletePlys()
//{
//	Plies plies = Plies.builder()
//		.configString( config.getConfig() )
//		.userName( Partij.DEFAULT_USER_NAME )
////		.started( LocalDateTime.now() )
//		.currentPlyNumber( 3 )
//		.begonnen( true )
//		.build();
//	plies.setStarted( LocalDateTime.now() );
//	BoStelling boStelling1 = BoStelling.alfaBuilder()
//		.wk( "a1" )
//		.zk( "h8" )
//		.s3( "b2" )
//		.s4( "g7" )
//		.s5( "a1" )
//		.aanZet( Wit )
//		.resultaat( Gewonnen )
//		.aantalZetten( 30 )
//		.build();
//	BoStelling boStelling2 = BoStelling.alfaBuilder()
//		.wk( "a1" )
//		.zk( "h8" )
//		.s3( "e5" )
//		.s4( "g7" )
//		.s5( "a1" )
//		.aanZet( Zwart )
//		.resultaat( Verloren )
//		.aantalZetten( 29 )
//		.build();
//	BoStelling boStelling3 = BoStelling.alfaBuilder()
//		.wk( "a1" )
//		.zk( "g8" )
//		.s3( "e5" )
//		.s4( "g7" )
//		.s5( "a1" )
//		.aanZet( Wit )
//		.resultaat( Gewonnen )
//		.aantalZetten( 29 )
//		.build();
//	plies.addPly( Ply.builder()
//		.plies( plies )
//		.einde( Nog_niet )
//		.zetNummer( 1 )
//		.schaak( false )
//		.vanNaar( new VanNaar( "b2", "e5") )
//		.boStelling( boStelling1 )
//		.build()
//		);
//	plies.addPly( Ply.builder()
//		.plies( plies )
//		.einde( Nog_niet )
//		.zetNummer( 1 )
//		.schaak( false )
//		.vanNaar( new VanNaar( "h8", "g8") )
//		.boStelling( boStelling2 )
//		.build()
//		);
//	plies.addPly( Ply.builder()
//		.plies( plies )
//		.einde( Nog_niet )
//		.zetNummer( 2 )
//		.schaak( false )
//		.vanNaar( null )
//		.boStelling( boStelling3 )
//		.build()
//		);
//	dao.savePlies( plies );
//	
//	Plies gotPlies = dao.getLatestPlies( Partij.DEFAULT_USER_NAME );
//	assertNotNull( plies );
//	assertThat( gotPlies.getPlies().size(), is( 3 ) );
//	assertThat( gotPlies, is( plies ) );
//
//	plies.addPly( Ply.builder()
//		.plies( plies )
//		.einde( Nog_niet )
//		.zetNummer( 2 )
//		.schaak( false )
//		.vanNaar( new VanNaar( 1, 2 ) )
//		.boStelling( boStelling3 )
//		.build()
//		);
//	dao.savePlies( plies );
//	gotPlies = dao.getLatestPlies( Partij.DEFAULT_USER_NAME );
//	assertNotNull( plies );
//	assertThat( gotPlies.getPlies().size(), is( 4 ) );
//	assertThat( gotPlies, is( plies ) );
//
	
//}
	
}
