package main;

public class AsyncTextPipe {
  // switch to Message class
  private String[] latestText;
  private boolean stop;

  public AsyncTextPipe() {
    this.stop = false;
  }
  
  public String[] getText() throws InterruptedException {
    synchronized(this) {
      wait();

      // if (this.stop) {
      //   throw new InterruptedException("Async pipe is interrupted!");
      // }
      
      return latestText;
    }
  }

  public void produceTest(String sender, String text) throws InterruptedException {
    if (text == null || text.isBlank()) {
      return;
    }
    // System.out.println(sender + text);
    synchronized(this) {
      // this.latestText = sender + text;
      this.latestText = new String[] { sender, text };

      notify();
    }
  }

  public boolean stop() {
    try {
      synchronized(this) {
        this.stop = true;

        notifyAll();
        return this.stop;
      }
    } catch (RuntimeException re) {
      return false;
    }
  }

  public boolean isStoped() {
    return this.stop;
  }
}
