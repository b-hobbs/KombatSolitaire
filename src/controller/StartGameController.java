package controller;

import org.w3c.dom.Document;

import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

public class StartGameController implements IController{
    
    /** Needs to know about the lobby **/
    ILobby lobby;
    
    public StartGameController (ILobby lobby) {
        this.lobby = lobby;
    }
    
    public Message startGame(int tableID){
        //TODO: do this request correctly
        String cmd = Message.requestHeader() + "<start table='" + tableID +  "'/></request>";
        Document doc = Message.construct(cmd);
        Message m = new Message(doc);
        
        //send the message
        lobby.getContext().getClient().sendToServer(lobby, m, this);
                
        return m;
        
    }

    @Override
    public void process(ILobby lobby, Message request, Message response) {
        // Response from starting a game is expected to be
        // a "tableResponse" with type reading "inPlay"
        new TableResponseController().process(lobby, response);
    }

}
