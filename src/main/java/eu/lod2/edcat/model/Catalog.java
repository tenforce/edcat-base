package eu.lod2.edcat.model;

import com.github.jsonldjava.core.JsonLdError;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLD;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.query.Db;
import eu.lod2.query.Sparql;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import java.util.Collection;
import java.util.Date;
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
    return DcatURI.catalogUri(getId());
  }

  /**
   * Verifies that a statement exists declaring our uri is a catalog
   */
  public boolean exists() {
    // does not use hasStatement because of https://github.com/openlink/virtuoso-opensource/issues/100
    return Db.getStatements(getUri(),RDF.TYPE,Sparql.namespaced("dcat","Catalog"),false,getUri()).size() == 1;
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


  // --- PERSISTENCE

  /**
   * Inserts the statements to identify a new CatalogRecord for this Catalog into the graph.
   *
   * @param datasetId UUID identifier of the Dataset.
   * @return Model containing the statements which have been inserted into the Catalog graph.
   */
  public Model createRecord(String datasetId) {
    Model statements = new LinkedHashModel();
    URI dataset = DcatURI.datasetURI(getId(), datasetId);
    URI record = DcatURI.recordURI(getId(), datasetId);
    Literal now = ValueFactoryImpl.getInstance().createLiteral( new Date() );
    statements.add( record, DCTERMS.MODIFIED, now );
    statements.add( record, DCTERMS.ISSUED, now );
    statements.add( record, RDF.TYPE, Sparql.namespaced( "dcat", "Record" ) );
    statements.add( record, Sparql.namespaced( "foaf", "primaryTopic" ), dataset );
    statements.add( getUri(), Sparql.namespaced( "dcat","dataset" ), dataset );
    statements.add( getUri(), Sparql.namespaced( "dcat", "record" ), record );
    Db.add(statements, getUri());
    return statements;
  }

  @SuppressWarnings( "UnusedDeclaration" )
  public Model getRecord( String datasetId ) {
    return Db.getStatements( DcatURI.recordURI(getId(), datasetId), null, null, true, getUri() );
  }

  /**
   * Indicates that the Dataset is changed.
   * <p/>
   * This updates the dct:modified timestamp and returns all remaining statements describing the
   * CatalogRecord directly.
   *
   * @param datasetId UUID identifier of the Dataset.
   * @return Engine containing the statements which describe the Dataset.
   */
  public Model updateRecord(String datasetId) {
    URI record = DcatURI.recordURI(getId(), datasetId);
    Db.update( Sparql.query( "" +
            " @PREFIX" +
            " DELETE {" +
            "   GRAPH $catalog {" +
            "     $record dct:modified ?modified" +
            "   }" +
            " } WHERE {" +
            "   GRAPH $catalog {" +
            "     $record dct:modified ?modified" +
            "   }" +
            " }",
            "catalog", getUri(),
            "record", record ) );
    Model statements = new LinkedHashModel();
    Literal now = ValueFactoryImpl.getInstance().createLiteral( new Date() );
    statements.add( record, DCTERMS.MODIFIED, now, getUri() );
    Db.add( statements, getUri() );
    return Db.getStatements( record, null, null, true, getUri() );
  }

  /**
   * Removes the CatalogRecord for a Dataset with UUID {@code datasetId} from the Database.
   *
   * @param datasetId UUID identifier of the Dataset which we want to remove.
   */
  public void deleteRecord(String datasetId) {
    Db.update( Sparql.query( "" +
            " @PREFIX" +
            " DELETE WHERE {" +
            "   GRAPH $catalog {" +
            "     $record ?p ?o." +
            "     OPTIONAL { ?o ?op ?oo. }" +
            "   }" +
            " }",
            "catalog", getUri(),
            "record", DcatURI.recordURI(getId(), datasetId)));
  }


}