package controller;

import ks.framework.common.Message;

import org.w3c.dom.Document;

public class TestTableResponseShared {

    public static Message createResponse(String messageID, int tableID, int seed, int playerID, String game_name){
                    
        String cmd = Message.responseHeader(true, messageID) + "<tableResponse><table id='" + tableID + "' seed='" + seed + "' type='public' full='false' moderator='";
        cmd += playerID + "' game='"+ game_name + "' options='undo=true,newHand=true,time=500'><player-id player='" + playerID + "'/></table></tableResponse>";
        cmd += "</response>";
        Document d = Message.construct(cmd);
        Message m = new Message(d);
    
        return m;
    
    }
    
    public static Message createFalseResponse(String messageID, int tableID, int seed, int playerID, String game_name){
        
        String cmd = Message.responseHeader(false, messageID) + "<tableResponse> <table id='" + tableID + "' seed='" + seed + "' type='public' full='false' moderator='";
        cmd += playerID + "' game='"+ game_name + "' options='undo=true,newHand=true,time=500'><player-id player='" + playerID + "'/></table></tableResponse>";
        cmd += "</response>";
        Document d = Message.construct(cmd);
        Message m = new Message(d);
    
        return m;
    }
    
    public static Message createSpaceResponse(String messageID, int tableID, int seed, int playerID, String game_name){
        
        String cmd = Message.responseHeader(true, messageID) + "<tableResponse>       <table id='" + tableID + "' seed='" + seed + "' type='public' full='false' moderator='";
        cmd += playerID + "' game='"+ game_name + "' options='undo=true,newHand=true,time=500'>        <player-id player='" + playerID + "'/></table></tableResponse>";
        cmd += "</response>";
        Document d = Message.construct(cmd);
        Message m = new Message(d);
        
        return m;
    }
}
