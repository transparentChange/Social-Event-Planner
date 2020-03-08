import javax.swing.*;
import java.util.ArrayList;

public class FloorPlanSystem extends JPanel {

    public FloorPlanSystem(ArrayList<Student> s) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JLabel("Never gonna give you up"));
        this.add(new JLabel("Never gonna let you down"));
    }
}
