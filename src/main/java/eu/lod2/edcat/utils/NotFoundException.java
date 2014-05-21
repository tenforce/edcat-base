package eu.lod2.edcat.utils;

public class NotFoundException extends Exception {
  public NotFoundException() {
    super();
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message,Throwable t) {
    super(message,t);
  }
}
