package eu.lod2.hooks.contexts.base;

import javax.servlet.http.HttpServletRequest;

public interface PreContextBase extends ContextBase {

  /**
   * Retrieves the request object as sent by the user.
   *
   * @return HttpServletRequest as sent by the user.
   */
  public HttpServletRequest getRequest();

}
