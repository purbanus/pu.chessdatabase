package pu.chessdatabase.web;

import java.util.Iterator;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import pu.chessdatabase.service.ChessDatabaseService;
import pu.chessdatabase.service.NewGameDocument;
import pu.chessdatabase.service.PartijDocument;

import jakarta.servlet.http.HttpSession;
import lombok.Data;

@Controller
//@Scope( "session" )
@Data
public class ChessDatabaseController
{
private static final Logger LOG = LoggerFactory.getLogger( ChessDatabaseController.class);

@Autowired private ChessDatabaseService service;

@Value( "${spring.application.name}" )
String appName;

void showSession( HttpSession aSession )
{
	LOG.info( "SessionId=" + aSession.getId() );
	Iterator<String> it = aSession.getAttributeNames().asIterator();
	if ( ! it.hasNext() )
	{
		LOG.info( "Session heeft geen attributes" );
	}
	else
	{
		for ( ; it.hasNext(); )
		{
			String attribute = it.next();
			LOG.info( "Session attribute=" + attribute + " = " + aSession.getAttribute( attribute ) );
		}
	}
}

@GetMapping( "/" )
public String goHome(Locale locale, Model model)
{
	return "redirect:/newgame";
}
@GetMapping( { "/newgame", "/newgame.html" } )
public String newGame( Model aModel, HttpSession aSession )
{
	LOG.info( "NewGame gestart" );
	LOG.debug( "Model=" + aModel.asMap() );
//	showSession( aSession );
	
	NewGameDocument newGameDocument = getService().newGame();
	aModel.addAttribute( "AppName", appName );
	aModel.addAttribute( "doc", newGameDocument );
	LOG.debug( "Model na vullen=" + aModel.asMap() );

	return "newgame";
}
@PostMapping(value = { "/do-newgame" } )
public RedirectView doNewGame( @ModelAttribute NewGameResponse aGameResponse, Model aModel ) 
{
	LOG.info( "NewGame response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );
	LOG.debug( "NewGameResponse=" + aGameResponse );

	PartijDocument partijDocument = getService().doNewGame( aGameResponse );
	return new RedirectView( "/game.html"
		+ "?wk=" + partijDocument.getWk()
		+ "&zk=" + partijDocument.getZk()
		+ "&s3=" + partijDocument.getS3()
		+ "&s4=" + partijDocument.getS4()
		+ "&aanZet=" + partijDocument.getAanZet()
	);
}
@PostMapping(value = { "/do-switch-config" } )
public RedirectView doSwitchConfig( @ModelAttribute SwitchConfigResponse aSwitchConfigResponse, Model aModel ) 
{
	LOG.info( "SwitchConfig response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );
	LOG.debug( "SwitchConfigResponse=" + aSwitchConfigResponse );

	getService().doSwitchConfig( aSwitchConfigResponse );
	return new RedirectView( "/newgame.html" );
}
@GetMapping( { "/game", "/game.html" } )
public String game( @ModelAttribute GameResponse aGameResponse, Model aModel, HttpSession aSession )
{
	LOG.info( "Game response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );
	LOG.debug( "GameResponse=" + aGameResponse );
	showSession( aSession );

	PartijDocument partijDocument = getService().getPartijDocument( aGameResponse.getBoStellingKey() );
	aModel.addAttribute( "doc", partijDocument );
	LOG.info( "NewGame response klaar. Model=" + aModel.asMap() );

	return "game";
}
@GetMapping(value = { "/do-zet" } )
public RedirectView doZet( @ModelAttribute ZetResponse aZetResponse, Model aModel ) 
{
	LOG.info( "Zet response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );
	LOG.debug( "ZetResponse=" + aZetResponse );

	PartijDocument partijDocument = getService().zet( aZetResponse );
	return new RedirectView( "/game.html"
		+ "?wk=" + partijDocument.getWk()
		+ "&zk=" + partijDocument.getZk()
		+ "&s3=" + partijDocument.getS3()
		+ "&s4=" + partijDocument.getS4()
		+ "&aanZet=" + partijDocument.getAanZet()
	);
}
@GetMapping(value = { "/zet-naar-begin" } )
public RedirectView zetNaarBegin( Model aModel ) 
{
	LOG.info( "Zet-naar-begin response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );

	PartijDocument partijDocument = getService().zetNaarBegin();
	return new RedirectView( "/game.html"
		+ "?wk=" + partijDocument.getWk()
		+ "&zk=" + partijDocument.getZk()
		+ "&s3=" + partijDocument.getS3()
		+ "&s4=" + partijDocument.getS4()
		+ "&aanZet=" + partijDocument.getAanZet()
	);
}
@GetMapping(value = { "/zet-terug" } )
public RedirectView zetTerug( Model aModel ) 
{
	LOG.info( "Zet-terug response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );

	PartijDocument partijDocument = getService().zetTerug();
	return new RedirectView( "/game.html"
		+ "?wk=" + partijDocument.getWk()
		+ "&zk=" + partijDocument.getZk()
		+ "&s3=" + partijDocument.getS3()
		+ "&s4=" + partijDocument.getS4()
		+ "&aanZet=" + partijDocument.getAanZet()
	);
}
@GetMapping(value = { "/zet-vooruit" } )
public RedirectView zetVooruit( Model aModel ) 
{
	LOG.info( "Zet-vooruit response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );

	PartijDocument partijDocument = getService().zetVooruit();
	return new RedirectView( "/game.html"
		+ "?wk=" + partijDocument.getWk()
		+ "&zk=" + partijDocument.getZk()
		+ "&s3=" + partijDocument.getS3()
		+ "&s4=" + partijDocument.getS4()
		+ "&aanZet=" + partijDocument.getAanZet()
	);
}
@GetMapping(value = { "/zet-naar-einde" } )
public RedirectView zetNaarEinde( Model aModel ) 
{
	LOG.info( "Zet-naar-einde response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );

	PartijDocument partijDocument = getService().zetNaarEinde();
	return new RedirectView( "/game.html"
		+ "?wk=" + partijDocument.getWk()
		+ "&zk=" + partijDocument.getZk()
		+ "&s3=" + partijDocument.getS3()
		+ "&s4=" + partijDocument.getS4()
		+ "&aanZet=" + partijDocument.getAanZet()
	);
}

}
