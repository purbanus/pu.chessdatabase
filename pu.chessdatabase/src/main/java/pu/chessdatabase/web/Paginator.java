package pu.chessdatabase.web;

import java.util.ArrayList;
import java.util.List;

import pu.chessdatabase.dal.RowBounds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Paginator
{
public static final int PAGES_IN_PAGINATOR = 20;
public static final int FIRST_PAGE_ID = 1977;

@Data
@ToString
@Builder
@AllArgsConstructor
public static class Page
{
	private final int id;
	private final int offset;
	private final int limit;
	private final boolean active; 
}
static final Page NULL_PAGE = new Page( 1, 0, 0, true );
static final int ROWS_PER_PAGE = 1; 

private final int aantalRijen;
private final int activePageId;
private List<Page> pages;
private Page firstPage;
private Page previousPage;
private Page huidigePage;
private Page nextPage;
private Page lastPage;

public static RowBounds getStartingRowBounds()
{
	return new RowBounds( 0, ROWS_PER_PAGE );
}
public Paginator( int aAantalRijen, int aActivePageId )
{
	super();
	aantalRijen = aAantalRijen;
	activePageId = aActivePageId;
	maakPages();
}
void maakPages()
{
	List<Page> allPages = new ArrayList<>();
	if ( aantalRijen <= 0 )
	{
		firstPage = NULL_PAGE;
		lastPage = NULL_PAGE;
		previousPage = NULL_PAGE;
		nextPage = NULL_PAGE;
		huidigePage = NULL_PAGE;
		pages = new ArrayList<>();
		return;
	}
	for ( int x = FIRST_PAGE_ID; x < FIRST_PAGE_ID + aantalRijen; x += ROWS_PER_PAGE )
	{
		int pageId = x / ROWS_PER_PAGE;
		allPages.add( 
			Page.builder()
				.id( pageId )
				.offset( x )
				.limit( ROWS_PER_PAGE )
				.active( pageId == activePageId )
				.build()
			);
	}
	firstPage = allPages.get( 0 );
	lastPage = allPages.get( allPages.size() - 1 );

	// We gaan nu PAGES_IN_PAGINATOR pagina's uitkiezen
	pages = trimPageListDownTo( allPages );
	
	previousPage = pages.get( 0 );
	boolean thisPageIsActive = false;
	for ( Page page : pages )
	{
		if ( page.isActive() )
		{
			thisPageIsActive = true;
			huidigePage = page;
		}
		else if ( thisPageIsActive )
		{
			nextPage = page;
			break;
		}
		else
		{
			previousPage = page;
		}
	}
	// Als we niet in die else-if-tak hierboven terechtgekomen zijn, is nextPage nog null
	if ( nextPage == null )
	{
		nextPage = lastPage;
	}
}
private List<Page> trimPageListDownTo( List<Page> aPages )
{
	if ( aPages.size() <= PAGES_IN_PAGINATOR )
	{
		return aPages;
	}
	int activePageIndex = 0;
	for ( int x = 0; x < aPages.size(); x++ )
	{
		Page page = aPages.get( x );
		if ( page.isActive() )
		{
			activePageIndex = x;
			break;
		}
	}
	int startIndex = Math.max(  0,  activePageIndex - PAGES_IN_PAGINATOR / 2 );
	startIndex = Math.min(  startIndex,  aPages.size() - PAGES_IN_PAGINATOR );
	List<Page> newPages = aPages.subList( startIndex, startIndex + PAGES_IN_PAGINATOR );
	return newPages;
}
public RowBounds getHuidigeRowBounds()
{
	return new RowBounds( getHuidigePage().getOffset(), getHuidigePage().getLimit() );
}
}
