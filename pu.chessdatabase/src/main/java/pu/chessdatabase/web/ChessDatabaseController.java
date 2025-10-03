package pu.chessdatabase.web;

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
import pu.chessdatabase.service.PartijDocument;

import lombok.Data;

@Controller
@Data
public class ChessDatabaseController
{
private static final Logger LOG = LoggerFactory.getLogger( ChessDatabaseController.class);

@Autowired private ChessDatabaseService chessDatabaseService;

@Value( "${spring.application.name}" )
String appName;

@GetMapping( "/" )
public String goHome(Locale locale, Model model)
{
	return "redirect:/newgame";
}
@GetMapping( { "/newgame", "/newgame.html" } )
public String newGame( Model aModel )
{
	LOG.info( "NewGame gestart" );
	LOG.debug( "Model=" + aModel.asMap() );

	aModel.addAttribute( "AppName", appName );
	aModel.addAttribute( "wk", "a1" );
	aModel.addAttribute( "zk", "h8" );
	aModel.addAttribute( "s3", "b2" );
	aModel.addAttribute( "s4", "g7" );
	aModel.addAttribute( "aanZet", "wit" );
	LOG.debug( "Model na vullen=" + aModel.asMap() );

	return "newgame";
}
@PostMapping(value = { "/do-newgame" } )
public RedirectView doNewGame( @ModelAttribute NewGameResponse aNewGameResponse, Model aModel ) 
{
	LOG.info( "NewGame response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );
	LOG.debug( "NewGameResponse=" + aNewGameResponse );

	getChessDatabaseService().newGame( aNewGameResponse );
	return new RedirectView( "/game.html"
		+ "?wk=" + aNewGameResponse.getWk()
		+ "&zk=" + aNewGameResponse.getZk()
		+ "&s3=" + aNewGameResponse.getS3()
		+ "&s4=" + aNewGameResponse.getS4()
		+ "&aanZet=" + aNewGameResponse.getAanZet()
	);
}
@GetMapping( { "/game", "/game.html" } )
public String game( @ModelAttribute NewGameResponse aNewGameResponse, Model aModel )
{
	LOG.info( "Game response gestart" );
	LOG.debug( "Model=" + aModel.asMap() );
	LOG.debug( "GameResponse=" + aNewGameResponse );

	PartijDocument partijDocument = getChessDatabaseService().getPartijDocument( aNewGameResponse );
	aModel.addAttribute( "partijDocument", partijDocument );
	LOG.info( "NewGame response klaar. Model=" + aModel.asMap() );

	return "game";
}

}
