package controller;


import model.Player;
import model.Table;


import org.w3c.dom.Document;

import arch.InviteExtension;

import view.TabbedLayoutGUI;
import view.TableManagerGUI;
import view.UserManagerGUI;
import ks.LocalClientProcessor;
import ks.client.controllers.ClientControllerChain;
import ks.client.ipc.MockServer;
import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.lobby.LobbyFrame;
import ks.client.processor.ClientProcessor;
import ks.framework.common.Configure;
import ks.framework.common.Message;

/**
 * Test Case for Invite Controller
 * @author bhobbs
 *
 */
public class TestInviteController extends TestCase {

    // host
    public static final String localhost = "localhost";
    
    // sample credentials
    public static final String user = "11323";
    public static final String password = "password";
    
    // sample client front-end
    UserContext context;

    // GUI object
    LobbyFrame lobby;
    
    // mock server for the code
    MockServer mockServer;
    
    LocalClientProcessor lcp;
    
    Table table;
    TableManager tm;
    UserManager um;
    int tableID = 1;
    
    // prepare client front-end through a mock-server object
    protected void setUp() {
        // Determine the XML schema we are going to use
        try {
            Message.unconfigure();
            assertTrue (Configure.configure());
            
            // create client to connect
            context = new UserContext();  
            lobby = new LobbyFrame (context);
        
            context.setUser(user);
            context.setPassword(password);
            context.setSelfRegister(false);
            
            mockServer = new MockServer(lobby);
            context.setClient(mockServer);
            
            ClientProcessor cp = new ClientProcessor(lobby);
            ClientControllerChain head = cp.head();
            head.append(new InviteExtension());
            
            mockServer.setProcessor(cp);
            
            lcp = new LocalClientProcessor(lobby);
            context.getClient().setProcessor(lcp);
        } catch (Exception e) {
            fail ("Unable to setup Message tests.");
        }
        
        table = new Table(1);
        tm = TableManager.instance();
        Player p = new Player(390);
        Player p2 = new Player(Integer.parseInt(user));
        tm.addPlayer(1, p2);
        um = UserManager.instance();
        um.addPlayer(p);
        um.addPlayer(p2);
        TabbedLayoutGUI tl = new TabbedLayoutGUI();
        TableManagerGUI tmgui = new TableManagerGUI();
        tmgui.setLobby(lobby);
        tl.setTablesPanel(tmgui);
        lobby.setTableManagerGUI(tl);
        lobby.setUserManagerGUI(new UserManagerGUI());
    }

    protected void tearDown(){
        um.inst = null;
        tm.instance = null;
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
     * Test sending a succesful invite
     */
    public void testSendInvite() {
        
        InviteController invc = new InviteController(lobby);
        invc.process(Integer.parseInt(user), 390, tableID);
        
        // now have mockserver process response
        Message req = mockServer.firstRequest();
        assertEquals("invite", req.getName());
        assertEquals("390", req.getAttribute("player"));
        
        waitASecond();
    }
    
    /**
     * Test Receiving an invite
     */
    public void testReceiveInvite(){
        //construct invite request
        String xmlString = Message.requestHeader()
                + "<invite table='1' player='"+ user + "' />"
                + "</request>";
        
        Document d = Message.construct (xmlString);
        Message resp = new Message(d);
        
        //process the request
        mockServer.process(resp);
        
        //player should have an invite
        assertTrue(lcp.hasMessage());
    }
    
    /**
     * Test sending an invite when not a moderator.
     */
    public void testSendInviteNotModerator() {
        //set moderator to another user
        tm.getTable(1).setModerator(new Player(1));
        InviteController invc = new InviteController(lobby);
        
        //process invite
        invc.process(Integer.parseInt(user), 390, tableID);
        
        //make sure the server has no request
        Message req;
        assertNull(req = mockServer.firstRequest());
        
        waitASecond();
    }
}
