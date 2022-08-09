package com.alterdekim.freedom.reseeder;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Tunnel extends Thread {

    public Socket sock;

    private boolean isLogged = false;

    private PrintWriter pw;

    private HashMap<String, ITunnelListener> listeners = new HashMap<String, ITunnelListener>();

    public Tunnel( Socket sock ) {
        this.sock = sock;
        try {
            this.pw = new PrintWriter(sock.getOutputStream());
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        start();
    }

    public void write( String guid, JSONObject jsonObject, ITunnelListener listener ) {
        listeners.put(guid, listener);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("guid", guid);
        jsonObject1.put("body", jsonObject);
        pw.println(jsonObject1.toString());
        pw.flush();
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(sock.getInputStream());
            String line = "";
            while ((line = scanner.nextLine()) != null) {
                try {
                    JSONObject jsonObject = new JSONObject(line);
                    String guid = jsonObject.get("guid").toString();
                    listeners.get(guid).response(jsonObject.get("body").toString());
                    listeners.remove(guid);
                } catch ( Exception e ) {
                    //e.printStackTrace();
                }
            }
        } catch ( Exception e ) {
            Tunnels.socketTunnels.remove(this);
            e.printStackTrace();
        }
    }
}
