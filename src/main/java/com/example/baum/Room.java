package com.example.baum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Room class represents a room or location.
 */
public class Room {
    private final int id;
    private final String name;

    /**
     * Constructs a Room object with the specified ID and name.
     *
     * @param id   The ID of the room.
     * @param name The name of the room.
     */
    public Room(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the ID of the room.
     *
     * @return The ID of the room.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the room.
     *
     * @return The name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name property of the room.
     *
     * @return The name property of the room.
     */
    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    /**
     * Returns a string representation of the room.
     *
     * @return A string representation of the room.
     */
    @Override
    public String toString() {
        return name;
    }
}
