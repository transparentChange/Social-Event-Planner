import java.util.ArrayList;

public class Table {
    private int size;
    private ArrayList<Student> students;
    private int x;
    private int y;

    Table(int size){
        this.size = size;
        this.students = new ArrayList<Student>();
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void addStudent(Student student){
        students.add(student);
    }

    public void removeStudent(Student s){
        students.remove(s);
    }

    public boolean isFull(){
        if (this.students.size() >= this.size){
            return true;
        } else {
            return false;
        }
    }
}
