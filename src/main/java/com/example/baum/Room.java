package com.example.baum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {
    private final int id;
    private final String name;

    public Room(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
}
