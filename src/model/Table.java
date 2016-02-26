package model;

import java.util.ArrayList;


public class Table {
    int id;
    ArrayList<Player> players;
    TableVisibility tableVisibility;
    TableStatus tableStatus;
    GameVariant gameVariant;
    int timeLimit;
    String game_string;
    Player moderator;
    int seed;
    boolean undo;
    
    //for private tables, the player who have requested to join
    ArrayList<Player> requestedPlayers;
    //the ids of the requests these players sent
    ArrayList<String> reqIds;
    
    public static final int DEFAULT_TIME_LIMIT = 300;
    
    // TODO: add a "gameOptions" array or something we can pass to a
    // game initializer, that should include:
    //      -game variant
    //      -time limit

    /**
     * Default constructor for tables upon initialization.
     * These should get "populated" from the server "inhale" during
     * client connection. Basically we'll start with 100 blank tables
     * with unique ID numbers, and then everything else gets filled in
     * on connecting.
     * @param id the Table's id number
     */
    public Table(int id) {
        this.id = id;
        this.players = new ArrayList<Player>();
        this.tableVisibility = TableVisibility.PUBLIC;
        this.tableStatus = TableStatus.EMPTY;
        this.gameVariant = GameVariant.KLONDIKE;
        this.game_string = null;
        this.timeLimit = DEFAULT_TIME_LIMIT;
        this.moderator = null;
        this.seed = 0;
        this.requestedPlayers = new ArrayList<Player>();
        this.reqIds = new ArrayList<String>();
    }

    public int getID() {
        return this.id;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public TableStatus getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(TableStatus tableStatus) {
        this.tableStatus = tableStatus;
    }
    
    public GameVariant getGameVariant() {
        return this.gameVariant;
    }
    
    public void setGameVariant(GameVariant gv) {
        this.gameVariant = gv;
    }

    public String getGame() {
        return game_string;
    }
    
    public void setGame(String game) {
        this.game_string = game;
    }

    public Player getModerator() {
        return moderator;
    }

    public void setModerator(Player moderator) {
        this.moderator = moderator;
    }
    
    public int getSeed(){
        return seed;
    }
    
    public void setSeed(int seed){
        this.seed = seed;
    }

    public int getTimeLimit() {
        return timeLimit;
    }
    
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public TableVisibility getTableVisibility() {
        return tableVisibility;
    }
    
    public void setTableVisibility(TableVisibility tableVisibility) {
        this.tableVisibility = tableVisibility;
    }
    
    public ArrayList<Player> getRequested(){
        return this.requestedPlayers;
    }
    
    public ArrayList<String> getReqIds(){
        return this.reqIds;
    }
    
    public void request(Player req, String id){
        this.requestedPlayers.add(req);
        this.reqIds.add(id);
    }
    
    public void setAllRequests(ArrayList<Player> reqs, ArrayList<String> ids){
        this.requestedPlayers = reqs;
        this.reqIds = ids;
    }
    
    public void removeRequest(Player rem, String id){
        this.requestedPlayers.remove(rem);
        this.reqIds.remove(id);
    }

    /**
     * Adds the given player to the table
     * If it's the first player to the table, update the moderator
     * (and presumably make them the moderator)
     * For now this assumes checking whether the table is join-able
     * is handled somewhere else, before this method is called.
     * @param freshBlood
     */
    public void addPlayer(Player freshBlood) {
        // Add player to table
        players.add(freshBlood);
        // Update moderator if we just added the first player to the table
        if (players.size() == 1)
            updateModerator();
        // In case the table is now full, run table status update
        updateTableStatus();
    }

    /**
     * Removes a player from the table blindly
     * If that player specified isn't at the table, nothing happens
     * This should be called by the leave table controller whenever
     * a player leaves the table voluntarily, or is kicked, banned,
     * disconnected, etc.
     * @param soreLoser
     */
    public void removePlayer(Player soreLoser) {
        int i=0;
        int testID=0;
        for (Player p : this.players) {
            testID = p.getId();
            if (testID == soreLoser.getId()) {
                break;
            }
            i++;
        }
        // TODO: otherwise, return some kind of "no such player at this table" exception?
    
        // If we found the player to be removed
        if (i <= players.size()-1) {
            players.remove(i);
            updateTableStatus();
            // If player was moderator, update moderator
            if (moderator.getId()==testID)
                updateModerator();
        }
        
        return;
    }

    private void updateTableStatus() {
        // TODO: Prof. H- GameManager will determine INPLAY, table doesn't know it's game, only game type
        /*if (game != null) {
            tableStatus = TableStatus.INPLAY;
        } else {*/
            switch (players.size()) {
            case 0:
                tableStatus = TableStatus.EMPTY;
                break;
            case 1:
            case 2:
            case 3:
                tableStatus = TableStatus.AVAILABLE;
                break;
            case 4:
                tableStatus = TableStatus.FULL;
                break;              
            }
        }
    //}

    /**
     * Updates the moderator based on the current moderator specified and
     * the number of players at the table. The next player to have joined
     * chronologically is maintained by the invariant that, as players are added
     * or removed from an ArrayList, their indices are shifted appropriately.
     * So, players are always in the ArrayList by chronological order 
     */
    private void updateModerator() {
        if (players.size() >= 1) {
            // If there's at least one player at the table, assign to first player
            moderator = players.get(0);
        } else {
            moderator = null;
        }
    }

    /**
     * Update the table's settings according to moderator preferences
     * @param ...
     */
    public void updateTableOptions() {
        //TODO: figure out what parameters we're passing to this,
        // and what data types to use for those parameters
        // for example, will we just use an "int" type for minutes
        // for the time limit?
    }

    /**
     * Obtains an array of size <=4 with 
     * @return
     */
    public int[] getPlayerIDsArray() {
        int[] playerIDs = new int[4];
        int i = 0;
        for (Player p : players) {          
            playerIDs[i] = p.getId();
            i++;
        }
        return playerIDs;
    }

    public boolean getUndo() {
        return undo;
    }
}
