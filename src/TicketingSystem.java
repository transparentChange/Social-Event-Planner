import java.awt.event.*;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

public class TicketingSystem extends JPanel{
    private ArrayList<Student> students;
    private ArrayList<Table> tables;
    private JTextField gradeField;
    private JTextField payField;
    private LoginPanel loginPanel;
    private TicketPanel ticketPanel = new TicketPanel(null);
    private File loginCredentials;
    
    public TicketingSystem(ArrayList<Student> students, ArrayList<Table> tables) {
    	super(new GridBagLayout()); //might be the wrong layout for this implementation
        this.students = students;
        this.tables = tables;

        loginCredentials = new File("loginCredentials.txt");

        try {
            initializeStudents();
        } catch(Exception e) {
            e.printStackTrace();
        }

        this.loginPanel = new LoginPanel(this.getHeight(),this.getWidth());

        add(loginPanel);
        add(ticketPanel);
    }

    private class LoginPanel extends JPanel implements ActionListener {
        //private StudentLoginButton studentButton;
        private JTextField idField;
        private JTextField nameField;
        private JTextField passwordField;
        private StudentLoginButton loginButton;
        private JButton createAccountButton;

        LoginPanel(int windowHeight, int windowWidth) {
            this.setFocusable(false);
            this.setBounds(windowWidth / 10, 0, windowWidth / 5, windowHeight);
            this.setOpaque(false);
            this.setLayout(new GridBagLayout());

            //studentButton = new StudentLoginButton(0, 0);
            createAccountButton = new JButton("Don't have an account? Click here to sign up.");
            createAccountButton.addActionListener(this);

            idField = new JTextField(20);
            passwordField = new JTextField(20);

            loginButton = new StudentLoginButton(0, 0);
            loginButton.addActionListener(this);

            nameField = new JTextField(20);

            //this.add(studentButton);
            this.add(idField);
            this.add(passwordField);
            this.add(loginButton);
            this.add(createAccountButton);
        }

        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            String inputId = idField.getText();
            String inputPassword = passwordField.getText();
            if ((source == loginButton) && (studentExists(inputId, inputPassword))) {
                if (!((StudentLoginButton) loginButton).isLoginButton()) {
                    String grade = "12";
                    students.add(new Student(nameField.getText(), inputId, grade,inputPassword));
                }
                //send to ticketing system
            } else if (source == createAccountButton) {
                this.add(nameField);
                ((StudentLoginButton) loginButton).switchButtonState();
            }

            revalidate();
            repaint();
        }

        private boolean studentExists(String id, String password) {
            for (int i = 0; i < students.size(); i++) {
                if ((students.get(i).getId().equals(id)) && (students.get(i).getPassword().equals(password))) {
                    return true;
                }
            }

            return false;
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

    private void initializeStudents() {
        try {
            Scanner input = new Scanner(loginCredentials);
            String id = "";
            String password = "";
            String grade = "";
            String studentInfo;
            String[] name = {};
            int lenString, countSpaces;
            int prevSpaceIndex;
            while (input.hasNext()) {
                studentInfo = input.nextLine();
                lenString = studentInfo.length();

                countSpaces = 0;
                int i;
                while (countSpaces != 3) {
                    i = 0;
                    if (studentInfo.charAt(i) == ' ') {
                        countSpaces++;
                        prevSpaceIndex = i;
                        if (countSpaces == 1) {
                            name = studentInfo.substring(0, i).split("_");
                        } else if (countSpaces == 2) {
                            id = studentInfo.substring(prevSpaceIndex + 1, i);
                        } else if (countSpaces == 3) {
                            grade = studentInfo.substring(prevSpaceIndex + 1, i);
                            password = studentInfo.substring(i + 1);
                        }
                    }
                    i++;
                }
                students.add(new Student(name[0] + " " + name[1], id, grade, password));
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
