package com.example.baum;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Course {
    private final int id;
    private final String name;
    private final ObjectProperty<Room> room;

    public Course(int id, String name, Room room) {
        this.id = id;
        this.name = name;
        this.room = new SimpleObjectProperty<>(room);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Room getRoom() {
        return room.get();
    }

    public String getRoomName() {
        return room.getName();
    }

    public ObjectProperty<Room> roomProperty() {
        return room;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
    @Override
    public String toString() {
        return name;
    }
}
