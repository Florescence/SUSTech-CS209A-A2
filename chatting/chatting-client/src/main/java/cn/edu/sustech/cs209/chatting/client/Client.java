package cn.edu.sustech.cs209.chatting.client;
import cn.edu.sustech.cs209.chatting.common.User;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client extends Application {
    private ArrayList<User> users = new ArrayList<>();

    public HashMap<String, Integer> rooms_map;
    public ListView<String> rooms_model;
    private Stage stage;
    private Scene scene;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private User user;
    private String name;
    private BorderPane borderPane;
    private HBox infoPanel;
    private VBox userListPanel;
    private ListView<String> userList;
    private HBox textPanel;
    private VBox chatBoard;
    private TextField IPTextField;
    private TextField portTextField;
    private Button emojiButton;
    private Button sendButton;
    private TextField textField;

    private TextArea textArea;
    private Button chatButton;
    private ObservableList<String> usernames;
    private BorderPane chatBorderPane;
    private OutputStream os;

    private void initialize() {
        borderPane = new BorderPane();
        scene = new Scene(borderPane, 600, 600);

        userList = new ListView<String>();

        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        infoPanel = new HBox();
        userListPanel = new VBox(userList);
        textPanel = new HBox();
        chatBoard = new VBox(textArea);

        borderPane.setTop(infoPanel);
        borderPane.setCenter(userListPanel);
        //borderPane.setBottom(textPanel);
        //borderPane.setCenter(chatBoard);

        IPTextField = new TextField("127.0.0.1");
        portTextField = new TextField("8080");
        IPTextField.setPrefSize(100, 25);
        portTextField.setPrefSize(70, 25);
        Label hostLabel = new Label("服务器IP");
        Label portLabel = new Label("端口");
        Button connectButton = new Button("Connect");
        connectButton.setOnAction(actionEvent -> connect(IPTextField.getCharacters().toString(), Integer.parseInt(portTextField.getCharacters().toString())));
        chatButton = new Button("发起聊天");


        infoPanel.getChildren().add(hostLabel);
        infoPanel.getChildren().add(IPTextField);
        infoPanel.getChildren().add(portLabel);
        infoPanel.getChildren().add(portTextField);
        infoPanel.getChildren().add(connectButton);
        infoPanel.getChildren().add(chatButton);

        emojiButton = new Button("表情");
        sendButton = new Button("发送");
        textField = new TextField();

        textPanel.getChildren().add(textField);
        textPanel.getChildren().add(emojiButton);
        textPanel.getChildren().add(sendButton);

        BorderPane chatBorderPane = new BorderPane();
        chatBorderPane.setCenter(chatBoard);
        chatBorderPane.setBottom(textPanel);

        userList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        chatButton.setOnAction(actionEvent -> {
            ObservableList<String> candidates = userList.getSelectionModel().getSelectedItems();
            chatBorderPane.setCenter(textArea);
            Scene chatScene = new Scene(chatBorderPane);
            Stage chatStage = new Stage();
            chatStage.setScene(chatScene);
            chatStage.show();
            sendButton.setOnAction(actionEvent1 -> {
                sendMessage(textField.getCharacters().toString(), "send");
            });
        });
    }

    public Client() {
        //connect("127.0.0.1", 8080);
        initialize();
    }

    public boolean connect(String host, int port) {
        if (!users.contains(user)) {
            try {
                socket = new Socket(host, port);
                br = new BufferedReader(new InputStreamReader(System.in));
                pw = new PrintWriter(socket.getOutputStream());
                user = new User(name, socket);
                users.add(user);
                userList.getItems().add(user.getName());
                ClientThread thread = new ClientThread(socket, this);
                thread.start();
                System.out.println("Connected to server " + socket.getRemoteSocketAddress());
//            for (User u : users) {
//                Button chatButton = new Button(u.getName());
//                chatButton.setOnAction(actionEvent -> {
//                    String sendBy = name;
//                    ArrayList<String> sendTo = new ArrayList<>();
//                    sendTo.add(u.getName());
//                    TextArea chatArea = new TextArea(u.getName());
//                    sendButton.setOnAction(actionEvent1 -> {
//                        Message message = new Message(sendBy, sendTo, textField.getCharacters().toString());
//                        pw.println(textField.getCharacters().toString());
//                        textField.clear();
//                        chatArea.
//                    });
//                });
//                userList.getChildren().add(chatButton);
//            }
                return true;
            } catch (IOException e) {
                System.out.println("Server error");
                return false;
            }
        }
        return false;
    }

    public void sendMessage(String msg, String code) { //send msg to server socket
        try {

            pw.println("<code>" + code + "</code><msg>" + msg + "</msg>");
            pw.flush();
        } catch (Exception e) {
            System.out.println("Error occurs in sendMessage()");
        }
    }

    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage stage) {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
//        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setScene(scene);
        stage.setTitle("Chatting Client");
        StackPane welcomePane = new StackPane();
        TextField nameTextField = new TextField("Enter your name");
        Button affirmButton = new Button("确认");
        welcomePane.getChildren().add(nameTextField);
        welcomePane.getChildren().add(affirmButton);
        Scene welcomeScene = new Scene(welcomePane, 300, 250);
        Stage welcomeStage = new Stage();
        welcomeStage.setTitle("Welcome to chatroom");
        welcomeStage.setScene(welcomeScene);
        welcomeStage.show();
        affirmButton.setOnAction(actionEvent -> {
            //user = new User(nameTextField.getCharacters().toString(),socket);
            name = nameTextField.getCharacters().toString();
            System.out.println(name);
            welcomeStage.hide();
            Client client = new Client();
            stage.show();
        });
    }

    public void insertMessage(TextArea textArea, String title, String content) {
        textArea.appendText(title + ":\n" + content);
    }

    public void updateTextArea(TextArea textArea, String msg) {
        insertMessage(textArea, user.getName(), msg);
    }

    public void showEscDialog(String content) {
        chatBorderPane.setAccessibleText(content);
        textArea.clear();
        users.clear();
    }

    public void updateTextAreaFromUser(String content) {
        if (content.length() > 0) {
            Pattern pattern = Pattern.compile("<from>(.*)</from><smsg>(.*)</smsg>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String from = matcher.group(1);
                String smsg = matcher.group(2);
                String fromName = null;
                for (User u: users){
                    if (u.getName().equals(from)){
                        fromName = u.getName();
                    }
                }
                if (fromName.equals(name))
                    fromName = "You";
                if (smsg.startsWith("<emoji>")) {
                    String emojiCode = smsg.substring(7, smsg.length() - 8);
//     System.out.println(emojiCode);
                    insertMessage(textArea, emojiCode, fromName + "says：");
                    return;
                }
                insertMessage(textArea,fromName + "says：", smsg);
            }
        }
    }

    public void addUser(String content) throws IOException {
        if (content.length() > 0) {
            Pattern pattern = Pattern.compile("<name>(.*)</name>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String name = matcher.group(1);
                if (!users.contains(name)) {
                    User user1 = new User(name, socket);
                    users.add(user1);
                    userList.getItems().add(name);
                }
            }
        }
    }


    public void deleteUser(String content) {
        if (content.length() > 0) {
            for (User u : users) {
                if (u.getName().equals(content)) {
                    users.remove(u);
                    break;
                }
            }
        }
        insertMessage(textArea, "system: ", name + "exits room");
    }

    public void updateUser(String content) throws IOException {
        if (content.length() > 0) {
            Pattern pattern = Pattern.compile("<id>(.*)</id><name>(.*)</name>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String id = matcher.group(1);
                String name = matcher.group(2);
                addUser(name);
            }
        }
    }
    public void listUsers(String content) throws IOException {
        String name = null;
        String id = null;
        Pattern rough_pattern = null;
        Matcher rough_matcher = null;
        Pattern detail_pattern = null;
        /*
         * 先用正则表达式匹配用户信息
         * 然后插入数据模型中
         * 并更新用户数据模型中的条目
         */
        if (content.length() > 0) {
            rough_pattern = Pattern.compile("<member>(.*?)</member>");
            rough_matcher = rough_pattern.matcher(content);
            while (rough_matcher.find()) {
                String detail = rough_matcher.group(1);
                detail_pattern = Pattern.compile("<name>(.*)</name>");
                Matcher detail_matcher = detail_pattern.matcher(detail);
                if (detail_matcher.find()) {
                    name = detail_matcher.group(1);
                    addUser(name);
                }
            }
        }
    }
    public void addRoom(String content) {
        if (content.length() > 0) {
            Pattern pattern = Pattern.compile("<rid>(.*)</rid><rname>(.*)</rname>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String rid = matcher.group(1);
                String rname = matcher.group(2);
                insertRoom(Integer.parseInt(rid), rname);
            }
        }
    }

    /**
     * 删除一个room
     *
     * @param content
     */
    public void delRoom(String content) {
        if (content.length() > 0) {
            int delRoomId = Integer.parseInt(content);

            Set<String> set = rooms_map.keySet();
            Iterator<String> iter = set.iterator();
            String rname = null;
            while (iter.hasNext()) {
                rname = iter.next();
                if (rooms_map.get(rname) == delRoomId) {
                    rooms_model.getItems().remove(rname);
                    break;
                }
            }
            rooms_map.remove(rname);
        }
    }

    /**
     * 列出目前所有的rooms
     *
     * @param content
     */
    public void listRooms(String content) {
        String rname = null;
        String rid = null;
        Pattern rough_pattern = null;
        Matcher rough_matcher = null;
        Pattern detail_pattern = null;
        if (content.length() > 0) {
            rough_pattern = Pattern.compile("<room>(.*?)</room>");
            rough_matcher = rough_pattern.matcher(content);
            while (rough_matcher.find()) {
                String detail = rough_matcher.group(1);
                detail_pattern = Pattern.compile("<rname>(.*)</rname><rid>(.*)</rid>");
                Matcher detail_matcher = detail_pattern.matcher(detail);
                if (detail_matcher.find()) {
                    rname = detail_matcher.group(1);
                    rid = detail_matcher.group(2);
                    insertRoom(Integer.parseInt(rid), rname);
                }
            }
        }
    }

    /**
     * 插入一个room
     *
     * @param rid
     * @param rname
     */
    private void insertRoom(Integer rid, String rname) {
        if (!rooms_map.containsKey(rname)) {
            rooms_map.put(rname, rid);
        } else {
            rooms_map.remove(rname);
            rooms_map.put(rname, rid);
        }
    }

    /**
     * 获得用户所在房间的名称
     *
     * @param strId
     * @return
     */
    private String getRoomName(String strId) {
        int rid = Integer.parseInt(strId);
        Set<String> set = rooms_map.keySet();
        Iterator<String> iterator = set.iterator();
        String cur = null;
        while (iterator.hasNext()) {
            cur = iterator.next();
            if (rooms_map.get(cur) == rid) {
                return cur;
            }
        }
        return "";
    }
}
