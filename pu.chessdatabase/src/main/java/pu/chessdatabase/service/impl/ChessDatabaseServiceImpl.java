package pu.chessdatabase.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pu.chessdatabase.bo.BoStelling;
import pu.chessdatabase.bo.Kleur;
import pu.chessdatabase.bo.speel.Partij;
import pu.chessdatabase.service.ChessDatabaseService;
import pu.chessdatabase.service.PartijDocument;
import pu.chessdatabase.web.NewGameResponse;

import lombok.Data;

@Data
@Service
public class ChessDatabaseServiceImpl implements ChessDatabaseService
{
@Autowired private Partij partij;
@Override
public void newGame( NewGameResponse aNewGameResponse )
{
	BoStelling stelling = createBoStelling( aNewGameResponse );
	partij.newGame( stelling );
}
@Override
public PartijDocument getPartijDocument( NewGameResponse aNewGameResponse )
{
	BoStelling stelling = createBoStelling( aNewGameResponse );
	if ( ! partij.isBegonnen() && partij.isLegaleStelling( stelling ) )
	{
		partij.newGame( stelling );
	}
	return PartijDocument.builder()
		.stelling( stelling )
		.resultaat( partij.getResultaatRecord() )
		.zetten( partij.getPartijReport().getZetten() )
		.gegenereerdeZetten( partij.getGegenereerdeZetten() )
		.build();
}
BoStelling createBoStelling( NewGameResponse aNewGameResponse )
{
	BoStelling stelling = BoStelling.builder()
		.wk( partij.asciiToVeld( aNewGameResponse.getWk() ) )
		.zk( partij.asciiToVeld( aNewGameResponse.getZk() ) )
		.s3( partij.asciiToVeld( aNewGameResponse.getS3() ) )
		.s4( partij.asciiToVeld( aNewGameResponse.getS4() ) )
		.aanZet( Kleur.fromString( aNewGameResponse.getAanZet() ) )
		.build();
	return stelling;
}
}
