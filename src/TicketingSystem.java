import java.awt.event.*;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class TicketingSystem extends JPanel{
    private ArrayList<Student> students;
    private ArrayList<Table> tables;
    private JTextField gradeField;
    private JTextField payField;
    private LoginPanel loginPanel;
    private TicketPanel ticketPanel;
    
    public TicketingSystem(ArrayList<Student> students, ArrayList<Table> tables) {
    	super(new GridBagLayout()); //might be the wrong layout for this implementation
        this.students = students;
        this.tables = tables;
        this.loginPanel = new LoginPanel();
        add(loginPanel);
        add(ticketPanel);
    }

    private class LoginPanel extends JPanel{
        LoginPanel(){
            // Login stuff here
            // or override paint component for graphics (preferred)
            // when student logs in
            // setSelectedStudent(selectedStudent);
            // resetStuff
        }

        private void setSelectedStudent(Student selectedStudent){
            ticketPanel = new TicketPanel(selectedStudent);
            loginPanel.setVisible(false);
            ticketPanel.setVisible(true);
        }

    }

    private class TicketPanel extends JPanel{
        private Student selectedStudent;
        TicketPanel(Student student){
            this.selectedStudent = student;
            // Ticketing stuff here
            // or override paint component for graphics (preferred)
        }
    }

    public class txtInputListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //if event source or something = X button;
        }
    }
    private void addStudent(Student student){
        this.students.add(student);
    }

    private void removeStudent(Student student){
        this.students.remove(student);
    }

}
