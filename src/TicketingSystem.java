
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;

public class TicketingSystem extends JPanel {
    private ArrayList<Student> students;
    private ArrayList<Table> tables;
    private LoginPanel loginPanel;
    private TicketPanel ticketPanel = new TicketPanel(null);
    private FloorPlanPanel floorPanel;
    private FloorPlanSystem floorPlan;
    private File loginCredentials;

    // change
    private final int WINDOW_WIDTH = (int) getToolkit().getScreenSize().getWidth();
    private final int WINDOW_HEIGHT = (int) getToolkit().getScreenSize().getWidth();

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

    private Student findStudentByID(String id) {
        for (Student student : students){
            if (student.getId().equals(id)){
                return student;
            }
        }
        return null;
    }

    private void initializeStudents() {
        try {
            Scanner input = new Scanner(loginCredentials);
            ArrayList<String[]> partners = new ArrayList<String[]>();
            ArrayList<String[]> blackList = new ArrayList<String[]>();

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
                    String[] blackListString = keys.get("blacklist").split("#");

                    Boolean hasPaid = Boolean.parseBoolean(keys.get("paid"));
                    String password = keys.get("password");
                    String grade = keys.get("grade");

                    String image = keys.get("image");

                    ArrayList<String> accommodations = new ArrayList<String>();
                    if (keys.get("accommodation").length() > 0) {
                        accommodations.addAll(Arrays.asList(keys.get("accommodation").split("#")));
                    }

                    Student s = new Student(name, id, grade, password);
                    s.setPaid(hasPaid);
                    s.setAccommodations(accommodations);

                    addStudent(s);

                    partners.add(partnerString);
                    blackList.add(blackListString);

                    if (!image.equals("null")){
                        BufferedImage studentImage = ImageIO.read(new File("studentImages/" + id + ".png"));
                        s.setPicture(studentImage);
                    }
                }
            }

            for (int i = 0; i < students.size(); i++) {
                ArrayList<Student> studentPartners = students.get(i).getPartners();
                ArrayList<Student> blackListed = students.get(i).getBlacklist();
                for (int j = 0; j < partners.get(i).length; j++) {
                    Student s = findStudentByID(partners.get(i)[j]);
                    if (s != null) {
                        studentPartners.add(s);
                    }
                }
                for (int j = 0; j < blackList.get(i).length; j++) {
                    Student s = findStudentByID(blackList.get(i)[j]);
                    if (s != null) {
                        blackListed.add(s);
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

            for (Student curStudent : students) {
                output.print("name:" + curStudent.getName() + ",");
                output.print("id:" + curStudent.getId() + ",");
                output.print("paid:" + curStudent.hasPaid() + ",");
                output.print("grade:" + curStudent.getGrade() + ",");
                output.print("password:" + curStudent.getPassword() + ",");
                if (curStudent.getPicture() != null) {
                    URI programFilePath = URI.create("studentImages/" + curStudent.getId() + ".png");
                    ImageIO.write(curStudent.getPicture(), "png", new File("studentImages/" + curStudent.getId() + ".png"));
                    output.print("image:" + curStudent.getId() + ".png,");
                } else {
                    output.print("image:null,");
                }
                String partnerString = "";
                ArrayList<Student> partnerArray = curStudent.getPartners();
                for (int i = 0; i < partnerArray.size(); i++) {
                    partnerString += partnerArray.get(i).getId() + "#";
                }
                if (partnerString.length() != 0) {
                    partnerString = partnerString.substring(0, partnerString.length() - 1);
                }

                String accommodationString = "";
                ArrayList<String> accommodationArray = curStudent.getAccommodations();
                for (int i = 0; i < accommodationArray.size(); i++){
                    accommodationString += accommodationArray.get(i) + "#";
                }
                if (accommodationString.length() != 0){
                    accommodationString = accommodationString.substring(0, accommodationString.length() - 1);
                }

                String blackListString = "";
                ArrayList<Student> blackListArray = curStudent.getBlacklist();
                for (int i = 0; i < blackListArray.size(); i++) {
                    blackListString += blackListArray.get(i).getId() + "#";
                }
                if (blackListString.length() != 0){
                    blackListString = blackListString.substring(0,blackListString.length() - 1);
                }

                output.print("partners:" + partnerString + ",");
                output.print("accommodation:"+accommodationString+",");
                output.print("blacklist:"+blackListString+",");

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
        private JButton loginButton;
        private JButton createAccount;
        private JButton createToggle;
        private boolean createIsToggled;
        private JComboBox gradeOptions;
        private Font fieldFont;
        private JLabel nameText;
        private JLabel gradeText;
        private JLabel errorLabel;

        LoginPanel() {
            this.setFocusable(false);
            this.setOpaque(true);
            this.setLayout(new GridBagLayout());

            try {
                image = ImageIO.read(new File("loginBackground.jpg"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.add(new InnerFrame());

            this.setVisible(true);

            createIsToggled = false;
        }

        public void paintComponent(Graphics g) {
            if (this.image != null) {
                g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                g.setColor(new Color(0, 0, 0));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        }

        private class InnerFrame extends JPanel implements ActionListener {
            InnerFrame() {
        		this.setLayout(new GridBagLayout());
        		this.setVisible(true);
        		this.setBorder(new EmptyBorder(WINDOW_WIDTH / 40, WINDOW_WIDTH / 40,
        				WINDOW_WIDTH / 40, WINDOW_WIDTH / 40));
        		 this.setBackground(new Color(75, 112, 68));

        		fieldFont = new Font("Open Sans", Font.PLAIN, 20);

        		errorLabel = new JLabel("");

        		createToggle = new JButton("Don't have an account? Click here to sign up.");
        		createToggle.addActionListener(this);
        		
                GridBagConstraints c;

                addComponent(0, new JLabel("Student ID"));
               
        		idField = new JTextField();
        		addComponent(1, idField);
        		
        		addComponent(2, new JLabel("Password"));
        		
        		passwordField = new JTextField();
        		addComponent(3, passwordField);
        		
        		loginButton = new JButton("Login");
        		loginButton.addActionListener(this);
        		c = new GridBagConstraints();
        		c.gridy = 8;
        		c.anchor = GridBagConstraints.LINE_START;
        		c.insets = new Insets(0, 20, 0, 0); // change
        		this.add(loginButton, c);
        		
        		c.insets = new Insets(100, 0, 0, 0); // change
        		c.gridy = 9;
        		this.add(createToggle, c);
        		
        		String[] grades = {"9", "10", "11", "12"};
        		nameField = new JTextField(20);
        		gradeOptions = new JComboBox(grades);

        		createAccount = new JButton("Create Account");
        		createAccount.addActionListener(this);
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

            public void showError(String error){
                errorLabel = new JLabel(error);
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = -1;
                c.gridx = 0;
                c.insets = new Insets(20, 0, 0, 0);
                c.anchor = GridBagConstraints.CENTER;
                this.add(errorLabel, c);
                errorLabel.setVisible(true);
            }

            public void hideError(){
                this.remove(errorLabel);
            }

            public void actionPerformed(ActionEvent evt) {
                Object source = evt.getSource();
                String inputId = idField.getText();
                String inputPassword = passwordField.getText();
                Student enteredStudent = findStudentByID(inputId);

                hideError();

                if (source == loginButton) {
                    if (enteredStudent != null){
                        if (enteredStudent.getPassword().equals(inputPassword)) {
                            ticketPanel = new TicketPanel(enteredStudent); //show selected student
                            showTicket(); //switch to ticket panel
                        }
                    } else {
                        showError("Student doesn't exist");
                    }
                } else if (source == createAccount) {
                    String name = nameField.getText();
                    String grade = String.valueOf(gradeOptions.getSelectedIndex());
                    if (inputId.equals("")) {
                        showError("Id cannot be blank");
                    } else if (idExists(inputId)) {
                        showError("Id already taken");
                    } else if (inputPassword.equals("")) {
                        showError("Pasword cannot be blank");
                    } else if (name.equals("")){
                        showError("Name cannot be blank");
                    } else {
                        Student s = new Student(name, inputId, grade, inputPassword);
                        students.add(s);
                        writeStudents();
                        ticketPanel = new TicketPanel(s);
                        showTicket();
                    }
                } else if (source == createToggle) {
                    if (createIsToggled){
                        createToggle.setText("Don't have an account? Click here to sign up.");

                        this.remove(nameText);
                        this.remove(nameField);
                        this.remove(gradeText);
                        this.remove(gradeOptions);
                        this.remove(createAccount);
                    } else {
                        createToggle.setText("Back to login");

                        this.remove(loginButton);

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

                        c = new GridBagConstraints();
                        c.gridy = 8;
                        c.anchor = GridBagConstraints.LINE_START;
                        c.insets = new Insets(0, 20, 0, 0); // change
                        this.add(createAccount, c);

                    }
                    createIsToggled = !createIsToggled;
                }

                revalidate();
                repaint();
            }

            public boolean idExists(String inputId) {
                for (Student s : students){
                    if (inputId.equals(s.getId())){
                        return true;
                    }
                }
                return false;
            }
        }
    }

   private class TicketPanel extends JPanel{
        private Student selectedStudent;
        private ArrayList<Student> partners;
        
        private TicketListener listener;
        private JLabel infoMessage = new JLabel();
        private JTextField cardNumber = new JTextField();
        private JLabel cardLabel = new JLabel("Enter Card Number:");
        private JButton buyButton = new JButton("Buy now!");
        private JButton refund = new JButton("Click here for a refund");
        private ButtonPanel upperPanel;
        
        TicketPanel(Student student) {
            if (student != null) {
                listener = new TicketListener();
                this.setLayout(new BorderLayout());
                this.setVisible(true);
                this.selectedStudent = student;
                partners = student.getPartners();
                
                //add all components
                upperPanel = new ButtonPanel();
                this.add(upperPanel, BorderLayout.NORTH);
                
                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBackground(new Color(235, 255, 246));
                
                CenterPanel centerPanel = new CenterPanel();
                centerPanel.setBackground(Color.WHITE);
                
                GridBagConstraints c = new GridBagConstraints();
                c.anchor = GridBagConstraints.CENTER;
                c.weighty = 1.0;
                c.fill = GridBagConstraints.VERTICAL;
                c.ipadx = 400;
                
                mainPanel.add(centerPanel, c);
                
                this.add(mainPanel);
            }
        }
        
        class CenterPanel extends JPanel {
        	private JLabel title = new JLabel("Student Preferences for Seating");
        	private PartnerPanel partnerPanel;
        	
        	CenterPanel() {
        		this.setVisible(true);
        		//this.setOpaque(false);
                this.setLayout(new GridBagLayout());
                this.setBackground(new Color(235, 255, 246));
                
                title.setOpaque(true);
                title.setBorder(new EmptyBorder(10, 10, 10, 10));
                
                partnerPanel = new PartnerPanel();
				
                GridBagConstraints c = new GridBagConstraints();
                c.anchor = GridBagConstraints.NORTHWEST;
                c.weighty = 0.05;
                c.weightx = 1.0;
                c.fill = GridBagConstraints.HORIZONTAL;
                this.add(title, c);
                
                c.gridy = 1;
                c.weighty = 1.0;
                c.weightx = 1.0; 
                this.add(partnerPanel, c);

                c = new GridBagConstraints();
                c.gridy = 2;
                //this.add(new ProfilePanel(selectedStudent.getPicture()),c);
                this.add(new AccommodationPanel(selectedStudent.getAccommodations()));
        	}
        }
        
        private class TitleLabel extends JLabel {
        	private Font titleFont;
        	
        	TitleLabel(String text) {
        		this.setText(text);
        		
        		titleFont = new Font("Open Sans", Font.PLAIN, 20);
        	}
        }
        
        private class PartnerPanel extends JPanel implements ActionListener {
        	private PreferenceRow currentPreference;
        	
        	private JTextField nameField;
            private JButton addPreferenceButton;
            private JLabel errorLabel;
            private JLabel instructLabel = new JLabel("List of students you would like to sit with: ");
            
            PartnerPanel() {
                this.setVisible(true);
                //this.setLayout(new GridBagLayout());
                //this.setOpaque(false);
                this.setFocusable(true);
                this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                //this.setBackground(Color.WHITE);
                
        		nameField = new JTextField("New student's name");
        		nameField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                nameField.addMouseListener(new MouseAdapter() {
                	@Override
                	public void mouseClicked(MouseEvent e) {
                		nameField.setText("");
                	}
                });
                nameField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (nameField.getText().length() >= 50) // limit to 50 characters
                            e.consume();
                    }
                });
                nameField.setMaximumSize(new Dimension(800, 50));
                
                addPreferenceButton = new JButton("Add Partner");
                addPreferenceButton.addActionListener(this);
        		
                errorLabel = new JLabel("");
                
                this.add(instructLabel);
                
                if (partners.size() == 0) {
	            	this.add(new EditingRow(""));
                } else {
	                for (int i = 0; i < partners.size(); i++) {
	                	this.add(new FixedRow(partners.get(i).getName()));
	                }
                }
                
                this.add(addPreferenceButton);
                addPreferenceButton.addActionListener(this);
                
                this.setBorder(new EmptyBorder(10, 10, 10, 10));
            }
            
            public void addRow(PreferenceRow row, int index) {
            	this.add(row);
            	System.out.println(index);
            	
            	//this.setComponentZOrder(row, index);
            	this.setComponentZOrder(addPreferenceButton, getComponentCount() - 1);
            	
            	this.revalidate();
            	this.repaint();
            }
            
            public PreferenceRow getPreferenceRow(String identifier) {
            	Component[] componentList = this.getComponents();
            	
            	for (Component c : componentList) {
            		if ((c instanceof PreferenceRow) && (((PreferenceRow) c).getText().equals(identifier))) {
            			return ((PreferenceRow) c);
            		}
            	}
            	// add exception
            	return null;
            }
            
            public int getIndex(String identifier) {
            	Component[] componentList = this.getComponents();
            	
            	int index = 0;
            	for (Component c : componentList) {
            		index++;
            		if ((c instanceof PreferenceRow) && (((PreferenceRow) c).getText().equals(identifier))) {
            			return index;
            		}
            	}
            	
            	return -1;
            }
            
            public void removeRow(String identifier, boolean removePartner) {
            	this.remove(this.getPreferenceRow(identifier));
            	
            	for (int j = 0; j < partners.size(); j++) {
    				if (partners.get(j).getName().equals(identifier)) {
    					partners.remove(j);
    				}
    			}
            	writeStudents();
            	
            	if (removePartner) {
            		Component[] componentList = this.getComponents();
                	if (componentList[componentList.length - 2] instanceof FixedRow) {
                		((FixedRow) componentList[componentList.length - 2]).toggleState();
                	}	
            	}
            	
            	this.revalidate();
            	this.repaint();
            }
            
            public void actionPerformed(ActionEvent e) {
            	Object source = e.getSource();
            	if (source == addPreferenceButton) {
            		Component[] componentList = this.getComponents();
            		if (!(componentList[componentList.length - 2] instanceof EditingRow)) {
            			currentPreference = new EditingRow("");
                    	this.add(currentPreference);
                    	
                    	this.setComponentZOrder(currentPreference, partners.size() + 1);
            		} else {
            			// warning
            		}
            		
            	}
            	
            	writeStudents();
            	
            	this.revalidate();
            	this.repaint();
            }
            
            private class FixedRow extends PreferenceRow implements ActionListener {
            	private JButton editButton;
            	private JLabel nameLabel;
            	
            	FixedRow(String nameLabel) {
            		super(nameLabel);
            		
            		this.setBackground(Color.BLACK);
            		this.nameLabel = new JLabel(nameLabel);
            		
            		editButton = new JButton("%"); // change later
            		editButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            		editButton.addActionListener(this);
            		
            		this.add(this.nameLabel);
            		this.add(Box.createHorizontalGlue());
            		this.add(editButton);
            		this.add(removeButton);
            	}
            	
            	public String getText() {	
        			return nameLabel.getText();
            	}
            	
            	protected void toggleState() {
            		String identifier = nameLabel.getText();
            		
            		System.out.println("ho " + identifier);
                	EditingRow selectedRow = new EditingRow(identifier);
                	addRow(selectedRow, getIndex(identifier));
            		
                	removeRow(identifier, false);
                }
            	
            	public void actionPerformed(ActionEvent e) {
            		super.actionPerformed(e);
            		
            		Object source = e.getSource();
            		if (source == editButton) {
            			this.toggleState();
            		}
            	}
            }
            
            private class EditingRow extends PreferenceRow implements ActionListener {
            	private JButton okButton;
            	private JTextField nameField;
            	
            	EditingRow(String nameLabel) {
            		super(nameLabel);
            		this.setBackground(Color.WHITE);
            		nameField = new JTextField(nameLabel);
            		
            		okButton = new JButton("OK");
            		okButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            		okButton.addActionListener(this);
            		
            		this.add(nameField);
            		this.add(Box.createHorizontalGlue());
            		this.add(okButton);
            		this.add(removeButton);
            	}
            	
            	public String getText() {	
        			return nameField.getText();
            	}
                
            	protected void toggleState() {
            		String identifier = nameField.getText();
            		
            		System.out.println("ho " + identifier);
                	FixedRow selectedRow = new FixedRow(identifier);
                	addRow(selectedRow, getIndex(identifier));
            		
                	removeRow(identifier, false);
                }
            	
            	public void actionPerformed(ActionEvent e) {
            		super.actionPerformed(e);
            		
            		Object source = e.getSource();
            		if (source == okButton) {
            			Student foundStudent = findStudentByName(nameField.getText());
                		this.remove(errorLabel);
                        if (foundStudent == TicketPanel.this.selectedStudent) {
                            errorLabel = new JLabel("You can't add yourself");
                            this.add(errorLabel);
                        } else if (TicketPanel.this.selectedStudent.getPartners().contains(foundStudent)) {
                            errorLabel = new JLabel("Person already added");
                            this.add(errorLabel);
                        } else if (foundStudent == null) {
                            errorLabel = new JLabel("Partner not Found. Ask them to register before you can add them");
                            this.add(errorLabel);
                        } else {
                        	this.toggleState();
                        	
                        	partners.add(foundStudent);
                        }
            		}
            		
                	writeStudents();
                	
                	this.revalidate();
                	this.repaint();
            	}
            }
            
            abstract private class PreferenceRow extends JPanel implements ActionListener {
            	protected JButton removeButton;
            	abstract public String getText();
            	abstract protected void toggleState();
            	
            	PreferenceRow(String nameLabel) {
            		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            		this.setFocusable(true);
            		
            		this.setMaximumSize(new Dimension(300, 100));
            		this.setAlignmentX(Component.LEFT_ALIGNMENT);
            		
            		removeButton = new JButton("-");
            		removeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            		removeButton.addActionListener(this);
            	}
            	
            	public void actionPerformed(ActionEvent e) {
            		Object source = e.getSource();
            		if (source == removeButton) {
            			removeRow(this.getText(), true);
            		}
            	}
            	
            	@Override
            	public boolean equals(Object obj) {
            		if (!(obj instanceof PreferenceRow)) {
            			return false;
            		} else if (((PreferenceRow) obj).getText().equals(this.getText())) {
            			return true;
            		} else {
            			return false;
            		}
            	}
            }
        }

        private class ButtonPanel extends JPanel implements ActionListener {
            private JButton logout;
            private JButton showFloor;

            ButtonPanel() {
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                setBorder(new EmptyBorder(10, 0, 5, 0));
                setOpaque(false);
                setFocusable(false);

                add(Box.createRigidArea(new Dimension(1100, 0)));
                logout = new JButton("Logout "); // add name, call showLogin()
                logout.addActionListener(this);

                showFloor = new JButton("Floor Plan");
                showFloor.addActionListener(this);

                this.add(logout);
                this.add(showFloor);

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
                Object source = evt.getSource();
                if (source == logout) {
                    showLogin();
                } else if (source == showFloor){
                    showFloor(ticketPanel);
                }
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

        private class AccommodationPanel extends JPanel implements ActionListener{
            private ArrayList<String> accommodations;
            private ArrayList<JButton> removes;
            private ArrayList<JLabel> labels;
            private JButton addAccommodation;
            private JTextField accommodationInput;

            AccommodationPanel(ArrayList<String> accommodations) {
                this.setVisible(true);
                this.accommodations = accommodations;
                this.setLayout(new GridBagLayout());
                removes = new ArrayList<JButton>();
                labels = new ArrayList<JLabel>();
                GridBagConstraints c;
                for (int i = 0; i < this.accommodations.size(); i++) {
                    c = new GridBagConstraints();
                    c.gridx = 0;
                    c.gridy = i;

                    removes.add(new JButton("Remove"));
                    removes.get(i).addActionListener(this);

                    this.add(removes.get(i),c);

                   c = new GridBagConstraints();
                   c.gridx = 1;
                   c.gridy = i;

                   labels.add(new JLabel(this.accommodations.get(i)));
                   this.add(labels.get(i),c);
               }

               addAccommodation = new JButton("Add Accommodation");
               accommodationInput = new JTextField("Accommodation Here");

               c = new GridBagConstraints();
               c.gridx = 0;
               c.gridy = this.accommodations.size();
               this.add(addAccommodation, c);

               c = new GridBagConstraints();
               c.gridx = 1;
               c.gridy = this.accommodations.size();
               this.add(accommodationInput, c);
           }

           @Override
           public void actionPerformed(ActionEvent e) {
               Object source = e.getSource();

               this.remove(addAccommodation);
               this.remove(accommodationInput);

               if ((source == addAccommodation) && (!accommodationInput.getText().equals("Accommodation Here"))){

                       accommodations.add(accommodationInput.getText());

                       GridBagConstraints c = new GridBagConstraints();
                       c.gridx = 0;
                       c.gridy = accommodations.size()-1;
                       removes.add(new JButton("Remove"));
                       removes.get(removes.size()-1).addActionListener(this);

                       c = new GridBagConstraints();
                       c.gridx = 1;
                       c.gridy = this.accommodations.size()-1;
                       this.add(new JLabel(accommodationInput.getText()));

                       writeStudents();
               } else {
                   int index = removes.indexOf(e.getSource());

                   this.remove(removes.get(index));
                   this.remove(labels.get(index));

                   removes.remove(index);
                   labels.remove(index);
                   accommodations.remove(index);

                   writeStudents();
               }

               GridBagConstraints c = new GridBagConstraints();
               c.gridx = 0;
               c.gridy = this.accommodations.size();
               this.add(addAccommodation, c);

               c = new GridBagConstraints();
               c.gridx = 1;
               c.gridy = this.accommodations.size();
               this.add(accommodationInput, c);

               revalidate();
               repaint();
           }
       }
    }

   private class FloorPlanPanel extends JPanel implements ActionListener{
        private JPanel fromPanel;
        private JButton exitButton;

        FloorPlanPanel(JPanel fromPanel) {
            ArrayList<Student> paidStudents = new ArrayList<Student>();
            for (Student s : students){
                if (s.hasPaid() && paidStudents.size() < Prom.maxTables*Prom.tableSize){
                    paidStudents.add(s);
                }
            }
            tables = SeatingAssignmentSystem.assignTables(paidStudents, Prom.maxTables, Prom.tableSize);
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