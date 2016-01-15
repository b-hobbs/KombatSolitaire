package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import ks.client.interfaces.ILobby;
import model.Table;
import controller.JoinController;
import controller.LeaveController;
import controller.TableManager;

public class TableJoinPanel extends JPanel {

	ILobby lobby;
	
	static final String VARIANT_LABEL = "variant";
	static final String DEFAULT_VARIANT_TEXT = "KLONDIKE";
	static final String MOD_LABEL = "mod";
	static final String P2_LABEL = "p2";
	static final String P3_LABEL = "p3";
	static final String P4_LABEL = "p4";
	static final String EMPTY_TEXT = "";
	static final String JOIN_TEXT = "Join";
	static final String LEAVE_TEXT = "Leave";
	static final String FULL_TEXT = "Full";
	static final String INPLAY_TEXT = "In Play";
	static final String REQUEST_TEXT = "Request";
	static final String CLOSED_TEXT = "Closed";
	static final String ACCEPT_TEXT = "Accept";
	
	// ID of the table associated with this join panel
	int id;
	
	/**
	 * Constructor creating the square panel where the player
	 * joins, leaves, requests and accepts to a table
	 * Displays info on number of players, moderator and identities
	 * @param id
	 */
	public TableJoinPanel(int id) {
		// Initialize table basics
		this.id = id;
		this.lobby = lobby;
		this.setName("tableJoinPanel"+id);
		this.setLayout(new GridBagLayout());
		Border bevel = BorderFactory.createRaisedBevelBorder();
		this.setBorder(bevel);
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Set internal JPanel padding: 2 pixels on all sides
		Insets defaultInsets = new Insets(2, 2, 2, 2);
		gbc.insets = defaultInsets;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		// add Table label to panel
		JLabel tl = new JLabel("Table " + this.id);
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(tl, gbc);
		
		// add Game type label to panel
		JLabel variant = new JLabel(DEFAULT_VARIANT_TEXT, JLabel.CENTER);
		variant.setName(VARIANT_LABEL);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		this.add(variant, gbc);
		
		gbc.gridwidth = 1;
		
		// add moderator label to panel
		JLabel ml = new JLabel("M: ");
		gbc.gridy = 2;
		this.add(ml, gbc);
		JLabel mName = new JLabel(EMPTY_TEXT);
		mName.setName(MOD_LABEL);
		gbc.gridx = 1;
		this.add(mName, gbc);
		
		// add player labels to panel...
		JLabel p2 = new JLabel("P2: ");
		gbc.gridx = 0;
		gbc.gridy = 3;
		this.add(p2, gbc);
		gbc.gridx = 1;
		JLabel p2Name = new JLabel(EMPTY_TEXT);
		p2Name.setName(P2_LABEL);
		this.add(p2Name, gbc);

		JLabel p3 = new JLabel("P3: ");
		gbc.gridx = 0;
		gbc.gridy = 4;
		this.add(p3, gbc);
		gbc.gridx = 1;
		JLabel p3Name = new JLabel(EMPTY_TEXT);
		p3Name.setName(P3_LABEL);
		this.add(p3Name, gbc);
		
		JLabel p4 = new JLabel("P4: ");
		gbc.gridx = 0;
		gbc.gridy = 5;
		this.add(p4, gbc);
		gbc.gridx = 1;
		JLabel p4Name = new JLabel(EMPTY_TEXT);
		p4Name.setName(P4_LABEL);
		this.add(p4Name, gbc);
		
		// add Join button to panel
		JButton jb = new JButton();
		jb.setSize(new Dimension(100, 50));
		jb.setText(JOIN_TEXT);
		jb.setName("button"+id);
		gbc.gridx = 1;
		gbc.gridy = 0;
		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton)e.getSource();
				TableJoinPanel tjp = (TableJoinPanel)button.getParent(); 
				int id = tjp.id;
				
				// Do something based on what our button said to do...
				if (button.getText().equals(JOIN_TEXT))
					// Fire join message
					new JoinController(lobby).join(id);
				else if (button.getText().equals(LEAVE_TEXT))
					// Fire leave message
					new LeaveController(lobby).leaveTable();
				else if (button.getText().equals(REQUEST_TEXT))
					// Fire join message
					new JoinController(lobby).join(id);
				else if (button.getText().equals(ACCEPT_TEXT))
					// Fire join message
					new JoinController(lobby).join(id);
				else
					// unhandled type of button text...
					System.err.println("unexpected string in button text '"+button.getText()+"' when firing action");
			}

		});
		// Add the "join" button to the GUI
		this.add(jb, gbc);
	}
	
	/**
	 * Refreshed the data displayed in the panel according to the
	 * current Table model
	 */
	public void refresh() {
		Table t = TableManager.instance().getTable(id);
		
		// Update labels based on number of players found at table
		getLabelByName(VARIANT_LABEL).setText(t.getGameVariant().name());
		int numPlayers = t.getPlayers().size();
		switch (numPlayers) {
			case 4:
				// Clear no labels
			case 3:
				getLabelByName(P4_LABEL).setText(EMPTY_TEXT);
			case 2:
				getLabelByName(P3_LABEL).setText(EMPTY_TEXT);
			case 1:
				getLabelByName(P2_LABEL).setText(EMPTY_TEXT);
			case 0:
				getLabelByName(MOD_LABEL).setText(EMPTY_TEXT);
		}
		switch (numPlayers) {
			case 4:
				getLabelByName(P4_LABEL).setText(t.getPlayers().get(3).getDisplayedName());
			case 3:
				getLabelByName(P3_LABEL).setText(t.getPlayers().get(2).getDisplayedName());
			case 2:
				getLabelByName(P2_LABEL).setText(t.getPlayers().get(1).getDisplayedName());
			case 1:
				getLabelByName(MOD_LABEL).setText(t.getModerator().getDisplayedName());
			case 0:
				// Empty table, so don't display any names
		}
	}

	/**
	 * Updates the text and enable-ment of the JOIN button given
	 * a TableStatus
	 * @param tableStatus
	 */
	void setButtonEnabled(boolean isEnabled) {
		JButton button = getButton();
		button.setEnabled(isEnabled);
	}
	

	/**
	 * Updates the text and enable-ment of the JOIN button given
	 * a TableStatus
	 * @param tableStatus
	 */
	void setButtonText(String updatedText) {
		JButton button = getButton();
		button.setText(updatedText);
	}
	
	
	/**
	 * gets a JLabel in this panel by name
	 * @param name
	 * @return
	 */
	private JLabel getLabelByName(String name) {
		for (Component c : this.getComponents()) {
			if (c.getName() != null
					&& c.getName().equals(name))
				return (JLabel)c;
		}
		return null;
	}
	
	/**
	 * gets the JButton (join, leave etc) in this panel
	 * @param name
	 * @return
	 */
	private JButton getButton() {
		for (Component c : this.getComponents()) {
			if (c.getClass().equals(JButton.class))
				return (JButton)c;
		}
		return null;
	}

	public void setToTableEmpty() {
		// TODO
	}

	public void setLobby(final ILobby lobby) {
		this.lobby = lobby;
	}
	
}
