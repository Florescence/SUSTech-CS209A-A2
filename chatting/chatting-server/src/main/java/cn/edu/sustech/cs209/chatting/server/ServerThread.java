package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private User user;
    private ArrayList<User> users;
    private PrintWriter pw;

    public ServerThread(User user, ArrayList<User> users) {
        this.user = user;
        this.users = users;
        pw = null;
    }

    public void run() {
        try {
            while (true) {
                String msg = user.getBr().readLine();
                System.out.println(msg);
            }
        } catch (SocketException e) {
            System.out.println("user " + user.getName() + " log out.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                remove(user);
                user.getBr().close();
                user.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void remove(User user) {
        users.remove(user);
    }

    private void sendMsg(Message message) {
        for (String username : message.getSendTo()) {
            User u = User.getUserFromName(username);
            try {
                assert u != null;
                pw = u.getPw();
                pw.println(message.getMsg());
                pw.flush();
                System.out.println(message.getMsg());
            } catch (Exception e) {
                System.out.println("Exception occurs when " + message.getSentBy() + "sends message to " + message.getSendTo());
            }
        }


    }

}
