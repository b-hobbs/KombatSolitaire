package controller;

import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

import org.w3c.dom.Document;

import view.GameManagerGUI;
import view.TabbedLayoutGUI;

/**
 * The JoinController sends a request to the server for the player on this client
 * to join a public table. It receives a response back from the server
 * that the request either succeeded (the player joins the table), or fails
 * (the player cannot join the table). If the player successfully joins the table,
 * the JoinController calls the Table Manager to update the tables, if the player
 * cannot join the table, the player is notified.
 *
 */
public class JoinController implements IController {

	/** Needs to know about the lobby **/
	ILobby lobby;
	
	public JoinController (ILobby lobby) {
		this.lobby = lobby;
	}
	
	/**
	 * Send a join table request to the server 
	 * @param tableID 
	 * @return
	 */
	public Message join(int tableID) {
		String cmd = Message.requestHeader() + "<join table = '" + tableID + "'/>";
		cmd += "</request>";
		//System.out.println("constructing:"+cmd);
		Document doc = Message.construct(cmd);
		
		Message m = new Message(doc);
		
		lobby.getContext().getClient().sendToServer(lobby, m, this);
		
		return m;
	}
	
	@Override
	/**
	 * Processes the response
	 * Could be a tableResponse, confirmResponse, or rejectResponse
	 */
	public void process(ILobby lobby, Message request, Message response) {
		
		String resp = response.name;
		
		if(resp.equals("tableResponse")){
		
			//for now, if it is unsuccessful, just tell the player
			//TODO: actually do something with the response
			if(!response.getResponseSuccess()){
				lobby.append("Unable to join table!");
				return;
			} else {
				// Process tableResponse
				new TableResponseController().process(lobby, response);
			
				// Update the GUI as necessary
				TabbedLayoutGUI tlg = ((TabbedLayoutGUI)lobby.getTableManagerGUI());
				// Open up a game tab for us
				tlg.setGamePanel(new GameManagerGUI());
				// Provide lobby to new game tab
				tlg.setILobby(lobby);
				// Initialize GameManagerGUI componenets
				tlg.initializeGameManagerGUI();
				// Switch us to the GameManagerGUI
				tlg.getTabbedPane().setSelectedIndex(tlg.getGamePanelTabIndex());
			}
		}
		
		else if (resp.equals("confirmResponse")){
			//TODO
			lobby.append("Joining!");
		}
		
		else if (resp.equals("rejectResponse")){
			lobby.append("Sorry! Your request to join the table was rejected!");
		}
	}
}
