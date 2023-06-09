package com.example.baum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final int id;
    private final String name;
    private final String surname;
    private final Course course;
    private final Company company;

    public Student(int id, String name, String surname, Course course, Company company) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.course = course;
        this.company = company;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public String getCourseName() {
        return course != null ? course.getName() : "";
    }

    public String getCompanyName() {
        return company != null ? company.getName() : "";
    }


}
