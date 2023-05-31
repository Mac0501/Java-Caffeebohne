package com.example.baum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final int id;
    private final String name;
    private final String className;
    private final String company;

    public Student(int id, String name, String className, String company) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.company = company;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getCompany() {
        return company;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public StringProperty classProperty() {
        return new SimpleStringProperty(className);
    }

    public StringProperty companyProperty() {
        return new SimpleStringProperty(company);
    }
}
