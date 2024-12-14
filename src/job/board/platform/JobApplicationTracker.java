package job.board.platform;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class JobApplicationTracker extends JFrame {
    private int userId;
    private JTable applicationTable;
    private DefaultTableModel tableModel;

    public JobApplicationTracker(int userId) {
        this.userId = userId;

        setTitle("Job Application Tracker");
        setSize(800, 600);
        setLocation(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Job Title", "Company Name", "Submission Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        applicationTable = new JTable(tableModel);

        // Customize table appearance
        applicationTable.setRowHeight(30);
        applicationTable.setFont(new Font("Arial", Font.PLAIN, 14));
        applicationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        applicationTable.getTableHeader().setBackground(Color.LIGHT_GRAY);

        // Scroll Pane for table
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Refresh Button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> fetchApplications());
        buttonPanel.add(refreshButton);

        // Delete Button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = applicationTable.getSelectedRow();
                if (selectedRow != -1) {
                    String jobTitle = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this application for '" + jobTitle + "'?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteApplication(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(deleteButton);

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.addActionListener(e -> dispose()); // Close the window
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display data
        fetchApplications();

        setVisible(true);
    }

    private void fetchApplications() {
        try {
            // Clear existing rows
            tableModel.setRowCount(0);

            // Database connection
            Conn conn = new Conn();
            String query = "SELECT j.job_title, c.company_name, a.applied_date, a.status, a.application_id " +
                           "FROM job_applications a " +
                           "JOIN jobs j ON a.job_id = j.job_id " +
                           "JOIN companies c ON j.company_id = c.company_id " +
                           "WHERE a.user_id = ?";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            // Add rows to table
            while (rs.next()) {
                String jobTitle = rs.getString("job_title");
                String companyName = rs.getString("company_name");
                String submissionDate = rs.getDate("applied_date").toString();
                String status = rs.getString("status");
                tableModel.addRow(new Object[]{jobTitle, companyName, submissionDate, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching applications: " + e.getMessage());
        }
    }

    private void deleteApplication(int rowIndex) {
        try {
            // Database connection
            Conn conn = new Conn();
            String jobTitle = (String) tableModel.getValueAt(rowIndex, 0);
            String query = "DELETE a FROM job_applications a " +
                           "JOIN jobs j ON a.job_id = j.job_id " +
                           "WHERE j.job_title = ? AND a.user_id = ?";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setString(1, jobTitle);
            pst.setInt(2, userId);
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Application deleted successfully!");
                fetchApplications(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete application.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new JobApplicationTracker(1); // Example: user_id = 1
    }
}
