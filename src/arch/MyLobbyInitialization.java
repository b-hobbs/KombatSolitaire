package arch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ks.client.game.GameManager;
import ks.client.interfaces.IGameInterface;
import ks.client.interfaces.ILobby2;
import ks.client.interfaces.ILobbyInitialize;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Message;

import org.w3c.dom.Document;

import view.TabbedLayoutGUI;
import view.UserManagerGUI;
import controller.LogoutController;
import controller.PlayerInfoController;
import controller.ProcessLobbyInputController;
import controller.UpdateController;
import controller.UpdateController;

/**
 * My sample shows how to integrate the client code with the GameManager I am
 * providing.
 * 
 * Note: Don't edit this class here. Rather copy this sample code into your
 * own project and (1) rename it; and (2) modify as you need
 * 
 * @author George Heineman
 */
public class MyLobbyInitialization implements ILobbyInitialize, IGameInterface {
	
	/** Panels. */
	JPanel tabgui;
	JPanel umgui;
	
	/** Enclosing lobby. */
	ILobby2 lobby;
	
	/**
	 * Our preferred Lobby pane dimensions
	 */	
	static final int MIN_TABLE_PANE_WIDTH = 540;
	static final int MIN_TABLE_PANE_HEIGHT = 280;
	static final int MAX_TABLE_PANE_WIDTH = 780;
	static final int MAX_TABLE_PANE_HEIGHT = 560;
	
	static final int MIN_CHAT_PANE_WIDTH = 235;
	static final int MIN_CHAT_PANE_HEIGHT = 120;
	static final int MAX_CHAT_PANE_WIDTH = 780;
	static final int MAX_CHAT_PANE_HEIGHT = 500;
	static final int MAX_INPUT_FIELD_WIDTH = 780;
	static final int MAX_INPUT_FIELD_HEIGHT = 24;
	
	static final int MIN_USER_PANE_WIDTH = 240;
	static final int MIN_USER_PANE_HEIGHT = 240;

	static final int MIN_FRAME_WIDTH = 850;
	static final int MIN_FRAME_HEIGHT = MIN_TABLE_PANE_HEIGHT + MIN_CHAT_PANE_WIDTH;
	
	
	public MyLobbyInitialization (JPanel tabbedpane, JPanel um) {
		this.tabgui = tabbedpane;
		this.umgui = um;
	}
	
	@Override
	public void initializeLobby(final LobbyFrame frame) {
		// remember this lobby for later use
		this.lobby = frame;
		
		// install the two manager GUIs and callback
		frame.setUserManagerGUI(umgui);
		frame.setTableManagerGUI(tabgui);
		frame.setLobbyInitialization(this);
		
		//install the lobby input controller
		frame.getInnerPanel().setLobbyInputController(new ProcessLobbyInputController(lobby));
		
		// callback object will likely need to know about things.
		frame.getContext();
		
		// Some GUI hacks.. this changes some dimensions and options of Prof. H's
		// provided GUI without having to change his code or create new classes ;)
		
		// Minimum frame size (can't be resized lower than...)
		frame.setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
		
		// Get the child components in the frame
		Component[] frameComps = frame.getContentPane().getComponents();
		JScrollPane tableManagerScrollPane = (JScrollPane) frameComps[0];
		JScrollPane chatScrollPane = (JScrollPane) frameComps[1];
		JTextField inputField = (JTextField) frameComps[2];
		JScrollPane userManagerScrollPane = (JScrollPane) frameComps[3];
		
		// Remove table manager horizontal scroll bar most of the time
		tableManagerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// Set minimum pane dimensions
		tableManagerScrollPane.setMinimumSize(new Dimension(MIN_TABLE_PANE_WIDTH, MIN_TABLE_PANE_HEIGHT));
		tableManagerScrollPane.setMaximumSize(new Dimension(MAX_TABLE_PANE_WIDTH, MAX_TABLE_PANE_HEIGHT));
		chatScrollPane.setMinimumSize(new Dimension(MIN_CHAT_PANE_WIDTH, MIN_CHAT_PANE_HEIGHT));
		chatScrollPane.setMaximumSize(new Dimension(MAX_CHAT_PANE_WIDTH, MAX_CHAT_PANE_HEIGHT));
		inputField.setMaximumSize(new Dimension(MAX_INPUT_FIELD_WIDTH, MAX_INPUT_FIELD_HEIGHT));
		userManagerScrollPane.setMinimumSize(new Dimension(MIN_USER_PANE_WIDTH, MIN_USER_PANE_HEIGHT));
		lobby.getUserManagerGUI().setPreferredSize(new Dimension(MIN_USER_PANE_WIDTH - 10, MIN_USER_PANE_HEIGHT));
		
		// grab the menu bar and make an update 
		JMenuBar menu = frame.getJMenuBar();
		
		//remove current menu items
		menu.removeAll();
		
		JMenu kg = new JMenu("KombatGames");
		
		JMenuItem options = new JMenuItem("Options");
		options.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new PlayerInfoController(lobby).displayOptions();
			}
		});
		kg.add(options);
		
		JMenuItem logout = new JMenuItem("Logout");
		logout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new LogoutController(lobby).logout();
			}
			
		});
		kg.add(logout);
				
		// Add the menu to the menu bar
		menu.add(kg);
	}

	@Override
	public void sendTableChat(int tableID, String text) {
		GameManager.instance().showTableText("", text);
		
	}

	@Override
	public void update(int tableID, int score, String game, boolean complete) {
		//UpdateController uc = new UpdateController();
		new UpdateController().update(lobby, tableID, score, game, complete);
		//uc.update(lobby, tableID, score, game, complete);
		
		if (complete) {
			GameManager.instance().exitGameWindow();
			lobby.append("You've completed a game on table " + tableID);
		}
	}

	@Override
	public void leaveGame(int tableID) {
		System.err.println("Player leaves table " + tableID);
		System.err.println("Do something about it!");
	}

	@Override
	public void turn(int tableID, Properties scores, String move, boolean complete) {
		// Turn not defined for KombatSolitaire
	}

	@Override
	public void skip(int tableNumber) {
		// Skip not defined for KombatSolitaire
	}

	@Override
	public void connected(boolean status) {
		if (!status) {
			lobby.append("Server connection lost");
		} else {
			//Request current players and tables from server for client GUI
			requestPlayers();
			requestTables();
			
			// Update the GUI elements with the successfully
			// created lobby reference.
			provideLobby();
			// Dislpay welcome message
			lobby.append("My Initializer welcomes you");
		}
		
	}

	private void requestPlayers() {
		String cmd = Message.requestHeader() + "<players /></request>";
		Document d = Message.construct(cmd);
		Message m = new Message(d);
		
		lobby.getContext().getClient().sendToServer(m);
	}

	private void requestTables() {
		String cmd = Message.requestHeader() + "<tables /></request>";
		Document d = Message.construct(cmd);
		Message m = new Message(d);
		
		lobby.getContext().getClient().sendToServer(m);
	}

	/**
	 * Provide the lobby callback to GUI elements which require it
	 */
	private void provideLobby() {
		((TabbedLayoutGUI)tabgui).setILobbyInitialize(MyLobbyInitialization.this);
		((TabbedLayoutGUI)lobby.getTableManagerGUI()).setILobby(lobby);
		((UserManagerGUI)lobby.getUserManagerGUI()).setLobby(lobby);
	}
}
