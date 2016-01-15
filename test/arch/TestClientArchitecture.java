package arch;

import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.lobby.LobbyFrame;
import ks.client.controllers.ClientControllerChain;

public class TestClientArchitecture extends TestCase {

	protected void setUp() {
		
	}

	// helper function to sleep for a second.
	private void waitASecond() {
		// literally wait a second.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
	}
	
	public void testClient() {
		try {
			ClientArchitecture.main(new String[]{});
			assertTrue (ClientArchitecture.cf != null);
			
			assertTrue(ClientArchitecture.cf.isVisible());
			
			// close it down
			ClientArchitecture.cf.setVisible(false);
			ClientArchitecture.cf.dispose();
			
		} catch (Exception e) {
			fail (e.getMessage());
		}
		
	}
	
	public void testClientLobbySetup() {
		MyLobbyInitialization testLobbyInit = (MyLobbyInitialization) ClientArchitecture.clientLobbySetup();
		assertTrue(testLobbyInit != null);
		
		testLobbyInit.initializeLobby(new LobbyFrame(new UserContext()));
		assertTrue(testLobbyInit.lobby.getTableManagerGUI()!=null);
		assertTrue(testLobbyInit.lobby.getUserManagerGUI()!=null);
	}
	
	public void testClientChainSetup() {
		//assertTrue();
		ClientArchitecture.controllerChainSetup();
		ClientControllerChain ccc = new ClientControllerChain();
		//ccc.
	}
}
