package controller;

import model.Player;
import model.Table;

import org.w3c.dom.Document;

import view.GameManagerGUI;
import view.TabbedLayoutGUI;
import view.TableManagerGUI;
import view.UserManagerGUI;

import arch.ClientExtension;

import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.controllers.ClientControllerChain;
import ks.client.controllers.ConnectController;
import ks.client.controllers.DisconnectController;
import ks.client.lobby.LobbyFrame;
import ks.client.processor.ClientProcessor;
import ks.framework.common.Configure;
import ks.framework.common.Message;
import ks.server.ipc.Server;


public class TestJoinController extends TestCase {
	
	// host
		public static final String localhost = "localhost";
		
		// sample credentials (really meaningless in the testing case)
		public static final String user = "11323";
		public static final String password = "password";
		public static final int tableID = 24;
		public static final String game_name = "Solitaire";
		public static final int seed = 123;
		public static final int playerID = Integer.parseInt(user);
		
		TableManager tm;
		UserManager um;
				
		/** Constructed objects for this test case. */
		Server server;
		UserContext context;
		LobbyFrame lobby;
		
		// random port 8000-10000 to avoid arbitrary conflicts
		int port;
		
		/**
		 * setUp() method is executed by the JUnit framework prior to each 
		 * test case.
		 */
		protected void setUp() {
			// Determine the XML schema we are going to use
			try {
				Message.unconfigure();
				assertTrue (Configure.configure());
			} catch (Exception e) {
				fail ("Unable to setup Message tests.");
			}
			
			// start server on a random port.
			port = (int) (8000 + Math.random()*2000);
			server = new Server(port);
			
			// Any non-standard controllers for server would need to be included here
			
			assertTrue (server.activate());
			
			waitASecond();
			
			// Any non-standard controllers for client would need to be included here.
			// Specifically, the output response handler is non standard, so include
			// that one here.
			ClientControllerChain head = ClientProcessor.head();
			head.append(new ClientExtension());
			
			// create client to connect
			context = new UserContext();  // by default, localhost
			lobby = new LobbyFrame (context);
			lobby.setVisible(true);
			
			tm = TableManager.instance();
			um = UserManager.instance();
			
			context.setPort(port);
			context.setUser(user);
			context.setPassword(password);
			context.setSelfRegister(false);
						
			um.addPlayer(new Player(playerID));
			
			// connect client to server
			assertTrue (new ConnectController(lobby).process(context));
			
			
			// Set UserManager, TableManager and GameManager GUI panels
			UserManagerGUI userManagerGUI = new UserManagerGUI();
			TableManagerGUI tableManagerGUI = new TableManagerGUI();
			
			TabbedLayoutGUI tabbedLayoutGUI = new TabbedLayoutGUI();
			tabbedLayoutGUI.setTablesPanel(tableManagerGUI);
					
			lobby.setUserManagerGUI(userManagerGUI);
			lobby.setTableManagerGUI(tabbedLayoutGUI);
			
			((TabbedLayoutGUI)lobby.getTableManagerGUI()).setILobby(lobby);
			((UserManagerGUI)lobby.getUserManagerGUI()).setLobby(lobby);
			
			// wait for things to settle down. As your test cases become more
			// complex, we may find it necessary to include additional waiting 
			// times.
			waitASecond();
		}

		/**
		 * tearDown() is executed by JUnit at the conclusion of each individual
		 * test case.
		 */
		protected void tearDown() {
			// the other way to leave is to manually invoke controller.
			assertTrue (new DisconnectController (lobby).process(context));
			
			waitASecond();
			server.deactivate();
			
			lobby.setVisible(false);
			lobby.dispose();
			lobby = null;
			um.inst = null;
			tm.instance = null;

			context = null;
			server = null;
		}
		
		// helper function to sleep for a second.
		private void waitASecond() {
			// literally wait a second.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		/**
		 * Test case- successfully join a table
		 */
		public void testJoin(){
			
			Player thisPlayer = um.getPlayer(playerID);
			
			JoinController jc = new JoinController(lobby);
			
			//create JOIN REQUEST
			Message req = jc.join(tableID);
			
			String messageID = req.id;
			
			// create TABLE RESPONSE message
			Message m = TestTableResponseShared.createResponse(messageID, tableID, seed, playerID, game_name);
			
			// process message on client
			context.getClient().process(m);
			
			//make sure it is correct
			//get the newly updated table from the TM
			Table t = tm.getTable(tableID);
			//create a new table with the same ID and add the player
			Table test = new Table(tableID);
			test.addPlayer(thisPlayer);
			test.setModerator(thisPlayer);
			test.setGame(game_name);
			test.setSeed(seed);
			
			//make sure the tables have the same info
			assertEquals(test.getID(), t.getID());
			assertEquals(test.getModerator(), t.getModerator());
			assertEquals(test.getGame(), t.getGame());
			assertEquals(test.getSeed(), t.getSeed());
			assertEquals(test.getTableStatus(), t.getTableStatus());
			assertEquals(test.getPlayers(), t.getPlayers());
			
			int p_table = um.getPlayer(playerID).getTable();
			assertEquals(tableID, p_table);
		}
		
		//a failed request to join a table
		public void testFailJoin(){
			
			String existing = lobby.getInnerPanel().getLobbyOutput().getText();
			
			JoinController jc = new JoinController(lobby);
			
			//create JOIN REQUEST
			Message req = jc.join(tableID);
						
			String messageID = req.id;
			
			Message m = TestTableResponseShared.createFalseResponse(messageID, tableID, seed, playerID, game_name);
			
			// process message on client
			context.getClient().process(m);
			
			String output = lobby.getInnerPanel().getLobbyOutput().getText().trim();
			assertEquals(existing + "Unable to join table!", output);
		}
		
		// try testing JoinController (and all controllers in general)
		//		 with messages that also contain white spaces between the markup tags
		public void testSpaceJoin(){
			
			Player thisPlayer = um.getPlayer(playerID);
			
			JoinController jc = new JoinController(lobby);
						
			//create JOIN REQUEST
			Message req = jc.join(tableID);
			
			String messageID = req.id;
			
			Message m = TestTableResponseShared.createSpaceResponse(messageID, tableID, seed, playerID, game_name);
			
			// process message on client
			context.getClient().process(m);
			
			//make sure it is correct
			//get the newly updated table from the TM
			Table t = tm.getTable(tableID);
			//create a new table with the same ID and add the player
			Table test = new Table(tableID);
			//Player thisPl = um.getPlayer(playerID);
			test.addPlayer(thisPlayer);
			test.setModerator(um.getPlayer(playerID));
			test.setGame(game_name);
			test.setSeed(seed);
			
			//make sure the tables have the same info
			assertEquals(test.getID(), t.getID());
			assertEquals(test.getGame(), t.getGame());
			assertEquals(test.getSeed(), t.getSeed());
			assertEquals(test.getModerator(), t.getModerator());
			assertEquals(test.getTableStatus(), t.getTableStatus());
			assertEquals(test.getPlayers(), t.getPlayers());
			
			int p_table = thisPlayer.getTable();
			assertEquals(p_table, tableID);
			
		}

}
