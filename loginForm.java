import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class loginForm extends JFrame {

    final private Font mainFont = new Font("Verdana", Font.BOLD, 18);

    public void initialize() {

        setTitle("Login - APACHE Delivery App");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Login Form", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Form fields in the center
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JTextField tfEmail        = new JTextField();
        JPasswordField tfPassword = new JPasswordField();

        tfEmail.setFont(mainFont);
        tfPassword.setFont(mainFont);
        tfEmail.setPreferredSize(new Dimension(0, 35));
        tfPassword.setPreferredSize(new Dimension(0, 35));

        formPanel.add(createLabel("Email"));
        formPanel.add(tfEmail);
        formPanel.add(createLabel("Password"));
        formPanel.add(tfPassword);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(mainFont);
        btnLogin.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(mainFont);
        btnCancel.setBackground(Color.WHITE);

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email    = tfEmail.getText();
                String password = String.valueOf(tfPassword.getPassword());

                user User = getAuthenticatedUser(email, password);

                if (User != null) {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.initialize(User);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Email or Password Invalid", "Try again",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnCancel);

        // Register link at the bottom
        JButton btnRegister = new JButton("Create Account");
        btnRegister.setFont(new Font("Verdana", Font.PLAIN, 13));
        btnRegister.setForeground(new Color(70, 130, 180));
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerForm register = new registerForm();
                register.initialize();
            }
        });

        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(new Font("Verdana", Font.PLAIN, 13));

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 5));
        registerPanel.add(noAccount);
        registerPanel.add(btnRegister);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonsPanel,   BorderLayout.NORTH);
        southPanel.add(registerPanel,  BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Verdana", Font.BOLD, 16));
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
        loginForm form = new loginForm();
        form.initialize();
    }
}