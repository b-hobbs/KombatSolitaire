package controller;

//import java.util.ArrayList;

import model.Player;
import model.Table;
import model.TableStatus;

public class TableManager {

    public static TableManager instance = null;
    
    // 1-indexed array of tables. tables[0] should be null
    Table[] tables;
    
    /**
     * Default constructor empty; singleton class
     */
    TableManager() { }
    
    /**
     * Create the singleton instance of the class
     * @return
     */
    public static TableManager instance() {
        if (instance==null) {
            instance = new TableManager();
            instance.init();
        }
        return instance;
    }
    
    /**
     * Perform table initialization steps:
     * Create each table and add it to the list
     * Table ID numbers correspond to array indices
     */
    public void init() {
        tables = new Table[101];
        // Initialize 100 fresh tables
        // Prof. H: Skip index 0 because table numbers are 1-100
        tables[0] = null;
        for (int i=1; i<=100; i++) {
            Table newTable = new Table(i);
            tables[i] = newTable;
        }
    }
    
    /**
     * Tells whether the given table is empty (no players) or not
     * @param tableID
     * @return boolean evaluation of whether status == empty
     */
    public boolean isTableEmpty(int tableID) {
        return (tables[tableID].getTableStatus() == TableStatus.EMPTY);
    }
    
    /**
     * Tells whether the given table has started a game (is "in play")
     * @param tableID
     * @return boolean evaluation of whether status == inplay
     */
    public boolean isTableStarted(int tableID) {
        return (tables[tableID].getTableStatus() == TableStatus.INPLAY);
    }
    
    /**
     * Overwrite the old table with the updated one
     * @param tableID
     * @param table
     */
    public void overwriteTable(int tableID, Table table){
        tables[tableID] = table;
    }
    
    /**
     * Sets the table status of a certain table
     * @param tableID ID number of table to change
     * @param ts TableStatus enumeration value
     */
    public void setTableStatus(int tableID, TableStatus ts){
        tables[tableID].setTableStatus(ts);
    }
    
    /**
     * Adds a player to a certain table
     * @param tableID
     * @param player
     */
    public void addPlayer(int tableID, Player player){
        tables[tableID].addPlayer(player);
    }
    
    /**
     * Gets a table from the list of tables by ID number (1-100)
     * @param tableID
     * @return requested table
     */
    public Table getTable(int tableID) {
        return tables[tableID];
    }
    
}
