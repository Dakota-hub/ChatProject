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
        ChatLogger.getInstance().log("Новое подключение с ID: " + clientId);
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
            broadcast("SYS$$$Пользователь " + username + " присоединился к чату");

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
            if (c != this && c != null && c.out != null) {
                c.sendMessage(message);
            }
        }
        if (message.startsWith("MSG$$$")) {
            String[] parts = message.split("\\$\\$\\$", 3);
            if (parts.length >= 3) {
                this.sendMessage("MY$$$" + parts[2]);
            }
        }
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
            if (clients != null) {
                clients.remove(this);
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();

            ChatLogger.getInstance().log("Отключился: " + username);
            broadcast("SYS$$$Пользователь " + username + " покинул чат");
        } catch (IOException e) {
            ChatLogger.getInstance().log("Ошибка при отключении: " + e.getMessage());
        }
    }
}