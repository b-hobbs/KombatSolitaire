package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import controller.PlayerInfoController;

import ks.client.ipc.Client;

/**
 * Panel for allowing a player to change their password
 * See PlayerInfoController
 * @author bhobbs
 *
 */
public class ChangePasswordPanel extends JPanel{
	//create labels and fields for form
	JLabel warningLabel = new JLabel();
	JLabel passwordLabel = new JLabel("Current Password:");
	JPasswordField passwordField = new JPasswordField(13);
	JLabel newPasswordLabel = new JLabel("New Password:");
	JPasswordField newPasswordField = new JPasswordField(13);
	JLabel confirmNewPasswordLabel = new JLabel("Confirm New Password:");
	JPasswordField confirmNewPasswordField = new JPasswordField(13);
	JButton submit = new JButton("Submit");
	
	/**
	 * Constructs the form for changing a password
	 * @param pic
	 */
	public ChangePasswordPanel(final PlayerInfoController pic){
		Box inner = new Box(BoxLayout.Y_AXIS);
		Box row1 = new Box(BoxLayout.X_AXIS);
		Box row2 = new Box(BoxLayout.X_AXIS);
		Box row3 = new Box(BoxLayout.X_AXIS);
		
		row1.add(passwordLabel);
	
		row1.add(Box.createHorizontalStrut(40));
		row1.add(passwordField);
		row2.add(newPasswordLabel);
		
		row2.add(Box.createHorizontalStrut(58));
		row2.add(newPasswordField);
		row3.add(confirmNewPasswordLabel);
		
		row3.add(Box.createHorizontalStrut(10));
		row3.add(confirmNewPasswordField);
		
		//add components
		inner.add(row1);
		inner.add(row2);
		inner.add(row3);
		
		//process the inputs
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pic.process(ChangePasswordPanel.this);
			}
		});
		
		inner.add(submit);
		add(warningLabel);
		add(inner);
	}
	
	/**
	 * Get the encrypted input from the current password field
	 * @return
	 */
	public String getEncryptedCurrentPassword(){
		String password = extractPassword(passwordField);
		return password.equals("") ? password : Client.sha1(password);
	}
	
	/**
	 * Get the encrypted value of the new password
	 * @return
	 * 	Encrypted new password or null if new password and confirm password do not agree
	 */
	public String getEncryptedNewPassword(){
		if (doPasswordsMatch()) {
			String password = extractPassword(newPasswordField);
			return password.equals("") ? password : Client.sha1(password);
		}
		return null;
	}
	
	/**
	 * See if new password and new password confirmation agree
	 * @return
	 */
	public boolean doPasswordsMatch(){
		String password = extractPassword(newPasswordField);
		String confirmedPassword = extractPassword(confirmNewPasswordField);
		return password.equals(confirmedPassword);
	}
	
	/**
	 * Get the value of the field
	 * @param p
	 * 	A JpasswordField
	 * @return
	 */
	private String extractPassword(JPasswordField p) {
		char chars[] = p.getPassword();

		// eliminates from memory safely
		String password = new String("");
		for (char c : chars) {
			password = password + c;
		}
		for (int i = 0; i < chars.length; i++) {
			chars[i] = '\0';   // protect.
		}

		return password;
	}
	
	/**
	 * Reset the warnings
	 */
	public void resetWarningLabel(){
		warningLabel.setText("");
		warningLabel.setVisible(false);
	}
	
	/**
	 * Set the warning label
	 * @param string
	 * 	A string containing an error message to be displayed to the user
	 */	
	public void setWarningLabel(String string) {
		warningLabel.setText(string);
		warningLabel.setForeground(Color.RED);
		warningLabel.setVisible(true);
	}
	
	/**
	 * Set the text of the current password field
	 * @param s
	 */
	public void setPasswordFieldText(String s){
		passwordField.setText(s);
	}
	
	/**
	 * Set the text of the new desired password
	 * @param s
	 */
	public void setNewPasswordFieldText(String s){
		newPasswordField.setText(s);
	}
	
	/**
	 * Set the text of the new password confirmation
	 * @param s
	 */
	public void setConfirmPasswordFieldText(String s){
		confirmNewPasswordField.setText(s);
	}
}