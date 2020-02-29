import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

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

    private ArrayList<Student> findStudentsWithName(String name) {
    	ArrayList<Student> studentsWithName = new ArrayList<Student>();
        for (Student student : students) {
            if (name.equals(student.getName())){
            	System.out.println("here");
                studentsWithName.add(student);
            }
        }
        return studentsWithName;
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
                    HashMap<String, String> keys = new HashMap<String, String>();
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
            input.close();

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
                output.print("blacklist:"+blackListString);

                output.println();
            }

            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void toUniversalFont(JPanel panel) {
    	Component[] componentList = panel.getComponents();
    	
    	for (Component component : componentList) {
    		component.setFont(DesignConstants.SMALL_FONT);
    	}
    	
        revalidate();
        repaint();
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
        		 this.setBackground(DesignConstants.MAIN_COLOUR);

        		errorLabel = new JLabel("");

        		createToggle = new JButton("Don't have an account? Click here to sign up.");
        		createToggle.addActionListener(this);

                addComponent(0, new JLabel("Student ID"));
               
        		idField = new JTextField();
        		addComponent(1, idField);
        		
        		addComponent(2, new JLabel("Password"));
        		
        		passwordField = new JTextField();
        		addComponent(3, passwordField);
        		
        		loginButton = new JButton("Login");
        		loginButton.addActionListener(this);
        		addComponent(8,loginButton);

        		addComponent(9 ,createToggle);
        		
        		String[] grades = {"9", "10", "11", "12"};
        		nameField = new JTextField(20);
        		gradeOptions = new JComboBox(grades);

        		createAccount = new JButton("Create Account");
        		createAccount.addActionListener(this);
            }
            
            public void addComponent(int gridy, JComponent component) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = gridy;

                if (component instanceof JLabel) {
                    c.insets = new Insets(10, 0, 0, 0); // space in between
                    c.anchor = GridBagConstraints.LINE_START;
                    component.setForeground(Color.WHITE);
                    component.setFont(DesignConstants.SMALL_FONT);
                } else if (component instanceof JTextField) {
                    c.insets = new Insets(5, 0, 0, 0);
                    c.fill = GridBagConstraints.HORIZONTAL;
                    component.setFont(DesignConstants.LARGE_FONT);
                } else if (component instanceof JComboBox) {
                    c.anchor = GridBagConstraints.LINE_START;
                } else if (component instanceof  JButton) {
                    c.insets = new Insets(5, 0, 0, 0);
                    c.anchor = GridBagConstraints.LINE_START;
                }

                this.add(component, c);
            }

            public void showError(String error){
                errorLabel = new JLabel(error);
                addComponent(10, errorLabel);
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
                        } else {
                            showError("Password Incorrect");
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

                        addComponent(8,loginButton);

                    } else {
                        createToggle.setText("Back to login");

                        this.remove(loginButton);

                        nameText = new JLabel("Name");
                        addComponent(4, nameText);
                        addComponent(5, nameField);

                        gradeText = new JLabel("Grade");
                        addComponent(6, gradeText);

                        addComponent(7, gradeOptions);

                        addComponent(8, createAccount);

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

        private JLabel infoMessage = new JLabel();
        private ButtonPanel upperPanel;
        
        private int X_PADDING = 200;
        
        TicketPanel(Student student) {
            if (student != null) {
                this.setLayout(new BorderLayout());
                this.setVisible(true);
                this.selectedStudent = student;
                partners = student.getPartners();
                
                //add all components
                upperPanel = new ButtonPanel();
                this.add(upperPanel, BorderLayout.NORTH);
                
                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBackground(DesignConstants.BACK_COLOUR);
                
                CenterPanel centerPanel = new CenterPanel();
                //centerPanel.setBackground(Color.WHITE);
                
                JScrollPane scrollFrame = new JScrollPane(centerPanel);
                //centerPanel.setAutoscrolls(true);
                scrollFrame.setPreferredSize(new Dimension(600, 400));
                scrollFrame.setBorder(null);
                
                GridBagConstraints c = new GridBagConstraints();
                c.anchor = GridBagConstraints.CENTER;
                c.weighty = 1.0;
                c.fill = GridBagConstraints.VERTICAL;
                c.ipadx = X_PADDING;
                
                mainPanel.add(scrollFrame, c);
              //JScrollPane pane = new JScrollPane(centerPanel);

                this.add(mainPanel);
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

            }
        }
        
        /*
        public void paintComponent(Graphics g) {
        	super.paintComponent(g);
        	
        	this.revalidate();
        	this.repaint();
        }
        */
        
        
        class CenterPanel extends JPanel {
        	private JLabel title = new JLabel("Student Preferences for Seating");
        	private PartnerPanel partnerPanel;
        	private int currentYPos = 1;
        	
        	CenterPanel() {
        		this.setVisible(true);
        		//this.setOpaque(false);
        		this.setPreferredSize(new Dimension(500, 2000));
        		//this.setMaximumSize(new Dimension(WINDOW_WIDTH - X_PADDING * 2, WINDOW_HEIGHT));
                this.setLayout(new GridBagLayout());
                this.setBackground(DesignConstants.BACK_COLOUR);
                
                partnerPanel = new PartnerPanel();
				
                JLabel studentPreferences = new JLabel("Student Preferences for Seating");
                //studentPreferences.setBackground(Color.WHITE);
                //studentPreferences.setOpaque(true);
                addComponent(currentYPos, studentPreferences);
                
                addComponent(currentYPos, partnerPanel);
                addComponent(currentYPos, new JLabel("ID"));
                
                addComponent(currentYPos, new JLabel("Local School Cash"));
                addComponent(currentYPos, new PaymentPanel());
                addComponent(currentYPos, new AccommodationPanel());
                addComponent(currentYPos, new ProfilePanel(selectedStudent.getPicture()));
                //this.setSize(new Dimension(WINDOW_WIDTH * 3 / 7, WINDOW_HEIGHT));
        	}
        	
        	public void addComponent(int gridy, JComponent component) {
        		currentYPos += 2;
        		
        		component.setBackground(Color.WHITE);
        		component.setOpaque(true);
        		int yPadding;
        		GridBagConstraints c = new GridBagConstraints();
        		if (component instanceof JLabel) {
        			System.out.println(((JLabel) component).getText());
        			yPadding = 20;
                    component.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10, true));
                    component.setFont(DesignConstants.MEDIUM_FONT);
        		} else {
        			yPadding = 5;
        			component.setBorder(BorderFactory.createLineBorder(Color.WHITE, 20, true));
        		}
        		
        		/*
        		JPanel spacePanel = new JPanel();
                spacePanel.setBackground(DesignConstants.BACK_COLOUR);
                c.gridy = gridy - 1;
                c.weightx = 1.0;
                c.ipady = yPadding;
            	
                c.fill = GridBagConstraints.HORIZONTAL;
                this.add(spacePanel, c);
        		*/
        		c.gridy = gridy - 1;
                c.weightx = 1.0;
                c.ipady = yPadding;
                c.fill = GridBagConstraints.HORIZONTAL;
        		this.add(Box.createRigidArea(new Dimension(0, 0)), c);
        		
                c = new GridBagConstraints();
        		if (gridy == 13) {
        			c.anchor = GridBagConstraints.NORTHWEST;
        			c.weighty = 1.0;

        		} else {
        			c.anchor = GridBagConstraints.LINE_START;
        		}
        		
        		c.gridy = gridy;
                c.weightx = 1.0;
                c.fill = GridBagConstraints.HORIZONTAL;
                //component.setBorder(new EmptyBorder(10, 10, 10, 10));
                this.add(component, c);
        	}
        }
        
        private class PartnerPanel extends JPanel implements ActionListener {
        	private JTextField nameField;
            private JButton addPreferenceButton;
            private JLabel instructLabel = new JLabel("List of students you would like to sit with: ");
            private int editingIndex;
            
            PartnerPanel() {
                this.setVisible(true);
                //this.setLayout(new GridBagLayout());
                //this.setOpaque(false);
                this.setFocusable(true);
                this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                //this.setBackground(Color.WHITE);
                
                /*
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
                */
                addPreferenceButton = new JButton("Add Partner");
                addPreferenceButton.addActionListener(this);
                
                this.add(instructLabel);

                if (partners.size() == 0) {
                	RowPair partnerPair = new RowPair("New Studet's Name", false);
                	this.add(partnerPair);
                } else {
	                for (int i = 0; i < partners.size(); i++) {
	                	RowPair partnerPair = new RowPair(partners.get(i).getName(), true);
	                	this.add(partnerPair);
	                }
                }
                
                editingIndex = -1;
                
                this.add(addPreferenceButton);
                addPreferenceButton.addActionListener(this);
                
                this.setBorder(new EmptyBorder(10, 10, 10, 10));
                toUniversalFont(this);
            }
            
            public void actionPerformed(ActionEvent e) {
            	Object source = e.getSource();
            	if (source == addPreferenceButton) {
            		Component[] componentList = this.getComponents();
            		int index = componentList.length - 2;
            		if (index == 0) {
            			RowPair partnerPair = new RowPair("New Studet's Name", false);
                    	this.add(partnerPair);
            		} else {
	            		while ((index != 0) && (!((RowPair) componentList[index]).isVisible())) {
	            			index--;
	            		}
	            		
	            		if ((componentList[componentList.length - 2] instanceof RowPair) &&
	            				(!(((RowPair) componentList[componentList.length - 2]).editingIsVisible()))) {
	            			this.remove(addPreferenceButton);
	
	            			RowPair partnerPair = new RowPair("New Studet's Name", false);
	                    	this.add(partnerPair);
	                    	
	                    	this.add(addPreferenceButton);
	                    	addPreferenceButton.addActionListener(this);
	            		}
	            		
	            		editingIndex = componentList.length - 1;
            		}
            	}
            	
            	this.revalidate();
            	this.repaint();
            }
            
            private class RowPair extends JPanel {
            	private FixedRow fixed;
            	private EditingRow editing;
            	
            	RowPair(String text, boolean fixedVisiblity) {
            		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            		
            		fixed = new FixedRow(text);
            		editing = new EditingRow(text);
            		
            		if (fixedVisiblity) {
            			toFixedVisibility();
            		} else {
            			toEditingVisibility();
            		}
            		
            		this.add(fixed);
            		this.add(editing);
            	}
            	
            	private void copyToFixed() {
            		fixed.setText(editing.getText());
            	}
            	
            	private void copyToEditing() {
            		editing.setText(fixed.getText());
            	}
            	
            	private void addErrorToFixed(String text) {
            		fixed.addErrorLabel(text);
            	}
            	
            	public void toFixedVisibility() {
            		fixed.setVisible(true);
            		editing.setVisible(false);
            	}
            	
            	private void toEditingVisibility() {
            		fixed.setVisible(false);
            		editing.setVisible(true);
            	}
            	
            	public boolean editingIsVisible() {
            		if (editing.isVisible()) {
            			return true;
            		} else {
            			return false;
            		}
            	}
            	
            	private void hidePair() {
                	this.setVisible(false);
                	
                	/*
                	if (row instanceof EditingRow) {
                		editingIndex = -1;
                	}
                	*/
                	
                	for (int j = 0; j < partners.size(); j++) {
        				if (partners.get(j).getName().equals(fixed.getText())) {
        					partners.remove(j);
        				}
        			}
                	writeStudents();
            	}
            	
                private class FixedRow extends PreferenceRow implements ActionListener {
                	private JButton editButton;
                	private JLabel nameLabel;
                	private JLabel personAddedLabel;

                	FixedRow(String nameLabel) {
                		super(nameLabel);
                		
                		//this.setBackground(Color.BLACK);
                		this.nameLabel = new JLabel(nameLabel);
                		
                		editButton = new JButton("%"); // change later
                		editButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
                		editButton.addActionListener(this);

                		this.add(this.nameLabel);
                		this.add(Box.createHorizontalGlue());
                		this.add(editButton);
                		this.add(removeButton);
                		
                		toUniversalFont(this);
                	}
                	
                	public String getText() {	
            			return nameLabel.getText();
                	}
                	
                	public void actionPerformed(ActionEvent e) {
                		super.actionPerformed(e);
                		
                		Object source = e.getSource();
                		if (source == editButton) {
                			copyToEditing();
                			toEditingVisibility();
                			
                			if (editingIndex != -1) {
                				
                			}
                			
                		}
                	}

    				@Override
    				public void setText(String newText) {
    					nameLabel.setText(newText);
    				}
                }
                
                private class EditingRow extends PreferenceRow implements ActionListener {
                	private JButton okButton;
                	private JTextField nameField;
                	
                	EditingRow(String nameLabel) {
                		super(nameLabel);
                		this.setBackground(Color.WHITE);
                		
                		nameField = new JTextField(nameLabel);
                		nameField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                        nameField.addMouseListener(new MouseAdapter() {
                        	@Override
                        	public void mouseClicked(MouseEvent e) {
                        		nameField.setText("");
                        	}
                        });
                        
                        nameField.setMaximumSize(new Dimension(300, 100));
                		/*
                        nameField.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyTyped(KeyEvent e) {
                                if (nameField.getText().length() >= 50) // limit to 50 characters
                                    e.consume();
                            }
                        });
                		*/
                		
                		okButton = new JButton("OK");
                		okButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
                		okButton.addActionListener(this);
                		
                		this.add(nameField);
                		this.add(Box.createHorizontalGlue());
                		this.add(okButton);
                		this.add(removeButton);
                		
                		toUniversalFont(this);
                	}
                	
                	public String getText() {	
            			return nameField.getText();
                	}
                	
                	public void actionPerformed(ActionEvent e) {
                		super.actionPerformed(e);
                		
                		Object source = e.getSource();
                		if (source == okButton) {
                			ArrayList<Student> foundStudents = findStudentsWithName(nameField.getText());
                    		
                			if (foundStudents == null) {
                                this.addErrorLabel("Partner not Found. Ask them to register before you can add them");
                            } else if (foundStudents.size() == 1) {
                            	if (foundStudents.get(0) == TicketPanel.this.selectedStudent) {
    	                            this.addErrorLabel("You can't add yourself");
    	                        } else if (TicketPanel.this.selectedStudent.getPartners().contains(foundStudents.get(0))) {
    	                            copyToFixed();
    	                            toFixedVisibility();
    	                            addErrorToFixed("Person already added");
    	                        } else {
    	                        	copyToFixed();
    	                            toFixedVisibility();
    	                        	
    	                        	partners.add(foundStudents.get(0));
    	                        }
                            } else {
                            	this.addErrorLabel("More than one student with that name");
                            }
                		}
                    	writeStudents();
                    	
                    	this.revalidate();
                    	this.repaint();
                	}

    				@Override
    				public void setText(String newText) {
    					nameField.setText(newText);
    				}
                }
                
                abstract private class PreferenceRow extends JPanel implements ActionListener {
                	protected JButton removeButton;
                	abstract public String getText();
                	abstract public void setText(String newText);
                	private JLabel errorLabel;
                	private final int RIGHT_BORDER = 400;
                	
                	PreferenceRow(String nameLabel) {
                		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                		this.setFocusable(true);
                		
                		//this.setMaximumSize(new Dimension(WINDOW_WIDTH, 100));
                		this.setBorder(new EmptyBorder(3, 3, 3, RIGHT_BORDER));
                		this.setAlignmentX(Component.LEFT_ALIGNMENT);
                		
                		removeButton = new JButton("-");
                		removeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
                		removeButton.addActionListener(this);
                	}
                	
                	public void actionPerformed(ActionEvent e) {
                		Object source = e.getSource();
                		if (source == removeButton) {
                			hidePair();
                		}
                	}
                	
                	public void addErrorLabel(String errorText) {
                		if (errorLabel == null) {
                			errorLabel = new JLabel(errorText);
                			//personAddedLabel = new JLabel("Person already added");
                			this.add(Box.createRigidArea(new Dimension(5, 0)));
                			this.add(errorLabel);
                			System.out.println(RIGHT_BORDER - errorLabel.getPreferredSize().getWidth() - 5);
                			this.setBorder(new EmptyBorder(3, 3, 3,
                					(int) (RIGHT_BORDER - errorLabel.getPreferredSize().getWidth() - 5)));
                		} else {
                			errorLabel.setText(errorText);
                		}
                	}
                	
                	@Override
                	public boolean equals(Object obj) {
                		if (!obj.getClass().equals(this.getClass())) {
                			//System.out.println("not ")
                			return false;
                		} else if (((PreferenceRow) obj).getText().equals(this.getText())) {
                			return true;
                		} else {
                			return false;
                		}
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
                setFocusable(false);
                setBackground(DesignConstants.MAIN_COLOUR);

                add(Box.createRigidArea(new Dimension(1100, 0)));
                logout = new JButton("Logout "); // add name, call showLogin()
                logout.addActionListener(this);

                showFloor = new JButton("Floor Plan");
                showFloor.addActionListener(this);
                
                this.add(logout);
                this.add(showFloor);
                
                toUniversalFont(this);
                
                revalidate();
                repaint();
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

        private class PaymentPanel extends JPanel implements ActionListener {
            //private JTextField cardNumber = new JTextField();
            private JLabel cardLabel = new JLabel("Enter Card Number:");
            private JButton buyButton = new JButton("Buy now!");
            private JButton refundButton = new JButton("Click here for a refund");

        	PaymentPanel() {
        		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                //do all layout

        		this.setBorder(new EmptyBorder(10, 10, 10, 10));

                // this.add(infoMessage, BorderLayout.CENTER);

        		buyButton.addActionListener(this);
        		refundButton.addActionListener(this);

                this.add(cardLabel);
                //this.add(cardNumber);
                this.add(buyButton);
                this.add(refundButton);
        	}

            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == buyButton){
                    cardLabel.setVisible(false);
                    //cardNumber.setVisible(false);
                    buyButton.setVisible(false);
                    refundButton.setVisible(true);
                    selectedStudent.setPaid(true);

                } else if (source == refundButton) {
                    cardLabel.setVisible(true);
                    //cardNumber.setVisible(true);
                    buyButton.setVisible(true);
                    refundButton.setVisible(false);
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

            AccommodationPanel() {
                this.setVisible(true);
                this.accommodations = selectedStudent.getAccommodations();
                this.setLayout(new GridBagLayout());
                removes = new ArrayList<JButton>();
                labels = new ArrayList<JLabel>();
                GridBagConstraints c;
                for (int i = 0; i < this.accommodations.size(); i++) {
                    c = new GridBagConstraints();
                    c.gridx = 0;
                    c.gridy = i;
                    JButton r = new JButton("Remove");
                    r.addActionListener(this);
                    this.add(r,c);
                    removes.add(r);

                    c = new GridBagConstraints();
                    c.gridx = 1;
                    c.gridy = i;
                    JLabel l = new JLabel(this.accommodations.get(i));
                    this.add(l,c);
                    labels.add(l);
               }

               addAccommodation = new JButton("Add Accommodation");
               accommodationInput = new JTextField("Accommodation Here");

               c = new GridBagConstraints();
               c.gridx = 0;
               c.gridy = this.accommodations.size();
               this.add(addAccommodation, c);
               addAccommodation.addActionListener(this);

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

                    GridBagConstraints c = new GridBagConstraints();
                    c.gridx = 0;
                    c.gridy = accommodations.size();
                    JButton r = new JButton("Remove");
                    r.addActionListener(this);
                    this.add(r,c);
                    removes.add(r);

                    c = new GridBagConstraints();
                    c.gridx = 1;
                    c.gridy = this.accommodations.size();
                    JLabel l = new JLabel(accommodationInput.getText());
                    this.add(l,c);
                    labels.add(l);

                    accommodations.add(accommodationInput.getText());

                    accommodationInput.setText("Accommodation Here");

                    writeStudents();
                } else {
                    int index = removes.indexOf(source);

                    if (index != -1) {
                        this.removeAll();

                        removes.remove(index);
                        labels.remove(index);
                        accommodations.remove(index);

                        GridBagConstraints c;
                        for (int i = 0; i < this.accommodations.size(); i++) {
                            c = new GridBagConstraints();
                            c.gridx = 0;
                            c.gridy = i;

                            this.add(removes.get(i),c);

                            c = new GridBagConstraints();
                            c.gridx = 1;
                            c.gridy = i;

                            this.add(labels.get(i),c);
                        }

                        writeStudents();
                    }
                }

                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = this.accommodations.size();
                this.add(addAccommodation, c);
                addAccommodation.addActionListener(this);

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
    
    public static final class DesignConstants {
		public static final Color BACK_COLOUR = Color.BLACK;//new Color((float) (130 / 255.0), (float) (235 / 255.0), 
    			//(float) (33 / 255.0), (float) 0.3);
        public static final Color MAIN_COLOUR = new Color(75, 112, 68);
        
        public static final Font LARGE_FONT = new Font("Garamond", Font.PLAIN, 20);
        public static final Font MEDIUM_FONT = new Font("Garamond", Font.BOLD, 15);
        public static final Font SMALL_FONT = new Font("Garamond", Font.BOLD, 13);

        DesignConstants() {
        	throw new AssertionError();
        }
    }
}