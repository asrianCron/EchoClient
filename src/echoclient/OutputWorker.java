/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author asrianCron
 */
public class OutputWorker implements Runnable {

    private Socket clientSocket;
    private String input;
    private BufferedReader in = null;
    private boolean running = false;

    public OutputWorker(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        System.out.println("THREAD ALIVE");
        running = true;
        while(running){
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            while (running) {
            while ((input = in.readLine()) != null) {
//                System.out.println(input);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        print(input); //updating textArea
                    }
                });
            }
//            }
        } catch (UnknownHostException ex) {
            System.err.println("unknown host " + clientSocket);
        } catch (IOException ex) {
            System.err.println("no I/O to" + clientSocket);
            running = false;
//            System.exit(1);
        } finally {
            try {
                closeConnection();
                System.exit(1);
            } catch (IOException ex) {
                System.err.println("I/O PROBLEMS");
                System.exit(1);
            }
        }
        }
    }

    public void closeConnection() throws IOException {
        System.out.println("ATTEPMTING TO CLOSE CONNECTION");
        in.close();
        clientSocket.close();
    }

    public void print(String str) {
        echoclient.Main.appendText(str);
        System.out.println(str);
    }
}
