package controller;

import org.w3c.dom.Document;
import view.ChangeNamePanel;
import view.ChangePasswordPanel;
import view.PlayerInfoFrame;
import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

/**
 * Controller for changing a players name or password,
 * uses playerInfo xml message
 * @author bhobbs
 *
 */
public class PlayerInfoController implements IController{
	ILobby lobby;
	static PlayerInfoFrame pic = null;
	
	public PlayerInfoController(ILobby lobby){
		this.lobby = lobby;
	}
	
	/**
	 * Displays the options frame
	 */
	public void displayOptions() {
		pic = new PlayerInfoFrame(this);
	}
	
	/**
	 * Constructs a playerInfo request for changing a users name
	 * and sends it to the server to be processed
	 * @param cnp
	 * 		ChangeNamePanel
	 */
	public void process(ChangeNamePanel cnp){
		if(cnp == null || pic == null) return;
		cnp.resetWarningLabel(); //Reset any warnings
		
		String password = cnp.getEncryptedPassword();
		String name = cnp.getName();
		
		//Make sure there are no empty fields
		if(name.equals("") || password.equals("")){
			cnp.setWarningLabel("Password or Name cannot be empty.");
			return;
		}
		
		//construct and send the playerInfo request
		String cmd = Message.requestHeader();
		cmd += "<playerInfo password='" + password + "' new-realName='" + name + "' />";
		cmd += "</request>";
		Document d = Message.construct(cmd);
		Message m = new Message(d);
		
		lobby.getContext().getClient().sendToServer(lobby, m, this);
		lobby.append("Proccessing request to change name.");
		
		//Close the options frame
		pic.dispose();
	}

	/**
	 * Constructs a playerInfo request for changing a users password
	 * and sends it to the server to be processed
	 * @param cpp
	 * 	ChangePasswordPanel
	 */
	public void process(ChangePasswordPanel cpp){
		if(cpp == null || pic == null) return;
		cpp.resetWarningLabel(); 
		
		String currentPassword = cpp.getEncryptedCurrentPassword();
		
		//get the newPassword, if the new password and confirm password field 
		//don't match, newPassword will equal null
		String newPassword = cpp.getEncryptedNewPassword();
		
		
		if(newPassword == null){
			cpp.setWarningLabel("New Password and Password confirmation do not match");
			return;
		}else if(currentPassword.equals("") || newPassword.equals("")){
			cpp.setWarningLabel("Fields cannot be empty");
			return;
		}
		
		//construct and send playerInfo request
		String cmd = Message.requestHeader();
		cmd += "<playerInfo password='" + currentPassword + "' new-password='" + newPassword + "' />";
		cmd += "</request>";
		Document d = Message.construct(cmd);
		Message m = new Message(d);
		
		lobby.getContext().getClient().sendToServer(lobby, m, this);
		lobby.append("Proccessing request to change password.");
		
		//close the options frame
		pic.dispose();	
	}

	/**
	 * Handles a response from the server for a playerInfo request
	 */
	@Override
	public void process(ILobby lobby, Message request, Message response) {
		//if we received a playerResponse from the server then the playerInfo request was successful
		//otherwise, we receive an output from the server with an error message
		
		if(response.getName().equals("playerResponse")){
			//indicate to the user that their request was successful
			lobby.append("Information successfully changed.");
		}
		
		//throw the response to the client chain to process the playerResponse or output
		lobby.getContext().getClient().process(response);
	}
}
