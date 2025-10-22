package pu.chessdatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories( "pu.chessdatabase.dal" ) 
//@EntityScan("pu.chessdatabase")
@SpringBootApplication 
public class Application {

	public static void main( String[] args )
	{
		SpringApplication.run( Application.class, args );
	}
}
