package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static pu.chessdatabase.bo.Kleur.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.dal.Dbs;
import pu.chessdatabase.dal.ResultaatType;

@SuppressWarnings( "unused" ) // Dit gaat over die jupiter.api.Assertions
@SpringBootTest
public class TestGenOverProductie
{
@Autowired private Gen gen;
@Autowired private Dbs dbs;

@BeforeEach
public void setup()
{
	dbs.setDbsNaam( dbs.DFT_DBS_NAAM );
	dbs.open();
}
@AfterEach
public void destroy()
{
}

@Test
public void testGenereerZetten()
{
	BoStelling boStelling;
	GegenereerdeZetten genZRec;
	
	boStelling = BoStelling.alfaBuilder()
		.wk( "a1" )
		.zk( "h8" )
		.s3( "b2" )
		.s4( "g7" )
		.aanZet( WIT )
		.build();
	genZRec = gen.genereerZetten( boStelling );
	assertThat( genZRec.getAantal(), is( 23 ) );

	assertThat( Gen.veldToAlfa(genZRec.getStellingen().get(  0 ).getWk() ), is( "b1" ) );
	assertThat( Gen.veldToAlfa(genZRec.getStellingen().get(  1 ).getWk() ), is( "a2" ) );
	
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  2 ).getS3() ), is( "c2" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  3 ).getS3() ), is( "d2" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  4 ).getS3() ), is( "e2" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  5 ).getS3() ), is( "f2" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  6 ).getS3() ), is( "g2" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  7 ).getS3() ), is( "h2" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  8 ).getS3() ), is( "c3" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get(  9 ).getS3() ), is( "d4" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 10 ).getS3() ), is( "e5" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 11 ).getS3() ), is( "f6" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 12 ).getS3() ), is( "g7" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 13 ).getS3() ), is( "b3" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 14 ).getS3() ), is( "b4" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 15 ).getS3() ), is( "b5" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 16 ).getS3() ), is( "b6" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 17 ).getS3() ), is( "b7" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 18 ).getS3() ), is( "b8" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 19 ).getS3() ), is( "a3" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 20 ).getS3() ), is( "a2" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 21 ).getS3() ), is( "b1" ) );
	assertThat( Gen.veldToAlfa( genZRec.getStellingen().get( 22 ).getS3() ), is( "c1" ) );
}
}