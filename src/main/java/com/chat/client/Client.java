package com.chat.client;

import java.io.InputStream;
import java.util.Properties;

public class Client {
    private final ChatClient chatClient;

    public Client() {
        String host = "localhost";
        int port = 8189;

        try (InputStream input = Client.class.getClassLoader().getResourceAsStream("client-settings.txt")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                host = prop.getProperty("server.host", "localhost");
                port = Integer.parseInt(prop.getProperty("server.port", "8189"));
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить настройки, используем localhost:8189");
        }

        this.chatClient = new ChatClient(host, port);
    }

    public void start() {
        chatClient.start();
    }
}