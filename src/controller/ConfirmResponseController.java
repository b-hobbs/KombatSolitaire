package controller;

import ks.client.game.GameManager;
import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

import org.w3c.dom.Document;

/**
 * Sends a confirm response to the server, receives tableResponse back if true, no response if false
 *
 */
public class ConfirmResponseController implements IController{
    
    UserManager um = UserManager.instance();
    TableManager tm = TableManager.instance();
    GameManager gm = GameManager.instance();
    
    /** Needs to know about the lobby **/
    ILobby lobby;
    
    public ConfirmResponseController (ILobby lobby) {
        this.lobby = lobby;
    }
    
    /**
     * Send a confirmation to the server
     * @param requesterId
     * @param tableId
     * @return
     */
    public Message confirmTrue(int requesterId, int tableId, String messageId){
        
        String cmd = "<response version='1.0' id='" + messageId + "' success='true'>\n" + 
                "<confirmResponse table='" + tableId + "' player='" + requesterId + "'/>";
        cmd += "</response>";
        Document doc = Message.construct(cmd);
        Message m = new Message(doc);
        
        tm.getTable(tableId).removeRequest(um.getPlayer(requesterId), messageId);
        
        lobby.getContext().getClient().sendToServer(lobby, m, this);
                
        return m;
    }
    
    /**
     * Send a rejection to the server
     * @param requesterId
     * @param tableId
     * @return
     */
    public Message confirmFalse(int requesterId, int tableId, String messageId){
        String cmd = "<response version='1.0' id='" + messageId + "' success='false'>\n" + 
                "<confirmResponse table='" + tableId + "' player='" + requesterId + "'/>";
        cmd += "</response>";
        Document doc = Message.construct(cmd);
        Message m = new Message(doc);
        
        tm.getTable(tableId).removeRequest(um.getPlayer(requesterId), messageId);
        
        lobby.getContext().getClient().sendToServer(lobby, m, this);
        
        return m;
    }
    
    @Override
    public void process(ILobby lobby, Message request, Message response) {
        new TableResponseController().process(lobby, response);
    }
    
}
