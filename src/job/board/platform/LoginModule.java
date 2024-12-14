package job.board.platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginModule extends JFrame implements ActionListener {
    JLabel usernameLabel, passwordLabel, roleLabel;
    JTextField usernameField;
    JPasswordField passwordField;
    JComboBox<String> roleComboBox;
    JButton loginBtn, registerBtn;

    public LoginModule() {
        setLayout(null);
        setTitle("Job Board Login");
        setSize(450, 400);
        setLocation(400, 200);

        // Background color
        getContentPane().setBackground(new Color(244, 247, 255));

        // Heading
        JLabel heading = new JLabel("Login");
        heading.setBounds(150, 20, 300, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(0, 122, 204));
        add(heading);

        // Username Label and Field
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 100, 100, 30);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 100, 200, 30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(usernameField);

        // Password Label and Field
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 150, 100, 30);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 200, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(passwordField);

        // Role Label and ComboBox
        roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 200, 100, 30);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(roleLabel);

        String[] roles = {"Job Seeker", "Employer", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(150, 200, 200, 30);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        add(roleComboBox);

        // Login Button
        loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 250, 100, 40);
        loginBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        loginBtn.setBackground(new Color(0, 122, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(this);
        add(loginBtn);

        // Register Button
        registerBtn = new JButton("Register");
        registerBtn.setBounds(270, 250, 100, 40);
        registerBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        registerBtn.setBackground(new Color(0, 204, 122));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.addActionListener(this);
        add(registerBtn);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginBtn) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username or Password cannot be empty.");
                return;
            }

            try {
                // Database connection
                Conn conn = new Conn();
                if (conn.c == null) {
                    JOptionPane.showMessageDialog(this, "Database connection failed.");
                    return;
                }

                // SQL query to validate user
                String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
                PreparedStatement pst = conn.c.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password); // Ideally hashed password
                pst.setString(3, role);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful.");
                    int loggedInUserId = rs.getInt("user_id"); // Assuming user_id is in the users table
                    setVisible(false); // Hide the login frame
                    switch (role) {
                        case "Job Seeker":
                            new JobSeekerDashboard(loggedInUserId); // Pass logged-in user ID
                            break;
//                        case "Employer":
//                            new EmployerDashboard(loggedInUserId); // Add EmployerDashboard class if needed
//                            break;
//                        case "Admin":
//                            new AdminPanel(); // Add AdminPanel class if needed
//                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials or role.");
                }
            } catch (SQLException e) {
                // Log error and show generic error message
                e.printStackTrace(); // Replace with logging to a file
                JOptionPane.showMessageDialog(this, "An error occurred. Please try again later.");
            }
        } else if (ae.getSource() == registerBtn) {
            setVisible(false);
            // Uncomment this when RegistrationModule is implemented
            // new RegistrationModule();
        }
    }

    public static void main(String[] args) {
        new LoginModule();
    }
}
