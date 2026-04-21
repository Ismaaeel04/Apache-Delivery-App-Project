import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class apacheMenu extends JPanel {

    private ArrayList<Product> selectedProducts = new ArrayList<>();
    private JLabel totalLabel;
    private JPanel selectedPanel;
    private Connection connection;
    private user currentUser;

    public apacheMenu(Connection connection, user currentUser) {
        this.connection  = connection;
        this.currentUser = currentUser;

        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("APACHE MENU", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel gridProductos = new JPanel(new GridLayout(0, 3, 15, 15));
        gridProductos.setBackground(Color.WHITE);
        gridProductos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ArrayList<Product> productList = getMenuDataFromDB(connection);

        for (int i = 0; i < productList.size(); i++) {
            gridProductos.add(createProductCard(productList.get(i)));
        }

        JScrollPane scroll = new JScrollPane(gridProductos);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        selectedPanel = new JPanel();
        selectedPanel.setLayout(new BoxLayout(selectedPanel, BoxLayout.Y_AXIS));
        selectedPanel.setBackground(Color.WHITE);
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Selected Items"));

        JScrollPane selectedScroll = new JScrollPane(selectedPanel);
        selectedScroll.setPreferredSize(new Dimension(0, 120));
        selectedScroll.setBorder(null);
        bottomPanel.add(selectedScroll, BorderLayout.CENTER);

        totalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Verdana", Font.BOLD, 15));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JButton btnOrder = new JButton("ORDER");
        btnOrder.setFont(new Font("Verdana", Font.BOLD, 15));
        btnOrder.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setFont(new Font("Verdana", Font.BOLD, 15));
        btnCancel.setBackground(Color.WHITE);

        btnOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedProducts.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please add something to your order first.");
                    return;
                }
                showPaymentDialog();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedProducts.clear();
                refreshSelectedPanel();
            }
        });

        buttonsPanel.add(btnOrder);
        buttonsPanel.add(btnCancel);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void saveOrderToDB() {
        try {
            int groupId = 1;
            String maxQuery = "SELECT MAX(order_group_id) FROM orders";
            PreparedStatement maxStmt = connection.prepareStatement(maxQuery);
            ResultSet rs = maxStmt.executeQuery();
            if (rs.next() && rs.getObject(1) != null) {
                groupId = rs.getInt(1) + 1;
            }

            String query = "INSERT INTO orders (user_id, product_name, order_date, status, order_group_id) VALUES (?, ?, NOW(), 0, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);

            for (int i = 0; i < selectedProducts.size(); i++) {
                stmt.setInt(1, currentUser.getId());
                stmt.setString(2, selectedProducts.get(i).getName());
                stmt.setInt(3, groupId);
                stmt.executeUpdate();
            }

            System.out.println("Order saved with group id: " + groupId);

        } catch (SQLException e) {
            System.out.println("Error saving order: " + e.getMessage());
        }
    }

    private void showPaymentDialog() {
        JDialog paymentDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Payment Method", true);
        paymentDialog.setSize(350, 200);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new BorderLayout(10, 10));

        JLabel label = new JLabel("Choose your payment method:", SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.BOLD, 14));
        paymentDialog.add(label, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnCash = new JButton("CASH");
        btnCash.setFont(new Font("Verdana", Font.BOLD, 14));
        btnCash.setBackground(Color.WHITE);

        JButton btnCard = new JButton("CARD");
        btnCard.setFont(new Font("Verdana", Font.BOLD, 14));
        btnCard.setBackground(Color.WHITE);

        btnCash.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paymentDialog.dispose();
                saveOrderToDB();
                showOrderConfirmed();
            }
        });

        btnCard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paymentDialog.dispose();
                showCardDialog();
            }
        });

        btnPanel.add(btnCash);
        btnPanel.add(btnCard);
        paymentDialog.add(btnPanel, BorderLayout.SOUTH);
        paymentDialog.setVisible(true);
    }

    private void showCardDialog() {
        JDialog cardDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Card Payment", true);
        cardDialog.setSize(400, 320);
        cardDialog.setLocationRelativeTo(this);
        cardDialog.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Enter Card Details", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cardDialog.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JTextField nameField = new JTextField();
        JTextField cardField = new JTextField();
        JTextField ccvField  = new JTextField();
        JTextField dateField = new JTextField();

        formPanel.add(new JLabel("Name on Card:"));    formPanel.add(nameField);
        formPanel.add(new JLabel("Card Number:"));     formPanel.add(cardField);
        formPanel.add(new JLabel("CCV:"));             formPanel.add(ccvField);
        formPanel.add(new JLabel("Expiration Date:")); formPanel.add(dateField);

        cardDialog.add(formPanel, BorderLayout.CENTER);

        JButton btnConfirm = new JButton("CONFIRM");
        btnConfirm.setFont(new Font("Verdana", Font.BOLD, 14));
        btnConfirm.setBackground(Color.WHITE);

        btnConfirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().isEmpty() || cardField.getText().isEmpty()
                        || ccvField.getText().isEmpty() || dateField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(cardDialog, "Please fill in all fields.");
                    return;
                }
                cardDialog.dispose();
                saveOrderToDB();
                showOrderConfirmed();
            }
        });

        JPanel confirmPanel = new JPanel();
        confirmPanel.add(btnConfirm);
        cardDialog.add(confirmPanel, BorderLayout.SOUTH);
        cardDialog.setVisible(true);
    }

    private void showOrderConfirmed() {
        selectedProducts.clear();
        refreshSelectedPanel();

        JDialog confirmedDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Order Confirmed", true);
        confirmedDialog.setSize(400, 200);
        confirmedDialog.setLocationRelativeTo(this);
        confirmedDialog.setLayout(new BorderLayout());

        JLabel message = new JLabel("<html><div style='text-align:center'>Your order is confirmed<br>and is on its way!</div></html>", SwingConstants.CENTER);
        message.setFont(new Font("Verdana", Font.BOLD, 16));
        message.setForeground(new Color(0, 150, 0));
        confirmedDialog.add(message, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.setBackground(Color.WHITE);

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmedDialog.dispose();
            }
        });

        JPanel closePanel = new JPanel();
        closePanel.add(btnClose);
        confirmedDialog.add(closePanel, BorderLayout.SOUTH);
        confirmedDialog.setVisible(true);
    }

    private void refreshSelectedPanel() {
        selectedPanel.removeAll();
        double total = 0;

        for (int i = 0; i < selectedProducts.size(); i++) {
            JLabel item = new JLabel("  - " + selectedProducts.get(i).getName() + "  $" + selectedProducts.get(i).getPrice());
            item.setFont(new Font("Verdana", Font.PLAIN, 13));
            selectedPanel.add(item);
            total = total + selectedProducts.get(i).getPrice();
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

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
        }

        JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Verdana", Font.PLAIN, 14));

        JButton btnAdd = new JButton("Add - $" + p.getPrice());
        btnAdd.setFont(new Font("Verdana", Font.PLAIN, 13));
        btnAdd.setBackground(Color.WHITE);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedProducts.add(p);
                refreshSelectedPanel();
            }
        });

        card.add(nameLabel, BorderLayout.CENTER);
        card.add(btnAdd,    BorderLayout.SOUTH);

        return card;
    }

    private ArrayList<Product> getMenuDataFromDB(Connection connection) {
        ArrayList<Product> list = new ArrayList<>();
        String query = "SELECT id, name, price, image FROM products";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int    id        = rs.getInt("id");
                String name      = rs.getString("name");
                double price     = rs.getDouble("price");
                String imagePath = rs.getString("image");
                list.add(new Product(id, name, price, imagePath));
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return list;
    }

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
        user testUser = new user();
        frame.add(new apacheMenu(myConnection, testUser));
        frame.setVisible(true);
    }
}

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