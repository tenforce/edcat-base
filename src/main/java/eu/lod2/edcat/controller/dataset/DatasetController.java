package eu.lod2.edcat.controller.dataset;

import com.github.jsonldjava.core.JsonLdError;
import eu.lod2.edcat.DatasetResponse;
import eu.lod2.edcat.utils.DcatJsonParser;
import eu.lod2.edcat.utils.Vocabulary;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class DatasetController {
  protected static final String ROUTE = "datasets";
  protected static final String OBJECT_ROUTE = ROUTE + "/{datasetId}";

  protected String datasetId;

  //* Returns default headers for the application. These headers should always be present
  protected HttpHeaders getHeaders() throws Exception {
    return new HttpHeaders();
  }

  // TODO: clean up post processing
  protected Object buildJsonFromStatements(Model statements) throws IOException, RDFHandlerException, JsonLdError {
    Map compactJson = (Map) DcatJsonParser.statementsToJsonLD(statements, getContext());
    DatasetResponse response = new DatasetResponse();
    Map dataset = (Map) compactJson.get("http://xmlns.com/foaf/0.1/primaryTopic");
    response.setSelf(dataset.get("self").toString());
    dataset.remove("self");
    response.setDataset(dataset);
    Map record = new LinkedHashMap();
    record.put("issued", compactJson.get("issued"));
    record.put("modified", compactJson.get("modified"));
    response.setRecord(record);
    return response;
  }

  protected Model buildModel(HttpServletRequest request, URI dataset) throws Exception {
    InputStream in = request.getInputStream();
    Model statements = DcatJsonParser.jsonLDToStatements(in, getContext().toString(), dataset, Vocabulary.get("Dataset"));
    in.close();
    return statements;
  }

  public String getId() {
    return this.datasetId == null ? UUID.randomUUID().toString() : this.datasetId;
  }

  public URL getContext() {
    return this.getClass().getResource("/eu/lod2/edcat/jsonld/dataset.jsonld");
  }

  protected URI getDatasetIdFromRecord(Model record) {
    return record.filter(null, Vocabulary.get("record.primaryTopic"), null).objectURI();
  }
}
