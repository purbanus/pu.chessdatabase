package pu.chessdatabase.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.dal.Dbs;

@Configuration
public class WebConfig
{
@Autowired private Dbs dbs;
@Bean
@Scope( 
	value = WebApplicationContext.SCOPE_SESSION, 
	proxyMode = ScopedProxyMode.TARGET_CLASS )
Partij partij()
{
	return new Partij( dbs );
}
}
