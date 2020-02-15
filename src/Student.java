import java.util.ArrayList;

public class Student {
    private String name;
    private String studentNumber;
    private ArrayList<Student> partners;
    private boolean hasPaid;
    private String password;
    
    public Student(String name, String studentNumber) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.partners = new ArrayList<Student>();
        this.hasPaid = false;
    }

    public Student(String name, String studentNumber, String password) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.partners = new ArrayList<Student>();
        this.hasPaid = false;
        this.password = password;
    }
    
    public Student(String name, String studentNumber, ArrayList<Student> partners) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.partners = partners;
        this.hasPaid = false;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return studentNumber;
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
    
    @Override
    public boolean equals(Object obj) {
    	if ((obj instanceof Student) && (((Student) obj).getId().equals(this.studentNumber))) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
