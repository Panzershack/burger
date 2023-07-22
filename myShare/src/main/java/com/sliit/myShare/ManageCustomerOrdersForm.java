/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sliit.myShare;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManageCustomerOrdersForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sliit"; // Replace "mydatabase" with your database name
    private static final String USER = "root"; // Replace "username" with your MySQL username
    private static final String PASSWORD = ""; // Replace "password" with your MySQL password

    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public ManageCustomerOrdersForm() {
        setTitle("Manage Customer Orders");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] { "Order ID", "Customer Name", "Email" ,"Status","Employee ID" });

        // Create the table with the table model
        ordersTable = new JTable(tableModel);

        // Add the table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(ordersTable);

        // Create the add order button
        JButton addOrderButton = new JButton("Add Order");
        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddOrderDialog();
            }
        });

        // Create the edit order button
        JButton editOrderButton = new JButton("Edit Order");
        editOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditOrderDialog(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(
                            ManageCustomerOrdersForm.this,
                            "Please select an order to edit.",
                            "Edit Order",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create the remove order button
        JButton removeOrderButton = new JButton("Remove Order");
        removeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(
                            ManageCustomerOrdersForm.this,
                            "Are you sure you want to remove this order?",
                            "Remove Order",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        removeOrder(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            ManageCustomerOrdersForm.this,
                            "Please select an order to remove.",
                            "Remove Order",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });
        
                // Create the edit order button
        JButton assignOrderButton = new JButton("Assign Order");
        assignOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    showAssignOrderDialog(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(
                            ManageCustomerOrdersForm.this,
                            "Please select an order to assign.",
                            "Assign Order",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addOrderButton);
        buttonPanel.add(editOrderButton);
        buttonPanel.add(removeOrderButton);
        buttonPanel.add(assignOrderButton);
                
        // Add the table scroll pane and button panel to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display the orders
        fetchOrders();

        setVisible(true);
    }

    private void fetchOrders() {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to fetch orders from the database
            String query = "SELECT * FROM orders";
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the table
            tableModel.setRowCount(0);

            // Retrieve the row data from the ResultSet and add to the table model
            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                String customerName = resultSet.getString("customer_name");
                String email = resultSet.getString("email");
                String status = resultSet.getString("status");
                String empNo = resultSet.getString("empNo");

                tableModel.addRow(new Object[] { orderId, customerName, email ,status, empNo });
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showAddOrderDialog() {
        JTextField orderIdField = new JTextField();
        JTextField customerNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField statusField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Order ID:"));
        inputPanel.add(orderIdField);
        inputPanel.add(new JLabel("Customer Name:"));
        inputPanel.add(customerNameField);
        inputPanel.add(new JLabel("Customer email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Add New Order",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int orderId = Integer.parseInt(orderIdField.getText());
                String customerName = customerNameField.getText();
                String email = emailField.getText();
                String status = statusField.getText();

                // Perform the database insert
                insertOrder(orderId, customerName,email ,status);

                // Refresh the orders
                fetchOrders();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Order ID. Please enter a numeric value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showEditOrderDialog(int selectedRow) {
        int orderId = (int) ordersTable.getValueAt(selectedRow, 0);
        String customerName = (String) ordersTable.getValueAt(selectedRow, 1);
        String email = (String) ordersTable.getValueAt(selectedRow, 2);
        String status = (String) ordersTable.getValueAt(selectedRow, 3);

        JTextField orderIdField = new JTextField(String.valueOf(orderId));
        JTextField customerNameField = new JTextField(customerName);
        JTextField emailField = new JTextField(email);
        JTextField statusField = new JTextField(status);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Order ID:"));
        inputPanel.add(orderIdField);
        inputPanel.add(new JLabel("Customer Name:"));
        inputPanel.add(customerNameField);
        inputPanel.add(new JLabel("Customer Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Edit Order",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newOrderId = Integer.parseInt(orderIdField.getText());
                String newCustomerName = customerNameField.getText();
                String newEmail = emailField.getText();
                String newStatus = statusField.getText();

                // Perform the database update
                updateOrder(selectedRow, newOrderId, newCustomerName, newEmail,newStatus);

                // Refresh the orders
                fetchOrders();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Order ID. Please enter a numeric value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void showAssignOrderDialog(int selectedRow){
        int orderId = (int) ordersTable.getValueAt(selectedRow, 0);

        JTextField empField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Emp ID to assign:"));
        inputPanel.add(empField);
        
        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Assign Order",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int empId = Integer.parseInt(empField.getText());

                // Perform the database update
                assignOrder(orderId, empId);

                // Refresh the orders
                fetchOrders();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid ID. Please enter a numeric value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void insertOrder(int orderId, String customerName, String email,String status) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateToDatabase = dateFormat.format(currentDate);
            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "INSERT INTO orders (order_id, customer_name, email,status,date) VALUES (?, ?, ?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderId);
            statement.setString(2, customerName);
            statement.setString(3, email);
            statement.setString(4, status);
            statement.setString(5, dateToDatabase);

            // Execute the insert statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateOrder(int selectedRow, int orderId, String customerName, String email ,String status) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "UPDATE orders SET order_id=?, customer_name=?, email=?, status=? WHERE order_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderId);
            statement.setString(2, customerName);
            statement.setString(3, email);
            statement.setString(4, status);
            statement.setInt(5, (int) ordersTable.getValueAt(selectedRow, 0));

            // Execute the update statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void removeOrder(int selectedRow) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "DELETE FROM orders WHERE order_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, (int) ordersTable.getValueAt(selectedRow, 0));

            // Execute the delete statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();

            // Refresh the orders
            fetchOrders();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void assignOrder(int orderId,int empId){
        assignEmployee(orderId,empId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ManageCustomerOrdersForm();
            }
        });
    }
    
    private void assignEmployee(int orderId, int empId) {
        new EmailEmployeeForm(orderId, empId);
    }
}
