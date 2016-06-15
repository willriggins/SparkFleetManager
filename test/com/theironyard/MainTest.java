package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

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

    @Test
    public void testEntries() throws SQLException {
        Connection conn = startConnection();

        Main.insertUser(conn, "Alice", "123");
        Main.insertUser(conn, "Bob", "123");

        User alice = Main.selectUser(conn, "Alice");
        User bob = Main.selectUser(conn, "Bob");

        Main.insertEntry(conn, "F-35C", "Lockheed Martin", "US Air Force", "Stealth Multirole Fighter", "116,000,000", alice.id);
        Main.insertEntry(conn, "F-18", "McDonnell Douglas", "US Navy", "Multirole Fighter", "58,000,000", bob.id);

        ArrayList<Airplane> ap = Main.selectEntries(conn);
        conn.close();

    }

    @Test
    public void testEditAndDelete() throws SQLException {
        Connection conn = startConnection();

        Main.insertUser(conn, "Alice", "123");
        User alice = Main.selectUser(conn, "Alice");
        Main.insertEntry(conn, "F-35C", "Lockheed Martin", "US Air Force", "Stealth Multirole Fighter", "116,000,000", alice.id);
        Main.updateEntry(conn, "F-16", "General Dynamics", "US Air Force", "Air Superiority Fighter", "14,600,000", alice.id);
        Main.deleteEntry(conn, alice.id);
    }

}