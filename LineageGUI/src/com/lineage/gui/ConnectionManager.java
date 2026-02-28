package com.lineage.gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ConnectionManager {
    private Connection connection;
    private String databaseName;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean connected = false;
    
    public boolean connectToDatabase(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = database;
        this.user = user;
        this.password = password;
        
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database 
                + "?useUnicode=true&characterEncoding=utf-8"
                + "&useSSL=false"
                + "&serverTimezone=UTC"
                + "&allowPublicKeyRetrieval=true";
        
        try {
            // Load JDBC driver (optional in Java 8+, but useful for debug)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            connection = DriverManager.getConnection(url, user, password);
            connected = true;
            System.out.println("✅ Connected to database: " + database);
            return true;
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Execute SELECT queries
    public ResultSet executeQuery(String query) {
        if (!connected) return null;
        
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("❌ Query error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Execute UPDATE, INSERT, DELETE commands
    public int executeUpdate(String query) {
        if (!connected) return -1;
        
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("❌ Update error: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    // List all tables in the database
    public List<String> getTables() {
        List<String> tables = new ArrayList<>();
        if (!connected) return tables;
        
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(databaseName, null, "%", new String[]{"TABLE"});
            
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tables;
    }
    
    // Execute game commands (via database)
    public boolean executeGameCommand(String command) {
        // Example: insert command into server command table
        // Each server has its own way of processing commands
        
        if (command.startsWith("//")) {
            // Typical GM command
            String insertQuery = "INSERT INTO command_records (command, executed_by, execution_time) VALUES ("
                    + "'" + command + "', 'GUI', NOW())";
            return executeUpdate(insertQuery) > 0;
        }
        
        return false;
    }
    
    // Get player information
    public ResultSet getPlayerInfo(String playerName) {
        String query = "SELECT * FROM characters WHERE char_name = '" + playerName + "'";
        return executeQuery(query);
    }
    
    // Create item for player (example - adapt according to DB structure)
    public boolean createItemForPlayer(String playerName, int itemId, long count) {
        // First get player_id
        try {
            ResultSet rs = getPlayerInfo(playerName);
            if (rs.next()) {
                int playerId = rs.getInt("charId"); // or obj_Id, depending on structure
                rs.close();
                
                // Insert item into inventory
                String insertQuery = "INSERT INTO items (owner_id, item_id, count, enchant_level, loc) VALUES ("
                        + playerId + ", " + itemId + ", " + count + ", 0, 'INVENTORY')";
                return executeUpdate(insertQuery) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connected = false;
                System.out.println("✅ Disconnected from database");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public String getDatabaseName() {
        return databaseName;
    }
}