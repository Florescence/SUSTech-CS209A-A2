package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Room;
import cn.edu.sustech.cs209.chatting.common.User;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ArrayList<User> users;
    private int port;
    private ServerSocket ss;

    public Server(int port) throws Exception {
        users = new ArrayList<>();
        this.port = port;
        ss = new ServerSocket(port);
        System.out.println("Starting server");
    }

    public void startListen() throws Exception{
        while(true){
            Socket socket =ss.accept();
            User user = new User("", socket);
            users.add(user);
            ServerThread thread = new ServerThread(user, users);
            thread.start();
        }
    }

    public static void main(String[] args) {
        try{
            Server server = new Server(8080);
            server.startListen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
