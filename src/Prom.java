import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JFrame;


public class Prom extends JFrame {
    private TicketingSystem ticketingPanel;
    private ArrayList<Student> students;
    private ArrayList<Table> tables;

    // constants that hold game window size information
    private final static int WINDOW_WIDTH = 1366;
    private final static int WINDOW_HEIGHT = 768;

    public final static int maxTables = 5;
    public final static int tableSize = 10;

    public Prom() {
        super("Prom");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        this.students = new ArrayList<Student>();
        this.tables = new ArrayList<Table>();
        //tables.add(new Table());

        this.ticketingPanel = new TicketingSystem(students,tables);

        this.add(ticketingPanel);

        this.requestFocusInWindow();
        this.setVisible(true);
    }

    public static void main(String[] args){
        new Prom();
    }
}
