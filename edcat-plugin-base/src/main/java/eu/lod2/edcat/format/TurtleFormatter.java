package eu.lod2.edcat.format;

import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class TurtleFormatter implements ResponseFormatter {
  @Override
  public Object format(Model statements) throws FormatException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      Rio.write(statements, output, RDFFormat.TURTLE);
      return new String(output.toByteArray(), Charset.forName("UTF-8"));
    } catch (RDFHandlerException e) {
      throw new FormatException(e);
    }
  }
}
