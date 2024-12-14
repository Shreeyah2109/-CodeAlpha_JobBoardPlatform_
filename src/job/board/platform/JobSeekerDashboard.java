package job.board.platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class JobSeekerDashboard extends JFrame implements ActionListener {
    JPanel profilePanel;
    JButton saveProfileBtn, clearBtn, closeBtn;
    JTextField nameField, experienceField, emailField;
    JComboBox<String> salaryComboBox, cityComboBox;
    JCheckBox[] skillsCheckboxes;
    int loggedInUserId;

    public JobSeekerDashboard(int userId) {
        this.loggedInUserId = userId;
        setTitle("Job Seeker Profile Setup");
        setSize(800, 600);
        setLocation(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Layout
        setLayout(new BorderLayout());

        // Profile Setup Panel
        profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("Setup Profile"));
        add(profilePanel, BorderLayout.CENTER);

        setupProfilePanel(); // Add components to panel

        setVisible(true);
    }

    private void setupProfilePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Heading
        JLabel heading = new JLabel("Job Seeker Profile Setup");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        profilePanel.add(heading, gbc);
        gbc.gridwidth = 1;

        // Name and Email fields
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        profilePanel.add(nameLabel, gbc);

        nameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        profilePanel.add(nameField, gbc);

        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        profilePanel.add(emailLabel, gbc);

        emailField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        profilePanel.add(emailField, gbc);

        // Experience Field
        JLabel experienceLabel = new JLabel("Experience (years):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        profilePanel.add(experienceLabel, gbc);

        experienceField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        profilePanel.add(experienceField, gbc);

        // Salary Field
        JLabel salaryLabel = new JLabel("Expected Salary (LPA):");
        gbc.gridx = 0;
        gbc.gridy = 4;
        profilePanel.add(salaryLabel, gbc);

        salaryComboBox = new JComboBox<>(new String[]{"1 LPA", "2 LPA", "3 LPA", "4 LPA", "5 LPA", "6 LPA"});
        gbc.gridx = 1;
        gbc.gridy = 4;
        profilePanel.add(salaryComboBox, gbc);

        // City Field
        JLabel cityLabel = new JLabel("Preferred Location:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        profilePanel.add(cityLabel, gbc);

        cityComboBox = new JComboBox<>(new String[]{
            "Mumbai", "Delhi", "Bengaluru", "Chennai", "Hyderabad", "Kolkata", "Pune", 
            "Ahmedabad", "Jaipur", "Chandigarh", "Lucknow", "Indore"});
        gbc.gridx = 1;
        gbc.gridy = 5;
        profilePanel.add(cityComboBox, gbc);

        // Skills Checkboxes
        JLabel skillsLabel = new JLabel("Skills:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        profilePanel.add(skillsLabel, gbc);

        skillsCheckboxes = new JCheckBox[]{
            new JCheckBox("Java"),
            new JCheckBox("Python"),
            new JCheckBox("C++"),
            new JCheckBox("JavaScript"),
            new JCheckBox("SQL")
        };

        JPanel skillsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JCheckBox skill : skillsCheckboxes) {
            skillsPanel.add(skill);
        }
        gbc.gridx = 1;
        gbc.gridy = 6;
        profilePanel.add(skillsPanel, gbc);

        // Buttons
        saveProfileBtn = new JButton("Save Profile");
        saveProfileBtn.setBackground(new Color(0, 122, 204));
        saveProfileBtn.setForeground(Color.WHITE);
        saveProfileBtn.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 7;
        profilePanel.add(saveProfileBtn, gbc);

        clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(204, 204, 204));
        clearBtn.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 7;
        profilePanel.add(clearBtn, gbc);

        closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(255, 51, 51));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 8;
        profilePanel.add(closeBtn, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveProfileBtn) {
            saveProfile();
        } else if (e.getSource() == clearBtn) {
            nameField.setText("");
            emailField.setText("");
            experienceField.setText("");
            salaryComboBox.setSelectedIndex(0);
            cityComboBox.setSelectedIndex(0);
            for (JCheckBox skill : skillsCheckboxes) {
                skill.setSelected(false);
            }
        } else if (e.getSource() == closeBtn) {
            dispose();
            new JobListingsPage(loggedInUserId); // Redirect to Job Listings Page after saving
        }
    }

    private void saveProfile() {
        String name = nameField.getText();
        String email = emailField.getText();
        String experience = experienceField.getText();
        String salaryExpectation = (String) salaryComboBox.getSelectedItem();
        String city = (String) cityComboBox.getSelectedItem();

        // Collect selected skills
        StringBuilder selectedSkills = new StringBuilder();
        for (JCheckBox skill : skillsCheckboxes) {
            if (skill.isSelected()) {
                if (selectedSkills.length() > 0) {
                    selectedSkills.append(", ");
                }
                selectedSkills.append(skill.getText());
            }
        }

        // Validate fields
        if (name.isEmpty() || experience.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Experience, and Email are required!");
            return;
        }

        try {
            Conn conn = new Conn();
            String query = "INSERT INTO job_seeker_profiles (user_id, name, email, experience, salary_expectation, city, skills) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                         + "name = ?, email = ?, experience = ?, salary_expectation = ?, city = ?, skills = ?";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setInt(1, loggedInUserId);
            pst.setString(2, name);
            pst.setString(3, email);
            pst.setString(4, experience);
            pst.setString(5, salaryExpectation);
            pst.setString(6, city);
            pst.setString(7, selectedSkills.toString());
            pst.setString(8, name);
            pst.setString(9, email);
            pst.setString(10, experience);
            pst.setString(11, salaryExpectation);
            pst.setString(12, city);
            pst.setString(13, selectedSkills.toString());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Profile saved successfully!");
            dispose();
            new JobListingsPage(loggedInUserId); // Redirect to Job Listings Page after saving
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving profile: " + e.getMessage());
        }
    }
}
