package pu.chessdatabase.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Gen;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.dbs.Dbs;

@Configuration
public class WebConfig
{
private final Dbs dbs;
private Gen gen;
private Config config;

WebConfig( Dbs aDbs, Gen aGen, Config aConfig )
{
	super();
	dbs = aDbs;
	gen = aGen;
	config = aConfig;
}

@Bean
@Scope( 
	value = WebApplicationContext.SCOPE_SESSION, 
	proxyMode = ScopedProxyMode.TARGET_CLASS )
Partij partij()
{
	return new Partij( dbs, gen, config );
}
}
