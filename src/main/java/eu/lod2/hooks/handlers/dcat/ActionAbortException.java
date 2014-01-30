package eu.lod2.hooks.handlers.dcat;

import org.springframework.http.HttpStatus;

/**
 * When a hook throws this exception it requests the place where it hooked in to to cancel the
 * execution of the call which caused this hook to be executed.
 */

public class ActionAbortException extends Exception {
  private HttpStatus status;

  /**
   * Simple constructor for the ActionAbortException
   *
   * @param message Message to be sent to the user regarding the abortion if such communication
   *                would take place.
   */
  public ActionAbortException(HttpStatus status, String message) {
    super(message);
    this.status =status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
