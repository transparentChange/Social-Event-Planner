import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FloorPlanSystem extends JPanel {
    FloorPlanSystem(ArrayList<Table> t){
        this.setLayout(new FlowLayout());
        this.add(new JLabel("Floor Plan"));
    }
}
