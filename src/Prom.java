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

public class Prom extends JFrame implements ActionListener {
	static private TicketingSystem ticketingPanel;
	private ArrayList<Student> students;
	private ArrayList<Table> tables;
    
	private JTextField idField;
	private JTextField passwordField;
	private StudentLoginButton studentButton;
	private JButton loginButton;
	private JButton createAccountButton;
	
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
        
		JPanel loginPanel = new JPanel(new GridBagLayout());
		
		loginPanel.setFocusable(false);
		loginPanel.setBounds(getWidth() / 10, 0, getWidth() / 5, WINDOW_HEIGHT);
		loginPanel.setOpaque(false);
		
		studentButton = new StudentLoginButton(0, 0);
		
		loginButton = new JButton("Login");
		loginButton.addActionListener(this);
		
		createAccountButton = new JButton("Don't have an account? Click to create a new account");
		
		idField = new JTextField(20);
		passwordField = new JTextField(20);
		
		loginPanel.add(studentButton);
		loginPanel.add(idField);
		loginPanel.add(passwordField);
		loginPanel.add(loginButton);
		
		this.students = new ArrayList<Student>();
        this.tables = new ArrayList<Table>();
        
        
        // this.add(floorPlanPanel);
        //this.add(ticketingPanel);
        this.add(loginPanel);
        
        this.requestFocusInWindow();
        this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source == loginButton) {
			this.getContentPane().removeAll();
			this.setContentPane(new JPanel(new BorderLayout()));
			
			ticketingPanel = new TicketingSystem(students, tables);
			this.add(ticketingPanel);
			
			revalidate();
			repaint();
		}
	}
	
	public static void main(String[] args){
        Prom prom = new Prom();
    }

    void redraw(){
        
    }
}
