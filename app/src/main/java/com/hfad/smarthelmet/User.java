package com.hfad.smarthelmet;

public class User {
    public String id;
    public String name;
    public int age;
    public String bloodtype;
    public String sex;
    public String contactPerson;
    public String contactNumber;
    public String medicalCondition;
    public String email;
    public String password;

    //Constructor with two parameters
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor containing all parameter
    public User(String id, String name, int age, String bloodtype, String sex, String contactPerson,
                String contactNumber, String medicalCondition, String email, String password) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.bloodtype = bloodtype;
        this.sex = sex;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.medicalCondition = medicalCondition;
        this.email = email;
        this.password = password;
    }

    public User(String name, int age, String bloodType, String sex, String contactPerson, String contactNumber,
                String medicalCondition, String email, String password) {
        this.name = name;
        this.age = age;
        this.bloodtype = bloodType;
        this.sex = sex;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.medicalCondition = medicalCondition;
        this.email = email;
        this.password = password;
    }

    // Add toString method for logging
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", bloodtype='" + bloodtype + '\'' +
                ", sex='" + sex + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", medicalCondition='" + medicalCondition + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
