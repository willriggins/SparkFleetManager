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
                        System.out.println("placeholder is here");
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
//                    String inService = request.queryParams("inService");
                    int cost = Integer.valueOf(request.queryParams("cost"));

                    Airplane airplane = new Airplane(model, manufacturer, serviceBranch, role, true, cost);
                    user.fleet.add(airplane);

                    response.redirect("/");
                    return "";


                }
        );
    }
}