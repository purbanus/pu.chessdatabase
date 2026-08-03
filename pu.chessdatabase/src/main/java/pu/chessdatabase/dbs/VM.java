package pu.chessdatabase.dbs;

import static pu.chessdatabase.dbs.Constants.*;
import static pu.chessdatabase.dbs.Lokatie.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.stereotype.Component;

import pu.chessdatabase.bo.Config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * ******************************************************************************
Systeem: Chess Data base voor 4 stukken
Module : VM - Virtual memory manager
Doel   : Implementeren van een virtual memory systeem voor de chess
         data base. Alle lees- en schrijfbewerkingen op de data base
         gaan door VM, die kijkt of het record nog in zijn cache zit.
         Een record is een byte, en de database bestaat uit 5,12 Mrecords.
******************************************************************************
 */
@Component
@Data
public class VM
{
public static final int VERLIES_OFFSET   = 0x80;
public static final int VM_SCHAAK        = 0x80;
public static final int VM_REMISE        = 0x00;
public static final int VM_ILLEGAAL      = 0xFF;
public static final String [] NOTATIE = new String [] {
	"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
	"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
	"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
	"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
	"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
	"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
	"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
	"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
};
/*------ Witte koning ------------*/
@SuppressWarnings( "unused" )
private static final String [] REP_WK_ZONDER_PIONNEN = {"a1", "b1", "c1", "d1", "b2", "c2", "d2", "c3", "d3", "d4" };

/*------ Andere stukken -----------*/
@SuppressWarnings( "unused" )
private static final String [] Rep_STUK = {
		"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
		"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
		"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
		"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
		"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
		"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
		"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
		"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
	};

/*------ Aan zet -----------------*/
@SuppressWarnings( "unused" )
private static final String [] REP_AZ = { "W", "Z" };

/**
 * ------- Veld naar alfa ----------------------------------
 */
public static String veldToAlfa( int aVeld )
{
	if ( aVeld < 0 || aVeld >= 64 )
	{
		throw new RuntimeException( "Veld moet tussen 0 en 63 liggen: " + aVeld );
	}
	return NOTATIE[aVeld];
}
/**
 * ------- alfa naar veld -----------------------------------
 */
public static int alfaToVeld( String aAlfaVeld )
{
	if ( aAlfaVeld.length() != 2 )
	{
		throw new RuntimeException( "AlfaVeld moet 2 lang zijn: " + aAlfaVeld );
	}
	String capAlfaVeld = aAlfaVeld.toUpperCase();
	char capAlfaIndex0 = capAlfaVeld.charAt( 0 );
	if ( capAlfaIndex0 < 'A' || capAlfaIndex0 > 'H' )
	{
		throw new RuntimeException( "De kolom (de letter) moet tussen A en H liggen" );
	}
	char capAlfaIndex1 = capAlfaVeld.charAt( 1 );
	if ( capAlfaIndex1 < '1' || capAlfaIndex1 > '8' )
	{
		throw new RuntimeException( "De rij (het cijfer) moet tussen 1 en 8 liggen" );
	}
	return capAlfaVeld.charAt( 0 ) - 'A' + 8 * ( capAlfaVeld.charAt( 1 ) - '1' );
}

private final Config config;
@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PACKAGE ) 
private PageDescriptorTable pageDescriptorTable/* = new PageDescriptorTable( config.getAantalStukken() )*/;
@Getter( AccessLevel.PACKAGE ) 
@Setter( AccessLevel.PACKAGE ) 
@EqualsAndHashCode.Exclude
@ToStringExclude
private Cache cache;

private String databaseName = null;
@Setter( AccessLevel.PACKAGE ) 
private File databaseFile;
@Getter( AccessLevel.PUBLIC ) 
@Setter( AccessLevel.PRIVATE ) 
private boolean open = false;
//@Autowired is overbodig zeggen ze
public VM( Config aConfig )
{
	super();
	config = aConfig;
	databaseFile = null;
}
PageSizeCalculator getPageSizeCalculator()
{
	return getConfig().getPageSizeCalculator();
}
RandomAccessFile getDatabase()
{
	return getCache().getDatabase();
}
void setDatabase( RandomAccessFile aRandomAccessFile )
{
	getCache().setDatabase( aRandomAccessFile );
}
public long getDatabaseSize()
{
	return getCache().getDatabaseSize();
}

public void switchConfig()
{
	setDatabaseName( null );
	if ( getDatabaseFile() == null || ! getDatabaseFile().exists() )
	{
		create();
	}
	else
	{
		open( "r" );
	}
}
public String getDatabaseName()
{
	if ( databaseName == null )
	{
		databaseName = getConfig().getDatabaseName();
	}
	return databaseName;
}
public File getDatabaseFile()
{
	if ( databaseFile == null )
	{
		createFile( getDatabaseName() );
	}
	return databaseFile;
}
public void setDatabaseName( String aDatabaseName )
{
	databaseName = aDatabaseName;
	databaseFile = null;
	if ( getCache() != null )
	{
		// @@HIGH Dit is dus de plek waar de database null wordt. Als je de cache daarna gaat gebruiken krijg je allemaal NPE's
		// Hier een oplossing voor verzinnen. Het gaat al mis als je een seek() probeerte te doen op de database (in getRawPageData)
		getCache().setDatabase( null );
	}
}
PageDescriptor getPageDescriptor( VMStelling aStelling )
{
	return getPageDescriptorTable().getPageDescriptor( aStelling );
}
public byte [] getPage( VMStelling aVmStelling )
{
	aVmStelling.checkStelling();
	return getCache().getPageFromDatabase( getPageDescriptor( aVmStelling ) );
}
/**
 *  ------------ Ophalen database record --------------
 */
public int get( VMStelling aVmStelling )
{
	// Dit is o.a. om de pageDescriptor goed te zetten. Gecheckt, ik zie niet wat dit uitmaakt
	// maar ik kan niet aantonen dat het fout gaat als je het weglaat. Voorlopig laten staan dus
    getPage( aVmStelling );
    
	PageDescriptor pageDescriptor = getPageDescriptor( aVmStelling );
    byte vmRec = getCache().getData( pageDescriptor, aVmStelling );
    return Byte.toUnsignedInt( vmRec );
}
/**
 * --------- Wegschrijven database record -----------
 */
public void put( VMStelling aVmStelling, int aDbsRec )
{
	// Dit is o.a. om de pageDescriptor goed te zetten. Gecheckt, ik zie niet wat dit uitmaakt
	// maar ik kan niet aantonen dat het fout gaat als je het weglaat. Voorlopig laten staan dus
    getPage( aVmStelling );
    
	PageDescriptor pageDescriptor = getPageDescriptor( aVmStelling );
    byte vmRec = (byte)( aDbsRec & 0xff );
    getCache().setData( pageDescriptor, aVmStelling, vmRec);
}
/**
 *  -------- Cache entry vrijmaken --------------------
 */
public void freeRecord( VMStelling aStelling )
{
	// En na de clear, page en pageDescriptor leegmaken?
	// - PD niet, die is permanent
	aStelling.checkStelling();
	PageDescriptor pageDescriptor = getPageDescriptor( aStelling );
	if ( pageDescriptor.getWaar() == InRam )
	{
		getCache().pageOut( pageDescriptor ); // Checkt of de page vuil is
		getCache().getCacheEntry( pageDescriptor ).setGeneratie( 0 );
	}
}
/**
 * -------- Hele cache vrijmaken ---------------------
 */
public void flush()
{
	getCache().flush();
}
/**
 *  --------- Sluiten van de database --------------
 */
public void close()
{
	setOpen( false );
	if ( getCache() != null && getDatabase() != null )
	{
		flush();
		try
		{
			getDatabase().close();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
		setDatabase( null );
		//databaseFile = null;
	}
}
void checkDatabaseFile( String aFileNaam )
{
	if ( ! getDatabaseFile().exists() )
	{
		throw new RuntimeException( "File bestaat niet: " + aFileNaam );
	}
	if ( ! getDatabaseFile().canRead() )
	{
		throw new RuntimeException( "Kan file niet lezen: " + aFileNaam );
	}
}
/**
 *  ----------- Openen van een database -------------
 */
public void open( String aMode ) 
{
	if ( getDatabaseName() == null || getDatabaseName().length() == 0 )
	{
		throw new RuntimeException( "Geen naam opgegeven voor de database" );
	}

	close();
	setDatabaseFile( new File( getDatabaseName() ) );
	checkDatabaseFile( getDatabaseName() ); // Throws RuntimeException-als er iets niet goed is
	RandomAccessFile database;
	try
	{
		/**Zie doc for mode = "rwd" or "rws". "rw" betekent volgens mij dat een update direct naar
		 * schijf wordt geschreven. "rwd" en "rws" cachen dat enigszins.
		 */
		database = new RandomAccessFile( getDatabaseFile(), aMode ); 
	}
	catch ( FileNotFoundException e )
	{
		throw new RuntimeException( e );
	}
    setCache( Cache.create( getConfig(), database ) );
	setPageDescriptorTable( PageDescriptorTable.create( getConfig() ) );
	setOpen( true );
}
/**
 *  ---------- Leegmaken cq creeren van de database -------
 */
void createFile( String aNaam )
{
	setDatabaseFile( new File( aNaam ) );
	if ( ! getDatabaseFile().exists() )
	{
		try
		{
			getDatabaseFile().createNewFile();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
}
public void create()
{
	if ( getDatabaseName() == null || getDatabaseName().length() == 0 )
	{
		throw new RuntimeException( "Geen naam opgegeven voor de database" );
	}
	if ( ! getDatabaseFile().canWrite() )
	{
		throw new RuntimeException( "Kan niet naar file schrijven: " + getDatabaseFile() );
	}
	close();
	createFile( getDatabaseName() );
	open( "rw" );
	initializeDatabase();
}
void initializeDatabase()
{
	getPageDescriptorTable().iterateOverAllPageDescriptors( this::initializeDatabasePage );
}
void initializeDatabasePage( VMStelling aVmStelling )
{
	PageDescriptor pageDescriptor = getPageDescriptor( aVmStelling );
	pageDescriptor.setCacheNummer( 1 );
	getCache().setVuil( pageDescriptor, true );
	getCache().pageOut( pageDescriptor );
}
void delete()
{
	close();
	if ( getDatabaseFile() != null )
	{
		if ( ! ( getDatabaseName().startsWith( PREFIX_TEST_DATABASE ) ) ) 
		{
			throw new RuntimeException( "Poging om een database te verwijderen <> " + PREFIX_TEST_DATABASE + "*: " + getDatabaseName() );
		}
		getDatabaseFile().delete();
	}
	setDatabaseFile( null );
	setDatabase( null );
}

}
