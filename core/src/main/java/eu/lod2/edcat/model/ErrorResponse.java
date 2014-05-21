package eu.lod2.edcat.model;

public class ErrorResponse {
  private String error;
  private String status;

  public ErrorResponse(String error, String status) {
    this.error = error;
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
