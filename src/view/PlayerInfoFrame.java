package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import controller.PlayerInfoController;

import ks.client.WindowManager;

/**
 * Frame for player options
 * Contains a TabbedPane with panels of different options 
 * see ChangeNamePanel, ChanePasswordPanel
 * @author bhobbs
 *
 */
public class PlayerInfoFrame extends JFrame {
	//controller that procceses the information from the user
	PlayerInfoController pic;
	
	//panels
	ChangeNamePanel changeName;
	ChangePasswordPanel changePassword;
	
	JTabbedPane tp;
	
	/**
	 * Contructs a JTabbed pane with different panels for changing options
	 * @param playerInfoController
	 */
	public PlayerInfoFrame(PlayerInfoController playerInfoController) {
		pic = playerInfoController;
		this.setTitle("Options");
		setSize(400,300);
		
		tp = new JTabbedPane();
		changeName = new ChangeNamePanel(pic);
		changePassword = new ChangePasswordPanel(pic);

		tp.addTab("Change Name", changeName);
		tp.addTab("Change Password", changePassword);
		add(tp);

		WindowManager.centerWindow(this);
		this.setVisible(true);
	}
	
	/**
	 * Get the panel for changing a name
	 * @return
	 */
	public ChangeNamePanel getChangeNamePanel(){
		return changeName;
	}
	
	/**
	 * Get the panel for changing a players password
	 * @return
	 */
	public ChangePasswordPanel getChangePasswordPanel(){
		return changePassword;
	}
	
	/**
	 * Get the tabbed pane
	 * @return
	 */
	public JTabbedPane getTabbedPane(){
		return tp;
	}
}