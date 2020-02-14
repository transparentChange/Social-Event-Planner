import java.util.ArrayList;

import javax.swing.JFrame;

public class Prom extends JFrame {
	static private TicketingSystem ticketingPanel;
	private ArrayList<Student> students;
	private ArrayList<Table> tables;
	// private FloorPlanSystem floorPlanPanel;
	//
	public Prom() {
		super("Prom");
        
		this.students = new ArrayList<Student>();
        this.tables = new ArrayList<Table>();
        this.ticketingPanel = new TicketingSystem(students, tables);
        
        // this.add(floorPlanPanel);
        this.add(ticketingPanel);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.requestFocusInWindow();
        this.setVisible(true);
	}
	
	public static void main(String[] args){
        
    }

    void redraw(){
        
    }
}
