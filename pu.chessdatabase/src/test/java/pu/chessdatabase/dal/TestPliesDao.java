package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.speel.Einde.*;
import static pu.chessdatabase.dbs.Resultaat.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.bo.speel.Plies;
import pu.chessdatabase.bo.speel.Ply;
import pu.chessdatabase.bo.speel.VanNaar;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class TestPliesDao
{
@Autowired private PliesQueryRepository repository;
@Autowired private PliesDao dao;
@Autowired private Config config;

@SuppressWarnings( {
    "null"
} )
public void checkPlies( Plies aPlies )
{
	assertThat( aPlies.getId(), is( 1 ) );
	assertThat( aPlies.getConfigString(), is( "KDKT" ) );
	assertThat( aPlies.getUserName(), is( Partij.DEFAULT_USER_NAME ) );
	assertThat( aPlies.getStarted(), is( LocalDateTime.of( 2025, 5, 14, 13, 15, 0, 0 ) ) );
	assertThat( aPlies.getCurrentPlyNumber(), is( 2 ) );
	assertThat( aPlies.isBegonnen(), is( true ) );
	assertThat( aPlies.getPlies().size(), is( 2 ) );
	
	BoStelling boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3(  "b2" )
		.s4( "g7" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 34 )
		.schaak( false )
		.build();
	Ply ply = aPlies.getPlies().get( 0 );
	assertThat( ply.getId(), is( 1 ) );
	assertThat( ply.getEinde(), is( Nog_niet ) );
	assertThat( ply.getZetNummer(), is( 1 ) );
	assertThat( ply.getVanNaar(), is( new VanNaar( 17, 85 ) ) );
	assertThat( ply.isSchaak(), is( false ) );
	assertThat( ply.getBoStelling(), is( boStelling ) );

	ply = aPlies.getPlies().get( 1 );
	assertThat( ply.getId(), is( 2 ) );
	assertThat( ply.getEinde(), is( Nog_niet ) );
	assertThat( ply.getZetNummer(), is( 2 ) );
	assertThat( ply.getVanNaar(), is( new VanNaar( 17, 85 ) ) );
	assertThat( ply.isSchaak(), is( false ) );
	assertThat( ply.getBoStelling(), is( boStelling ) );
}
//@Test
//public void testGetPliesAsTuples()
//{
//	List<Tuple> tuples = repository.getPliesTuplesById( 1 );
//	assertThat( tuples.size(), is( 2 ) );
//	Tuple tuple = tuples.get( 0 );
//	assertNotNull( tuple );
//}
//@Test
//public void testConvertTuplesToFlatDocuments()
//{
//	List<Tuple> tuples = repository.getPliesTuplesById( 1 );
//	List<FlatDocument> documents = converter.convertTuplesToFlatDocuments( tuples );
//	assertThat( documents.size(), is( 2 )  );
//}

@Test
public void testGetPliesAsFlatDocuments()
{
	List<FlatDocument> flatDocuments = repository.getPliesFlatDocumentsById( 1 );
	assertThat( flatDocuments.size(), is( 2 ) );
}
@Test
public void testGetPliesById()
{
	Plies plies = dao.getPliesById( 1 );
	checkPlies( plies );
}
@Test
public void testGetLatestPlies()
{
	Plies plies = dao.getLatestPlies( Partij.DEFAULT_USER_NAME );

	checkPlies( plies );
}
@Test
public void testGetLatestPliesWithInvalidUser()
{
	Plies plies = dao.getLatestPlies( "Pipo Koeie" );
	assertThat( plies, is( nullValue() ) );
}
@Test
@Transactional
public void testSavePlies()
{
	Plies plies = Plies.builder()
		.configString( config.getConfig() )
		.userName( Partij.DEFAULT_USER_NAME )
//		.started( LocalDateTime.now() )
		.currentPlyNumber( 3 )
		.begonnen( true )
		.build();
	plies.setStarted( LocalDateTime.now() );
	BoStelling boStelling1 = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 30 )
		.build();
	BoStelling boStelling2 = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "e5" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Zwart )
		.resultaat( Verloren )
		.aantalZetten( 29 )
		.build();
	BoStelling boStelling3 = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "g8" )
		.s3( "e5" )
		.s4( "g7" )
		.s5( "a1" )
		.aanZet( Wit )
		.resultaat( Gewonnen )
		.aantalZetten( 29 )
		.build();
	plies.addPly( Ply.builder()
		.plies( plies )
		.einde( Nog_niet )
		.zetNummer( 1 )
		.schaak( false )
		.vanNaar( new VanNaar( "b2", "e5") )
		.boStelling( boStelling1 )
		.build()
		);
	plies.addPly( Ply.builder()
		.plies( plies )
		.einde( Nog_niet )
		.zetNummer( 1 )
		.schaak( false )
		.vanNaar( new VanNaar( "h8", "g8") )
		.boStelling( boStelling2 )
		.build()
		);
	plies.addPly( Ply.builder()
		.plies( plies )
		.einde( Nog_niet )
		.zetNummer( 2 )
		.schaak( false )
		.vanNaar( null )
		.boStelling( boStelling3 )
		.build()
		);
	dao.savePlies( plies );
	
	Plies gotPlies = dao.getLatestPlies( Partij.DEFAULT_USER_NAME );
	assertNotNull( plies );
	assertThat( gotPlies, is( plies ) );
}
	
}
