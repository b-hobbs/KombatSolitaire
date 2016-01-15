package arch;

import org.w3c.dom.Document;

import view.TabbedLayoutGUI;
import view.TableManagerGUI;
import view.UserManagerGUI;

import arch.MyLobbyInitialization;
import junit.framework.TestCase;
import ks.client.controllers.DisconnectController;
import ks.client.interfaces.ILobby;
import ks.client.interfaces.ILobbyInitialize;
import ks.client.lobby.ConnectButtonController;
import ks.client.lobby.ConnectFrame;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Configure;
import ks.framework.common.Message;
import ks.framework.communicator.Communicator;
import ks.server.ipc.Server;

/** Validate controller works as expected. */
public class TestMyLobbyInitialization extends TestCase {

	ILobbyInitialize init;
	ConnectFrame cf;
	Server server;
	UserManagerGUI userManagerGUI;
	TableManagerGUI tableManagerGUI;
	TabbedLayoutGUI tabbedLayoutGUI;
	
	protected void setUp() {
		// Determine the XML schema we are going to use
		try {
			assertTrue (Configure.configure());
			
			// validate a simple tables
			String s = Message.requestHeader() + "<tables/></request>";
			Document d = Message.construct(s);
			assertTrue (d != null);
			
		} catch (Exception e) {
			fail ("Unable to setup Message tests.");
		}
		
		// standard 7878
		server = new Server();
		
		//if a server is already started on this port this may fail
		assertTrue(server.activate());
		
		// Set UserManager, TableManager and GameManager GUI panels
		userManagerGUI = new UserManagerGUI();
		tableManagerGUI = new TableManagerGUI();
		//GameManagerGUI gameManagerGUI = new GameManagerGUI();
		
		// Set TabbedLayout gui panel and add table and game manager GUIs to it
		tabbedLayoutGUI = new TabbedLayoutGUI();
		tabbedLayoutGUI.setTablesPanel(tableManagerGUI);
				
		// initialization callback. Client-side groups can pass in 
		// an object that provides this interface into the ConnectFrame
		// constructor and it will be called at the proper time.
		init = new MyLobbyInitialization(tabbedLayoutGUI, userManagerGUI);
		cf = new ConnectFrame(init);
	}
	
	protected void tearDown() {
		server.deactivate();
		
	}
	
	// helper function to sleep for a second.
	private void waitASecond() {
		// literally wait a second.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
	}
	
	public void testProcess() {
		cf.getLoginUserText().setText("113344");
		cf.getLoginPassText().setText("password");
		
		ILobby lobby = new ConnectButtonController(cf).process(init);
		assertTrue (lobby != null);
		
		// make sure our GUIs are in place
		assertTrue (tabbedLayoutGUI == lobby.getTableManagerGUI());
		assertTrue (userManagerGUI == lobby.getUserManagerGUI());
		
		waitASecond();
		
		// validate server has someone
		Communicator c = server.getCommunicator();
		assertTrue (c.isOnline("113344"));
		
		// request log out from client.
		new DisconnectController(lobby).process(lobby.getContext());
		
		
		waitASecond();
		assertFalse (c.isOnline("113344"));
		
		((LobbyFrame) lobby).setVisible(false);
		((LobbyFrame) lobby).dispose();
		
	}
}

