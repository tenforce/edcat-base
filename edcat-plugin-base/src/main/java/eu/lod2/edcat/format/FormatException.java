package eu.lod2.edcat.format;

public class FormatException extends Exception {
  public FormatException(Exception e) {
    super(e);
  }

  public FormatException(String message) {
    super(message);
  }
}
