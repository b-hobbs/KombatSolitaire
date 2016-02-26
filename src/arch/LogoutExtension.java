package arch;

import ks.client.controllers.ClientControllerChain;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;
import controller.LogoutResponseController;

/**
 * Extension for the logoutResponse message
 * Notifies other clients when a player logs out
 * @author bhobbs
 *
 */
public class LogoutExtension extends ClientControllerChain {
    
    @Override
    public boolean process(ILobby lobby, Message m) {
        if (m.getName().equalsIgnoreCase("logoutResponse")) {
            return new LogoutResponseController().process(lobby, m);
        } 
        
        // try the next one in the chain...
        return next (lobby, m);
    }

}