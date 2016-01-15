package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.w3c.dom.Document;

import ks.client.interfaces.ILobby;
import ks.framework.common.Message;
import model.GameVariant;
import model.Player;
import model.Table;
import model.TableVisibility;
import controller.ConfirmResponseController;
import controller.KickController;
import controller.LeaveController;
import controller.SetOptionsController;
import controller.StartGameController;
import controller.TableManager;
import controller.UserManager;

public class GameManagerGUI extends JPanel {

	ILobby lobby;
	TabbedLayoutGUI tabbedLayout;
	int tableID;
	
	/**
	 * Default constructor and initialization for game manager
	 * GUI and contained elements
	 */
	public GameManagerGUI() {
		super();
	}

	public void initializeGUI() {
		setLayout(new FlowLayout());
		// TODO: Make it green and cool
		// Set a 2-pixel space as "internal padding" on all sides
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setBackground(Color.GREEN);
		
		// Who is the user--are they the moderator?
		int myID = Integer.parseInt(lobby.getContext().getUser());
		int myTable = UserManager.instance().getPlayer(myID).getTable();
		int modID = TableManager.instance().getTable(myTable).getModerator().getId();
		
		// Everyone gets a leave button
		addLeaveButton();
		
		// Initialize different moderator GUI versions if moderator or non-moderator
		if (myTable>0) {
			if (myID == modID)
				initializeModeratorGUI(myTable);
			else
				initializeNonModeratorGUI(myTable);
		}
		else {
			System.err.println("Attempted to initialize GameManagerGUI for invalid table: " + myTable);
			return;
		}
		
	}

	/**
	 * Button for leaving the table
	 */
	private void addLeaveButton() {
		JButton leaveButton = new JButton("Leave Table");
		leaveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//JButton button = (JButton)e.getSource();
				new LeaveController(lobby).leaveTable();
			}
		});
		add(leaveButton);
	}

	/**
	 * Initialize the game panel as a non-moderator
	 * @param myTable
	 */
	private void initializeNonModeratorGUI(int myTable) {
		add(new JLabel("Non-moderator"));
		
		Table table = TableManager.instance().getTable(myTable);
		
		add(new JLabel ("Game type: " + table.getGameVariant()));
		
		add(new JLabel ("Table type: " + table.getTableVisibility()));
		
		add(new JLabel("Time limit: " + table.getTimeLimit()));
		
		ArrayList<Player> players = table.getPlayers();
		
		String playersString = " ";
		
		for(int i = 0; i < players.size(); i++){
			playersString += "Player " + (i+1) + "- " + players.get(i).getId() + " \n";
		}
		
		add(new JLabel ("Players: " + playersString));
	}

	/**
	 * Initialize the game panel as a moderator
	 * @param myTable
	 */
	private void initializeModeratorGUI(int myTable) {
		
		Table table = TableManager.instance().getTable(myTable);
		
		add(new JLabel("Moderator"));
		
		addGameVariantSelector(table.getGameVariant());
		
		addTableTypeSelector(table.getTableVisibility());
		
		add(new JLabel("Time Limit:"));
		
		addTimeSelector(table.getTimeLimit());
		
		ArrayList<Player> players = table.getPlayers();
		
		String playersString = " ";
		
		for(int i = 0; i < players.size(); i++){
			playersString += "Player " + (i+1) + "- " + players.get(i).getId() + " \n";
			//put kick buttons next to non-moderator players
			if (i>0){
				addKickButton(i+1);
			}
		}
		
		add(new JLabel ("Players: " + playersString));
		
		addStartButton();
		
	}

	/**
	 * Button for kicking a player off
	 * @param kId
	 */
	private void addKickButton(final int kId){
		JButton kickButton = new JButton("Kick Player " + kId);
		kickButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new KickController(lobby).kick(kId);
			}
		});
		add(kickButton);
	}
	
	/**
	 * Button for starting a game
	 */
	private void addStartButton() {
		JButton startButton = new JButton("Start!");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//JButton button = (JButton)e.getSource();
				new StartGameController(lobby).startGame(tableID);
			}
		});
		add(startButton);
		
	}

	/**
	 * Select the type of game
	 * @param currentVar
	 */
	private void addGameVariantSelector(GameVariant currentVar) {
		
		// TODO Auto-generated method stub
		//JComboBox variantSelector = new JComboBox(GameVariant.getVariantStringList());
		JComboBox variantSelector = new JComboBox(GameVariant.values());
		variantSelector.setSelectedItem(currentVar);
		variantSelector.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				//String variantName = (String)cb.getSelectedItem();
				//GameVariant newVariant = GameVariant.valueOf(variantName);
				GameVariant newVariant = (GameVariant) cb.getSelectedItem();
				TableManager.instance().getTable(tableID).setGameVariant(newVariant);
				// Fire game options request with updated variant string
				new SetOptionsController(lobby).setOptions(tableID, newVariant);
			}
		});
		add(variantSelector);
	}
	
	/**
	 * Select the table type (public, private, byInvitation)
	 * @param currVis
	 */
	private void addTableTypeSelector(TableVisibility currVis) {
		JComboBox typeSelector = new JComboBox(TableVisibility.values());
		typeSelector.setSelectedItem(currVis);
		typeSelector.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				JComboBox cmb = (JComboBox)e.getSource();
				TableVisibility newVisibility = (TableVisibility) cmb.getSelectedItem();
				TableManager.instance().getTable(tableID).setTableVisibility(newVisibility);
				//new SetOptionsController(lobby).setOptions(tableID, "noOptions", newVisibility, GameVariant.KLONDIKE);
				new SetOptionsController(lobby).setOptions(tableID, newVisibility);
			}
		});
		add(typeSelector);
	}
	
	/**
	 * Select the game time by typing it in the box and pressing enter
	 * @param currTime
	 */
	private void addTimeSelector(int currTime){
		//TODO: make it a submit button?
		
		//amount of time must be a number
		NumberFormat timeFormat = NumberFormat.getNumberInstance();
		
		JFormattedTextField timeSelector = new JFormattedTextField(timeFormat);
		timeSelector.setValue(currTime);
		timeSelector.setColumns(5);
		
		timeSelector.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFormattedTextField source = (JFormattedTextField) e.getSource();
		        Object value = source.getValue();
		        System.out.println("value is " + value);
		        //new SetOptionsController(lobby).setOptions(tableID, "time= " + value, TableVisibility.PUBLIC, GameVariant.KLONDIKE);
		        new SetOptionsController(lobby).setOptions(tableID, "time="+ value);
			}
			
		});
		
		add(timeSelector);
		
	}
	
	/**
	 * Confirm this player's request to join the table
	 * @param requesterId
	 * @param tableId
	 * @param messageId
	 */
	public void addConfirmButton(final int requesterId, final int tableId, final String messageId){
		
		final JLabel reqLabel = new JLabel ("Player " + requesterId + " would like to join");
		
		add (reqLabel);
		
		final JButton confirmButton = new JButton("Confirm Player " + requesterId);
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				lobby.append("confirmed");
				new ConfirmResponseController(lobby).confirmTrue(requesterId, tableId, messageId);
			}
		});
		add(confirmButton);
		
		final JButton rejectButton = new JButton("Reject Player " + requesterId);
		rejectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				lobby.append("rejected");
				
				//send a confirm response with "false" to the server
				new ConfirmResponseController(lobby).confirmFalse(requesterId, tableId, messageId);
				
				//remove these things
				remove(reqLabel);
				remove(confirmButton);
				remove(rejectButton);
				repaint();

			}
		});
		add(rejectButton);
	}
	
	/**
	 * Refreshes the game manager GUI elements
	 */
	public void refresh() {
		// Are you a moderator now? 
		int myID = Integer.parseInt(lobby.getContext().getUser());
		int myTable = UserManager.instance().getPlayer(myID).getTable();
		int modID = TableManager.instance().getTable(myTable).getModerator().getId();
		
		//remove the old elements
		removeAll();
		
		//put the leave button back
		addLeaveButton();
		
		//if you're the moderator now, get a moderator GUI
		if (myID == modID){
			initializeModeratorGUI(myTable);
		
			//if there are any pending requests, put them back in the GUI
			ArrayList<Player> requesters = TableManager.instance().getTable(myTable).getRequested();
			ArrayList<String> requesterIds = TableManager.instance().getTable(myTable).getReqIds();
			if(!requesters.isEmpty()){
				Iterator<Player> it = requesters.iterator();
				Iterator<String> idIt = requesterIds.iterator();
				while(it.hasNext()){
					addConfirmButton(it.next().getId(), myTable, idIt.next());
				}
			} 
		}
		//otherwise, update as non-moderator
		else
			initializeNonModeratorGUI(myTable);
		
		repaint();
	}
	
	public void setLobby(ILobby lobby) {
		this.lobby = lobby;
		// Provide lobby reference to each component "thing"...
			// someComponent.setLobby(lobby);
		// Store reference to the table ID...
		this.tableID = UserManager.instance().getPlayer(
				Integer.parseInt(lobby.getContext().getUser())).getTable();
	}

	public void setTabbedLayout(TabbedLayoutGUI tlg) {
		this.tabbedLayout = tlg;
		
	}
}
