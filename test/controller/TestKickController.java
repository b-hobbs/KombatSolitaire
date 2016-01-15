package controller;

import model.GameVariant;
import model.Player;
import model.Table;
import view.GameManagerGUI;
import view.TabbedLayoutGUI;
import view.TableManagerGUI;
import view.UserManagerGUI;
import arch.ClientExtension;
import arch.LogoutExtension;
import arch.TableEmptyExtension;
import arch.TableResponseExtension;
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

public class TestKickController extends TestCase{
	
	// host
		public static final String localhost = "localhost";

		// sample credentials (really meaningless in the testing case)
		public static final String user1 = "1";
		public static final String user2 = "2";
		public static final String password = "password";
		public static final int tableID = 1;
		public static final String game_name = "Solitaire";
		public static final int seed = 123;
		public static final int player1ID = Integer.parseInt(user1);
		public static final int player2ID = Integer.parseInt(user2);

		/** Constructed objects for this test case. */
		Server server;
		UserContext context;
		LobbyFrame lobby;
		TableManager tm;
		UserManager um;

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
			head.append(new LogoutExtension());
			head.append(new TableResponseExtension());
			head.append(new TableEmptyExtension());

			// create client to connect
			context = new UserContext();  // by default, localhost
			lobby = new LobbyFrame (context);
			
			um = UserManager.instance();
			tm = TableManager.instance();

			context.setPort(port);
			context.setUser(user1);
			context.setPassword(password);
			context.setSelfRegister(false);

			Player thisPlayer = new Player(player1ID);
			um.addPlayer(thisPlayer);
			tm.addPlayer(tableID, thisPlayer);
			thisPlayer.setTable(tableID);
			
			// Set UserManager, TableManager and GameManager GUI panels
			UserManagerGUI userManagerGUI = new UserManagerGUI();
			TableManagerGUI tableManagerGUI = new TableManagerGUI();
			GameManagerGUI gameManagerGUI = new GameManagerGUI();
			
			TabbedLayoutGUI tabbedLayoutGUI = new TabbedLayoutGUI();
			tabbedLayoutGUI.setTablesPanel(tableManagerGUI);
			tabbedLayoutGUI.setGamePanel(gameManagerGUI);
					
			lobby.setUserManagerGUI(userManagerGUI);
			lobby.setTableManagerGUI(tabbedLayoutGUI);
			
			((TabbedLayoutGUI)lobby.getTableManagerGUI()).setILobby(lobby);
			((UserManagerGUI)lobby.getUserManagerGUI()).setLobby(lobby);		
			
			lobby.setVisible(true);

			// connect client to server
			assertTrue (new ConnectController(lobby).process(context));

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

			um.inst = null;
			tm.instance = null;
			lobby.setVisible(false);
			lobby.dispose();
			lobby = null;
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
		
		public void testKick(){
			
			assertFalse(tm.isTableEmpty(tableID));
			assertEquals(1, tm.getTable(tableID).getPlayers().size());
			
			Player secondPlayer = new Player(player2ID);
			um.addPlayer(secondPlayer);
			tm.addPlayer(tableID, secondPlayer);
			secondPlayer.setTable(tableID);
			
			assertEquals(secondPlayer, um.getPlayer(secondPlayer.getId()));
			assertEquals(2, tm.getTable(tableID).getPlayers().size());
			Table t = tm.getTable(tableID);
			assertEquals(player1ID, t.getModerator().getId());
			
			//kick player2 off
			KickController kc = new KickController(lobby);
			Message req = kc.kick(player2ID);
			String messageID = req.id;
			
			// create TABLE RESPONSE message
			Message m = TestTableResponseShared.createResponse(messageID, tableID, seed, player1ID, game_name);
			
			// process message on client
			context.getClient().process(m);
			
			//make sure it is correct
			//get the newly updated table from the TM
			t = tm.getTable(tableID);
			//create a new table with the same ID and add the player
			Table test = new Table(tableID);
			test.addPlayer(um.getPlayer(player1ID));
			test.setModerator(um.getPlayer(player1ID));
			test.setGameVariant(GameVariant.KLONDIKE);
			
			//this player should remain, be moderator, etc.
			assertEquals(test.getID(), t.getID());
			assertEquals(test.getModerator(), t.getModerator());
			//assertEquals(test.getGameVariant(), t.getGameVariant());
			//assertEquals(test.getSeed(), t.getSeed());
			assertEquals(test.getTableStatus(), t.getTableStatus());
			assertEquals(test.getPlayers(), t.getPlayers());
			
		}

}
