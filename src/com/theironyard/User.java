package com.theironyard;

import java.util.ArrayList;

/**
 * Created by will on 6/10/16.
 */
public class User {
    String username;
    String password;
    ArrayList<Airplane> fleet = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
