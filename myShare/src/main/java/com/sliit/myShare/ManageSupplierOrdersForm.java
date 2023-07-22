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

public class ManageSupplierOrdersForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sliit"; // Replace "mydatabase" with your database name
    private static final String USER = "root"; // Replace "username" with your MySQL username
    private static final String PASSWORD = ""; // Replace "password" with your MySQL password

    private JTable suppliersTable;
    private DefaultTableModel tableModel;

    public ManageSupplierOrdersForm() {
        setTitle("Manage Suppliers");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] { "NIC", "Supplier Name", "Contact" });

        // Create the table with the table model
        suppliersTable = new JTable(tableModel);

        // Add the table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(suppliersTable);

        // Create the add supplier button
        JButton addOrderButton = new JButton("Add Supplier");
        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddOrderDialog();
            }
        });

        // Create the edit supplier button
        JButton editOrderButton = new JButton("Edit Supplier Detail");
        editOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = suppliersTable.getSelectedRow();
                if (selectedRow != -1) {
                    showEditOrderDialog(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(ManageSupplierOrdersForm.this,
                            "Please select a supplier to edit.",
                            "Edit Supplier",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Create the remove supplier button
        JButton removeOrderButton = new JButton("Remove Supplier");
        removeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = suppliersTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(ManageSupplierOrdersForm.this,
                            "Are you sure you want to remove this Supplier?",
                            "Remove Supplier",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        removeOrder(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(ManageSupplierOrdersForm.this,
                            "Please select an Supplier to remove.",
                            "Remove Supplier",
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

        // Add the table scroll pane and button panel to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display the suppliers
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

            // Execute the query to fetch suppliers from the database
            String query = "SELECT * FROM suppliers";
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the table
            tableModel.setRowCount(0);

            // Retrieve the row data from the ResultSet and add to the table model
            while (resultSet.next()) {
                String nic = resultSet.getString("nic");
                String supplierName = resultSet.getString("supplier_name");
                int contactNumber = resultSet.getInt("contactNumber");

                tableModel.addRow(new Object[] { nic, supplierName, contactNumber });
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
        JTextField nicField = new JTextField();
        JTextField supplierNameField = new JTextField();
        JTextField contactNumberField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("NIC:"));
        inputPanel.add(nicField);
        inputPanel.add(new JLabel("Supplier Name:"));
        inputPanel.add(supplierNameField);
        inputPanel.add(new JLabel("Contact Number:"));
        inputPanel.add(contactNumberField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Add New Supplier",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String nic = nicField.getText();
                String supplierName = supplierNameField.getText();
                int contactNumber = Integer.parseInt(contactNumberField.getText());

                // Perform the database insert
                insertOrder(nic, supplierName, contactNumber);

                // Refresh the suppliers
                fetchOrders();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Supplier ID. Please enter a valid value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showEditOrderDialog(int selectedRow) {
        String nic = (String) suppliersTable.getValueAt(selectedRow, 0);
        String supplierName = (String) suppliersTable.getValueAt(selectedRow, 1);
        int contactNumber = (int) suppliersTable.getValueAt(selectedRow, 2);

        JTextField nicField = new JTextField(String.valueOf(nic));
        JTextField supplierNameField = new JTextField(supplierName);
        JTextField contactNumberField = new JTextField(contactNumber);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("NIC"));
        inputPanel.add(nicField);
        inputPanel.add(new JLabel("Name"));
        inputPanel.add(supplierNameField);
        inputPanel.add(new JLabel("Contact"));
        inputPanel.add(contactNumberField);

        int result = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Edit Supplier",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newOrderId = nicField.getText();
                String newCustomerName = supplierNameField.getText();
                int newContact = Integer.parseInt(contactNumberField.getText());

                // Perform the database update
                updateOrder(selectedRow, newOrderId, newCustomerName, newContact);

                // Refresh the suppliers
                fetchOrders();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Supplier. Please enter a valid value.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void insertOrder(String nic, String supplierName, int contactNumber) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "INSERT INTO suppliers (nic, supplier_name, contactNumber) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nic);
            statement.setString(2, supplierName);
            statement.setInt(3, contactNumber);

            // Execute the insert statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateOrder(int selectedRow, String nic, String supplierName, int contactNumber) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "UPDATE suppliers SET nic=?, supplier_name=?, contactNumber=? WHERE nic=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nic);
            statement.setString(2, supplierName);
            statement.setInt(3, contactNumber);
            statement.setString(4, (String) suppliersTable.getValueAt(selectedRow, 0));

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
            String query = "DELETE FROM suppliers WHERE nic=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, (String) suppliersTable.getValueAt(selectedRow, 0));

            // Execute the delete statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();

            // Refresh the suppliers
            fetchOrders();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ManageSupplierOrdersForm();
            }
        });
    }
}
