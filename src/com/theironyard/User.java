package com.theironyard;

import java.util.ArrayList;

/**
 * Created by will on 6/10/16.
 */
public class User {
    int id;
    String username;
    String password;
    ArrayList<Airplane> fleet = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
