package com.chat.client;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientLogger {
    private static final ClientLogger INSTANCE = new ClientLogger();
    private final String LOG_FILE = "logs/client.log";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ClientLogger() {
        new File("logs").mkdirs();
    }

    public static ClientLogger getInstance() {
        return INSTANCE;
    }

    public synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String line = "[" + timestamp + "] " + message + System.lineSeparator();

        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}