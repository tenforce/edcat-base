package eu.lod2.edcat.format;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.SesameRDFParser;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFHandlerException;

import java.io.IOException;

public class JsonLDFormatter implements ResponseFormatter {

  /**
   * Simple constructor.
   */
  public JsonLDFormatter( ){ }

  @Override
  public Object format(Model statements) throws FormatException {
    try {
      return buildJsonFromStatements(statements);
    } catch (Exception e) {
      throw new FormatException(e);
    }
  }

  protected Object buildJsonFromStatements(Model statements) throws IOException, RDFHandlerException, JsonLdError {
    return JsonLdProcessor.fromRDF( statements, new SesameRDFParser() );
  }
}
