package com.example.baum.student;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import com.example.baum.company.Company;
import com.example.baum.course.Course;

/**
 * The Student class represents a student with relevant information such as ID,
 * name, surname, Java skills,
 * enrolled course, and associated company.
 */
public class Student {
    private final int id;
    private String name;
    private String surname;
    private int javaskills;
    private Course course;
    private Company company;

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
     * Sets the name of the student.
     *
     * @param name The name of the student.
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets the surname of the student.
     *
     * @param surname The surname of the student.
     */
    public void setSurname(String surname) {
        this.surname = surname;
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
     * Sets the Java skills level of the student.
     *
     * @param javaskills The Java skills level of the student.
     */
    public void setJavaskills(int javaskills) {
        this.javaskills = javaskills;
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
     * Returns the enrolled course of the student.
     *
     * @return The enrolled course of the student.
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Sets the enrolled course of the student.
     *
     * @param course The enrolled course of the student.
     */
    public void setCourse(Course course) {
        this.course = course;
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

    /**
     * Returns the associated company for the student.
     *
     * @return The associated company for the student.
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Sets the associated company for the student.
     *
     * @param company The associated company for the student.
     */
    public void setCompany(Company company) {
        this.company = company;
    }
}
