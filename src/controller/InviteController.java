package controller;

import org.w3c.dom.Document;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

/**
 * Controller to send an invite to a player.
 * The inviter must be a moderator on a table
 * @author bhobbs
 *
 */
public class InviteController{
	UserManager um = UserManager.instance();
	TableManager tm = TableManager.instance();
	
	/** Needs to know about the lobby **/
	ILobby lobby;
	
	public InviteController (ILobby lobby) {
		this.lobby = lobby;
	}
	
	/**
	 * Send an invite to a player
	 * @param inviterID
	 * 		Moderator sending the invite
	 * @param inviteeID
	 * 		Player receiving the invite
	 * @param tableID
	 * 		The table id to be invited to
	 */
	public void process(int inviterID, int inviteeID, int tableID){
		//if not moderator, return
		if(tm.getTable(tableID).getModerator().getId() != inviterID)
			return;
		
		//construct invite request
		String cmd = Message.requestHeader() + "<invite table = '" + tableID + "' player='" + inviteeID + "' />";
		cmd += "</request>";
		
		Document doc = Message.construct(cmd);
		Message m = new Message(doc);
		
		//send to server
		lobby.getContext().getClient().sendToServer(m);
	}
}
