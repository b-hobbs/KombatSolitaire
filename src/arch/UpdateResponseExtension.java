package arch;

import ks.client.controllers.ClientControllerChain;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;
import controller.UpdateResponseController;

public class UpdateResponseExtension extends ClientControllerChain {
        
        @Override
        public boolean process(ILobby lobby, Message m) {
            if (m.getName().equalsIgnoreCase("updateResponse")) {
                return new UpdateResponseController().process(lobby, m);
            } 
            
            // try the next one in the chain...
            return next (lobby, m);
        }
}
