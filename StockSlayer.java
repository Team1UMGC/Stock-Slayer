package com.groupone.stockslayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class StockSlayer {
    private JFrame frame;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel infoPanel;
    private JPanel logoPanel;
    private JLabel nameLabel;
    private JTextField stockNameField;
    private JButton searchButton;
    private JLabel infoLabel;
    private DefaultTableModel heldStocksTableModel;
    private JTable heldStocksTable;
    private JPanel buttonsPanel;
    private JButton buyButton;
    private JButton sellButton;
    private JButton sortSharesButton;
    private JButton sortHighestValueButton;
    private JButton sortLowestValueButton;
    private List<Stock> heldStocks;

    public StockSlayer() {
        initializeLogin();
    }

    private void initializeLogin() {
        frame = new JFrame("Stock Slayer - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel logoLabel = new JLabel(new ImageIcon("stock-slayer-logo-login.png"));
        logoLabel.setPreferredSize(new Dimension(175, 100));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(logoLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginPanel.add(registerButton, gbc);

        frame.add(loginPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check the login credentials
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String enteredPassword = new String(password); // convert password to a string

                if ("test".equals(username) && "password".equals(enteredPassword)) {
                    frame.getContentPane().removeAll();
                    initializeMainApplication();
                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid login credentials.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Registration not yet implemented");
            }
        });

        frame.setVisible(true);
    }

    private boolean isValidLogin(String username, char[] password) {
        return true;
    }

    private void initializeMainApplication() {
        frame = new JFrame("Stock Slayer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        logoPanel = new JPanel();
        JLabel logoLabel = new JLabel(new ImageIcon("stock-slayer-logo.png"));
        logoLabel.setPreferredSize(new Dimension(350, 110));
        logoPanel.add(logoLabel);

        nameLabel = new JLabel("Enter Stock Symbol:");
        stockNameField = new JTextField(10);
        searchButton = new JButton("Search");
        infoLabel = new JLabel();
        heldStocks = new ArrayList<>();

        heldStocksTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        heldStocksTableModel.addColumn("Stock");
        heldStocksTableModel.addColumn("Stock Price");
        heldStocksTableModel.addColumn("Shares Owned");
        heldStocksTableModel.addColumn("Total Value");

        heldStocksTable = new JTable(heldStocksTableModel);
        heldStocksTable.setRowHeight(20);
        heldStocksTable.getTableHeader().setReorderingAllowed(false);

        int columnCount = heldStocksTableModel.getColumnCount();
        int[] columnWidths = {200, 100, 100, 100};
        for (int i = 0; i < columnCount; i++) {
            TableColumn column = heldStocksTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        heldStocksTable.setDefaultRenderer(Object.class, centerRenderer);

        Font customFont = new Font("Segoe UI", Font.PLAIN, 12);
        nameLabel.setFont(customFont);
        stockNameField.setFont(customFont);
        searchButton.setFont(customFont);
        infoLabel.setFont(customFont);

        buttonsPanel = new JPanel(new FlowLayout());
        buyButton = createStyledButton("Buy");
        sellButton = createStyledButton("Sell");
        buttonsPanel.add(buyButton);
        buttonsPanel.add(sellButton);

        heldStocks.add(new Stock("AAPL", "Apple Inc.", 189.0, 100));
        heldStocks.add(new Stock("GOOGL", "Alphabet Inc.", 135.66, 70));
        heldStocks.add(new Stock("MSFT", "Microsoft Corporation", 328.33, 85));

        displayHeldStocks();

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String stockSymbol = stockNameField.getText();
                openStockInfoWindow(stockSymbol, 0.0);
            }
        });

        buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = heldStocksTable.getSelectedRow();
                if (rowIndex >= 0) {
                    int sharesToBuy = promptForShares("buy");
                    if (sharesToBuy > 0) {
                        // add share buying logic here
                    }
                }
            }
        });

        sellButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = heldStocksTable.getSelectedRow();
                if (rowIndex >= 0) {
                    int sharesToSell = promptForShares("sell");
                    if (sharesToSell > 0) {
                        // add share selling logic here
                    }
                }
            }
        });

        sortSharesButton = createStyledSmallButton("Sort by Shares");
        sortHighestValueButton = createStyledSmallButton("Sort by Highest Value");
        sortLowestValueButton = createStyledSmallButton("Sort by Lowest Value");

        // Rest of the initializeMainApplication method...
    }

    // Rest of the StockSlayer class...

    static class Stock {
        private String symbol;
        private String name;
        private double price;
        private int shares; // no. of shares

        public Stock(String symbol, String name, double price, int shares) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
            this.shares = shares;
        }
    }

    private void displayHeldStocks() {
        heldStocksTableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#.00");
        for (Stock stock : heldStocks) {
            double totalValue = stock.price * stock.shares;
            Object[] rowData = {
                    stock.symbol + " (" + stock.name + ")",
                    stock.price,
                    stock.shares,
                    df.format(totalValue)
            };
            heldStocksTableModel.addRow(rowData);
        }
    }

    private int promptForShares(String action) {
        String input = JOptionPane.showInputDialog(frame, "Enter the number of shares to " + action + ":", action, JOptionPane.PLAIN_MESSAGE);
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // invalid input
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }

    private JButton createStyledSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        return button;
    }

    private void openStockInfoWindow(String stockName, double stockPrice) {
        JFrame infoFrame = new JFrame("Stock Info: " + stockName);
        infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        infoFrame.setSize(300, 150);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel nameLabel = new JLabel("Stock Name:");
        JLabel priceLabel = new JLabel("Stock Price:");
        JLabel sharesLabel = new JLabel("Number of Shares to Buy:");

        JTextField nameField = new JTextField(stockName);
        JTextField priceField = new JTextField(String.valueOf(stockPrice));
        JTextField sharesField = new JTextField();

        JButton buyButton = new JButton("Buy");

        infoPanel.add(nameLabel);
        infoPanel.add(nameField);
        infoPanel.add(priceLabel);
        infoPanel.add(priceField);
        infoPanel.add(sharesLabel);
        infoPanel.add(sharesField);

        infoFrame.add(infoPanel, BorderLayout.CENTER);
        infoFrame.add(buyButton, BorderLayout.SOUTH);

        buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int sharesToBuy = Integer.parseInt(sharesField.getText());
                JOptionPane.showMessageDialog(frame, "Bought " + sharesToBuy + " shares of " + stockName);
                infoFrame.dispose();
            }
        });

        infoFrame.setVisible(true);
    }
}
