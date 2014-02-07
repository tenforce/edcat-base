package eu.lod2.edcat.model;

import com.github.jsonldjava.core.JsonLdError;
import eu.lod2.edcat.utils.JsonLD;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a Catalog and all operations which may occur on it.
 */
public class Catalog {

  /** Used to identify the Catalog. */
  private String uuid;

  /** Contains the statements which are currently known about the Catalog. */
  private Model statements = new LinkedHashModel();

  /**
   * Simplest constructor for the Catalog.
   */
  public Catalog() {
    this( UUID.randomUUID().toString() );
  }

  /**
   * Simple constructor for the Catalog, given that the UUID is known.
   *
   * @param uuid UUID of this catalog.
   */
  public Catalog( UUID uuid ) {
    this( uuid.toString() );
  }

  /**
   * Simple constructor for the Catalog, given that the UUID is known as a String.
   * <p/>
   * The UUID is currently always communicated as a String to the outside world.
   *
   * @param uuid String representation of the UUId of this Catalog.
   */
  public Catalog( String uuid ) {
    this.uuid = uuid;
    add( new StatementImpl( getUri(), RDF.TYPE, Sparql.namespaced( "dcat", "Catalog" ) ) );
    statements.add( getUri(), RDF.TYPE, Sparql.namespaced( "dcat", "Catalog" ) ,
        (URI) Sparql.getClassMapVariable( "CONFIG_GRAPH" ) );
  }

  /**
   * Sets the UUID used by this Catalog.
   *
   * @param uuid UUID this Catalog will use from now on.
   */
  public void setId( String uuid ) {
    this.uuid = uuid;
  }

  /**
   * Returns the UUID used by this Catalog.
   *
   * @return String representation of the UUID used by this Catalog.
   */
  public String getId() {
    return this.uuid;
  }

  /**
   * Returns the URI which identifies this Catalog.
   *
   * @return Catalog URI identifier.
   */
  public URI getUri() {
    return Sparql.namespaced( "catalogs", getId() );
  }

  /**
   * Retrieves the model for this Catalog, containing all currently known statements.
   *
   * @return Model containing all currently known statements about this catalog.
   */
  public Model getStatements() {
    return statements;
  }

  /**
   * Adds {@code statement} to the statements which define this catalog.
   * <p/>
   * Note that the statement will be added to the context of the graph which defines this catalog.
   *
   * @param statement Statement to add to this Catalog.
   */
  public void add( Statement statement ) {
    statements.add( statement.getSubject(), statement.getPredicate(), statement.getObject(), getUri() );
  }

  /**
   * Adds {@code statements} to the statements which define this catalog.
   * <p/>
   * Note that the statement will be added to the context of the graph which defines this catalog.
   *
   * @param statements Collection of statements to add.
   */
  public void add( Collection<Statement> statements ) {
    for ( Statement s : statements )
      add( s );
  }

  /**
   * Adds the triples of the JsonLD document to the triples defining the Catalog.
   *
   * @param json JSON-LD document containing the new statements.
   */
  public void add( JsonLD json ) throws JsonLdError {
    json.setId( getUri() );
    json.setContext( new JsonLdContext( JsonLdContext.Kind.Catalog ) );
    add( json.getStatements() );
  }


}