package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.CatalogService;
import eu.lod2.edcat.utils.SparqlEngine;
import org.openrdf.model.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * The PreContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.PreReadHandler} - {@link eu.lod2.hooks.handlers.dcat.PreCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.PreUpdateHandler} - {@link eu.lod2.hooks.handlers.dcat.PreDestroyHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings("UnusedDeclaration")
public class PreContext extends Context {



  //--- CONSTRUCTORS

  /**
   * Constructs a new PreContext with all fields set.
   */
  public PreContext( CatalogService catalogService, HttpServletRequest request, SparqlEngine engine, URI datasetUri ) {
    this.catalogService = catalogService;
    this.request = request;
    this.engine = engine;
    this.datasetUri = datasetUri;
  }



  //--- GETTERS AND SETTERS


  /** URI identifier for the DataSet which will be created. */
  private URI datasetUri;

  /**
   * Returns the URI identifier for the DataSet which will be created.
   * <p/>
   * This URI serves both as the name of the graph in which the metadata of the DataSet is stored,
   * as the name by which we identify the DataSet in the config graph.
   *
   * @return URI identifying the DataSet.
   */
  public URI getDatasetUri(){
    return datasetUri;
  }


  /** The {@link eu.lod2.edcat.utils.CatalogService} for which the DataSet will be created. */
  private CatalogService catalogService;

  /**
   * The {@link eu.lod2.edcat.utils.CatalogService} for which the DataSet will be created.
   */
  public CatalogService getCatalogService() {
    return catalogService;
  }


  /** Return the received request from the controller. May be altered to change the request. */
  private HttpServletRequest request;

  /**
   * Returns the received request from the controller. May be altered by plugins to change the
   * request.
   */
  public HttpServletRequest getRequest() {
    return request;
  }


  /** The SparqlEngine used for answering to the request. */
  private SparqlEngine engine;

  /**
   * Return the SparqlEngine used for answering to the request.
   */
  public SparqlEngine getEngine() {
    return engine;
  }

}
