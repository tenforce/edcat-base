package eu.lod2.edcat.handler;

import eu.lod2.edcat.utils.SparqlEngine;
import org.openrdf.model.URI;

public interface PreCreateHandler {
  public void handle(SparqlEngine engine,URI graph,URI resource) throws ActionAbortException;
}
