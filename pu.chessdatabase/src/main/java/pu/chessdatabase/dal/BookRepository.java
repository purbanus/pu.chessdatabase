package pu.chessdatabase.dal;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pu.chessdatabase.bo.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> 
{
List<Book> findByTitle(String title);
}