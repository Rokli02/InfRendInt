package main;

import java.io.File;

public record Constants() {
  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 80;
  public static final String SERVER_QUIT_WORD = "quit";
  public static final String CLIENT_QUIT_WORD = "bye";
  public static final File CLIENT_ROOT = new File("..", "res/client");
  public static final File SERVER_ROOT = new File("..", "res/server");
  public static final String SERVER_NAME = "[server]: ";
  public static final String CLIENT_NAME = "[kliens]: ";
}
