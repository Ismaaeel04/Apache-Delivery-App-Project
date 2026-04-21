import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class registerForm extends JFrame {

    public void initialize() {

        setTitle("Create Account - APACHE Delivery App");
        setSize(420, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 45));

        // Card
        JPanel card = new JPanel(new GridLayout(0, 1, 0, 10));
        card.setBackground(new Color(42, 42, 60));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(99, 102, 241));

        JLabel subtitle = new JLabel("Fill in your details below", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(160, 160, 180));

        // Fields
        JTextField     tfUsername  = createField();
        JTextField     tfEmail     = createField();
        JPasswordField tfPassword  = new JPasswordField();
        JPasswordField tfPassword2 = new JPasswordField();
        JTextField     tfPhone     = createField();
        JTextField     tfAddress   = createField();

        styleField(tfPassword);
        styleField(tfPassword2);

        // Register button
        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 15));
        btnRegister.setBackground(new Color(99, 102, 241));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.setBackground(new Color(55, 55, 75));
        btnCancel.setForeground(new Color(200, 200, 200));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(new Color(42, 42, 60));
        btnPanel.add(btnRegister);
        btnPanel.add(btnCancel);

        card.add(title);
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(createLabel("Username"));   card.add(tfUsername);
        card.add(createLabel("Email"));      card.add(tfEmail);
        card.add(createLabel("Password"));   card.add(tfPassword);
        card.add(createLabel("Confirm Password")); card.add(tfPassword2);
        card.add(createLabel("Phone"));      card.add(tfPhone);
        card.add(createLabel("Address"));    card.add(tfAddress);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(btnPanel);

        // Wrap card
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(30, 30, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.85;
        gbc.insets = new Insets(20, 30, 20, 30);
        wrapper.add(card, gbc);

        add(wrapper, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> {
            String username  = tfUsername.getText().trim();
            String email     = tfEmail.getText().trim();
            String password  = String.valueOf(tfPassword.getPassword());
            String password2 = String.valueOf(tfPassword2.getPassword());
            String phone     = tfPhone.getText().trim();
            String address   = tfAddress.getText().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                    || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            if (!password.equals(password2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }

            if (registerUser(username, email, password, phone, address)) {
                JOptionPane.showMessageDialog(this, "Account created! You can now log in.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Email may already be in use.");
            }
        });

        setVisible(true);
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(55, 55, 75));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 110), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(160, 160, 180));
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