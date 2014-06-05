package eu.lod2.hooks.contexts.dataset;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * The PreContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.PreReadHandler} - {@link eu.lod2.hooks.handlers.dcat.dataset.PreCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.PreUpdateHandler} - {@link eu.lod2.hooks.handlers.dcat.dataset.PreDestroyHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings("UnusedDeclaration")
public class PreContext extends Context {



  //--- CONSTRUCTORS

  /**
   * Constructs a new PreContext with all fields set.
   */
  public PreContext( Catalog catalog, HttpServletRequest request, URI datasetUri ) {
    this.catalog = catalog;
    this.request = request;
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


  /** The {@link eu.lod2.edcat.utils.Catalog} for which the DataSet will be created. */
  private Catalog catalog;

  /**
   * The {@link eu.lod2.edcat.utils.Catalog} for which the DataSet will be created.
   */
  public Catalog getCatalog() {
    return catalog;
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

}
