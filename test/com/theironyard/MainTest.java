package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Created by will on 6/14/16.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "123");
        User user = Main.selectUser(conn, "Alice");
        conn.close();
        assertTrue(user != null);

    }

    @Test
    public void testEntry() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "123");
        User user = Main.selectUser(conn, "Alice");
        Main.insertEntry(conn, "F/A-18", "McDonnell Douglas", "US Navy", "Fighter/Attack", "57,000,000", 1);
        Airplane ap = Main.selectEntry(conn, 1);
        conn.close();
        assertTrue(ap != null);
        assertTrue(ap.serviceBranch.equals("US Navy"));

    }

}