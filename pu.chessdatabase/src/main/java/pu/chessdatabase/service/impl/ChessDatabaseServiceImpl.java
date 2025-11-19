package pu.chessdatabase.service.impl;

import static pu.chessdatabase.bo.Kleur.*;
import static pu.chessdatabase.bo.configuraties.StukType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Config;
import pu.chessdatabase.bo.Stuk;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.bo.speel.Plies;
import pu.chessdatabase.dal.PliesDao;
import pu.chessdatabase.service.BoStellingKey;
import pu.chessdatabase.service.ChessDatabaseService;
import pu.chessdatabase.service.NewGameDocument;
import pu.chessdatabase.service.PartijDocument;
import pu.chessdatabase.web.NewGameResponse;
import pu.chessdatabase.web.SwitchConfigResponse;
import pu.chessdatabase.web.ZetResponse;

import jakarta.transaction.Transactional;
import lombok.Data;

@Data
@Service
public class ChessDatabaseServiceImpl implements ChessDatabaseService
{
@Autowired private Partij partij;
@Autowired private PliesDao pliesDao;
@Autowired private Config config;

@Override
public NewGameDocument newGame()
{
	List<Stuk> realStukken = getConfig().getRealStukken();
	List<Stuk> fakeStukken = getConfig().getFakeStukken();

	Map<String, String> stukVelden = createStukVelden();
	List<NewGameDocument.Stuk> realDocStukken = new ArrayList<>();
	List<NewGameDocument.Stuk> fakeDocStukken = new ArrayList<>();
	for ( Stuk stuk : realStukken )
	{
		realDocStukken.add( NewGameDocument.Stuk.builder()
			.name( stuk.getId() + "Alfa" )
			.label( stuk.getLabel() )
			.veld( stukVelden.get( stuk.getId() ) )
			.build()
		);
	}
	for ( Stuk stuk : fakeStukken )
	{
		fakeDocStukken.add( NewGameDocument.Stuk.builder()
			.name( stuk.getId() + "Alfa" )
			.label( stuk.getLabel() )
			.veld( stukVelden.get( "wk" ) )
			.build()
		);
	}
	return NewGameDocument.builder()
		.configList( getConfig().getAvailableConfigs() )
		.config( getConfig().getConfig() )
		.realStukken( realDocStukken )
		.fakeStukken( fakeDocStukken )
		.aanZet( Wit.toString() )
		.build();
}
// @@HIGH Dit hoort eigenlijk ook bij de config-beweging
Map<String, String> createStukVelden()
{
	Map<String, String> stukVelden = new HashMap<>();
	switch ( config.getConfig() )
	{
		case "KDK":
		{
			stukVelden.put( "wk", "a1" );
			stukVelden.put( "zk", "e4" );
			stukVelden.put( "s3", "a2" );
			break;
		}
		case "KTK":
		{
			stukVelden.put( "wk", "a1" );
			stukVelden.put( "zk", "e4" );
			stukVelden.put( "s3", "a2" );
			break;
		}
		case "KDKT":
		{
			stukVelden.put( "wk", "a1" );
			stukVelden.put( "zk", "h8" );
			stukVelden.put( "s3", "b2" );
			stukVelden.put( "s4", "g7" );
			break;
		}
		case "KLPK":
		{
			stukVelden.put( "wk", "a1" );
			stukVelden.put( "zk", "h8" );
			stukVelden.put( "s3", "b2" );
			stukVelden.put( "s4", "g7" );
			break;
		}
		case "KLLK":
		{
			stukVelden.put( "wk", "a1" );
			stukVelden.put( "zk", "h8" );
			stukVelden.put( "s3", "a2" );
			stukVelden.put( "s4", "a3" );
			break;
		}
		default:
		{
			throw new RuntimeException( "Ongeldige configuratie in newGame()" );
		}
	}
	return stukVelden;
}
@Override
public void doSwitchConfig( SwitchConfigResponse aSwitchConfigResponse )
{
	getConfig().switchConfig( aSwitchConfigResponse.getConfig() );
}
@Override
@Transactional
public PartijDocument doNewGame( NewGameResponse aNewGameResponse )
{
	BoStelling boStelling = createBoStelling( aNewGameResponse.getBoStellingKey() );
	boStelling.normaliseer( getConfig().getAantalStukken() );
	getPartij().newGame( boStelling );
	getPliesDao().savePlies( getPartij().getPlies() );
	return getPartijDocument( aNewGameResponse.getBoStellingKey() );
}
@Override
public PartijDocument getPartijDocument( BoStellingKey aStellingKey )
{
	BoStelling boStelling = createBoStelling( aStellingKey );
	maybeRestoreGame( boStelling );
	return PartijDocument.builder()
		.wk( Partij.veldToHexGetal( aStellingKey.getWk() ) )
		.zk( Partij.veldToHexGetal( aStellingKey.getZk() ) )
		.s3( Partij.veldToHexGetal( aStellingKey.getS3() ) )
		.s4( Partij.veldToHexGetal( aStellingKey.getS4() ) )
		.s5( Partij.veldToHexGetal( aStellingKey.getS5() ) )
		.aanZet( aStellingKey.getAanZet().toString() )
		.wkStuk( config.getStukken().getWk().getLabel() )
		.zkStuk( config.getStukken().getZk().getLabel() )
		.s3Stuk( config.getStukken().getS3().getLabel() )
		.s4Stuk( config.getStukken().getS4().getLabel() )
		.s5Stuk( config.getStukken().getS5().getLabel() )
//		.stelling( boStelling )
		.resultaat( getPartij().getResultaatRecord() )
		.zetten( getPartij().getPartijReport().getZetten() )
		.gegenereerdeZetten( getPartij().getGegenereerdeZetten() )
		.naarBeginMag( getPartij().getPlies().isNaarBeginMag() )
		.terugMag( getPartij().getPlies().isTerugMag() )
		.vooruitMag( getPartij().getPlies().isVooruitMag() )
		.naarEindeMag( getPartij().getPlies().isNaarEindeMag() )
		.build();
}
PartijDocument getPartijDocument( BoStelling aBoStelling )
{
	return getPartijDocument( aBoStelling.getBoStellingKey() );
}
BoStelling createBoStelling( BoStellingKey aBoStellingKey )
{
	BoStelling boStelling = BoStelling.builder()
		.wk( aBoStellingKey.getWk() )
		.zk( aBoStellingKey.getZk() )
		.s3( aBoStellingKey.getS3() )
		.s4( aBoStellingKey.getS4() )
		.s5( aBoStellingKey.getS5() )
		.aanZet( aBoStellingKey.getAanZet() )
		.build();
	boStelling.normaliseer( getConfig().getAantalStukken() );
	return boStelling;
}
void maybeRestoreGame( BoStelling aBoStelling )
{
	if ( ! getPartij().isBegonnen() )
	{
		Plies plies = getPliesDao().getLatestPlies( Partij.DEFAULT_USER_NAME );
		if ( plies != null )
		{
			// Restore current game
			getPartij().setPlies( plies );
		}
		else
		{
			if ( aBoStelling != null && getPartij().isLegaleStelling( aBoStelling ) )
			{
				// In vredesnaam maar een newGame
				getPartij().newGame( aBoStelling );
			}
			else
			{
				// @@HIGH Misschien een redirect naar newGame?
				throw new RuntimeException( "Er is geen stelling te bepalen. Probeer het met een nieuwe stelling (newGame)" );
			}
		}
	}
}
@Override
@Transactional
public PartijDocument zet( ZetResponse aZetResponse )
{
	BoStelling boStelling = createBoStelling( aZetResponse.getBoStellingKey() );
	maybeRestoreGame( boStelling );

	getPartij().zet( aZetResponse.getNieuweZet() );
	getPliesDao().savePlies( getPartij().getPlies() );
	return getPartijDocument( getPartij().getStand() );
}
@Override
@Transactional
public PartijDocument zetNaarBegin()
{
	maybeRestoreGame( null );
	BoStelling boStelling = getPartij().zetNaarBegin();
	getPliesDao().savePlies( getPartij().getPlies() );
	return getPartijDocument( boStelling );
}
@Override
@Transactional
public PartijDocument zetTerug()
{
	maybeRestoreGame( null );
	BoStelling boStelling = getPartij().zetTerug();
	getPliesDao().savePlies( getPartij().getPlies() );
	return getPartijDocument( boStelling );
}
@Override
@Transactional
public PartijDocument zetVooruit()
{
	maybeRestoreGame( null );
	BoStelling boStelling = getPartij().zetVooruit();
	getPliesDao().savePlies( getPartij().getPlies() );
	return getPartijDocument( boStelling );
}
@Override
@Transactional

public PartijDocument zetNaarEinde()
{
	maybeRestoreGame( null );
	BoStelling boStelling = getPartij().zetNaarEinde();
	getPliesDao().savePlies( getPartij().getPlies() );
	return getPartijDocument( boStelling );
}
}
