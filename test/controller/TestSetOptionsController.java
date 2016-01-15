package controller;

import model.GameVariant;
import model.Player;
import model.Table;
import model.TableVisibility;

import org.w3c.dom.Document;

import view.TabbedLayoutGUI;
import view.TableManagerGUI;
import view.UserManagerGUI;
import ks.client.ipc.MockServer;
import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.interfaces.IController;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Configure;
import ks.framework.common.Message;

public class TestSetOptionsController extends TestCase {

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
	
	Table table;
	TableManager tm;
	UserManager um;
	int tableID = 1;
	
	// prepare client front-end through a mock-server object
	protected void setUp() {
		// Determine the XML schema we are going to use
		try {
			Message.unconfigure();
			assertTrue (Configure.configure());
			
			// create client to connect
			context = new UserContext();  
			lobby = new LobbyFrame (context);
		
			context.setUser(user);
			context.setPassword(password);
			context.setSelfRegister(false);
			
			mockServer = new MockServer(lobby);
			context.setClient(mockServer);
			
		} catch (Exception e) {
			fail ("Unable to setup Message tests.");
		}
		
		table = new Table(1);
		tm = TableManager.instance();
		Player p = new Player(390);
		tm.addPlayer(1, p);
		um = UserManager.instance();
		um.addPlayer(p);
		um.addPlayer(new Player(Integer.parseInt(user)));
		TabbedLayoutGUI tl = new TabbedLayoutGUI();
		TableManagerGUI tmgui = new TableManagerGUI();
		tmgui.setLobby(lobby);
		tl.setTablesPanel(tmgui);
		lobby.setTableManagerGUI(tl);
		lobby.setUserManagerGUI(new UserManagerGUI());
	}

	protected void tearDown(){
		um.inst = null;
		tm.instance = null;
	}
	
	// helper function to sleep for a second.
	private void waitASecond() {
		// literally wait a second.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
	}

	public void testSetOptionsString() {
		
		SetOptionsController so = new SetOptionsController(lobby);
		so.setOptions(tableID, "time=50");
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		assertEquals("options", req.getName());
		assertEquals("time=50", req.getAttribute("options"));
		IController ic = mockServer.firstRoundTripController();
		
		String xmlString = Message.responseHeader(true, req.id)
				+ "<tableResponse>"
				+ "<table id='1' seed='0' type='public' game='nogame' full='false' moderator='390' options='time=50'>" 
				+ "<player-id player='390' rating='0'/>"
				+ "</table>" 
				+ "</tableResponse>" 
				+ "</response>";
		
		Document d = Message.construct (xmlString);
		Message resp = new Message(d);
		//mockServer.process(resp);
		context.getClient().process(resp);
		
		assertTrue(tm.getTable(tableID).getTimeLimit() == 50);
		waitASecond();
	}
	
	public void testSetOptionsTableVisibilty() {
		
		SetOptionsController so = new SetOptionsController(lobby);
		so.setOptions(tableID, TableVisibility.PRIVATE);
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		assertEquals("options", req.getName());
		assertEquals("private", req.getAttribute("type"));
		IController ic = mockServer.firstRoundTripController();
		
		String xmlString = Message.responseHeader(true, req.id)
				+ "<tableResponse>"
				+ "<table id='1' seed='0' type='private' game='nogame' full='false' moderator='390'>" 
				+ "<player-id player='390' rating='0'/>"
				+ "</table>" 
				+ "</tableResponse>" 
				+ "</response>";
		
		Document d = Message.construct (xmlString);
		Message resp = new Message(d);
		mockServer.process(resp);
		
		assertEquals(TableVisibility.PRIVATE, tm.getTable(tableID).getTableVisibility());
		waitASecond();
	}	
	
	public void testSetOptionsTableGameVariant() {
		
		SetOptionsController so = new SetOptionsController(lobby);
		so.setOptions(tableID, GameVariant.FREECELL);
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		assertEquals("options", req.getName());
		assertEquals("FREECELL", req.getAttribute("game"));
		IController ic = mockServer.firstRoundTripController();
		
		String xmlString = Message.responseHeader(true, req.id)
				+ "<tableResponse>"
				+ "<table id='1' seed='0' type='private' game='FREECELL' full='false' moderator='390'>" 
				+ "<player-id player='390' rating='0'/>"
				+ "</table>" 
				+ "</tableResponse>" 
				+ "</response>";
		
		Document d = Message.construct (xmlString);
		Message resp = new Message(d);
		mockServer.process(resp);
		
		assertEquals(GameVariant.FREECELL, tm.getTable(tableID).getGameVariant());
		waitASecond();
	}
	
	public void testSetOptionsAllOptions() {
		
		SetOptionsController so = new SetOptionsController(lobby);
		so.setOptions(tableID, "time=150", TableVisibility.BYINVITATION, GameVariant.IDIOT);
		
		// now have mockserver process response
		Message req = mockServer.firstRoundTripRequest();
		assertEquals("options", req.getName());
		assertEquals("IDIOT", req.getAttribute("game"));
		assertEquals("time=150", req.getAttribute("options"));
		assertEquals("byInvitation", req.getAttribute("type"));
		IController ic = mockServer.firstRoundTripController();
		
		String xmlString = Message.responseHeader(true, req.id)
				+ "<tableResponse>"
				+ "<table id='1' seed='0'  options='time=150' game='IDIOT' full='false' moderator='390' type='byInvitation'>" 
				+ "<player-id player='390' rating='0'/>"
				+ "</table>" 
				+ "</tableResponse>" 
				+ "</response>";
		
		Document d = Message.construct (xmlString);
		Message resp = new Message(d);
		mockServer.process(resp);
		
		assertEquals(GameVariant.IDIOT, tm.getTable(tableID).getGameVariant());
		assertEquals(TableVisibility.BYINVITATION, tm.getTable(tableID).getTableVisibility());
		assertEquals(150, tm.getTable(tableID).getTimeLimit());
		waitASecond();
	}
}
