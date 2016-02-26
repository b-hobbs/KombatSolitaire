package controller;
import java.util.HashMap;
import java.util.LinkedList;

import org.w3c.dom.NodeList;

import model.Player;

/**
 * Manages all of the connected players
 * Singleton
 * @author bhobbs
 *
 */
public class UserManager {
    /** Singleton instance. */
    static UserManager inst = null;
    
    //Hashmap of logged in players with <playerID, player object>
    protected HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    
    //list of player-ids that the user wants to send chat to
    private LinkedList<Integer> privateChatScope = new LinkedList<Integer>();
    
    /** Lock down the constructor. */
    UserManager() {}
    
    /** Return (or create) the singleton instance. */
    public static UserManager instance() {
        if (inst == null) {
            inst = new UserManager();
        }
        
        return inst;
    }
    
    /**
     * Add a player to the list of players connected to the lobby
     * @param player
     */
    public void addPlayer(Player player){
        players.put(player.getId(), player);
    }
    
    /**
     * Remove a player from the list of players connected to the lobby via an id
     * @param id
     */
    public void removePlayer(int id){
        players.remove(id);
    }
    
    /**
     * Remove a player from the list of players conntected to the lobby via a Player object
     * @param player
     */
    public void removePlayer(Player player){
        players.remove(player.getId());
    }
    
    /**
     * Get a player object by their id
     * @param id
     * @return Player
     */
    public Player getPlayer(int id){
        return players.get(id);
    }

    /**
     * Number of players logged in
     * @return
     *      Number of connected users
     */
    public int getNumberOfPlayers(){
        return players.size();
    }
    
    /**
     * Data to be drawn to UserManagerGUI
     * This constructs an array of data to be displayed to the UserManagerGUI
     * @return
     */
    public Object[][] getUserManagerData() {
        Object[][] data = new Object[getNumberOfPlayers()][5];
        int i = 0;
        for(Player player : players.values()){
            data[i][0] = player.getId();
            data[i][1] = player.getName();
            data[i][2] = player.getTable() > 0 ? player.getTable() : "-";
            data[i][3] = getSelectedPrivateChat(player.getId());
            i++;
        }
        return data;
    }

    /**
     * Add or remove a player from the privateChatScope.
     * @param id
     *      id of the player
     * @param value
     *      true - add player
     *      false - remove player
     */
    public void changePrivateChatScope(int id, boolean value){
        if(value == true)
            privateChatScope.add(id);
        else if(value == false)
            privateChatScope.removeFirstOccurrence(id);
    }
    
    /**
     * Get list of players the user wants to privately message. If the list is empty then
     * the message must be public.
     * @return
     */
    public LinkedList<Integer> getPrivateChatScope(){
        return privateChatScope;
    }
    
    /**
     * See if the privateChatScope is empty. Tells us if the user has selected users
     * that they want to message or if they have no users selected and want to send a public
     * message instead.
     * @return
     */
    public boolean hasPrivateChatScope(){
        return !(privateChatScope.isEmpty());
    }
    
    /**
     * Get selected private chat scope to set checkboxes values.
     * @param id
     * @return
     */
    protected Boolean getSelectedPrivateChat(int id) {
        if(privateChatScope.contains(id))
            return true;
        
        return false;
    }
    
    /**
     * Clears the private chat scope, chat will now be public
     */
    public void resetPrivateChatScope(){
        privateChatScope.clear();
    }
    
    /**
     * Change the privateChatScope to ONLY the player provided
     * @param id
     *      player id
     */
    public void privateChatTo(int id){
        resetPrivateChatScope();
        privateChatScope.add(id);
    }
    
    /**
     * Remove any players from this table in the user manager
     * @param tableId
     */
    public void setTableEmpty(int tableId){
        for(Player player: players.values()){
            if(player.getTable() == tableId)
                player.resetTable();
        }
    }
    
    /**
     * Update the user manager table column
     * @param tableID
     *      table id
     * @param playerIds
     *      LinkedList of playerIDs that are on the table
     */
    public void updateTableColumn(int tableId, LinkedList<Integer> playerIds){
        for(Player player: players.values()){
            if(playerIds.contains(player.getId()))
                player.setTable(tableId);
            else if(player.getTable() == tableId)
                player.resetTable();
        }
    }
}
