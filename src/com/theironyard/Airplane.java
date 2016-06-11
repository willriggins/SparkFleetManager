package com.theironyard;

/**
 * Created by will on 6/10/16.
 */
public class Airplane {
    String model;
    String manufacturer;
    String serviceBranch;
    String role;
    boolean inService;
    int cost;

    public Airplane(String model, String manufacturer, String serviceBranch, String role, boolean inService, int cost) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.serviceBranch = serviceBranch;
        this.role = role;
        this.inService = inService;
        this.cost = cost;
    }
}
