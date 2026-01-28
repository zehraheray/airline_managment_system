package model;
public class Staff extends Person {
    private String id;
    private String name;
    private String surname;
    private String contactInfo;
    public Staff(String id, String name, String surname, String contactInfo) {
        super(id, name, surname, contactInfo);
    }

    @Override
    public String toString() {

        return "Staff: " + this.id +" "+ this.name +" "+ this.surname +" "+ this.contactInfo ;
    }

}
