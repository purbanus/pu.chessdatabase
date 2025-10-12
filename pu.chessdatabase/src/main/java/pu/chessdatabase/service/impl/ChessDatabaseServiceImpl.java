package pu.chessdatabase.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.service.BoStellingKey;
import pu.chessdatabase.service.ChessDatabaseService;
import pu.chessdatabase.service.PartijDocument;
import pu.chessdatabase.web.NewGameResponse;
import pu.chessdatabase.web.ZetResponse;

import lombok.Data;

@Data
@Service
public class ChessDatabaseServiceImpl implements ChessDatabaseService
{
@Autowired private Partij partij;
@Override
public PartijDocument newGame( NewGameResponse aNewGameResponse )
{
	BoStelling stelling = createBoStelling( aNewGameResponse.getBoStellingKey() );
	getPartij().newGame( stelling );
	return getPartijDocument( aNewGameResponse.getBoStellingKey() );
}
@Override
public PartijDocument getPartijDocument( BoStellingKey aStellingKey )
{
	BoStelling boStelling = createBoStelling( aStellingKey );
	// @@NOG Dit moet je in Partij doen!
	if ( ! getPartij().isBegonnen() && getPartij().isLegaleStelling( boStelling ) )
	{
		getPartij().newGame( boStelling );
	}
	return PartijDocument.builder()
		.wk( Partij.veldToHexGetal( aStellingKey.getWk() ) )
		.zk( Partij.veldToHexGetal( aStellingKey.getZk() ) )
		.s3( Partij.veldToHexGetal( aStellingKey.getS3() ) )
		.s4( Partij.veldToHexGetal( aStellingKey.getS4() ) )
		.aanZet( aStellingKey.getAanZet().getNormaleSpelling() )
		.stelling( boStelling )
		.resultaat( getPartij().getResultaatRecord() )
		.zetten( getPartij().getPartijReport().getZetten() )
		.gegenereerdeZetten( getPartij().getGegenereerdeZetten() )
		.build();
}
PartijDocument getPartijDocument( BoStelling aBoStelling )
{
	return getPartijDocument( aBoStelling.getBoStellingKey() );
}
BoStelling createBoStelling( BoStellingKey aBoStellingKey )
{
	BoStelling stelling = BoStelling.builder()
		.wk( aBoStellingKey.getWk() )
		.zk( aBoStellingKey.getZk() )
		.s3( aBoStellingKey.getS3() )
		.s4( aBoStellingKey.getS4() )
		.aanZet( aBoStellingKey.getAanZet() )
		.build();
	return stelling;
}
@Override
public PartijDocument zet( ZetResponse aZetResponse )
{
	BoStelling boStelling = createBoStelling( aZetResponse.getBoStellingKey() );
	// @@NOG Dit moet je in Partij doen!
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
