package controller;

import model.Player;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import view.UserManagerGUI;

import ks.client.interfaces.ILobby;
import ks.client.interfaces.IProcessClientMessage;
import ks.framework.common.Message;

/**
 * Controller to handle playerResponse responses from the server.
 * Extracts player info from the response to store client side.
 * @author bhobbs
 *
 */
public class PlayerResponseController implements IProcessClientMessage{

    UserManager um = UserManager.instance();

    /**
     * Extracts information about players from the message to 
     * store client side
     */
    @Override
    public boolean process(ILobby lobby, Message m) {
        Node info = m.contents();
        
        //get all players in the message
        NodeList players = info.getChildNodes();
        
        
        //for each player create a Player object and add it to the list of current players
        for(int i = 0; i < players.getLength(); i++){
            // Verify valid node type
            // e.g. not a "whitespace" node in the XML markup
            Node playerItem = players.item(i);
            // If not "element" type, skip to next node in the list
            if (playerItem.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Node playerNode = players.item(i);
            NamedNodeMap playerMap = playerNode.getAttributes();
            
            //get player id
            int playerID = Integer.parseInt(playerMap.getNamedItem("player").getNodeValue());
            
            //try to get the players name, they may not have one
            String realName = "";
            try{
                realName = playerMap.getNamedItem("realName").getNodeValue(); 
            }catch(Exception e){}
            
            
            //create player object with extracted information
            Player player = new Player(playerID, realName);
            
            NodeList ratings = playerNode.getChildNodes();
    
            //add users ratings to the player object
            for(int j = 0; j < ratings.getLength(); j++){
                // Verify valid node type
                // e.g. not a "whitespace" node in the XML markup
                Node ratingItem = ratings.item(j);
                // If not "element" type, skip to next node in the list
                if (ratingItem.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                
                Node rating = ratings.item(j);          
                NamedNodeMap ratingMap = rating.getAttributes();
                
                String category = ratingMap.getNamedItem("category").getNodeValue();
                int value = Integer.parseInt(ratingMap.getNamedItem("value").getNodeValue());
                player.addRating(category, value);
            }
            
            // add/update the player to the UserManager
            um.addPlayer(player);
        }
        
        //refresh the UserManagerGUI to display new or updated users
        ((UserManagerGUI)lobby.getUserManagerGUI()).refresh();
        return true;
    }
}
