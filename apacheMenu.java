import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class apacheMenu extends JPanel {

    // Keeps track of everything the user has added
    private ArrayList<Product> selectedProducts = new ArrayList<>();
    private JLabel totalLabel;
    private JPanel selectedPanel;
    private Connection connection;
    private user currentUser; // the logged in user

    public apacheMenu(Connection connection, user currentUser) {
        this.connection  = connection;
        this.currentUser = currentUser;

        setBackground(new Color(0, 255, 255));
        setLayout(new BorderLayout(10, 10));

        
        add(new JLabel(""));

        // Grid panel: 3 columns, rows added automatically
        JPanel gridProductos = new JPanel(new GridLayout(0, 3, 15, 15));
        gridProductos.setBackground(new Color(0, 255, 255));

        // Get all products from the database
        ArrayList<Product> productList = getMenuDataFromDB(connection);

        // Make a card for each product and add it to the grid
        for (Product p : productList) {
            gridProductos.add(createProductCard(p));
        }

        // Wrap the grid in a scroll panel
        JScrollPane scroll = new JScrollPane(gridProductos);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Bottom panel holds the selected items, total and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(0, 255, 255));

        // Shows the list of selected products
        selectedPanel = new JPanel();
        selectedPanel.setLayout(new BoxLayout(selectedPanel, BoxLayout.Y_AXIS));
        selectedPanel.setBackground(Color.WHITE);
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Selected Items"));

        JScrollPane selectedScroll = new JScrollPane(selectedPanel);
        selectedScroll.setPreferredSize(new Dimension(0, 120));
        bottomPanel.add(selectedScroll, BorderLayout.CENTER);

        // Total price label
        totalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        // ORDER and CANCEL buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsPanel.setBackground(new Color(0, 255, 255));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnOrder = new JButton("ORDER");
        btnOrder.setBackground(new Color(0, 180, 0));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setBackground(new Color(200, 0, 0));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 16));

        // ORDER button - shows payment options
        btnOrder.addActionListener(e -> {
            if (selectedProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add something to your order first.");
                return;
            }
            showPaymentDialog();
        });

        // CANCEL button - clears everything
        btnCancel.addActionListener(e -> {
            selectedProducts.clear();
            refreshSelectedPanel();
        });

        buttonsPanel.add(btnOrder);
        buttonsPanel.add(btnCancel);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Saves each selected product as a row in the orders table
    // Saves all selected products under the same order group id
private void saveOrderToDB() {
    try {
        // Get the next group id (max + 1 so each order gets a unique group)
        int groupId = 1;
        String maxQuery = "SELECT MAX(order_group_id) FROM orders";
        PreparedStatement maxStmt = connection.prepareStatement(maxQuery);
        ResultSet rs = maxStmt.executeQuery();
        if (rs.next() && rs.getObject(1) != null) {
            groupId = rs.getInt(1) + 1;
        }

        // Save every product under the same group id
        String query = "INSERT INTO orders (user_id, product_name, order_date, status, order_group_id) VALUES (?, ?, NOW(), 0, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);

        for (Product p : selectedProducts) {
            stmt.setInt(1, currentUser.getId());
            stmt.setString(2, p.getName());
            stmt.setInt(3, groupId);
            stmt.executeUpdate();
        }

        System.out.println("Order saved with group id: " + groupId);

    } catch (SQLException e) {
        System.out.println("Error saving order: " + e.getMessage());
    }
}

    // Shows the payment choice: Cash or Card
    private void showPaymentDialog() {
        JDialog paymentDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Payment Method", true);
        paymentDialog.setSize(350, 200);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new BorderLayout(10, 10));

        JLabel label = new JLabel("Choose your payment method:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        paymentDialog.add(label, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnCash = new JButton("CASH");
        btnCash.setBackground(new Color(0, 150, 0));
        btnCash.setForeground(Color.WHITE);
        btnCash.setFont(new Font("Arial", Font.BOLD, 14));

        JButton btnCard = new JButton("CARD");
        btnCard.setBackground(new Color(0, 100, 200));
        btnCard.setForeground(Color.WHITE);
        btnCard.setFont(new Font("Arial", Font.BOLD, 14));

        // Cash - saves order and confirms directly
        btnCash.addActionListener(e -> {
            paymentDialog.dispose();
            saveOrderToDB();
            showOrderConfirmed();
        });

        // Card - opens the card details form
        btnCard.addActionListener(e -> {
            paymentDialog.dispose();
            showCardDialog();
        });

        btnPanel.add(btnCash);
        btnPanel.add(btnCard);
        paymentDialog.add(btnPanel, BorderLayout.SOUTH);

        paymentDialog.setVisible(true);
    }

    // Shows the card details form
    private void showCardDialog() {
        JDialog cardDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Card Payment", true);
        cardDialog.setSize(400, 320);
        cardDialog.setLocationRelativeTo(this);
        cardDialog.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Enter Card Details", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cardDialog.add(title, BorderLayout.NORTH);

        // Form panel with all 4 fields
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JTextField nameField = new JTextField();
        JTextField cardField = new JTextField();
        JTextField ccvField  = new JTextField();
        JTextField dateField = new JTextField();

        nameField.setToolTipText("e.g. John Smith");
        cardField.setToolTipText("e.g. 1234 5678 9012 3456");
        ccvField.setToolTipText("e.g. 123");
        dateField.setToolTipText("e.g. 12/27");

        formPanel.add(new JLabel("Name on Card:"));  formPanel.add(nameField);
        formPanel.add(new JLabel("Card Number:"));   formPanel.add(cardField);
        formPanel.add(new JLabel("CCV:"));           formPanel.add(ccvField);
        formPanel.add(new JLabel("Expiration Date:")); formPanel.add(dateField);

        cardDialog.add(formPanel, BorderLayout.CENTER);

        // Confirm button - saves order then confirms
        JButton btnConfirm = new JButton("CONFIRM");
        btnConfirm.setBackground(new Color(0, 150, 0));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));

        btnConfirm.addActionListener(e -> {
            // Make sure all fields are filled before confirming
            if (nameField.getText().isEmpty() || cardField.getText().isEmpty()
                    || ccvField.getText().isEmpty() || dateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(cardDialog, "Please fill in all fields.");
                return;
            }
            cardDialog.dispose();
            saveOrderToDB(); // save to DB before confirming
            showOrderConfirmed();
        });

        JPanel confirmPanel = new JPanel();
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        confirmPanel.add(btnConfirm);
        cardDialog.add(confirmPanel, BorderLayout.SOUTH);

        cardDialog.setVisible(true);
    }

    // Shows the final confirmation message and clears the order
    private void showOrderConfirmed() {
        selectedProducts.clear();
        refreshSelectedPanel();

        JDialog confirmedDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Order Confirmed", true);
        confirmedDialog.setSize(400, 200);
        confirmedDialog.setLocationRelativeTo(this);
        confirmedDialog.setLayout(new BorderLayout());

        JLabel message = new JLabel("<html><div style='text-align:center'>✓ Your order is confirmed<br>and is on its way!</div></html>", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 18));
        message.setForeground(new Color(0, 150, 0));
        confirmedDialog.add(message, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> confirmedDialog.dispose());
        JPanel closePanel = new JPanel();
        closePanel.add(btnClose);
        confirmedDialog.add(closePanel, BorderLayout.SOUTH);

        confirmedDialog.setVisible(true);
    }

    // Rebuilds the selected items panel and updates the total
    private void refreshSelectedPanel() {
        selectedPanel.removeAll();
        double total = 0;

        for (Product p : selectedProducts) {
            JLabel item = new JLabel("  - " + p.getName() + "  $" + p.getPrice());
            item.setFont(new Font("Arial", Font.PLAIN, 13));
            selectedPanel.add(item);
            total += p.getPrice();
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    // Builds a small visual card showing the product image, name and price
    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        String fullPath = p.getImagePath();
        java.io.File imageFile = new java.io.File(fullPath);

        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(fullPath);
            Image scaled = icon.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaled));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(imageLabel, BorderLayout.NORTH);
        } else {
            JLabel noImage = new JLabel("No image", SwingConstants.CENTER);
            noImage.setForeground(Color.GRAY);
            card.add(noImage, BorderLayout.NORTH);
            System.out.println("Image not found: " + imageFile.getAbsolutePath());
        }

        JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // When clicked, adds the product to the selected list
        JButton btnAdd = new JButton("Add - $" + p.getPrice());
        btnAdd.addActionListener(e -> {
            selectedProducts.add(p);
            refreshSelectedPanel();
        });

        card.add(nameLabel, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    // Runs a SELECT and returns every product from the database
    private ArrayList<Product> getMenuDataFromDB(Connection connection) {
        ArrayList<Product> list = new ArrayList<>();
        String query = "SELECT id, name, price, image FROM products";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int    id        = rs.getInt("id");
                String name      = rs.getString("name");
                double price     = rs.getDouble("price");
                String imagePath = rs.getString("image");

                list.add(new Product(id, name, price, imagePath));
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading menu from database.");
        }

        return list;
    }

    // Entry point - uses data_base.java to get the connection
    public static void main(String[] args) {

        Connection myConnection = data_base.getConnection();

        if (myConnection == null) {
            JOptionPane.showMessageDialog(null, "Could not connect to the database. Exiting.");
            return;
        }

        JFrame frame = new JFrame("Apache Fast Food POS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Pass a test user for standalone testing
        user testUser = new user();
        frame.add(new apacheMenu(myConnection, testUser));
        frame.setVisible(true);
    }
}

// Simple container to hold one product's data
class Product {
    private int id;
    private String name;
    private double price;
    private String imagePath;

    public Product(int id, String name, double price, String imagePath) {
        this.id        = id;
        this.name      = name;
        this.price     = price;
        this.imagePath = imagePath;
    }

    public int    getId()        { return id;        }
    public String getName()      { return name;      }
    public double getPrice()     { return price;     }
    public String getImagePath() { return imagePath; }
}