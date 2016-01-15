package arch;

import controller.ConfirmController;
import ks.client.controllers.ClientControllerChain;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

/**
 * Extension for a player requesting to join a table
 */
public class ConfirmExtension extends ClientControllerChain{
	
	@Override
	public boolean process(ILobby lobby, Message m) {
		
		if (m.getName().equalsIgnoreCase("confirm")) {
			return new ConfirmController().process(lobby, m);
		} 
		
		// try the next one in the chain...
		return next (lobby, m);
	}

}
