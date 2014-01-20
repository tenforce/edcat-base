package eu.lod2.edcat.format;

import org.openrdf.model.Model;

public interface ResponseFormatter {
  public Object format(Model statements) throws FormatException;
}
