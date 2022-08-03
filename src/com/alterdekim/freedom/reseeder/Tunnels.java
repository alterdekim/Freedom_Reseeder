package com.alterdekim.freedom.reseeder;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Tunnels extends Thread {

    public static ArrayList<Tunnel> socketTunnels = new ArrayList<Tunnel>();

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8081);
            Socket socket;
            while( ( socket = serverSocket.accept() ) != null ) {
                socketTunnels.add(new Tunnel(socket));
            }
        } catch ( Exception e ){
            e.printStackTrace();
        }
    }
}
