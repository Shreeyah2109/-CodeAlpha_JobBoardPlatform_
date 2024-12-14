package job.board.platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class JobListingsPage extends JFrame {
    JPanel jobsPanel;
    JButton backButton;
    int loggedInUserId;

    public JobListingsPage(int userId) {
        this.loggedInUserId = userId;
        setTitle("Job Listings");
        setSize(800, 600);
        setLocation(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Layout
        setLayout(new BorderLayout());

        // Jobs Panel
        jobsPanel = new JPanel();
        jobsPanel.setLayout(new BoxLayout(jobsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(jobsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Load Jobs
        loadJobs();

        setVisible(true);
    }

    private void loadJobs() {
        try {
            Conn conn = new Conn();
            String query = "SELECT j.job_id, j.job_title, j.location, c.company_name " +
                           "FROM jobs j JOIN companies c ON j.company_id = c.company_id";
            PreparedStatement pst = conn.c.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String jobTitle = rs.getString("job_title");
                String location = rs.getString("location");
                String companyName = rs.getString("company_name");

                JPanel jobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel jobLabel = new JLabel("Job: " + jobTitle + " at " + companyName + " in " + location);
                jobLabel.setFont(new Font("Arial", Font.BOLD, 16));
                JButton applyButton = new JButton("Apply");

                // Open Apply Job Page when clicking "Apply"
                applyButton.addActionListener(e -> {
                    dispose();
                    new ApplyJobPage(loggedInUserId, jobId, companyName);
                });

                jobPanel.add(jobLabel);
                jobPanel.add(applyButton);
                jobsPanel.add(jobPanel);
            }

            jobsPanel.revalidate();
            jobsPanel.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading jobs: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new JobListingsPage(1); // Assuming user_id = 1
    }
}
