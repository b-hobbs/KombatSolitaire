package view;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ks.client.interfaces.ILobby;
import controller.TableManager;
import controller.UserManager;

public class TableManagerGUI extends JPanel {
	
	// Make Eclipse happy
	private static final long serialVersionUID = 1L;
	
	TableJoinPanel[] tjps = new TableJoinPanel[101];
	ILobby lobby;
	TabbedLayoutGUI tabbedLayout;
	/**
	 * Default constructor and initialization for table manager
	 * GUI and contained elements
	 */
	public TableManagerGUI() {
		super();

		// Table panel layout params
		int rows = 25;
		int cols = 4;
		int hgap = 5;
		int vgap = 5;
		
		setLayout(new GridLayout(rows, cols, hgap, vgap));
		
		// Set a 2-pixel space as "internal padding" on all sides
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		tjps[0] = null;
		// Initialize each table
		for (int i=1; i<=100; i++) {
			TableJoinPanel tjp = new TableJoinPanel(i);
			tjp.setToTableEmpty();
			tjps[i] = tjp;
			add(tjp, null);
		}
	}
	
	/**
	 * Refreshes a specified set of tables by table IDs array
	 * @param tables int array of the table IDs of tables to be refreshed
	 */
	public void refresh(int[] tables) {
		if (tables == null) {
			// if no tables specified, refresh all
			tables = new int[101];
			for (int i=1; i<tables.length; i++) {
				tables[i] = i;
			}
		}
		
		// refresh individual TableJoinPanels
		for (int j=1; j<tables.length; j++) {
			// Update the displayed information (variant, players, moderator)
			TableJoinPanel currentJoinPanel = tjps[tables[j]]; 
			currentJoinPanel.refresh();
			
			// Now update the button based on whether I'm on a table, etc.
			
			// First check that we have a good player id for ourselves
			// TODO: add an unchecked exception in case the parse fails? very minor concern
			int myID = Integer.parseInt(lobby.getContext().getUser());
			if (myID > 0) {
				// Otherwise, we're not on a table and we should see the appropriate
				// button text for the type of table
				switch (TableManager.instance().getTable(j).getTableStatus()) {
					case EMPTY:
						currentJoinPanel.setButtonEnabled(true);
						currentJoinPanel.setButtonText(TableJoinPanel.JOIN_TEXT);
						break;
					case AVAILABLE:
						currentJoinPanel.setButtonEnabled(true);
						currentJoinPanel.setButtonText(TableJoinPanel.JOIN_TEXT);
						break;
					case FULL:
						currentJoinPanel.setButtonEnabled(false);
						currentJoinPanel.setButtonText(TableJoinPanel.FULL_TEXT);
						break;
					case INPLAY:
						currentJoinPanel.setButtonEnabled(false);
						currentJoinPanel.setButtonText(TableJoinPanel.INPLAY_TEXT);
						break;
				}
				
				switch (TableManager.instance().getTable(j).getTableVisibility()) {
					case PRIVATE:
						currentJoinPanel.setButtonEnabled(true);
						currentJoinPanel.setButtonText(TableJoinPanel.REQUEST_TEXT);
						break;
					case BYINVITATION:
						currentJoinPanel.setButtonEnabled(false);
						currentJoinPanel.setButtonText(TableJoinPanel.CLOSED_TEXT);
						break;
				}
				
				// If we're on some table, we know 99 buttons will be disabled and 1 enabled
				int myTable = UserManager.instance().getPlayer(myID).getTable();
				if (myTable > 0) {
					// Same table? There should be a "leave" button
					if (myTable == j) {
						currentJoinPanel.setButtonEnabled(true);
						currentJoinPanel.setButtonText(TableJoinPanel.LEAVE_TEXT);
					} else {
						// Otherwise, it's a disabled button until we're off our current table
						currentJoinPanel.setButtonEnabled(false);
					}
				}
			}
			// if we're not on this table, disable the button but keep it as join
		}
	}

	/**
	 * Provide the given lobby callback to this table manager GUI
	 * (Must be done after construction/initialization)
	 * @param lobby
	 */
	public void setLobby(ILobby lobby) {
		this.lobby = lobby;
		// Provide lobby reference to each table panel
		for (int i=1; i<=100; i++) {
			tjps[i].setLobby(lobby);
		}
	}
	
	/**
	 * Provide parent gui reference for switching between tabs, etc.
	 * @param tlg
	 */
	public void setTabbedLayout(TabbedLayoutGUI tlg) {
		this.tabbedLayout = tlg;
	}

	/**
	 * Update a join panel button to allow player to Accept invitation
	 * @param tID the table to which player is invited
	 */
	public void setInvited(int tID) {
		tjps[tID].setButtonEnabled(true);
		tjps[tID].setButtonText(TableJoinPanel.ACCEPT_TEXT);
	}
}
