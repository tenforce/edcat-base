package eu.lod2.edcat.format;

import com.github.jsonldjava.core.JsonLdError;
import eu.lod2.edcat.utils.DcatJsonParser;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFHandlerException;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class JsonLDFormatter implements ResponseFormatter {

  private URL context;

  public JsonLDFormatter(URL context) {
    this.context = context;
  }

  @Override
  public Object format(Model statements) throws FormatException {
    try {
      return buildJsonFromStatements(statements);
    } catch (Exception e) {
      throw new FormatException(e);
    }
  }

  public URL getContext() {
    return context;
  }

  protected Object buildJsonFromStatements(Model statements) throws IOException, RDFHandlerException, JsonLdError {
    Map compactJson = (Map) DcatJsonParser.statementsToJsonLD(statements, getContext());
    return compactJson;
  }
}
