package controller;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ks.client.interfaces.IController;
import ks.client.interfaces.ILobby;
import ks.framework.common.Message;

/**
 * Controller to request a profile, a players ratings, from the server
 * @author bhobbs
 *
 */
public class ProfileController implements IController {
	//get an copy of the UserManager
	UserManager um = UserManager.instance();
	
	/** Needs to know about the lobby **/
	ILobby lobby;

	public ProfileController(ILobby lobby) {
		this.lobby = lobby;
	}

	/**
	 * Constructs a request to send to the server to obtain a player's ratings
	 * @param playerID
	 * 		Players unique ID number that you want to get ratings for
	 */
	public void getProfile(int playerID) {
		String cmd = Message.requestHeader() + "<getProfile player = '"
				+ playerID + "'/>";
		cmd += "</request>";

		Document doc = Message.construct(cmd);
		Message m = new Message(doc);
		lobby.getContext().getClient().sendToServer(lobby, m, this);
	}

	/**
	 * Handles a playerResponse from the result of a getProfile request
	 * to append a players ratings to the lobby of the originator of the 
	 * request
	 */
	@Override
	public void process(ILobby lobby, Message request, Message response) {
		//We expect the response to be a playerResponse
		if(!response.getName().equals("playerResponse")){
			lobby.append("Error retrieving profile");
			return;
		}
		Node info = response.contents();
		NodeList players = info.getChildNodes();

		//if there are no players in the response send error message
		if (players.getLength() < 1){
			lobby.append("Error retrieving profile");
			return;
		}
		
		//sift through player nodes to find a Node.Element_Node and not an empty node
		Node playerNode;
		int i = 0;
		do {
			playerNode = players.item(i);
			i++;
			if (i > players.getLength()) {
				lobby.append("Error retrieving profile");
				return;
			}
		} while (playerNode.getNodeType() != Node.ELEMENT_NODE);

		
		NamedNodeMap playerMap = playerNode.getAttributes();

		//get player id
		int playerID = Integer.parseInt(playerMap.getNamedItem("player")
				.getNodeValue());

		//try to get player's real name, they may not have one
		String realName = "";
		try {
			realName = playerMap.getNamedItem("realName").getNodeValue();
		} catch (Exception e) {
		}

		//append starting message with the player the profile belongs to
		lobby.append("Ratings for " + realName + "(" + playerID + "):");

		//get the players ratings
		NodeList ratings = playerNode.getChildNodes();

		if (ratings.getLength() == 0) {
			lobby.append("This user currently has no ratings.");
			return;
		} else {
			//a player may have more then one rating(one for each type of game)
			for (int j = 0; j < ratings.getLength(); j++) {
				// Verify valid node type
				// e.g. not a "whitespace" node in the XML markup
				Node ratingItem = ratings.item(j);
				// If not "element" type, skip to next node in the list
				if (ratingItem.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Node rating = ratings.item(j);
				NamedNodeMap ratingMap = rating.getAttributes();

				//get category and value
				String category = ratingMap.getNamedItem("category")
						.getNodeValue();
				int value = Integer.parseInt(ratingMap.getNamedItem("value")
						.getNodeValue());

				//append the category and value to the players lobby
				lobby.append("\t" + "Category: " + category + " " + value);
				
			}
		}
	}

}
