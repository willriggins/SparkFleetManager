package com.theironyard;

/**
 * Created by will on 6/10/16.
 */
public class Airplane {
    int id;
    String model;
    String manufacturer;
    String serviceBranch;
    String role;
    String unitCost;



    public Airplane(String model, String manufacturer, String serviceBranch, String role, String unitCost) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.serviceBranch = serviceBranch;
        this.role = role;
        this.unitCost = unitCost;
    }

    public Airplane(int id, String model, String manufacturer, String serviceBranch, String role, String unitCost) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.serviceBranch = serviceBranch;
        this.role = role;
        this.unitCost = unitCost;
    }
}
