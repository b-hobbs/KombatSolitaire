package controller;

import model.Player;

import org.w3c.dom.Document;

import view.UserManagerGUI;

import arch.ServerExtension;


import junit.framework.TestCase;
import ks.LocalClientProcessor;
import ks.client.UserContext;
import ks.client.controllers.ConnectController;
import ks.client.controllers.DisconnectController;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Configure;
import ks.framework.common.Message;
import ks.framework.communicator.SampleOutput;
import ks.server.controllers.ServerControllerChain;
import ks.server.ipc.Server;
import ks.server.processor.ServerProcessor;

/**
 * 
 */
public class TestProcessLobbyInputController extends TestCase {

    // host
    public static final String localhost = "localhost";
    
    // sample credentials
    public static final String user = "11324";
    public static final String password = "password";
    
    /** Constructed objects for this test case. */
    Server server;
    UserContext context;
    LobbyFrame lobby;
    LocalClientProcessor lcp;
    UserManager um;
    UserManagerGUI umgui;
    // random port 8000-10000 to avoid arbitrary conflicts
    int port;
    
    protected void setUp() {
        // Determine the XML schema we are going to use
        try {
            assertTrue (Configure.configure());
            
            // validate a simple tables
            String s = Message.requestHeader() + "<tables/></request>";
            Document d = Message.construct(s);
            assertTrue (d != null);
            
        } catch (Exception e) {
            fail ("Unable to setup Message tests.");
        }
        
        port = (int) (8000 + Math.random()*2000);
        server = new Server(port);
        
        // Now that CHAT is not standard, we have to add in its controller
        // for processing.
        ServerControllerChain head = ServerProcessor.head();
        head.append(new ServerExtension());
        
        assertTrue (server.activate());
        
        waitASecond();
        
        // create client to connect
        context = new UserContext();  // by default, localhost
        lobby = new LobbyFrame (context);
        umgui = new UserManagerGUI();
        lobby.setUserManagerGUI(umgui);
        lobby.setVisible(true);
        
        context.setPort(port);
        context.setUser(user);
        context.setPassword(password);
        context.setSelfRegister(false);
        
        assertTrue (new ConnectController(lobby).process(context));
        
        waitASecond();
        
        // now 'hook' in a new processor to validate messages are being
        // properly received here.
        lcp = new LocalClientProcessor(lobby);
        context.getClient().setProcessor(lcp);
        
        um = UserManager.instance();
    }

    @Override
    protected void tearDown() {
        // the other way to leave is to manually invoke controller.
        assertTrue (new DisconnectController (lobby).process(context));
        
        waitASecond();
        server.deactivate();
        
        lobby.setVisible(false);
        lobby.dispose();
        
        //set user manager to null
        um.inst = null;
    }
    
    private void waitASecond() {
        // literally wait a second.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
        }
    }
    
    // Note that all test cases now can focus on the sending of a message
    // and the proper receipt of actual request(s) from the server. 
    // Still doesn't handle broadcast well, however.
    public void testClient() {

        /*
         * Test Public Chat
         */
        
        // another "client" connected to the same server
        SampleOutput sample = new SampleOutput();
        server.connectUser("1133", sample);
        um.addPlayer(new Player(1133));
        um.addPlayer(new Player(Integer.parseInt(user)));
        umgui.refresh();
        
        // validate that lobby input is working as expected
        ProcessLobbyInputController plic = new ProcessLobbyInputController(lobby);
        
        // try to send chat message.
        plic.process("Hey there");
        
        waitASecond();
        
        // WE should have no message because chat doesn't come back to owner
        assertFalse (lcp.hasMessage());
        
        // however user 1133 should have seen it
        boolean hasObj = sample.hasObject();
        assertTrue (hasObj);
        Message m = (Message) sample.readObject();
        assertEquals ("output", m.getName());
        
        /*
         * Test Private chat
         */
        
        //another "client" connected to the same server
        SampleOutput sample2 = new SampleOutput();
        server.connectUser("1134", sample2);
        um.addPlayer(new Player(1134));
        umgui.refresh();
        
        sample.clear();
        
        //set our privateChatScope to player 1133
        umgui.getJTable().setValueAt(true, 1, 3);
        
        //send chat message
        plic.process("Private Message");
        waitASecond();
        //we should have no message
        assertFalse(lcp.hasMessage());
        
        //player 1133 should receive the private message
        boolean hasObject1 = sample.hasObject();
        assertTrue(hasObject1);
        Message msg1 = (Message) sample.readObject();
        assertEquals("output", msg1.getName());
        
        //player 1134 should have not received a message
        assertFalse(sample2.hasObject());   
    }   
}
