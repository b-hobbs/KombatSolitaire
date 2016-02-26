package model;

import java.util.HashMap;

/**
 * Player model
 * @author bhobbs
 *
 */
public class Player {
    protected int id;
    protected String name;
    
    //Hashmap of ratings by game <game name, rating value>
    protected HashMap<String, Integer> ratings;
    protected boolean isAdmin = false;
    
    protected final int NOT_ON_TABLE = -1;
    
    //table the user is currently on
    protected int currentTable = NOT_ON_TABLE;
    
    
    /**
     * Constructor for a player that only has an id
     * @param id
     */
    public Player(int id){
        this.id = id;
        name = "";
        ratings = new HashMap<String, Integer>();
    }
    
    /**
     * Constructor for a player that has an id and name
     * @param id
     * @param name
     *  name of the player
     */
    public Player(int id, String name){
        this.id = id;
        this.name = name;
        ratings = new HashMap<String, Integer>();
    }

    /**
     * Set a players name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add a rating to a player
     * @param type
     *  String of the game name
     * @param rating
     *  Integer value of the players rating
     */
    public void addRating(String type, int rating){
        ratings.put(type, rating);
    }

    /**
     * Set whether a player is an admin
     * @param isAdmin
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    /**
     * Set which table the player is on
     * @param tableID
     *  The ID of the table
     */
    public void setTable(int tableID){
        if(tableID < 1
                || tableID > 100)
            resetTable();
        else
            currentTable = tableID;
    }
    
    /**
     * Set a player as not being on a table
     */
    public void resetTable(){
        currentTable = NOT_ON_TABLE;
    }
    
    /**
     * Get the table of the player
     * @return
     *  Table id
     */
    public int getTable(){
        return currentTable;
    }
    
    /**
     * Get the player's id
     * @return
     *  Player id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get the player's name
     * @return
     *  Player's name
     */
    public String getName(){
        return name;
    }
    
    /**
     * Get the player's rating of a specific game
     * @param type
     *  Game name
     * @return
     *  Rating for the game
     */
    public int getRating(String type){
        return ratings.get(type);
    }
    
    /**
     * Create a string array of all the player's ratings
     * @return
     */
    public String[] ratingsToStringArray(){
        String[] r = new String[ratings.size()];
        String[] set = new String[ratings.size()];
        ratings.keySet().toArray(set);
        int i = 0;
        for(String s: set){
            r[i] = s + " : " + ratings.get(s);
            i++;
        }
        return r;
    }
    
    /**
     * Find out if the user is currently an admin
     * @return
     */
    public boolean isAdmin(){
        return isAdmin;
    }
    
    /**
     * Finds a real name if one exists, or returns their ID as a string
     * @return
     */
    public String getDisplayedName() {
        if (name!=null && name!="" && !name.matches("\\s*")) {
            return name;
        } else {
            return String.valueOf(id);
        }
    }
}
