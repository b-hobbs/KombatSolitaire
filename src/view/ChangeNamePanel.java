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
import javax.swing.JTextField;

import controller.PlayerInfoController;

import ks.client.ipc.Client;

/**
 * Panel for allowing a player to change their name
 * Requires the user enter in their current password and the name
 * they would like.
 * See PlayerInfoController
 * @author bhobbs
 *
 */
public class ChangeNamePanel extends JPanel{
    //labels and fields for form
    JLabel warningLabel = new JLabel(); //display any form errors
    JLabel passwordLabel = new JLabel("Password:    ");
    JPasswordField passwordField = new JPasswordField(13);
    JLabel newNameLabel = new JLabel("New Name:   ");
    JTextField newNameField = new JTextField(13);
    JButton submit = new JButton("Submit");
    
    /**
     * Construct the form for changing a players name
     * @param pic
     */
    public ChangeNamePanel(final PlayerInfoController pic){
        
        submit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pic.process(ChangeNamePanel.this);
            }
        });
        Box inner = new Box(BoxLayout.Y_AXIS);
        Box row1 = new Box(BoxLayout.X_AXIS);
        Box row2 = new Box(BoxLayout.X_AXIS);
        
        //add components
        row1.add(passwordLabel);
        row1.add(passwordField);
        row2.add(newNameLabel);
        row2.add(newNameField);
        inner.add(row1);
        inner.add(row2);
        inner.add(submit);
        add(warningLabel);
        add(inner);
    }
    
    /**
     * Get the value of the newNameField
     */
    public String getName(){
        return newNameField.getText();
    }
    
    /**
     * Get the encrypted value of the password field
     * @return
     */
    public String getEncryptedPassword(){
        String password = extractPassword(passwordField);
        return password.equals("") ? password : Client.sha1(password);
    }
    
    /**
     * Gets the value of the password field
     * @param p
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
     *  A string containing an error message to be displayed to the user
     */
    public void setWarningLabel(String string) {
        warningLabel.setText(string);
        warningLabel.setForeground(Color.RED);
        warningLabel.setVisible(true);
    }

    /**
     * Set the text of the password field
     * @param s
     */
    public void setPasswordFieldText(String s) {
        passwordField.setText(s);
    }

    /**
     * Set the text of the new name field
     * @param s
     */
    public void setNewNameFieldText(String s) {
        newNameField.setText(s);
    }   
}
