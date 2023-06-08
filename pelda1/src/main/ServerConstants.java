package main;

import java.io.File;

public record ServerConstants() {
  static final int DEFAULT_SERVER_PORT = 80;
  static final File ROOT = new File("..", "res");
  static final String INDEX_FILE = "index.html";
  static final String NOT_FOUND_FILE = "not_found.html";
}
