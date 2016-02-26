package controller;

import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

import org.w3c.dom.Document;

public class UpdateController implements IController {
    
    public UpdateController() {
        
    }
    
    /**
     * Send update request to the server
     * @param lobby
     * @param tableID
     * @param score
     * @param game
     * @param finished
     * @return
     */
    public Message update(ILobby lobby, int tableID, int score, String game, boolean finished){
        // send a request to the server to update the game
        String cmd = Message.requestHeader() + "<update table= '" + tableID + "' score='" + score + "' game='" + game + "' finished='" + String.valueOf(finished) + "'/>";
        cmd += "</request>";
        Document doc = Message.construct(cmd);
        
        Message m = new Message(doc);
        
        lobby.getContext().getClient().sendToServer(lobby, m, this);
        
        return m;
    }
    
    /**
     * Process updateResponse
     */
    @Override
    public void process(ILobby lobby, Message request, Message response) {
        new UpdateResponseController().process(lobby, response);
    }

}
