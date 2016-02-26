package controller;

import ks.client.UserContext;
import ks.client.interfaces.ILobby;
import ks.client.interfaces.ILobby2;
import ks.client.interfaces.IProcessLobbyInput;
import ks.client.ipc.Client;
import ks.framework.common.Message;
import org.w3c.dom.Document;

/**
 * Controller to handle user input to lobby
 * @author bhobbs
 *
 */
public class ProcessLobbyInputController implements IProcessLobbyInput{
    
    /** Lobby being controlled. */
    ILobby lobby;
    
    UserManager um = UserManager.instance();
    
    /**
     * The Lobby Input processor needs to know the lobby to complete
     * its tasks.
     * 
     * @param lobby
     */
    public ProcessLobbyInputController (ILobby lobby) {
        this.lobby = lobby;
    }
    
    /**
     * Process string entered by user.
     * @param s
     */
    public void process(String s) {
        UserContext context = lobby.getContext();
        Client client = context.getClient();

        // must have been a chat command all along. 
        String cmd = Message.requestHeader() + "<chat>";
        
        //check to see if the user has selected people to private chat to
        if(um.hasPrivateChatScope()){
            for(Integer i: um.getPrivateChatScope()){
                //add selected players to the chat request to make it a private chat request
                cmd += "<player-id player='" + i + "' />";
            }
        }
        
        cmd += "<text>" + s + "</text></chat></request>";

        //send request to server
        Document d = Message.construct(cmd);
        Message m = new Message(d);
        client.sendToServer(m);
        
        String who = context.getUser();
        
        // Use new interface if it is available, otherwise resort to old one.
        if (!(lobby instanceof ILobby2) ){
            lobby.append(who + ": " + s);
        } else {
            ((ILobby2) lobby).append(who, s, true);
        }
    }
}
