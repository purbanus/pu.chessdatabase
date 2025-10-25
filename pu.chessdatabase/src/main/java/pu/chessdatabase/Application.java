package pu.chessdatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableJpaRepositories( "pu.chessdatabase.dal" ) 
//@EntityScan("pu.chessdatabase")
@SpringBootApplication 
public class Application {

	public static void main( String[] args )
	{
		SpringApplication.run( Application.class, args );
	}
}
