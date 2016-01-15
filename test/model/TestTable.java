package model;

import junit.framework.TestCase;

public class TestTable extends TestCase {
	
	// Test 
	public void testConstruction() {
		Table t = new Table(42);
		
		assertEquals(42, 	t.getID());
		assertEquals(0, 	t.getPlayers().size());
		assertEquals(null,	t.getModerator());
		assertEquals(0, 	t.getSeed());
		assertEquals(Table.DEFAULT_TIME_LIMIT, 	t.getTimeLimit());
		// this fails // assertEquals(null, 	t.getPlayerIDsArray()[0]);
		assertEquals(TableStatus.EMPTY, 		t.getTableStatus());
		assertEquals(TableVisibility.PUBLIC, 	t.getTableVisibility());
		assertEquals(GameVariant.KLONDIKE, 	t.getGameVariant());
	}
	
	
	public void testAddPlayer() {
		Table t = new Table(89);
		
		assertEquals(0, t.getPlayers().size());
		
		Player p = new Player(714);
		t.addPlayer(p);
		
		assertEquals(1, t.getPlayers().size());
		assertEquals(p, t.getPlayers().get(0));
		assertEquals(p, t.getModerator());
		assertEquals(TableStatus.AVAILABLE, t.getTableStatus());
	}
	
	public void testAddRemovePlayer() {
		Table t = new Table(1);
		
		// add 2 players
		Player p1 = new Player(1);
		t.addPlayer(p1);
		Player p2 = new Player(2);
		t.addPlayer(p2);
		
		assertEquals(2, t.getPlayers().size());
		assertEquals(p1, t.getPlayers().get(0));
		assertEquals(p2, t.getPlayers().get(1));
		
		t.removePlayer(p1);
		// p2 should shift down into p1's spot
		assertEquals(1, t.getPlayers().size());
		assertEquals(p2, t.getPlayers().get(0));
		assertEquals(p2, t.getModerator());
		
		// no players should be left
		t.removePlayer(p2);
		assertEquals(0, t.getPlayers().size());
		assertEquals(null, t.getModerator());
		assertEquals(TableStatus.EMPTY, t.getTableStatus());
	}
	
	public void testFullTable() {
		Table t = new Table(99);
		
		// add 4 players
		Player p1 = new Player(10);
		Player p2 = new Player(20);
		Player p3 = new Player(30);
		Player p4 = new Player(40);
		
		t.addPlayer(p1);
		t.addPlayer(p2);
		t.addPlayer(p3);
		t.addPlayer(p4);
		
		// Test getPlayerIDsArray
		int[] ids = {10, 20, 30, 40};
		int[] idsFromTable = t.getPlayerIDsArray();
		for (int i=0; i<4; i++) {
			assertEquals(ids[i], idsFromTable[i]);
		}
		
		assertEquals(TableStatus.FULL, t.getTableStatus());
		t.removePlayer(p1);
		assertEquals(p2, t.getModerator());
		assertEquals(TableStatus.AVAILABLE, t.getTableStatus());
		
		t.addPlayer(p1);
		t.removePlayer(p1);
		assertEquals(p2, t.getModerator());
		assertEquals(TableStatus.AVAILABLE, t.getTableStatus());
	}
	
	
}
