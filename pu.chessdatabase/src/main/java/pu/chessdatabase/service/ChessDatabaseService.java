package pu.chessdatabase.service;

import pu.chessdatabase.web.GameResponse;
import pu.chessdatabase.web.NewGameResponse;
import pu.chessdatabase.web.ZetResponse;

public interface ChessDatabaseService
{
public abstract PartijDocument newGame( NewGameResponse aNewGameResponse );
public abstract PartijDocument getPartijDocument( BoStellingKey aBoStellingKey );
public abstract PartijDocument zet( ZetResponse aZetResponse );
}
