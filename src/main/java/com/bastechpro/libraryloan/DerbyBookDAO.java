package com.bastechpro.libraryloan;
/* library-loan
 * @created 01/10/2023
 * @author Konstantin Staykov
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DerbyBookDAO implements BookDAO {

    private Connection connection;


    private static final List<Book> EMPTY = new ArrayList<>();

    @Override
    public void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:derby:books.db;create=true");
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "BOOKS", null);
        if (tables.next()) {
            return;

        }
        PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE BOOKS ("
                + "uniqueID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                + "name VARCHAR(30), authors VARCHAR(100), publishedYear INTEGER, available BOOLEAN"
                + ")");
        preparedStatement.execute();
        System.out.println("Added db table.");
    }

    @Override
    public void connect() throws Exception {
        setup(); // create database if it doesn't exist
        connection = DriverManager.getConnection("jdbc:derby:books.db");
    }

    @Override
    public void close() throws Exception {
        connection.close();
        try {
            DriverManager.getConnection("jdbc:derby:books.db;shutdown=true");
        } catch (Exception e) {
        }
    }

    @Override
    public long insertBook(Book book) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BOOKS (name, authors, publishedYear, available) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, book.getName());
            preparedStatement.setString(2, book.getAuthors());
            preparedStatement.setInt(3, book.getPublishedYear());
            preparedStatement.setBoolean(4, book.isAvailable());
            preparedStatement.execute();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("No generated key");
                }
                long aLong = generatedKeys.getLong(1);
                book.setUniqueID(aLong);
                return aLong;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
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

            case ID -> {
                whereClause = "uniqueID = ?";
                valueClause = value.toString();
            }
            case NAME -> {
                whereClause = "name LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case AUTHOR -> {
                whereClause = "authors LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case PUBLISHED_YEAR -> {
                whereClause = "publishedYear = ?";
                valueClause = value.toString();
            }
            case AVAILABLE -> {
                whereClause = "available = ?";
                valueClause = value.toString();
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Books WHERE " + whereClause)) {
            preparedStatement.setString(1, valueClause);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                List<Book> books = new ArrayList<>();
                while (resultSet.next()) {
                    Book book = new Book();
                    book.setUniqueID(resultSet.getLong("uniqueID"));
                    book.setName(resultSet.getString("name"));
                    book.setAuthors(resultSet.getString("authors"));
                    book.setPublishedYear(resultSet.getInt("publishedYear"));
                    book.setAvailable(resultSet.getBoolean("available"));
                    books.add(book);
                }
                return books;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return EMPTY;
    }

    @Override
    public List<Book> findAll() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BOOKS");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                List<Book> books = new ArrayList<>();
                while (resultSet.next()) {
                    Book book = new Book();
                    book.setUniqueID(resultSet.getLong("uniqueID"));
                    book.setName(resultSet.getString("name"));
                    book.setAuthors(resultSet.getString("authors"));
                    book.setPublishedYear(resultSet.getInt("publishedYear"));
                    book.setAvailable(resultSet.getBoolean("available"));
                    books.add(book);
                }
                return books;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return EMPTY;
    }

}
