package com.alterdekim.freedom.reseeder;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.Security;
import java.util.Scanner;

public class Main {

    public static DB db = new DB();

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            Scanner sc = new Scanner(new File("config.json"));
            JSONObject config = new JSONObject(sc.nextLine());
            db.connect(config.get("host").toString(), config.get("port").toString(), config.get("db").toString(), config.get("user").toString(), config.get("password").toString());
        } catch ( Exception e ) {
            try {
                new File("config.json").createNewFile();
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(new File("config.json")));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("host", "localhost");
                jsonObject.put("port", 8888);
                jsonObject.put("db", "database");
                jsonObject.put("user", "root");
                jsonObject.put("password", "123");
                pw.println(jsonObject.toString());
                pw.flush();
                pw.close();
                System.exit(0);
            } catch ( Exception e2 ) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }

        init();
        new Clients().start();
        new Tunnels().start();
    }

    private static void init() {
        if( !new File( "crypt.json" ).exists() ) {
            ECCKeyPair keyPair = ECC.generateECC();
            Settings.rsaKeyPair = keyPair;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("private_key", keyPair.getPrivateKey());
            jsonObject.put("public_key", keyPair.getPublicKey());
            try {
                new File("crypt.json").createNewFile();
                PrintWriter pw = new PrintWriter(new FileOutputStream(new File("crypt.json")));
                pw.println(jsonObject.toString());
                pw.flush();
                pw.close();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        } else {
            try {
                Scanner scanner = new Scanner(new FileInputStream(new File("crypt.json")));
                String line = "";
                String data = "";
                try {
                    while ((line = scanner.nextLine()) != null) {
                        data += line;
                    }
                } catch ( Exception e ) {
                    //e.printStackTrace();
                }
                JSONObject jsonObject = new JSONObject(data);
                ECCKeyPair keyPair = new ECCKeyPair( jsonObject.get("private_key").toString(), jsonObject.get("public_key").toString() );
                Settings.rsaKeyPair = keyPair;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
