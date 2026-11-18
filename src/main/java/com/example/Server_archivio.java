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
        

     while (true) {
            try {
                Socket s = ss.accept();
                System.out.println("Client connesso!");

                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);

                String richiesta = in.readLine();

                if (richiesta != null) {
                    System.out.println("Richiesta: " + richiesta);
                    String pulita = richiesta.trim();

                    // 1. Controllo richiesta vuota
                    if (pulita.isEmpty()) {
                        out.println("ERR SYNTAX");
                        out.println();
                    } else {
                        // Separa comando e nome file senza usare array
                        int primoSpazio = pulita.indexOf(" ");
                        String comando;
                        String nomeRichiesto = "";

                        if (primoSpazio == -1) {
                            comando = pulita;
                        } else {
                            comando = pulita.substring(0, primoSpazio);
                            nomeRichiesto = pulita.substring(primoSpazio).trim();
                        }

                        // 2. Controllo comando
                        if (!comando.equals("LEGGI")) {
                            out.println("ERR UNKNOWNCMD");
                            out.println();
                        }
                        // 3. Controllo nome mancante, spazi interni o caratteri vietati
                        else if (nomeRichiesto.isEmpty() || nomeRichiesto.contains(" ") || nomeRichiesto.contains("/") || nomeRichiesto.contains("\\") || nomeRichiesto.contains("..")) {
                            out.println("ERR SYNTAX");
                            out.println();
                        } else {
                            File fileTrovato = null;
                            String tipoFile = "";

                            // Caso A: il nome ha già un'estensione
                            if (nomeRichiesto.contains(".")) {
                                if (nomeRichiesto.endsWith(".txt") && nomeRichiesto.length() > 4) {
                                    tipoFile = "txt";
                                } else if (nomeRichiesto.endsWith(".md") && nomeRichiesto.length() > 3) {
                                    tipoFile = "md";
                                } else if (nomeRichiesto.endsWith(".csv") && nomeRichiesto.length() > 4) {
                                    tipoFile = "csv";
                                } else if (nomeRichiesto.endsWith(".html") && nomeRichiesto.length() > 5) {
                                    tipoFile = "html";
                                }

                                if (tipoFile.isEmpty()) {
                                    out.println("ERR SYNTAX");
                                    out.println();
                                } else {
                                    File f = new File("documenti", nomeRichiesto);
                                    if (f.exists() && f.isFile()) {
                                        fileTrovato = f;
                                    } else {
                                        out.println("ERR NOTFOUND");
                                        out.println();
                                    }
                                }
                            }
                            // Caso B: il nome NON ha estensione -> cerca prima .txt, poi .md, poi .csv, poi .html
                            else {
                                File fTxt = new File("documenti", nomeRichiesto + ".txt");
                                File fMd = new File("documenti", nomeRichiesto + ".md");
                                File fCsv = new File("documenti", nomeRichiesto + ".csv");
                                File fHtml = new File("documenti", nomeRichiesto + ".html");

                                if (fTxt.exists() && fTxt.isFile()) {
                                    fileTrovato = fTxt;
                                    tipoFile = "txt";
                                } else if (fMd.exists() && fMd.isFile()) {
                                    fileTrovato = fMd;
                                    tipoFile = "md";
                                } else if (fCsv.exists() && fCsv.isFile()) {
                                    fileTrovato = fCsv;
                                    tipoFile = "csv";
                                } else if (fHtml.exists() && fHtml.isFile()) {
                                    fileTrovato = fHtml;
                                    tipoFile = "html";
                                } else {
                                    out.println("ERR NOTFOUND");
                                    out.println();
                                }
                            }

                            // 4. Invio stato, righe di intestazione e contenuto
                            if (fileTrovato != null) {
                                try {
                                    BufferedReader leggiFile = new BufferedReader(
                                        new InputStreamReader(new FileInputStream(fileTrovato), StandardCharsets.UTF_8)
                                    );

                                    out.println("OK FILE");
                                    out.println("NAME: " + fileTrovato.getName());
                                    out.println("TYPE: " + tipoFile);
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

            } catch (IOException e) {
                System.out.println("Errore su una connessione: " + e.getMessage());
            }
        }
    }
}



