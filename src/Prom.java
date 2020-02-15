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
        
		loginPanel = new LoginPanel(WINDOW_HEIGHT, WINDOW_WIDTH);
		
		//loginButton.addActionListener(this);
		
		this.students = new ArrayList<Student>();
        this.tables = new ArrayList<Table>();
        
        // this.add(floorPlanPanel);
        //this.add(ticketingPanel);
        this.add(loginPanel);
        
        this.requestFocusInWindow();
        this.setVisible(true);
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
    	private StudentLoginButton studentButton;
    	private JTextField idField;
    	private JTextField passwordField;
    	private JButton loginButton;
    	private JButton createAccountButton;
    	
    	LoginPanel(int windowHeight, int windowWidth) {
    		this.setFocusable(false);
    		this.setBounds(windowWidth / 10, 0, windowWidth / 5, windowHeight);
    		this.setOpaque(false);
    		this.setLayout(new GridBagLayout());
    		
    		studentButton = new StudentLoginButton(0, 0);
    		createAccountButton = new JButton("Don't have an account? Click to create a new account");
    		
    		idField = new JTextField(20);
    		passwordField = new JTextField(20);
    		
    		loginButton = new JButton("Login");
    		loginButton.addActionListener(this);
    			
    		this.add(studentButton);
    		this.add(idField);
    		this.add(passwordField);
    		this.add(loginButton);
    	}
    	
    	public void actionPerformed(ActionEvent evt) {
    		Object source = evt.getSource();
    		if (source == loginButton) {
    			toTicketingSystem();
    		}
    	}
    }
}
