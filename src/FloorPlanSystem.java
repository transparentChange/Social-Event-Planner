import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FloorPlanSystem extends JPanel {
    ArrayList<Table> tables;
    FloorPlanSystem(ArrayList<Table> tables) {
        this.setLayout(new FlowLayout());
        this.tables = tables;
        this.add(new JLabel("Your panel here"));
        for (Table t : this.tables){
            this.add(new JLabel("This is a Table "));
        }
    }


}
