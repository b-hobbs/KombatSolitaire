package controller;

import org.w3c.dom.Document;

import ks.LocalClientProcessor;
import ks.client.ipc.MockServer;
import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.interfaces.IController;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Configure;
import ks.framework.common.Message;

public class TestProfileController extends TestCase {

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

	public void testValidGetProfile() {
		
		ProfileController pc = new ProfileController(lobby);
		pc.getProfile(11323);
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		assertEquals("getProfile", req.getName());
		assertEquals("11323", req.getAttribute("player"));
		IController ic = mockServer.firstRoundTripController();
		
		String xmlString = Message.responseHeader(true, req.id)
				+ "<playerResponse>"
				+ "<player player='11323' realName='Shadow'>"
				+ "<rating category='solitaire' value='229'/>"
				+ "<rating category='wordsteal' value='1234'/>" 
				+ "</player>" 
				+ "</playerResponse>"
				+ "</response>";
		Document d = Message.construct (xmlString);
		Message resp = new Message(d);
		mockServer.process(resp);
		
		waitASecond();
		
		// make sure it was received.
		String s = lobby.getInnerPanel().getLobbyOutput().getText();
		assertTrue(s.contains("solitaire 229"));
		assertTrue(s.contains("wordsteal 1234"));
		
		lobby.dispose();
	}	
	
	public void testGetProfileWithNoRatings() {
		
		ProfileController pc = new ProfileController(lobby);
		pc.getProfile(11323);
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		assertEquals("getProfile", req.getName());
		assertEquals("11323", req.getAttribute("player"));
		IController ic = mockServer.firstRoundTripController();
		
		String xmlString = Message.responseHeader(true, req.id)
				+ "<playerResponse>"
				+ "<player player='11323' realName='Shadow'>"
				+ "</player>" 
				+ "</playerResponse>"
				+ "</response>";
		Document d = Message.construct (xmlString);
		Message resp = new Message(d);
		mockServer.process(resp);
		
		waitASecond();
		
		// make sure it was received.
		String s = lobby.getInnerPanel().getLobbyOutput().getText();
		assertTrue(s.contains("no ratings"));
		
		lobby.dispose();
	}
	
}
