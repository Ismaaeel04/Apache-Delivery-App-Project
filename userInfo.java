import java.awt.*;
import javax.swing.*;

public class userInfo extends JPanel {

    public userInfo(user user) {

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 45));

        // Top banner with name
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(30, 30, 45));
        banner.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        // Big circle avatar with the first letter of the username
        String firstLetter = (user.username != null && !user.username.isEmpty())
                ? String.valueOf(user.username.charAt(0)).toUpperCase() : "?";

        JLabel avatar = new JLabel(firstLetter, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99, 102, 241));
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Arial", Font.BOLD, 36));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(90, 90));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setVerticalAlignment(SwingConstants.CENTER);

        // Center the avatar
        JPanel avatarWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarWrapper.setBackground(new Color(30, 30, 45));
        avatarWrapper.add(avatar);

        JLabel nameLabel = new JLabel(user.username != null ? user.username : "-", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Account Details", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subLabel.setForeground(new Color(160, 160, 180));
        subLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel namePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        namePanel.setBackground(new Color(30, 30, 45));
        namePanel.add(nameLabel);
        namePanel.add(subLabel);

        banner.add(avatarWrapper, BorderLayout.NORTH);
        banner.add(namePanel,     BorderLayout.CENTER);
        add(banner, BorderLayout.NORTH);

        // Card with all the info fields
        JPanel card = new JPanel(new GridLayout(0, 1, 0, 0));
        card.setBackground(new Color(42, 42, 60));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));

        card.add(createRow("EMAIL",    user.email));
        card.add(createRow("PHONE",    user.phone));
        card.add(createRow("ADDRESS",  user.address));

        // Limit the card width so it doesn't stretch across the whole screen
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setBackground(new Color(30, 30, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 80, 40, 80);
        cardWrapper.add(card, gbc);

        add(cardWrapper, BorderLayout.CENTER);
    }

    // Each row has a small colored label and a white value below it
    private JPanel createRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setBackground(new Color(42, 42, 60));
        row.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(new Color(99, 102, 241));

        JLabel value = new JLabel(valueText != null ? valueText : "-");
        value.setFont(new Font("Arial", Font.PLAIN, 15));
        value.setForeground(new Color(220, 220, 235));

        // Thin separator between rows
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 80));
        sep.setBackground(new Color(60, 60, 80));

        row.add(label, BorderLayout.NORTH);
        row.add(value, BorderLayout.CENTER);
        row.add(sep,   BorderLayout.SOUTH);

        return row;
    }
}