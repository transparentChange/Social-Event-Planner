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
    private TicketingSystem ticketingPanel;
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

        //loginButton.addActionListener(this);

        this.students = new ArrayList<Student>();
        this.tables = new ArrayList<Table>();

        this.ticketingPanel = new TicketingSystem(students,tables);

        this.add(ticketingPanel);

        this.requestFocusInWindow();
        this.setVisible(true);
    }

    public static void main(String[] args){
        Prom prom = new Prom();
    }
}
