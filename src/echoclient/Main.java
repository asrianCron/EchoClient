/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author asrianCron
 */
public class Main extends Application {

//    private final int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width / 3;
//    private final int maxHeight = Toolkit.getDefaultToolkit().getScreenSize().height / 3;
    private final int maxWidth = 600;
    private final int maxHeight = 400;
    public static TextField userInput = null;
    public static TextArea serverInput = null;
    public static PrintWriter out = null;
    private static boolean connected = false;
    private static Thread inputThread;
    private static OutputWorker worker;
    private static TextArea consoleArea = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = new GridPane();
        Button FSButton = new Button("SEND");
        Button consoleButton = new Button("CONSOLE");
        userInput = new TextField();
        serverInput = new TextArea();
        FSButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                updateConsole(userInput.getText());
                choiceMaker(userInput.getText());
            }
        });
        userInput.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    updateConsole(userInput.getText());
                    choiceMaker(userInput.getText());
                }
            }
        });
        consoleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                initialiseConsole();
                updateConsole("CONSOLE INITALISED");
            }
        });
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                System.out.println("CLOSE REQUESTED");
                updateConsole("CLOSE REQUESTED");
                if (worker != null) {
                    //trying to close buffers and socket properly, haven't managed yet worker.closeConnection();
                    System.exit(1);
                }
            }
        });
        FSButton.setFocusTraversable(false);
        FSButton.setMinSize(maxWidth / 4, maxHeight / 4);

        userInput.setMinSize(maxWidth / 2, maxHeight / 4);
        serverInput.setMinSize(maxWidth / 2, maxHeight / 4);
        serverInput.setEditable(false);

        grid.add(serverInput, 0, 0);
        grid.add(userInput, 0, 1);
        grid.add(FSButton, 1, 1);
        grid.add(consoleButton, 2, 0);
        grid.setAlignment(Pos.CENTER);

        informUser();

        Scene scene = new Scene(grid);
        primaryStage.setTitle("Client interface");
        primaryStage.setResizable(false);
        primaryStage.setMinHeight(maxHeight);
        primaryStage.setMinWidth(maxWidth);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static boolean initialiseClient(String[] args) {
        updateConsole("INITALISING CLIENT");
        Socket clientSocket;
        try {
            String hostName = args[0];
            int portNumber = Integer.parseInt(args[1]);
            System.out.println("Socket parsed: hostName=" + hostName + " , portNumber=" + portNumber);
            updateConsole("Socket parsed: hostName=" + hostName + " , portNumber=" + portNumber);
            clientSocket = new Socket(hostName, portNumber); // creating socket
//            ExecutorService service = Executors.newCachedThreadPool();
//            service.submit(new OutputWorker(clientSocket)); // starting output thread
            worker = new OutputWorker(clientSocket);
            inputThread = new Thread(worker);
            inputThread.start(); // starting thread
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException ex) {
                return false;
            }
            connected = true;
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void informUser() {
        serverInput.appendText(String.format("%s%n", "Use '/login URI port' to login to a server"));
        serverInput.appendText(String.format("%s%n", "Use '/name insertNameHere' to change your name on the server"));
        serverInput.appendText(String.format("%s%n", "Use '/whisper insertNameHere insertTextHere' to whisper to another user"));
    }

    public static void changeName(String name) {
        name = name.split("\n")[0];
        out.println(String.format("<name %s>", name));
        userInput.setText(null);
    }

    public static void appendText(String str) {
        serverInput.appendText(String.format("%s%n", str));// appends text to main text box
    }

    public static void printToStream(String str) {
        if (connected) {
            if (!str.equals("")) {
                out.println(str); // sends string to socket stream
                serverInput.appendText("You said :" + str + "\n");
                userInput.setText(null);
            }
        }
    }

    public static void choiceMaker(String str) {
        String[] choppedPieces = str.split(" ");
        if (choppedPieces[0].equals("/login") && choppedPieces.length == 3) {
            String[] loginInfo = {choppedPieces[1], choppedPieces[2]}; // arguments are : [1]URI [2]port
            if (initialiseClient(loginInfo)) {
                serverInput.appendText("LOGIN SUCCESSFUL\n");
                updateConsole("LOGIN SUCCESSFUL");
            } else {
                updateConsole("LOGIN FAILED");
                serverInput.appendText("LOGIN FAILED\n");
            }
            userInput.setText(null);
        } else if (choppedPieces[0].equals("/name") && choppedPieces.length == 2) {
            changeName(choppedPieces[1]);
        } else if (choppedPieces[0].equals("/whisper") && choppedPieces.length == 3) {
            printToStream(String.format("<whisper %s %s>", choppedPieces[1], choppedPieces[2]));
        } else if (choppedPieces[0].equals("<console>")) {
            initialiseConsole();
        } else {
            printToStream(str);
        }

    }

    public static void initialiseConsole() {
        if (consoleArea != null) {
            Stage consoleStage = new Stage();
            consoleStage.setTitle("Console");
            GridPane grid = new GridPane();
            consoleArea = new TextArea();
            consoleArea.setMinSize(450, 450);

            grid.add(consoleArea, 0, 0);
            consoleStage.setScene(new Scene(grid, 450, 450));
            consoleStage.show();
        }
    }

    public static void updateConsole(String text) {
        if (consoleArea != null) {
            consoleArea.appendText(text + "\n");
        }

    }

    public static void main(String[] args) {

        launch(args);
    }
}
