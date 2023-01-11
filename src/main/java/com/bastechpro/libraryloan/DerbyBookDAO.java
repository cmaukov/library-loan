package com.bastechpro.libraryloan;
/* library-loan
 * @created 01/10/2023
 * @author Konstantin Staykov
 */

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DerbyBookDAO implements BookDAO {
    private Connection connection;

    private QueryRunner dbAccess = new QueryRunner();

    private static final List<Book> EMPTY = new ArrayList<>();

    @Override
    public void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:derby:books.db;create=true");

        dbAccess.update(connection, "CREATE TABLE Books ("
                + "uniqueID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                + "name VARCHAR(30), authors VARCHAR(100), publishedYear INTEGER, available BOOLEAN"
                + ")");
    }

    @Override
    public void connect() throws Exception {
        connection = DriverManager.getConnection("jdbc:derby:books.db");
    }

    @Override
    public void close() throws Exception {
        connection.close();
        try {
            DriverManager.getConnection("jdbc:derby:books.db;shutdown=true");
        }
        catch (Exception e) {}
    }

    @Override
    public long insertBook(Book book) {

        try {
            long id = dbAccess.insert(connection, "INSERT INTO Books (name, authors, publishedYear, available) VALUES (?, ?, ?, ?)",
                    new ScalarHandler<BigDecimal>(), book.getName(), book.getAuthors(), book.getPublishedYear(), book.isAvailable()).longValue();
            return id;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return -1L;
    }

    @Override
    public boolean updateBook(Book book) {

        try {
            dbAccess.update(connection, "UPDATE Books SET name=?, authors=?, publishedYear=?, available=? WHERE uniqueID=?",
                    book.getName(), book.getAuthors(), book.getPublishedYear(), book.isAvailable(), book.getUniqueID());
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteBook(Book book) {
        try {
            dbAccess.update(connection, "DELETE FROM Books WHERE uniqueID=?", book.getUniqueID());
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
            return dbAccess.query(connection, "SELECT * FROM Books WHERE " + whereClause, new BeanListHandler<Book>(Book.class), valueClause);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY;
    }

    @Override
    public List<Book> findAll() {
        try {
            return dbAccess.query(connection, "SELECT * FROM Books", new BeanListHandler<Book>(Book.class));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return EMPTY;
    }

}
