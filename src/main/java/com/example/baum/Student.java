package com.example.baum;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final int id;
    private final String name;
    private final String surname;
    private final int javaskills;
    private final Course course;
    private final Company company;

    public Student(int id, String name, String surname, int javaskills, Course course, Company company) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.javaskills = javaskills;
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

    public int getJavaskills() {
        return javaskills;
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
