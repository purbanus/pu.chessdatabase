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
import pu.chessdatabase.service.BoStellingKey;
import pu.chessdatabase.service.ChessDatabaseService;
import pu.chessdatabase.service.NewGameDocument;
import pu.chessdatabase.service.PartijDocument;
import pu.chessdatabase.web.NewGameResponse;
import pu.chessdatabase.web.SwitchConfigResponse;
import pu.chessdatabase.web.ZetResponse;

import lombok.Data;

@Data
@Service
public class ChessDatabaseServiceImpl implements ChessDatabaseService
{
@Autowired private Partij partij;
@Autowired private Config config;

@Override
public NewGameDocument newGame()
{
	List<Stuk> stukken = config.getStukList();
	stukken = sorteerStukken( stukken );

	Map<String, String> stukVelden = createStukVelden();
	List<NewGameDocument.Stuk> realStukken = new ArrayList<>();
	List<NewGameDocument.Stuk> fakeStukken = new ArrayList<>();
	for ( Stuk stuk : stukken )
	{
		if ( stuk.getStukType() == Geen )
		{
			fakeStukken.add( NewGameDocument.Stuk.builder()
				.name( stuk.getId() + "Alfa" )
				.label( stuk.getLabel() )
				.veld( stukVelden.get( "wk" ) )
				.build()
			);
		}
		else
		{
			realStukken.add( NewGameDocument.Stuk.builder()
				.name( stuk.getId() + "Alfa" )
				.label( stuk.getLabel() )
				.veld( stukVelden.get( stuk.getId() ) )
				.build()
			);
		}
	}
	return NewGameDocument.builder()
		.configList( getConfig().getAvailableConfigs() )
		.config( getConfig().getConfig() )
		.realStukken( realStukken )
		.fakeStukken( fakeStukken )
		.aanZet( Wit.toString() )
		.build();
}
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
private List<Stuk> sorteerStukken( List<Stuk> aStukken )
{
	List<Stuk> newStukken = new ArrayList<>();
	for ( Stuk stuk : aStukken )
	{
		if ( stuk.getKleur() == Wit )
		{
			newStukken.add( stuk );
		}
	}
	for ( Stuk stuk : aStukken )
	{
		if ( stuk.getKleur() == Zwart )
		{
			newStukken.add( stuk );
		}
	}
	return newStukken;
}
@Override
public void doSwitchConfig( SwitchConfigResponse aSwitchConfigResponse )
{
	getConfig().switchConfig( aSwitchConfigResponse.getConfig() );
  }
@Override
public PartijDocument doNewGame( NewGameResponse aNewGameResponse )
{
	BoStelling boStelling = createBoStelling( aNewGameResponse.getBoStellingKey() );
	boStelling.normaliseer( getConfig().getAantalStukken() );
	getPartij().newGame( boStelling );
	return getPartijDocument( aNewGameResponse.getBoStellingKey() );
}
@Override
public PartijDocument getPartijDocument( BoStellingKey aStellingKey )
{
	BoStelling boStelling = createBoStelling( aStellingKey );
	// @@LOW Dit moet je in Partij doen!
	if ( ! getPartij().isBegonnen() && getPartij().isLegaleStelling( boStelling ) )
	{
		getPartij().newGame( boStelling );
	}
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
@Override
public PartijDocument zet( ZetResponse aZetResponse )
{
	BoStelling boStelling = createBoStelling( aZetResponse.getBoStellingKey() );
	// @@LOW Dit moet je in Partij doen!
	if ( ! getPartij().isBegonnen() && getPartij().isLegaleStelling( boStelling ) )
	{
		getPartij().newGame( boStelling );
	}

	getPartij().zet( aZetResponse.getNieuweZet() );
	return getPartijDocument( getPartij().getStand() );
}
@Override
public PartijDocument zetNaarBegin()
{
	BoStelling boStelling = getPartij().zetNaarBegin();
	return getPartijDocument( boStelling );
}
@Override
public PartijDocument zetTerug()
{
	BoStelling boStelling = getPartij().zetTerug();
	return getPartijDocument( boStelling );
}
@Override
public PartijDocument zetVooruit()
{
	BoStelling boStelling = getPartij().zetVooruit();
	return getPartijDocument( boStelling );
}
@Override
public PartijDocument zetNaarEinde()
{
	BoStelling boStelling = getPartij().zetNaarEinde();
	return getPartijDocument( boStelling );
}
}
