import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;


public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel panel;
    
    public void initialize(user user)
    {



        
        cardLayout = new CardLayout();
        panel = new JPanel (cardLayout);

        Connection connection = data_base.getConnection();


        userInfo userInfoPanel = new userInfo(user);
        apacheMenu apacheMenuPanel = new apacheMenu(connection, user);
        orders ordersPanel = new orders(connection, user);

        panel.add(userInfoPanel, "USER INFO");
        panel.add(apacheMenuPanel, "APACHE MENU");
        panel.add(ordersPanel, "ORDERS MENU");



        JButton btnUserInfo = new JButton("User Info");
        btnUserInfo.setBackground(Color.WHITE);


        JButton btnApacheMenu = new JButton("Menu");
        btnApacheMenu.setBackground(Color.WHITE);
        btnApacheMenu.setPreferredSize(new Dimension(20, 50));

        JButton btnOrders = new JButton("Orders");
        btnOrders.setBackground(Color.WHITE);

        JButton btnExit = new JButton("Exit");
        btnExit.setBackground(Color.WHITE);


        btnExit.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e)
        {

            dispose();

        }
        
        });



        btnApacheMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
        
                cardLayout.show(panel, "APACHE MENU");

            }
    
        });

        btnOrders.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {

                cardLayout.show(panel, "ORDERS MENU");

            }



        });

        btnUserInfo.addActionListener(new ActionListener(){


            public void actionPerformed(ActionEvent e)
            {

                cardLayout.show(panel, "USER INFO");

            }



        });
    
        

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
        buttonsPanel.add(btnUserInfo);
        buttonsPanel.add(btnOrders);
        buttonsPanel.add(btnApacheMenu);
        buttonsPanel.add(btnExit);
        
        

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.NORTH);


        
        

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1100,650);
        setLocationRelativeTo(null);
        setVisible(true);

      
        


       

    }

    public static void main(String[] args) {
        
        MainFrame example;

        user ismael;

        ismael = new user();

        example = new MainFrame();

        example.initialize(ismael);


    }


}
