package PHASE_B;

import javax.swing.*;
import java.awt.*;

public class GUI {

    public GUI(){
        JFrame frame = new JFrame("Phase B GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 550);

        // Create three text fields
        JTextField textField1 = new JTextField(30);
        JTextField textField2 = new JTextField(10);

        // Create the labels
        JLabel queryLabel = new JLabel("Query: ");
        JLabel typeLabel = new JLabel("Type: ");

        // Create button
        JButton button = new JButton("Search");

        // Create a panel to hold the text fields
        JPanel textFieldPanel = new JPanel();
        textFieldPanel.setLayout(new FlowLayout()); // Use FlowLayout to arrange components horizontally
        textFieldPanel.add(queryLabel);
        textFieldPanel.add(textField1);
        textFieldPanel.add(typeLabel);
        textFieldPanel.add(textField2);
        textFieldPanel.add(button);
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

        // Create a panel to hold the text fields and separator
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(textFieldPanel, BorderLayout.CENTER);
        topPanel.add(separator, BorderLayout.SOUTH); // Add separator below the text fields

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.setVisible(true);
    }
}
