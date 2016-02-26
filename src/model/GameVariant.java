package model;

public enum GameVariant {
    KLONDIKE("Klondike"),
    FREECELL("Freecell"),
    SPIDER("Spider"),
    GRANDFATHERCLOCK("Grandfather Clock"),
    PYRAMIDGAME("Pyramid Game"),
    IDIOT("Idiot"),
    BCASTLE("B-Castle"),
    FLOWERGARDEN("Flower Garden");
    
    private final String displayString;
    
    // Constructor setting the display string of the game variant
    GameVariant(String displayString) {
        this.displayString = displayString;
    }

    // Returns a nicely formatted display string of the game variant
    private String getDisplayString() {
        return displayString;
    }
};

