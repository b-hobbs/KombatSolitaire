package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import ks.client.game.Factory;
import ks.client.game.GameManager;
import ks.client.interfaces.IGameInterface;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;
import model.GameVariant;
import model.Player;
import model.Table;
import model.TableStatus;
import model.TableVisibility;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import view.TabbedLayoutGUI;
import view.UserManagerGUI;

/**
 * Method(s) to be used by controllers that process table responses
 *
 */

public class TableResponseShared {
    
    UserManager um = UserManager.instance();
    TableManager tm = TableManager.instance();
    GameManager gm = GameManager.instance();
    
    //for table response
    public static final String ID_ITEM_NAME = "id";
    public static final String SEED_ITEM_NAME = "seed";
    public static final String TYPE_ITEM_NAME = "type";
    public static final String FULL_ITEM_NAME = "full";
    public static final String MOD_ITEM_NAME = "moderator";
    public static final String GAME_ITEM_NAME = "game";
    public static final String OPTIONS_ITEM_NAME = "options";
    public static final String PLAYER_ITEM_NAME = "player";

    //for empty response
    public static final String TABLE_ITEM_NAME = "table";
    
    /**
     * Process a table response
     */
    public boolean processTableResponse (ILobby lobby, Message m){
                
        if(!m.name.equals("tableResponse")){
            System.err.println("Called processTableResponse when message name was not \"tableResponse\" as expected");
            return false;
        }
        
        //get the table response
        Node tables_info = m.contents();
                
        //get the tables
        NodeList tables = tables_info.getChildNodes();
        
        // get my user, my table
        Player me = um.getPlayer(Integer.parseInt(lobby.getContext().getUser()));
        int myTableID = me.getTable();
                        
        //get the relevant information for each table
        for(int i = 0; i < tables.getLength(); i++){
            // Verify valid node type
            // e.g. not a "whitespace" node in the XML markup
            Node currentItem = tables.item(i);
            // If not "element" type, skip to next node in the list
            if (currentItem.getNodeType() != Node.ELEMENT_NODE)
                continue;
                                
            NamedNodeMap map = tables.item(i).getAttributes();
                    
            //the table id
            Node t_node = map.getNamedItem(ID_ITEM_NAME);
            String id = t_node.getNodeValue();
                    
            //get the tableID as an int
            int tableID = Integer.parseInt(id);
                    
            //get the seed
            Node s_node = map.getNamedItem(SEED_ITEM_NAME);
            String seed = s_node.getNodeValue();
            //get the seed as an int
            int seed_int = Integer.parseInt(seed);
                
            //the type of table (public/private/byInvitation)
            Node ty_node = map.getNamedItem(TYPE_ITEM_NAME);
            String type = ty_node.getNodeValue();
                
            //whether the table is full
            Node f_node = map.getNamedItem(FULL_ITEM_NAME);
            String full = f_node.getNodeValue();
                    
            //get the moderator
            Node mod_node = map.getNamedItem(MOD_ITEM_NAME);
            String mod = mod_node.getNodeValue();
            int modID = Integer.parseInt(mod);
            Player moderator = um.getPlayer(modID);                 
                                            
            //create a new table with this id
            Table t = new Table(tableID);
                    
            //get the type of game
            Node game_node = map.getNamedItem(GAME_ITEM_NAME);
            if(game_node != null){
                String game = game_node.getNodeValue();
                t.setGame(game);
                //a bazillion if statements...is there an easier way?
                if(game.equals("KLONDIKE") || game.equals("nogame")) t.setGameVariant(GameVariant.KLONDIKE);
                else if(game.equals("FREECELL")) t.setGameVariant(GameVariant.FREECELL);
                else if(game.equals("SPIDER")) t.setGameVariant(GameVariant.SPIDER);
                else if(game.equals("GRANDFATHERCLOCK")) t.setGameVariant(GameVariant.GRANDFATHERCLOCK);
                else if(game.equals("PYRAMIDGAME")) t.setGameVariant(GameVariant.PYRAMIDGAME);
                else if(game.equals("IDIOT")) t.setGameVariant(GameVariant.IDIOT);
                else if(game.equals("BCASTLE")) t.setGameVariant(GameVariant.BCASTLE);
                else if(game.equals("FLOWERGARDEN")) t.setGameVariant(GameVariant.FLOWERGARDEN);
                
                //if it is not a good string, keep it as is
                else{
                    t.setGameVariant(tm.getTable(tableID).getGameVariant());
                }
            }
                    
            // get the game options
            Node opt_node = map.getNamedItem(OPTIONS_ITEM_NAME);
            
            //options may be null since the attribute is optional
            if (opt_node != null) {
                String opts = opt_node.getNodeValue();
                // TODO: can it be other things?
                if (opts.startsWith("time=")) {
                    String newTime = opts.substring(5);
                    t.setTimeLimit(Integer.parseInt(newTime));
                }
            }
            // set the seed
            t.setSeed(seed_int);
                    
            //get the players at this table
            NodeList players = tables.item(i).getChildNodes();
            
            //store playerIds to send to user manager
            LinkedList<Integer> playerIds = new LinkedList<Integer>();
            for(int j = 0; j < players.getLength(); j++){
            
                Node currentPItem = players.item(j);
                                
                if (currentPItem.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                                
                NamedNodeMap pMap = currentPItem.getAttributes();
                                        
                //get the player info
                Node player = pMap.getNamedItem(PLAYER_ITEM_NAME);
                
                String p = player.getNodeValue();
                        
                //get the player ID as an int
                int playerID = Integer.parseInt(p);             
                
                //get the player from the UM                
                Player pl = um.getPlayer(playerID);
                
                pl.setTable(tableID);
                playerIds.add(playerID);
                
                //add the player to the table
                t.addPlayer(pl);        
            }
            
            // Set table type and visibility ...
            // TODO: explain this a bit better
            if (type.equals("inPlay")) {
                t.setTableVisibility(tm.getTable(tableID).getTableVisibility());
                t.setTableStatus(TableStatus.INPLAY);
            } else {
                // If table not in play, set appropriate status based on number of players
                int playerCount = playerIds.size();
                if (playerCount == 0)
                    t.setTableStatus(TableStatus.EMPTY);
                else if (playerCount > 0 && playerCount < 4)
                    t.setTableStatus(TableStatus.AVAILABLE);
                else if (playerCount == 4 && full.equals("true"))
                    t.setTableStatus(TableStatus.FULL);
                else
                    System.err.println("TableResponseShared.java: encountered unexpected game type: " + type);
                
                if(type.equals("public"))
                    t.setTableVisibility(TableVisibility.PUBLIC);
                else if(type.equals("private")){
                    t.setTableVisibility(TableVisibility.PRIVATE);
                    //if I am the moderator of this table...
                    if(myTableID == tableID && tm.getTable(myTableID).getModerator()==me)
                        //then make sure I save the requests
                        t.setAllRequests(tm.getTable(myTableID).getRequested(), tm.getTable(myTableID).getReqIds());
                }
                else if(type.equals("byInvitation"))
                    t.setTableVisibility(TableVisibility.BYINVITATION);
                else
                    System.err.println("TableResponseShared.java: encountered unexpected player count: " + playerCount);
            }
            
            
                    
            //set the moderator
            t.setModerator(moderator);
                                
            //replace the table with the updated one
            tm.overwriteTable(tableID, t);  
            
            // Update the UserManager table columns
            um.updateTableColumn(tableID, playerIds);
                        
        }
        
        // Refresh GUI elements
        ((TabbedLayoutGUI)lobby.getTableManagerGUI()).refreshTableManager();
        ((UserManagerGUI)lobby.getUserManagerGUI()).refresh();
        
        // get my user, my table
    //  Player me = um.getPlayer(Integer.parseInt(lobby.getContext().getUser()));
    //  int myTableID = me.getTable();
        
        if (myTableID>0) {
            Table myTable = tm.getTable(myTableID);
            // If a game is starting at my table, create my game
            if (myTable.getTableStatus()==TableStatus.INPLAY && !gm.isGameActive())
                startGame(myTable, me.getId(),
                        ((IGameInterface)((TabbedLayoutGUI)lobby.getTableManagerGUI()).getLobbyCallback()));
        }
        
        return true;
    }
    
    /**
     * Starts the game in a new window with the given options.
     */
    protected void startGame(Table t, int myPlayerID, IGameInterface lobby) {
        int tableID = t.getID();
        String seed = String.valueOf(t.getSeed());
        String game = t.getGame();
        // Convert the game name to what the game information factor will recognize
        game = convertToClassString(game);
        String timeLimit = String.valueOf(t.getTimeLimit());
        String undo = String.valueOf(t.getUndo());
        String myID = String.valueOf(myPlayerID);
        
        
        Properties options = new Properties();
        // default ones from message
        options.setProperty("seed", seed);
        options.setProperty("game", game);
        
        // game specific ones for solitaire variations.
        Properties gameOptions = new Properties();
        gameOptions.setProperty("time", timeLimit);
        gameOptions.setProperty("undo", undo);
        gameOptions.setProperty("newHand", "true");
        
        // Initialize player data structures
        Properties players = new Properties();
        String playerID = "";
        String playerName = "";
        ArrayList<String> order = new ArrayList<String>();
        // Build player data by iterating through table's player list
        for (Player p : t.getPlayers()) {
            playerID = String.valueOf(p.getId());
            playerName = p.getName();
            players.setProperty(playerID, playerName);
            order.add(playerID);
        }
        
        // Launch the game window given the options we've defined
        gm.createGameWindow(tableID, myID, options, gameOptions, order, players, lobby);
    }

    private String convertToClassString(String game) {
        String[] validType = {"",""};
        Iterator<String> gameIterator = Factory.validGameTypes();
        while (gameIterator.hasNext()) {
            validType = gameIterator.next().split("\\.");
            if (validType[1].equalsIgnoreCase(game))
                return validType[0] + "." + validType[1];
        }
        // If we fail to match the game type, return Klondike by default..
        return "heineman.Klondike";
    }

    /**
     * Process a tableEmpty response
     * @param lobby
     * @param m
     * @return
     */
    public boolean processEmptyResponse(ILobby lobby, Message m){
        
        if(!m.name.equals("tableEmpty")) {
            System.err.println("Called processEmptyResponse when message name was not \"tableEmpty\" as expected");
            return false;
        }
        
        Node empty_info = m.contents();
        
        NamedNodeMap empty_map = empty_info.getAttributes();
        
        Node table_node = empty_map.getNamedItem(TABLE_ITEM_NAME);
        
        String tableID = table_node.getNodeValue();
        
        int tID = Integer.parseInt(tableID);
        
        Table t = new Table(tID);
        
        //change out the table in the TM for an empty one
        tm.overwriteTable(tID, t);
        
        um.setTableEmpty(tID);

        // this works, but we actually need to refresh all the individual table
        // GUIs whenever we leave a table
        //((TabbedLayoutGUI)lobby.getTableManagerGUI()).refreshTableManager(tID);
        ((TabbedLayoutGUI)lobby.getTableManagerGUI()).refreshTableManager();
        ((UserManagerGUI)lobby.getUserManagerGUI()).refresh();
        
        return true;
    }
}
