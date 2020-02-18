import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TicketingSystem extends JPanel {
    private ArrayList<Student> students;
    private ArrayList<Table> tables;
    private LoginPanel loginPanel;
    private TicketPanel ticketPanel = new TicketPanel(null);
    // private FloorPlanSystem floorPlanPanel;
    private File loginCredentials;

    public TicketingSystem(ArrayList<Student> students, ArrayList<Table> tables) {
        this.setLayout(new GridLayout());
        this.students = students;
        this.tables = tables;
        
        loginCredentials = new File("loginCredentials.txt");
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

    /*
    private void showFloor(){
        this.removeAll();
        this.add(this.floorPlanPanel);
        this.floorPlanPanel.setVisible(true);
        this.floorPlanPanel.requestFocus();
        this.updateUI();
    }
    **/

    private void addStudent(Student student) {
        this.students.add(student);
    }

    private void removeStudent(Student student) {
        this.students.remove(student);
    }

    private void initializeStudents() {
        try {
            Scanner input = new Scanner(loginCredentials);
            String id = "";
            String password = "";
            String grade = "";
            String studentInfo;
            String[] name = {};
            int lenString, countSpaces;
            int prevSpaceIndex = 0;
            while (input.hasNext()) {
                studentInfo = input.nextLine();
                lenString = studentInfo.length();

                countSpaces = 0;
                int i = 0;
                while (countSpaces != 3) {
                    if (studentInfo.charAt(i) == ' ') {
                        countSpaces++;
                        if (countSpaces == 1) {
                            name = studentInfo.substring(0, i).split("_");
                        } else if (countSpaces == 2) {
                            id = studentInfo.substring(prevSpaceIndex + 1, i);
                        } else if (countSpaces == 3) {
                            grade = studentInfo.substring(prevSpaceIndex + 1, i);
                            password = studentInfo.substring(i + 1);
                        }
                        prevSpaceIndex = i;
                    }
                    i++;
                }
                students.add(new Student(name[0] + " " + name[1], id, grade, password));
            }
            input.close();
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

        private void writeLastStudent() {
            try {
                FileWriter studentRecords = new FileWriter(loginCredentials, true);
                PrintWriter output = new PrintWriter(studentRecords);
                Student curStudent = students.get(students.size() - 1);
                output.println();
                output.print(curStudent.getName().replace(' ', '_') + " " + curStudent.getId()
                        + " " + curStudent.getGrade() + " " + curStudent.getPassword());
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private class InnerFrame extends JPanel implements ActionListener {
            InnerFrame() {
            	this.setFocusable(false);
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
                    writeLastStudent(); //write to file
                    ticketPanel = new TicketPanel(students.get(students.size() - 1)); //show the new student
                    showTicket();  //switch ti ticket panel
                } else if (source == createAccountButton) {
                    loginButton.switchButtonState();
                    if (loginButton.isLoginButton()) {
                        this.remove(nameText);
                        this.remove(nameField);
                        this.remove(gradeText);
                        this.remove(gradeOptions);
                    } else {
                    	addComponent(4, new JLabel("Name"));
            			addComponent(5, nameField);
            			
            			addComponent(6, new JLabel("Grade"));
            			
            			GridBagConstraints c = new GridBagConstraints();
            			c.gridy = 7;
            			c.anchor = GridBagConstraints.LINE_START;
            			c.insets = new Insets(5, 0, 0, 0);
            			this.add(gradeOptions, c);
            			loginButton.switchButtonState();
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
    
    class ButtonPanel extends JPanel implements ActionListener {
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
    
    private class TicketPanel extends JPanel{
        private Student selectedStudent;
        private TicketListener listener;
        private JLabel infoMessage = new JLabel();
        private JTextField cardNumber = new JTextField();
        private final JLabel cardLabel = new JLabel("Enter Card Number:");
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
                
                cardLabel.setText("adfs");
                this.partnerPanel = new PartnerPanel(this.selectedStudent.getPartners());

                //add all components
                
                upperPanel = new ButtonPanel();
                this.add(upperPanel, BorderLayout.NORTH);
                
                
                if (selectedStudent.hasPaid() && selectedStudent.getPartners().size() == 0){
                    this.infoMessage.setText("Woo! You're coming to Prom!\nMake sure to set your preferences");
                } else if (selectedStudent.hasPaid()) {
                    this.infoMessage.setText("Woo! You're set for Prom!\nYou can still add more or edit your preferences");
                } else {
                    this.infoMessage.setText("You're almost there!\nMake sure to purchase your ticket");
                }
                cardNumber.setText("This is the card number input");

                //do all layout
                
                this.add(infoMessage, BorderLayout.CENTER);
                this.add(cardLabel, BorderLayout.CENTER);
                this.add(cardNumber, BorderLayout.CENTER);
                this.add(buyButton, BorderLayout.CENTER);
                this.add(refund, BorderLayout.CENTER);
                this.add(partnerPanel);

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

                //add listener
                buyButton.addActionListener(listener);
                refund.addActionListener(listener);
            }
        }
        private class PartnerPanel extends JPanel implements ActionListener{
            private ArrayList<Student> partners;
            private ArrayList<JButton> removes;
            private ArrayList<JLabel> labels;
            private JButton addPartner;
            private JTextField partnerName;
            private JLabel errorLabel;

            PartnerPanel(ArrayList<Student> partners) {
                this.setVisible(true);
                this.partners = partners;
                this.setLayout(new GridBagLayout());
                removes = new ArrayList<JButton>();
                labels = new ArrayList<JLabel>();
                errorLabel = new JLabel("");
                GridBagConstraints c;
                for (int i = 0; i < this.partners.size(); i++) {
                    c = new GridBagConstraints();
                    c.gridx = 0;
                    c.gridy = i;

                    removes.add(new JButton("Remove"));
                    removes.get(i).addActionListener(this);

                    this.add(removes.get(i),c);

                    c = new GridBagConstraints();
                    c.gridx = 1;
                    c.gridy = i;

                    labels.add(new JLabel(this.partners.get(i).getName()));
                    this.add(labels.get(i),c);
                }

                addPartner = new JButton("Add Partner");
                partnerName = new JTextField("Partner Name here");

                c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = this.partners.size();
                this.add(addPartner, c);
                addPartner.addActionListener(this);

                c = new GridBagConstraints();
                c.gridx = 1;
                c.gridy = this.partners.size();
                this.add(partnerName, c);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == addPartner){
                    Student foundStudent = null;
                    //add partner search code
                    System.out.println(partnerName.getText());
                    GridBagConstraints c;
                    if (foundStudent == null){
                        errorLabel = new JLabel("Partner not Found. Ask them to register before you can add them");
                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridwidth = 2;
                        c.gridy = this.partners.size() + 1;
                        this.add(errorLabel, c);
                    } else {
                        this.remove(errorLabel);

                        remove(addPartner);
                        remove(partnerName);

                        partners.add(foundStudent);

                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridy = this.partners.size()-1;

                        removes.add(new JButton("Remove"));
                        removes.get(removes.size()-1).addActionListener(this);

                        this.add(removes.get(removes.size()-1),c);

                        c = new GridBagConstraints();
                        c.gridx = 1;
                        c.gridy = this.partners.size()-1;

                        labels.add(new JLabel(this.partners.get(partners.size()-1).getName()));
                        this.add(labels.get(labels.size()-1),c);

                        c = new GridBagConstraints();
                        c.gridx = 0;
                        c.gridy = this.partners.size();
                        this.add(addPartner, c);
                        addPartner.addActionListener(this);

                        c = new GridBagConstraints();
                        c.gridx = 1;
                        c.gridy = this.partners.size();
                        this.add(partnerName, c);


                    }
                } else {
                    int index = removes.indexOf(source);

                    this.remove(removes.get(index));
                    this.remove(labels.get(index));

                    removes.remove(index);
                    labels.remove(index);
                    partners.remove(index);
                }

                revalidate();
                repaint();
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
    }
}