
package com.lineage.gui;

@SuppressWarnings("unused")
public class CommandExecutor {
    private ConnectionManager connection;
    
    public CommandExecutor(ConnectionManager connection) {
        this.connection = connection;
    }
    
    // Comandos GM básicos
    public boolean createItem(int playerId, int itemId, long count) {
        return connection.executeGameCommand("//createitem " + playerId + " " + itemId + " " + count);
    }
    
    public boolean createItem(String playerName, int itemId, long count) {
        return connection.executeGameCommand("//createitem \"" + playerName + "\" " + itemId + " " + count);
    }
    
    public boolean teleportTo(int playerId, int x, int y, int z) {
        return connection.executeGameCommand("//teleportto " + playerId + " " + x + " " + y + " " + z);
    }
    
    public boolean teleportTo(String playerName, int x, int y, int z) {
        return connection.executeGameCommand("//teleportto \"" + playerName + "\" " + x + " " + y + " " + z);
    }
    
    public boolean teleportPlayer(String playerName, String targetName) {
        return connection.executeGameCommand("//teleportto \"" + playerName + "\" \"" + targetName + "\"");
    }
    
    public boolean announce(String message) {
        return connection.executeGameCommand("//announce \"" + message + "\"");
    }
    
    public boolean kickPlayer(String playerName) {
        return connection.executeGameCommand("//kick " + playerName);
    }
    
    public boolean banPlayer(String playerName) {
        return connection.executeGameCommand("//ban " + playerName);
    }
    
    public boolean giveLevel(String playerName, int level) {
        return connection.executeGameCommand("//level \"" + playerName + "\" " + level);
    }
    
    public boolean setPvP(String playerName, int count) {
        return connection.executeGameCommand("//setpvp \"" + playerName + "\" " + count);
    }
    
    public boolean setPK(String playerName, int count) {
        return connection.executeGameCommand("//setpk \"" + playerName + "\" " + count);
    }
    
    public boolean healPlayer(String playerName) {
        return connection.executeGameCommand("//heal \"" + playerName + "\"");
    }
    
    public boolean ressurectPlayer(String playerName) {
        return connection.executeGameCommand("//res \"" + playerName + "\"");
    }
    
    public boolean spawnNpc(int npcId, int x, int y, int z) {
        return connection.executeGameCommand("//spawn " + npcId + " " + x + " " + y + " " + z);
    }
    
    public boolean openOfflineShop(String playerName) {
        return connection.executeGameCommand("//openofflineshop \"" + playerName + "\"");
    }
    
    public boolean customCommand(String command) {
        return connection.executeGameCommand(command);
    }
}