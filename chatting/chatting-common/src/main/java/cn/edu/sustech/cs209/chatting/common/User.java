package cn.edu.sustech.cs209.chatting.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class User {
    private static ArrayList<User> users;
    private String name;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;

    /**
     * @param name:                设置user的姓名
     * @param socket:保存用户连接的socket
     * @throws IOException
     */
    public User(String name, final Socket socket) throws IOException {
        this.name = name;
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        this.pw = new PrintWriter(socket.getOutputStream());
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public BufferedReader getBr() {
        return br;
    }
    public void setBr(BufferedReader br) {
        this.br = br;
    }
    public PrintWriter getPw() {
        return pw;
    }
    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }
    @Override
    public String toString() {
        return "#User." + name +  "<socket:" + socket + ">";
    }
    public static User getUserFromName(String name){
        for (User u: users){
            if (u.name.equals(name)) return u;
        }
        return null;
    }
}
