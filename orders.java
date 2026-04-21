import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;

public class orders extends JPanel {

    private Connection connection;
    private user currentUser;
    private JPanel ordersListPanel;

    public orders(Connection connection, user currentUser) {
        this.connection  = connection;
        this.currentUser = currentUser;

        setBackground(Color.orange);
        setLayout(new BorderLayout(10, 10));

        // Title at the top
        JLabel title = new JLabel("MY ORDERS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Panel that holds all order groups
        ordersListPanel = new JPanel();
        ordersListPanel.setLayout(new BoxLayout(ordersListPanel, BoxLayout.Y_AXIS));
        ordersListPanel.setBackground(Color.orange);

        loadOrders();

        JScrollPane scroll = new JScrollPane(ordersListPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.orange);
        add(scroll, BorderLayout.CENTER);

        // Refresh button
        JButton btnRefresh = new JButton("Refresh Orders");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> {
            ordersListPanel.removeAll();
            loadOrders();
            ordersListPanel.revalidate();
            ordersListPanel.repaint();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.orange);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadOrders() {
        // Fetch all orders sorted by group so products stay together
        String query = "SELECT id, product_name, order_date, status, order_group_id " +
                       "FROM orders WHERE user_id = ? ORDER BY order_group_id DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();

            // Use a map to group products by order_group_id
            // LinkedHashMap keeps the insertion order so newest order shows first
            Map<Integer, ArrayList<String>> groupProducts = new LinkedHashMap<>();
            Map<Integer, String>            groupDate     = new LinkedHashMap<>();
            Map<Integer, Integer>           groupStatus   = new LinkedHashMap<>();

            while (rs.next()) {
                int    groupId     = rs.getInt("order_group_id");
                String productName = rs.getString("product_name");
                String orderDate   = rs.getString("order_date");
                int    status      = rs.getInt("status");

                // If this group id is new, create a new list for it
                if (!groupProducts.containsKey(groupId)) {
                    groupProducts.put(groupId, new ArrayList<>());
                    groupDate.put(groupId, orderDate);
                    groupStatus.put(groupId, status);
                }

                // Add the product to its group
                groupProducts.get(groupId).add(productName);
            }

            if (groupProducts.isEmpty()) {
                JLabel noOrders = new JLabel("No orders yet.", SwingConstants.CENTER);
                noOrders.setFont(new Font("Arial", Font.PLAIN, 16));
                noOrders.setAlignmentX(Component.CENTER_ALIGNMENT);
                ordersListPanel.add(noOrders);
                return;
            }

            // Build one card per order group
            for (int groupId : groupProducts.keySet()) {
                ordersListPanel.add(createOrderCard(
                    groupId,
                    groupProducts.get(groupId),
                    groupDate.get(groupId),
                    groupStatus.get(groupId)
                ));
                ordersListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

        } catch (SQLException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
    }

    // Builds one card showing all products in the same order
    private JPanel createOrderCard(int groupId, ArrayList<String> products, String orderDate, int status) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        // Top row: order number and date
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JLabel idLabel = new JLabel("Order #" + groupId);
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel dateLabel = new JLabel(orderDate, SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        topRow.add(idLabel, BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Middle: list all products in this order
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.WHITE);
        productPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        for (String product : products) {
            JLabel productLabel = new JLabel("• " + product);
            productLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            productPanel.add(productLabel);
        }

        card.add(productPanel, BorderLayout.CENTER);

        // Bottom row: status and completed button
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setBackground(Color.WHITE);

        JLabel statusLabel;
        if (status == 1) {
            statusLabel = new JLabel("✓ Completed", SwingConstants.LEFT);
            statusLabel.setForeground(new Color(0, 150, 0));
        } else {
            statusLabel = new JLabel("⏳ Pending", SwingConstants.LEFT);
            statusLabel.setForeground(new Color(200, 100, 0));
        }
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JButton btnCompleted = new JButton("Mark Completed");
        btnCompleted.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCompleted.setBackground(new Color(0, 180, 0));
        btnCompleted.setForeground(Color.WHITE);

        // Hide the button if already completed
        if (status == 1) {
            btnCompleted.setVisible(false);
        }

        btnCompleted.addActionListener(e -> {
            markGroupAsCompleted(groupId);
            statusLabel.setText("✓ Completed");
            statusLabel.setForeground(new Color(0, 150, 0));
            btnCompleted.setVisible(false);
            card.revalidate();
            card.repaint();
        });

        bottomRow.add(statusLabel, BorderLayout.WEST);
        bottomRow.add(btnCompleted, BorderLayout.EAST);
        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    // Updates all rows with this group id to status = 1
    private void markGroupAsCompleted(int groupId) {
        String query = "UPDATE orders SET status = 1 WHERE order_group_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
            System.out.println("Order group #" + groupId + " marked as completed.");
        } catch (SQLException e) {
            System.out.println("Error updating order: " + e.getMessage());
        }
    }
}