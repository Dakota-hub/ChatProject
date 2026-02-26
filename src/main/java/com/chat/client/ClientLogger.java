package com.chat.client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientLogger {
    private static final ClientLogger INSTANCE = new ClientLogger();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ClientLogger() {
        File logsDir = new File("logs"); // папка для логов.
        if (!logsDir.exists() && !logsDir.mkdirs()) {
            System.err.println("Не удалось создать папку для логов");
        }
    }

    public static ClientLogger getInstance() {
        return INSTANCE;
    }

    public synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String line = "[" + timestamp + "] " + message + System.lineSeparator();

        System.out.print(line);

        try (FileOutputStream fos = new FileOutputStream("logs/client.log", true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {

            bw.write(line);
            bw.flush();
        } catch (IOException e) {
            System.err.println("Ошибка записи в лог: " + e.getMessage());
            log("КРИТИЧЕСКАЯ ОШИБКА: " + e.getMessage());
        }
    }
}