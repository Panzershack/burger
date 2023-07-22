/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sliit.myShare;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailEmployeeForm extends JFrame {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sliit"; // Replace "mydatabase" with your database name
    private static final String USER = "root"; // Replace "username" with your MySQL username
    private static final String PASSWORD = ""; // Replace "password" with your MySQL password
    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
//    Go to https://myaccount.google.com/u/7/lesssecureapps and toggle on Allow Less secure apps
    private static final String EMAIL_USERNAME = "it22332394@my.sliit.lk"; // Replace with your Gmail username
    private static final String EMAIL_PASSWORD = "Fightclub2003$"; // Replace with your Gmail password


    private int orderId;
    private int empId;
    

    public EmailEmployeeForm(int orderId, int empId) {

        this.orderId = orderId;
        this.empId = empId;
        System.out.println(this.orderId);
        System.out.println(this.empId);

        sendEmail();
    }

    private void sendEmail() {
        
        // Fetch order from the database
        Order order = fetchOrder(this.empId);

        if (order != null) {
            // Update status column in the database
            updateOrderStatus(orderId, this.empId);

            // Send email
            sendEmail(order);

            JOptionPane.showMessageDialog(EmailEmployeeForm.this,
                    "Email sent successfully!",
                    "Email Sent",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(EmailEmployeeForm.this,
                    "Order not found. Please enter a valid Order ID.",
                    "Order Not Found",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Order fetchOrder(int empId) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to fetch the order from the database
            String query = "SELECT * FROM employee WHERE emp_id = " + empId;
            ResultSet resultSet = statement.executeQuery(query);

            // Check if the order exists
            if (resultSet.next()) {
                // Retrieve the order details
                String customerName = resultSet.getString("emp_name");
                String email = resultSet.getString("email");

                // Create and return the Order object
                return new Order(empId, customerName, email);
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

        return null;
    }

    private void updateOrderStatus(int orderId, int empId) {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create a prepared statement
            String query = "UPDATE orders SET empNo = ? WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, empId);
            statement.setInt(2, orderId);

            // Execute the update statement
            statement.executeUpdate();

            // Close the resources
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void sendEmail(Order order) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getEmail()));
            message.setSubject("Order Status Update");
            message.setText("Dear " + order.getCustomerName() + ",\n\nYou have new order with ID " + order.getOrderId() + ".");

            Transport.send(message);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Email sent to: " + order.getEmail());
        System.out.println("Order details: " + order.toString());
    }

    private static class Order {
        private int orderId;
        private String customerName;
        private String email;

        public Order(int orderId, String customerName, String email) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.email = email;
        }

        public int getOrderId() {
            return orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "Order ID: " + orderId + ", Customer Name: " + customerName + ", Email: " + email;
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new EmailEmployeeForm(orderId, empId);
//            }
//        });
//    }
}
