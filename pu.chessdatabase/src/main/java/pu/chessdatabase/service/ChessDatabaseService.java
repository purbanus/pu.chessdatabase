package pu.chessdatabase.service;

import pu.chessdatabase.web.NewGameResponse;

public interface ChessDatabaseService
{
public abstract void newGame( NewGameResponse newGameResponse );
public abstract PartijDocument getPartijDocument( NewGameResponse aNewGameResponse );
}
