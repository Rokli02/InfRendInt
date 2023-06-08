package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
  private Socket socket;

  public Client(Socket socket) {
    this.socket = socket;
  }

  public static void main(String[] args) {
    int port = Utils.getPortFromArgs(args);

    try {
      var client = new Client(new Socket(Constants.DEFAULT_HOST, port));
      System.out.println("[kliens]: Client online");

      client.run();

      System.out.println("[kliens]: Client offline!");
    } catch (IOException e) {
      // e.printStackTrace();
      System.out.println("[kliens]: Client crashed...");
    }
  }

  @Override
  public void run() {
    String[] input = null;

    try (
      var reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      var writer = new PrintWriter(this.socket.getOutputStream());
      var streamIn = new InputStreamReader(this.socket.getInputStream());
      var streamOut = new BufferedOutputStream(this.socket.getOutputStream());
      var in = new BufferedReader(new InputStreamReader(System.in));
    ) {
      writer.println("Joined chat!");
      writer.flush();

      final AsyncTextPipe pipe = new AsyncTextPipe();

      Thread serverThread = getInputThread(reader, pipe, Constants.SERVER_NAME);
      Thread clientThread = getInputThread(in, pipe, Constants.CLIENT_NAME);

      serverThread.start();
      clientThread.start();

      while (!serverThread.isInterrupted() && !clientThread.isInterrupted()) {
        if (socket.isClosed()) {
          System.out.println("[server]: Server went offline!");
          break;
        }

        input = pipe.getText();

        if (pipe.isStoped()) {
          break;
        }

        if (input[0].equals(Constants.CLIENT_NAME)) {
          if (input[1].startsWith("upload")) {
            byte[] uploadableFile = Utils.readFileContent(Constants.CLIENT_ROOT, "random.txt");
            streamOut.write(uploadableFile, 0, uploadableFile.length);
          } else {
            writer.println(input[1]);
            writer.flush();
          }
        } else {
          if (input[1].startsWith("download")) {
            // download
          } else {
            System.out.println("\n" + input[0] + input[1]);
          }
        }

      } 
    } catch (IOException | InterruptedException | IllegalArgumentException e) {
      // e.printStackTrace();
    }
  }

  private Thread getInputThread(BufferedReader reader, AsyncTextPipe pipe, String sender) {
    return new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String response = null;
          final Thread currentThread = Thread.currentThread();

          do {
            try {
              if (sender.equals(Constants.CLIENT_NAME)) {
                System.out.print(sender);
              }
              
              response = reader.readLine();
              pipe.produceTest(sender, response);
            } catch (InterruptedException ie) {
              for (int i = 0; i < 4; i++) {
                long waitTime = (long) (Math.random() * 500);
                try {
                  wait(waitTime);
                  pipe.produceTest(sender, response);
                } catch (InterruptedException e) {}
              }
            }
          } while (
            response != null &&
            !response.isBlank() &&
            !socket.isClosed() &&
            !(sender.equals(Constants.SERVER_NAME) && response.equals(Constants.CLIENT_QUIT_WORD))
          );
          currentThread.interrupt();
          pipe.stop();
        } catch (IOException e) {}
      }
    });
  }

  private void fileHandler() {
    throw new RuntimeException("Method 'fileHandler' is not implemented!");
  }
}
