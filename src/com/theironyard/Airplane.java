package com.theironyard;

/**
 * Created by will on 6/10/16.
 */
public class Airplane {
    String model;
    String manufacturer;
    String serviceBranch;
    String role;
    String unitCost;
    int id;


    public Airplane(String model, String manufacturer, String serviceBranch, String role, String unitCost) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.serviceBranch = serviceBranch;
        this.role = role;
        this.unitCost = unitCost;

    }
}
