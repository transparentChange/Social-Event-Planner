import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.nio.file.Path;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Ticketing System used by the Prom Project.
 * @see Prom
 * @see Student
 * @see Table
 * @author Daksh & Matthew
 */
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

    /**
     * Constructs the TicketingSystem. Sets displayed panel to login.
     * @param students The master list of students.
     * @param tables The master list of tables.
     */
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

    /**
     * Sets the displayed panel to the ticket panel when the student is logged in.
     */
    private void showTicket() {
        this.removeAll();
        this.add(this.ticketPanel);
        this.ticketPanel.setVisible(true);
        this.ticketPanel.requestFocus();
        this.updateUI();
    }

    /**
     * Sets the displayed panel to the login panel when the student logs off.
     */
    private void showLogin() {
        this.removeAll();
        this.loginPanel = new LoginPanel(); //reset the login panel
        this.add(this.loginPanel);
        this.loginPanel.setVisible(true);
        this.loginPanel.requestFocus();
        this.updateUI();
    }

    /**
     * Sets the displayed panel to the floor plan panel.
     * @param fromPanel the panel from which the panel was called from so it can shift back.
     */
    private void showFloor(JPanel fromPanel){
        floorPanel = new FloorPlanPanel(fromPanel);
        this.removeAll();
        this.add(this.floorPanel);
        this.floorPanel.setVisible(true);
        this.floorPanel.requestFocus();
        this.updateUI();
    }

    /**
     * Adds a student to the master list of students.
     * @param student the student.
     */
    private void addStudent(Student student) {
        this.students.add(student);
    }

    /**
     * Removes a student from the master list of students. Never used, but here for UML.
     * @param student the student.
     */
    private void removeStudent(Student student) {
        this.students.remove(student);
    }

    /**
     * Finds a student based off of ID. Used by LoginPanel and PartnerPanel
     * @param id The id.
     * @return The student with the id. Null if not found.
     */
    private Student findStudentByID(String id) {
        for (Student student : students){
            if (student.getId().equals(id)){
                return student;
            }
        }
        return null;
    }

    /**
     * Loads all students from file and recreates the master list
     */
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

    /**
     * Writes all students to file to save all student information.
     */
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
    
    public void toUniversalFont(Container panel) {
    	Component[] componentList = panel.getComponents();
    	String fullClassName = panel.getClass().getName();
    	String className = fullClassName.substring(fullClassName.lastIndexOf("$") + 1);
    	
    	for (Component component : componentList) {
    		if (((component instanceof JPanel) || (component instanceof JViewport) || 
    				(component instanceof JScrollPane))) {
    			toUniversalFont((Container) component);
    		} else if (component instanceof JButton) {
    			component.setFont(DesignConstants.SMALL_BOLD_FONT);
    		} else if (!((className.equals("CenterPanel")) && (component instanceof JLabel))
    				&& (!className.equals("LoginPanel")) && (!className.equals("OrderInfoPanel"))) {
    				component.setFont(DesignConstants.GENERAL_FONT);
			}
    	}
    	
        revalidate();
        repaint();
    }
    //-------------------------------------------------------------------------------------------------------------------------
    // beginning of TicketingSystem inner classes
    //-------------------------------------------------------------------------------------------------------------------------
    /*
     * LoginPanel
     * This class is a JPanel that contains the InnerLoginPanel and displays the background image located
     * under the name "loginBakground.jpg" in the background
     */
    private class LoginPanel extends JPanel {
        BufferedImage image = null;
        
        LoginPanel() {
            this.setLayout(new GridBagLayout());

            try {
                image = ImageIO.read(new File("loginBackground.jpg"));
            } catch (Exception e) {
                e.printStackTrace();	
            }

            this.add(new InnerLoginPanel());
        }
        
        /*
         * paintComponent
         * This method draws the BufferedImage image if it is not equal to null
         * @param g, a Graphics object
         * @throws RuntimeException, indicated that the image is equal to null
         */
        public void paintComponent(Graphics g) {
            if (this.image != null) {
                g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
            	throw new RuntimeException("Image does not exist");
            }
        }
        
        /*
         * InnerLoginPanel
         * This class is a JPanel that will be shown in the center of the window. It will contain
         * all the necessary fields and buttons to login and create an account. Includes some verification
         * of user input.
         */
        private class InnerLoginPanel extends JPanel implements ActionListener {
            private JLabel nameText;
            private JLabel gradeText;
            private JLabel errorLabel;
            
            private JTextField idField = new JTextField();
            private JTextField nameField;
            private JTextField passwordField = new JTextField();
            private JComboBox<String> gradeOptions;
            
            private JButton loginButton;
            private JButton createAccountButton;
            private JButton toggleButton;
        	
        	private String TO_LOGIN_MESSAGE = "Back to login";
        	private String TO_CREATE_MESSAGE = "Don't have an account? Click here to sign up.";
        	
            InnerLoginPanel() {
        		this.setLayout(new GridBagLayout());
        		this.setBorder(new EmptyBorder(WINDOW_WIDTH / 40, WINDOW_WIDTH / 40,
        				WINDOW_WIDTH / 40, WINDOW_WIDTH / 40));
        		this.setBackground(DesignConstants.MAIN_COLOUR);

        		errorLabel = new JLabel("");

        		toggleButton = new JButton("Don't have an account? Click here to sign up.");
        		toggleButton.addActionListener(this);

                addComponent(0, new JLabel("Student ID"));
        		addComponent(1, idField);
        		
        		addComponent(2, new JLabel("Password"));
        		addComponent(3, passwordField);
        		
        		loginButton = new JButton("Login");
        		loginButton.addActionListener(this);
        		addComponent(8,loginButton);

        		addComponent(9, toggleButton);
        		
        		String[] grades = {"9", "10", "11", "12"};
        		nameField = new JTextField(20);
        		gradeOptions = new JComboBox<String>(grades);

        		createAccountButton = new JButton("Create Account");
        		createAccountButton.addActionListener(this);
            }
            
            /*
             * addComponent
             * This method adds the component passed in as a parameter to the specified gridy position.
             * It also adjusts other design aspects of the component such as insets, font, and colour
             * @param gridy, an integer that specifies the vertical position on the panel relative 
             * to other components through Gridbaglayout
             * @param component, the JComponent that is being added
             */
            private void addComponent(int gridy, JComponent component) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = gridy;

                if (component instanceof JLabel) {
                    c.insets = new Insets(10, 0, 0, 0);
                    c.anchor = GridBagConstraints.LINE_START;
                    component.setForeground(Color.WHITE);
                    component.setFont(DesignConstants.SMALL_BOLD_FONT);
                } else if (component instanceof JTextField) {
                    c.insets = new Insets(5, 0, 0, 0);
                    c.fill = GridBagConstraints.HORIZONTAL;
                    component.setFont(DesignConstants.LARGE_FONT);
                } else if (component instanceof JComboBox) {
                    c.anchor = GridBagConstraints.LINE_START;
                } else if (component instanceof  JButton) {
                    c.insets = new Insets(5, 0, 0, 0);
                    c.anchor = GridBagConstraints.LINE_START;
                    component.setFont(DesignConstants.SMALL_BOLD_FONT);
                }

                this.add(component, c);
            }
            
            /*
             * showError
             * This method sets the errorLabel text to the error String parameter, 
             * and adds the JLabel to the bottom of the panel
             * @param the String object that will be displayed on the errorLabel 
             */
            private void showError(String error){
                errorLabel.setText(error);
                addComponent(10, errorLabel);
            }

            /*
             * actionPerformed
             * This method determines what happens after the loginButton, createAccountButton, or toggleButton JButtons
             * are pressed
             * @param e, an ActionEvent object
             */
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                
                String inputId = idField.getText();
                String inputPassword = passwordField.getText();
                Student enteredStudent = findStudentByID(inputId);

                this.remove(errorLabel);

                if (source == loginButton) {
                    if ((enteredStudent != null) && (enteredStudent.getPassword().equals(inputPassword))) {
                        ticketPanel = new TicketPanel(enteredStudent);
                        showTicket();
                    } else if (enteredStudent != null) {
                    	showError("Password Incorrect");
                    } else {
                        showError("Student doesn't exist");
                    }
                } else if (source == createAccountButton) {
                    String name = nameField.getText();
                    String grade = String.valueOf(gradeOptions.getSelectedIndex());
                    
                    if (inputId.equals("")) {
                        showError("Id cannot be blank");
                    } else if (findStudentByID(inputId) != null) {
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
                } else if (source == toggleButton) {
                    if (toggleButton.getText().equals(TO_LOGIN_MESSAGE)) {
                        toggleButton.setText(TO_CREATE_MESSAGE);

                        this.remove(nameText);
                        this.remove(nameField);
                        this.remove(gradeText);
                        this.remove(gradeOptions);
                        this.remove(createAccountButton);

                        addComponent(8,loginButton);
                    } else {
                        toggleButton.setText(TO_LOGIN_MESSAGE);

                        this.remove(loginButton);

                        nameText = new JLabel("Name");
                        addComponent(4, nameText);
                        
                        addComponent(5, nameField);

                        gradeText = new JLabel("Grade");
                        addComponent(6, gradeText);

                        addComponent(7, gradeOptions);
                        addComponent(8, createAccountButton);

                    }
                }

                revalidate();
                repaint();
            }
        } // end of InnerLoginPanel
    } // end of LoginPanel
    
    public class TicketPanel extends JPanel{
        private Student selectedStudent;
        private ArrayList<Student> partners;
        private ArrayList<Student> blackList;
        private BufferedImage image;
        private ButtonPanel upperPanel;
        
        private final int SCROLL_FRAME_WIDTH = (int) (WINDOW_WIDTH * 0.58);
        
        TicketPanel(Student student) {
        	try {
                image = ImageIO.read(new File("loginBackground.jpg"));
            } catch (Exception e) {
                e.printStackTrace();	
            }
        	
            if (student != null) {
                this.setLayout(new BorderLayout());
                this.setVisible(true);
                this.selectedStudent = student;
                partners = student.getPartners();
                blackList = student.getBlacklist();
                
                //add all components
                upperPanel = new ButtonPanel();
                this.add(upperPanel, BorderLayout.NORTH);
                
                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBackground(DesignConstants.BACK_COLOUR);
                
                CenterPanel centerPanel = new CenterPanel();
                centerPanel.setBackground(DesignConstants.BACK_COLOUR);
                
                JScrollPane scrollFrame = new JScrollPane(centerPanel);
                scrollFrame.setPreferredSize(new Dimension(SCROLL_FRAME_WIDTH, 0));
                scrollFrame.setBorder(null);
                
                GridBagConstraints c = new GridBagConstraints();
                c.anchor = GridBagConstraints.CENTER;
                c.weighty = 1.0;
                c.fill = GridBagConstraints.VERTICAL;
                
                mainPanel.add(scrollFrame, c);

                this.add(mainPanel);
                
                toUniversalFont(this);
            }
        }
        
        public void paintComponent(Graphics g) {
            if (this.image != null) {
                g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                g.setColor(new Color(0, 0, 0));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
            
            this.revalidate();
            this.repaint();
        }
        
        //-------------------------------------------------------------------------------------------------------
        // start of TicketPanel inner classes
        //-------------------------------------------------------------------------------------------------------        
        /*
         * ButtonPanel
         * This class is a JPanel containing a logoutButton and a showFloorButton.
         * It is meant to be displayed at the top of the window. 
         */
        private class ButtonPanel extends JPanel implements ActionListener {
            private JButton logoutButton;
            private JButton showFloorButton;

            ButtonPanel() {
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                setBorder(new EmptyBorder(10, 0, 5, 0));
                setBackground(DesignConstants.MAIN_COLOUR);

                add(Box.createRigidArea(new Dimension((int) (0.81 * WINDOW_WIDTH), 0)));
                logoutButton = new JButton("Logout ");
                logoutButton.addActionListener(this);

                showFloorButton = new JButton("Floor Plan");
                showFloorButton.addActionListener(this);
                
                this.add(logoutButton);
                this.add(showFloorButton);
                
                revalidate();
                repaint();
            }
            
            /*
             * actionPerformed
             * This method calls the appropriate methods for showing the floor plan or the login
             * when one of the buttons is pressed
             * @param e, an ActionEvent object
             */
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == logoutButton) {
                    showLogin();
                } else if (source == showFloorButton){
                    showFloor(ticketPanel);
                }
            }
        }
        
        public class CenterPanel extends JPanel {
        	private int currentYPos = 1;
        	private ResolveNamesPanel resolvePanel;
        	private PartnerPanel partnerPanel;
        	private JLabel infoMessage = new JLabel();
        	private final Color CENTER_COLOUR = new Color(192, 171, 140);

        	CenterPanel() {
        		this.setVisible(true);
                this.setLayout(new GridBagLayout());
                this.setBackground(CENTER_COLOUR);
                
                partnerPanel = new PartnerPanel();
                resolvePanel = new ResolveNamesPanel();
				
                JLabel titleLabel = new JLabel("RHHS Prom Registration");
                initComponent(titleLabel, 10);
                titleLabel.setFont(DesignConstants.LARGE_BOLD_FONT);

                GridBagConstraints c = new GridBagConstraints();
        		c.gridy = currentYPos;
                c.weightx = 1.0;
                c.fill = GridBagConstraints.HORIZONTAL;
                this.add(titleLabel, c);

                updateInfoLabel();
                infoMessage.setFont(DesignConstants.GENERAL_FONT);
                initComponent(infoMessage, 5);

                c.gridy = currentYPos + 1;
                this.add(infoMessage, c);
                currentYPos += 2;

                addComponent(new JLabel("Student Preferences for Seating"));
                addComponent(partnerPanel);
                addComponent(new JLabel("Students' IDs"));
                addComponent(resolvePanel);
                addComponent(new JLabel("Local School Cash"));
                addComponent(new PaymentPanel());
                addComponent(new JLabel("Accommodations"));
                addComponent(new AccommodationPanel());
                addComponent(new ProfilePanel(selectedStudent.getPicture()));
        	}
        	
        	private void updateInfoLabel() {
        		if ((selectedStudent.hasPaid()) && (selectedStudent.getPartners().size() == 0)) {
                    infoMessage.setText("Woo! You're coming to Prom! Make sure to set your preferences");
                } else if (selectedStudent.hasPaid()) {
                    infoMessage.setText("Woo! You're set for Prom! You can still add more or edit your preferences");
                } else {
                    infoMessage.setText("You're almost there! Make sure to purchase your ticket");
                }
        	}

        	private void initComponent(JComponent label, int borderY) {
        		label.setBackground(Color.WHITE);
        		label.setOpaque(true);
        		label.setBorder(BorderFactory.createMatteBorder(borderY, 20, borderY, 20, Color.WHITE));
        	}

        	private void addComponent(JComponent component) {
        		int yInterPadding;
        		GridBagConstraints c = new GridBagConstraints();
        		if (component instanceof JLabel) {
        			yInterPadding = 20;
        			initComponent(component, 10);
                    component.setFont(DesignConstants.MEDIUM_BOLD_FONT);
        		} else {
        			initComponent(component, 15);
        			yInterPadding = 5;
        		}
                c = new GridBagConstraints();
        		if (currentYPos == 11) {
        			c.anchor = GridBagConstraints.NORTHWEST;
        			c.weighty = 1.0;
        		} else {
        			c.anchor = GridBagConstraints.LINE_START;
        		}
        		
        		c.insets = new Insets(yInterPadding, 0, 0, 0);
        		c.gridy = currentYPos + 1;
                c.weightx = 1.0;
                c.fill = GridBagConstraints.HORIZONTAL;
                this.add(component, c);

                currentYPos++;
        	}
        	
        	//------------------------------------------------------------------------------------------------
        	// beginning of CenterPanel inner classes
        	//------------------------------------------------------------------------------------------------
        	/*
             * DynamicBoxLayoutPanel
             * This abstract class contains an addButton and an infoLabel, as well as methods to add and
             * remove components contained in the JPanel
             */
            abstract private class DynamicBoxLayoutPanel extends JPanel implements ActionListener {
            	protected JButton addButton;
            	protected abstract void defineAddButton();
            	
            	protected JLabel infoLabel;
            	protected abstract void defineInfoLabel();
            	
            	protected Component areaForAddButton = Box.createRigidArea(new Dimension(0, 10));
            	
            	DynamicBoxLayoutPanel() {
            		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            		
            		defineAddButton();
            		addButton.addActionListener(this);
            		
            		defineInfoLabel();
            		infoLabel.setBorder(DesignConstants.INFO_LABEL_BORDER);
            		this.add(infoLabel);
            	}
            	
            	/*
            	 * addAdditiveComponent
            	 * This method adds the JPanel parameter row, making sure the addButton ends up at the bottom
            	 * @param row, the JPanel that is to be added
            	 */
            	public void addAdditiveComponent(JPanel row) {
            		this.remove(addButton);
        			this.remove(areaForAddButton);
            		
            		this.add(row);
            		
                	this.add(areaForAddButton);
            		this.add(addButton);
            		
            		this.revalidate();
            		this.repaint();
            	}
            	
            	/*
            	 * removeAdditiveComponent
            	 * This method removes the JPanel parameter row
            	 * @param row, the JPanel that is to be removed
            	 */
            	public void removeAdditiveComponent(JPanel row) {
            		this.remove(row);	
            		this.revalidate();
            		this.repaint();
            	}
            }
        	
        	/*
        	 * RowOption
        	 * This abstract class defines a row with a removeButton and errorLabel, as well as a LINE_AXIS BoxLayout
        	 */
        	abstract public class RowOption extends JPanel implements ActionListener {
            	protected JButton removeButton;
            	protected JLabel errorLabel;
            	
            	abstract public String getText();
            	abstract public void setText(String newText);
            	
            	protected final int SPACE_TO_BUTTONS = (int) (SCROLL_FRAME_WIDTH * 0.375);
            	
            	RowOption() {
            		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            		this.setFocusable(true);
            		
            		this.setBorder(new EmptyBorder(3, 10, 3, 3));
            		this.setAlignmentX(Component.LEFT_ALIGNMENT);
            		
            		errorLabel = new JLabel();
            		errorLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

            		removeButton = new JButton("-");
            		removeButton.addActionListener(this);
            	}
            }
        	
        	/*
        	 * EditingRow
        	 * This abstract class is a RowOption that adds a JTextField rowField 
        	 */
        	abstract private class EditingRow extends RowOption implements ActionListener {
            	protected JTextField rowField;
            	private final int NUM_COLUMNS = 20;

            	EditingRow(String nameLabel) {
            		super();
            		
            		rowField = new JTextField(nameLabel);
            		rowField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                    rowField.setMaximumSize(new Dimension(SPACE_TO_BUTTONS, NUM_COLUMNS));
                    rowField.setColumns(20);
            		
            		this.add(rowField);
            		this.add(Box.createRigidArea(new Dimension(
            				SPACE_TO_BUTTONS - DesignConstants.metricsSmall.charWidth('m') * NUM_COLUMNS, 0)));
            	}
            	
            	/*
            	 * getText
            	 * This method returns the text in the rowField
            	 * @return rowField.getText(), the text of the rowField
            	 */
            	@Override
            	public String getText() {	
        			return rowField.getText();
            	}
            	
            	/*
            	 * setText
            	 * This method sets the text of the rowField to the parameter
            	 * @param newText, the String that is going to be copied into the rowField
            	 */
				@Override
				public void setText(String newText) {
					rowField.setText(newText);
				}
				
				/*
				 * addFieldListener
				 * This method adds a mouse listener to the rowField that removes any text from
				 * the rowField when it is clicked, unless it contains the text equal to defaultLabel
				 * @param defaultLabel, the String that will be used to determine whether or not 
				 * to set the text of the rowField to an empty String
				 */
				public void addFieldListener(String defaultLabel) {
                    rowField.addMouseListener(new MouseAdapter() {
                    	@Override
                    	public void mouseClicked(MouseEvent e) {
                    		if (rowField.getText().equals(defaultLabel)) {
                    			rowField.setText("");
                    		}
                    	}
                    });
				}
            }
        	
        	/*
        	 * FixedRow
        	 * This abstract class is a RowOption that adds a JLabel rowLabel 
        	 */
        	abstract private class FixedRow extends RowOption implements ActionListener {
            	private Component rigid;
            	private JLabel rowLabel;

            	FixedRow(String rowText) {
            		super();
            		
            		this.rowLabel = new JLabel(rowText);
            		this.add(this.rowLabel);

            		rigid = Box.createRigidArea(new Dimension
            				(SPACE_TO_BUTTONS - DesignConstants.metricsSmall.stringWidth(this.rowLabel.getText()), 0));
            		this.add(rigid);
            	}
            	
            	/*
            	 * getText
            	 * This method returns the text in the rowLabel
            	 * @return rowLabel.getText(), the text of the rowLabel
            	 */
            	@Override
            	public String getText() {	
        			return rowLabel.getText();
            	}
            	
            	/*
            	 * setText
            	 * This method sets the text of the rowLabel to the parameter
            	 * @param newText, the String that is going to be copied into the rowLabel
            	 */
				@Override
				public void setText(String newText) {
					rowLabel.setText(newText);
					
					this.remove(rigid);
					rigid = Box.createRigidArea(new Dimension
            				(SPACE_TO_BUTTONS - DesignConstants.metricsSmall.stringWidth(this.rowLabel.getText()), 0));
					this.add(rigid);
					this.setComponentZOrder(rigid, 1);
				}
            }
        	
        	public class PartnerPanel extends DynamicBoxLayoutPanel implements ActionListener {
                private int editingIndex;
                private final String FIELD_INSTRUCTION = "New Student's Name";
                
                PartnerPanel() {
                    if (partners.size() == 0) {
                    	RowPair partnerPair = new RowPair(FIELD_INSTRUCTION, false);
                    	this.add(partnerPair);
                    } else {
    	                for (Student s : partners) {
    	                	RowPair partnerPair = new RowPair(s.getName(), s.getId(), false);
    	                	this.add(partnerPair);
    	                }
                    }

                    for (Student s : blackList) {
                        RowPair partnerPair = new RowPair(s.getName(), s.getId(), true);
                        this.add(partnerPair);
                    }
                    
                    editingIndex = 1;
                    
                    this.add(areaForAddButton);
                    this.add(addButton);
                }
                
                public void defineAddButton() {
                	addButton = new JButton("Add Partner");
                }
                
                public void defineInfoLabel() {
                	infoLabel = new JLabel("List of students you would like to sit with: ");
                }
                
                public int getIndex(RowOption row) {
                	Component[] componentList = this.getComponents();

                	for (int i = 0; i < componentList.length; i++) {
                		if ((componentList[i] instanceof RowPair) && (((RowPair) componentList[i]).hasRow(row))) {
                			return i;
                		}
                	}
                	
                	return -1;
                }
                
                public boolean showFixed(int index) {
                	Component[] componentList = this.getComponents();
                	
                	if (componentList[index] instanceof RowPair) {
                		if ((index == componentList.length - 3) && 
                				(((RowPair) componentList[index]).getFixedText().equals(FIELD_INSTRUCTION))) {
                			this.remove(componentList[index]);
                		} else {
                			((RowPair) componentList[index]).toFixedVisibility(((RowPair) componentList[index]).getStudentID());
                		}
                		return true;
                	} else {
                		return false;
                	}
                }
            	
                protected void addPairToBottom(String text, String id) {
                	Component[] componentList = this.getComponents();
                	
                	if (id == null) {
	                	if (editingIndex != -1) {
	        				showFixed(editingIndex);
	        			} 
	                	editingIndex = componentList.length - 2;
                	} else {
                		editingIndex = -1; // not editing anything
                	}
        			
        			RowPair pairToAdd;
        			if (id == null) {
        				pairToAdd = new RowPair(text, false);
        			} else {
        				pairToAdd = new RowPair(text, id, false);
        			}
                	addAdditiveComponent(pairToAdd);
                	
                	this.revalidate();
                	this.repaint();
                }
                 
                @Override
                public void removeAdditiveComponent(JPanel pairToRemove) {
                	if (pairToRemove instanceof RowPair) {
	                	super.removeAdditiveComponent(pairToRemove);
	                	
	                    Student s = findStudentByID(((RowPair) pairToRemove).getStudentID());
	
	                    if (((RowPair) pairToRemove).isBlackList()){
	                        blackList.remove(s);
	                    } else {
	                        partners.remove(s);
	                    }
	
	                	writeStudents();
                	}
                }
                
                public void actionPerformed(ActionEvent e) {
                	Object source = e.getSource();
                	if (source == addButton) {
	            		addPairToBottom(FIELD_INSTRUCTION, null);
                	}
                }
                
                /*
                 * RowPair
                 * This class is made up of a PartnerFixedRow and a PartnerEditingRow that alternate visibility
                 * based on the okButton and editButton. 
                 */
                private class RowPair extends JPanel {
                	private PartnerFixedRow fixed;
                	private EditingRow editing;
                	private String savedStudentID;
                    private boolean isBlackList;
                    
                    private final Color BLACKLIST_COLOUR = new Color(177, 176, 178);
                    private final Color PREFERRED_COLOUR = new Color(255, 252, 231);
                	
                    /*
                     * Constructor
                     * This constructor creates a RowPair set to EditingVisibility
                     * @param text, the String that is being added to the two rows
                     * @param isBlacklist, whether or not the student represented by the row is blacklisted
                     */
                	RowPair(String text, boolean isBlackList) {
                		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                		
                	    this.isBlackList = isBlackList;
                	    recolour();

                		fixed = new PartnerFixedRow(text);
                		editing = new PartnerEditingRow(text);
                		
                		this.add(fixed);
                		this.add(editing);
                		
                		toEditingVisibility();
                		toUniversalFont(this);
                	}
                	
                    /*
                     * Constructor
                     * This constructor creates a RowPair set to FixedVisibility
                     * @param text, the String that is being added to the two rows
                     * @param id, the String that becomes associated with the RowPair
                     * @param isBlacklist, whether or not the student represented by the row is blacklisted
                     */
                	RowPair(String text, String id, boolean isBlackList) {
                		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                		
                	    this.isBlackList = isBlackList;
                	    recolour();

                		fixed = new PartnerFixedRow(text);
                		editing = new PartnerEditingRow(text);
                		
                		this.add(fixed);
                		this.add(editing);

                		toFixedVisibility(id);
                        toUniversalFont(this);
                	}
                	
                	/*
                	 * getFixedText
                	 * This method returns the text of the PartnerFixedRow's JLabel as defined by its own
                	 * getText() method
                	 * @return fixed.getText(), the text that is returned
                	 */
                	public String getFixedText() {
                		return fixed.getText();
                	}
                	
                	/*
                	 * getStudentID
                	 * This method returns the savedStudentID String associated with the RowPair
                	 * @return savedStudentID, the String to be returned
                	 */
                	public String getStudentID() {
                		return savedStudentID;
                	}
                	
                	/*
                     * setBlackList
                     * This method sets the isBlackList boolean to the parameter that is passed in updates the
                     * background colour via recolour()
                     * @param isBlackList, the boolean that will be copied to the isBlackList instance variable
                     */
                    public void setBlackList(boolean isBlackList) {
                        this.isBlackList = isBlackList;
                        recolour();
                    }
                    
                    /*
                     * isBlackList
                     * This methods determines whether or not the student represented by the RowPair is blacklisted
                     * @return isBlacklist, true if the student is blacklisted, false otherwise
                     */
                    public boolean isBlackList(){
                        return this.isBlackList;
                    }
                	
                	/*
                	 * hasRow
                	 * This method determines if the row that it passed in as a parameter is contained in the RowPair
                	 * @param row, the RowOption that is being tested
                	 */
                	public boolean hasRow(RowOption row) {
                		if ((row.equals(editing)) || (row.equals(fixed))) {
                			return true;
                		} else {
                			return false;
                		}
                	}
                	
                	/*
                	 * copyToEditing
                	 * This method copies the text from the editing panel to the fixed panel
                	 */
                	private void copyToEditing() {
                		editing.setText(fixed.getText());
                	}

                	/*
                	 * toFixedVisibility
                	 * This method hides the editing JPanel and shows the fixed JPanel
                	 * @param id, a String that specifies the id of the student that is saved to partners
                	 */
                	private void toFixedVisibility(String id) {
                		editingIndex = -1; // nothing is being edited
                		savedStudentID = id;	
                	
                		int indexOfSpace = editing.getText().indexOf("  -  ID:  ");
                		if (indexOfSpace == -1) { // prevents several additions of ID number
                			fixed.setText(editing.getText() + "  -  ID:  " + id);
                		}
                		
                		fixed.setVisible(true);
                		editing.setVisible(false);
                	}
                	
                	/*
                	 * toEditingVisibility
                	 * This method hides the fixed JPanel and shows the editing JPanel
                	 */
                	private void toEditingVisibility() {
                		fixed.setVisible(false);
                		editing.setVisible(true);
                	}
                	
                	/*
                	 * removeThis
                	 * This method removes the current instance of the RowPair from the DynamicBoxLayoutPanel
                	 * it is contained in. Called by PartnerFixedRow and PartnerEditingRow classes.
                	 */
                	private void removeThis() {
                		removeAdditiveComponent(this);
                	}

                	/*
                	 * recolour
                	 * This method sets the background of the RowPair based on whether or not the student
                	 * represented by the row is blacklisted 
                	 */
                    private void recolour() {
                        if (isBlackList) {
                            this.setBackground(BLACKLIST_COLOUR);
                        } else {
                            this.setBackground(PREFERRED_COLOUR);
                        }
                    }

                    /*
                     * findStudentsWithName
                     * Finds all students with the inputted name. If there is no exact match, returns
                     * the Students with a similar name, if there are any.
                     * @param name, the Student's name
                     * @return an ArrayList of Students with that name or similar. If none exist, returns null.
                     */
                    private ArrayList<Student> findStudentsWithName(String name) {
                        ArrayList<Student> studentsWithNameExact = new ArrayList<Student>();
                        ArrayList<Student> studentWithNameClose = new ArrayList<>();

                        for (Student student : students) {
                            if (student.getName().equals(name)) {
                                studentsWithNameExact.add(student);
                            } else if (student.getName().contains(name)) {
                                studentWithNameClose.add(student);
                            }
                        }

                        if (studentsWithNameExact.size() == 0) {
                            if (studentWithNameClose.size() == 0) {
                                return null;
                            } else {
                                return studentWithNameClose;
                            }
                        } else {
                            return studentsWithNameExact;
                        }
                    }
                    
                    //---------------------------------------------------------------------------------------------------------
                    // beginning of RowPair inner classes
                    //---------------------------------------------------------------------------------------------------------
                    /*
                     * PartnerFixedRow
                     * This class defines and adds an edit button and toggle button. An action listener is added to all
                     * of the existing buttons, including the removeButton from the OptionRow superclass
                     */
                    private class PartnerFixedRow extends FixedRow implements ActionListener {
                    	private JButton editButton;
                    	private JButton whiteBlackListToggle;

                    	PartnerFixedRow(String nameLabel) {
                    		super(nameLabel);
                    		this.setOpaque(false);

                    		editButton = new JButton("%");
                    		editButton.addActionListener(this);

                    		if (isBlackList()){
                                whiteBlackListToggle = new JButton("Make Preferred");
                            } else {
                                whiteBlackListToggle = new JButton("Make Blacklisted");
                            }
                            whiteBlackListToggle.addActionListener(this);

                    		this.add(editButton);
                    		this.add(removeButton);
                    		this.add(whiteBlackListToggle);
                    		this.add(Box.createHorizontalGlue());
                    	}
                    	
                    	/*
                    	 * actionPerformed
                    	 * This method defines what happens when the editButton, removeButton, or whiteBlackListToggle 
                    	 * are pressed. 
                    	 * @param e, an ActionEvent object
                    	 */
                    	public void actionPerformed(ActionEvent e) {
                    		Object source = e.getSource();
                    		if (source == editButton) {
                    			// switch this row to Editing, and the previous editing row to Fixed
                    			copyToEditing();
                    			toEditingVisibility();
                    			
                    			if (editingIndex != -1) {
                    				showFixed(editingIndex);
                    			} 
                    			editingIndex = getIndex(this);
                    		} else if (source == removeButton) {
                    			removeThis();
                    		} else if (source == whiteBlackListToggle) {
                    		    setBlackList(!isBlackList());
                    		    //recolour();
                                
                    		    Student current = findStudentByID(savedStudentID);
                    		    if (isBlackList()) {
                                    partners.remove(current);
                                    blackList.add(current);
                                    whiteBlackListToggle.setText("Make Preferred");
                                } else {
                                    blackList.remove(current);
                                    partners.add(current);
                                    whiteBlackListToggle.setText("Make Blacklisted");
                                }
                    		    
                    		    writeStudents();
                            }
                    	}
                    }
                    
                    /*
                     * PartnerEditingRow
                     * This class is an EditingRow that is used by the user for adding partners
                     */
                    private class PartnerEditingRow extends EditingRow implements ActionListener {
                    	private JButton okButton;

                    	PartnerEditingRow(String nameLabel) {
                    		super(nameLabel);
                    		
                    		addFieldListener(FIELD_INSTRUCTION);
                    		okButton = new JButton("OK");
                    		okButton.addActionListener(this);
                    		
                    		this.add(okButton);
                    		this.add(removeButton);
                    		this.add(errorLabel);
                    		this.add(Box.createHorizontalGlue());
                    	}
                    	
                    	/*
                    	 * actionPerformed
                    	 * This class defines what happens when the okButton or removeButton is pressed.
                    	 * The pressing of the okButton involves checking the students in the system
                    	 * that have the current rowField input as their name
                    	 * @param e, an ActionEvent object
                    	 */
                    	public void actionPerformed(ActionEvent e) {                		
                    		Object source = e.getSource();
                    		if (source == okButton) {
                                ArrayList<Student> foundStudents = findStudentsWithName(rowField.getText());
                                ArrayList<Student> foundNotAdded = new ArrayList<>();
                                if (foundStudents != null) {
	                                for (Student student : foundStudents) {
	                                    if (!(partners.contains(student)) || (blackList.contains(student))) {
	                                        foundNotAdded.add(student);
	                                    }
	                                }
                                } 
                                
                                if (foundStudents == null) {
                                    errorLabel.setText("<html> Partner not Found. "
                                            + "<br> Ask them to register before you can add them. </html>");
                                } else if ((foundStudents.size() == 1) || (foundNotAdded.size() == 1)) {
                                	Student tempStudent;
                                	if (foundStudents.size() == 1) {
                                		tempStudent = foundStudents.get(0);
                                	} else {
                                		tempStudent = foundNotAdded.get(0);
                                	}
                                	
                                    if (tempStudent == TicketPanel.this.selectedStudent) {
                                        errorLabel.setText("You can't add yourself");
                                    } else if ((partners.contains(tempStudent)) || (blackList.contains(tempStudent))) {
                                        toFixedVisibility(tempStudent.getId());
                                        errorLabel.setText("Person already added");
                                    } else { 
                                    	// add tempStudent
                                        if (isBlackList) {
                                            blackList.remove(new Student(getFixedText(), getStudentID()));
                                        } else {
                                            partners.remove(new Student(getFixedText(), getStudentID()));
                                        }

                                        toFixedVisibility(tempStudent.getId());
                                        partners.add(tempStudent);
                                        setBlackList(false);
                                    }
                                } else if (foundNotAdded.size() == 0) {
                                    errorLabel.setText("All people with that name have been added");
                                } else {
                                	errorLabel.setText("Warning! More than one student with that name");
                                	resolvePanel.addResolvingRow(foundNotAdded); // send to resolvePanel to be resolved
                                	removeThis();
                                }
                    		} else if (source == removeButton) {
                    			removeThis();
                    			editingIndex = -1;
                    		}

                    		writeStudents();

                        	this.revalidate();
                        	this.repaint();
                    	}
                    }    
                    //----------------------------------------------------------------------------------------------
                    // end of RowPair inner classes
                    //----------------------------------------------------------------------------------------------
                } // end of RowPair
        	} // end of PartnerPanel 
        	
        	/*
        	 * ResolveNamesPanel
        	 * This panel is a JPanel that is made up of ResolveRows arranged vertically in BoxLayout.
        	 * The number of ResolveRows will vary based on the number of users that have the 
        	 * same name but have different studentIDs 
        	 */
            public class ResolveNamesPanel extends JPanel {
            	private JLabel infoLabel;
            	private static final String EMPTY_PANEL_TEXT = "Your partner list does not currently "
            			+ "require any additional information to be provided";
            	private static final String IN_USE_TEXT = "Please select which student you refer to";

            	ResolveNamesPanel() {
            		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            		
            		infoLabel = new JLabel(EMPTY_PANEL_TEXT);
            		infoLabel.setBorder(DesignConstants.INFO_LABEL_BORDER);
            		this.add(infoLabel);
            	}
            	
            	/*
            	 * addResolvingRow
            	 * This method creates a resolving row populated with the ArrayList of Students passed in as 
            	 * a parameter and adds it to the ResolveNamesPanel
            	 * @param foundStudents, the ArrayList of Students
            	 */
            	public void addResolvingRow(ArrayList<Student> foundStudents) {
            		if (infoLabel.getText().equals(EMPTY_PANEL_TEXT)) {
            			infoLabel.setText(IN_USE_TEXT);
            		}
                    ResolveRow resolveRow = new ResolveRow(foundStudents);

            		this.add(resolveRow);

            		this.revalidate();
            		this.repaint();
            	}
            	
            	/*
            	 * removeResolvingRow
            	 * This method removes the ResolveRow passed in as a parameter from the ResolveNamesJPanel
            	 * and refreshes the screen
            	 */
            	public void removeResolvingRow(ResolveRow row) {
            	    this.remove(row);

            	    this.revalidate();
            	    this.repaint();
                }
            	
            	/*
            	 * ResolveRow
            	 * This class is a JPanel that defines a row that allows a user to select between
            	 * Students. It displays the image, the id, and the name of each possible student
            	 */
            	private class ResolveRow extends JPanel implements ActionListener {
            	    ArrayList<String> studentIds;
                    ArrayList<JButton> studentSelectors;

                    ResolveRow (ArrayList<Student> foundStudents) {
                        studentIds = new ArrayList<String>();
                        studentSelectors = new ArrayList<JButton>();

                        this.setLayout(new GridBagLayout());

                        Insets space = new Insets(3, 3, 3, 3);
                        GridBagConstraints c = new GridBagConstraints();
                        c.insets = space;
                        for (int i = 0; i < foundStudents.size(); i++) {
                            Student student = foundStudents.get(i);
                            BufferedImage studentPicture = student.getPicture();
                            
                            c.gridy = 0;
                            c.gridx = i;
                            if (studentPicture != null){
                                this.add(new JLabel(
                                		new ImageIcon(studentPicture.getScaledInstance(50, 50, Image.SCALE_SMOOTH))), c);
                            } else {
                                this.add(new JLabel("Picture Not Found"),c);
                            }

                            c.gridy = 1;
                            this.add(new JLabel(student.getId()),c);
                            c = new GridBagConstraints();

                            c.gridy = 2;
                            this.add(new JLabel(student.getName()),c);

                            c.gridy = 3;
                            JButton select = new JButton("Select");
                            select.addActionListener(this);
                            this.add(select, c);
                            
                            studentSelectors.add(select);
                            studentIds.add(student.getId());
                        }
                    }
                    
                    /*
                     * actionPerformed
                     * This method adds a partner based on the student that is selected through studentSelectors
                     * if they are not already a partner or are not already blacklisted 
                     * @param e, an ActionEvent object
                     */
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object source = e.getSource();
                        int index = studentSelectors.indexOf(source);
                        String id = studentIds.get(index);
                        Student partner = findStudentByID(id);
                        if ((!partners.contains(partner)) && (!blackList.contains(partner))) {
                            partnerPanel.addPairToBottom(partner.getName(), id);
                            partners.add(partner);
                            writeStudents();
                        }
                        removeResolvingRow(this);
                    }
                } // end of ResolveRow
            } // end of ResolveNamesPanel

            /*
             * PaymentPanel
             * This class is a JPanel that replicates a payment system similar to School Cash online.
             * The current version does not save any of the payment information or do any verification
             * of the information entered, but simply updates the selectedStudent's state via the setPaid method
             */
            private class PaymentPanel extends JPanel implements ActionListener {
            	private JLabel cardLabel = new JLabel("Card Number");
            	private JTextField cardField = new JTextField();

                private JLabel codeLabel = new JLabel("Security Code");
                private JTextField codeField = new JTextField();

                private JLabel expiryLabel = new JLabel("Expiry Date");
                private JComboBox<String> monthBox;
                private JComboBox<String> yearBox;

                private JButton buyButton = new JButton("Buy now!");
                private JButton refundButton = new JButton("Click here for a refund");

                private int currentYPos = 0; // keeps track of y position for layout purposes

            	PaymentPanel() {
            		this.setLayout(new GridBagLayout());

            		// add credit card number row
                    cardField.setColumns(20);
            		addRow(cardLabel, cardField);

            		// add credit card code row
            		codeField.setColumns(10);
            		addRow(codeLabel, codeField);

            		// add credit card expiry row
            		addLabel(expiryLabel);

            		String[] monthOptions = new String[13];
            		monthOptions[0] = "mm ";
            		for (int i = 1; i < 13; i++) {
            			monthOptions[i] = Integer.toString(i);
            			if (monthOptions[i].length() == 1) {
            				monthOptions[i] = "0" + monthOptions[i];
            			}
            		}
            		monthBox = new JComboBox<String>(monthOptions);

            		GridBagConstraints c = new GridBagConstraints();
            		c.gridx = 1;
            		c.gridy = currentYPos;
            		this.add(monthBox, c);

            		c.gridx = 2;
            		c.insets = new Insets(0, 5, 0, 5);
            		this.add(new JLabel("/"), c);

            		String[] yearOptions = new String[17];
            		yearOptions[0] = "yy";
            		int curYear = Calendar.getInstance().get(Calendar.YEAR);
            		for (int i = 0; i < 16; i++) {
            			yearOptions[i + 1] = Integer.toString(curYear + i);
            			yearOptions[i + 1] = yearOptions[i + 1].substring(yearOptions[i + 1].length() - 2);
            		}
            		yearBox = new JComboBox<String>(yearOptions);

            		c = new GridBagConstraints();
            		c.gridx = 3;
            		c.gridy = currentYPos;
            		c.anchor = GridBagConstraints.LINE_START;
            		this.add(yearBox, c);
            		
            		// add buy and refund buttons to end
            		c = new GridBagConstraints();
            		c.anchor = GridBagConstraints.LINE_START;
            		c.gridy = currentYPos + 1;
                    this.add(buyButton, c);

                    c.gridy = currentYPos + 2;
                    this.add(refundButton, c);

            		buyButton.addActionListener(this);
            		refundButton.addActionListener(this);

                    // add an order information panel to upper right side
                    OrderInfoPanel orderPanel = new OrderInfoPanel();
            		c = new GridBagConstraints();
            		c.gridheight = 3;
            		c.gridx = 4;
            		c.anchor = GridBagConstraints.PAGE_START;
            		c.weightx = 1.0;
            		this.add(orderPanel, c);
                    
            		setVisibility();
            	}

            	/*
            	 * addRow
            	 * This method accepts a label and field as parameters and adds them to the panel in a horizontal row with
            	 * vertical and horizontal spacing.
            	 * @param the JLabel to be added
            	 * @param the JTextField to be added
            	 */
            	private void addRow(JLabel label, JTextField field) {
                    addLabel(label);

                    GridBagConstraints c = new GridBagConstraints();
                    c.anchor = GridBagConstraints.WEST;
            		c.gridwidth = 3;
                    c.gridx = 1;
                    c.insets = new Insets(0, 0, 20, 0);
                    this.add(field, c);

                    currentYPos++; // keep track of gridy
            	}

            	/*
            	 * getLabelConstraints
            	 * This method adds a label to the panel with northwest positioning and insets below and to the right
            	 * @param label, the JLabel to be added
            	 */
            	public void addLabel(JLabel label) {
            		GridBagConstraints c = new GridBagConstraints();
            		c.anchor = GridBagConstraints.FIRST_LINE_START;
            		c.gridy = currentYPos;
            		c.insets = new Insets(0, 0, 20, 100);

            		this.add(label, c);
            	}

            	/*
            	 * setVisibility
            	 * This method sets the visibility of components based on whether or not the user has paid.
            	 * If a student has paid, they will be able to ask for a refund, otherwise, all components will be
            	 * shown except for the refund button
            	 * @param a boolean that represents whether or not the student has b
            	 */
            	public void setVisibility() {
            		Component[] componentList = this.getComponents();

            		for (Component component : componentList) {
            			if (!component.equals(refundButton)) {
            				component.setVisible(!selectedStudent.hasPaid());
            			} else {
            				component.setVisible(selectedStudent.hasPaid());
            			}
            		}
            	}

            	/*
            	 * actionPerformed
            	 * This method is used for the buy and refund buttons, updating the visibility, the info label at the top
            	 * of the application, and the loginCredentials file when one of these buttons are pressed
            	 * @param e, the ActionEvent object
            	 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    if (source == buyButton){
                        selectedStudent.setPaid(true);
                    } else if (source == refundButton) {
                        selectedStudent.setPaid(false);
                    }
                    setVisibility();

                    writeStudents();
                    updateInfoLabel();
                }

                /*
                 * OrderInfoPanel
                 * This class is a JPanel that displays information on the type of event and cost of ticket
                 */
                private class OrderInfoPanel extends JPanel {
            		private JLabel titleLabel = new JLabel("Your Order");
            		private JLabel nameLabel, infoLabel, costLabel;

            		OrderInfoPanel() {
            			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            			this.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1, true),
            					new EmptyBorder(10, 10, 10, 10)));
            			this.setOpaque(false);

            			titleLabel.setFont(DesignConstants.SMALL_BOLD_FONT);
            			this.add(titleLabel);
            			this.add(new JSeparator(SwingConstants.HORIZONTAL));

            			nameLabel = new JLabel(selectedStudent.getName());
            			nameLabel.setFont(DesignConstants.GENERAL_FONT);
            			this.add(nameLabel);

            			infoLabel = new JLabel("    " + Prom.TYPE_OF_EVENT + " - Ticket (Qty: 1)");
            			infoLabel.setFont(DesignConstants.GENERAL_FONT);
            			this.add(infoLabel);
            			this.add(new JSeparator(SwingConstants.HORIZONTAL));

            			costLabel = new JLabel("Total             " + Prom.COST_OF_TICKET);
            			costLabel.setFont(DesignConstants.SMALL_BOLD_ITALICS_FONT);
            			costLabel.setForeground(new Color(106, 116, 125));
            			this.add(costLabel);
            		}
            	}
            } // end of PaymentPanel

            private class ProfilePanel extends JPanel implements ActionListener{
                private BufferedImage studentImage;
                private JLabel imageComponent;
                private JFileChooser fileChooser;
                private JButton selectImage;
                private Path filePath;

                private static final int IMAGE_SIZE = 200;

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

                                int width = tempBufferedImage.getWidth();
                                int height = tempBufferedImage.getHeight();
                                int side = Math.min(width, height);

                                tempBufferedImage = tempBufferedImage.getSubimage((width - side) / 2,
                                		(height - side) / 2, side, side);
                                Image tempImage = tempBufferedImage.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);

                                studentImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D resize = (Graphics2D) studentImage.getGraphics();
                                resize.drawImage(tempImage, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
                                resize.dispose();
                                
                                // remove old image
                                this.remove(imageComponent);
                                
                                // add new image
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
            
            /*
             * AccommodationPanel
             * This class is a DynamicBoxLayoutPanel that consists of AccommodationRows as well as add and save buttons
             * for these rows. This class deals with saving accommodations for the user, allowing them to remove, edit,
             * and add new accommodations as they please
             */
            private class AccommodationPanel extends DynamicBoxLayoutPanel {
            	private final String FIELD_INSTRUCTION = "New Accommodation";
            	private JButton saveButton;
            	private Component areaForSaveButton;
            	
            	AccommodationPanel() {
            		for (int i = 0; i < selectedStudent.getAccommodations().size(); i++) {
            			this.add(new AccommodationRow(selectedStudent.getAccommodations().get(i)));
            		}
            		
            		this.add(areaForAddButton);
            		this.add(addButton);
            		
            		saveButton = new JButton("Save");
            		saveButton.addActionListener(this);
            		
            		areaForSaveButton = Box.createRigidArea(new Dimension(0, 10));
            		this.add(areaForSaveButton);
            		this.add(saveButton);
            	}
            	
            	/*
            	 * defineAddButton
            	 * This method defines the addButton of the superclass, setting its text to "Add Accommodation"
            	 */
            	@Override
            	protected void defineAddButton() {
            		addButton = new JButton("Add Accommodation");
            	}
            	
            	/*
            	 * defineInfoLabel
            	 * This method defines the infoLabel of the superclass
            	 */
            	@Override
            	protected void defineInfoLabel() {
            		infoLabel = new JLabel("Any accommodations you would like the organisers to keep in mind: ");
            	}
            	
            	/*
            	 * removeAdditiveComponent
            	 * This method removes the row and then removes the user's accommodation that that row contained
            	 * @param the JPanel that is to be removed
            	 */
            	@Override 
            	public void removeAdditiveComponent(JPanel row) {
            		super.removeAdditiveComponent(row);
            		
            		Component[] components = this.getComponents();
            		for (int i = 0; i < components.length; i++) {
            			if (components[i] instanceof AccommodationRow) {
        					selectedStudent.getAccommodations().remove(((AccommodationRow) components[i]).savedAccommodation);
        				}
        			}

            		writeStudents();
            	}
            	
            	/*
            	 * actionPerformed
            	 * This method allows new rows to be added by pressing the addButton and the accommodations to be
            	 * saved in selectedStudent
            	 * @param e, an ActionEvent object
            	 */
            	public void actionPerformed(ActionEvent e) {
            		Object source = e.getSource();
            		if (source == addButton) {
            			super.addAdditiveComponent(new AccommodationRow(FIELD_INSTRUCTION));
            			
            			// ensure the saveButton is at the bottom
            			this.remove(areaForSaveButton);
            			this.remove(saveButton);
            			
            			this.add(areaForSaveButton);
            			this.add(saveButton);
            		} else if (source == saveButton) {
            			Component[] components = this.getComponents();
            			
            			// re-write all accommodations since multiple rows could have been edited at once
            			selectedStudent.setAccommodations(new ArrayList<String>());
            			for (int i = 0; i < components.length; i++) {
            				if (components[i] instanceof AccommodationRow) {
            					((AccommodationRow) components[i]).savedAccommodation = ((EditingRow) components[i]).getText();
            					selectedStudent.getAccommodations().add(((AccommodationRow) components[i]).savedAccommodation);
            				}
            			}
            		}

            		writeStudents();
            	}
            	
            	/*
            	 * AccommodationRow
            	 * This class is an Editing Row that adds a field listener and a remove button.
            	 */
            	public class AccommodationRow extends EditingRow implements ActionListener {
            		private String savedAccommodation;
            		
            		AccommodationRow(String labelText) {
            			super(labelText);
            			this.setOpaque(false);
            			
            			addFieldListener(FIELD_INSTRUCTION);
            			
            			this.add(removeButton);
            			this.add(Box.createHorizontalGlue());
            			
            			toUniversalFont(this); // necessary when the user adds a row
            		}
            		
            		/*
            		 * actionPerformed
            		 * This method calls the removeAdditiveComponent method, effectively removing the AccommodationRow
            		 * @param e, the ActionEvent object
            		 */
            		@Override
            		public void actionPerformed(ActionEvent e) {
            			Object source = e.getSource();
            			if (source == removeButton) {
            				removeAdditiveComponent(this);
            			}
            		}
            	}
            }
            //-----------------------------------------------------------------------------------------------------------
            // end of CenterPanel inner classes
            //-----------------------------------------------------------------------------------------------------------
        } // end of CenterPanel
        //---------------------------------------------------------------------------------------------------------------
        // end of TicketPanel inner classes
        //---------------------------------------------------------------------------------------------------------------
    } // end of TicketPanel

    /**
     * The floor plan panel. Displays the floor plan.
     */
    private class FloorPlanPanel extends JPanel implements ActionListener{
        private JPanel fromPanel;
        private JButton exitButton;

        /**
         * The constructor for the FloorPlanPanel. Adds an exit button and a FloorPlanSystem.
         * @param fromPanel the panel that called the floor plan.
         * @see FloorPlanSystem
         */
        FloorPlanPanel(JPanel fromPanel) {
            HashMap<Student, HashMap<Student, Double>> paidHash = new HashMap<Student, HashMap<Student, Double>>();
            ArrayList<Student> paidStudents = new ArrayList<Student>();

            for (Student s : students){
                if (s.hasPaid() && paidHash.size() < Prom.maxTables*Prom.tableSize){
                    HashMap<Student, Double> weights = new HashMap<Student, Double>();
                    for (Student partner : s.getPartners()){
                        weights.put(partner, 1.0);
                    }
                    for (Student blackList : s.getBlacklist()){
                        weights.put(blackList, -100.0);
                    }
                    paidHash.put(s, weights);
                    paidStudents.add(s);
                }
            }
            tables = SeatingAssignmentSystem.assignTables(paidStudents, Prom.maxTables, Prom.tableSize, paidHash);
            for (Table t: tables){
                System.out.print("[");
                for (Student s : t.getStudents()){
                    System.out.print(s.getId() + ",");
                }
                System.out.println("]");
            }
            floorPlan = new FloorPlanSystem(tables);
            this.fromPanel = fromPanel;
            this.exitButton = new JButton("Hide FloorPlan");
            this.setLayout(new BorderLayout());
            this.add(floorPlan, BorderLayout.CENTER);
            this.add(exitButton, BorderLayout.PAGE_START);
            this.exitButton.addActionListener(this);
        }

        /**
         * Monitors for the exit button being pressed.
         * @param e the button pressed.
         */
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
    
    public final static class DesignConstants {
		public static final Color BACK_COLOUR = new Color((float) (130 / 255.0), (float) (235 / 255.0), 
    			(float) (33 / 255.0), (float) 0.3);
        public static final Color MAIN_COLOUR = new Color(75, 112, 68);
        
        public static final Font LARGE_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 25);
        public static final Font LARGE_FONT = new Font("Segoe UI", Font.PLAIN, 20);
        public static final Font MEDIUM_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 17);
        public static final Font GENERAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
        public static final Font SMALL_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
        public static final Font SMALL_BOLD_ITALICS_FONT = new Font("Segoe UI", Font.BOLD + Font.ITALIC, 13);
        public static final Canvas C = new Canvas();
        public static final FontMetrics metricsSmall = C.getFontMetrics(GENERAL_FONT);
        
        public static final EmptyBorder INFO_LABEL_BORDER = new EmptyBorder(0, 0, 10, 0);
        
        DesignConstants() {
        	throw new AssertionError();
        }
    }
}