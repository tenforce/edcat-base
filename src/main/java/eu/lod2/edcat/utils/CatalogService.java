package eu.lod2.edcat.utils;

import eu.lod2.query.Db;
import eu.lod2.query.Sparql;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import java.util.Date;

/**
 * Service class to handle Catalog provenance.
 */
public class CatalogService {


  // --- LOCAL VARIABLES

  /**
   * URI of the catalog.
   */
  private URI catalogUri;

  /**
   * Returns the URI of the catalog.
   */
  public URI getURI() {
    return catalogUri;
  }


  // --- CONSTRUCTORS

  /**
   * Constructs a new CatalogService with minimal information.
   *
   * @param catalogUri URI which this CatalogService manages.
   */
  public CatalogService( String catalogUri ) {
    this.catalogUri = new URIImpl( catalogUri );
  }

  /**
   * Constructs a CatalogService which refers to the default catalog.
   *
   * @return New CatalogService-object representing the default catalog.
   */
  // todo: remove me
  public static CatalogService getDefaultCatalog( ) {
    QueryResult result = Db.query( "" +
        "@PREFIX " +
        "SELECT ?catalog" +
        " FROM @CONFIG_GRAPH " +
        "WHERE {" +
        " ?catalog a dcat:CatalogService" +
        "}" );
    if ( result.size() > 0 ) {
      String catalog = result.get( 0 ).get( "catalog" );
      return new CatalogService(  catalog );
    }
    return new CatalogService( (( URI ) Sparql.getClassMapVariable( "DEFAULT_CATALOG" )).stringValue() );
  }


  // --- DATA GENERATION

  /**
   * Constructs a new URI for a Dataset in this Catalog.
   *
   * @param datasetId String identifier of the new Dataset, should not contain /
   * @return URI which may be used by the new Dataset.
   */
  public URI generateDatasetUri( String datasetId ) {
    return buildURI( catalogUri.stringValue(), "dataset", datasetId );
  }

  /**
   * Constructs a new URI for the (optional) CatalogRecord of this Catalog.
   *
   * @param datasetId UUID identifier if the obligatory connection to the Dataset.
   * @return URI for the Record connecting to this Catalog.
   */
  public URI generateRecordUri( String datasetId ) {
    return buildURI( catalogUri.stringValue(), "record", datasetId );
  }


  // --- PERSISTENCE

  /**
   * Inserts the statements to identify a new Dataset for this Catalog into the graph.
   *
   * @param datasetId UUID identifier of the Dataset.
   * @return Model containing the statements which have been inserted into the Catalog graph.
   */
  public Model insertDataset( String datasetId ) {
    Model statements = new LinkedHashModel();
    URI dataset = generateDatasetUri( datasetId );
    URI record = generateRecordUri( datasetId );
    Literal now = ValueFactoryImpl.getInstance().createLiteral( new Date() );
    statements.add( record, DCTERMS.MODIFIED, now );
    statements.add( record, DCTERMS.ISSUED, now );
    statements.add( record, RDF.TYPE, Vocabulary.get( "Record" ) );
    statements.add( record, Vocabulary.get( "record.primaryTopic" ), dataset );
    statements.add( catalogUri, Vocabulary.get( "catalog.dataset" ), dataset );
    statements.add( catalogUri, Vocabulary.get( "catalog.record" ), record );
    Db.add( statements, catalogUri );
    return statements;
  }

  @SuppressWarnings( "UnusedDeclaration" )
  public Model getRecord( String datasetId ) {
    return Db.getStatements( generateRecordUri( datasetId ), null, null, true, catalogUri );
  }

  /**
   * Indicates that the Dataset is changed.
   * <p/>
   * This updates the dct:modified timestamp and returns all remaining statements describing the
   * Catalog directly.
   *
   * @param datasetId UUID identifier of the Dataset.
   * @return Engine containing the statements which describe the Dataset.
   */
  public Model updateDataset( String datasetId ) {
    URI record = generateRecordUri( datasetId );
    Db.update( Sparql.query( "" +
        " @PREFIX" +
        " DELETE WHERE" +
        " GRAPH $catalog {" +
        "   $record dct:modified ?modified" +
        " }",
        "catalog", catalogUri,
        "record", record ) );
    Model statements = new LinkedHashModel();
    Literal now = ValueFactoryImpl.getInstance().createLiteral( new Date() );
    statements.add( record, DCTERMS.MODIFIED, now, catalogUri );
    Db.add( statements, catalogUri );
    return Db.getStatements( record, null, null, true, catalogUri );
  }

  /**
   * Removes the Dataset with UUID {@code datasetId} from the Database.
   *
   * @param datasetId UUID identifier of the Dataset which we want to remove.
   */
  // todo: shouldn't this remove the dataset and it's distributions as well?  the implementation does not fit the method name.
  public void removeDataset( String datasetId ) {
    Db.update( Sparql.query( "" +
        " @PREFIX" +
        " DELETE WHERE {" +
        "   GRAPH $catalog {" +
        "     $record ?p ?o." +
        "     OPTIONAL { ?o ?op ?oo. }" +
        "   }" +
        " }",
        "catalog", catalogUri,
        "record", generateRecordUri( datasetId ) ) );
  }


  // --- SUPPORTING CODE

  /**
   * Constructs a new URI from three sections.  Concatenating all of the fields together with a /
   * if necessary.
   *
   * @param base Base of the uri.
   * @param sub  Middle of the URI (ie. predicate).
   * @param id   Last part of the URI (ie. UUID identifier).
   * @return URI which is slashed together.
   */
  private URI buildURI( String base, String sub, String id ) {
    return new URIImpl( concatenateWithSlash( base, sub, id ) );
  }

  /**
   * Concatenates multiple strings together, adding a / in between if a / would not be in between
   * after joining.
   *
   * @param strings Strings to concatenate.
   * @return Concatenation of all strings, with an added / in between if no / existed in between.
   */
  private String concatenateWithSlash( String... strings ) {
    if ( strings.length == 0 ) return "";

    StringBuilder b = new StringBuilder( strings[0] );
    for ( int i = 1; i < strings.length; i++ )
      if ( strings[i - 1].endsWith( "/" ) || strings[i].startsWith( "/" ) )
        b.append( strings[i] );
      else
        b.append( "/" ).append( strings[i] );

    return b.toString();
  }

}
