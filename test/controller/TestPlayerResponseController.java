package controller;

import org.w3c.dom.Document;

import view.UserManagerGUI;

import model.Player;

import arch.ClientExtension;
import arch.LogoutExtension;
import arch.PlayerResponseExtension;
import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.controllers.ClientControllerChain;
import ks.client.controllers.ConnectController;
import ks.client.controllers.DisconnectController;
import ks.client.lobby.LobbyFrame;
import ks.client.processor.ClientProcessor;
import ks.framework.common.Configure;
import ks.framework.common.Message;
import ks.server.ipc.Server;

public class TestPlayerResponseController extends TestCase{
    // host
    public static final String localhost = "localhost";
    
    // sample credentials (really meaningless in the testing case)
    public static final String user = "11323";
    public static final String password = "password";
    
    /** Constructed objects for this test case. */
    Server server;
    UserContext context;
    LobbyFrame lobby;
    
    // random port 8000-10000 to avoid arbitrary conflicts
    int port;
    
    /**
     * setUp() method is executed by the JUnit framework prior to each 
     * test case.
     */
    protected void setUp() {
        // Determine the XML schema we are going to use
        try {
            Message.unconfigure();
            assertTrue (Configure.configure());
        } catch (Exception e) {
            fail ("Unable to setup Message tests.");
        }
        
        // start server on a random port.
        port = (int) (8000 + Math.random()*2000);
        server = new Server(port);
        
        // Any non-standard controllers for server would need to be included here
        
        assertTrue (server.activate());
        
        waitASecond();
        
        // Any non-standard controllers for client would need to be included here.
        // Specifically, the output response handler is non standard, so include
        // that one here.
        ClientControllerChain head = ClientProcessor.head();
        head.append(new ClientExtension());
        head.append(new LogoutExtension());
        head.append(new PlayerResponseExtension());
        
        // create client to connect
        context = new UserContext();  // by default, localhost
        lobby = new LobbyFrame (context);
        lobby.setVisible(true);
        lobby.setUserManagerGUI(new UserManagerGUI());
        context.setPort(port);
        context.setUser(user);
        context.setPassword(password);
        context.setSelfRegister(false);
        
        // connect client to server
        assertTrue (new ConnectController(lobby).process(context));
        
        // wait for things to settle down. As your test cases become more
        // complex, we may find it necessary to include additional waiting 
        // times.
        waitASecond();
    }

    /**
     * tearDown() is executed by JUnit at the conclusion of each individual
     * test case.
     */
    protected void tearDown() {
        // the other way to leave is to manually invoke controller.
        assertTrue (new DisconnectController (lobby).process(context));
        
        waitASecond();
        server.deactivate();
        
        lobby.setVisible(false);
        lobby.dispose();
        
        UserManager.inst = null;
    }
    
    // helper function to sleep for a second.
    private void waitASecond() {
        // literally wait a second.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
        }
    }
    /**
     * The actual test case
     */
    public void testPlayerResponse(){

        
        String cmd = "<response version='1.0' id='589a39591271844e3fbe32bbb9281ad9' success='true'>" +
        "<playerResponse>"+
        "<player player='1213' realName='George Heineman'>"+
        "<rating category='solitaire' value='229'/>"+
        "<rating category='wordsteal' value='1234'/>" +
        "</player>"+
        " <player player='1217'>"+
        "<rating category='solitaire' value='2983'/>"+
        "</player>"+
        "</playerResponse>"+
        "</response>";
        
        System.out.println(cmd);
        
        Document d = Message.construct(cmd);
        Message m = new Message(d);
        lobby.getContext().getClient().process(m);
        UserManager um = UserManager.instance();
        ((UserManagerGUI)lobby.getUserManagerGUI()).refresh();
        Player p1 = um.getPlayer(1213);
        Player p2 = um.getPlayer(1217);
        assertNotNull(p1);
        assertNotNull(p2);
        assertEquals("George Heineman", p1.getName());
        assertEquals(229, p1.getRating("solitaire"));
        assertEquals(1234, p1.getRating("wordsteal"));
        assertEquals("", p2.getName());
        assertEquals(2983, p2.getRating("solitaire"));
    }
}
