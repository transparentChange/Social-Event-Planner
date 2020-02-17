import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        this.setLayout(new GridLayout(1, 1));
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

        LoginPanel() {
            this.setFocusable(false);
            this.setOpaque(true);

            this.setLayout(new GridBagLayout());

            try {
                image = ImageIO.read(new File("src/loginBackground.jpg"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //this.add(new InnerFrame());
            this.setVisible(true);

            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(50, 50, 50, 50);

            this.add(new InnerFrame(), c);

            this.setVisible(true);

        }

        public void paintComponent(Graphics g) {
            if (this.image != null) {
                System.out.println("Hi");
                g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                g.setColor(new Color(0, 0, 0));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
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

            BufferedImage image = null;

            InnerFrame() {
                this.setLayout(new FlowLayout());
                this.setOpaque(false);

                fieldFont = new Font("Open Sans", Font.PLAIN, 20);

                loginButton = new LoginButton(0, 0);
                loginButton.addActionListener(this);

                this.add(loginButton);

                //studentButton = new StudentLoginButton(0, 0);
                createAccountButton = new JButton("Don't have an account? Click here to sign up.");
                createAccountButton.addActionListener(this);

                this.add(createAccountButton);

                this.add(new JLabel("Student ID"));

                idField = new JTextField();
                this.add(idField);


                this.add(new JLabel("Password"));

                passwordField = new JTextField();
                this.add(passwordField);

                String[] grades = {"9", "10", "11", "12"};
                nameField = new JTextField(20);
                gradeOptions = new JComboBox(grades);
            }

            public void paintComponent(Graphics g) {
                if (this.image != null) {
                    System.out.println("Hi");
                    g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
                } else {
                    g.setColor(new Color(255, 0, 0));
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());
                }
            }

            public void actionPerformed(ActionEvent evt) {
                Object source = evt.getSource();
                String inputId = idField.getText();
                String inputPassword = passwordField.getText();
                int studentIndex = getStudentIndex(inputId, inputPassword);
                if ((source == loginButton) && (studentIndex != -1)
                        && (loginButton.isLoginButton())) {
                    ticketPanel = new TicketPanel(students.get(studentIndex));
                    showTicket();
                } else if ((source == loginButton) && (!loginButton.isLoginButton())) {
                    students.add(new Student(nameField.getText(), inputId,
                            gradeOptions.getSelectedItem().toString(), inputPassword));
                    writeLastStudent();
                    ticketPanel = new TicketPanel(students.get(students.size() - 1));
                    showTicket();
                } else if (source == createAccountButton) {
                    addComponent(4, new JLabel("Name"));
                    addComponent(5, nameField);

                    addComponent(6, new JLabel("Grade"));

                    GridBagConstraints c = new GridBagConstraints();
                    c.gridy = 7;
                    c.anchor = GridBagConstraints.LINE_START;
                    c.insets = new Insets(5, 0, 0, 0);
                    this.add(gradeOptions, c);
                    //addComponent(6, new JLabel("Grade"));
                    //addComponent(6, gradeOptions);
                    loginButton.switchButtonState();
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

    private class TicketPanel extends JPanel {
        private Student selectedStudent;

        TicketPanel(Student student) {
            if (student != null) {
                this.setLayout(new BorderLayout()); // change to GridBagLayout
                this.setVisible(true);
                this.selectedStudent = student;

                JButton logout = new JButton(student.getName() + " Logout");
                logout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showLogin();
                    }
                });

                JPanel j = new JPanel();
                j.setLayout(new FlowLayout(FlowLayout.RIGHT));
                j.add(logout);

                this.add(j, BorderLayout.PAGE_START);
                this.repaint();
            }
        }
    }

}
