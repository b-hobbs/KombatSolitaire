package controller;

import java.util.Properties;

import ks.client.game.GameManager;
import ks.client.interfaces.ILobby;
import ks.client.interfaces.IProcessClientMessage;
import ks.framework.common.Message;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class UpdateResponseController implements IProcessClientMessage {

	/**
	 * Process an incoming 'updateResponse' message from the server to update scores
	 */
	@Override
	public boolean process(ILobby lobby, Message m) {
		// Update game scores...
		
		Node info = m.contents();
		
		NamedNodeMap updateMap = info.getAttributes();
		String score = updateMap.getNamedItem("score").getNodeValue();
		String player = updateMap.getNamedItem("player").getNodeValue();
		
		// Assign updated score values
		Properties updatedScores = new Properties();
		updatedScores.put(player, score);
		
		Properties updatedGame = new Properties();
		// Not yet implemented
		
		GameManager.instance().updateScores(updatedScores, updatedGame);
		
		return false;
	}

}
