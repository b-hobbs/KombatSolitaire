package controller;

import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;
import model.Player;

import org.w3c.dom.Document;

import view.GameManagerGUI;
import view.TabbedLayoutGUI;

/**
 * LeaveController causes the player on this client to leave his/her
 * current table. Once the server accepts the request (this request should not
 * fail), the TM is updated.
 * 
 *
 */
public class LeaveController implements IController {
	
	/** Needs to know about lobby**/
	ILobby lobby;
	
	TableManager tm = TableManager.instance();
	UserManager um = UserManager.instance();
	
	public LeaveController (ILobby lobby) {
		this.lobby = lobby;
	}
	
	public Message leaveTable(){
		//get the player's current table
		int playerID = Integer.parseInt(lobby.getContext().getUser());
		Player player = um.getPlayer(playerID);
		int tableID = player.getTable();

		// The table response from the server won't tell us that we left
		// Therefore, remove ourselves from the table
		um.getPlayer(playerID).setTable(-1);
		
		// send a request to the server to leave the table
		String cmd = Message.requestHeader() + "<leave table = '" + tableID + "'/>";
		cmd += "</request>";
		Document doc = Message.construct(cmd);
		
		Message m = new Message(doc);
		
		lobby.getContext().getClient().sendToServer(lobby, m, this);
		
		return m;
	}

	@Override
	/**
	 * Process the table response to the request
	 */
	public void process(ILobby lobby, Message request, Message response) {
		String resp = response.name;
		TabbedLayoutGUI tlg = ((TabbedLayoutGUI)lobby.getTableManagerGUI());
		// Remove our GameManagerGUI tab
		tlg.clearGamePanel();
		// Switch us to the TableManagerGUI
		tlg.getTabbedPane().setSelectedIndex(tlg.getTablesPanelTabIndex());		
		if(resp.equals("tableResponse")) {
			
			new TableResponseController().process(lobby, response);
		} else if (resp.equals("tableEmpty")) {
			new TableEmptyController().process(lobby, response);
		}
	}

}
