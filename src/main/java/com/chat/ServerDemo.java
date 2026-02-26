package com.chat;

import com.chat.server.Server;

public class ServerDemo {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}