package controller;

import org.w3c.dom.Document;

import ks.framework.common.Message;

public class TestTableEmpty {

    public static Message createResponse(String messageID, int tableID) {
        
        String cmd = Message.responseHeader(true, messageID) + "<tableEmpty table='" + tableID + "'/>";
        cmd += "</response>";
        Document d = Message.construct(cmd);
        Message m = new Message(d);
    
        return m;
    }
}
