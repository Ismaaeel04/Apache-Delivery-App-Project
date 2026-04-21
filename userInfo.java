import java.awt.*;
import javax.swing.*;

public class userInfo extends JPanel {

    public userInfo(user user) {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title at the top
        JLabel title = new JLabel("My Profile", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Card in the center
        JPanel card = new JPanel(new GridLayout(0, 1, 0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        card.add(createRow("Username", user.username));
        card.add(createRow("Email",    user.email));
        card.add(createRow("Phone",    user.phone));
        card.add(createRow("Address",  user.address));

        add(card, BorderLayout.CENTER);
    }

    // Each row has a bold label and the value below with a separator line
    private JPanel createRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Verdana", Font.BOLD, 13));
        label.setForeground(Color.GRAY);

        JLabel value = new JLabel(valueText != null ? valueText : "-");
        value.setFont(new Font("Verdana", Font.PLAIN, 16));
        value.setForeground(Color.BLACK);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 220, 220));

        row.add(label, BorderLayout.NORTH);
        row.add(value, BorderLayout.CENTER);
        row.add(sep,   BorderLayout.SOUTH);

        return row;
    }
}