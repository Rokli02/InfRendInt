package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Utils {
  private static int maxByteRead = 4096;
  public static int getPortFromArgs(String[] args) {
    try {
      if (args.length < 2) {
        throw new NumberFormatException();
      }

      for (int i = 0; i < args.length; i++) {
        if ((i + 1) < args.length && args[i].equals("-p")) {
          return Integer.parseInt(args[i + 1]);
        }
      }

      throw new NumberFormatException();
    } catch (NumberFormatException nfe) {
      return Constants.DEFAULT_PORT;
    }
  }

  public static byte[] readFileContent(File root, String path) throws FileNotFoundException, IOException {
    List<Byte> byteList = new ArrayList<Byte>();
    byte[] buffer = new byte[maxByteRead];

    File file = new File(root, path);

    if (!file.exists()) {
      throw new FileNotFoundException();
    }

    int readSize;
    var reader = new FileInputStream(file);
    for (int i = 0; i < file.length(); i += maxByteRead) {
      readSize = Math.min((int) Math.max(0, file.length() - i), maxByteRead);

      reader.read(buffer, i, readSize);
      byteList.addAll(byteList);
    }

    reader.close();

    byte[] response = new byte[byteList.size()];
    int index = 0;
    byteList.spliterator().forEachRemaining((element) -> {
      response[index] = element;
    });
    return response;
  }

  public static void writeFileContent(File root, String path, byte[] content) throws Exception {
    throw new Exception("Not implemented");
  }
}
