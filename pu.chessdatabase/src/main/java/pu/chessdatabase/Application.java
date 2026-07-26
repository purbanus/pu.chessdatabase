package pu.chessdatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

//@EnableJpaRepositories( "pu.chessdatabase.dal" ) 
//@EntityScan("pu.chessdatabase")
@SpringBootApplication 
public class Application extends SpringBootServletInitializer
{
	public static void main( String[] args )
	{
		SpringApplication.run( Application.class, args );
	}
}
