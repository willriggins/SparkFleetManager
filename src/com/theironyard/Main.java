package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
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
                    if (username == null || password == null) {
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
