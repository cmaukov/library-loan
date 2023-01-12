package com.bastechpro.libraryloan;
/* library-loan
 * @created 01/11/2023
 * @author Konstantin Staykov
 */

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class MySqlBookDAO implements BookDAO {
    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private static final List<Book> EMPTY = new ArrayList<>();

    @Override
    public void setup() throws Exception {

    }

    @Override
    public void connect() throws Exception {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "dbuser", "dbuser");
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    @Override
    public long insertBook(Book book) {
        try {
            long id = dbAccess.insert(connection, "INSERT INTO Books (name, authors, publishedYear, available) VALUES (?, ?, ?, ?)",
                    new ScalarHandler<BigInteger>(), book.getName(), book.getAuthors(), book.getPublishedYear(), book.isAvailable()).longValue();
            return id;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return -1L;
    }

    @Override
    public boolean updateBook(Book book) {
        return false;
    }

    @Override
    public boolean deleteBook(Book book) {
        return false;
    }

    @Override
    public List<Book> findBookByProperty(BookSearchType searchType, Object value) {
        String whereClause = "";
        String valueClause = "";

        switch (searchType) {
            case AUTHOR -> {
                whereClause = "authors LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case AVAILABLE -> {
                whereClause = "available = ?";

//                valueClause = value.toString().equals("true")?"1":"0";
                valueClause = value.toString();
            }
            case ID -> {
                whereClause = "uniqueID = ?";
                valueClause = value.toString();
            }
            case NAME -> {
                whereClause = "name LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case PUBLISHED_YEAR -> {
                whereClause = "publishedYear = ?";
                valueClause = value.toString();
            }
            default -> System.out.println("Unknown search type");
        }

        try {
            return dbAccess.query(connection, "SELECT * FROM Books WHERE " + whereClause, new BeanListHandler<>(Book.class), valueClause);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY;
    }

    @Override
    public List<Book> findAll() {
        return null;
    }


}
