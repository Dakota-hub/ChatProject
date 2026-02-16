package com.chat.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final CopyOnWriteArrayList<ClientHandler> clients;
    private BufferedReader in;
    private PrintWriter out;
    private String username = "anonymous";

    public ClientHandler(Socket socket, String clientId, CopyOnWriteArrayList<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "user_" + System.currentTimeMillis() % 10000;
            }

            ChatLogger.getInstance().log("Подключился: " + username);
            broadcast("SYS|Пользователь " + username + " присоединился к чату");

            String message;
            while ((message = in.readLine()) != null) {
                if ("/exit".equalsIgnoreCase(message.trim())) {
                    break;
                }
                String logMsg = "[" + username + "] " + message;
                ChatLogger.getInstance().log(logMsg);
                broadcast("MSG$$$" + username + "$$$" + message);
            }
        } catch (IOException ignored) {
        } finally {
            disconnect();
        }
    }

    private void broadcast(String message) {
        for (ClientHandler c : clients) {
            if (c != this) {
                c.sendMessage(message);
            }
        }
        this.sendMessage("MY$$$" + message.split("\\$\\$\\$", 3)[2]);
    }

    private void sendMessage(String message) {
        try {
            out.println(message);
        } catch (Exception ignored) {
            disconnect();
        }
    }

    private void disconnect() {
        try {
            clients.remove(this);
            in.close();
            out.close();
            socket.close();
            ChatLogger.getInstance().log("Отключился: " + username);
            broadcast("SYS|Пользователь " + username + " покинул чат");
        } catch (IOException ignored) {
        }
    }
}