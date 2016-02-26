package controller;

import org.w3c.dom.Document;

import view.TabbedLayoutGUI;

import model.GameVariant;
import model.TableVisibility;
import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

public class SetOptionsController implements IController{

    /** Needs to know about the lobby **/
    ILobby lobby;
    
    public SetOptionsController (ILobby lobby) {
        this.lobby = lobby;
    }
    
    /**
     * Send an options request to change only the options string options
     * @param tableID
     * @param optionsString
     * @return
     */
    public Message setOptions(int tableID, String optionsString){
        
        String cmd = Message.requestHeader() + "<options table='" + tableID + "' options='" + optionsString + "'/></request>";
        Document doc = Message.construct(cmd);
        Message m = new Message(doc);
        
        //send the message
        lobby.getContext().getClient().sendToServer(lobby, m, this);
                
        return m;
    }
    
    /**
     * Send an options request to change only the type of table
     * @param tableID
     * @param tableChoice
     * @return
     */
    public Message setOptions(int tableID, TableVisibility tableChoice){
        
        //fix the table choice to work with the schema
        String tc = tableChoice.name();
        if(tc.equals("BYINVITATION")) tc = "byInvitation";
        else tc = tc.toLowerCase();
        
        String cmd = Message.requestHeader() + "<options table='" + tableID + "' type='" + tc + "'/></request>";
        Document doc = Message.construct(cmd);
        Message m = new Message(doc);
        
        //send the message
        lobby.getContext().getClient().sendToServer(lobby, m, this);
                
        return m;
    }
    
    /**
     * Send an options request to change only the game variant
     * @param tableID
     * @param game
     * @return
     */
    public Message setOptions(int tableID, GameVariant game){
        
        String cmd = Message.requestHeader() + "<options table='" + tableID + "' game='" + game + "'/></request>";
        Document doc = Message.construct(cmd);
        Message m = new Message(doc);
        
        //send the message
        lobby.getContext().getClient().sendToServer(lobby, m, this);
                
        return m;
    }
    
    /**
     * Send an options request to the server containing an options string, type of table, and type of game
     * @param tableID
     * @return
     */
    public Message setOptions(int tableID, String optionsString, TableVisibility tableChoice, GameVariant game){
        
        //fix the table choice to work with the schema
        String tc = tableChoice.name();
        if(tc.equals("BYINVITATION")) tc = "byInvitation";
        else tc = tc.toLowerCase();
        
        //create the message
        String cmd = Message.requestHeader() + "<options table='" + tableID + "' options='" + optionsString + "' game='" + game.name() + "' type='" + tc + "'/>";
        cmd += "</request>";
        Document doc = Message.construct(cmd);
        
        Message m = new Message(doc);
        
        //send the message
        lobby.getContext().getClient().sendToServer(lobby, m, this);
        
        return m;
    }
    
    /**
     * Process the table response sent back by the server
     */
    @Override
    public void process(ILobby lobby, Message request, Message response) {
        new TableResponseShared().processTableResponse(lobby, response);
        
        ((TabbedLayoutGUI)lobby.getTableManagerGUI()).refreshGamePanel();
    }
    

}
