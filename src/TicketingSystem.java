import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

public class TicketingSystem extends JPanel{
    private ArrayList<Student> students;
    private ArrayList<Table> tables;
    private JTextField payField;
    private LoginPanel loginPanel;
    private JPanel colourPanel;
    private TicketPanel ticketPanel = new TicketPanel(null);
    private File loginCredentials;
    private final static int WINDOW_WIDTH = 1366;
    private final static int WINDOW_HEIGHT = 768;
    
    public TicketingSystem(ArrayList<Student> students, ArrayList<Table> tables) {
        this.setLayout(new GridBagLayout());
    	this.students = students;
        this.tables = tables;
        //this.setBackground(Color.BLACK);
        
        loginCredentials = new File("loginCredentials.txt");

        try {
            initializeStudents();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        colourPanel = new JPanel();
        colourPanel.setBackground(Color.BLACK);
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(50, 50, 50, 50);
        loginPanel = new LoginPanel(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        //backPanel.add(colourPanel);
        colourPanel.add(loginPanel);
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        
        this.add(colourPanel, c);
    }
    
    private void toBorderLayout() {
    	this.setLayout(new BorderLayout());
    }

    private void showTicket(){
        this.remove(colourPanel);
        this.add(ticketPanel);
        ticketPanel.setVisible(true);
        ticketPanel.requestFocus();
        this.updateUI();
    }

    private void showLogin(){
        this.remove(ticketPanel);
        this.add(this.colourPanel);
        colourPanel.setVisible(true);
        colourPanel.requestFocus();
        this.updateUI();
    }

    private class LoginPanel extends JPanel implements ActionListener {
    	private JTextField idField;
    	private JTextField nameField;
    	private JTextField passwordField;
    	private StudentLoginButton loginButton;
    	private JButton createAccountButton;
        private JComboBox gradeOptions;

    	LoginPanel(int windowHeight, int windowWidth) {
    		this.setFocusable(false);
    		this.setOpaque(false);
    		
    		this.setLayout(new GridBagLayout());
    		this.setVisible(true);
            
    		//studentButton = new StudentLoginButton(0, 0);
    		createAccountButton = new JButton("Don't have an account? Click here to sign up.");
    		createAccountButton.addActionListener(this);
    		
            GridBagConstraints c = new GridBagConstraints();
            //c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(0, 0, 0, 0);
            
    		idField = new JTextField(20);
    		this.add(idField, c);
    		
    		c = new GridBagConstraints();
    		c.gridy = 1;
    		passwordField = new JTextField(20);
    		this.add(passwordField, c);
    		
    		loginButton = new StudentLoginButton(0, 0);
    		loginButton.addActionListener(this);
    		c.gridy = 4;
    		c.anchor = GridBagConstraints.LINE_START;
    		c.insets = new Insets(0, windowWidth / 20, 0, 0);
    		this.add(loginButton, c);
    		
    		c.insets = new Insets(100, 0, 0, 0);
    		c.gridy = 5;
    		this.add(createAccountButton, c);
    		
    		String[] grades = {"9", "10", "11", "12"};
    		nameField = new JTextField(20);
    		gradeOptions = new JComboBox(grades);

    		//this.add(studentButton);
    		
    		//this.add(loginButton);
    		//this.add(createAccountButton);

    	}

        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            String inputId = idField.getText();
            String inputPassword = passwordField.getText();
            int studentIndex = getStudentIndex(inputId, inputPassword);
            if ((source == loginButton) && (studentIndex != -1)
    				&& ((StudentLoginButton) loginButton).isLoginButton()) {
            	ticketPanel = new TicketPanel(students.get(studentIndex));
            	showTicket();
            } else if ((source == loginButton) && (!((StudentLoginButton) loginButton).isLoginButton())) {
    			students.add(new Student(nameField.getText(), inputId, 
    					gradeOptions.getSelectedItem().toString(), inputPassword));
    			writeLastStudent();
    			ticketPanel = new TicketPanel(students.get(students.size() - 1));
                showTicket();
            } else if (source == createAccountButton) {
            	GridBagConstraints c = new GridBagConstraints();
            	c.gridy = 2;
    			this.add(nameField, c);
    			
    			c.anchor = GridBagConstraints.LINE_START;
    			c.insets = new Insets(0, 50, 0, 0);
    			c.gridy = 3;
    			this.add(gradeOptions, c);
    			((StudentLoginButton) loginButton).switchButtonState();
    		}

            revalidate();
            repaint();
        }

        private int getStudentIndex(String id, String password) {
            for (int i = 0; i < students.size(); i++) {
                if ((students.get(i).getId().equals(id)) && (students.get(i).getPassword().equals(password))) {
                    return i;
                }
            }

            return -1;
        }
        
    	private void writeLastStudent() {
    		try {
    			FileWriter studentRecords = new FileWriter(loginCredentials, true);
    			PrintWriter output = new PrintWriter(studentRecords);
    			Student curStudent = students.get(students.size() - 1);
    			output.println();
    			output.print(curStudent.getName().replace(' ', '_') + " " + curStudent.getId() 
    					+ " " + curStudent.getGrade() + " " + curStudent.getPassword());
    			output.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }

    private class TicketPanel extends JPanel{
        private Student selectedStudent;
        TicketPanel(Student student){
            if (student != null) {
            	toBorderLayout();
                this.setLayout(new BorderLayout()); // change to GridBagLayout
                this.setVisible(true);
                this.selectedStudent = student;
                // Ticketing stuff here
                // or override paint component for graphics (preferred)
                JButton button = new JButton(student.getName());
                this.add(button, BorderLayout.LINE_START);
                this.repaint();
            }
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
            int prevSpaceIndex = 0;
            while (input.hasNext()) {
                studentInfo = input.nextLine();
                lenString = studentInfo.length();

                countSpaces = 0;
                int i = 0;
                while (countSpaces != 3) {
                    if (studentInfo.charAt(i) == ' ') {
                        countSpaces++;
                        if (countSpaces == 1) {
                            name = studentInfo.substring(0, i).split("_");
                        } else if (countSpaces == 2) {
                            id = studentInfo.substring(prevSpaceIndex + 1, i);
                        } else if (countSpaces == 3) {
                            grade = studentInfo.substring(prevSpaceIndex + 1, i);
                            password = studentInfo.substring(i + 1);
                        }
                        prevSpaceIndex = i;
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
