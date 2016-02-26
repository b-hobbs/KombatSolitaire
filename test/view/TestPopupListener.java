package view;

import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.lobby.LobbyFrame;
import ks.server.ipc.Server;

public class TestPopupListener extends TestCase{
    UserManagerGUI umgui = new UserManagerGUI();
    UserContext context;
    LobbyFrame lobby;
    
    // random port 8000-10000 to avoid arbitrary conflicts
    int port;
    
    // host
    public static final String localhost = "localhost";
    
    // sample credentials (really meaningless in the testing case)
    public static final String user = "11323";
    public static final String password = "password";
    
    public void setUp(){
        // start server on a random port.
        port = (int) (8000 + Math.random()*2000);
        
        
        // Any non-standard controllers for server would need to be included here
        
        
        context = new UserContext();  // by default, localhost
        lobby = new LobbyFrame (context);
        lobby.setUserManagerGUI(new UserManagerGUI());
        context.setPort(port);
        context.setUser(user);
        context.setPassword(password);
        context.setSelfRegister(false);
    }
    public void testPopUpMenu(){
        
        PopupListener pop = new PopupListener(umgui.getJTable(), lobby);
        pop.createContextMenu(0, 0, "Name");
    }
}
