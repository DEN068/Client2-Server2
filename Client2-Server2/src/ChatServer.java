import java.io.*;
import java.net.*;

public class ChatServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(12345); // Порт для прослушивания
            System.out.println("Server started.");

            clientSocket = serverSocket.accept(); // Ожидание подключения клиента
            System.out.println("Client connected.");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine, outputLine;
            ChatProtocol protocol = new ChatProtocol();

            while ((inputLine = in.readLine()) != null) {
                outputLine = protocol.processInput(inputLine);
                out.println(outputLine);

                // Дублирование сообщений в файл
                BufferedWriter writer = new BufferedWriter(new FileWriter("chat_log.txt", true));
                writer.write(outputLine);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ChatProtocol {
        public String processInput(String input) {
            // Логика обработки сообщения
            return "Server processed: " + input;
        }
    }
}