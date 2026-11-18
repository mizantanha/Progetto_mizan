package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server_archivio {
    public static void main(String[] args) throws IOException {
      
        ServerSocket ss = new ServerSocket(3000);
        System.out.println("server in ascolto..\n");

        while(true){
            try {
                Socket s = ss.accept();
                System.out.println("Client connesso!");
                
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);

                String richiesta = in.readLine();

                //controllo richiesta vuoto
                if(richiesta != null){
                    System.out.println("Richiesta : " + richiesta);
                    String pulita = richiesta.trim();

                    if(pulita.isEmpty() ){
                        out.println("ERR SYNTAX");
                        out.println();
                    }
                    //controllo comandi
                    else{
                        String[] parti = pulita.split( " ");
                        String comando = parti[0];
                        if(!comando.equals("LEGGI")){
                            out.println("ERR UNKNOWNCMD");
                            out.println();
                        }
                        // 3. Controllo numero argomenti e nome file valido
                        else if((parti.length != 2 || parti[1].contains("/") || 
                        parti[1].contains("\\") || parti[1].contains("..") || 
                        !parti[1].endsWith(".txt") || parti[1].length() <= 4)) {
                            out.println("ERR SYNTAX");
                            out.println();
                        } else {
                            File file = new File("documenti", parti[1]);

                            // 4. Controllo presenza del file
                            if (!file.exists() || !file.isFile()) {
                                out.println("ERR NOTFOUND");
                                out.println();
                            } else {
                                // 5. Apertura e lettura del file
                                try {
                                    BufferedReader leggiFile = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                                    out.println("OK FILE");
                                    out.println();

                                    String riga;
                                    while ((riga = leggiFile.readLine()) != null) {
                                        out.println(riga);
                                    }

                                    leggiFile.close();
                                } catch (IOException e) {
                                    out.println("ERR READ");
                                    out.println();
                                }
                            }
                        }
                    
                    }
                }


                s.close();
            } catch (Exception e) {
                System.out.println("Errore su una connessione: " + e.getMessage());
            }
        }

        
        














    }
}