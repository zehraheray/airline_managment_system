package model;

public abstract class Person {
    private String id;
    private String name;
    private String surname;
    private String contactInfo;
    public Person(String id, String name, String surname, String contactInfo) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.contactInfo = contactInfo;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getContactInfo() {
        return contactInfo;
    }
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    
} 

