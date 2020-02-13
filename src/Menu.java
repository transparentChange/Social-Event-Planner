import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Menu extends JPanel implements MouseListener {
    ArrayList<Student> students = new ArrayList<Student>();
    ArrayList<Table> tables = new ArrayList<Table>();

    public Menu(){
        this.addMouseListener(this);
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
