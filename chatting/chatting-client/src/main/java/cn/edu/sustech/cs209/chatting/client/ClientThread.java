package cn.edu.sustech.cs209.chatting.client;

import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientThread extends Thread {
    private Socket socket;
    private Client client;
    private BufferedReader br;
    private PrintWriter pw;

    public ClientThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Failed to get input stream from socket.");
        }
    }

    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String msg = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseMessage(String message) throws IOException {
        String code = null;
        String msg = null;
        /*
         * 先用正则表达式匹配code码和msg内容
         */
        if (message.length() > 0) {
            Pattern pattern = Pattern.compile("<code>(.*)</code>");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                code = matcher.group(1);
            }
            pattern = Pattern.compile("<msg>(.*)</msg>");
            matcher = pattern.matcher(message);
            if (matcher.find()) {
                msg = matcher.group(1);
            }
            System.out.println(code + ":" + msg);
            switch (code) {
                case "1": /*一个普通消息处理*/
                    client.updateTextArea(new TextArea(), msg);
                    break;
                case "2": /*退出消息*/
                    client.showEscDialog(msg);
                    break;
                case "3": /*更新消息*/
                    client.updateTextAreaFromUser(msg);
                    break;
                case "4": /*添加用户*/
                    client.addUser(msg);
                    break;
                case "5": /*删除用户*/
                    client.deleteUser(msg);
                    break;
                case "6": /*列出用户列表*/
                    client.listUsers(msg);
                    break;
                case "7":
                    client.addRoom(msg);
                case "8":
                    client.delRoom(msg);
                case "9":
                    client.listRooms(msg);
            }
        }

    }

}
