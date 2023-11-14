import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private JButton sendButton;
    private JTextField messageField;
    private JTextArea chatArea;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient() {
        setTitle("Chat Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        messageField = new JTextField(30);
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        chatArea = new JTextArea(10, 30);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel panel = new JPanel();
        panel.add(messageField);
        panel.add(sendButton);

        add(panel, "South");
        add(scrollPane, "Center");

        setVisible(true);

        try {
            socket = new Socket("127.0.0.1", 12345); // Подключение к серверу
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            loadChatHistory();
            receiveMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        out.println(message);
        messageField.setText("");

        // Дублирование сообщений в файл
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("chat_log.txt", true));
            writer.write("Client: " + message);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        chatArea.append("Server: " + message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadChatHistory() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("chat_log.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                chatArea.append(line + "\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}