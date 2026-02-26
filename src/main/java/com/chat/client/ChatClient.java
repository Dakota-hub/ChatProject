package com.chat.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

public class ChatClient {
    private String host;
    private int port;
    private Socket socket;
    private String username;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
        loadSettings();
    }

    private void loadSettings() {
        try (InputStream input = ChatClient.class.getClassLoader().getResourceAsStream("client-settings.txt")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                host = prop.getProperty("server.host", "localhost");
                try {
                    port = Integer.parseInt(prop.getProperty("server.port", "8189"));
                } catch (NumberFormatException e) {
                    System.err.println("Некорректный порт, используем 8189");
                    port = 8189;
                }
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить настройки, используем localhost:8189");
        }
    }

    public void start() {
        try {
            socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            Scanner console = new Scanner(System.in, StandardCharsets.UTF_8);
            System.out.print("Введите имя: ");
            username = console.nextLine().trim();
            if (username.isEmpty()) username = "user" + (int) (Math.random() * 1000);

            out.println(username);
            ClientLogger.getInstance().log("Имя: " + username);

            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        processMessage(msg);
                    }
                } catch (IOException e) {
                    System.err.println("Поток получения сообщений завершён: " + e.getMessage());
                }
            }).start();

            System.out.println("Чат активен. Введите /exit для выхода.");
            while (true) {
                String msg = console.nextLine();
                ClientLogger.getInstance().log("Отправлено: " + msg);
                out.println(msg);
                if ("/exit".equalsIgnoreCase(msg.trim())) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void processMessage(String raw) {
        String[] parts = raw.split("\\$\\$\\$", 3);
        if (parts.length < 2) {
            System.err.println("Неверный формат сообщения: " + raw);
            return;
        }

        switch (parts[0]) {
            case "MSG":
                if (!parts[1].equals(username)) {
                    System.out.println("[" + parts[1] + "]: " + parts[2]);
                }
                break;
            case "MY":
                System.out.println("[Вы]: " + parts[1]);
                break;
            case "SYS":
                System.out.println("[Система] " + parts[1]);
                break;
            default:
                System.err.println("Неизвестный тип сообщения: " + parts[0]);
        }
    }
}