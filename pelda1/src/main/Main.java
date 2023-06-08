package main;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
  public static void main(String[] args) {
    try(ServerSocket socket = new ServerSocket(ServerConstants.DEFAULT_SERVER_PORT)) {
      String host = socket.getInetAddress().getHostName();
      int port = socket.getLocalPort();
      System.out.println(String.format("Server is listening on %s:%d...", host, port));

      while (true) {       
        Thread thread = new Thread(new HttpServer(socket.accept(), host, port));
        thread.start();
      }

    } catch (RuntimeException | IOException re) {
      re.printStackTrace();
    }
  }
}
