package com.example.baum.company;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a company in the application.
 */
public class Company {
    private final int id;
    private final String name;

    /**
     * Constructs a new Company object with the specified ID and name.
     *
     * @param id   The ID of the company.
     * @param name The name of the company.
     */
    public Company(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the ID of the company.
     *
     * @return The ID of the company.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the company.
     *
     * @return The name of the company.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the company.
     *
     * @return The name of the company.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a string property representing the name of the company.
     *
     * @return The string property representing the name of the company.
     */
    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
}