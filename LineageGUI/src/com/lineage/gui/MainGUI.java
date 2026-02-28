package com.lineage.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List; 

@SuppressWarnings("unused")
public class MainGUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private ConnectionManager connectionManager;
    private CommandExecutor executor;
 
    // Connection components
    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtUser;
    private JTextField txtDbName; 
    private JPasswordField txtPass;
    private JButton btnConnect;
    private JLabel lblStatus;
    
    // Command components
    private JTextField txtPlayerName;
    private JComboBox<String> cmbCommandType;
    private JTextField txtItemId;
    private JTextField txtItemCount;
    private JTextField txtMessage;
    private JTextArea txtLog;
    
    public MainGUI() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        connectionManager = new ConnectionManager();
        executor = new CommandExecutor(connectionManager);
       
        initComponents();
        setupListeners();
    }
    
    private void initComponents() {
        setTitle("Lineage-GUI - Server Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem clearLogItem = new JMenuItem("Clear Log");
        clearLogItem.addActionListener(e -> txtLog.setText(""));
        toolsMenu.add(clearLogItem);
        menuBar.add(toolsMenu);
        
        setJMenuBar(menuBar);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ===== CONNECTION PANEL (NORTH) =====
        JPanel connectionPanel = new JPanel(new GridBagLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "🔌 Database Connection",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Host
        gbc.gridx = 0; gbc.gridy = row;
        connectionPanel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        txtHost = new JTextField("localhost", 10);
        connectionPanel.add(txtHost, gbc);
        
        gbc.gridx = 2;
        connectionPanel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 3;
        txtPort = new JTextField("3306", 5);
        connectionPanel.add(txtPort, gbc);
        
        row++;
        // Database Name
        gbc.gridx = 0; gbc.gridy = row;
        connectionPanel.add(new JLabel("Database:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDbName = new JTextField("l2jmobiush5", 15);
        connectionPanel.add(txtDbName, gbc);
        gbc.gridwidth = 1;

        row++;
        // User
        gbc.gridx = 0; gbc.gridy = row;
        connectionPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUser = new JTextField("root", 10);
        connectionPanel.add(txtUser, gbc);        
        
        gbc.gridx = 2;
        connectionPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3;
        txtPass = new JPasswordField(8);
        connectionPanel.add(txtPass, gbc);
        
        row++;
        // Connect button
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        btnConnect = new JButton("Connect to Database");
        btnConnect.setBackground(new Color(0, 120, 215));
        btnConnect.setForeground(Color.WHITE);
        connectionPanel.add(btnConnect, gbc);
        
        row++;
        // Status
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        lblStatus = new JLabel("⛔ Disconnected");
        lblStatus.setForeground(Color.RED);
        connectionPanel.add(lblStatus, gbc);
        
        mainPanel.add(connectionPanel, BorderLayout.NORTH);
        
        // ===== LOG PANEL (CENTER) =====
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("📋 Activity Log"));
        
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtLog);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        // ===== COMMAND PANEL (SOUTH) =====
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commandPanel.setBorder(BorderFactory.createTitledBorder("🎮 Quick Command"));
        
        // Command type combobox
        String[] commandTypes = {"Create Item", "Teleport", "Announce", "Heal", "Resurrect", 
                                 "Give Level", "Set PvP", "Set PK", "Kick", "Ban", "Custom Command"};
        cmbCommandType = new JComboBox<>(commandTypes);
        
        // Command fields
        txtPlayerName = new JTextField(10);
        txtItemId = new JTextField(5);
        txtItemCount = new JTextField("1", 3);
        txtMessage = new JTextField(15);
        JButton btnExecute = new JButton("Execute");
        btnExecute.addActionListener(e -> executeCommand());
        
        // Add components to panel
        commandPanel.add(new JLabel("Type:"));
        commandPanel.add(cmbCommandType);
        commandPanel.add(new JLabel("Player:"));
        commandPanel.add(txtPlayerName);
        commandPanel.add(new JLabel("Item ID:"));
        commandPanel.add(txtItemId);
        commandPanel.add(new JLabel("Qty:"));
        commandPanel.add(txtItemCount);
        commandPanel.add(new JLabel("Message:"));
        commandPanel.add(txtMessage);
        commandPanel.add(btnExecute);
        
        mainPanel.add(commandPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupListeners() {
        // Connect button
        btnConnect.addActionListener(e -> connect());
        
        // ComboBox listener
        if (cmbCommandType != null) {
            cmbCommandType.addActionListener(e -> updateCommandUI());
        }
    }
    
    private void connect() {
        String host = txtHost.getText();
        int port = Integer.parseInt(txtPort.getText());
        String dbName = txtDbName.getText();
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        
        btnConnect.setEnabled(false);
        btnConnect.setText("Connecting...");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return connectionManager.connectToDatabase(host, port, dbName, user, pass);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        lblStatus.setText("✅ Connected to " + dbName);
                        log("Connection successful!");
                        
                        // Show available tables
                        List<String> tables = connectionManager.getTables();
                        log("Tables found: " + tables.size());
                        for (String table : tables) {
                            log("   - " + table);
                        }
                        
                    } else {
                        lblStatus.setText("⛔ Connection Failed");
                        log("Connection failed!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log("Error: " + e.getMessage());
                }
                btnConnect.setEnabled(true);
                btnConnect.setText("Connect");
            }
        };
        worker.execute();
    }
    
    private void executeCommand() {
        if (!connectionManager.isConnected()) {
            log("❌ Not connected to database");
            return;
        }
        
        String type = (String) cmbCommandType.getSelectedItem();
        String player = txtPlayerName.getText();
        
        if (player.isEmpty() && !"Announce".equals(type)) {
            log("❌ Player name is required");
            return;
        }
        
        switch (type) {
            case "Create Item":
                String itemId = txtItemId.getText();
                String count = txtItemCount.getText();
                if (!itemId.isEmpty() && !count.isEmpty()) {
                    try {
                        connectionManager.createItemForPlayer(player, Integer.parseInt(itemId), Long.parseLong(count));
                        log("✅ Created item " + itemId + " x" + count + " for " + player);
                    } catch (NumberFormatException ex) {
                        log("❌ Invalid item ID or count");
                    }
                }
                break;
                
            case "Announce":
                String msg = txtMessage.getText();
                if (!msg.isEmpty()) {
                    // TODO: Implement announce
                    log("📢 Announce: " + msg);
                }
                break;
                
            case "Custom Command":
                String cmd = txtMessage.getText();
                if (!cmd.isEmpty()) {
                    connectionManager.executeGameCommand(cmd);
                    log("✅ Command executed: " + cmd);
                }
                break;
                
            default:
                log("⏳ Command " + type + " for " + player + " (not implemented yet)");
        }
    }
    
    private void updateCommandUI() {
        if (cmbCommandType == null) return;
        
        String type = (String) cmbCommandType.getSelectedItem();
        
        // Enable/disable fields based on command type
        boolean isItemCommand = "Create Item".equals(type);
        txtItemId.setEnabled(isItemCommand);
        txtItemCount.setEnabled(isItemCommand);
        
        boolean isMessageCommand = "Announce".equals(type) || "Custom Command".equals(type);
        txtMessage.setEnabled(isMessageCommand);
        
        boolean isPlayerCommand = !"Announce".equals(type) && !"Custom Command".equals(type);
        txtPlayerName.setEnabled(isPlayerCommand);
    }
    
    private void log(String message) {
        if (txtLog != null) {
            txtLog.append("[" + java.time.LocalTime.now().toString().substring(0, 8) + "] " + message + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}