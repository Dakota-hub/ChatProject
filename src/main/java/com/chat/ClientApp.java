package com.chat;

import com.chat.client.Client;

public class ClientApp {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}