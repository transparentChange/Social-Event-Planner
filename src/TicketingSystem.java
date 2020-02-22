import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TicketingSystem extends JPanel {
    private ArrayList<Student> students;
    private ArrayList<Table> tables;
    private LoginPanel loginPanel;
    private TicketPanel ticketPanel = new TicketPanel(null);
    private FloorPlanPanel floorPanel;
    private FloorPlanSystem floorPlan;
    private File loginCredentials;
    private int maxTables;
    private int tableSize;

    public TicketingSystem(ArrayList<Student> students, ArrayList<Table> tables) {
        this.setLayout(new GridLayout());
        this.students = students;
        this.tables = tables;

        this.maxTables = Prom.maxTables;
        this.tableSize = Prom.tableSize;

        Path programFilePath = Path.of(URI.create(this.getClass().getProtectionDomain().getCodeSource().getLocation().toString() + "loginCredentials.txt"));
        loginCredentials = new File(programFilePath.toString());

        try {
            initializeStudents();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginPanel = new LoginPanel();
        this.add(loginPanel);

        this.setVisible(true);
    }

    private void showTicket() {
        this.removeAll();
        this.add(this.ticketPanel);
        this.ticketPanel.setVisible(true);
        this.ticketPanel.requestFocus();
        this.updateUI();
    }

    private void showLogin() {
        this.removeAll();
        this.loginPanel = new LoginPanel(); //reset the login panel
        this.add(this.loginPanel);
        this.loginPanel.setVisible(true);
        this.loginPanel.requestFocus();
        this.updateUI();
    }

    private void showFloor(JPanel fromPanel){
        floorPanel = new FloorPlanPanel(fromPanel);
        this.removeAll();
        this.add(this.floorPanel);
        this.floorPanel.setVisible(true);
        this.floorPanel.requestFocus();
        this.updateUI();
    }

    private void addStudent(Student student) {
        this.students.add(student);
    }

    private void removeStudent(Student student) {
        this.students.remove(student);
    }

    private Student findStudentByName(String name){
        for (Student student : students){
            if (name.compareToIgnoreCase(student.getName()) == 0){
                return student;
            }
        }
        return null;
    }

    private void initializeStudents() {
        try {
            Scanner input = new Scanner(loginCredentials);
            ArrayList<String[]> partners = new ArrayList<String[]>();

            while (input.hasNext()) {
                String student = input.nextLine();
                if (!student.equals("")) {
                    String[] ids = student.split(",");
                    HashMap<String, String> keys = new HashMap();
                    for (String key : ids) {
                        String[] temp = key.split(":");
                        if (temp.length > 1){
                        keys.put(temp[0].replace("\"", ""), temp[1].replace("\"", ""));
                        } else {
                            keys.put(temp[0].replace("\"", ""), "");
                        }
                    }

                    String name = keys.get("name");
                    String id = keys.get("id");
                    String[] partnerString = keys.get("partners").split("#");

                    Boolean hasPaid = Boolean.parseBoolean(keys.get("paid"));
                    String password = keys.get("password");
                    String grade = keys.get("grade");

                    String image = keys.get("image");

                    Student s = new Student(name, id, grade, password);
                    s.setPaid(hasPaid);
                    addStudent(s);
                    partners.add(partnerString);

                    if (!image.equals("null")){
                        Path programFilePath = Path.of(URI.create(this.getClass().getProtectionDomain().getCodeSource().getLocation().toString() + "/studentImages/" + id + ".png"));
                        BufferedImage studentImage = ImageIO.read(new File(programFilePath.toString()));
                        s.setPicture(studentImage);
                    }
                }
            }

            for (int i = 0; i < students.size(); i++) {
                ArrayList<Student> studentPartners = students.get(i).getPartners();
                for (int j = 0; j < partners.get(i).length; j++) {
                    Student s = findStudentByName(partners.get(i)[j]);
                    if (s != null){
                        studentPartners.add(s);
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeStudents() {
        try {
            FileWriter studentRecords = new FileWriter(loginCredentials, false);
            PrintWriter output = new PrintWriter(studentRecords);

            for (Student curStudent : students){
                output.print("name:" + curStudent.getName() + ",");
                output.print("id:" + curStudent.getId() + ",");
                output.print("paid:" + curStudent.hasPaid() + ",");
                output.print("grade:" + curStudent.getGrade() + ",");
                output.print("password:" + curStudent.getPassword() + ",");
                if (curStudent.getPicture() != null) {
                    Path programFilePath = Path.of(URI.create(this.getClass().getProtectionDomain().getCodeSource().getLocation().toString()+ "/studentImages/" + curStudent.getId() + ".png"));
                    System.out.println(programFilePath.toString());
                    ImageIO.write(curStudent.getPicture(), "png", new File(programFilePath.toString()));
                    output.print("image:" + curStudent.getId() + ".png,");
                } else {
                    output.print("image:null,");
                }
                String partnerString = "";
                ArrayList<Student> partnerArray = curStudent.getPartners();
                for (int i = 0; i < partnerArray.size(); i++) {
                    partnerString += curStudent.getPartners().get(i).getName() + "#";
                }
                if (partnerArray.size() != 0) {
                    partnerString = partnerString.substring(0, partnerString.length() - 1);
                }
                output.print("partners:" + partnerString);
                output.println();
            }

            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LoginPanel extends JPanel {
        BufferedImage image = null;
        private JTextField idField;
        private JTextField nameField;
        private JTextField passwordField;
        private LoginButton loginButton;
        private JButton createAccountButton;
        private JComboBox gradeOptions;
        private Font fieldFont;
        private JLabel nameText;
        private JLabel gradeText;

        LoginPanel() {
            this.setFocusable(false);
            this.setOpaque(true);

            this.setLayout(new GridBagLayout());

            try {
                image = ImageIO.read(new File("src/loginBackground.jpg"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JPanel colourPanel = new JPanel(new GridBagLayout());
            colourPanel.setBackground(new Color(75, 112, 68));
            
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(50, 50, 50, 50);

            colourPanel.add(new InnerFrame(), c);
            this.add(colourPanel);

            this.setVisible(true);
        }

        public void paintComponent(Graphics g) {
            if (this.image != null) {
                g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                g.setColor(new Color(0, 0, 0));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        }

        private int getStudentIndex(String id, String password) {
            for (int i = 0; i < students.size(); i++) {
                if ((students.get(i).getId().equals(id)) && (students.get(i).getPassword().equals(password))) {
                    return i;
                }
            }
            return -1;
        }

        private class InnerFrame extends JPanel implements ActionListener {
            InnerFrame() {
        		this.setOpaque(false);
        		this.setLayout(new GridBagLayout());
        		this.setVisible(true);
        		
        		fieldFont = new Font("Open Sans", Font.PLAIN, 20);
                
        		createAccountButton = new JButton("Don't have an account? Click here to sign up.");
        		createAccountButton.addActionListener(this);
        		
                GridBagConstraints c = new GridBagConstraints();

                addComponent(0, new JLabel("Student ID"));
               
        		idField = new JTextField();
        		addComponent(1, idField);
        		
        		addComponent(2, new JLabel("Password"));
        		
        		passwordField = new JTextField();
        		addComponent(3, passwordField);
        		
        		loginButton = new LoginButton(0, 0);
        		loginButton.addActionListener(this);
        		c = new GridBagConstraints();
        		c.gridy = 8;
        		c.anchor = GridBagConstraints.LINE_START;
        		c.insets = new Insets(0, 20, 0, 0);
        		this.add(loginButton, c);
        		
        		c.insets = new Insets(100, 0, 0, 0);
        		c.gridy = 9;
        		this.add(createAccountButton, c);
        		
        		String[] grades = {"9", "10", "11", "12"};
        		nameField = new JTextField(20);
        		gradeOptions = new JComboBox(grades);
            }
            
            public void addComponent(int gridy, JComponent component) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = gridy;

                if (component instanceof JLabel) {
                    c.insets = new Insets(20, 0, 0, 0); // space in between
                    c.anchor = GridBagConstraints.LINE_START;
                    component.setForeground(Color.WHITE);
                } else if (component instanceof JTextField) {
                    c.insets = new Insets(5, 0, 0, 0);
                    c.fill = GridBagConstraints.HORIZONTAL;
                    component.setFont(fieldFont);
                } else if (component instanceof JComboBox) {
                    c.anchor = GridBagConstraints.LINE_START;
                }

                this.add(component, c);
            }

            public void actionPerformed(ActionEvent evt) {
                Object source = evt.getSource();
                String inputId = idField.getText();
                String inputPassword = passwordField.getText();
                int studentIndex = getStudentIndex(inputId, inputPassword);
                if ((source == loginButton) && (studentIndex != -1) && (loginButton.isLoginButton())) { //if login button is clicked and is a login button and student is valid
                    ticketPanel = new TicketPanel(students.get(studentIndex)); //show selected student
                    showTicket(); //switch to ticket panel
                } else if ((source == loginButton) && (!loginButton.isLoginButton())) { //if login button is a create account
                    students.add(new Student(nameField.getText(), inputId, gradeOptions.getSelectedItem().toString(), inputPassword)); //create new student
                    writeStudents(); //write to file
                    ticketPanel = new TicketPanel(students.get(students.size() - 1)); //show the new student
                    showTicket();  //switch to ticket panel
                } else if (source == createAccountButton) {
                    loginButton.switchButtonState();
                    if (loginButton.isLoginButton()) {
                        this.remove(nameText);
                        this.remove(nameField);
                        this.remove(gradeText);
                        this.remove(gradeOptions);
                    } else {
                        nameText = new JLabel("Name");
                    	addComponent(4, nameText);
            			addComponent(5, nameField);

            			gradeText = new JLabel("Grade");
            			addComponent(6, gradeText);
            			
            			GridBagConstraints c = new GridBagConstraints();
            			c.gridy = 7;
            			c.anchor = GridBagConstraints.LINE_START;
            			c.insets = new Insets(5, 0, 0, 0);
            			this.add(gradeOptions, c);
                    }
                }

                revalidate();
                repaint();
            }
        }

        private class LoginButton extends JButton {
            private boolean login;

            LoginButton(int gridX, int gridY) {
                this.login = true;
                setText("Login");
            }

            public boolean isLoginButton() {
                return login;
            }

            public void switchButtonState() {
                login = !login;
                if (login) {
                    this.setText("Login");
                } else {
                    this.setText("Create Account");
                }
            }
        }
    }
    
    private class TicketPanel extends JPanel{
        private Student selectedStudent;
        private TicketListener listener;
        private JLabel infoMessage = new JLabel();
        private JTextField cardNumber = new JTextField();
        private JLabel cardLabel = new JLabel("Enter Card Number:");
        private JButton buyButton = new JButton("Buy now!");
        private JButton refund = new JButton("Click here for a refund");
        private PartnerPanel partnerPanel;
        private ButtonPanel upperPanel;

        TicketPanel(Student student) {
            if (student != null) {
                listener = new TicketListener();
                this.setLayout(new BorderLayout());
                this.setVisible(true);

                this.selectedStudent = student;
                this.partnerPanel = new PartnerPanel(this.selectedStudent.getPartners());

                //add all components
                upperPanel = new ButtonPanel();
                this.add(upperPanel, BorderLayout.NORTH);
                
                /*
                if ((selectedStudent.hasPaid()) && (selectedStudent.getPartners().size() == 0)) {
                    infoMessage.setText("Woo! You're coming to Prom!\nMake sure to set your preferences");
                } else if (selectedStudent.hasPaid()) {
                    infoMessage.setText("Woo! You're set for Prom!\nYou can still add more or edit your preferences");
                } else {
                    infoMessage.setText("You're almost there!\nMake sure to purchase your ticket");
                }
                cardNumber.setText("This is the card number input");
				*/

                //do all layout
                
                // this.add(infoMessage, BorderLayout.CENTER);

                /*
                this.add(cardLabel, BorderLayout.CENTER);
                this.add(cardNumber, BorderLayout.CENTER);
                this.add(buyButton, BorderLayout.CENTER);
                this.add(refund, BorderLayout.CENTER);
                */

                this.add(partnerPanel, BorderLayout.WEST);

                this.add(new ProfilePanel(selectedStudent.getPicture()), BorderLayout.EAST);

                /*
                if (selectedStudent.hasPaid()){
                    cardLabel.setVisible(false);
                    cardNumber.setVisible(false);
                    buyButton.setVisible(false);
                    refund.setVisible(true);

                } else {
                    cardLabel.setVisible(true);
                    cardNumber.setVisible(true);
                    buyButton.setVisible(true);
                    refund.setVisible(false);
                }
                */

                //add listener
                //buyButton.addActionListener(listener);
                //refund.addActionListener(listener);
            }
        }
        private class PartnerPanel extends JPanel implements ActionListener {
            private ArrayList<Student> partners;
            private ArrayList<JButton> removeButtons;
            private ArrayList<JLabel> partnerLabels;
            private JButton addPartnerButton;
            private JTextField partnerNameField;
            private JLabel errorLabel;
            private JLabel title = new JLabel("Student Preferences for Seating");

            PartnerPanel(ArrayList<Student> partners) {
                this.setVisible(true);
                this.partners = partners;
                this.setLayout(new GridBagLayout());
                this.removeButtons = new ArrayList();
                this.partnerLabels = new ArrayList();
                this.errorLabel = new JLabel("");

                this.setBorder(new EmptyBorder(10, 10, 10, 10));

                GridBagConstraints c;
                for(int i = 0; i < this.partners.size(); ++i) {
                    c = new GridBagConstraints();
                    c.gridx = 0;
                    c.gridy = i;
                    removeButtons.add(new JButton("-"));
                    removeButtons.get(i).addActionListener(this);

                    this.add(removeButtons.get(i), c);
                    c = new GridBagConstraints();
                    c.gridx = 1;
                    c.gridy = i;
                    this.partnerLabels.add(new JLabel((this.partners.get(i)).getName()));
                    this.add(this.partnerLabels.get(i), c);
                }

                addPartnerButton = new JButton("+");
                partnerNameField = new JTextField("Partner Name here");
                partnerNameField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

                c = new GridBagConstraints();
                this.add(title, c);

                c.gridx = 0;
                c.gridy = 1;
                c.anchor = GridBagConstraints.LINE_START;
                this.add(this.partnerNameField, c);

                c = new GridBagConstraints();
                c.gridx = 1;
                c.gridy = 1;

                this.add(this.addPartnerButton, c);
                this.addPartnerButton.addActionListener(this);

            }

            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                System.out.println(e.getActionCommand());
                if (source == this.addPartnerButton) {
                    Student foundStudent = TicketingSystem.this.findStudentByName(this.partnerNameField.getText());
                    this.remove(this.errorLabel);
                    GridBagConstraints c;
                    if (foundStudent == TicketPanel.this.selectedStudent) {
                        this.errorLabel = new JLabel("You can't add yourself");
                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridwidth = 2;
                        c.gridy = -1;
                        this.add(this.errorLabel, c);
                    } else if (TicketPanel.this.selectedStudent.getPartners().contains(foundStudent)) {
                        this.errorLabel = new JLabel("Person already added");
                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridwidth = 2;
                        c.gridy = -1;
                        this.add(this.errorLabel, c);
                    } else if (foundStudent == null) {
                        this.errorLabel = new JLabel("Partner not Found. Ask them to register before you can add them");
                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridwidth = 2;
                        c.gridy = -1;
                        this.add(this.errorLabel, c);
                    } else {
                    	// add a student
                        this.remove(addPartnerButton);
                        this.remove(partnerNameField);
                        this.partners.add(foundStudent);

                        c = new GridBagConstraints();
                        c.gridx = 2;
                        c.gridy = partners.size() + 1;
                        c.anchor = GridBagConstraints.LINE_START;
                        removeButtons.add(new JButton("-"));
                        removeButtons.get(removeButtons.size() - 1).addActionListener(this);
                        this.add(removeButtons.get(removeButtons.size() - 1), c);

                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridy = this.partners.size();
                        c.anchor = GridBagConstraints.LINE_START;
                        this.partnerLabels.add(new JLabel(this.partners.get(this.partners.size() - 1).getName()));
                        this.add(this.partnerLabels.get(this.partnerLabels.size() - 1), c);

                        c = new GridBagConstraints();
                        c.gridx = 1;
                        c.gridy = this.partners.size() + 1;
                        this.add(this.addPartnerButton, c);
                        this.addPartnerButton.addActionListener(this);

                        partnerNameField = new JTextField("Partner name here");
                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridy = partners.size() + 1;
                        c.anchor = GridBagConstraints.LINE_START;
                        this.add(partnerNameField, c);
                    }
                } else {
                    //int index = this.remove.indexOf(source);
                    //this.remove(this.removes.get(index));
                    //this.remove(this.labels.get(index));
                    //this.removes.remove(index);
                	//this.remove(removeButton);
                    //this.labels.remove(index);
                    //this.partners.remove(index);
                }

                TicketingSystem.this.writeStudents();
                this.revalidate();
                this.repaint();
            }
        }

        private class ButtonPanel extends JPanel implements ActionListener {
            private JButton logout;

            ButtonPanel() {
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                setBorder(new EmptyBorder(10, 0, 5, 0));
                setOpaque(false);
                setFocusable(false);

                add(Box.createRigidArea(new Dimension(1100, 0)));
                logout = new JButton("Logout "); // add name, call showLogin()
                logout.addActionListener(this);

                this.add(logout);

                revalidate();
                repaint();
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                GradientPaint blackToGray = new GradientPaint(0, 0, new Color(25, 38, 23),
                        0, getHeight(), new Color(147, 222, 135));
                g2.setPaint(blackToGray);
                g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            }

            public void actionPerformed(ActionEvent evt) {
                showLogin();
            }
        }

        private class TicketListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == buyButton){
                    cardLabel.setVisible(false);
                    cardNumber.setVisible(false);
                    buyButton.setVisible(false);
                    refund.setVisible(true);
                    selectedStudent.setPaid(true);

                } else if (source == refund){
                    cardLabel.setVisible(true);
                    cardNumber.setVisible(true);
                    buyButton.setVisible(true);
                    refund.setVisible(false);
                    selectedStudent.setPaid(false);
                }
                if (selectedStudent.hasPaid() && selectedStudent.getPartners().size() == 0){
                    infoMessage.setText("Woo! You're coming to Prom!\nMake sure to set your preferences");
                } else if (selectedStudent.hasPaid()) {
                    infoMessage.setText("Woo! You're set for Prom!\nYou can still add more or edit your preferences");
                } else {
                    infoMessage.setText("You're almost there!\nMake sure to purchase your ticket");
                }
            }
        }

        private class ProfilePanel extends JPanel implements ActionListener{
            private BufferedImage studentImage;
            private JLabel imageComponent;
            private JFileChooser fileChooser;
            private JButton selectImage;
            private Path filePath;

            private static final int imageSize = 200;

            ProfilePanel(BufferedImage image){
                this.setLayout(new GridBagLayout());

                studentImage = image;

                if (studentImage != null) {
                    imageComponent = new JLabel(new ImageIcon(studentImage));
                } else {
                    imageComponent = new JLabel("No image");
                }
                selectImage = new JButton("Select Image");
                fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));

                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0;
                this.add(selectImage,c);
                selectImage.addActionListener(this);

                c = new GridBagConstraints();
                c.gridy = 1;
                this.add(imageComponent,c);

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == selectImage){
                    int returnValue = fileChooser.showOpenDialog(null);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        try {
                            File selectedFile = fileChooser.getSelectedFile();
                            filePath = selectedFile.toPath();

                            BufferedImage tempBufferedImage = ImageIO.read(new File(filePath.toString()));

                            int w = tempBufferedImage.getWidth();
                            int h = tempBufferedImage.getHeight();
                            int s;
                            if (w > h){
                                s = h;
                            } else {
                                s = w;
                            }

                            tempBufferedImage = tempBufferedImage.getSubimage((w-s)/2,(h-s)/2, s, s);
                            Image tempImage = tempBufferedImage.getScaledInstance(imageSize,imageSize, Image.SCALE_SMOOTH);

                            studentImage = new BufferedImage(imageSize,imageSize,BufferedImage.TYPE_INT_ARGB);
                            Graphics2D resize = (Graphics2D) studentImage.getGraphics();
                            resize.drawImage(tempImage, 0, 0, null);
                            resize.dispose();

                            //remove old image
                            this.remove(imageComponent);
                            //add new image
                            imageComponent = new JLabel(new ImageIcon(studentImage));

                            GridBagConstraints c = new GridBagConstraints();
                            c.gridy = 1;

                            this.add(imageComponent,c);

                            selectedStudent.setPicture(studentImage);

                            writeStudents();
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }

                        revalidate();
                        repaint();
                    }
                }
            }
        }

    }

    private class FloorPlanPanel extends JPanel implements ActionListener{
        private JPanel fromPanel;
        private JButton exitButton;

        FloorPlanPanel(JPanel fromPanel) {
            ArrayList<Student> paidStudents = new ArrayList<Student>();
            for (Student s : students){
                if (s.hasPaid()){
                    paidStudents.add(s);
                }
            }
            tables = SeatingAssignmentSystem.assignTables(paidStudents, maxTables, tableSize);
            floorPlan = new FloorPlanSystem(tables);
            this.fromPanel = fromPanel;
            this.exitButton = new JButton("Hide FloorPlan");
            this.setLayout(new BorderLayout());
            this.add(floorPlan, BorderLayout.CENTER);
            this.add(exitButton, BorderLayout.PAGE_START);
            this.exitButton.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == exitButton){
                if (this.fromPanel == loginPanel){
                    showLogin();
                } else if (this.fromPanel == ticketPanel){
                    showTicket();
                }
            }
        }
    }
}