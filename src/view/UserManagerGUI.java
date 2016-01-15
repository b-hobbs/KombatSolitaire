package view;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import controller.JoinController;
import controller.ProfileController;
import controller.UserManager;

import ks.client.interfaces.ILobby;

/**
 * 	
 *	GUI that displays information about the users on the client
 *	Displays JTable of user ids, names, table they are on, and privateChatScope 
 * @author bhobbs
 *
 */
public class UserManagerGUI extends JPanel {
	JTable table;
	ILobby lobby;

	/**
	 * Creates the table of connect user info
	 */
	public UserManagerGUI() {
		super(new GridLayout(1, 0));

		table = new JTable(new UserManagerModel());

		// Enable column sorting
		table.setAutoCreateRowSorter(true);

		// Center the Table column
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumn("Table").setCellRenderer(dtcr);

		// Center the Chat Column
		// table.getColumn("Chat").setCellRenderer(dtcr);

		table.setCellSelectionEnabled(false);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Set preffered width of Name column
		table.getColumnModel().getColumn(1).setPreferredWidth(300);

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}

	/**
	 * Updates the data in the JTable
	 */
	public void refresh() {
		((UserManagerModel) table.getModel()).updateData();
	}

	public void setLobby(ILobby lobby) {
		this.lobby = lobby;
		table.addMouseListener(new PopupListener(table, lobby));
	}

	public JTable getJTable(){
		return table;
	}
}
