package pu.chessdatabase.dbs;

import pu.services.Range;

public interface Constants
{
public static final String DATABASE_NAME_PIPO = "dbs/Pipo";
public static final String DATABASE_NAME_PIPO4 = "dbs/Pipo4";
public static final String DATABASE_NAME_5PIECES = "dbs/Pipo5";
public static final String PREFIX_TEST_DATABASE = "dbs/Pipo";
public static final int MAX_WK = 10;
public static final int MAX_STUK = 64;
public static final int MAX_PION = 64;
public static final int MAX_AANZET = 2;
public static final Range WK_VELD_RANGE = new Range( 0, MAX_WK - 1 );
public static final Range STUK_VELD_RANGE = new Range( 0, MAX_STUK - 1 );
public static final Range PION_VELD_RANGE = new Range( 0, MAX_PION - 1 );
}
