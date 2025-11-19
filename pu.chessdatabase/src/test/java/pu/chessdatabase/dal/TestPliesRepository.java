package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.speel.Partij;

@SpringBootTest

// Dit is eigenlijk verboden. Je moet je queries zo maken dat ze alles meteen ophalen
//@Transactional

// Waarvoor was dit?
//@Rollback( false )

// Dit doet het niet. Ik krijg een fout: "No qualifying bean of type 'pu.heavymetal.dal.AuteurRepositoryHelper' available"
// Snap er niks van maar het werkt gewoon niet.
//@DataJpaTest

public class TestPliesRepository
{
@Autowired private PliesRepository pliesRepository;

//@Test
//// Dit is nodig omdat we refereren aan attributen van Issue die nog niet opgehaald zijn. Door eraan te refereren (bijv strips)
//// moeten ze alsnog worden opgehaald. Standaard krijg je dan een LazyInitializationException. Dit voorkom je door de transactie
//// hier naar toe te halen met @Transactional. Echter, je krijgt dan een extra query per onopgehaald attribuut, in dit geval 
//// strips, paginas en auteurs. In dit geval is dit niet zo erg omdat we maar één issue ophalen, maar bij 100 issues heb je al snel
//// 300 extra queries en dat is te veel.
//@Transactional

@Test
//@Transactional // Zie hierboven
public void testGetLatestPlies()
{
	int latestPly = pliesRepository.getLatestPlies( Partij.DEFAULT_USER_NAME );
	assertThat( latestPly, is( 1 ) );
}


}
