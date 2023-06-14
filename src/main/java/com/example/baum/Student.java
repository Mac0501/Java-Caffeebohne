package com.example.baum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The Student class represents a student with relevant information such as ID,
 * name, surname, Java skills,
 * enrolled course, and associated company.
 */
public class Student {
    private final int id;
    private final String name;
    private final String surname;
    private final int javaskills;
    private final Course course;
    private final Company company;

    /**
     * Constructs a Student object with the specified parameters.
     *
     * @param id         The ID of the student.
     * @param name       The name of the student.
     * @param surname    The surname of the student.
     * @param javaskills The Java skills level of the student.
     * @param course     The enrolled course of the student.
     * @param company    The associated company of the student.
     */
    public Student(int id, String name, String surname, int javaskills, Course course, Company company) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.javaskills = javaskills;
        this.course = course;
        this.company = company;
    }

    /**
     * Returns the ID of the student.
     *
     * @return The ID of the student.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the student.
     *
     * @return The name of the student.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the surname of the student.
     *
     * @return The surname of the student.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Returns the Java skills level of the student.
     *
     * @return The Java skills level of the student.
     */
    public int getJavaskills() {
        return javaskills;
    }

    /**
     * Returns a StringProperty containing the name of the student.
     *
     * @return The StringProperty of the student's name.
     */
    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    /**
     * Returns the name of the enrolled course for the student.
     *
     * @return The name of the enrolled course, or an empty string if no course is
     *         enrolled.
     */
    public String getCourseName() {
        return course != null ? course.getName() : "";
    }

    /**
     * Returns the name of the associated company for the student.
     *
     * @return The name of the associated company, or an empty string if no company
     *         is associated.
     */
    public String getCompanyName() {
        return company != null ? company.getName() : "";
    }
}
