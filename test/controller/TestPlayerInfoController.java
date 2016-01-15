package controller;

import org.w3c.dom.Document;

import view.ChangeNamePanel;
import view.ChangePasswordPanel;

import ks.LocalClientProcessor;
import ks.client.ipc.MockServer;
import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.interfaces.IController;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Configure;
import ks.framework.common.Message;

public class TestPlayerInfoController extends TestCase {

	// host
	public static final String localhost = "localhost";
	
	// sample credentials
	public static final String user = "11323";
	public static final String password = "password";
	
	// sample client front-end
	UserContext context;

	// GUI object
	LobbyFrame lobby;
	
	// mock server for the code
	MockServer mockServer;
	
	// prepare client front-end through a mock-server object
	protected void setUp() {
		// Determine the XML schema we are going to use
		try {
			Message.unconfigure();
			assertTrue (Configure.configure());
			
			// create client to connect
			context = new UserContext();  
			lobby = new LobbyFrame (context);
			lobby.setVisible(true);
			
			context.setUser(user);
			context.setPassword(password);
			context.setSelfRegister(false);
			
			mockServer = new MockServer(lobby);
			context.setClient(mockServer);
			
		} catch (Exception e) {
			fail ("Unable to setup Message tests.");
		}
	}

	// helper function to sleep for a second.
	private void waitASecond() {
		// literally wait a second.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
	}

	public void testChangeName() {
		
		PlayerInfoController pc = new PlayerInfoController(lobby);
		pc.displayOptions();
		ChangeNamePanel changeName = pc.pic.getChangeNamePanel();
		changeName.setPasswordFieldText(password);
		changeName.setNewNameFieldText("Bob");
		waitASecond();
		pc.process(changeName);
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		IController ic = mockServer.firstRoundTripController();
		
		assertEquals("playerInfo", req.getName());
		assertEquals("Bob", req.getAttribute("new-realName"));
		assertNotNull(req.getAttribute("password"));
		
		lobby.dispose();
	}	
	
	public void testPasswordName() {
		
		PlayerInfoController pc = new PlayerInfoController(lobby);
		pc.displayOptions();
		pc.pic.getTabbedPane().setSelectedIndex(1);
		ChangePasswordPanel changePassword = pc.pic.getChangePasswordPanel();
		changePassword.setPasswordFieldText(password);
		changePassword.setNewPasswordFieldText("1");
		changePassword.setConfirmPasswordFieldText("1");
		waitASecond();
		pc.process(changePassword);
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		IController ic = mockServer.firstRoundTripController();
		
		assertEquals("playerInfo", req.getName());
		assertNotNull(req.getAttribute("password"));
		assertNotNull(req.getAttribute("new-password"));
		lobby.dispose();
	}	
}
