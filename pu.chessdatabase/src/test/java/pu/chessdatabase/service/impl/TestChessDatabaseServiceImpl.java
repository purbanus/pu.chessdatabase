package pu.chessdatabase.service.impl;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.bo.speel.Plies;
import pu.chessdatabase.bo.speel.Ply;
import pu.chessdatabase.bo.speel.VanNaar;
import pu.chessdatabase.dal.PliesDao;
import pu.chessdatabase.dbs.TestHelper;
import pu.chessdatabase.service.NewGameDocument;
import pu.chessdatabase.web.NewGameResponse;
import pu.chessdatabase.web.SwitchConfigResponse;

import jakarta.transaction.Transactional;
import lombok.Data;

@Data
@SpringBootTest
public class TestChessDatabaseServiceImpl
{
@Autowired private Partij partij;
@Autowired private ChessDatabaseServiceImpl service;
@Autowired private Config config;
@Autowired private PliesDao dao;

String savedConfigString;
@BeforeEach
public void setup()
{
	savedConfigString = config.getConfig();
	config.switchConfig( "KDKT" );
}
@AfterEach
public void destroy()
{
	config.switchConfig( savedConfigString );
}
@Test
public void testNewGame()
{
	List<NewGameDocument.Stuk> realStukken = List.of( 
		NewGameDocument.Stuk.builder()
			.name( "wkAlfa" )
			.label( "Witte Koning" )
			.veld( "a1" )
			.build()
		,
		NewGameDocument.Stuk.builder()
			.name( "s3Alfa" )
			.label( "Witte Dame" )
			.veld( "b2" )
			.build()
		,
		NewGameDocument.Stuk.builder()
			.name( "zkAlfa" )
			.label( "Zwarte Koning" )
			.veld( "h8" )
			.build()
		,
		NewGameDocument.Stuk.builder()
		.name( "s4Alfa" )
		.label( "Zwarte Toren" )
		.veld( "g7" )
		.build()
	);
	List<NewGameDocument.Stuk> fakeStukken = List.of( 
		NewGameDocument.Stuk.builder()
			.name( "s5Alfa" )
			.label( "Witte Geen stuk" )
			.veld( "a1" )
			.build()
	);
	NewGameDocument expectedNewGameDocument = NewGameDocument.builder()
		.configList( getConfig().getAvailableConfigs() )
		.config( "KDKT" )
		.realStukken( realStukken )
		.fakeStukken( fakeStukken )
		.aanZet( "Wit" )
		.build();

	NewGameDocument newGameDocument = service.newGame();
	assertThat( newGameDocument, is( expectedNewGameDocument ) );
}
@Test
public void testCreateStukVelden()
{
	Map<String, String> stukVelden = service.createStukVelden();
	assertThat( stukVelden.get( "wk" ), is( "a1" ) );
	assertThat( stukVelden.get( "zk" ), is( "h8" ) );
	assertThat( stukVelden.get( "s3" ), is( "b2" ) );
	assertThat( stukVelden.get( "s4" ), is( "g7" ) );

	config.switchConfig(  "KLPK" );
	stukVelden = service.createStukVelden();
	assertThat( stukVelden.get( "wk" ), is( "a1" ) );
	assertThat( stukVelden.get( "zk" ), is( "h8" ) );
	assertThat( stukVelden.get( "s3" ), is( "b2" ) );
	assertThat( stukVelden.get( "s4" ), is( "g7" ) );

	config.switchConfig(  "KDK" );
	stukVelden = service.createStukVelden();
	assertThat( stukVelden.get( "wk" ), is( "a1" ) );
	assertThat( stukVelden.get( "zk" ), is( "e4" ) );
	assertThat( stukVelden.get( "s3" ), is( "a2" ) );
}
@Test
public void doSwitchConfig()
{
	SwitchConfigResponse switchConfigResponse = new SwitchConfigResponse( "KTK" ); 
	service.doSwitchConfig( switchConfigResponse );
	assertThat( getConfig().getConfig(), is( "KTK" ) );
}
@Test
@Transactional
public void testDoNewGame()
{
	NewGameResponse newGameResponse = NewGameResponse.builder()
		.wkAlfa( "a1" )
		.zkAlfa( "h8" )
		.s3Alfa( "b2" )
		.s4Alfa( "g7" )
		.s5Alfa( "a1" )
		.aanZet(  "Wit" )
		.build();
	getService().doNewGame( newGameResponse );
	
	assertThat( getPartij().getPlies(), is( notNullValue() ) );
	assertThat( getPartij().getPlies().getPlies(), is( notNullValue() ) );
	assertThat( getPartij().getPlies().getPlies().size(), is( 1 ) );
	assertThat( getPartij().getPlies().getCurrentPlyNumber(), is( 0 ) );
	
	Ply ply = TestHelper.createOnePly( (VanNaar) null );
	ply.setId( 501 );
	assertThat( getPartij().getPlies().getCurrentPly(), is( ply ) );

	LocalDateTime started = LocalDateTime.now();
	Plies plies = Plies.builder()
		.id( 500 )
		.configString( "KDKT" )
		.userName( Partij.DEFAULT_USER_NAME )
		.currentPlyNumber( 0 )
		.begonnen( true )
		.plies( List.of( ply ) )
		.build();
	plies.setStarted( started );
	// Check de database
	Plies gotPlies = dao.getLatestPlies( Partij.DEFAULT_USER_NAME ); 
	assertThat( gotPlies, is( notNullValue() ) );
	gotPlies.setStarted( started );
	assertThat( gotPlies, is( plies ) );
}

}