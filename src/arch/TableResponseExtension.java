package arch;

import controller.TableResponseController;
import ks.client.controllers.ClientControllerChain;
import ks.client.controllers.LobbyOutputController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

/**
 * Extension for a table response
 * 
 */
public class TableResponseExtension extends ClientControllerChain {
    
    @Override
    public boolean process(ILobby lobby, Message m) {
        if (m.getName().equalsIgnoreCase("tableResponse")) {
            return new TableResponseController().process(lobby, m);
        } 
        
        // try the next one in the chain...
        return next (lobby, m);
    }

}
