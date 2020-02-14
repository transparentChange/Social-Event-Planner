import java.util.ArrayList;

public class Student {
    public Student(String name, int studentNumber) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.partners = new ArrayList<Student>();
        this.hasPaid = false;
    }

    public Student(String name, int studentNumber, ArrayList<Student> partners) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.partners = partners;
        this.hasPaid = false;
    }

    private String name;
    private int studentNumber;
    private ArrayList<Student> partners;
    private boolean hasPaid;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public ArrayList<Student> getPartners() {
        return partners;
    }

    public void setPartners(ArrayList<Student> partners) {
        this.partners = partners;
    }

    public boolean isHasPaid() {
        return hasPaid;
    }

    public void setHasPaid(boolean hasPaid) {
        this.hasPaid = hasPaid;
    }
}
