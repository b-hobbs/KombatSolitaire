package controller;

import ks.client.interfaces.ILobby;
import ks.client.interfaces.IProcessClientMessage;
import ks.framework.common.Message;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import view.UserManagerGUI;

/**
 * Controller for handling logout responses
 *
 */
public class LogoutResponseController implements IProcessClientMessage{
	
	public final String PLAYER_ITEM_NAME = "player";
	UserManager um = UserManager.instance();

	@Override
	public boolean process(ILobby lobby, Message m) {
		Node info = m.contents();
		
		NamedNodeMap playerMap = info.getAttributes();
		int playerID = Integer.parseInt(
				playerMap.getNamedItem(PLAYER_ITEM_NAME).getNodeValue());
					
		um.removePlayer(playerID);
		
		//TODO: make player leave from the table they were on if necessary
		
		((UserManagerGUI)lobby.getUserManagerGUI()).refresh();
		return true;
	}
}
