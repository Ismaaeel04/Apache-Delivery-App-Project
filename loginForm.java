import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class loginForm extends JFrame {

    public void initialize() {

        setTitle("Login - APACHE Delivery App");
        setSize(420, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 45));

        // Center card
        JPanel card = new JPanel(new GridLayout(0, 1, 0, 12));
        card.setBackground(new Color(42, 42, 60));
        card.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        // Title
        JLabel title = new JLabel("APACHE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(99, 102, 241));

        JLabel subtitle = new JLabel("Delivery App", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(160, 160, 180));

        // Fields
        JTextField tfEmail       = createField("Email");
        JPasswordField tfPassword = new JPasswordField();
        styleField(tfPassword, "Password");

        // Login button
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 15));
        btnLogin.setBackground(new Color(99, 102, 241));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 40));

        // Register link
        JButton btnRegister = new JButton("Create Account");
        btnRegister.setFont(new Font("Arial", Font.PLAIN, 13));
        btnRegister.setForeground(new Color(99, 102, 241));
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerPanel.setBackground(new Color(42, 42, 60));
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setForeground(new Color(160, 160, 180));
        noAccount.setFont(new Font("Arial", Font.PLAIN, 13));
        registerPanel.add(noAccount);
        registerPanel.add(btnRegister);

        card.add(title);
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(createLabel("Email"));
        card.add(tfEmail);
        card.add(createLabel("Password"));
        card.add(tfPassword);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(btnLogin);
        card.add(registerPanel);

        // Wrap card so it doesn't stretch
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(30, 30, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.85;
        gbc.insets = new Insets(30, 30, 30, 30);
        wrapper.add(card, gbc);

        add(wrapper, BorderLayout.CENTER);

        // Button actions
        btnLogin.addActionListener(e -> {
            String email    = tfEmail.getText();
            String password = String.valueOf(tfPassword.getPassword());

            user User = getAuthenticatedUser(email, password);

            if (User != null) {
                MainFrame mainFrame = new MainFrame();
                mainFrame.initialize(User);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Email or Password Invalid", "Try again",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegister.addActionListener(e -> {
            registerForm register = new registerForm();
            register.initialize();
        });

        setVisible(true);
    }

    // Styled text field
    private JTextField createField(String placeholder) {
        JTextField field = new JTextField();
        styleField(field, placeholder);
        return field;
    }

    private void styleField(JTextField field, String placeholder) {
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

    private user getAuthenticatedUser(String email, String password) {
        user user = null;
        try {
            Connection conn = data_base.getConnection();
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user          = new user();
                user.id       = rs.getInt("id");
                user.username = rs.getString("username");
                user.email    = rs.getString("email");
                user.address  = rs.getString("address");
                user.password = rs.getString("password");
                user.phone    = rs.getString("phone");
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return user;
    }

    public static void main(String[] args) {
        loginForm loginForm = new loginForm();
        loginForm.initialize();
    }
}