package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpServer implements Runnable {
  private Socket socket;
  private Map<String, String> substitution;

  public HttpServer(Socket socket, String host, int port) {
    this.socket = socket;
    this.substitution = new HashMap<String, String>(3);
    this.substitution.put("${HOST}", host);
    this.substitution.put("${PORT}", String.valueOf(port));
  }

  @Override
  public void run() {
    try (
      var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      var writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    ) {
      StringTokenizer token = new StringTokenizer(reader.readLine());
      String 
        method = token.nextToken(),
        path = token.nextToken();

      this.substitution.put("${PATH}", path);

      File file = getFileOnPath(path, method);
      String body = this.getFileAsBody(file);

      HttpServer.addBasicHeaders(writer, body.length());
      writer.println(body);

      writer.flush();
    } catch (RuntimeException | IOException e) {
      e.printStackTrace();
    }
  }

  private File getFileOnPath(String path, String method) {
    File file;

    if (!method.equals("GET")) {
      file = new File(ServerConstants.ROOT, ServerConstants.NOT_FOUND_FILE);
    }
    else if (path.equals("/")) {
      file = new File(ServerConstants.ROOT, ServerConstants.INDEX_FILE);
    }
    else {
      file = new File(ServerConstants.ROOT, path + ".html");
    }

    if (!file.exists()) {
      file = new File(ServerConstants.ROOT, ServerConstants.NOT_FOUND_FILE);
    }

    return file;
  }

  private static void addBasicHeaders(PrintWriter writer, long contentLength) {
    writer.println("HTTP/1.1 200 OK");
    writer.println("Content-length: " + contentLength);
    writer.println("Date: " + new Date());
    writer.println("Content-type: text/html; charset=utf-8");
    writer.println();
  }

  private String getFileAsBody(File file) {
    StringBuilder builder = new StringBuilder();
    try (var in = new BufferedReader(new FileReader(file))) {

      String dataRow;
      boolean isFound, canSearch = false;
      var subSet = this.substitution.keySet();

      while (in.ready()) {
        dataRow = in.readLine();

        if (canSearch) {
          for (String key : subSet) {
            isFound = dataRow.contains(key);
            if (isFound) {
              dataRow = dataRow.replace(key, this.substitution.get(key));
            }
          }
        }

        if (dataRow.stripLeading().startsWith("<body>")) canSearch = true;

        builder.append(dataRow);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }

    return builder.toString();
  }
}