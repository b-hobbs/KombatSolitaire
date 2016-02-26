package controller;

import ks.client.interfaces.ILobby;
import ks.client.interfaces.IProcessClientMessage;
import ks.framework.common.Message;
import view.TabbedLayoutGUI;

/**
 * Processes unsolicited table responses (ie when another player joins
 * or leaves a table)
 **/
public class TableResponseController implements IProcessClientMessage {
    
    @Override
    /**
     * Process an unsolicited table response
     **/
    public boolean process(ILobby lobby, Message m) {
        
        boolean returnVal = new TableResponseShared().processTableResponse(lobby, m);
        
        ((TabbedLayoutGUI)lobby.getTableManagerGUI()).refreshGamePanel();
        
        return returnVal;
    }

}
