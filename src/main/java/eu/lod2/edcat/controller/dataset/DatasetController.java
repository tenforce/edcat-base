package eu.lod2.edcat.controller.dataset;

import com.github.jsonldjava.core.JsonLdError;
import eu.lod2.edcat.utils.DcatJsonParser;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public abstract class DatasetController {
  protected static final String ROUTE = "datasets";
  protected static final String OBJECT_ROUTE = ROUTE + "/{datasetId}";

  protected String datasetId;

  //* Returns default headers for the application. These headers should always be present
  protected HttpHeaders getHeaders() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    return headers;
  }

  protected Object buildJsonFromStatements(Model statements) throws IOException, RDFHandlerException, JsonLdError {
    Map compactJson =  (Map) DcatJsonParser.statementsToJsonLD(statements, new URL(getContext()));

    return compactJson;
  }

  protected Model buildModel(HttpServletRequest request,URI dataset) throws Exception{
    InputStream in = request.getInputStream();
    Model statements = DcatJsonParser.jsonLDToStatements(in, getContext(), dataset);
    in.close();
    return statements;
  }

  public String getId() {
    return this.datasetId == null ? UUID.randomUUID().toString() : this.datasetId;
  }

  public String getContext() {
    URL r = this.getClass().getResource("/eu/lod2/edcat/jsonld/dataset.jsonld");
    return r.toString();
  }
}
