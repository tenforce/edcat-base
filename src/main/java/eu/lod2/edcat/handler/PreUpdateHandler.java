package eu.lod2.edcat.handler;

import eu.lod2.edcat.utils.SparqlEngine;

import javax.servlet.http.HttpServletRequest;

public interface PreUpdateHandler {
  public void handlePreUpdate(HttpServletRequest request, SparqlEngine engine) throws ActionAbortException;

}
