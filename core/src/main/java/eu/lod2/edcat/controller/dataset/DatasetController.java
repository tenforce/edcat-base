package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.controller.BaseController;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.*;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.UUID;

public abstract class DatasetController extends BaseController {
  /** Id of the current dataset, retrieve through {@link #getId()}. */
  protected String datasetId;

  /** Kind of object used by the JsonLdContext */
  static JsonLdContext.Kind kind = JsonLdContext.Kind.Dataset;


  // --- ROUTING

  /** list datasets route */
  protected static final String LIST_ROUTE = DcatURI.DATASET_LIST_PATH;

  /** show dataset route */
  protected static final String OBJECT_ROUTE = DcatURI.DATASET_OBJECT_PATH;


  // --- HELPERS


  //* Returns default headers for the application. These headers should always be present
  protected HttpHeaders getHeaders() throws Exception {
    return new HttpHeaders();
  }

  protected Model buildModel( HttpServletRequest request, URI dataset ) throws Exception {
    InputStream in = request.getInputStream();
    Model statements = DcatJsonParser.jsonLDToStatements(
        in, new JsonLdContext( kind ),
        dataset, Sparql.namespaced( "dcat", "Dataset" ) );
    BlankNodeNuker.nuke( statements, kind );
    in.close();
    return statements;
  }

  public String getId() {
    return this.datasetId == null ? UUID.randomUUID().toString() : this.datasetId;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected URI getDatasetIdFromRecord( Model record ) {
    return record.filter( null, Sparql.namespaced( "foaf", "primaryTopic" ), null ).objectURI();
  }

  protected void verifyCatalogExists(Catalog catalog) throws NotFoundException {
    if (!catalog.exists()) {
      throw new NotFoundException(catalog.getUri() + "does not exists");
    }
  }

}
