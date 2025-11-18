package pu.chessdatabase.dal;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pu.chessdatabase.bo.speel.Plies;
import pu.chessdatabase.bo.speel.Ply;

import jakarta.persistence.Tuple;
import lombok.Data;

@Data
@Service
public class PliesDao
{
@Autowired PliesRepository pliesRepository;
@Autowired PliesQueryRepository pliesQueryRepository;

public PliesDao()
{
	super();
}
// Om het LazyInitializationException probleem op te lossen. Zie https://stackoverflow.com/questions/64762080/how-to-map-sql-native-query-result-into-dto-in-spring-jpa-repository?rq=1
// - In de repository definieer je de ophaalmethode als List<Tuple> getBla
// - Die Tuples converteer je naar een FlatDocument, zie convertTuplesToFlatDocuments. Het enige lastige is dat je dat hier op index moet doen, 
//   niet op veldnaam, en dat je de class moet opgeven
// - Converteer de FlatDocuments naar Plies met hun Plys
@SuppressWarnings( "null" )
//List<FlatDocument> convertTuplesToFlatDocuments( List<Tuple> aTuples )
//{
//	return aTuples.stream()
//		.map( t -> new FlatDocument( 
//			  t.get(  0, Integer.class )          // plies Id
//			, t.get(  1, String.class )      // configString
//			, t.get(  2, String.class )          // userName
//			, t.get(  3, Timestamp.class  )       // started
//			, t.get(  4, Integer.class ) // currentPlyNumber
//			, t.get(  5, Boolean.class )         // begonnen
//			, t.get(  6, Integer.class )            // ply id
//			, t.get(  7, String.class )             // ply einde
//			, t.get(  8, Integer.class )        // ply zetnummer
//			, t.get(  9, Integer.class )              // ply van
//			, t.get(  10, Integer.class )            // ply naar
//			, t.get( 11, Boolean.class )           // ply schaak
//			, t.get( 12, Integer.class )               // ply wk
//			, t.get( 13, Integer.class )               // ply zk
//			, t.get( 14, Integer.class )               // ply s3
//			, t.get( 15, Integer.class )               // ply s4
//			, t.get( 16, Integer.class )               // ply s5
//			, t.get( 17, String.class )           // ply aanZet
//			, t.get( 18, String.class )        // ply resultaat
//			, t.get( 19, Integer.class )    // ply aantalZetten
//		) )
//		.collect( Collectors.toList() );
//}
//private Plies convertTuplesToPlies( List<Tuple> tuples )
//{
//	List<FlatDocument> flatDocuments = convertTuplesToFlatDocuments( tuples );
//	return convertFlatDocumentsToPlies( flatDocuments );
//}

Plies convertFlatDocumentsToPlies( List<FlatDocument> aFlatDocuments )
{
	Plies plies = null;
	Map<Integer, Ply> plyMap = new HashMap<>();
	for ( FlatDocument flatDocument : aFlatDocuments )
	{
		if ( plies == null )
		{
			plies = Plies.fromFlatDocument( flatDocument );
		}
		if ( plies.getId() != flatDocument.getPliesId() )
		{
			throw new RuntimeException("Meer dan een pliesId in convertFlatDocumentsToPlies" );
		}
		
		Ply ply = plyMap.get( flatDocument.getPlyId() );
		if ( ply == null )
		{
			ply = Ply.fromFlatDocument( flatDocument );
			plyMap.put( ply.getId(), ply );
			ply.setPlies( plies );
			plies.getPlies().add( ply );
		}
	}	
	return plies;
}
public Plies getPliesById( int aId )
{
	List<FlatDocument> flatDocuments = getPliesQueryRepository().getPliesFlatDocumentsById( aId );
	return convertFlatDocumentsToPlies( flatDocuments );
}
@SuppressWarnings( { "null", "unused" } )
public Plies getLatestPlies( String aUserName )
{
	Integer latestId = getPliesRepository().getLatestPlies( aUserName );
	if ( latestId == null )
	{
		return null;
	}
	return getPliesById( latestId );
}
public Plies getPliesByIdNewStyle( int aId )
{
	// @@NOG Ik krijg dit maar niet blijvend aan de praat
	//return pliesRepository.getReferenceById( aId );
	return pliesRepository.getPliesById( aId );
}
@SuppressWarnings( { "null", "unused" } )
public Plies getLatestPliesNewStyle( String aUserName )
{
	// @@NOG Ik krijg dit maar niet blijvend aan de praat
	Integer latestId = getPliesRepository().getLatestPlies( aUserName );
	if ( latestId == null )
	{
		return null;
	}
	return getPliesByIdNewStyle( latestId );
}
public void savePlies( Plies aPlies )
{
	getPliesRepository().save( aPlies );
}
//public void savePly( Ply aPly )
}
