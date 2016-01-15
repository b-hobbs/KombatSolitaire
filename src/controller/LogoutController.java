package controller;

import ks.client.controllers.DisconnectController;
import ks.client.interfaces.ILobby;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Message;

import org.w3c.dom.Document;

/**
 * Controller for logging users out of the system
 * @author bhobbs
 *
 */
public class LogoutController {
	ILobby lobby;
	
	public LogoutController(ILobby lobby){
		this.lobby = lobby;
	}
	
	/**
	 * Sends a logout request to the server and logs the user out of the system
	 */
	public void logout(){
		String cmd = Message.requestHeader() + "<logout /></request>";
		Document d = Message.construct(cmd);
		Message m = new Message(d);
		
		//send request to server
		lobby.getContext().getClient().sendToServer(m);
		
		//logout
		new DisconnectController(lobby).process(lobby.getContext());
		
		((LobbyFrame) lobby).dispose();
	}
}
