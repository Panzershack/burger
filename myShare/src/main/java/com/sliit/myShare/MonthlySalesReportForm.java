/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sliit.myShare;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MonthlySalesReportForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sliit"; // Replace "mydatabase" with your database name
    private static final String USER = "root"; // Replace "username" with your MySQL username
    private static final String PASSWORD = ""; // Replace "password" with your MySQL password

    private JDateChooser startDatePicker;
    private JDateChooser endDatePicker;
    private JButton generateButton;
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public MonthlySalesReportForm() {
        setTitle("Monthly Sales Report");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create date pickers
        startDatePicker = new JDateChooser();
        endDatePicker = new JDateChooser();

        // Create generate button
        generateButton = new JButton("Generate Report");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        // Create table model
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"orderId", "Customer Name","Email","Status","Assigned Emp","Date"});
        salesTable = new JTable(tableModel);

        // Add components to the frame
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Start Date:"));
        inputPanel.add(startDatePicker);
        inputPanel.add(new JLabel("End Date:"));
        inputPanel.add(endDatePicker);
        inputPanel.add(generateButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        setVisible(true);
    }

    private void generateReport() {
        Date startDate = startDatePicker.getDate();
        Date endDate = endDatePicker.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);

        // Fetch data from the database based on the selected dates
        fetchSalesData(startDateStr, endDateStr);
    }

    private void fetchSalesData(String startDate, String endDate) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to fetch sales data for the selected month
            String query = "SELECT * FROM orders WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "'";
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the table
            tableModel.setRowCount(0);

            // Retrieve the data from the ResultSet and add to the table model
            while (resultSet.next()) {
                String order_id = Integer.toString(resultSet.getInt("order_id"));
                String customer_name = resultSet.getString("customer_name");
                String email = resultSet.getString("email");
                String status = resultSet.getString("status");
                String empNo = Integer.toString(resultSet.getInt("empNo"));
                String date = resultSet.getString("date");

                tableModel.addRow(new Object[]{order_id,customer_name,email,status,empNo,date});
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MonthlySalesReportForm();
            }
        });
    }
}
