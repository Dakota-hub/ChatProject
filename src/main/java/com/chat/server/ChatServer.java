package com.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatServer {
    private final int port;
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            ChatLogger.getInstance().log("Сервер запущен на порту " + port);

            AtomicInteger id = new AtomicInteger(0);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                String clientId = String.valueOf(id.incrementAndGet());
                ClientHandler handler = new ClientHandler(socket, clientId, clients);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                ChatLogger.getInstance().log("Ошибка сервера: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            ChatLogger.getInstance().log("Сервер остановлен");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8189;
        try (InputStream input = ChatServer.class.getClassLoader().getResourceAsStream("server-settings.txt")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                port = Integer.parseInt(prop.getProperty("server.port", "8189"));
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить настройки, используем порт 8189");
        }

        ChatServer server = new ChatServer(port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}