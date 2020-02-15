import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Prom extends JFrame {
	static private TicketingSystem ticketingPanel;
	private ArrayList<Student> students;
	private ArrayList<Table> tables;
	private LoginPanel loginPanel;
	
	private File loginCredentials;
  // constants that hold game window size information
  private final static int WINDOW_WIDTH = 1366;
  private final static int WINDOW_HEIGHT = 768;
  
	// private FloorPlanSystem floorPlanPanel;
	
	public Prom() {
		super("Prom");
		
		this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		
		//loginButton.addActionListener(this);
        loginCredentials = new File("loginCredentials.txt");
        
		this.students = new ArrayList<Student>();
        this.tables = new ArrayList<Table>();
        
        try {
        	initializeStudents();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
		loginPanel = new LoginPanel(WINDOW_HEIGHT, WINDOW_WIDTH);
        
        // this.add(floorPlanPanel);
        //this.add(ticketingPanel);
        this.add(loginPanel);
        
        this.requestFocusInWindow();
        this.setVisible(true);
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
	
	static public int getWindowHeight() {
		return WINDOW_HEIGHT;
	}
	
	static public int getWindowWidth() {
		return WINDOW_WIDTH;
	}
	
	private void toTicketingSystem() {
		this.getContentPane().removeAll();
		this.setContentPane(new JPanel(new BorderLayout()));
		
		ticketingPanel = new TicketingSystem(students, tables);
		this.add(ticketingPanel);
		
		revalidate();
		repaint();
	}
	
	public static void main(String[] args){
        Prom prom = new Prom();
    }

    void redraw(){
        
    }
    
    public class LoginPanel extends JPanel implements ActionListener, ItemListener {
    	//private StudentLoginButton studentButton;
    	private JTextField idField;
    	private JTextField nameField;
    	private JTextField passwordField;
    	private StudentLoginButton loginButton;
    	private JButton createAccountButton;
        private JComboBox gradeOptions;
    	
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
    		
    		String[] grades = {"9", "10", "11", "12"};
    		nameField = new JTextField(20);
    		gradeOptions = new JComboBox(grades);
    		
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
    		if ((source == loginButton) && (studentExists(inputId, inputPassword))
    				&& ((StudentLoginButton) loginButton).isLoginButton()) {
    			toTicketingSystem();
    		} else if ((source == loginButton) && (!((StudentLoginButton) loginButton).isLoginButton())) {
    			students.add(new Student(nameField.getText(), inputId, 
    					gradeOptions.getSelectedItem().toString(), inputPassword));
    			toTicketingSystem();
    		} else if (source == createAccountButton) {
    			this.add(nameField);
    			this.add(gradeOptions);
    			((StudentLoginButton) loginButton).switchButtonState();
    		}
    		
    		revalidate();
    		repaint();
    	}
    	
    	public void itemStateChanged(ItemEvent e) {
    		if (e.getSource() == gradeOptions) {
    			
    		}
    	}
    	
    	private void writeLastStudent() {
    		try {
    			PrintWriter output = new PrintWriter(loginCredentials);
    			Student curStudent = students.get(students.size() - 1);
    			output.print(curStudent.getName().replace(' ', '_') + " " + curStudent.getId() 
    					+ " " + curStudent.getGrade() + " " + curStudent.getPassword());
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
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
}
