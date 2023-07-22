/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sliit.myShare;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MyShare extends JFrame {

    
    private JButton manageOrdersButton;
    private JButton manageSuppliersButton;
    private JButton manageInventoryButton;
    private JButton manageEmployeesButton;
    private JButton generateReportsButton;
    private JButton readAnOrderButton;

    public MyShare() {
        // Set up the main window
        setTitle("MyShare Application");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the buttons
        manageOrdersButton = new JButton("Manage Customer Orders");
        manageSuppliersButton = new JButton("Manage Suppliers");
        manageInventoryButton = new JButton("Manage Inventory");
        manageEmployeesButton = new JButton("Manage Employees");
        generateReportsButton = new JButton("Generate Reports");
        readAnOrderButton = new JButton("Ready an Order");
        

        // Set up button action listeners
        manageOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              showManageOrdersForm();
            }
        });

        manageSuppliersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageSuppliersForm();
            }
        });

        manageInventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageInventoryForm();
            }
        });

        manageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManageEmployeesForm();
            }
        });


        generateReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMonthlySalesReportForm();
            }
        });
        
        readAnOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEmailSenderForm();
            }
        });

        // Create the main panel and add the buttons
        ImageIcon originalImageIcon = new ImageIcon("images/image.jpg");
        Image originalImage = originalImageIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);

        // Create a label to display the image
        JLabel imageLabel = new JLabel(scaledImageIcon);

        // Add the label to the frame
        JPanel mainPanel = new JPanel(new GridLayout(4, 2));
        mainPanel.add(imageLabel, BorderLayout.CENTER);
        mainPanel.add(new JPanel());
        mainPanel.add(manageOrdersButton);
        mainPanel.add(manageSuppliersButton);
        mainPanel.add(manageInventoryButton);
        mainPanel.add(manageEmployeesButton);
        mainPanel.add(readAnOrderButton);
        mainPanel.add(generateReportsButton);
        

        // Add the main panel to the frame
        add(mainPanel);

        // Display the main window
        setVisible(true);
    }
        

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MyShare();
            }
        });
    }
    
    private void showManageOrdersForm() {
        new ManageCustomerOrdersForm();
    }
    
    private void showManageSuppliersForm() {
        new ManageSupplierOrdersForm();
    }
    
    private void showEmailSenderForm() {
        new EmailSenderForm();
    }
    
    private void showManageEmployeesForm() {
        new ManageEmployeesForm();
    }
            
    private void showManageInventoryForm() {
        new ManageInventoryForm();
    }
            
    private void showMonthlySalesReportForm() {
        new MonthlySalesReportForm();
    }


}
