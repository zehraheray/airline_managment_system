package model;

public class Passenger extends Person{
    private String id;
    private String name;
    private String surname;
    private String contactInfo;
    public Passenger(String id, String name, String surname, String contactInfo) {
        super(id, name, surname, contactInfo);
    }

    @Override
    public String toString() {

        return "Passenger: " + this.id +" "+ this.name +" "+ this.surname +" "+ this.contactInfo ;
    }

    
}