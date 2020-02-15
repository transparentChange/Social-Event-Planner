import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

public class Prom extends JFrame {
	static private TicketingSystem ticketingPanel;
	private ArrayList<Student> students;
	private ArrayList<Table> tables;
	private LoginPanel loginPanel;
	
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
		File loginCredentials = new File("loginCredentials.txt");
		try {
			Scanner input = new Scanner(loginCredentials);
			String id, password;
			String studentInfo;
			String[] name;
			int[] spaceIndices = new int[2];
			while (input.hasNext()) {
				studentInfo = input.nextLine();
				spaceIndices[0] = studentInfo.indexOf(' ');
				spaceIndices[1] = studentInfo.lastIndexOf(' ');
				name = studentInfo.substring(0, spaceIndices[0]).split("_");
				id = studentInfo.substring(spaceIndices[0] + 1, spaceIndices[1]);
				password = studentInfo.substring(spaceIndices[1] + 1);
				
				students.add(new Student(name[0] + " " + name[1], id, password));
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
    
    public class LoginPanel extends JPanel implements ActionListener {
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
    			System.out.println("here");
				if (!((StudentLoginButton) loginButton).isLoginButton()) {
					students.add(new Student(nameField.getText(), inputId, inputPassword));
				}
    			
    			toTicketingSystem();
    		} else if (source == createAccountButton) {
    			this.add(nameField);
    			((StudentLoginButton) loginButton).switchButtonState();
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
