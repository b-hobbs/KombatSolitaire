package view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import model.Player;
import model.Table;
import model.TableVisibility;

import ks.client.interfaces.ILobby;

import controller.InviteController;
import controller.JoinController;
import controller.ProfileController;
import controller.TableManager;
import controller.UserManager;

/**
 * Handles the popup menu for UserManagerGUI JTable
 */
class PopupListener extends MouseAdapter {
	int tableID;
	JTable table;
	ILobby lobby;
	UserManager um = UserManager.instance();
	TableManager tm = TableManager.instance();

	public PopupListener(JTable table, ILobby lobby) {
		this.table = table;
		this.lobby = lobby;
	}

	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	private void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			Point p = new Point(e.getX(), e.getY());
			int col = table.columnAtPoint(p);
			int row = table.rowAtPoint(p);

			int mcol = table.getColumn(table.getColumnName(col))
					.getModelIndex();
			String colName = table.getColumnName(col);

			table.setRowSelectionInterval(row, row);

			JPopupMenu contextMenu = createContextMenu(row, mcol, colName);

			contextMenu.show(table, p.x, p.y);
		}
	}

	/**
	 * Creates the menu for the popupmenu
	 * 
	 * @param row
	 * @param mcol
	 * @param colName
	 *            Can use colName create a different menu for a specific column
	 * @return
	 */
	protected JPopupMenu createContextMenu(final int row, final int mcol,
			final String colName) {
		JPopupMenu contextMenu = new JPopupMenu();

		/*
		 * View Profile menu item
		 */
		JMenuItem viewProfile = new JMenuItem();
		viewProfile.setText("View Profile");
		contextMenu.add(viewProfile);
		viewProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int playerID = (Integer) table.getValueAt(row,
						table.getColumn("ID").getModelIndex());
				new ProfileController(lobby).getProfile(playerID);
			}
		});

		/*
		 * Private message menu item
		 */
		JMenuItem sendMessage = new JMenuItem();
		sendMessage.setText("Private Message");
		sendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int playerID = (Integer) table.getValueAt(row,
						table.getColumn("ID").getModelIndex());
				UserManager.instance().privateChatTo(playerID);
				refresh();
			}
		});
		contextMenu.add(sendMessage);

		/*
		 * Clear private chat
		 */
		JMenuItem clearChat = new JMenuItem();
		clearChat.setText("Clear chat selections");
		clearChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserManager.instance().resetPrivateChatScope();
				refresh();
			}
		});
		contextMenu.add(clearChat);

		/*
		 * Join Table menu item
		 */
		try {
			tableID = -1; // reset
			String t = table.getValueAt(row,
					table.getColumn("Table").getModelIndex()).toString();
			if (!t.equals("-")) {
				tableID = Integer.parseInt(t);
			}
		} catch (Exception x) {
		}

		if (tableID > 0) { // if user selected is on a table
			final int currentuser = Integer.parseInt(lobby.getContext()
					.getUser());
			Player currentPlayer = um.getPlayer(currentuser);
			if (currentPlayer.getTable() == -1) {
				JMenuItem joinTable = new JMenuItem();
				joinTable.setText("Join Table");
				joinTable.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							new JoinController(lobby).join(tableID);
						} catch (Exception x) {
						}
					}
				});
				contextMenu.add(joinTable);
			}
		}

		/*
		 * Invite menu item
		 */
		// if user selected is not on a table
		try {
			if (tableID == -1) {
				final int inviterID = Integer.parseInt(lobby.getContext()
						.getUser());
				Player inviterPlayer = um.getPlayer(inviterID);
				final int tableInvitationID = inviterPlayer.getTable();
				if (tableInvitationID != -1) {
					Table t = tm.getTable(tableInvitationID);
					Player moderator = t.getModerator();
					final int inviteeID = (Integer) table.getValueAt(row, table
							.getColumn("ID").getModelIndex());
					if (inviterID == moderator.getId()
							&& (t.getTableVisibility() == TableVisibility.BYINVITATION || t
									.getTableVisibility() == TableVisibility.PRIVATE)) {
						JMenuItem invite = new JMenuItem();
						invite.setText("Invite to Table");
						invite.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								try {
									new InviteController(lobby).process(
											inviterID, inviteeID,
											tableInvitationID);
								} catch (Exception x) {
								}
							}
						});
						contextMenu.add(invite);
					}
				}
			}
		} catch (Exception x) {
		}
		return contextMenu;
	}

	public void refresh() {
		((UserManagerModel) table.getModel()).updateData();
	}
}