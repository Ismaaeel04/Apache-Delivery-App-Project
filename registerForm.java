import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class registerForm extends JFrame {

    final private Font mainFont = new Font("Verdana", Font.BOLD, 16);

    public void initialize() {

        setTitle("Create Account - APACHE Delivery App");
        setSize(400, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Form fields
        JTextField     tfUsername  = new JTextField();
        JTextField     tfEmail     = new JTextField();
        JPasswordField tfPassword  = new JPasswordField();
        JPasswordField tfPassword2 = new JPasswordField();
        JTextField     tfPhone     = new JTextField();
        JTextField     tfAddress   = new JTextField();

        // Set font to all fields manually
        tfUsername.setFont(mainFont);
        tfEmail.setFont(mainFont);
        tfPhone.setFont(mainFont);
        tfAddress.setFont(mainFont);
        tfPassword.setFont(mainFont);
        tfPassword2.setFont(mainFont);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        formPanel.add(createLabel("Username"));         formPanel.add(tfUsername);
        formPanel.add(createLabel("Email"));            formPanel.add(tfEmail);
        formPanel.add(createLabel("Password"));         formPanel.add(tfPassword);
        formPanel.add(createLabel("Confirm Password")); formPanel.add(tfPassword2);
        formPanel.add(createLabel("Phone"));            formPanel.add(tfPhone);
        formPanel.add(createLabel("Address"));          formPanel.add(tfAddress);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(mainFont);
        btnRegister.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(mainFont);
        btnCancel.setBackground(Color.WHITE);

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String username  = tfUsername.getText().trim();
                String email     = tfEmail.getText().trim();
                String password  = String.valueOf(tfPassword.getPassword());
                String password2 = String.valueOf(tfPassword2.getPassword());
                String phone     = tfPhone.getText().trim();
                String address   = tfAddress.getText().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                        || phone.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }

                if (password.equals(password2) == false) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.");
                    return;
                }

                if (registerUser(username, email, password, phone, address)) {
                    JOptionPane.showMessageDialog(null, "Account created! You can now log in.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed. Email may already be in use.");
                }
            }
        });

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        buttonsPanel.add(btnRegister);
        buttonsPanel.add(btnCancel);

        add(buttonsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Verdana", Font.BOLD, 14));
        return label;
    }

    private boolean registerUser(String username, String email, String password,
                                  String phone, String address) {
        String query = "INSERT INTO users (username, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = data_base.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
}