package controller;


import junit.framework.TestCase;
import ks.client.UserContext;
import ks.client.controllers.DisconnectController;
import ks.client.game.GameManager;
import ks.client.interfaces.IController;
import ks.client.ipc.MockServer;
import ks.client.lobby.LobbyFrame;
import ks.framework.common.Configure;
import ks.framework.common.Message;
import model.Player;
import model.Table;

import org.junit.Test;
import org.w3c.dom.Document;

import view.GameManagerGUI;
import view.TabbedLayoutGUI;
import view.TableManagerGUI;
import view.UserManagerGUI;

public class TestUpdateController extends TestCase {
    // host
    public static final String localhost = "localhost";
    
    // sample credentials (really meaningless in the testing case)
    public static final String player1 = "1";
    public static final String player2 = "2";
    public static final String password = "password";
    
    /** Constructed objects for this test case. */
    UserContext context;
    LobbyFrame lobby;
    MockServer mockServer;
    
    TableManager tm;
    UserManager um;
    Table table;
    
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
            
            // create client to connect
            context = new UserContext();  
            lobby = new LobbyFrame (context);
        
            context.setUser(player1);
            context.setPassword(password);
            context.setSelfRegister(false);
            
            mockServer = new MockServer(lobby);
            context.setClient(mockServer);
            
        } catch (Exception e) {
            fail ("Unable to setup Message tests.");
        }
        
        table = new Table(1);
        tm = TableManager.instance();
        Player p = new Player(Integer.parseInt(player1));
        tm.addPlayer(1, p);
        p.setTable(table.getID());
        table.setModerator(p);
        um = UserManager.instance();
        um.addPlayer(p);
        TabbedLayoutGUI tl = new TabbedLayoutGUI();
        TableManagerGUI tmgui = new TableManagerGUI();
        tmgui.setLobby(lobby);
        tl.setTablesPanel(tmgui);
        GameManagerGUI gmgui = new GameManagerGUI();
        gmgui.setLobby(lobby);
        gmgui.initializeGUI();
        tl.setGamePanel(gmgui);
        lobby.setTableManagerGUI(tl);
        lobby.setUserManagerGUI(new UserManagerGUI());
        
    }

    /**
     * tearDown() is executed by JUnit at the conclusion of each individual
     * test case.
     */
    protected void tearDown() {
        // the other way to leave is to manually invoke controller.
        assertTrue (new DisconnectController (lobby).process(context));
        
        waitASecond();
        mockServer.disconnect();
        
        lobby.setVisible(false);
        lobby.dispose();
        
        UserManager.inst = null;
        tm.instance = null;
        GameManager.instance().exitGameWindow();
        
    }
    
    @Test
    public void testUpdate() {
        assertEquals(table.getModerator().getId(), Integer.parseInt(player1));
        
        assertFalse(GameManager.instance().isGameActive());
        
        StartGameController sg = new StartGameController(lobby);
        
        // now have mockserver process response
        Message sendReq = sg.startGame(table.getID());
        mockServer.sendToServer(lobby, sendReq, (IController)sg);
        Message req = mockServer.firstRoundTripRequest();
        assertEquals("start", req.name);
        
        String xmlString = Message.responseHeader(true, req.id)
                + "<tableResponse>"
                + "<table id='1' seed='0' type='inPlay' game='nogame' full='false' moderator='1' options='time=300'>" 
                + "<player-id player='1' rating='0'/>"
                + "</table>" 
                + "</tableResponse>" 
                + "</response>";
        
        Document d = Message.construct (xmlString);
        Message resp = new Message(d);
        mockServer.process(resp);
        
        assertTrue(GameManager.instance().isGameActive());
        
        // Clear mock server
        mockServer.requests.clear();
        mockServer.messages.clear();
        mockServer.roundTrip.clear();
                
        UpdateController u = new UpdateController();
        // now have mockserver process response
        sendReq = u.update(lobby, table.getID(), 1, "undo=true", false);
        mockServer.sendToServer(lobby, sendReq, (IController)u);
        req = mockServer.firstRoundTripRequest();
        assertEquals("update", req.name);
        
        xmlString = Message.responseHeader(true, req.id)
                + "<updateResponse table='1' score='1' finished='false' player='1'>"
                + "</updateResponse>"
                + "</response>";
        
        d = Message.construct (xmlString);
        resp = new Message(d);
        mockServer.process(resp);

        assertEquals(1, GameManager.instance().getFrame().getTableNumber());
        
    }
    
    // helper function to sleep for a second.
    private void waitASecond() {
        // literally wait a second.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
        }
    }
}
