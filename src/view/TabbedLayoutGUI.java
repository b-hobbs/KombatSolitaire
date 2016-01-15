package view;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ks.client.interfaces.ILobby;
import ks.client.interfaces.ILobbyInitialize;

public class TabbedLayoutGUI extends JPanel {

	//Make Eclipse happy
	private static final long serialVersionUID = 1L;
	
	// "cards" we can have in the view...
	TableManagerGUI tablesPanel;
	private final String TABLES_PANEL_TITLE = "Lobby Tables";
	
	GameManagerGUI gamePanel;
	private final String GAME_PANEL_TITLE = "My Table";
	
	JTabbedPane tabbedPane;
	
	// Reference to lobby stored here
	ILobby lobby;

	// Will need a reference back to the lobby initialization
	private ILobbyInitialize lobbyCallback;
	
	/**
	 * Constructor 
	 */
	public TabbedLayoutGUI() {
		super();
		// initialization steps?
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(tabbedPane);
	}
	
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	public void setTablesPanel(TableManagerGUI tp) {
		tablesPanel = tp;
		tp.setTabbedLayout(this);
		tabbedPane.addTab(TABLES_PANEL_TITLE, tablesPanel);
	}
	
	public void setGamePanel(GameManagerGUI gp) {
		gamePanel = gp;
		gp.setTabbedLayout(this);
		tabbedPane.addTab(GAME_PANEL_TITLE, gamePanel);
	}
	
	/**
	 * Removes a player's Game panel from the set of tabbed panels
	 * (Should leave them with only a TableManagerGUI) 
	 */
	public void clearGamePanel() {
		this.remove(gamePanel);
		tabbedPane.removeTabAt(getGamePanelTabIndex());
		gamePanel = null;
	}
	
	/**
	 * Provide the ***ILobby*** callback to this GUI
	 * @param lobby
	 */
	public void setILobby(ILobby lobby) {
		this.lobby = lobby;
		// provide lobby to table manager gui
		tablesPanel.setLobby(lobby);
		if (gamePanel != null)
			gamePanel.setLobby(lobby);
	}
	
	/**
	 * Provide the ***ILobbyInitialize*** callback to this GUI
	 * @param lobby
	 */
	public void setILobbyInitialize(ILobbyInitialize lobbyCallback) {
		this.lobbyCallback = lobbyCallback;
	}
	
	public ILobbyInitialize getLobbyCallback() {
		return lobbyCallback;
	}

	/**
	 * Refreshes the GUI panels for each table
	 */
	public void refreshTableManager() {
		tablesPanel.refresh(null);
	}
	
	/**
	 * Refreshes the GUI panel for the game tab, if it exists
	 */
	public void refreshGamePanel(){
		if(gamePanel != null)
			gamePanel.refresh();
	}
	
	/**
	 * Gets the index of the tables panel tab, normally provided
	 * to let you switch to it as the active tab.
	 * @return
	 */
	public int getTablesPanelTabIndex() {
		return tabbedPane.indexOfComponent(tablesPanel); 
	}
	
	/**
	 * Gets the index of the game panel tab, normally provided
	 * to let you switch to it as the active tab.
	 * @return
	 */
	public int getGamePanelTabIndex() {
		return tabbedPane.indexOfComponent(gamePanel); 
	}
	
	public GameManagerGUI getGamePanel(){
		return gamePanel;
	}
	
	public TableManagerGUI getTablesPanel() {
		return tablesPanel;
	}
	
	public void initializeGameManagerGUI() {
		gamePanel.initializeGUI();
	}
}
