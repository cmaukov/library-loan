package com.bastechpro.libraryloan;
/* library-loan
 * @created 01/10/2023
 * @author Konstantin Staykov
 */

import java.util.List;

public interface BookDAO extends DAO {
    long insertBook(Book book);

    boolean updateBook(Book book);

    boolean deleteBook(Book book);

    List<Book> findBookByProperty(BookSearchType searchType, Object value);

    List<Book> findAll();
}
