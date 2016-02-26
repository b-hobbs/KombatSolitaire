package controller;

import model.Player;

import org.w3c.dom.Document;

import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

/**
 * Moderator kicks another player off a table
 *
 */
public class KickController implements IController{
    
    /** Needs to know about lobby**/
    ILobby lobby;
    
    TableManager tm = TableManager.instance();
    UserManager um = UserManager.instance();
    
    public KickController (ILobby lobby) {
        this.lobby = lobby;
    }
    
    /**
     * Send a request to kick the specifed player to the server
     */
    public Message kick(int kickee){
        
        //get the player's current table
        int playerID = Integer.parseInt(lobby.getContext().getUser());
        Player player = um.getPlayer(playerID);
        int tableID = player.getTable();
        
        //send the leave2 request to the server
        String cmd = Message.requestHeader() + "<leave table='" + tableID + "' player='" + kickee + "'/>";
        cmd += "</request>";
        Document doc = Message.construct(cmd);
        
        Message m = new Message(doc);
        
        lobby.getContext().getClient().sendToServer(lobby, m, this);
        
        return m;
    }
    
    /**
     * Receive a table response when the player is kicked off
     */
    @Override
    public void process(ILobby lobby, Message request, Message response) {
        TableResponseShared tsr = new TableResponseShared();
        tsr.processTableResponse(lobby, response);
        
    }

}
