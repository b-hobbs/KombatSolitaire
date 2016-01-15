package arch;

import ks.client.controllers.ClientControllerChain;
import ks.client.interfaces.ILobby;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Message;
import controller.PlayerResponseController;

/**
 * Extension for the playerResponse message
 * This accepts the response to the 'players' request which
 * the client sends on initial connection
 * @author bhobbs
 *
 */
public class PlayerResponseExtension extends ClientControllerChain {
	boolean titleSet = false;
	@Override
	public boolean process(ILobby lobby, Message m) {
		//Hack to set the frame title, this shouldn't really go here but since we don't have the loginResponseController
		//this is one of the first places we can guarantee that context user is not null
		if(!titleSet)
			((LobbyFrame)lobby).setTitle("KombatSolitaire - User: " + lobby.getContext().getUser());
		if (m.getName().equalsIgnoreCase("playerResponse")) {
			return new PlayerResponseController().process(lobby, m);
		} 
		
		// try the next one in the chain...
		return next (lobby, m);
	}

}
