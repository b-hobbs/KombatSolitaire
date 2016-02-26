package controller;

import ks.client.game.GameManager;
import ks.client.interfaces.ILobby;
import ks.client.interfaces.IProcessClientMessage;
import ks.framework.common.Message;
import model.Table;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import view.GameManagerGUI;
import view.TabbedLayoutGUI;

/**
 * Processes confirm request
 *
 */
public class ConfirmController implements IProcessClientMessage{
    
    UserManager um = UserManager.instance();
    TableManager tm = TableManager.instance();
    GameManager gm = GameManager.instance();
    
    public static final String TABLE_ITEM_NAME = "table";
    public static final String PLAYER_ITEM_NAME = "player";
    
    public boolean process(ILobby lobby, Message m) {
                    
        Node c_info = m.contents();
        
        NamedNodeMap c_map = c_info.getAttributes();
        
        int tableId = Integer.parseInt(c_map.getNamedItem(TABLE_ITEM_NAME).getNodeValue());
        int playerId = Integer.parseInt(c_map.getNamedItem(PLAYER_ITEM_NAME).getNodeValue());
        
        //make sure it is this user's table and s/he is the moderator
        int myID = Integer.parseInt(lobby.getContext().getUser());
        int myTable = UserManager.instance().getPlayer(myID).getTable();
        if(!(myTable == tableId)){
            System.err.println("You cannot confirm the request because you are not on table " + tableId);
            return false;
        }
        int modID = TableManager.instance().getTable(myTable).getModerator().getId();
        if (!(myID == modID)){
            System.err.println("You cannot confirm the request because you are not the moderator of table " + tableId);
            return false;
        }
        
        //add the player to the list of requested players on the table
        TableManager.instance().getTable(myTable).request(UserManager.instance().getPlayer(playerId), m.id);
                
        lobby.append("Player " + playerId + " has requested to join table " + tableId);
        
        //add button on gamePanel
        TabbedLayoutGUI tlg = ((TabbedLayoutGUI)lobby.getTableManagerGUI());
        GameManagerGUI gamePanel = tlg.getGamePanel();
        gamePanel.addConfirmButton(playerId, tableId, m.id);
        
        return true;
    }

}
