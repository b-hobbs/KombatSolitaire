package controller;

import ks.client.interfaces.ILobby;
import ks.client.interfaces.IProcessClientMessage;
import ks.framework.common.Message;

/**
 * Processes unsolicited tableEmpty responses (when another player is the last to leave a table)
 *
 */
public class TableEmptyController implements IProcessClientMessage{

    @Override
    /**
     * Process an unsolicited tableEmpty response
     */
    public boolean process(ILobby lobby, Message m) {
        return new TableResponseShared().processEmptyResponse(lobby, m);
    }

}
