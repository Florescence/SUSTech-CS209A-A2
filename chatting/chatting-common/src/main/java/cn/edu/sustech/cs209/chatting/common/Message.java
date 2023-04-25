package cn.edu.sustech.cs209.chatting.common;

import java.util.ArrayList;

public class Message {
    private String sentBy; //user name

    private ArrayList<String> sendTo; //user name

    private String msg;

    public Message(String sentBy, ArrayList<String> sendTo, String data) {
        this.sentBy = sentBy;
        this.sendTo = sendTo;
        this.msg = data;
    }

    public String getSentBy() {
        return sentBy;
    }

    public ArrayList<String> getSendTo() {
        return sendTo;
    }

    public String getMsg() {
        return msg;
    }
}
