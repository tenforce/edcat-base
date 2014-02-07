package eu.lod2.edcat.model;

import eu.lod2.edcat.utils.JsonLD;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;

import java.util.Collection;
import java.util.UUID;

/**
 * Superclass for lightweight objects which we keep track of.
 * <p/>
 * Namely:
 * - {@link Catalog}
 * - {@link Dataset}
 */
public abstract class DomainObject {

  /** Used to identify the domain object. */
  private String uuid;

  /** Contains the statements which are currently known about the domain object. */
  private Model statements;

  /**
   * Simplest constructor for the domain object.
   */
  public DomainObject() {
    this( UUID.randomUUID().toString() );
  }

  /**
   * Simple constructor for the domain object, given that the UUID is known.
   *
   * @param uuid UUID of this domain object.
   */
  public DomainObject( UUID uuid ) {
    this( uuid.toString() );
  }

  /**
   * Simple constructor for the domain object, given that the UUID is known as a String.
   * <p/>
   * The UUID is currently always communicated as a String to the outside world.
   *
   * @param uuid UUID for the identifying portion of this domain object.
   */
  public DomainObject( String uuid ) {
    this.uuid = uuid;
    add( new StatementImpl( getUri(), RDF.TYPE, getType() ) );
  }

  /**
   * Retrieves the URI type of this domain object.
   *
   * @return URI which identifies the type of the domain object.
   */
  protected abstract URI getType();

  /**
   * Retrieves the JsonLdContext which is responsible for this domain object.
   *
   * @return JsonLdContext responsible for this domain object.
   */
  protected abstract JsonLdContext getJsonLdContext();

  /**
   * Sets the UUID used by this domain object.
   *
   * @param uuid UUID this domain object will use from now on.
   */
  public void setId( String uuid ) {
    this.uuid = uuid;
  }

  /**
   * Returns the UUID used by this domain object.
   *
   * @return String representation of the UUID used by this domain object.
   */
  public String getId() {
    return this.uuid;
  }

  /**
   * Returns the URI which identifies this domain object.
   *
   * @return URI identifier.
   */
  public URI getUri() {
    return Sparql.namespaced( "catalogs", getId() );
  }

  /**
   * Retrieves the model for this domain object, containing all currently known statements.
   *
   * @return Model containing all currently known statements about this domain object.
   */
  public Model getStatements() {
    return statements;
  }

  /**
   * Adds {@code statement} to the statements which define this domain object.
   * <p/>
   * Note that the statement will be added to the context of the graph which defines this domain
   * object.
   *
   * @param statement Statement to add to the description of this domain object.
   */
  public void add( Statement statement ) {
    statements.add( statement.getSubject(), statement.getPredicate(), statement.getObject(), getUri() );
  }

  /**
   * Adds {@code statements} to the statements which define this domain object.
   * <p/>
   * Note that the statement will be added to the context of the graph which defines this domain
   * object.
   *
   * @param statements Collection of statements to add.
   */
  public void add( Collection<Statement> statements ) {
    for ( Statement s : statements )
      add( s );
  }

  /**
   * Adds the information enclosed in {@code json} to the current domain object.
   *
   * @param json JsonLD document from which we should insert the content in the definition for this
   *             domain object.
   */
  public void add( JsonLD json ) {
    json.setId( getUri() );
    json.setContext( new JsonLdContext( JsonLdContext.Kind.Catalog ) );

  }


}
