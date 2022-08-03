package com.alterdekim.freedom.reseeder;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Clients extends Thread {

    public static ArrayList<Client> socketClients = new ArrayList<Client>();

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket;
            while( ( socket = serverSocket.accept() ) != null ) {
                socketClients.add(new Client(socket));
            }
        } catch ( Exception e ){
            e.printStackTrace();
        }
    }
}
