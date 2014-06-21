/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asrianCron
 */
public class InputThread extends Thread {

    private Socket clientSocket;

    public InputThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        BufferedReader stdIn = null;
        PrintWriter out = null;

        System.out.format("%s CONNECTION ESTABLISHED%nType \"client:\" to get client-side operations%n", Utilitaries.getTime());

        String userInput;

        List<String> possibleOperations = new ArrayList<>();
        possibleOperations.add("shutdown(shut downs client DUUH)");
        possibleOperations.add("info(retireves info about the current client)");

        try {
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            while ((userInput = stdIn.readLine()) != null) {
                if (userInput.toLowerCase().contains("client:")) {
                    System.out.format("possible client operations : %s%n", possibleOperations.toString());
                    if (userInput.toLowerCase().contains("shutdown")) {
                        System.exit(0);
                    }
                    if (userInput.toLowerCase().contains("info")) {
                        //missing code here
                    }
                } else {
                    out.println(userInput);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(InputThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stdIn.close();
                out.close();
                clientSocket.close();
                System.exit(0);
            } catch (IOException ex) {
                Logger.getLogger(InputThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
