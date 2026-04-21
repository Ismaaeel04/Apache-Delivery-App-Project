import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class orders extends JPanel {

    private Connection connection;
    private user currentUser;
    private JPanel ordersListPanel;

    public orders(Connection connection, user currentUser) {
        this.connection  = connection;
        this.currentUser = currentUser;

        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("MY ORDERS", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        ordersListPanel = new JPanel();
        ordersListPanel.setLayout(new BoxLayout(ordersListPanel, BoxLayout.Y_AXIS));
        ordersListPanel.setBackground(Color.WHITE);

        loadOrders();

        JScrollPane scroll = new JScrollPane(ordersListPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Orders");
        btnRefresh.setFont(new Font("Verdana", Font.PLAIN, 14));
        btnRefresh.setBackground(Color.WHITE);

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ordersListPanel.removeAll();
                loadOrders();
                ordersListPanel.revalidate();
                ordersListPanel.repaint();
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadOrders() {
        String groupQuery = "SELECT DISTINCT order_group_id FROM orders WHERE user_id = ? ORDER BY order_group_id DESC";

        try {
            PreparedStatement stmt = connection.prepareStatement(groupQuery);
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();

            boolean hasOrders = false;

            while (rs.next()) {
                hasOrders = true;
                int groupId = rs.getInt("order_group_id");
                ordersListPanel.add(createOrderCard(groupId));
                ordersListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (hasOrders == false) {
                JLabel noOrders = new JLabel("No orders yet.", SwingConstants.CENTER);
                noOrders.setFont(new Font("Verdana", Font.PLAIN, 16));
                noOrders.setAlignmentX(Component.CENTER_ALIGNMENT);
                ordersListPanel.add(noOrders);
            }

        } catch (SQLException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
    }

    private JPanel createOrderCard(int groupId) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JLabel idLabel = new JLabel("Order #" + groupId);
        idLabel.setFont(new Font("Verdana", Font.BOLD, 14));

        String orderDate = "";
        int status = 0;

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.WHITE);
        productPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT product_name, order_date, status FROM orders WHERE order_group_id = ?"
            );
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orderDate = rs.getString("order_date");
                status    = rs.getInt("status");

                JLabel productLabel = new JLabel("  " + rs.getString("product_name"));
                productLabel.setFont(new Font("Verdana", Font.PLAIN, 13));
                productPanel.add(productLabel);
            }

        } catch (SQLException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }

        JLabel dateLabel = new JLabel(orderDate, SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        topRow.add(idLabel,   BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);
        card.add(topRow,       BorderLayout.NORTH);
        card.add(productPanel, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setBackground(Color.WHITE);

        JLabel statusLabel = new JLabel("Pending");
        statusLabel.setFont(new Font("Verdana", Font.BOLD, 13));
        statusLabel.setForeground(new Color(200, 100, 0));

        if (status == 1) {
            statusLabel.setText("Completed");
            statusLabel.setForeground(new Color(0, 150, 0));
        }

        JButton btnCompleted = new JButton("Mark Completed");
        btnCompleted.setFont(new Font("Verdana", Font.PLAIN, 12));
        btnCompleted.setBackground(Color.WHITE);

        if (status == 1) {
            btnCompleted.setVisible(false);
        }

        btnCompleted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                markAsCompleted(groupId);
                statusLabel.setText("Completed");
                statusLabel.setForeground(new Color(0, 150, 0));
                btnCompleted.setVisible(false);
                card.revalidate();
                card.repaint();
            }
        });

        bottomRow.add(statusLabel,  BorderLayout.WEST);
        bottomRow.add(btnCompleted, BorderLayout.EAST);
        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    private void markAsCompleted(int groupId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE orders SET status = 1 WHERE order_group_id = ?"
            );
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating order: " + e.getMessage());
        }
    }
}