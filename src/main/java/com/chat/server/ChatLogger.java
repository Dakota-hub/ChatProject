package com.chat.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogger {
    private static final ChatLogger INSTANCE = new ChatLogger();
    private final String LOG_FILE = "logs/server.log";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ChatLogger() {
        new File("logs").mkdirs(); // создаём папку logs
    }

    public static ChatLogger getInstance() {
        return INSTANCE;
    }

    public synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String line = "[" + timestamp + "] " + message + System.lineSeparator();

        // В консоль
        System.out.print(line);

        // В файл
        try (FileOutputStream fos = new FileOutputStream(LOG_FILE, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}