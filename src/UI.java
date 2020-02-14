import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UI extends JFrame {

    static UI ui;
    static Menu panel;

    public UI(){
        super("Prom");
        panel = new Menu();
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.requestFocusInWindow();
        this.setVisible(true);

    }

    public static void main(String[] args){
        ui = new UI();
    }

    void redraw(){
        panel.repaint();
    }
}
