package arch;

import ks.client.controllers.ClientControllerChain;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;
import controller.TableEmptyController;

/**
 * Extension for a table empty response 
 * 
 */
public class TableEmptyExtension extends ClientControllerChain {
    
    @Override
    public boolean process(ILobby lobby, Message m) {
        if (m.getName().equalsIgnoreCase("tableEmpty")) {
            return new TableEmptyController().process(lobby, m);
        } 
        
        // try the next one in the chain...
        return next (lobby, m);
    }

}