package com.example.baum;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final int id;
    private final String name;
    private final ObjectProperty<Course> course;
    private final ObjectProperty<Company> company;

    public Student(int id, String name, Course course, Company company) {
        this.id = id;
        this.name = name;
        this.course = new SimpleObjectProperty<>(course);
        this.company = new SimpleObjectProperty<>(company);
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

    public ObjectProperty<Course> courseProperty() {
        return course;
    }

    public ObjectProperty<Company> companyProperty() {
        return company;
    }
}
