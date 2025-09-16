package pu.chessdatabase.bo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {
 
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(nullable = false, unique = true)
	private String title;
	
	@Column(nullable = false)
	private String author;

	public long getId()
	{
		return id;
	}
	public void setId( long aId )
	{
		id = aId;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle( String aTitle )
	{
		title = aTitle;
	}
	public String getAuthor()
	{
		return author;
	}
	public void setAuthor( String aAuthor )
	{
		author = aAuthor;
	}
}