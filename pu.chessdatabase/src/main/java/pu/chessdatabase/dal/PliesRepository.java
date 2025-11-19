package pu.chessdatabase.dal;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pu.chessdatabase.bo.speel.Plies;

public interface PliesRepository extends JpaRepository<Plies, Integer>
{
//List<JaarDocument> getJaren();

//@Query( value = "SELECT count(distinct i.jaar) from Issue i" )
//int getJarenCount();

@Query( value = """
	SELECT id from Plies p
		WHERE p.userName = :userName
		ORDER BY p.started DESC 
		LIMIT 1
	""")
Integer getLatestPlies( @Param( value = "userName" ) String aUser );

@EntityGraph( attributePaths = { "ply" } )
Plies getPliesById( int aId );

//@Modifying
//@Query("update Strip s set s.review = :review where s.id = :id")
//void updateStripReview(@Param(value = "id") int id, @Param(value = "review") String review );
}
