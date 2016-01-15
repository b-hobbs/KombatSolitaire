package controller;

import java.util.LinkedList;

import model.Player;
import junit.framework.TestCase;

public class TestUserManager extends TestCase{
	
	public void tearDown(){
		UserManager.inst = null;
	}
	
	public void testConstructor(){
		//test singleton
		UserManager um = new UserManager();
		//assertTrue(um.inst == null);
		
		um = um.instance();
		assertTrue(um.inst != null);
		
		UserManager um2 = UserManager.instance();
		assertEquals(um, um2);
	}
	
	public void testPlayers(){
		UserManager um = UserManager.instance();
		
		//add and remove one player
		Player p = new Player(1);
		um.addPlayer(p);
		assertEquals(1, um.getNumberOfPlayers());
		assertEquals(p, um.getPlayer(1));
		um.removePlayer(1);
		assertEquals(0, um.getNumberOfPlayers());
		
		//remove player when no players exists
		um.removePlayer(1);
		assertEquals(0, um.getNumberOfPlayers());
		
		//add two players, remove a non-existent player, and remove both players
		Player p1 = new Player(1);
		Player p2 = new Player(2);
		um.addPlayer(p1);
		um.addPlayer(p2);
		assertEquals(2, um.getNumberOfPlayers());
		assertNotNull(um.getPlayer(1));
		assertNotNull(um.getPlayer(2));
		um.removePlayer(3); //remove non-existing player
		assertEquals(2, um.getNumberOfPlayers()); //no player should have been removed
		um.removePlayer(1);
		assertEquals(1, um.getNumberOfPlayers());
		assertNull(um.getPlayer(1));
		um.removePlayer(p2);
		assertEquals(0, um.getNumberOfPlayers());
		assertNull(um.getPlayer(2));
	}
	
	public void testGetUserManagerData(){
		UserManager um = UserManager.instance();
		
		//create players
		Player p1 = new Player(1);
		Player p2 = new Player(2, "Joe");
		Player p3 = new Player(3);
		p3.setTable(2);
		Player p4 = new Player(4, "Bob");
		p4.setTable(3);
		
		//add players
		um.addPlayer(p1);
		um.addPlayer(p2);
		um.addPlayer(p3);
		um.addPlayer(p4);
		
		//Make sure we get the desired information
		Object[][] data = um.getUserManagerData();
		assertEquals(1, data[0][0]);
		assertEquals("", data[0][1]);
		assertEquals("-", data[0][2]);
		assertEquals(false, data[0][3]);
		assertEquals(2, data[1][0]);
		assertEquals("Joe", data[1][1]);
		assertEquals("-", data[1][2]);
		assertEquals(false, data[1][3]);
		assertEquals(3, data[2][0]);
		assertEquals("", data[2][1]);
		assertEquals(2, data[2][2]);
		assertEquals(false, data[2][3]);
		assertEquals(4, data[3][0]);
		assertEquals("Bob", data[3][1]);
		assertEquals(3, data[3][2]);
		assertEquals(false, data[3][3]);	
		
		//remove players
		um.removePlayer(p1);
		um.removePlayer(p2);
		um.removePlayer(p3);
		um.removePlayer(p4);
		assertEquals(0, um.getNumberOfPlayers());
	}
	
	public void testSetTableEmpty(){
		UserManager um = UserManager.instance();
		
		//create players
		Player p1 = new Player(1);
		p1.setTable(1);
		Player p2 = new Player(2);
		p2.setTable(1);
		Player p3 = new Player(3);
		p3.setTable(2);
		
		//add players
		um.addPlayer(p1);
		um.addPlayer(p2);
		um.addPlayer(p3);
		
		//set table 1 empty
		um.setTableEmpty(1);
		for(Player p: um.players.values())
			assertTrue(p.getTable() != 1);
		
		assertEquals(2, um.getPlayer(3).getTable()); //p3 is still on table 2
		
		//set table 3 empty, doesn't exist
		um.setTableEmpty(3);
		assertEquals(-1, p1.getTable());
		assertEquals(-1, p2.getTable());
		assertEquals(2, p3.getTable());
		assertEquals(3, um.getNumberOfPlayers());
		
		//set table 2 empty
		um.setTableEmpty(2);
		for(Player p: um.players.values())
			assertEquals(-1, p.getTable());
		
		//remove players
		um.removePlayer(p1);
		um.removePlayer(2);
		um.removePlayer(p3);
		
		assertEquals(0, um.getNumberOfPlayers());
	}
	
	public void testUpdateTableColumn(){
		UserManager um = UserManager.instance();
		
		//create players
		Player p1 = new Player(1);
		p1.setTable(1);
		Player p2 = new Player(2);
		p2.setTable(1);
		Player p3 = new Player(3);
		p3.setTable(2);
		
		//add players
		um.addPlayer(p1);
		um.addPlayer(p2);
		um.addPlayer(p3);
		
		assertEquals(3, um.getNumberOfPlayers());
		
		//Indicate that only player 1 is on table 1, removes player 2
		LinkedList<Integer> ll = new LinkedList<Integer>();
		ll.add(1);
		um.updateTableColumn(1, ll); //should remove player 2 from table 1
		assertEquals(-1, p2.getTable());
		assertEquals(1, p1.getTable());
		assertEquals(2, p3.getTable());
		assertEquals(3, um.getNumberOfPlayers());
		
		//add all players to table 5
		ll.remove();
		ll.add(1);
		ll.add(2);
		ll.add(3);
		um.updateTableColumn(5, ll);
		assertEquals(5, p1.getTable());
		assertEquals(5, p2.getTable());
		assertEquals(5, p3.getTable());
	
		//remove players
		um.removePlayer(p1);
		um.removePlayer(p2);
		um.removePlayer(p3);
	}
	
	public void testChatScope(){
		UserManager um = UserManager.instance();
		
		//create players
		Player p1 = new Player(1);
		Player p2 = new Player(2);
		Player p3 = new Player(3);
		
		//add players
		um.addPlayer(p1);
		um.addPlayer(p2);
		um.addPlayer(p3);
		
		//change chat scope to only player 1
		assertFalse(um.hasPrivateChatScope());
		um.changePrivateChatScope(1, true);
		assertTrue(um.hasPrivateChatScope());
		assertTrue(um.getSelectedPrivateChat(1));
		assertFalse(um.getSelectedPrivateChat(2));
		assertFalse(um.getSelectedPrivateChat(3));
		
		LinkedList ll = new LinkedList();
		ll = um.getPrivateChatScope();
		assertNotNull(ll);
		assertEquals(1, ll.size());
		assertTrue(ll.contains(1));
		
		//add player 2 to chat scope
		um.changePrivateChatScope(2, true);
		assertTrue(um.getSelectedPrivateChat(1));
		assertTrue(um.getSelectedPrivateChat(2));
		assertFalse(um.getSelectedPrivateChat(3));
		ll = um.getPrivateChatScope();
		assertEquals(2, ll.size());
		assertTrue(ll.contains(1));
		assertTrue(ll.contains(2));
		
		//remove player 2 from chat scope
		um.changePrivateChatScope(2, false);
		assertTrue(um.getSelectedPrivateChat(1));
		assertFalse(um.getSelectedPrivateChat(2));
		assertFalse(um.getSelectedPrivateChat(3));
		ll = um.getPrivateChatScope();
		assertEquals(1, ll.size());
		assertTrue(ll.contains(1));
		assertFalse(ll.contains(2));
		
		//test reset chat scope (removes all users)
		um.resetPrivateChatScope();
		assertEquals(0, um.getPrivateChatScope().size());
		um.changePrivateChatScope(2, true);
		um.changePrivateChatScope(1, true);
		assertEquals(2, um.getPrivateChatScope().size());
		um.resetPrivateChatScope();
		assertEquals(0, um.getPrivateChatScope().size());
		
		//test changing the chat scope to one person when multiple are selected
		um.changePrivateChatScope(2, true);
		um.changePrivateChatScope(1, true);
		assertEquals(2, um.getPrivateChatScope().size());
		um.privateChatTo(3);
		assertEquals(1, um.getPrivateChatScope().size());
		assertTrue(um.getPrivateChatScope().contains(3));
		
		//remove players
		um.removePlayer(1);
		um.removePlayer(2);
		um.removePlayer(3);
	}
	
	
}
