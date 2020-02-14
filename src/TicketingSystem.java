import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class TicketingSystem extends JPanel implements MouseListener {
    private ArrayList<Student> students;
    private ArrayList<Table> tables;

    //JPanel floorPlan = new FloorPlanSystem();

    public TicketingSystem(ArrayList<Student> students, ArrayList<Table> tables){
        this.addMouseListener(this);
        this.students = students;
        this.tables = tables;
        //this.add(floorPlan);
    }

    private void addStudent(Student student){
        this.students.add(student);
    }

    private void removeStudent(Student student){
        this.students.remove(student);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        setDoubleBuffered(true);



        //the greatest gui goes here
        g.fillRect(50,50,50,50);
        /*
        if add student
            get info
            find friends by id
            if found add to friends list in student
            else spit out some sort of error that friend has to register before you can add them
            or we can have some sort of a temp user name storage and say that friend hasn't registered but if they do then they will be considered
         */



        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(e.getPoint().toString());
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
