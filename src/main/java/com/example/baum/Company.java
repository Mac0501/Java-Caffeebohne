package com.example.baum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Company {
    private final int id;
    private final String name;

    public Company(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
}
