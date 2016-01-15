package arch;

import ks.client.controllers.ClientControllerChain;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;
import view.TabbedLayoutGUI;
import view.TableManagerGUI;

/**
 * Extension for the logoutResponse message
 * Notifies other clients when a player logs out
 * @author bhobbs
 *
 */
public class InviteExtension extends ClientControllerChain {
	
	@Override
	public boolean process(ILobby lobby, Message m) {
		if (m.getName().equalsIgnoreCase("invite")) {
			String tableID = m.getAttribute("table");
			TableManagerGUI tmg = ((TabbedLayoutGUI)lobby.getTableManagerGUI()).getTablesPanel();
			tmg.setInvited(Integer.parseInt(tableID));
			lobby.append("You have been invited to table: " + tableID + "!! Click Accept at that table to join.");
		} 
		
		// try the next one in the chain...
		return next (lobby, m);
	}

}