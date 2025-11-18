package pu.chessdatabase.dal;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pu.chessdatabase.bo.speel.Plies;

import jakarta.persistence.Tuple;

public interface PliesQueryRepository extends CrudRepository<Plies, Integer>
{
@Query( value = """
SELECT
  plies.id AS PLIES_ID
, plies.config_string AS PLIES_CONFIG_STRING
, plies.user_name AS PLIES_USER_NAME
, plies.started AS PLIES_STARTED
, plies.current_ply_number AS PLIES_CURRENT_PLY_NUMBER
, plies.begonnen AS PLIES_BEGONNEN
, ply.id AS PLY_ID
, ply.einde AS PLY_EINDE
, ply.zet_nummer AS PLY_ZETNUMMER
, ply.van AS PLY_VAN
, ply.naar AS PLY_NAAR
, ply.schaak AS PLY_SCHAAK
, ply.wk AS PLY_WK
, ply.zk AS PLY_ZK
, ply.s3 AS PLY_S3
, ply.s4 AS PLY_S4
, ply.s5 AS PLY_S5
, ply.aan_zet AS PLY_AANZET
, ply.resultaat AS PLY_RESULTAAT
, ply.aantal_zetten AS PLY_AANTAL_ZETTEN
from plies
join ply
	on plies.id = ply.plies_id
where plies.id = (:id)
order by ply.id
""", nativeQuery = true )
List<Tuple> getPliesTuplesById( @Param( "id" ) int aId );
@Query( value = """
SELECT
  plies.id AS PLIES_ID
, plies.config_string AS PLIES_CONFIG_STRING
, plies.user_name AS PLIES_USER_NAME
, plies.started AS PLIES_STARTED
, plies.current_ply_number AS PLIES_CURRENT_PLY_NUMBER
, plies.begonnen AS PLIES_BEGONNEN
, ply.id AS PLY_ID
, ply.einde AS PLY_EINDE
, ply.zet_nummer AS PLY_ZETNUMMER
, ply.van AS PLY_VAN
, ply.naar AS PLY_NAAR
, ply.schaak AS PLY_SCHAAK
, ply.wk AS PLY_WK
, ply.zk AS PLY_ZK
, ply.s3 AS PLY_S3
, ply.s4 AS PLY_S4
, ply.s5 AS PLY_S5
, ply.aan_zet AS PLY_AANZET
, ply.resultaat AS PLY_RESULTAAT
, ply.aantal_zetten AS PLY_AANTAL_ZETTEN
from plies
join ply
	on plies.id = ply.plies_id
where plies.id = (:id)
order by ply.id
""", nativeQuery = true )
List<FlatDocument> getPliesFlatDocumentsById( @Param( "id" ) int aId );

}
