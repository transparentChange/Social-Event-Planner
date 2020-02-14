import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;

public class Prom extends JFrame {// implements ActionListener {
	static private TicketingSystem ticketingPanel;
	private ArrayList<Student> students;
	private ArrayList<Table> tables;
	
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
		
		StudentLoginButton studentButton = new StudentLoginButton(0, 0);
		//studentLoginButton.addActionListener(this);
		loginPanel.add(studentButton);
		
		this.students = new ArrayList<Student>();
        this.tables = new ArrayList<Table>();
        this.ticketingPanel = new TicketingSystem(students, tables);
        
        // this.add(floorPlanPanel);
        //this.add(ticketingPanel);
        this.add(loginPanel);
        
        this.requestFocusInWindow();
        this.setVisible(true);
	}
	
	public static void main(String[] args){
        Prom prom = new Prom();
    }

    void redraw(){
        
    }
}
