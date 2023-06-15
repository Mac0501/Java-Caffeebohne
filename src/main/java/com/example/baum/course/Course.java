package com.example.baum.course;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import com.example.baum.room.Room;

/**
 * The Course class represents a course with its ID, name, and assigned room.
 */
public class Course {
    private final int id;
    private final String name;
    private final ObjectProperty<Room> room;

    /**
     * Constructs a new Course object with the specified ID, name, and room.
     *
     * @param id   The ID of the course.
     * @param name The name of the course.
     * @param room The assigned room for the course.
     */
    public Course(int id, String name, Room room) {
        this.id = id;
        this.name = name;
        this.room = new SimpleObjectProperty<>(room);
    }

    /**
     * Returns the ID of the course.
     *
     * @return The ID of the course.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the course.
     *
     * @return The name of the course.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the assigned room for the course.
     *
     * @return The assigned Room object.
     */
    public Room getRoom() {
        return room.get();
    }

    /**
     * Returns the name of the assigned room for the course.
     *
     * @return The name of the assigned room.
     */
    public String getRoomName() {
        return room.getName();
    }

    /**
     * Returns the ObjectProperty representing the assigned room for the course.
     *
     * @return The ObjectProperty of Room.
     */
    public ObjectProperty<Room> roomProperty() {
        return room;
    }

    /**
     * Returns the StringProperty representing the name of the course.
     *
     * @return The StringProperty of the course name.
     */
    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    /**
     * Returns a string representation of the course.
     *
     * @return The string representation of the course (name).
     */
    @Override
    public String toString() {
        return name;
    }
}
