import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StockSlayer {
    private JFrame frame;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton; // new registration
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel infoPanel;
    private JPanel logoPanel; // logo panel
    private JLabel nameLabel;
    private JTextField stockNameField;
    private JButton searchButton;
    private JLabel infoLabel; // stock info label
    private DefaultTableModel heldStocksTableModel;
    private JTable heldStocksTable;
    private JPanel buttonsPanel;
    private JButton buyButton;
    private JButton sellButton;
    private JButton sortSharesButton; // sort by shares
    private JButton sortHighestValueButton; // sort by highest value
    private JButton sortLowestValueButton; // sort by lowest value
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
                frame.getContentPane().removeAll(); // remove login panel ***not working for some reason, try .dispose()?
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
            //  registration logic/open a registration window
            JOptionPane.showMessageDialog(frame, "Registration not yet implemented");
        }
    });

    frame.setVisible(true);
}

    
    

    private boolean isValidLogin(String username, char[] password) {
        // replace with login validation logic
        // remove return true; below once logic is added of course
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
        JLabel logoLabel = new JLabel(new ImageIcon("stock-slayer-logo.png")); // logo image path
        logoLabel.setPreferredSize(new Dimension(350, 110)); // set logo size
        logoPanel.add(logoLabel);

        nameLabel = new JLabel("Enter Stock Symbol:");
        stockNameField = new JTextField(10);
        searchButton = new JButton("Search");
        infoLabel = new JLabel(); // empty label to display stock info after search
        heldStocks = new ArrayList<>();

        heldStocksTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // makes table cells non-editable
            }
        };
        heldStocksTableModel.addColumn("Stock");
        heldStocksTableModel.addColumn("Stock Price");
        heldStocksTableModel.addColumn("Shares Owned");
        heldStocksTableModel.addColumn("Total Value");

        heldStocksTable = new JTable(heldStocksTableModel);
        heldStocksTable.setRowHeight(20); // adjust row height if needed
        heldStocksTable.getTableHeader().setReorderingAllowed(false); // disable column reordering

        // column widths
        int columnCount = heldStocksTableModel.getColumnCount();
        int[] columnWidths = {200, 100, 100, 100}; // adjust column widths if needed
        for (int i = 0; i < columnCount; i++) {
            TableColumn column = heldStocksTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        // center table contents
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        heldStocksTable.setDefaultRenderer(Object.class, centerRenderer);

        // custom fonts and styles
        Font customFont = new Font("Segoe UI", Font.PLAIN, 12);
        nameLabel.setFont(customFont);
        stockNameField.setFont(customFont);
        searchButton.setFont(customFont);
        infoLabel.setFont(customFont);

        // buttons panel
        buttonsPanel = new JPanel(new FlowLayout());
        buyButton = createStyledButton("Buy");
        sellButton = createStyledButton("Sell");
        buttonsPanel.add(buyButton);
        buttonsPanel.add(sellButton);

        // placeholder fake stocks for prototyping
        heldStocks.add(new Stock("AAPL", "Apple Inc.", 189.0, 100));
        heldStocks.add(new Stock("GOOGL", "Alphabet Inc.", 135.66, 70));
        heldStocks.add(new Stock("MSFT", "Microsoft Corporation", 328.33, 85));

        displayHeldStocks(); // self-explanatory

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String stockSymbol = stockNameField.getText();

                // open the stock info window
                openStockInfoWindow(stockSymbol, 0.0); // replace 0.0 with the actual stock price from API
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

        sortSharesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // sort by shares owned
                heldStocks.sort(Comparator.comparingInt(stock -> -stock.shares));
                displayHeldStocks();
            }
        });

        sortHighestValueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // sort by highest value
                heldStocks.sort(Comparator.comparingDouble(stock -> -stock.price));
                displayHeldStocks();
            }
        });

        sortLowestValueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // sort by lowest value
                heldStocks.sort(Comparator.comparingDouble(stock -> stock.price));
                displayHeldStocks();
            }
        });

        // sorting buttons panel
        JPanel sortingButtonsPanel = new JPanel(new FlowLayout());
        sortingButtonsPanel.add(sortSharesButton);
        sortingButtonsPanel.add(sortHighestValueButton);
        sortingButtonsPanel.add(sortLowestValueButton);

        // currently held stocks panel
        JPanel heldStocksPanel = new JPanel(new BorderLayout());
        heldStocksPanel.add(sortingButtonsPanel, BorderLayout.NORTH); // sorting buttons
        heldStocksPanel.add(new JScrollPane(heldStocksTable), BorderLayout.CENTER);
        heldStocksPanel.add(buttonsPanel, BorderLayout.SOUTH); // buy and sell buttons

        topPanel.add(logoPanel); // logo panel
        topPanel.add(nameLabel);
        topPanel.add(stockNameField);
        topPanel.add(searchButton);

        infoPanel.add(infoLabel);

        bottomPanel.add(infoPanel, BorderLayout.NORTH); // info panel
        bottomPanel.add(heldStocksPanel, BorderLayout.CENTER); // held stocks panel

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private class Stock {
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
        heldStocksTableModel.setRowCount(0); // clears table
        DecimalFormat df = new DecimalFormat("#.00"); // format for two decimal places
        for (Stock stock : heldStocks) {
            double totalValue = stock.price * stock.shares; // calculate total value
            Object[] rowData = {
                    stock.symbol + " (" + stock.name + ")",
                    stock.price,
                    stock.shares,
                    df.format(totalValue) // format total value
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
        infoPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns with spacing

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
                // buying logic here
                int sharesToBuy = Integer.parseInt(sharesField.getText());
                // purchase processing logic here
                JOptionPane.showMessageDialog(frame, "Bought " + sharesToBuy + " shares of " + stockName);
                infoFrame.dispose();
            }
        });

        infoFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StockSlayer();
            }
        });
    }
}
