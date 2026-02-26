package com.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties; // ← Добавлен импорт

public class Server {
    private final ChatServer chatServer;

    public Server() {
        int port = loadPortFromSettings();
        this.chatServer = new ChatServer(port);
    }


    public void start() {
        chatServer.start();
    }


    private int loadPortFromSettings() {
        try (InputStream input = Server.class.getClassLoader().getResourceAsStream("server-settings.txt")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                return Integer.parseInt(prop.getProperty("server.port", "8189"));
            }
        } catch (IOException e) {
            System.err.println("Не удалось загрузить настройки, используем порт 8189");
        } catch (NumberFormatException e) {
            System.err.println("Некорректное значение порта в настройках, используем порт 8189");
        }
        return 8189;
    }
}