package eu.lod2.hooks.contexts.distribution;

import eu.lod2.edcat.model.Catalog;
import eu.lod2.hooks.contexts.Context;
import eu.lod2.hooks.contexts.catalog.InstanceContext;
import org.openrdf.model.Model;
import org.openrdf.model.URI;

/**
 * The AtContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.distribution.AtCreateHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
public class AtContext extends Context {

  /** Contains the model which defines the new triples created in this AtContext. */
  private Model statements;
  private URI datasetURI;
  private URI distributionUri;

  /**
   * Simple constructor of the AtContext.
   *
   * @param dataset dataset on which the request operates.
   * @param statements statements about the distribution
   */
  public AtContext(URI dataset, URI distributionUri, Model statements) {
    setDatasetURI(dataset);
    setStatements( statements );
    setDistributionUri(distributionUri);
  }

  /**
   * Sets the statements of the model.
   *
   * @param statements The statements available in this AtContext.
   */
  protected void setStatements( Model statements ) {
    this.statements = statements;
  }

  /**
   * Retrieves the statements which will be created in this action.
   *
   * @return Model of this AtContext.
   */
  public Model getStatements() {
    return statements;
  }

  public URI getDatasetURI() {
    return datasetURI;
  }

  public void setDatasetURI(URI datasetURI) {
    this.datasetURI = datasetURI;
  }

  public URI getDistributionUri() {
    return distributionUri;
  }

  public void setDistributionUri(URI distributionUri) {
    this.distributionUri = distributionUri;
  }
}
