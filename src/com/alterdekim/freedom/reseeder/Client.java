package com.alterdekim.freedom.reseeder;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Scanner;
import java.util.UUID;

import static com.alterdekim.freedom.reseeder.Clients.socketClients;
import static com.alterdekim.freedom.reseeder.Tunnels.socketTunnels;

public class Client extends Thread {

    public String public_key = "";

    private Socket sock;

    private boolean isLogged = false;

    private PrintWriter pw;

    public Client( Socket sock ) {
        this.sock = sock;
        try {
            this.pw = new PrintWriter(sock.getOutputStream());
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        start();
    }

    private void write( String str ) {
        pw.println(str);
        pw.flush();
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(sock.getInputStream());
            String line = "";
            while ((line = scanner.nextLine()) != null) {
                JSONObject jsonObject = new JSONObject(line);
                System.out.println("Client with line " + line);
                if( jsonObject.get("act").toString().equals("login") ) {
                    String password = RSA.RSADecode(jsonObject.get("password").toString(), Settings.rsaKeyPair.getPrivateKey());
                    String public_key = jsonObject.get("public_key").toString();
                    ResultSet rs = Main.db.query("SELECT * FROM peers WHERE PUBLIC_KEY = '"+public_key+"'");
                    if( rs.next() ) {
                        if( password.equals(rs.getString("PASSWORD")) ) {
                            isLogged = true;
                            this.public_key = public_key;
                        }
                    } else {
                        Main.db.getS().executeUpdate("INSERT INTO peers (PUBLIC_KEY, `PASSWORD`) VALUES ('"+public_key+"', '"+password+"')");
                        isLogged = true;
                        this.public_key = public_key;
                    }
                } else if( isLogged && jsonObject.get("act").toString().equals("connect") ) {
                    String dest = jsonObject.get("destination").toString();
                    System.out.println("Searching for destination...");
                    boolean is_true = false;
                    for( Client c : socketClients ) {
                        if( RSA.SHA256(c.public_key).equals(dest) ) {
                            System.out.println("Found destination! Initializing tunnel!");
                            Collections.shuffle(socketTunnels);
                            if( socketTunnels.size() > 0 ) {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("act", "connect");
                                jsonObject1.put("initiator", sock.getRemoteSocketAddress().toString().substring(1).split("\\:")[0]);
                                jsonObject1.put("dest", c.sock.getRemoteSocketAddress().toString().substring(1).split("\\:")[0]);
                                is_true = true;
                                String ip_t = socketTunnels.get(0).sock.getRemoteSocketAddress().toString().substring(1).split("\\:")[0];
                                socketTunnels.get(0).write(UUID.randomUUID().toString(), jsonObject1, new ITunnelListener() {
                                    @Override
                                    public void response(String response) {
                                        JSONObject jsonObject2 = new JSONObject(response);
                                        JSONObject r = new JSONObject();
                                        r.put("act", "tunnel_created");
                                        r.put("ip", ip_t);
                                        r.put("port", jsonObject2.get("initiator_port").toString());
                                        write(r.toString());
                                        r = new JSONObject();
                                        r.put("act", "endgate_created");
                                        r.put("ip", ip_t);
                                        r.put("port", jsonObject2.get("dest_port").toString());
                                        c.write(r.toString());

                                    }
                                });
                            } else {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("act", "tunnel_not_found");
                                write(jsonObject1.toString());
                            }
                            break;
                        }
                    }
                    if( !is_true ) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("act", "host_not_found");
                        write(jsonObject1.toString());
                    }
                }
            }
        } catch ( Exception e ) {
            socketClients.remove(this);
           // e.printStackTrace();
        }
    }
}
