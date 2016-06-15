package com.theironyard;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();



    public static Airplane selectEntry(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM fleet INNER JOIN users ON fleet.user_id = users.id WHERE users.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String model = results.getString("fleet.model");
            String manufacturer = results.getString("fleet.manufacturer");
            String serviceBranch = results.getString("fleet.service_branch");
            String role = results.getString("fleet.role");
            String unitCost = results.getString("fleet.unit_cost");
            return new Airplane(model, manufacturer, serviceBranch, role, unitCost);
        }
        return null;
    }

    public static void insertEntry(Connection conn, String model, String manufacturer, String serviceBranch, String role, String unitCost, int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO fleet VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, model);
        stmt.setString(2, manufacturer);
        stmt.setString(3, serviceBranch);
        stmt.setString(4, role);
        stmt.setString(5, unitCost);
        stmt.setInt(6, userId);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS fleet (id IDENTITY, model VARCHAR, manufacturer VARCHAR, service_branch VARCHAR, role VARCHAR, unit_cost VARCHAR, user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, password VARCHAR)");
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);


        Spark.staticFileLocation("public");
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    User user = users.get(username);
                    HashMap m = new HashMap();

                    if (username == null) {
                        return new ModelAndView(m, "home.html");
                    }
                    else {
                        int id = 1;
                        for (Airplane plane : user.fleet) {
                            plane.id = id;
                            id++;
                        }
                    }
                    m.put("username", user.username);
                    m.put("fleet", user.fleet);
                    return new ModelAndView(m, "fleet.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    if (username.equals("") || password.equals("")) {
                        response.redirect("/");
                        throw new Exception("Username and password required.");

                    }
                    User user = users.get(username);
                    if (user == null) {
                        user = new User(username, password);
                        users.put(username, user);
                    }
                    else if (!password.equals(user.password)) {
                        throw new Exception("Incorrect password.");
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";

                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/add-aircraft",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    User user = users.get(username);

                    String model = request.queryParams("model");
                    String manufacturer = request.queryParams("manufacturer");
                    String serviceBranch = request.queryParams("serviceBranch");
                    String role = request.queryParams("role");
                    String unitCost = (request.queryParams("cost"));


                    Airplane airplane = new Airplane(model, manufacturer, serviceBranch, role, unitCost);
                    user.fleet.add(airplane);

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/delete-aircraft",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    User user = users.get(username);

                    int id = Integer.valueOf(request.queryParams("id"));
                    if (id <= 0 || id - 1 > user.fleet.size()) {
                        throw new Exception("Invalid ID");
                    }
                    user.fleet.remove(id - 1);

                    response.redirect("/");
                    return "";
                }
        );
        Spark.get(
                "/edit",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("eid"));
                    Session session = request.session();
                    String username = session.attribute("username");
                    User user = users.get(username);

                    Airplane airplane = user.fleet.get(id - 1);

                    return new ModelAndView(airplane, "edit.html");

                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "edit-aircraft",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("eid"));
                    Session session = request.session();
                    String username = session.attribute("username");
                    User user = users.get(username);

                    String newManufacturer = request.queryParams("newManufacturer");
                    String newRole = request.queryParams("newRole");
                    String newServiceBranch = request.queryParams("newServiceBranch");
                    String newCost = request.queryParams("newCost");

                    Airplane airplane = user.fleet.get(id-1);

                    if (!newManufacturer.equals("")) {
                        airplane.manufacturer = newManufacturer;
                    }
                    if (!newRole.equals("")) {
                        airplane.role = newRole;
                    }
                    if (!newServiceBranch.equals("")) {
                        airplane.serviceBranch = newServiceBranch;
                    }
                    if (!newCost.equals("")) {
                        airplane.unitCost = newCost;
                    }

                    response.redirect("/");
                    return "";
                }
        );
    }
}
