import java.util.ArrayList;

import javax.swing.JFrame;

public class Prom extends JFrame {
	static private TicketingSystem menuPanel;
	private ArrayList<Student> students;
	private ArrayList<Table> tables;
	
	public Prom() {
		super("Prom");
        students = new ArrayList<Student>();
        tables = new ArrayList<Table>();
        menuPanel = new TicketingSystem(students, tables);
        this.add(menuPanel);
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
