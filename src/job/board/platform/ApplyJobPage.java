package job.board.platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class ApplyJobPage extends JFrame {
    private int loggedInUserId, jobId;
    private String companyName, email;
    private JTextArea notesArea;
    private JTextField linkedinUrlField;
    private JComboBox<String> positionComboBox, availabilityComboBox, timingsComboBox;
    private JLabel resumeFileLabel;
    private File selectedResumeFile;

    public ApplyJobPage(int userId, int jobId, String companyName) {
        this.loggedInUserId = userId;
        this.jobId = jobId;
        this.companyName = companyName;

        setTitle("Apply for Job: " + companyName);
        setSize(600, 600);
        setLocation(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Apply for Job: " + companyName, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Scrollable Panel
        JPanel applyPanel = new JPanel();
        applyPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Get company email
        getCompanyEmail();

        // Contact Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        applyPanel.add(new JLabel("Contact Email:"), gbc);

        gbc.gridx = 1;
        JLabel emailLabel = new JLabel(email != null ? email : "N/A");
        applyPanel.add(emailLabel, gbc);

        // Resume Upload
        gbc.gridx = 0;
        gbc.gridy++;
        applyPanel.add(new JLabel("Upload Resume:"), gbc);

        gbc.gridx = 1;
        JButton uploadButton = new JButton("Choose File");
        resumeFileLabel = new JLabel("No file selected");
        uploadButton.addActionListener(e -> chooseResumeFile());
        applyPanel.add(uploadButton, gbc);

        gbc.gridx = 2;
        applyPanel.add(resumeFileLabel, gbc);

        // LinkedIn URL
        gbc.gridx = 0;
        gbc.gridy++;
        applyPanel.add(new JLabel("LinkedIn Profile URL:"), gbc);

        gbc.gridx = 1;
        linkedinUrlField = new JTextField(30);
        applyPanel.add(linkedinUrlField, gbc);

        // Position
        gbc.gridx = 0;
        gbc.gridy++;
        applyPanel.add(new JLabel("Position Applied For:"), gbc);

        gbc.gridx = 1;
        positionComboBox = new JComboBox<>(new String[]{"Junior", "Mid", "Senior", "Lead"});
        applyPanel.add(positionComboBox, gbc);

        // Availability
        gbc.gridx = 0;
        gbc.gridy++;
        applyPanel.add(new JLabel("Availability:"), gbc);

        gbc.gridx = 1;
        availabilityComboBox = new JComboBox<>(new String[]{"Immediate", "1 Week", "2 Weeks", "1 Month"});
        applyPanel.add(availabilityComboBox, gbc);

        // Preferred Timings
        gbc.gridx = 0;
        gbc.gridy++;
        applyPanel.add(new JLabel("Preferred Timings:"), gbc);

        gbc.gridx = 1;
        timingsComboBox = new JComboBox<>(new String[]{"Morning", "Afternoon", "Evening", "Flexible"});
        applyPanel.add(timingsComboBox, gbc);

        // Notes Area
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTH;
        applyPanel.add(new JLabel("Additional Notes:"), gbc);

        gbc.gridx = 1;
        notesArea = new JTextArea(5, 30);
        notesArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        applyPanel.add(notesScroll, gbc);

        // Submit Button
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("Submit Application");
        submitButton.addActionListener(ae -> submitApplication());
        applyPanel.add(submitButton, gbc);

        // Scrollable Panel
        JScrollPane scrollPane = new JScrollPane(applyPanel);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void chooseResumeFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedResumeFile = fileChooser.getSelectedFile();
            resumeFileLabel.setText(selectedResumeFile.getName());
        }
    }

    private void getCompanyEmail() {
        try {
            Conn conn = new Conn();
            String query = "SELECT contact_email FROM companies WHERE company_name = ?";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setString(1, companyName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                email = rs.getString("contact_email");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving company email: " + e.getMessage());
        }
    }

    private void submitApplication() {
        try {
            if (selectedResumeFile == null) {
                JOptionPane.showMessageDialog(this, "Please upload your resume.");
                return;
            }

            Conn conn = new Conn();
            String query = "INSERT INTO job_applications (user_id, job_id, resume_url, linkedin_profile_url, position, availability, timings, notes, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setInt(1, loggedInUserId);
            pst.setInt(2, jobId);
            pst.setString(3, selectedResumeFile.getAbsolutePath());
            pst.setString(4, linkedinUrlField.getText());
            pst.setString(5, positionComboBox.getSelectedItem().toString());
            pst.setString(6, availabilityComboBox.getSelectedItem().toString());
            pst.setString(7, timingsComboBox.getSelectedItem().toString());
            pst.setString(8, notesArea.getText());
            pst.setString(9, email);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            dispose();
            new JobApplicationTracker(loggedInUserId); // Open JobApplicationTracker after closing this page
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error submitting application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ApplyJobPage(1, 101, "TCS"); // Example: user_id = 1, job_id = 101, company_name = "TCS"
    }
}
