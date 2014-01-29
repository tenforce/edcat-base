package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.DcatJsonParser;
import eu.lod2.edcat.utils.Vocabulary;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

public abstract class DatasetController {
  protected static final String ROUTE = "datasets";
  protected static final String OBJECT_ROUTE = ROUTE + "/{datasetId}";

  protected String datasetId;

  //* Returns default headers for the application. These headers should always be present
  protected HttpHeaders getHeaders() throws Exception {
    return new HttpHeaders();
  }

  protected Model buildModel( HttpServletRequest request, URI dataset ) throws Exception {
    InputStream in = request.getInputStream();
    Model statements = DcatJsonParser.jsonLDToStatements( in, getContext().toString(), dataset, Vocabulary.get( "Dataset" ) );
    in.close();
    return statements;
  }

  public String getId() {
    return this.datasetId == null ? UUID.randomUUID().toString() : this.datasetId;
  }

  //todo: this should A) be retrieved from a configurable location and B) be published in a more sane class than the DatasetController.
  public static URL getContext() {
    return DatasetController.class.getResource( "/eu/lod2/edcat/jsonld/dataset.jsonld" );
  }

  @SuppressWarnings({ "UnusedDeclaration" })
  protected URI getDatasetIdFromRecord( Model record ) {
    return record.filter( null, Vocabulary.get( "record.primaryTopic" ), null ).objectURI();
  }

}
