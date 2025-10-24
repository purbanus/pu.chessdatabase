package pu.chessdatabase.service;

import pu.chessdatabase.web.NewGameResponse;
import pu.chessdatabase.web.ZetResponse;

public interface ChessDatabaseService
{
public abstract NewGameDocument newGame();
public abstract PartijDocument doNewGame( NewGameResponse aNewGameResponse );
public abstract PartijDocument getPartijDocument( BoStellingKey aBoStellingKey );
public abstract PartijDocument zet( ZetResponse aZetResponse );
public abstract PartijDocument zetNaarBegin();
public abstract PartijDocument zetTerug();
public abstract PartijDocument zetVooruit();
public abstract PartijDocument zetNaarEinde();
}
