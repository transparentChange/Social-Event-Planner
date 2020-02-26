import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Student {
    private String name;
    private String id;
    private ArrayList<Student> partners;
    private ArrayList<Student> blacklist;
    private ArrayList<String> accommodations;
    private boolean hasPaid;
    private String password;
    private String grade;
    private BufferedImage profilePic;
    
    public Student(String name, String studentNumber) {
        this.name = name;
        this.id = studentNumber;
        this.grade = "";
        this.password = "";
        this.partners = new ArrayList<Student>();
        this.hasPaid = false;
        this.accommodations = new ArrayList<String>();
        this.blacklist = new ArrayList<Student>();
    }
    
    public Student(String name, String studentNumber, ArrayList<Student> partners) {
        this.name = name;
        this.id = studentNumber;
        this.grade = "";
        this.password = "";
        this.partners = partners;
        this.hasPaid = false;
        this.accommodations = new ArrayList<String>();
        this.blacklist = new ArrayList<Student>();
    }

    public Student(String name, String id, String grade, String password) {
        this.name = name;
        this.id = id;
        this.grade = grade;
        this.password = password;
        this.partners = new ArrayList<Student>();
        this.hasPaid = false;
        this.accommodations = new ArrayList<String>();
        this.blacklist = new ArrayList<Student>();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGrade() {
    	return grade;
    }
    
    public ArrayList<Student> getPartners() {
        return partners;
    }

    public void setPartners(ArrayList<Student> partners) {
        this.partners = partners;
    }

    public boolean hasPaid() {
        return this.hasPaid;
    }

    public void setPaid(boolean hasPaid) {
        this.hasPaid = hasPaid;
    }
    
    public String getPassword() {
    	return this.password;
    }

    public BufferedImage getPicture() {
        return profilePic;
    }

    public void setPicture(BufferedImage image){
        this.profilePic = image;
    }

    public ArrayList<String> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(ArrayList<String> accommodations) {
        this.accommodations = accommodations;
    }

    public ArrayList<Student> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(ArrayList<Student> blacklist) {
        this.blacklist = blacklist;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Student) && (((Student) obj).getId().equals(this.id));
    }
}
