package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
  private int port;

  public Server(int port) {
    this.port = port;
  }

  public static void main(String[] args) {
    System.out.println("[server]: Server online");

    int port = Utils.getPortFromArgs(args);

    while (true) {
      var server = new Server(port);
      server.run();
    }
  }

  @Override
  public void run() {
    BufferedReader reader = null;
    PrintWriter writer = null;
    Socket socket = null;

    try (
      var serverSocket = new ServerSocket(port);
    ) {
      System.out.println("[server]: Waiting for connection...");
      socket = serverSocket.accept();
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new PrintWriter(socket.getOutputStream());
      String input;

      input = reader.readLine();
      System.out.println("[kliens]: " + input);
      writer.println("Hello, itt szerver, mit akarsz?");
      writer.flush();

      do {
        input = reader.readLine();
        System.out.println("[kliens]: " + input);

        if (input.startsWith("upload")) {
          // upload
        } else if (input.startsWith("download")) {
          // download
        }
      } while (input != null && !socket.isClosed() && !input.equals(Constants.SERVER_QUIT_WORD));

      System.out.println("[server]: Server offline");
      writer.println(Constants.CLIENT_QUIT_WORD);
      writer.flush();
    } catch (IOException e) {
      System.out.println("[server]: Connection freed with some force");
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
        
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("[server]: Connection is closing...");
  }
}