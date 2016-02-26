package model;

import junit.framework.TestCase;

public class TestPlayer extends TestCase {

    public void testConstructor() {
        Player p = new Player(1);
        
        assertEquals(1,     p.getId());
        assertTrue(p.getName().equals(""));
        assertEquals(false, p.isAdmin());
        assertEquals(-1,    p.getTable());
        
        Player p2 = new Player(11, "Eleven");
        p2.setAdmin(true);
        assertEquals(11,        p2.getId());
        assertTrue(p2.getName().equals("Eleven"));
        assertEquals(true,  p2.isAdmin());
        assertEquals(-1,    p2.getTable());
    }
    
    public void testGetDisplayedName() {
        // No name set? Default to ID
        Player p = new Player(2);
        assertTrue(p.getDisplayedName().equals("2"));
        
        // Null name? Also use ID
        p.setName(null);
        assertTrue(p.getDisplayedName().equals("2"));
                
        // Return the string name now that we have one
        p.setName("Eugene");
        assertTrue(p.getDisplayedName().equals("Eugene"));
                
        // If the name is just whitespace, default back to the ID
        p.setName("             ");
        assertTrue(p.getDisplayedName().equals("2"));
    }

    public void testTableSetting() {
        // Default table "no table" is -1 by convention
        Player p = new Player(3);
        assertEquals(-1, p.getTable());
        
        // Set to a valid table and observe change
        p.setTable(20);
        assertEquals(20, p.getTable());
        
        // Do a manual reset of the table
        p.setTable(20);
        p.resetTable();
        assertEquals(-1, p.getTable());
        
        // Set back to valid and do an invalid table
        p.setTable(20);
        p.setTable(0);
        assertEquals(-1, p.getTable());
        
        // Set back to valid and do another invalid table
        p.setTable(20);
        p.setTable(-10);
        assertEquals(-1, p.getTable());
        
        // Set back to valid and do another invalid table
        p.setTable(20);
        p.setTable(101);
        assertEquals(-1, p.getTable());
        
    }
    
    public void testRatings(){
        Player p = new Player(4);
        assertTrue(p.ratings.size() == 0);
        
        //add one rating
        p.addRating("Solitaire", 1200);
        assertTrue(p.ratings.size() == 1);
        assertTrue(p.ratings.containsKey("Solitaire"));
        assertTrue(p.ratings.containsValue(1200));
        assertEquals(1200, p.getRating("Solitaire"));
        
        //add another rating
        p.addRating("Word Steal", 1500);
        assertTrue(p.ratings.size() == 2);
        assertTrue(p.ratings.containsKey("Solitaire"));
        assertTrue(p.ratings.containsValue(1200));
        assertEquals(1200, p.getRating("Solitaire"));
        assertTrue(p.ratings.containsKey("Word Steal"));
        assertTrue(p.ratings.containsValue(1500));
        assertEquals(1500, p.getRating("Word Steal"));
        
        //update a rating
        p.addRating("Solitaire", 1300);
        assertTrue(p.ratings.size() == 2);
        assertTrue(p.ratings.containsKey("Solitaire"));
        assertTrue(p.ratings.containsValue(1300));
        assertTrue(!p.ratings.containsValue(1200));
        assertEquals(1300, p.getRating("Solitaire"));
        
        String[] ratings = p.ratingsToStringArray();
        assertEquals(ratings.length, p.ratings.size());
        //NOTE: Removing ratings is not allowed
    }
}
